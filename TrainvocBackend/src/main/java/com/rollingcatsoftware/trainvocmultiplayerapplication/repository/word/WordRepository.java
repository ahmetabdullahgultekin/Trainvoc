package com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, String> {
    /**
     * Get random words by level for quiz generation.
     * Note: ORDER BY random() performs a full table scan. This is acceptable for
     * the vocabulary dataset size (~5000 words). For larger datasets, consider
     * using TABLESAMPLE or application-level random selection.
     */
    @Query(value = "SELECT * FROM words WHERE level = :level ORDER BY random() LIMIT :count", nativeQuery = true)
    List<Word> findRandomWordsByLevel(@Param("level") String level, @Param("count") int count);

    List<Word> findByLevel(String level);

    // Get words by exam (join with WordExamCrossRef)
    @Query(value = "SELECT w.* FROM words w JOIN word_exam_cross_ref x ON w.word = x.word WHERE x.exam = :exam", nativeQuery = true)
    List<Word> findByExam(@Param("exam") String exam);
}
