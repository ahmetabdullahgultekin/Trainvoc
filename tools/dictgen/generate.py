#!/usr/bin/env python3
"""Trainvoc dictionary seed generator.

Turns the legacy packed word data (data/all_words.json) into the relational,
multilingual seed used by the Android app's Room v18 schema:

  languages(id, code, name)              en=1, tr=2
  words(id, word, language_id, ...)      every EN and TR lemma is a row
  word_translations(word_id, translated_word_id, sense_index, note, is_primary)
  synonyms(word_id, synonym_word_id)     same-language pairs
  word_exam_cross_ref(word_id, exam)

Word IDs are permanent: ids.lock.json maps "lang|lemma" -> id and is
committed; regeneration never renumbers, new lemmas append after max id.

Subcommands:
  emit-manifest   parse inputs -> seed_v18.json manifest (+ updates ids.lock.json)
  build-db        manifest + Room schema JSON -> prepopulated sqlite asset
  validate        integrity checks on the manifest (and DB when present)

Typical full run (from tools/dictgen/):
  python3 generate.py emit-manifest
  python3 generate.py build-db --schema ../../TrainvocClient/app/schemas/\
com.gultekinahmetabdullah.trainvoc.database.AppDatabase/18.json
  python3 generate.py validate
"""

from __future__ import annotations

import argparse
import hashlib
import json
import pathlib
import random
import sqlite3
import sys
from collections import OrderedDict

from meaning_parser import parse_meaning, turkish_lower

HERE = pathlib.Path(__file__).parent
DATA = HERE / "data" / "all_words.json"
IDS_LOCK = HERE / "ids.lock.json"
REVIEW_DIR = HERE / "review"
DEFAULT_MANIFEST = (
    HERE / ".." / ".." / "TrainvocClient" / "app" / "src" / "main" / "assets"
    / "database" / "seed_v18.json"
).resolve()
DEFAULT_DB = DEFAULT_MANIFEST.parent / "trainvoc-db.db"

CEFR_LEVELS = ["A1", "A2", "B1", "B2", "C1", "C2"]
LANG_EN, LANG_TR, LANG_AR = 1, 2, 3
DB_VERSION = 18


# --------------------------------------------------------------------------
# ID ledger
# --------------------------------------------------------------------------

class IdLedger:
    """Stable "lang|lemma" -> id mapping, persisted in ids.lock.json."""

    def __init__(self, path: pathlib.Path):
        self.path = path
        self.ids: dict[str, int] = {}
        if path.exists():
            self.ids = json.loads(path.read_text(encoding="utf-8"))
        self._next = max(self.ids.values(), default=0) + 1

    def key(self, lang: str, lemma: str) -> str:
        return f"{lang}|{lemma}"

    def get(self, lang: str, lemma: str) -> int:
        k = self.key(lang, lemma)
        if k not in self.ids:
            self.ids[k] = self._next
            self._next += 1
        return self.ids[k]

    def save(self) -> None:
        ordered = OrderedDict(sorted(self.ids.items(), key=lambda kv: kv[1]))
        self.path.write_text(
            json.dumps(ordered, ensure_ascii=False, indent=1) + "\n",
            encoding="utf-8",
        )


# --------------------------------------------------------------------------
# Manifest generation
# --------------------------------------------------------------------------

def norm_en(lemma: str) -> str:
    return " ".join(lemma.strip().lower().split())


def norm_tr(lemma: str) -> str:
    return " ".join(turkish_lower(lemma.strip()).split())


def display_meaning(senses) -> str:
    if len(senses) == 1:
        return ", ".join(senses[0].lemmas)
    return " ".join(
        f"({i + 1}) " + ", ".join(s.lemmas) for i, s in enumerate(senses)
    )


