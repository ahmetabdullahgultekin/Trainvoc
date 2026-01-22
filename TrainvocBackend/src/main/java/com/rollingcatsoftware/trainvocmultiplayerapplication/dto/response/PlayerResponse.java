package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for player information in API responses.
 * Hides internal entity structure and sensitive data.
 */
@Data
@Builder
public class PlayerResponse {
    private String id;
    private String name;
    private int score;
    private int correctCount;
    private int wrongCount;
    private long totalAnswerTime;
    private Integer avatarId;
    private Integer currentAnsweredQuestionIndex;
}
