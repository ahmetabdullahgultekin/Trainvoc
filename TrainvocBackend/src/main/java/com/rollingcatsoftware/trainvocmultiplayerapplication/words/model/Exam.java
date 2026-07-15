package com.rollingcatsoftware.trainvocmultiplayerapplication.words.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An exam category (mirrors the client's Room {@code exams} table, v18). The seed manifest
 * ships six: TOEFL, IELTS, YDS, YÖKDİL, KPDS, Mixed. Referenced by {@link WordExamCrossRef}.
 */
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @Column(name = "exam")
    private String exam;

    public Exam() {
    }

    public Exam(String exam) {
        this.exam = exam;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }
}