def emit_manifest(manifest_path: pathlib.Path, report: list[str]) -> dict:
    raw = json.loads(DATA.read_text(encoding="utf-8"))
    ledger = IdLedger(IDS_LOCK)

    # 1. Collect EN entries: lowest CEFR level wins; senses merged in
    #    encounter order; the YDS group only tags the exam + adds senses.
    en_entries: "OrderedDict[str, dict]" = OrderedDict()
    for level in CEFR_LEVELS + ["YDS"]:
        for item in raw.get(level, []):
            lemma = norm_en(item["word"])
            if not lemma:
                continue
            entry = en_entries.setdefault(
                lemma, {"level": None, "meanings": [], "exams": set(), "note": None}
            )
            if level in CEFR_LEVELS and entry["level"] is None:
                entry["level"] = level
            if level == "YDS":
                entry["exams"].add("YDS")
            meaning = item["meaning"].strip()
            if meaning and meaning not in entry["meanings"]:
                entry["meanings"].append(meaning)

    # 2. Parse meanings into senses; build TR lemma set.
    words: list[dict] = []
    translations: list[dict] = []
    translation_keys: set[tuple[int, int, int]] = set()
    synonyms: set[tuple[int, int]] = set()
    word_exams: list[dict] = []
    tr_ids: dict[str, int] = {}
    en_ids: dict[str, int] = {}

    for lemma in en_entries:
        en_ids[lemma] = ledger.get("en", lemma)

    def tr_word_id(lemma: str) -> int:
        key = norm_tr(lemma)
        if key not in tr_ids:
            tr_ids[key] = ledger.get("tr", key)
        return tr_ids[key]

    for lemma, entry in en_entries.items():
        en_id = en_ids[lemma]
        # Parse every raw meaning; senses accumulate across sources
        # (CEFR gloss first, then richer YDS senses).
        senses = []
        word_note = None
        seen_sense_keys: set[tuple[str, ...]] = set()
        for meaning in entry["meanings"]:
            parsed = parse_meaning(meaning)
            if parsed.word_note and not word_note:
                word_note = parsed.word_note
            for sense in parsed.senses:
                key = tuple(norm_tr(l) for l in sense.lemmas)
                if key in seen_sense_keys:
                    continue
                seen_sense_keys.add(key)
                senses.append(sense)
        if not senses:
            report.append(f"NO_SENSES {lemma!r}: {entry['meanings']!r}")
            continue

        words.append(
            {
                "id": en_id,
                "lemma": lemma,
                "lang": LANG_EN,
                "level": entry["level"],
                "note": word_note,
                "meaning": display_meaning(senses),
            }
        )
        for exam in sorted(entry["exams"]):
            word_exams.append({"wordId": en_id, "exam": exam})

        for sense_index, sense in enumerate(senses):
            sense_tr_ids = []
            for pos, tr_lemma in enumerate(sense.lemmas):
                tid = tr_word_id(tr_lemma)
                sense_tr_ids.append(tid)
                edge_key = (en_id, tid, sense_index)
                if edge_key in translation_keys:
                    continue
                translation_keys.add(edge_key)
                translations.append(
                    {
                        "wordId": en_id,
                        "translatedWordId": tid,
                        "senseIndex": sense_index,
                        "note": sense.note,
                        "isPrimary": sense_index == 0 and pos == 0,
                    }
                )
            # TR lemmas sharing a sense are near-synonyms of each other.
            for i in range(len(sense_tr_ids)):
                for j in range(i + 1, len(sense_tr_ids)):
                    a, b = sense_tr_ids[i], sense_tr_ids[j]
                    if a != b:
                        synonyms.add((min(a, b), max(a, b)))
            # (= hint) links the EN headword to an EN synonym, if known.
            for hint in sense.syn_hints:
                hint_lemma = norm_en(hint)
                hint_id = en_ids.get(hint_lemma)
                if hint_id and hint_id != en_id:
                    synonyms.add((min(en_id, hint_id), max(en_id, hint_id)))
                elif hint_id is None:
                    report.append(f"UNRESOLVED_SYN {lemma!r} -> {hint!r}")

    # 3. TR word rows (meaning cache = the EN lemmas they translate).
    en_by_id = {w["id"]: w["lemma"] for w in words}
    tr_meanings: dict[int, list[str]] = {}
    for t in translations:
        lemmas = tr_meanings.setdefault(t["translatedWordId"], [])
        en_lemma = en_by_id[t["wordId"]]
        if en_lemma not in lemmas:
            lemmas.append(en_lemma)
    for key, tid in sorted(tr_ids.items(), key=lambda kv: kv[1]):
        words.append(
            {
                "id": tid,
                "lemma": key,
                "lang": LANG_TR,
                "level": None,
                "note": None,
                "meaning": ", ".join(tr_meanings.get(tid, [])[:6]),
            }
        )

    manifest = {
        "manifestVersion": 1,
        "dbVersion": DB_VERSION,
        "languages": [
            {"id": LANG_EN, "code": "en", "name": "English"},
            {"id": LANG_TR, "code": "tr", "name": "Türkçe"},
        ],
        "exams": ["TOEFL", "IELTS", "YDS", "YÖKDİL", "KPDS", "Mixed"],
        "words": sorted(words, key=lambda w: w["id"]),
        "translations": translations,
        "synonyms": [{"wordId": a, "synonymWordId": b} for a, b in sorted(synonyms)],
        "wordExams": word_exams,
    }

    ledger.save()
    manifest_path.parent.mkdir(parents=True, exist_ok=True)
    manifest_path.write_text(
        json.dumps(manifest, ensure_ascii=False, separators=(",", ":")),
        encoding="utf-8",
    )
    return manifest


