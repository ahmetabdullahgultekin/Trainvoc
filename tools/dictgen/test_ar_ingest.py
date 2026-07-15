"""Tests for the Arabic ingestion stage. Run: python3 test_ar_ingest.py

Exercises the MSA/dialect/romanization/form-of filters, gloss->EN candidate
extraction, deterministic frequency-ranked ordering, and the append-only
id-ledger contract, against a small committed fixture of *real* (trimmed)
kaikki records (fixtures/ar_kaikki_sample.jsonl).
"""

import json
import pathlib
import sys
import tempfile

import ar_ingest
from generate import IdLedger

HERE = pathlib.Path(__file__).parent
FIXTURE = HERE / "fixtures" / "ar_kaikki_sample.jsonl"

# A tiny slice of "existing EN lemmas" so edge attachment is exercised without
# the full 10k ledger. Ids are arbitrary but must be echoed back on the edges.
EN_IDS = {
    "book": 101, "letter": 102, "house": 110, "building": 111, "new": 120,
    "big": 130, "large": 131, "great": 132, "sun": 140, "dog": 150,
    "school": 160, "academy": 161, "water": 170, "man": 180, "day": 190,
    "gold": 200, "eat": 210, "consume": 211, "fox": 220, "moon": 230,
    "door": 240, "gate": 241, "write": 250, "eye": 260,
}

FAILURES: list[str] = []


def check(cond: bool, msg: str) -> None:
    if cond:
        print(f"ok   {msg}")
    else:
        FAILURES.append(msg)
        print(f"FAIL {msg}")


def load_entries():
    with FIXTURE.open(encoding="utf-8") as f:
        return list(ar_ingest.iter_entries(f))


def by_word(entries):
    return {e.word: e for e in entries}


# --------------------------------------------------------------------------

def test_strip_diacritics():
    check(ar_ingest.strip_diacritics("كِتَاب") == "كتاب", "strip_diacritics kitāb")
    check(ar_ingest.strip_diacritics("مَدْرَسَة") == "مدرسة", "strip_diacritics madrasa")
    # tatweel (kashida) removed too
    check(ar_ingest.strip_diacritics("كــتاب") == "كتاب", "strip_diacritics tatweel")


def test_gloss_candidates():
    check(ar_ingest.gloss_candidates("to speak, to talk, to address")
          == ["speak", "talk", "address"], "gloss verb infinitive+split")
    check(ar_ingest.gloss_candidates("allowed, permitted, allowable.")
          == ["allowed", "permitted", "allowable"], "gloss trailing period")
    check(ar_ingest.gloss_candidates("a house (dwelling)")
          == ["house"], "gloss article + parenthetical stripped")
    check(ar_ingest.gloss_candidates("the Scripture") == ["scripture"],
          "gloss leading 'the' stripped + lowercased")


def test_romanization_required():
    entries = by_word(load_entries())
    # روز has a canonical form + gloss but NO romanization form -> dropped.
    check(all(e.roman for e in entries.values()), "every parsed entry has romanization")
    check("رَوْز" not in entries and "روز" not in entries,
          "no-romanization headword (روز) dropped")


def test_nogloss_and_formof_dropped():
    entries = by_word(load_entries())
    # 'overweight' is a no-gloss translation stub; كبيرة is the feminine
    # (form-of / alt-of) of كبير — both must be absent.
    check("overweight" not in entries, "no-gloss stub dropped")
    check(not any("كبيرة" == ar_ingest.strip_diacritics(w) for w in entries),
          "form-of feminine (كبيرة) dropped")


def test_dialect_senses_dropped():
    entries = by_word(load_entries())
    # ثعلب: MSA 'fox' kept, dialectal 'jackal' (tags: dialectal) dropped.
    fox = entries.get("ثَعْلَب")
    check(fox is not None, "ثعلب parsed")
    if fox:
        joined = " ".join(s.gloss.lower() for s in fox.senses)
        check("fox" in joined, "ثعلب keeps MSA 'fox' sense")
        check("jackal" not in joined, "ثعلب drops dialectal 'jackal' sense (tag path)")
    # ثقافة: MSA 'education' kept, Levantine sense (dialect via category) dropped.
    thaqafa = entries.get("ثَقَافَة")
    check(thaqafa is not None and len(thaqafa.senses) == 1,
          "ثقافة drops Levantine sense (category path), keeps MSA")


