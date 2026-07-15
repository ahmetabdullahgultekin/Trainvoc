package com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word;

import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Synonym;
import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.SynonymId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SynonymRepository extends JpaRepository<Synonym, SynonymId> {
    List<Synonym> findByWordId(Long wordId);
}
