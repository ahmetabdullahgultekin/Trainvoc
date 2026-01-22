package com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.WordExamCrossRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordExamCrossRefRepository extends JpaRepository<WordExamCrossRef, WordExamCrossRef.PK> {
    List<WordExamCrossRef> findByExam(String exam);

    List<WordExamCrossRef> findByWord(String word);
}

