# tools/dictgen/review — Arabic candidate batch (DATA PENDING HUMAN REVIEW)

Dictionary **content** requires human sign-off before it becomes permanent.
This directory is that review gate for the issue #97 Arabic (MSA) batch.
Nothing here has been written into `ids.lock.json` or the shipped
`seed_v18.json` — the provisional word ids only become permanent when the batch
is **promoted** (see below) after the owner approves the data.

## Files

| File | What it is |
|------|------------|
| `ar_candidate_manifest.json` | The full generated batch: language row `{id:3, code:"ar"}`, 2,000 AR word rows, AR→EN translation edges, provisional ids, and the `sources` block (download URLs + SHA-256). `_`-prefixed word keys (`_pos`, `_freqRank`, `_sourceLine`, `_senseGlosses`) are review metadata and are stripped on promotion. |
| `ar_review_sample.md` | 100 random entries rendered as a table (AR vocalized · romanization · POS · EN gloss(es) · freq rank · source line) for eyeball review. |
| `ar_ids.candidate.json` | The provisional `ar\|<headword> → id` ledger additions (ids 10542–12541). |
| `ATTRIBUTION.md` | License/attribution notice for the derived pack (CC BY-SA 4.0) + Leipzig frequency source (CC BY 4.0). |

## What to review

Every AR word is extracted **verbatim** from kaikki (parsed English Wiktionary);
nothing is invented. Please spot-check for:

- **Diacritics** — the vocalized headword (`lemma`) should be correctly and
  fully vocalized.
- **MSA, not dialect** — dialect-tagged senses are filtered out, but confirm no
  colloquial forms slipped through.
- **Romanization** (`note`) matches the headword.
- **Gloss faithfulness** — each AR→EN edge's `note` is the kaikki English gloss;
  the edge points to that gloss's matching existing English word id.
- **RTL rendering** — confirm the app renders the Arabic script RTL with the
  Latin romanization LTR alongside (client wiring is a **follow-up**, not part
  of this PR).

## How to regenerate (deterministic)

```bash
cd tools/dictgen
python3 generate.py ar-candidate \
  --kaikki  <kaikki.org-dictionary-Arabic.jsonl> \
  --leipzig <ara_news_2020_30K-words.txt> \
  --kaikki-url  https://kaikki.org/dictionary/Arabic/kaikki.org-dictionary-Arabic.jsonl \
  --leipzig-url https://downloads.wortschatz-leipzig.de/corpora/ara_news_2020_30K.tar.gz \
  --downloaded  2026-07-15 --limit 2000
```

Same inputs → byte-identical output. The source files are **not** committed
(≈0.5 GB); their SHA-256 digests are recorded in the manifest's `sources` block.

## How to promote (ONLY after sign-off)

Promotion is the single step that makes the ids permanent and lands the data in
the shipped seed. It re-allocates the AR ids through the live ledger in
candidate order and **aborts if they drift** from the reviewed provisional ids,
so exactly the reviewed batch lands. It is idempotent.

```bash
cd tools/dictgen
python3 generate.py ar-promote            # folds candidate → ids.lock.json + seed_v18.json
python3 generate.py build-db --schema \
  ../../TrainvocClient/app/schemas/com.gultekinahmetabdullah.trainvoc.database.AppDatabase/18.json
python3 generate.py validate              # must print "manifest OK"
```

Enabling Arabic in the client's language picker
(`LanguagePreference`, Settings "coming soon" list) is a **separate follow-up**
after the data is promoted.