# --------------------------------------------------------------------------
# DB build (DDL comes verbatim from Room's exported schema JSON)
# --------------------------------------------------------------------------

def build_db(manifest_path: pathlib.Path, schema_path: pathlib.Path,
             db_path: pathlib.Path) -> None:
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    schema = json.loads(schema_path.read_text(encoding="utf-8"))["database"]
    if schema["version"] != manifest["dbVersion"]:
        raise SystemExit(
            f"schema version {schema['version']} != manifest dbVersion "
            f"{manifest['dbVersion']}"
        )

    if db_path.exists():
        db_path.unlink()
    con = sqlite3.connect(db_path)
    cur = con.cursor()

    # Execute Room's own DDL so createFromAsset validation cannot drift.
    for entity in schema["entities"]:
        cur.execute(
            entity["createSql"].replace("${TABLE_NAME}", entity["tableName"])
        )
        for index in entity.get("indices", []):
            cur.execute(
                index["createSql"].replace("${TABLE_NAME}", entity["tableName"])
            )
    for view in schema.get("views", []):
        cur.execute(view["createSql"].replace("${VIEW_NAME}", view["viewName"]))

    cur.executemany(
        "INSERT INTO languages (id, code, name) VALUES (?, ?, ?)",
        [(l["id"], l["code"], l["name"]) for l in manifest["languages"]],
    )
    cur.executemany(
        "INSERT INTO exams (exam) VALUES (?)",
        [(e,) for e in manifest["exams"]],
    )
    # The app's shared default statistics row (stat_id 0), matching the
    # previous seed database.
    cur.execute(
        "INSERT INTO statistics (stat_id, correct_count, wrong_count, "
        "skipped_count, learned) VALUES (0, 0, 0, 0, 0)"
    )
    cur.executemany(
        "INSERT INTO words (id, word, language_id, meaning, level, note, "
        "stat_id, seconds_spent, easiness_factor, interval_days, repetitions, "
        "isFavorite) VALUES (?, ?, ?, ?, ?, ?, 0, 0, 2.5, 0, 0, 0)",
        [
            (w["id"], w["lemma"], w["lang"], w["meaning"], w["level"], w["note"])
            for w in manifest["words"]
        ],
    )
    cur.executemany(
        "INSERT INTO word_translations (word_id, translated_word_id, "
        "sense_index, note, is_primary) VALUES (?, ?, ?, ?, ?)",
        [
            (t["wordId"], t["translatedWordId"], t["senseIndex"], t["note"],
             1 if t["isPrimary"] else 0)
            for t in manifest["translations"]
        ],
    )
    cur.executemany(
        "INSERT INTO synonyms (word_id, synonym_word_id) VALUES (?, ?)",
        [(s["wordId"], s["synonymWordId"]) for s in manifest["synonyms"]],
    )
    cur.executemany(
        "INSERT INTO word_exam_cross_ref (word_id, exam) VALUES (?, ?)",
        [(x["wordId"], x["exam"]) for x in manifest["wordExams"]],
    )
    # Reserve ids >= 1_000_000 for user-added words (seed ids stay below),
    # matching the Room 17->18 migration. AUTOINCREMENT already created the
    # sqlite_sequence row for `words`, so UPDATE it (INSERT would duplicate).
    cur.execute("UPDATE sqlite_sequence SET seq = 999999 WHERE name = 'words'")

    cur.execute(f"PRAGMA user_version = {manifest['dbVersion']}")
    con.commit()
    con.close()
    print(f"built {db_path} ({db_path.stat().st_size // 1024} KiB)")


