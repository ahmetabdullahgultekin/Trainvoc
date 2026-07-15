"""Arabic (MSA) ingestion for dictgen.

Parses the kaikki.org English-edition Arabic JSONL extract into review-ready
Arabic word rows + Arabic->English translation edges for the Room v18 schema.

Faithful extraction only. The vocalized headword, romanization, part of speech
and English glosses all come **verbatim** from kaikki (parsed English
Wiktionary, CC BY-SA 4.0); this module never invents a word, a translation or a
gloss. It only *selects* and *reshapes* existing data:

  * senses are filtered to Modern Standard Arabic lemma senses — dialect-tagged,
    inflected-form (`form-of`/`alt-of`), and glossless senses are dropped;
  * a headword is kept only if it carries a romanization (research requirement);
  * each Arabic word attaches to *existing* English word ids by matching its
    English glosses against the EN lemmas already in the id ledger; a headword
    with no such overlap is dropped so every emitted edge is well-formed;
  * headwords are ranked by a Leipzig Corpora Collection Arabic frequency list
    (CC BY) and capped to a first batch.

The kaikki record shape this relies on (verified against the 2026-07 extract):
  word            unvocalized headword (frequency-match key)
  pos             part of speech
  forms[]         {form, tags}; tag "canonical" -> vocalized headword,
                  tag "romanization" -> Latin transliteration
  senses[]        {glosses:[str], tags:[str], raw_tags:[str], categories:[{name}]}

Stdlib only, Python 3.10+. Kept separate from generate.py the same way
meaning_parser.py is, so the filters are unit-testable in isolation.
"""

from __future__ import annotations

import json
import re
import unicodedata
from dataclasses import dataclass, field

# --------------------------------------------------------------------------
# Arabic script helpers
# --------------------------------------------------------------------------

# Combining marks (tashkeel) + tatweel (kashida) stripped for frequency
# matching; the stored headword keeps its full vocalization.
_DIACRITICS = re.compile(
    "[ؐ-ًؚ-ٰٟۖ-ۜ۟-ۨ"
    "۪-ۭ࣓-ࣿـ]"
)


def strip_diacritics(s: str) -> str:
    """Undiacritized, tatweel-free NFC form — the frequency-match key."""
    return _DIACRITICS.sub("", unicodedata.normalize("NFC", s))


def norm_ar(s: str) -> str:
    """Normalize a vocalized headword for storage / ledger keying."""
    return unicodedata.normalize("NFC", " ".join(s.split()))


# --------------------------------------------------------------------------
# MSA / lemma-sense filters
# --------------------------------------------------------------------------

# Part-of-speech allowlist: pedagogical content classes + high-frequency
# function words. Proper nouns ("name"), affixes, symbols, punctuation and
# multi-word phrase/proverb entries are excluded.
POS_ALLOW = frozenset({
    "noun", "verb", "adj", "adv", "num", "pron",
    "prep", "conj", "particle", "det", "interj", "article",
})

# Sense tags that mean "not a Modern-Standard-Arabic dictionary sense".
DROP_SENSE_TAGS = frozenset({
    "no-gloss", "form-of", "alt-of", "alternative",
    "obsolete", "archaic", "misspelling", "romanization",
})

# Dialect / colloquial markers. Matched case-insensitively against a sense's
# tags, raw_tags and category names; a hit drops that sense (keep MSA only).
DIALECT_MARKERS = (
    "dialect", "colloquial", "egyptian", "levantine", "gulf", "hijazi",
    "hejazi", "najdi", "moroccan", "maghrebi", "maghrbi", "iraqi",
    "tunisian", "algerian", "libyan", "sudanese", "yemeni", "bahrani",
    "andalusian", "chadian", "cypriot", "nigerian", "juba",
)

_PAREN = re.compile(r"\([^)]*\)")
_BRACKET = re.compile(r"\[[^\]]*\]")
_LEADING = re.compile(r"^(?:to|a|an|the)\s+", re.IGNORECASE)


def _sense_is_dialectal(sense: dict) -> bool:
    blob = " ".join(
        (sense.get("tags") or [])
        + (sense.get("raw_tags") or [])
        + [c.get("name", "") for c in (sense.get("categories") or [])]
    ).lower()
    return any(m in blob for m in DIALECT_MARKERS)


