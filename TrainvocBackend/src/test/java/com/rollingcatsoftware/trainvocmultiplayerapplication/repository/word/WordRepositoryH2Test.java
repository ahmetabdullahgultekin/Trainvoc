package com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word;

import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Exam;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Language;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Synonym;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.SynonymId;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.WordExamCrossRef;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.WordTranslation;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.WordTranslationId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the rewritten words-DB repositories — including the two native queries — against
 * a real (H2) database, wired through a self-contained single-datasource context so it does
 * not drag in the app's dual-EMF configuration. Proves the v18 re-key: {@code random()} is
 * portable to H2, {@code findByExam} joins on {@code word_id}, and the composite {@code @IdClass}
 * mappings round-trip.
 */
@SpringBootTest(classes = WordRepositoryH2Test.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:wordsrepo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.jdbc-url=jdbc:h2:mem:wordsrepo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@DisplayName("Words DB repositories (H2)")
class WordRepositoryH2Test {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = Word.class)
    @EnableJpaRepositories(basePackageClasses = WordRepository.class)
    static class Config {
    }

    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private WordExamCrossRefRepository wordExamRepository;
    @Autowired
    private WordTranslationRepository translationRepository;
    @Autowired
    private SynonymRepository synonymRepository;

    @BeforeEach
    void seed() {
        // Idempotent: assigned ids mean saveAll upserts, so re-running per test is safe.
        languageRepository.saveAll(List.of(
                new Language(1L, "en", "English"),
                new Language(2L, "tr", "Türkçe")));
        examRepository.saveAll(List.of(new Exam("YDS")));
        wordRepository.saveAll(List.of(
                word(1L, "book", 1L, "kitap", "A1"),
                word(2L, "run", 1L, "koşmak", "A2"),
                word(5L, "kitap", 2L, "book", null)));   // Turkish row: no CEFR level
        wordExamRepository.save(new WordExamCrossRef(2L, "YDS"));
        translationRepository.save(new WordTranslation(1L, 5L, 0, null, true));
        synonymRepository.save(new Synonym(1L, 2L));
        wordRepository.flush();
    }

    private static Word word(Long id, String lemma, Long languageId, String meaning, String level) {
        Word w = new Word();
        w.setId(id);
        w.setLemma(lemma);
        w.setLanguageId(languageId);
        w.setMeaning(meaning);
        w.setLevel(level);
        return w;
    }

    @Test
    @DisplayName("findByLevel returns the English word at that CEFR level")
    void findByLevel() {
        List<Word> a1 = wordRepository.findByLevel("A1");
        assertEquals(1, a1.size());
        assertEquals("book", a1.get(0).getLemma());
    }

    @Test
    @DisplayName("findRandomWordsByLevel filters by level AND language_id = 1 (English)")
    void findRandomWordsByLevel() {
        List<Word> res = wordRepository.findRandomWordsByLevel("A2", 5);
        assertEquals(1, res.size());
        assertEquals("run", res.get(0).getLemma());
        assertEquals(1L, res.get(0).getLanguageId().longValue());
    }

    @Test
    @DisplayName("findByExam joins word_exam_cross_ref on word_id = words.id")
    void findByExam() {
        List<Word> yds = wordRepository.findByExam("YDS");
        assertEquals(1, yds.size());
        assertEquals("run", yds.get(0).getLemma());
    }

    @Test
    @DisplayName("composite @IdClass keys round-trip for translations, synonyms and exam edges")
    void compositeKeysRoundTrip() {
        assertTrue(translationRepository.findById(new WordTranslationId(1L, 5L, 0)).isPresent());
        assertTrue(synonymRepository.findById(new SynonymId(1L, 2L)).isPresent());
        assertTrue(wordExamRepository.findById(new WordExamCrossRef.PK(2L, "YDS")).isPresent());

        assertEquals(1, translationRepository.findByWordId(1L).size());
        assertEquals(1, wordExamRepository.findByExam("YDS").size());
        assertEquals(1, wordExamRepository.findByWordId(2L).size());
    }

    @Test
    @DisplayName("language code unique lookup works")
    void languageByCode() {
        assertTrue(languageRepository.findByCode("tr").isPresent());
        assertEquals(2L, languageRepository.findByCode("tr").orElseThrow().getId().longValue());
    }
}
