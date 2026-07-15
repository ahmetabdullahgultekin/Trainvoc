package com.rollingcatsoftware.trainvocmultiplayerapplication.words.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

/**
 * Association between a {@link Word} and an {@link Exam} it belongs to (mirrors the
 * client's Room {@code word_exam_cross_ref} table, v18). Re-keyed from the old String
 * {@code (word, exam)} to {@code (word_id, exam)} — {@code word_id} is a logical FK to
 * {@link Word#getId()}. In the seed manifest every edge targets the {@code YDS} exam.
 */
@Entity
@Table(name = "word_exam_cross_ref",
        indexes = @Index(name = "idx_word_exam_cross_ref_exam", columnList = "exam"))
@IdClass(WordExamCrossRef.PK.class)
public class WordExamCrossRef {

    @Id
    @Column(name = "word_id")
    private Long wordId;

    @Id
    @Column(name = "exam")
    private String exam;

    public WordExamCrossRef() {
    }

    public WordExamCrossRef(Long wordId, String exam) {
        this.wordId = wordId;
        this.exam = exam;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    /** Composite PK {@code (wordId, exam)} with null-safe equals/hashCode. */
    public static class PK implements Serializable {
        private Long wordId;
        private String exam;

        public PK() {
        }

        public PK(Long wordId, String exam) {
            this.wordId = wordId;
            this.exam = exam;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK pk = (PK) o;
            return Objects.equals(wordId, pk.wordId) && Objects.equals(exam, pk.exam);
        }

        @Override
        public int hashCode() {
            return Objects.hash(wordId, exam);
        }
    }
}
