-- ============================================================================
-- trainvoc-words  —  DDL for the words database (schema v18)
-- ============================================================================
-- This script creates the RELATIONAL, MULTILINGUAL words schema and NOTHING ELSE.
-- It is DDL-only on purpose: the row data (languages, exams, ~10.5k words, their
-- translations, synonyms and exam edges) is loaded at application boot by
-- `WordSeedImporter`, which reads the single source of truth
-- `classpath:seed/seed_v18.json` (produced by the client's tools/dictgen and copied
-- into the backend build by a Gradle task). The former ~12k-line INSERT dump was
-- retired with the v18 re-key; see TrainvocBackend/CLAUDE.md and issue #96.
--
-- Mounted by docker-compose as /docker-entrypoint-initdb.d for the trainvoc-words
-- container, so the schema exists before Spring Boot (Hibernate ddl-auto=validate)
-- connects. Column names and types match the JPA entities in
-- com.rollingcatsoftware.trainvocmultiplayerapplication.words.model exactly.
--
-- Ids are application-assigned and permanent (legal holes — never renumber).
-- ============================================================================

BEGIN;

-- Learning languages (en=1, tr=2). Ids are assigned by the manifest, not generated.
CREATE TABLE IF NOT EXISTS languages
(
    id   BIGINT NOT NULL,
    code TEXT   NOT NULL,
    name TEXT   NOT NULL,
    CONSTRAINT pk_languages PRIMARY KEY (id),
    CONSTRAINT uk_languages_code UNIQUE (code)
);

-- Exam categories (TOEFL, IELTS, YDS, YÖKDİL, KPDS, Mixed).
CREATE TABLE IF NOT EXISTS exams
(
    exam TEXT NOT NULL,
    CONSTRAINT pk_exams PRIMARY KEY (exam)
);

-- Words. Every language is first-class: an English lemma and its Turkish gloss are two
-- separate rows linked through word_translations. `meaning` is a denormalized display
-- cache kept from the client (NOT NULL to mirror Room). `language_id` is a real FK.
CREATE TABLE IF NOT EXISTS words
(
    id          BIGINT NOT NULL,
    lemma       TEXT   NOT NULL,
    language_id BIGINT NOT NULL,
    meaning     TEXT   NOT NULL,
    level       TEXT,
    note        TEXT,
    CONSTRAINT pk_words PRIMARY KEY (id),
    CONSTRAINT uk_words_lemma_language UNIQUE (lemma, language_id),
    CONSTRAINT fk_words_language FOREIGN KEY (language_id) REFERENCES languages (id)
);

CREATE INDEX IF NOT EXISTS idx_words_language_id ON words (language_id);
CREATE INDEX IF NOT EXISTS idx_words_level ON words (level);

-- Directed, sense-grouped translation edges (EN -> TR in the manifest).
CREATE TABLE IF NOT EXISTS word_translations
(
    word_id            BIGINT  NOT NULL,
    translated_word_id BIGINT  NOT NULL,
    sense_index        INTEGER NOT NULL,
    note               TEXT,
    is_primary         BOOLEAN NOT NULL,
    CONSTRAINT pk_word_translations PRIMARY KEY (word_id, translated_word_id, sense_index),
    CONSTRAINT fk_word_translations_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE,
    CONSTRAINT fk_word_translations_translated FOREIGN KEY (translated_word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_word_translations_word_id ON word_translations (word_id);
CREATE INDEX IF NOT EXISTS idx_word_translations_translated_word_id ON word_translations (translated_word_id);

-- Same-language synonym pairs, stored once with word_id < synonym_word_id.
CREATE TABLE IF NOT EXISTS synonyms
(
    word_id         BIGINT NOT NULL,
    synonym_word_id BIGINT NOT NULL,
    CONSTRAINT pk_synonyms PRIMARY KEY (word_id, synonym_word_id),
    CONSTRAINT fk_synonyms_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE,
    CONSTRAINT fk_synonyms_synonym FOREIGN KEY (synonym_word_id) REFERENCES words (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_synonyms_word_id ON synonyms (word_id);
CREATE INDEX IF NOT EXISTS idx_synonyms_synonym_word_id ON synonyms (synonym_word_id);

-- Word <-> exam membership (id-keyed; every manifest edge targets YDS).
CREATE TABLE IF NOT EXISTS word_exam_cross_ref
(
    word_id BIGINT NOT NULL,
    exam    TEXT   NOT NULL,
    CONSTRAINT pk_word_exam_cross_ref PRIMARY KEY (word_id, exam),
    CONSTRAINT fk_word_exam_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE,
    CONSTRAINT fk_word_exam_exam FOREIGN KEY (exam) REFERENCES exams (exam)
);

CREATE INDEX IF NOT EXISTS idx_word_exam_cross_ref_exam ON word_exam_cross_ref (exam);

COMMIT;
