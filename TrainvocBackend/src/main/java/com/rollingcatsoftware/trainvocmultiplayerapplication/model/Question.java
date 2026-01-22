package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "question")
public class Question {
    @Id
    private String id;

    @Column
    private String text;

    @ElementCollection
    private List<String> options;

    @Column
    private int correctIndex;
}
