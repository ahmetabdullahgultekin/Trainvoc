package com.rollingcatsoftware.trainvocmultiplayerapplication.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerRequest {
    @NotBlank(message = "Room code is required")
    @Size(min = 4, max = 10, message = "Room code must be between 4 and 10 characters")
    private String roomCode;

    @NotBlank(message = "Player ID is required")
    private String playerId;

    @Size(max = 500, message = "Answer must not exceed 500 characters")
    private String answer;

    @Min(value = 0, message = "Answer time cannot be negative")
    @Max(value = 300, message = "Answer time cannot exceed 300 seconds")
    private int answerTime;

    private int score;
    private boolean isCorrect;

    @Min(value = 0, message = "Option pick rate must be between 0 and 1")
    @Max(value = 1, message = "Option pick rate must be between 0 and 1")
    private double optionPickRate;

    public AnswerRequest() {
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
