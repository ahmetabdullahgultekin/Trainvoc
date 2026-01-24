package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Handles answer submissions via WebSocket.
 * Calculates score, updates player, and broadcasts results.
 */
@Component
public class AnswerHandler implements WebSocketMessageHandler {

    private final RoomService roomService;
    private final PlayerService playerService;
    private final WebSocketContext context;

    public AnswerHandler(RoomService roomService, PlayerService playerService, WebSocketContext context) {
        this.roomService = roomService;
        this.playerService = playerService;
        this.context = context;
    }

    @Override
    public String getMessageType() {
        return "answer";
    }

    @Override
    public void handle(WebSocketSession session, JSONObject message, WebSocketContext ctx) throws Exception {
        String roomCode = message.getString("roomCode");
        String playerId = message.getString("playerId");
        int answerIndex = message.getInt("answerIndex");
        int answerTime = message.optInt("answerTime", 0); // Time in seconds

        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            sendError(session, ctx, "Room not found.");
            return;
        }

        // Verify game is in QUESTION state
        if (room.getCurrentState() != GameState.QUESTION) {
            sendError(session, ctx, "Cannot submit answer at this time.");
            return;
        }

        // Variables to capture results from synchronized block
        Player player;
        boolean isCorrect;
        int scoreChange;

        // Synchronize on interned player ID to prevent race conditions
        // when the same player submits multiple answers concurrently
        synchronized (playerId.intern()) {
            player = playerService.findById(playerId);
            if (player == null || !player.getRoom().getRoomCode().equals(roomCode)) {
                sendError(session, ctx, "Player not found in this room.");
                return;
            }

            // Check if player already answered this question (atomic with the set below)
            Integer answeredIndex = player.getCurrentAnsweredQuestionIndex();
            int currentQuestion = room.getCurrentQuestionIndex();
            if (answeredIndex != null && answeredIndex == currentQuestion) {
                sendError(session, ctx, "Already answered this question.");
                return;
            }

            // Mark player as having answered - now protected by synchronized block
            player.setCurrentAnsweredQuestionIndex(currentQuestion);

            // Get correctness from message (client knows from questions array)
            isCorrect = message.optBoolean("isCorrect", false);

            // Calculate score
            scoreChange = calculateScore(isCorrect, answerTime, room.getQuestionDuration());

            // Update player stats
            player.setScore(player.getScore() + scoreChange);
            if (isCorrect) {
                player.setCorrectCount(player.getCorrectCount() + 1);
            } else {
                player.setWrongCount(player.getWrongCount() + 1);
            }
            player.setTotalAnswerTime(player.getTotalAnswerTime() + answerTime);

            playerService.save(player);
        }

        // Send answer result to the answering player
        JSONObject result = new JSONObject();
        result.put("type", "answerResult");
        result.put("correct", isCorrect);
        result.put("scoreChange", scoreChange);
        result.put("newScore", player.getScore());
        result.put("answerIndex", answerIndex);

        ctx.sendMessage(session, result);

        // Broadcast player answered notification to all players
        JSONObject playerAnswered = new JSONObject();
        playerAnswered.put("type", "playerAnswered");
        playerAnswered.put("playerId", playerId);
        playerAnswered.put("playerName", player.getName());

        context.broadcastToRoom(room, playerAnswered);

        // Check if all players have answered
        checkAllAnswered(room);
    }

    private int calculateScore(boolean isCorrect, int answerTime, int questionDuration) {
        if (!isCorrect) {
            return GameConstants.MIN_SCORE;
        }

        // Base score for correct answer
        int score = GameConstants.BASE_CORRECT_SCORE;

        // Time bonus - faster answers get more points
        if (questionDuration > 0 && answerTime >= 0) {
            double timeRatio = 1.0 - ((double) answerTime / questionDuration);
            int timeBonus = (int) (timeRatio * GameConstants.TIME_BONUS_MAX);
            score += Math.max(0, timeBonus);
        }

        return score;
    }

    private void checkAllAnswered(GameRoom room) throws Exception {
        List<Player> players = playerService.getPlayersByRoom(room);
        int currentQuestion = room.getCurrentQuestionIndex();

        boolean allAnswered = players.stream()
                .allMatch(p -> {
                    Integer answered = p.getCurrentAnsweredQuestionIndex();
                    return answered != null && answered == currentQuestion;
                });

        if (allAnswered) {
            // All players answered - broadcast rankings update
            broadcastRankings(room, players);
        }
    }

    private void broadcastRankings(GameRoom room, List<Player> players) throws Exception {
        // Sort by score descending
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

        JSONObject rankingsMsg = new JSONObject();
        rankingsMsg.put("type", "rankings");
        rankingsMsg.put("players", rankings);

        context.broadcastToRoom(room, rankingsMsg);
    }

    private void sendError(WebSocketSession session, WebSocketContext ctx, String msg) throws Exception {
        JSONObject error = new JSONObject();
        error.put("type", "error");
        error.put("message", msg);
        ctx.sendMessage(session, error);
    }
}