# --------------------------------------------------------------------------
# Validation
# --------------------------------------------------------------------------

def validate(manifest_path: pathlib.Path) -> int:
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    words = manifest["words"]
    errors: list[str] = []

    by_id = {}
    seen_lemma_lang = set()
    for w in words:
        if w["id"] in by_id:
            errors.append(f"duplicate word id {w['id']}")
        by_id[w["id"]] = w
        key = (w["lemma"], w["lang"])
        if key in seen_lemma_lang:
            errors.append(f"duplicate (lemma, lang): {key}")
        seen_lemma_lang.add(key)
        if not w["lemma"].strip():
            errors.append(f"empty lemma for id {w['id']}")

    en_ids = {w["id"] for w in words if w["lang"] == LANG_EN}
    tr_ids = {w["id"] for w in words if w["lang"] == LANG_TR}

    translated_en = set()
    edge_keys = set()
    for t in manifest["translations"]:
        pk = (t["wordId"], t["translatedWordId"], t["senseIndex"])
        if pk in edge_keys:
            errors.append(f"duplicate translation PK {pk}")
        edge_keys.add(pk)
        if t["wordId"] not in by_id or t["translatedWordId"] not in by_id:
            errors.append(f"orphan translation edge {t}")
            continue
        if by_id[t["wordId"]]["lang"] == by_id[t["translatedWordId"]]["lang"]:
            errors.append(f"same-language translation edge {t}")
        translated_en.add(t["wordId"])

    for s in manifest["synonyms"]:
        a, b = s["wordId"], s["synonymWordId"]
        if a not in by_id or b not in by_id:
            errors.append(f"orphan synonym edge {s}")
        elif by_id[a]["lang"] != by_id[b]["lang"]:
            errors.append(f"cross-language synonym edge {s}")

    exams = set(manifest["exams"])
    for x in manifest["wordExams"]:
        if x["wordId"] not in by_id:
            errors.append(f"orphan exam ref {x}")
        if x["exam"] not in exams:
            errors.append(f"unknown exam {x}")

    missing = en_ids - translated_en
    if missing:
        errors.append(f"{len(missing)} EN words without any translation")

    # Spot round-trips.
    lemma_to_id = {(w["lemma"], w["lang"]): w["id"] for w in words}
    for probe in ("take", "go", "abandon"):
        pid = lemma_to_id.get((probe, LANG_EN))
        if pid is None:
            continue
        targets = [
            t["translatedWordId"] for t in manifest["translations"]
            if t["wordId"] == pid
        ]
        if not targets:
            errors.append(f"round-trip: {probe} has no TR targets")
        else:
            back = {
                t["wordId"] for t in manifest["translations"]
                if t["translatedWordId"] in targets
            }
            if pid not in back:
                errors.append(f"round-trip: {probe} not reachable back")

    print(
        f"words={len(words)} (en={len(en_ids)}, tr={len(tr_ids)}) "
        f"translations={len(manifest['translations'])} "
        f"synonyms={len(manifest['synonyms'])} "
        f"wordExams={len(manifest['wordExams'])}"
    )
    if errors:
        for e in errors[:40]:
            print("ERROR:", e)
        print(f"{len(errors)} validation errors")
        return 1
    print("manifest OK")
    return 0


# --------------------------------------------------------------------------
# Arabic candidate batch (data pending human review — see review/README.md)
# --------------------------------------------------------------------------

_IMPORT_WORD_KEYS = ("id", "lemma", "lang", "level", "note", "meaning")
_IMPORT_EDGE_KEYS = ("wordId", "translatedWordId", "senseIndex", "note", "isPrimary")


def _sha256(path: pathlib.Path) -> str:
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()


