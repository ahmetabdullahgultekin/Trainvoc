package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler.MessageDispatcher;
import com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler.WebSocketContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

/**
 * Main WebSocket handler that delegates message processing to the MessageDispatcher.
 * Follows the Single Responsibility Principle by focusing only on WebSocket lifecycle.
 * Handles session cleanup and player disconnect notifications.
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(GameWebSocketHandler.class);

    private final MessageDispatcher messageDispatcher;
    private final WebSocketContext wsContext;
    private final PlayerService playerService;
    private final RoomService roomService;

    public GameWebSocketHandler(MessageDispatcher messageDispatcher, WebSocketContext wsContext,
                                PlayerService playerService, RoomService roomService) {
        this.messageDispatcher = messageDispatcher;
        this.wsContext = wsContext;
        this.playerService = playerService;
        this.roomService = roomService;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        messageDispatcher.dispatch(session, message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        log.info("WebSocket connection established: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        log.info("WebSocket connection closed: {} with status {}", session.getId(), status);

        // Find and remove player session
        String playerId = wsContext.removeBySession(session);
        if (playerId != null) {
            handlePlayerDisconnect(playerId);
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        log.error("WebSocket transport error for session {}: {}", session.getId(), exception.getMessage());

        // Clean up on error
        String playerId = wsContext.removeBySession(session);
        if (playerId != null) {
            handlePlayerDisconnect(playerId);
        }
    }

    private void handlePlayerDisconnect(String playerId) {
        try {
            Player player = playerService.findById(playerId);
            if (player == null || player.getRoom() == null) {
                return;
            }

            String roomCode = player.getRoom().getRoomCode();
            String playerName = player.getName();

            // Remove player from room
            playerService.leaveRoom(roomCode, playerId);

            // Get remaining players
            GameRoom room = roomService.findByRoomCode(roomCode);
            if (room == null) {
                return; // Room was deleted
            }

            List<Player> remainingPlayers = playerService.getPlayersByRoom(room);

            if (remainingPlayers.isEmpty()) {
                // No players left, disband room
                roomService.disbandRoom(roomCode);
                return;
            }

            // Broadcast player left to remaining players
            JSONObject playerLeft = new JSONObject();
            playerLeft.put("type", "playerLeft");
            playerLeft.put("playerId", playerId);
            playerLeft.put("playerName", playerName);
            playerLeft.put("reason", "disconnected");

            wsContext.broadcastToRoom(room, playerLeft);

            // Also send updated player list
            JSONObject playersUpdate = new JSONObject();
            playersUpdate.put("type", "playersUpdate");
            playersUpdate.put("players", buildPlayersArray(remainingPlayers));

            wsContext.broadcastToRoom(room, playersUpdate);

        } catch (Exception e) {
            log.error("Error handling player disconnect for {}: {}", playerId, e.getMessage());
        }
    }

    private JSONArray buildPlayersArray(List<Player> players) {
        JSONArray arr = new JSONArray();
        for (Player p : players) {
            JSONObject pObj = new JSONObject();
            pObj.put("id", p.getId());
            pObj.put("name", p.getName());
            pObj.put("avatarId", p.getAvatarId());
            pObj.put("score", p.getScore());
            arr.put(pObj);
        }
        return arr;
    }
}
