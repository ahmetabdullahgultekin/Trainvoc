package com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, String> {
    @Query(value = "SELECT * FROM words WHERE level = :level ORDER BY random() LIMIT :count", nativeQuery = true)
    List<Word> findRandomWordsByLevel(@Param("level") String level, @Param("count") int count);

    List<Word> findByLevel(String level);

    // Sınava göre kelimeleri getir (WordExamCrossRef ile join)
    @Query(value = "SELECT w.* FROM words w JOIN word_exam_cross_ref x ON w.word = x.word WHERE x.exam = :exam", nativeQuery = true)
    List<Word> findByExam(@Param("exam") String exam);
}