def _validate_candidate(words, translations, en_id_values, min_id) -> list[str]:
    """Self-check the AR candidate: unique fresh ids, well-formed edges."""
    errors: list[str] = []
    ar_ids: set[int] = set()
    seen_lemma: set[str] = set()
    for w in words:
        if w["id"] in ar_ids:
            errors.append(f"duplicate AR id {w['id']}")
        if w["id"] < min_id:
            errors.append(f"AR id {w['id']} collides with existing id space")
        ar_ids.add(w["id"])
        if w["lemma"] in seen_lemma:
            errors.append(f"duplicate AR lemma {w['lemma']!r}")
        seen_lemma.add(w["lemma"])
        if not w["note"]:
            errors.append(f"AR word {w['id']} missing romanization")
    edge_pks: set[tuple[int, int, int]] = set()
    with_edge: set[int] = set()
    for t in translations:
        pk = (t["wordId"], t["translatedWordId"], t["senseIndex"])
        if pk in edge_pks:
            errors.append(f"duplicate translation PK {pk}")
        edge_pks.add(pk)
        if t["wordId"] not in ar_ids:
            errors.append(f"edge from unknown AR id {t['wordId']}")
        if t["translatedWordId"] not in en_id_values:
            errors.append(f"edge to unknown EN id {t['translatedWordId']}")
        with_edge.add(t["wordId"])
    for wid in ar_ids - with_edge:
        errors.append(f"AR word {wid} has no translation edge")
    return errors


def ar_candidate(kaikki_path: pathlib.Path, leipzig_path: pathlib.Path,
                 sources: dict, limit: int, out_dir: pathlib.Path,
                 manifest_path: pathlib.Path = DEFAULT_MANIFEST) -> dict:
    """Build the reviewable AR candidate WITHOUT touching ids.lock/seed.

    Provisional ids are allocated through the real ledger (append-after-max) so
    they equal what promotion will assign, but the ledger is never saved — the
    permanent commitment happens only at `ar-promote`, after human sign-off.
    """
    import ar_ingest

    ledger = IdLedger(IDS_LOCK)
    # EN match targets come from the *seed's real word rows*, not the ledger:
    # a handful of ledger ids (e.g. "the") were allocated but dropped from the
    # seed for lack of a parseable sense, so matching against the ledger would
    # attach AR edges to non-existent word rows. Keyed by the same normalized
    # lemma the ledger uses.
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    en_ids = {w["lemma"]: w["id"] for w in manifest["words"] if w["lang"] == LANG_EN}
    en_id_values = set(en_ids.values())
    min_ar_id = ledger._next

    freq = ar_ingest.load_frequency(leipzig_path.read_text(encoding="utf-8"))
    with open(kaikki_path, encoding="utf-8") as f:
        entries = list(ar_ingest.iter_entries(f))

    words, translations, stats = ar_ingest.build_candidate(
        entries, en_ids, freq, lambda w: ledger.get("ar", w), limit
    )
    errors = _validate_candidate(words, translations, en_id_values, min_ar_id)
    if errors:
        for e in errors[:40]:
            print("CANDIDATE ERROR:", e)
        raise SystemExit(f"{len(errors)} candidate validation errors")

    provisional = {
        k: v for k, v in sorted(ledger.ids.items(), key=lambda kv: kv[1])
        if k.startswith("ar|")
    }
    ar_id_list = [w["id"] for w in words]
    candidate = {
        "generatedFor": "#97 Arabic dictgen — DATA PENDING HUMAN REVIEW; ids "
                        "become permanent only when this batch is promoted+merged",
        "dbVersion": DB_VERSION,
        "language": {"id": LANG_AR, "code": "ar", "name": "العربية"},
        "sources": sources,
        "idRange": {
            "first": min(ar_id_list) if ar_id_list else None,
            "last": max(ar_id_list) if ar_id_list else None,
            "count": len(ar_id_list),
            "allocatedAfterExistingMax": min_ar_id - 1,
        },
        "stats": stats,
        "words": words,
        "translations": translations,
        "provisionalIds": provisional,
    }

    out_dir.mkdir(parents=True, exist_ok=True)
    (out_dir / "ar_candidate_manifest.json").write_text(
        json.dumps(candidate, ensure_ascii=False, indent=1) + "\n", encoding="utf-8"
    )
    (out_dir / "ar_ids.candidate.json").write_text(
        json.dumps(provisional, ensure_ascii=False, indent=1) + "\n", encoding="utf-8"
    )
    (out_dir / "ar_review_sample.md").write_text(
        _render_review_sample(candidate), encoding="utf-8"
    )
    print(
        f"AR candidate: {stats['arWords']} words, {stats['translations']} edges, "
        f"ids {candidate['idRange']['first']}..{candidate['idRange']['last']} "
        f"(pending review; ids.lock.json + seed untouched)"
    )
    print("POS:", stats["posDistribution"])
    return candidate


