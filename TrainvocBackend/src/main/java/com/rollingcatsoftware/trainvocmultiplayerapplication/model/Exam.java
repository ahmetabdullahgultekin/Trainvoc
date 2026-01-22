package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