def gloss_candidates(gloss: str) -> list[str]:
    """Normalized English lemma candidates named by a kaikki gloss.

    "to speak, to talk, to address" -> ["speak", "talk", "address"]
    "allowed, permitted, allowable." -> ["allowed", "permitted", "allowable"]

    Parentheticals and wiki-link residue are dropped; leading articles / the
    infinitive "to " are stripped. The gloss text itself is never altered on
    disk — this only derives lookup keys for matching existing EN ids.
    """
    g = _BRACKET.sub(" ", _PAREN.sub(" ", gloss))
    out: list[str] = []
    for part in re.split(r"[;,/]", g):
        p = _LEADING.sub("", part.strip())
        p = " ".join(p.strip(" .!?;:\"'“”‘’").split()).lower()
        if p:
            out.append(p)
    return out


# --------------------------------------------------------------------------
# Parsed entry model
# --------------------------------------------------------------------------

@dataclass
class ArSense:
    gloss: str                       # primary English gloss, verbatim from kaikki
    en_candidates: list[str]         # normalized EN lemma candidates from the gloss
    pos: str


@dataclass
class ArEntry:
    word: str                        # vocalized canonical headword (display + key)
    unvoc: str                       # undiacritized form (frequency-match key)
    roman: str                       # romanization
    pos: str                         # part of speech of the source record
    source_line: int                 # 1-based JSONL line (review provenance)
    senses: list[ArSense] = field(default_factory=list)


def _canonical_and_roman(obj: dict) -> tuple[str | None, str | None]:
    canonical = roman = None
    for fm in obj.get("forms") or []:
        tags = fm.get("tags") or []
        if canonical is None and "canonical" in tags:
            canonical = fm.get("form")
        if roman is None and "romanization" in tags:
            roman = fm.get("form")
    return canonical, roman


def parse_entry(obj: dict, line_no: int) -> ArEntry | None:
    """Parse one kaikki record into an ArEntry, or None if it is not a usable
    MSA lemma (wrong POS, no romanization, no surviving gloss sense)."""
    if obj.get("lang_code") != "ar" or obj.get("pos") not in POS_ALLOW:
        return None

    canonical, roman = _canonical_and_roman(obj)
    headword = norm_ar(canonical or obj.get("word") or "")
    if not headword or not roman:                 # romanization is required
        return None
    roman = roman.strip()

    senses: list[ArSense] = []
    for sense in obj.get("senses") or []:
        glosses = sense.get("glosses")
        if not glosses:
            continue
        tags = sense.get("tags") or []
        if any(t in DROP_SENSE_TAGS for t in tags):
            continue
        if "form_of" in sense or "alt_of" in sense:
            continue
        if _sense_is_dialectal(sense):
            continue
        gloss = glosses[0].strip()
        cands = gloss_candidates(gloss)
        if not cands:
            continue
        senses.append(ArSense(gloss=gloss, en_candidates=cands, pos=obj["pos"]))

    if not senses:
        return None
    return ArEntry(
        word=headword,
        unvoc=strip_diacritics(headword),
        roman=roman,
        pos=obj["pos"],
        source_line=line_no,
        senses=senses,
    )


def iter_entries(lines) -> "Iterator[ArEntry]":
    """Yield ArEntry for every usable record in a kaikki JSONL iterable."""
    for i, line in enumerate(lines, start=1):
        line = line.strip()
        if not line:
            continue
        entry = parse_entry(json.loads(line), i)
        if entry is not None:
            yield entry


# --------------------------------------------------------------------------
# Frequency ranking (Leipzig Corpora Collection word list, CC BY)
# --------------------------------------------------------------------------

def load_frequency(words_txt: str) -> dict[str, int]:
    """Leipzig `*-words.txt` (id \\t word \\t freq) -> {undiacritized word: rank}.

    The file is keyed by an arbitrary surrogate id, so rank is assigned here by
    descending frequency (rank 1 = most frequent). Ties break by word for
    determinism. Keys are undiacritized to match kaikki headwords.
    """
    rows: list[tuple[int, str]] = []
    for line in words_txt.splitlines():
        cols = line.split("\t")
        if len(cols) < 3:
            continue
        word = cols[1].strip()
        try:
            freq = int(cols[2])
        except ValueError:
            continue
        if word:
            rows.append((freq, word))
    rows.sort(key=lambda r: (-r[0], r[1]))
    rank: dict[str, int] = {}
    for i, (_freq, word) in enumerate(rows, start=1):
        key = strip_diacritics(word)
        if key and key not in rank:            # first (best) rank wins
            rank[key] = i
    return rank


# --------------------------------------------------------------------------
# Candidate builder
# --------------------------------------------------------------------------

_NO_RANK = 1 << 30                              # sorts after every ranked word


