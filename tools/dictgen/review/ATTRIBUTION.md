# Attribution & license — Trainvoc Arabic word pack

This directory holds a **derived Arabic (Modern Standard Arabic) word pack**
generated for issue #97. It is offered under **CC BY-SA 4.0**, inheriting the
share-alike obligation of its primary source (parsed Wiktionary). Ship this
notice alongside the data (in-app Attributions screen and/or the pack file).

> **Arabic dictionary content** — Contains information from **Wiktionary**
> (https://en.wiktionary.org), extracted via **kaikki.org / wiktextract**
> (https://kaikki.org). © Wiktionary contributors. Licensed under
> **CC BY-SA 4.0** (https://creativecommons.org/licenses/by-sa/4.0/).
> *Modifications:* parsed; filtered to Modern Standard Arabic lemma senses
> (dialect-tagged, inflected-form and glossless senses removed); each headword
> matched to Trainvoc's existing English word ids by its English glosses;
> deduplicated and reformatted for Trainvoc's Room v18 schema.
> **This derived dataset is likewise licensed under CC BY-SA 4.0.**
>
> **Word-frequency data** — from the **Leipzig Corpora Collection**,
> Universität Leipzig (https://wortschatz.uni-leipzig.de), corpus
> `ara_news_2020_30K`, licensed **CC BY 4.0**
> (https://creativecommons.org/licenses/by/4.0/). *Modifications:* frequency
> ranks recomputed for Trainvoc's word set. Attribution only — no share-alike
> on this column.

## Source files (this batch)

| Role | File | License | Obligation |
|------|------|---------|------------|
| Senses, vocalization, romanization, POS | `kaikki.org-dictionary-Arabic.jsonl` (kaikki.org English edition, Arabic extract) | CC BY-SA 4.0 (+GFDL; comply via CC BY-SA 4.0) | Attribution **+ share-alike** on the derived AR dataset |
| Frequency ranking | `ara_news_2020_30K-words.txt` (Leipzig Corpora Collection) | CC BY 4.0 | Attribution only |

Exact download URLs and SHA-256 digests of the files used are recorded in
`ar_candidate_manifest.json` → `sources`.

## Why a share-alike-safe boundary

The kaikki source is CC BY-SA 4.0, so the **derived Arabic word pack is
CC BY-SA 4.0** — the share-alike binds the *dataset*, not Trainvoc's app code.
Leipzig (CC BY) is used only for the frequency-rank column so that column
carries an attribution-only obligation. Non-commercial sources (KELLY) and
GPL sources (FreeDict) were deliberately avoided so nothing here blocks a
shipped/commercial app. See issue #97 (research comment, 2026-07-15) for the
full source evaluation. *Engineering research, not legal advice.*
