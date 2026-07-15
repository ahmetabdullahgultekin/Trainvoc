package com.rollingcatsoftware.trainvocmultiplayerapplication.service.seed;

import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.ExamRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.LanguageRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.SynonymRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordExamCrossRefRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordTranslationRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Exam;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Language;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Synonym;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.WordExamCrossRef;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.WordTranslation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WordSeedService Tests")
class WordSeedServiceTest {

    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private ExamRepository examRepository;
    @Mock
    private WordRepository wordRepository;
    @Mock
    private WordTranslationRepository translationRepository;
    @Mock
    private SynonymRepository synonymRepository;
    @Mock
    private WordExamCrossRefRepository wordExamRepository;

    @InjectMocks
    private WordSeedService service;

    /** 2 languages, 2 exams, 4 words, 2 translations, 1 synonym, 1 word-exam edge. */
    private SeedManifest fixture() {
        return new SeedManifest(
                1, 18,
                List.of(new SeedManifest.LanguageEntry(1L, "en", "English"),
                        new SeedManifest.LanguageEntry(2L, "tr", "Türkçe")),
                List.of("YDS", "TOEFL"),
                List.of(new SeedManifest.WordEntry(1L, "book", 1, "A1", null, "kitap"),
                        new SeedManifest.WordEntry(2L, "run", 1, "A2", "verb", "koşmak"),
                        new SeedManifest.WordEntry(5L, "kitap", 2, null, null, "book"),
                        new SeedManifest.WordEntry(6L, "koşmak", 2, null, null, "run")),
                List.of(new SeedManifest.TranslationEntry(1L, 5L, 0, null, true),
                        new SeedManifest.TranslationEntry(2L, 6L, 1, "informal", false)),
                List.of(new SeedManifest.SynonymEntry(1L, 2L)),
                List.of(new SeedManifest.WordExamEntry(2L, "YDS")));
    }

    @Test
    @DisplayName("skips seeding when word and language counts already match the manifest")
    void skipsWhenAlreadySeeded() {
        when(wordRepository.count()).thenReturn(4L);
        when(languageRepository.count()).thenReturn(2L);

        boolean seeded = service.seedIfNeeded(fixture());

        assertFalse(seeded);
        verify(languageRepository, never()).saveAll(any());
        verify(wordRepository, never()).saveAll(any());
        verify(translationRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("re-seeds when counts differ (e.g. a half-populated table)")
    void reSeedsWhenCountsDiffer() {
        when(wordRepository.count()).thenReturn(1L).thenReturn(4L);
        when(languageRepository.count()).thenReturn(2L);

        boolean seeded = service.seedIfNeeded(fixture());

        assertTrue(seeded);
        verify(wordRepository).saveAll(any());
    }

    @Test
    @DisplayName("seeds every table in FK-dependency order with the manifest's counts")
    @SuppressWarnings("unchecked")
    void seedsInDependencyOrder() {
        when(wordRepository.count()).thenReturn(0L).thenReturn(4L);
        when(languageRepository.count()).thenReturn(0L);

        ArgumentCaptor<List<Language>> langCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Exam>> examCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Word>> wordCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<WordTranslation>> transCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Synonym>> synCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<WordExamCrossRef>> examEdgeCap = ArgumentCaptor.forClass(List.class);

        boolean seeded = service.seedIfNeeded(fixture());
        assertTrue(seeded);

        InOrder order = inOrder(languageRepository, examRepository, wordRepository,
                translationRepository, synonymRepository, wordExamRepository);
        order.verify(languageRepository).saveAll(langCap.capture());
        order.verify(examRepository).saveAll(examCap.capture());
        order.verify(wordRepository).saveAll(wordCap.capture());
        order.verify(translationRepository).saveAll(transCap.capture());
        order.verify(synonymRepository).saveAll(synCap.capture());
        order.verify(wordExamRepository).saveAll(examEdgeCap.capture());

        assertEquals(2, langCap.getValue().size());
        assertEquals(2, examCap.getValue().size());
        assertEquals(4, wordCap.getValue().size());
        assertEquals(2, transCap.getValue().size());
        assertEquals(1, synCap.getValue().size());
        assertEquals(1, examEdgeCap.getValue().size());
    }

    @Test
    @DisplayName("maps manifest fields onto entities (lang -> languageId, null isPrimary -> false)")
    @SuppressWarnings("unchecked")
    void mapsManifestFieldsOntoEntities() {
        when(wordRepository.count()).thenReturn(0L).thenReturn(4L);
        when(languageRepository.count()).thenReturn(0L);

        ArgumentCaptor<List<Word>> wordCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<WordTranslation>> transCap = ArgumentCaptor.forClass(List.class);

        service.seedIfNeeded(fixture());

        verify(wordRepository).saveAll(wordCap.capture());
        Word turkishWord = wordCap.getValue().stream()
                .filter(w -> w.getId() == 5L).findFirst().orElseThrow();
        assertEquals("kitap", turkishWord.getLemma());
        assertEquals(2L, turkishWord.getLanguageId().longValue());
        assertEquals("book", turkishWord.getMeaning());

        verify(translationRepository).saveAll(transCap.capture());
        WordTranslation secondary = transCap.getValue().stream()
                .filter(t -> t.getSenseIndex() == 1).findFirst().orElseThrow();
        assertFalse(secondary.isPrimary());
        WordTranslation primary = transCap.getValue().stream()
                .filter(t -> t.getSenseIndex() == 0).findFirst().orElseThrow();
        assertTrue(primary.isPrimary());
    }
}
