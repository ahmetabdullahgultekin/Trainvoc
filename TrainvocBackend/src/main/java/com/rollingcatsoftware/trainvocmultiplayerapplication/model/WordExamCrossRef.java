package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "word_exam_cross_ref")
@IdClass(WordExamCrossRef.PK.class)
public class WordExamCrossRef {
    @Id
    @Column(name = "word")
    private String word;

    @Id
    @Column(name = "exam")
    private String exam;

    public WordExamCrossRef() {
    }

    public WordExamCrossRef(String word, String exam) {
        this.word = word;
        this.exam = exam;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public static class PK implements Serializable {
        private String word;
        private String exam;

        public PK() {
        }

        public PK(String word, String exam) {
            this.word = word;
            this.exam = exam;
        }

        // equals & hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK pk = (PK) o;
            return word.equals(pk.word) && exam.equals(pk.exam);
        }

        @Override
        public int hashCode() {
            return word.hashCode() + exam.hashCode();
        }
    }
}

