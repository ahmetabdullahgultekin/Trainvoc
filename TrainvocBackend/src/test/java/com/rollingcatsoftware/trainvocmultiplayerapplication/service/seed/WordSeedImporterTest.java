package com.rollingcatsoftware.trainvocmultiplayerapplication.service.seed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("WordSeedImporter Tests")
class WordSeedImporterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WordSeedService seedService = mock(WordSeedService.class);

    private WordSeedImporter importer(String path) {
        return new WordSeedImporter(seedService, objectMapper, path);
    }

    @Test
    @DisplayName("parses the fixture manifest, preserving id holes and sense indexes")
    void parsesFixtureManifest() {
        SeedManifest m = importer("seed/seed_v18_test.json").load();

        assertEquals(1, m.manifestVersion());
        assertEquals(18, m.dbVersion());
        assertEquals(2, m.languages().size());
        assertEquals(List.of("YDS", "TOEFL"), m.exams());

        // Ids are inserted verbatim; the manifest legally skips 3 and 4.
        assertEquals(List.of(1L, 2L, 5L, 6L),
                m.words().stream().map(SeedManifest.WordEntry::id).toList());

        assertEquals(2, m.translations().size());
        SeedManifest.TranslationEntry secondSense = m.translations().get(1);
        assertEquals(1, secondSense.senseIndex());
        assertFalse(secondSense.isPrimary());

        assertEquals(1, m.synonyms().size());
        assertTrue(m.synonyms().get(0).wordId() < m.synonyms().get(0).synonymWordId());

        assertEquals(1, m.wordExams().size());
        assertEquals("YDS", m.wordExams().get(0).exam());
    }

    @Test
    @DisplayName("validateVersion accepts the v1 / dbVersion 18 fixture")
    void validateVersionAcceptsMatch() {
        WordSeedImporter importer = importer("seed/seed_v18_test.json");
        importer.validateVersion(importer.load()); // no throw
    }

    @Test
    @DisplayName("validateVersion fails fast on a dbVersion mismatch")
    void validateVersionThrowsOnMismatch() {
        WordSeedImporter importer = importer("seed/seed_v18_badversion.json");
        SeedManifest m = importer.load();
        assertThrows(IllegalStateException.class, () -> importer.validateVersion(m));
    }

    @Test
    @DisplayName("tolerates an absent manifest: load() is null and run() skips seeding")
    void absentManifestIsTolerated() {
        WordSeedImporter importer = importer("seed/does_not_exist.json");

        assertNull(importer.load());

        importer.run(null);
        verifyNoInteractions(seedService);
    }

    @Test
    @DisplayName("run() delegates to the seed service when the manifest is present and valid")
    void runSeedsWhenManifestPresent() {
        importer("seed/seed_v18_test.json").run(null);
        verify(seedService).seedIfNeeded(org.mockito.ArgumentMatchers.any(SeedManifest.class));
    }
}
