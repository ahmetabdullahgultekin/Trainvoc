package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for game state calculations and transitions.
 * Handles state machine logic for the game flow.
 */
@Service
public class GameStateService {

    private final RoomService roomService;
    private final PlayerService playerService;

    public GameStateService(RoomService roomService, PlayerService playerService) {
        this.roomService = roomService;
        this.playerService = playerService;
    }

    /**
     * Gets the full game state including players and scores.
     */
    public Map<String, Object> getGameState(String roomCode, String playerId) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            return null;
        }

        // Validate player if provided
        if (playerId != null) {
            Player player = playerService.findById(playerId);
            if (player == null || !player.getRoom().getRoomCode().equals(roomCode)) {
                return null;
            }
        }

        StateCalculationResult stateResult = calculateAndUpdateState(room);

        List<Map<String, Object>> scores = buildPlayerScores(room);

        Map<String, Object> result = new HashMap<>();
        result.put("state", stateResult.state().ordinal());
        result.put("remainingTime", stateResult.remainingTime());
        result.put("players", scores);
        result.put("currentQuestionIndex", room.getCurrentQuestionIndex());
        result.put("totalQuestionCount", room.getTotalQuestionCount());

        return result;
    }

    /**
     * Gets a simplified game state (state name and remaining time only).
     */
    public Map<String, Object> getSimpleState(String roomCode, String playerId) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            return null;
        }

        Player player = playerService.findById(playerId);
        if (player == null || !player.getRoom().getRoomCode().equals(roomCode)) {
            return null;
        }

        StateCalculationResult stateResult = calculateAndUpdateState(room);

        Map<String, Object> result = new HashMap<>();
        result.put("state", stateResult.state().name().toLowerCase());
        result.put("remainingTime", stateResult.remainingTime());

        return result;
    }

    /**
     * Advances the game to the next question or final state.
     * @return true if advanced to next question, false if game ended
     */
    public boolean goToNextQuestion(GameRoom room) {
        if (room == null) {
            return false;
        }

        int currentIndex = room.getCurrentQuestionIndex();
        int totalQuestions = room.getTotalQuestionCount();

        if (currentIndex >= totalQuestions - 1) {
            // Last question - go to ranking
            room.setCurrentState(GameState.RANKING);
            room.setStateStartTime(LocalDateTime.now());
            roomService.save(room);
            return false;
        }

        // Advance to next question
        room.setCurrentQuestionIndex(currentIndex + 1);
        room.setCurrentState(GameState.COUNTDOWN);
        room.setStateStartTime(LocalDateTime.now());
        roomService.save(room);

        // Reset player answers for new question
        playerService.resetAnswersForRoom(room);

        return true;
    }

    /**
     * Calculates current state and remaining time, updating state if needed.
     */
    private StateCalculationResult calculateAndUpdateState(GameRoom room) {
        GameState state = room.getCurrentState();
        LocalDateTime stateStart = room.getStateStartTime();
        LocalDateTime now = LocalDateTime.now();
        int remainingTime = 0;

        switch (state) {
            case COUNTDOWN -> {
                long elapsed = Duration.between(stateStart, now).getSeconds();
                remainingTime = (int) (GameConstants.COUNTDOWN_SECONDS - elapsed);
                if (remainingTime <= 0) {
                    state = transitionTo(room, GameState.QUESTION);
                    remainingTime = room.getQuestionDuration();
                }
            }
            case QUESTION -> {
                long elapsed = Duration.between(stateStart, now).getSeconds();
                remainingTime = (int) (room.getQuestionDuration() - elapsed);
                if (remainingTime <= 0) {
                    state = transitionTo(room, GameState.ANSWER_REVEAL);
                    remainingTime = GameConstants.ANSWER_REVEAL_SECONDS;
                }
            }
            case ANSWER_REVEAL -> {
                // No automatic transition - host triggers next question
                remainingTime = 0;
            }
            case RANKING -> {
                long elapsed = Duration.between(stateStart, now).getSeconds();
                remainingTime = (int) (GameConstants.RANKING_SECONDS - elapsed);
                if (remainingTime <= 0) {
                    state = transitionTo(room, GameState.FINAL);
                    remainingTime = 0;
                }
            }
            case FINAL, LOBBY -> remainingTime = 0;
            default -> remainingTime = 0;
        }

        return new StateCalculationResult(state, remainingTime);
    }

    private GameState transitionTo(GameRoom room, GameState newState) {
        room.setCurrentState(newState);
        room.setStateStartTime(LocalDateTime.now());
        roomService.save(room);
        return newState;
    }

    private List<Map<String, Object>> buildPlayerScores(GameRoom room) {
        List<Map<String, Object>> scores = new ArrayList<>();
        for (Player player : playerService.getPlayersByRoom(room)) {
            Map<String, Object> score = new HashMap<>();
            score.put("playerId", player.getId());
            score.put("name", player.getName());
            score.put("score", player.getScore());
            scores.add(score);
        }
        return scores;
    }

    /**
     * Result of state calculation.
     */
    private record StateCalculationResult(GameState state, int remainingTime) {
    }
}