def test_build_candidate_edges_and_survival():
    entries = load_entries()
    # Frequency: make school rank best, then book, then new; others unranked.
    freq = {
        ar_ingest.strip_diacritics("مَدْرَسَة"): 1,
        ar_ingest.strip_diacritics("كِتَاب"): 2,
        ar_ingest.strip_diacritics("جَدِيد"): 3,
    }
    allocated = {}
    counter = [500]

    def alloc(word):
        if word not in allocated:
            counter[0] += 1
            allocated[word] = counter[0]
        return allocated[word]

    words, translations, stats = ar_ingest.build_candidate(
        entries, EN_IDS, freq, alloc, limit=100
    )
    wid = {w["lemma"]: w for w in words}

    # Only headwords with >=1 EN-matching gloss survive. ثقافة's only gloss is
    # "education, literacy" — neither in EN_IDS — so it must be dropped.
    check("ثَقَافَة" not in wid, "headword with no EN overlap dropped")
    check("مَدْرَسَة" in wid and "كِتَاب" in wid, "matching headwords survive")

    # Frequency-ranked ordering drives id allocation: madrasa(1) < kitab(2) < jadid(3).
    check(wid["مَدْرَسَة"]["id"] < wid["كِتَاب"]["id"] < wid["جَدِيد"]["id"],
          "ids follow frequency rank order")

    # kitāb edges attach to the *existing* EN ids for book/letter with the
    # right sense grouping (book at sense 0, letter at sense 1).
    kitab_edges = [
        (t["translatedWordId"], t["senseIndex"])
        for t in translations if t["wordId"] == wid["كِتَاب"]["id"]
    ]
    check((EN_IDS["book"], 0) in kitab_edges, "kitāb->book edge at sense 0")
    check((EN_IDS["letter"], 1) in kitab_edges, "kitāb->letter edge at sense 1")
    check(any(t["isPrimary"] for t in translations
              if t["wordId"] == wid["كِتَاب"]["id"] and t["senseIndex"] == 0),
          "kitāb sense-0 first edge is primary")

    # Every emitted edge targets an existing EN id; every AR word carries romanization.
    en_id_values = set(EN_IDS.values())
    check(all(t["translatedWordId"] in en_id_values for t in translations),
          "all edges point to existing EN ids")
    check(all(w["note"] for w in words), "all AR words carry romanization in note")
    check(stats["arWords"] == len(words) and stats["translations"] == len(translations),
          "stats agree with emitted rows")


def test_determinism():
    entries = load_entries()
    freq = {ar_ingest.strip_diacritics("كِتَاب"): 5}

    def run():
        seq = [1000]

        def alloc(word):
            seq[0] += 1
            return seq[0]
        return ar_ingest.build_candidate(entries, EN_IDS, freq, alloc, limit=100)

    w1, t1, s1 = run()
    w2, t2, s2 = run()
    check(w1 == w2 and t1 == t2 and s1 == s2,
          "build_candidate is deterministic across runs")


def test_id_ledger_append_only():
    with tempfile.TemporaryDirectory() as d:
        lock = pathlib.Path(d) / "ids.lock.json"
        lock.write_text(json.dumps({"en|book": 1, "en|house": 2, "tr|ev": 3}),
                        encoding="utf-8")
        ledger = IdLedger(lock)
        # Existing keys keep their ids.
        check(ledger.get("en", "book") == 1, "existing en id preserved")
        check(ledger.get("tr", "ev") == 3, "existing tr id preserved")
        # New AR keys append strictly after the current max (3).
        first = ledger.get("ar", "كِتَاب")
        second = ledger.get("ar", "بَيْت")
        check(first == 4 and second == 5, "new AR ids append after max")
        # Idempotent: re-requesting returns the same id.
        check(ledger.get("ar", "كِتَاب") == 4, "AR id stable on re-request")
        # Persisted + reloaded ledger allocates the next fresh id without renumbering.
        ledger.save()
        reloaded = IdLedger(lock)
        check(reloaded.get("ar", "كِتَاب") == 4, "id survives save/reload (no renumber)")
        check(reloaded.get("ar", "مَاء") == 6, "next fresh id continues after reload")


def main() -> int:
    test_strip_diacritics()
    test_gloss_candidates()
    test_romanization_required()
    test_nogloss_and_formof_dropped()
    test_dialect_senses_dropped()
    test_build_candidate_edges_and_survival()
    test_determinism()
    test_id_ledger_append_only()
    print()
    if FAILURES:
        print(f"{len(FAILURES)} checks failed")
        return 1
    print("all ar_ingest checks passed")
    return 0


if __name__ == "__main__":
    sys.exit(main())
