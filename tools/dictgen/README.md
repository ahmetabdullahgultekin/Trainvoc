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

## Kotlin mirror

`meaning_parser.py` has a Kotlin twin used by the Room migration to unpack
user-added words:
`TrainvocClient/app/src/main/java/com/gultekinahmetabdullah/trainvoc/database/seed/MeaningParser.kt`.
Both are tested against `fixtures/parser_cases.json` — change the fixtures
first, then keep both implementations green.
