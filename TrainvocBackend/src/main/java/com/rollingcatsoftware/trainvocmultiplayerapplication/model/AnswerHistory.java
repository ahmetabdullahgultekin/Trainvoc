package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerHistory {
    private String questionId;
    private String questionText;
    private int selectedOption;
    private int correctOption;
    private boolean correct;
    private long timestamp;
}