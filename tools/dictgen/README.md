# dictgen — Trainvoc dictionary seed generator

Generates the relational, multilingual vocabulary seed for the Android
app's Room v18 schema from the legacy packed word data.

## Inputs

- `data/all_words.json` — legacy `{word, meaning}` entries grouped by CEFR
  level (`A1`…`C2`) plus a `YDS` group. The packed Turkish `meaning`
  strings (`"(1) terk etmek (= leave) (2) vazgeçmek"`) are unpacked by
  `meaning_parser.py` into separate senses, Turkish lemmas, synonym hints
  and usage notes.
- `ids.lock.json` — **committed ID ledger** mapping `lang|lemma` → word id.
  Regeneration never renumbers existing words; new lemmas append after the
  max id. Do not edit or delete this file: word ids are referenced by user
  progress data and (later) backend sync.

## Outputs

- `TrainvocClient/app/src/main/assets/database/seed_v18.json` — canonical
  manifest (languages, words, translations, synonyms, exam refs). Read by
  the Room `MIGRATION_17_18` at runtime *and* by `build-db`, so upgraders
  and fresh installs converge on identical word ids.
- `TrainvocClient/app/src/main/assets/database/trainvoc-db.db` —
  prepopulated SQLite asset for fresh installs. Its DDL is taken verbatim
  from Room's exported schema JSON so `createFromAsset` validation cannot
  drift.

## Usage

```bash
cd tools/dictgen
python3 test_parser.py          # parser fixtures (shared with Kotlin mirror)
python3 test_ar_ingest.py       # Arabic ingestion filters + id-ledger contract
python3 generate.py emit-manifest
python3 generate.py build-db --schema \
  ../../TrainvocClient/app/schemas/com.gultekinahmetabdullah.trainvoc.database.AppDatabase/18.json
python3 generate.py validate    # orphan edges, dup PKs, round-trips, counts
```

Python 3.10+, stdlib only.

## Adding a language (e.g. Arabic)

The schema needs **no changes**: add the language row (id 3, `ar`), add AR
word rows and AR↔EN (or AR↔TR) `word_translations` edges to the manifest
generation, and regenerate. The ID ledger keeps everything else stable.

### Arabic (MSA) ingestion — `ar-candidate` / `ar-promote` (issue #97)

Arabic is ingested from the **kaikki.org English-edition Arabic** JSONL extract
(vocalized headword, romanization, POS, English glosses; CC BY-SA 4.0) and
ranked by a **Leipzig Corpora Collection** Arabic frequency list (CC BY). The
parsing/filtering lives in `ar_ingest.py` (stdlib, unit-tested like
`meaning_parser.py`); it extracts **verbatim** — no invented words or glosses —
keeping Modern Standard Arabic lemma senses (dialect / inflected-form / no-gloss
senses dropped), requiring a romanization, and attaching each headword to
**existing English word rows** via its English glosses.

Because dictionary content needs **human sign-off**, this is a two-phase flow:

```bash
# 1. Build the reviewable batch → tools/dictgen/review/ (does NOT touch
#    ids.lock.json or seed_v18.json; ids are provisional).
python3 generate.py ar-candidate --kaikki <Arabic.jsonl> --leipzig <ara_*-words.txt> \
  --downloaded 2026-07-15 --limit 2000

# 2. AFTER review sign-off: fold the reviewed candidate into the ledger + seed.
python3 generate.py ar-promote
python3 generate.py build-db --schema .../18.json
python3 generate.py validate
```

Deterministic given the same inputs. See `review/README.md` for the review gate
and `review/ATTRIBUTION.md` for the license notice. Source files (~0.5 GB) are
**not** committed; their SHA-256 digests are recorded in the candidate manifest.
Tests: `python3 test_ar_ingest.py` (fixture `fixtures/ar_kaikki_sample.jsonl`).

## Kotlin mirror

`meaning_parser.py` has a Kotlin twin used by the Room migration to unpack
user-added words:
`TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/database/seed/MeaningParser.kt`.
Both are tested against `fixtures/parser_cases.json` — change the fixtures
first, then keep both implementations green.
