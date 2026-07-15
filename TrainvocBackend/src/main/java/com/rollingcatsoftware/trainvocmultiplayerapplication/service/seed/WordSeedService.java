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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Loads the parsed {@link SeedManifest} into the words database. All writes run in a single
 * transaction bound to the {@code secondTransactionManager} (the words persistence unit).
 * <p>
 * Idempotent: a fast-path count check skips seeding when the word and language counts already
 * match the manifest. Ids are inserted verbatim (application-assigned, permanent); rows are
 * saved in FK-dependency order so the physical PostgreSQL foreign keys are satisfied at commit.
 */
@Service
public class WordSeedService {

    private static final Logger log = LoggerFactory.getLogger(WordSeedService.class);
    private static final int BATCH_SIZE = 1000;

    private final LanguageRepository languageRepository;
    private final ExamRepository examRepository;
    private final WordRepository wordRepository;
    private final WordTranslationRepository translationRepository;
    private final SynonymRepository synonymRepository;
    private final WordExamCrossRefRepository wordExamRepository;

    public WordSeedService(LanguageRepository languageRepository,
                           ExamRepository examRepository,
                           WordRepository wordRepository,
                           WordTranslationRepository translationRepository,
                           SynonymRepository synonymRepository,
                           WordExamCrossRefRepository wordExamRepository) {
        this.languageRepository = languageRepository;
        this.examRepository = examRepository;
        this.wordRepository = wordRepository;
        this.translationRepository = translationRepository;
        this.synonymRepository = synonymRepository;
        this.wordExamRepository = wordExamRepository;
    }

    /**
     * Seeds the words DB unless the fast-path count check shows it is already populated.
     *
     * @return {@code true} if seeding ran, {@code false} if it was skipped as already-seeded.
     */
    @Transactional("secondTransactionManager")
    public boolean seedIfNeeded(SeedManifest manifest) {
        long existingWords = wordRepository.count();
        long existingLanguages = languageRepository.count();
        if (existingWords == manifest.words().size() && existingLanguages == manifest.languages().size()) {
            log.info("Words DB already seeded ({} words, {} languages) — skipping v18 import.",
                    existingWords, existingLanguages);
            return false;
        }

        log.info("Seeding words DB from manifest (manifestVersion={}, dbVersion={}): "
                        + "{} languages, {} exams, {} words, {} translations, {} synonyms, {} word-exam edges.",
                manifest.manifestVersion(), manifest.dbVersion(),
                manifest.languages().size(), manifest.exams().size(), manifest.words().size(),
                manifest.translations().size(), manifest.synonyms().size(), manifest.wordExams().size());

        // Dependency order: languages and exams first, then words (reference languages),
        // then edges that reference words (translations, synonyms, word-exam).
        saveInBatches(languageRepository, manifest.languages().stream()
                .map(l -> new Language(l.id(), l.code(), l.name())).toList());
        saveInBatches(examRepository, manifest.exams().stream()
                .map(Exam::new).toList());
        saveInBatches(wordRepository, manifest.words().stream()
                .map(WordSeedService::toWord).toList());
        saveInBatches(translationRepository, manifest.translations().stream()
                .map(t -> new WordTranslation(t.wordId(), t.translatedWordId(), t.senseIndex(),
                        t.note(), Boolean.TRUE.equals(t.isPrimary()))).toList());
        saveInBatches(synonymRepository, manifest.synonyms().stream()
                .map(s -> new Synonym(s.wordId(), s.synonymWordId())).toList());
        saveInBatches(wordExamRepository, manifest.wordExams().stream()
                .map(x -> new WordExamCrossRef(x.wordId(), x.exam())).toList());

        log.info("Words DB seed complete: {} words, {} translations now present.",
                wordRepository.count(), translationRepository.count());
        return true;
    }

    private static Word toWord(SeedManifest.WordEntry w) {
        Word word = new Word();
        word.setId(w.id());
        word.setLemma(w.lemma());
        word.setLanguageId(w.lang() == null ? null : w.lang().longValue());
        word.setMeaning(w.meaning());
        word.setLevel(w.level());
        word.setNote(w.note());
        return word;
    }

    private static <T> void saveInBatches(JpaRepository<T, ?> repository, List<T> entities) {
        for (int start = 0; start < entities.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, entities.size());
            repository.saveAll(entities.subList(start, end));
        }
        repository.flush();
    }
}
