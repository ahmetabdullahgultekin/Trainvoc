package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler.WebSocketContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service that handles automatic game state transitions.
 * Runs as a scheduled task to check and update game states.
 * Broadcasts state changes to all players via WebSocket.
 */
@Service
@EnableScheduling
public class GameTimerService {

    private static final Logger log = LoggerFactory.getLogger(GameTimerService.class);

    private final GameRoomRepository gameRoomRepository;
    private final RoomService roomService;
    private final PlayerService playerService;
    private final GameStateService gameStateService;
    private final WebSocketContext wsContext;

    public GameTimerService(GameRoomRepository gameRoomRepository, RoomService roomService,
                           PlayerService playerService, GameStateService gameStateService,
                           WebSocketContext wsContext) {
        this.gameRoomRepository = gameRoomRepository;
        this.roomService = roomService;
        this.playerService = playerService;
        this.gameStateService = gameStateService;
        this.wsContext = wsContext;
    }

    /**
     * Check all active games every second for state transitions.
     */
    @Scheduled(fixedRate = 1000)
    public void checkGameStates() {
        List<GameRoom> activeRooms = gameRoomRepository.findAll().stream()
                .filter(room -> room.getStarted() != null && room.getStarted())
                .filter(room -> room.getCurrentState() != GameState.FINAL)
                .filter(room -> room.getCurrentState() != GameState.LOBBY)
                .toList();

        for (GameRoom room : activeRooms) {
            try {
                processRoomState(room);
            } catch (Exception e) {
                log.error("Error processing room state for {}: {}", room.getRoomCode(), e.getMessage());
            }
        }
    }

    private void processRoomState(GameRoom room) throws IOException {
        GameState currentState = room.getCurrentState();
        LocalDateTime stateStart = room.getStateStartTime();
        LocalDateTime now = LocalDateTime.now();

        if (stateStart == null) {
            return;
        }

        long elapsedSeconds = Duration.between(stateStart, now).getSeconds();

        switch (currentState) {
            case COUNTDOWN -> processCountdown(room, elapsedSeconds);
            case QUESTION -> processQuestion(room, elapsedSeconds);
            case ANSWER_REVEAL -> {
                // No automatic transition - wait for host/timer
                // Auto-advance after 5 seconds for smooth gameplay
                if (elapsedSeconds >= 5) {
                    advanceToNextQuestion(room);
                }
            }
            case RANKING -> processRanking(room, elapsedSeconds);
            default -> {
                // No action needed for LOBBY and FINAL
            }
        }
    }

    private void processCountdown(GameRoom room, long elapsedSeconds) throws IOException {
        int remaining = (int) (GameConstants.COUNTDOWN_SECONDS - elapsedSeconds);

        if (remaining <= 0) {
            // Transition to QUESTION
            transitionTo(room, GameState.QUESTION);

            // Broadcast state change
            broadcastStateChange(room, GameState.QUESTION, room.getQuestionDuration());

            // Broadcast current question
            broadcastCurrentQuestion(room);
        } else {
            // Broadcast countdown update
            broadcastStateChange(room, GameState.COUNTDOWN, remaining);
        }
    }

    private void processQuestion(GameRoom room, long elapsedSeconds) throws IOException {
        int remaining = (int) (room.getQuestionDuration() - elapsedSeconds);

        if (remaining <= 0) {
            // Time's up - transition to ANSWER_REVEAL
            transitionTo(room, GameState.ANSWER_REVEAL);

            // Broadcast state change with answer reveal
            broadcastStateChange(room, GameState.ANSWER_REVEAL, 0);

            // Broadcast rankings
            broadcastRankings(room);
        }
    }

    private void processRanking(GameRoom room, long elapsedSeconds) throws IOException {
        int remaining = (int) (GameConstants.RANKING_SECONDS - elapsedSeconds);

        if (remaining <= 0) {
            // Transition to FINAL
            transitionTo(room, GameState.FINAL);

            // Broadcast game ended
            broadcastGameEnded(room);
        }
    }

    private void advanceToNextQuestion(GameRoom room) throws IOException {
        int currentIndex = room.getCurrentQuestionIndex();
        int totalQuestions = room.getTotalQuestionCount();

        if (currentIndex >= totalQuestions - 1) {
            // Last question - go to RANKING
            transitionTo(room, GameState.RANKING);
            broadcastStateChange(room, GameState.RANKING, GameConstants.RANKING_SECONDS);
            broadcastRankings(room);
        } else {
            // Advance to next question
            room.setCurrentQuestionIndex(currentIndex + 1);
            transitionTo(room, GameState.COUNTDOWN);

            // Reset player answers for new question
            playerService.resetAnswersForRoom(room);

            // Broadcast countdown for next question
            broadcastStateChange(room, GameState.COUNTDOWN, GameConstants.COUNTDOWN_SECONDS);
        }
    }

    private void transitionTo(GameRoom room, GameState newState) {
        room.setCurrentState(newState);
        room.setStateStartTime(LocalDateTime.now());
        roomService.save(room);
    }

    private void broadcastStateChange(GameRoom room, GameState state, int remainingTime) throws IOException {
        JSONObject msg = new JSONObject();
        msg.put("type", "gameStateChanged");
        msg.put("state", state.ordinal());
        msg.put("stateName", state.name());
        msg.put("remainingTime", remainingTime);
        msg.put("currentQuestionIndex", room.getCurrentQuestionIndex());
        msg.put("totalQuestionCount", room.getTotalQuestionCount());

        wsContext.broadcastToRoom(room, msg);
    }

    private void broadcastCurrentQuestion(GameRoom room) throws IOException {
        // Note: Questions are sent at game start via StartGameHandler
        // This broadcasts which question index we're on
        JSONObject msg = new JSONObject();
        msg.put("type", "questionIndex");
        msg.put("index", room.getCurrentQuestionIndex());

        wsContext.broadcastToRoom(room, msg);
    }

    private void broadcastRankings(GameRoom room) throws IOException {
        List<Player> players = playerService.getPlayersByRoom(room);
        List<Player> sorted = players.stream()
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .toList();

        JSONArray rankings = new JSONArray();
        int rank = 1;
        for (Player p : sorted) {
            JSONObject pObj = new JSONObject();
            pObj.put("rank", rank++);
            pObj.put("id", p.getId());
            pObj.put("name", p.getName());
            pObj.put("avatarId", p.getAvatarId());
            pObj.put("score", p.getScore());
            pObj.put("correctCount", p.getCorrectCount());
            rankings.put(pObj);
        }

        JSONObject msg = new JSONObject();
        msg.put("type", "rankings");
        msg.put("players", rankings);

        wsContext.broadcastToRoom(room, msg);
    }

    private void broadcastGameEnded(GameRoom room) throws IOException {
        List<Player> players = playerService.getPlayersByRoom(room);
        List<Player> sorted = players.stream()
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .toList();

        JSONArray finalRankings = new JSONArray();
        int rank = 1;
        for (Player p : sorted) {
            JSONObject pObj = new JSONObject();
            pObj.put("rank", rank++);
            pObj.put("id", p.getId());
            pObj.put("name", p.getName());
            pObj.put("avatarId", p.getAvatarId());
            pObj.put("score", p.getScore());
            pObj.put("correctCount", p.getCorrectCount());
            pObj.put("wrongCount", p.getWrongCount());
            pObj.put("totalAnswerTime", p.getTotalAnswerTime());
            finalRankings.put(pObj);
        }

        JSONObject msg = new JSONObject();
        msg.put("type", "gameEnded");
        msg.put("players", finalRankings);

        wsContext.broadcastToRoom(room, msg);
    }
}
