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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles next question requests via WebSocket.
 * Advances the game to the next question or finishes the game.
 */
@Component
public class NextQuestionHandler implements WebSocketMessageHandler {

    private final RoomService roomService;
    private final PlayerService playerService;
    private final WebSocketContext context;

    public NextQuestionHandler(RoomService roomService, PlayerService playerService, WebSocketContext context) {
        this.roomService = roomService;
        this.playerService = playerService;
        this.context = context;
    }

    @Override
    public String getMessageType() {
        return "next";
    }

    @Override
    public void handle(WebSocketSession session, JSONObject message, WebSocketContext ctx) throws Exception {
        String roomCode = message.getString("roomCode");
        String playerId = message.optString("playerId", "");

        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            sendError(session, ctx, "Room not found.");
            return;
        }

        // Verify game is in ANSWER_REVEAL state (ready for next question)
        if (room.getCurrentState() != GameState.ANSWER_REVEAL) {
            sendError(session, ctx, "Cannot advance at this time.");
            return;
        }

        int currentIndex = room.getCurrentQuestionIndex();
        int totalQuestions = room.getTotalQuestionCount();

        if (currentIndex >= totalQuestions - 1) {
            // Last question - transition to RANKING
            room.setCurrentState(GameState.RANKING);
            room.setStateStartTime(LocalDateTime.now());
            roomService.save(room);

            // Broadcast state change
            JSONObject stateMsg = new JSONObject();
            stateMsg.put("type", "gameStateChanged");
            stateMsg.put("state", GameState.RANKING.ordinal());
            stateMsg.put("stateName", GameState.RANKING.name());
            stateMsg.put("remainingTime", GameConstants.RANKING_SECONDS);
            stateMsg.put("currentQuestionIndex", currentIndex);
            stateMsg.put("totalQuestionCount", totalQuestions);

            context.broadcastToRoom(room, stateMsg);

            // Broadcast final rankings
            broadcastRankings(room);
        } else {
            // Advance to next question - start with COUNTDOWN
            room.setCurrentQuestionIndex(currentIndex + 1);
            room.setCurrentState(GameState.COUNTDOWN);
            room.setStateStartTime(LocalDateTime.now());
            roomService.save(room);

            // Reset player answers for new question
            playerService.resetAnswersForRoom(room);

            // Broadcast countdown
            JSONObject stateMsg = new JSONObject();
            stateMsg.put("type", "gameStateChanged");
            stateMsg.put("state", GameState.COUNTDOWN.ordinal());
            stateMsg.put("stateName", GameState.COUNTDOWN.name());
            stateMsg.put("remainingTime", GameConstants.COUNTDOWN_SECONDS);
            stateMsg.put("currentQuestionIndex", currentIndex + 1);
            stateMsg.put("totalQuestionCount", totalQuestions);

            context.broadcastToRoom(room, stateMsg);
        }
    }

    private void broadcastRankings(GameRoom room) throws Exception {
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

        context.broadcastToRoom(room, msg);
    }

    private void sendError(WebSocketSession session, WebSocketContext ctx, String msg) throws Exception {
        JSONObject error = new JSONObject();
        error.put("type", "error");
        error.put("message", msg);
        ctx.sendMessage(session, error);
    }
}
