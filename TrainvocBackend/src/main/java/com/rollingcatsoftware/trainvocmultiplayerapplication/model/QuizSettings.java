package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import lombok.Data;

@Data
public class QuizSettings {
    private int questionDuration = 60; // seconds
    private int optionCount = 4; // 2 to 4
    private String level = "A1"; // A1, B1, C1, C2, Mixed
    private int totalQuestionCount = 5; // how many questions to ask
}