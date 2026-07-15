package com.rollingcatsoftware.trainvocmultiplayerapplication.service.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Deserialized form of {@code seed_v18.json} — the single source of truth for the words DB,
 * produced by the client's {@code tools/dictgen} and copied into the backend build resources
 * by a Gradle task. Consumed by {@link WordSeedImporter} at boot.
 * <p>
 * Invariants the importer relies on (asserted or documented, never re-derived here):
 * ids are opaque and permanent (legal holes — do not assume contiguity); translations are
 * directed EN→TR edges keyed {@code (wordId, translatedWordId, senseIndex)}; synonyms are
 * stored once with {@code wordId < synonymWordId}; exams must exist before word-exam edges.
 * Unknown JSON fields are ignored so the manifest can grow without breaking older backends.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SeedManifest(
        int manifestVersion,
        int dbVersion,
        List<LanguageEntry> languages,
        List<String> exams,
        List<WordEntry> words,
        List<TranslationEntry> translations,
        List<SynonymEntry> synonyms,
        List<WordExamEntry> wordExams
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LanguageEntry(Long id, String code, String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WordEntry(Long id, String lemma, Integer lang, String level, String note, String meaning) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TranslationEntry(Long wordId, Long translatedWordId, Integer senseIndex, String note,
                                   Boolean isPrimary) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SynonymEntry(Long wordId, Long synonymWordId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WordExamEntry(Long wordId, String exam) {
    }
}