@dataclass
class CandidateWord:
    id: int
    lemma: str                                  # vocalized headword
    roman: str
    pos_set: list[str]
    freq_rank: int | None
    source_line: int
    meaning: str                                # display cache (matched EN lemmas)


def build_candidate(entries, en_ids, freq_rank, alloc, limit):
    """Assemble the review candidate.

    entries    : iterable of ArEntry (file order)
    en_ids     : {normalized EN lemma: existing EN word id}
    freq_rank  : {undiacritized AR word: rank}
    alloc      : callable(headword) -> permanent id (append-only ledger .get)
    limit      : cap on the number of AR headwords emitted

    Returns (words, translations, stats). Each AR headword becomes one row
    (UNIQUE(word, language_id)); its senses across all POS merge in encounter
    order; an edge is emitted per (sense, matched existing EN id). Only
    headwords with >=1 matched edge survive. Ordering (and therefore id
    allocation) is deterministic: (frequency rank, headword).
    """
    # 1. Group senses by vocalized headword, preserving encounter order.
    grouped: "OrderedDictType[str, dict]" = {}
    for e in entries:
        g = grouped.get(e.word)
        if g is None:
            g = grouped[e.word] = {
                "roman": e.roman, "unvoc": e.unvoc,
                "pos": [], "source_line": e.source_line, "senses": [],
            }
        if e.pos not in g["pos"]:
            g["pos"].append(e.pos)
        for s in e.senses:
            g["senses"].append(s)

    # 2. Resolve each headword's senses to edges against existing EN ids.
    resolved: list[dict] = []
    for word, g in grouped.items():
        sense_edges: list[tuple[int, list[tuple[int, str]]]] = []
        seen_gloss: set[str] = set()
        matched_lemmas: list[str] = []
        for s in g["senses"]:
            if s.gloss in seen_gloss:
                continue
            seen_gloss.add(s.gloss)
            hits: list[tuple[int, str]] = []
            seen_ids: set[int] = set()
            for cand in s.en_candidates:
                en_id = en_ids.get(cand)
                if en_id is not None and en_id not in seen_ids:
                    seen_ids.add(en_id)
                    hits.append((en_id, cand))
                    if cand not in matched_lemmas:
                        matched_lemmas.append(cand)
            if hits:
                sense_edges.append((len(sense_edges), s.gloss, hits))
        if not sense_edges:
            continue
        resolved.append({
            "word": word, "roman": g["roman"], "pos": g["pos"],
            "unvoc": g["unvoc"], "source_line": g["source_line"],
            "sense_edges": sense_edges, "meaning_lemmas": matched_lemmas,
            "rank": freq_rank.get(g["unvoc"], _NO_RANK),
        })

    # 3. Deterministic order: most frequent first, then headword; cap.
    resolved.sort(key=lambda r: (r["rank"], r["word"]))
    capped = resolved[:limit]

    # 4. Allocate permanent ids in that order and emit rows + edges.
    words: list[dict] = []
    translations: list[dict] = []
    pos_hist: dict[str, int] = {}
    with_two_plus = 0
    for r in capped:
        ar_id = alloc(r["word"])
        for p in r["pos"]:
            pos_hist[p] = pos_hist.get(p, 0) + 1
        meaning = ", ".join(r["meaning_lemmas"][:6])
        words.append({
            "id": ar_id,
            "lemma": r["word"],
            "lang": 3,
            "level": None,
            "note": r["roman"],
            "meaning": meaning,
            # review-only metadata (stripped before promotion into the seed):
            "_pos": r["pos"],
            "_freqRank": None if r["rank"] == _NO_RANK else r["rank"],
            "_sourceLine": r["source_line"],
            "_senseGlosses": [g for _i, g, _h in r["sense_edges"]],
        })
        edge_count = 0
        for sense_index, gloss, hits in r["sense_edges"]:
            for pos, (en_id, _cand) in enumerate(hits):
                translations.append({
                    "wordId": ar_id,
                    "translatedWordId": en_id,
                    "senseIndex": sense_index,
                    "note": gloss,
                    "isPrimary": sense_index == 0 and pos == 0,
                })
                edge_count += 1
        if edge_count >= 2:
            with_two_plus += 1

    stats = {
        "arWords": len(words),
        "translations": len(translations),
        "posDistribution": dict(sorted(pos_hist.items())),
        "withFreqRank": sum(1 for w in words if w["_freqRank"] is not None),
        "withTwoPlusEdges": with_two_plus,
        "candidatesBeforeCap": len(resolved),
    }
    return words, translations, stats
