package com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word;

import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.WordTranslation;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.WordTranslationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordTranslationRepository extends JpaRepository<WordTranslation, WordTranslationId> {
    List<WordTranslation> findByWordId(Long wordId);
}
