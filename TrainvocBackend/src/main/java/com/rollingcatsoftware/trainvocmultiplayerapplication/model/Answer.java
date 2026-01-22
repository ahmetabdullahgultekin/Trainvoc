package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Answer {
    private String playerId;
    private String roomCode;
    private String questionId;
    private int selectedOptionIndex;
    private long timestamp; // for speed-based scoring
}
