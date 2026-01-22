package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for detailed game room information in API responses.
 * Includes full room details with player list.
 */
@Data
@Builder
public class GameRoomResponse {
    private String roomCode;
    private List<PlayerResponse> players;
    private int currentQuestionIndex;
    private Boolean started;
    private String hostId;
    private int questionDuration;
    private int optionCount;
    private String level;
    private int totalQuestionCount;
    private LocalDateTime lastUsed;
    private boolean hasPassword;
    private GameState currentState;
    private LocalDateTime stateStartTime;
}
