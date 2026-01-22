package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for room list items (simplified view without full player details).
 * Used for room listing endpoints to reduce payload size.
 */
@Data
@Builder
public class RoomListItemResponse {
    private String roomCode;
    private int playerCount;
    private Boolean started;
    private String hostId;
    private int questionDuration;
    private String level;
    private int totalQuestionCount;
    private boolean hasPassword;
    private GameState currentState;
}
