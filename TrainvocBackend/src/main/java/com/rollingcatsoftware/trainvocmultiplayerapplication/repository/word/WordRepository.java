package com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word;

import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    /**
     * Random English words at a CEFR level, for quiz generation.
     * <p>
     * Restricted to {@code language_id = 1} (English): CEFR levels only exist on English
     * rows in the v18 schema, and the quiz prompt is the English lemma with the Turkish
     * {@code meaning} as the answer. {@code random()} is portable across PostgreSQL (prod)
     * and H2 (tests). ORDER BY random() is a full scan, acceptable at this dataset size
     * (~5.5k English rows).
     */
    @Query(value = "SELECT * FROM words WHERE level = :level AND language_id = 1 ORDER BY random() LIMIT :count",
            nativeQuery = true)
    List<Word> findRandomWordsByLevel(@Param("level") String level, @Param("count") int count);

    List<Word> findByLevel(String level);

    /**
     * Words attached to an exam, via the id-keyed cross-reference table.
     * Joins {@code word_exam_cross_ref.word_id} to {@code words.id} (was a String
     * {@code word} join before the v18 re-key).
     */
    @Query(value = "SELECT w.* FROM words w JOIN word_exam_cross_ref x ON x.word_id = w.id WHERE x.exam = :exam",
            nativeQuery = true)
    List<Word> findByExam(@Param("exam") String exam);
}