def _render_review_sample(candidate: dict, n: int = 100, seed: int = 97) -> str:
    words = candidate["words"]
    sample = sorted(
        random.Random(seed).sample(words, min(n, len(words))),
        key=lambda w: (w["_freqRank"] is None, w["_freqRank"] or 0, w["id"]),
    )
    s = candidate["sources"]
    lines = [
        "# Arabic candidate — human review sample",
        "",
        "**DATA PENDING HUMAN REVIEW.** Word ids are provisional until this "
        "batch is promoted and merged. Nothing here has landed in "
        "`ids.lock.json` or `seed_v18.json`.",
        "",
        f"- Batch size: **{candidate['stats']['arWords']}** AR headwords, "
        f"**{candidate['stats']['translations']}** AR→EN edges.",
        f"- Provisional id range: **{candidate['idRange']['first']}–"
        f"{candidate['idRange']['last']}** (allocated after existing max "
        f"{candidate['idRange']['allocatedAfterExistingMax']}).",
        f"- Source (senses/romanization): kaikki.org Arabic extract "
        f"`{s['kaikki']['file']}` (sha256 `{s['kaikki']['sha256'][:16]}…`, "
        f"downloaded {s['kaikki']['downloaded']}).",
        f"- Source (frequency rank): Leipzig `{s['leipzig']['file']}` "
        f"(sha256 `{s['leipzig']['sha256'][:16]}…`).",
        "",
        f"Showing {min(n, len(words))} of {len(words)} entries "
        "(random, seed=97; sorted by frequency rank).",
        "",
        "Reviewer checklist per row: diacritics correct? MSA (not dialect)? "
        "romanization matches? EN gloss faithful? RTL renders?",
        "",
        "| # | AR (vocalized) | Romanization | POS | EN gloss(es) | Freq rank | Src line |",
        "|---|----------------|--------------|-----|--------------|-----------|----------|",
    ]
    for i, w in enumerate(sample, start=1):
        glosses = " ⏐ ".join(g.replace("|", "/") for g in w["_senseGlosses"][:3])
        pos = ", ".join(w["_pos"])
        rank = w["_freqRank"] if w["_freqRank"] is not None else "—"
        lines.append(
            f"| {i} | {w['lemma']} | `{w['note']}` | {pos} | {glosses} | "
            f"{rank} | {w['_sourceLine']} |"
        )
    lines.append("")
    return "\n".join(lines)


def ar_promote(candidate_path: pathlib.Path, manifest_path: pathlib.Path) -> int:
    """Fold a REVIEWED AR candidate into ids.lock.json + the seed manifest.

    Run only after human sign-off. Re-allocates AR ids through the live ledger
    in candidate order and aborts if they drift from the reviewed provisional
    ids, so exactly the reviewed data lands. Idempotent: re-running is a no-op.
    """
    candidate = json.loads(candidate_path.read_text(encoding="utf-8"))
    ledger = IdLedger(IDS_LOCK)
    for w in candidate["words"]:
        got = ledger.get("ar", w["lemma"])
        if got != w["id"]:
            raise SystemExit(
                f"ledger drift for {w['lemma']!r}: candidate id {w['id']} != "
                f"ledger id {got}; regenerate the candidate before promoting"
            )

    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    if not any(l["id"] == LANG_AR for l in manifest["languages"]):
        manifest["languages"].append(candidate["language"])
    existing_word_ids = {w["id"] for w in manifest["words"]}
    for w in candidate["words"]:
        if w["id"] not in existing_word_ids:
            manifest["words"].append({k: w[k] for k in _IMPORT_WORD_KEYS})
    manifest["words"].sort(key=lambda w: w["id"])
    existing_edges = {
        (t["wordId"], t["translatedWordId"], t["senseIndex"])
        for t in manifest["translations"]
    }
    for t in candidate["translations"]:
        pk = (t["wordId"], t["translatedWordId"], t["senseIndex"])
        if pk not in existing_edges:
            manifest["translations"].append({k: t[k] for k in _IMPORT_EDGE_KEYS})

    ledger.save()
    manifest_path.write_text(
        json.dumps(manifest, ensure_ascii=False, separators=(",", ":")),
        encoding="utf-8",
    )
    print(
        f"promoted {len(candidate['words'])} AR words + "
        f"{len(candidate['translations'])} edges into {manifest_path.name}; "
        f"ids.lock.json updated. Now: build-db + validate."
    )
    return 0


# --------------------------------------------------------------------------

def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    sub = ap.add_subparsers(dest="cmd", required=True)

    p_emit = sub.add_parser("emit-manifest", help="parse inputs -> manifest")
    p_emit.add_argument("--out", type=pathlib.Path, default=DEFAULT_MANIFEST)

    p_build = sub.add_parser("build-db", help="manifest -> sqlite asset")
    p_build.add_argument("--manifest", type=pathlib.Path, default=DEFAULT_MANIFEST)
    p_build.add_argument("--schema", type=pathlib.Path, required=True,
                         help="Room exported schema JSON (…/AppDatabase/18.json)")
    p_build.add_argument("--out", type=pathlib.Path, default=DEFAULT_DB)

    p_val = sub.add_parser("validate", help="integrity-check the manifest")
    p_val.add_argument("--manifest", type=pathlib.Path, default=DEFAULT_MANIFEST)

    p_arc = sub.add_parser(
        "ar-candidate",
        help="build the reviewable Arabic batch (writes review/, NOT ids.lock/seed)",
    )
    p_arc.add_argument("--kaikki", type=pathlib.Path, required=True,
                       help="kaikki.org English-edition Arabic JSONL extract")
    p_arc.add_argument("--leipzig", type=pathlib.Path, required=True,
                       help="Leipzig Arabic *-words.txt frequency list (CC BY)")
    p_arc.add_argument("--limit", type=int, default=2000,
                       help="cap on AR headwords in the first batch")
    p_arc.add_argument("--out", type=pathlib.Path, default=REVIEW_DIR)
    p_arc.add_argument("--kaikki-url", default="")
    p_arc.add_argument("--leipzig-url", default="")
    p_arc.add_argument("--downloaded", default="",
                       help="ISO date the sources were downloaded (provenance)")

    p_arp = sub.add_parser(
        "ar-promote",
        help="fold a REVIEWED review/ar_candidate_manifest.json into ids.lock + seed",
    )
    p_arp.add_argument("--candidate", type=pathlib.Path,
                       default=REVIEW_DIR / "ar_candidate_manifest.json")
    p_arp.add_argument("--manifest", type=pathlib.Path, default=DEFAULT_MANIFEST)

    args = ap.parse_args()

    if args.cmd == "emit-manifest":
        report: list[str] = []
        manifest = emit_manifest(args.out, report)
        for line in report[:30]:
            print("note:", line)
        if len(report) > 30:
            print(f"... {len(report) - 30} more notes")
        print(
            f"wrote {args.out} — {len(manifest['words'])} words, "
            f"{len(manifest['translations'])} translations, "
            f"{len(manifest['synonyms'])} synonyms"
        )
        return 0
    if args.cmd == "build-db":
        build_db(args.manifest, args.schema, args.out)
        return 0
    if args.cmd == "validate":
        return validate(args.manifest)
    if args.cmd == "ar-candidate":
        sources = {
            "kaikki": {
                "file": args.kaikki.name,
                "sha256": _sha256(args.kaikki),
                "url": args.kaikki_url,
                "downloaded": args.downloaded,
                "license": "CC BY-SA 4.0 (+GFDL); comply via CC BY-SA 4.0",
                "attribution": "English Wiktionary via kaikki.org / wiktextract",
            },
            "leipzig": {
                "file": args.leipzig.name,
                "sha256": _sha256(args.leipzig),
                "url": args.leipzig_url,
                "downloaded": args.downloaded,
                "license": "CC BY 4.0",
                "attribution": "Leipzig Corpora Collection, Universität Leipzig",
            },
        }
        ar_candidate(args.kaikki, args.leipzig, sources, args.limit, args.out)
        return 0
    if args.cmd == "ar-promote":
        return ar_promote(args.candidate, args.manifest)
    return 2


if __name__ == "__main__":
    sys.exit(main())
