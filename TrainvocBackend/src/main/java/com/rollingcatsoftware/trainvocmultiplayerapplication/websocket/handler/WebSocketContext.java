package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context shared among WebSocket message handlers.
 * Provides access to session registry and utility methods.
 * Singleton Spring component for broadcasting to connected clients.
 */
@Component
public class WebSocketContext {

    private final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToPlayer = new ConcurrentHashMap<>();

    /**
     * Registers a player session with O(1) bidirectional lookup.
     */
    public void registerSession(String playerId, WebSocketSession session) {
        playerSessions.put(playerId, session);
        sessionToPlayer.put(session.getId(), playerId);
    }

    /**
     * Gets a player's WebSocket session.
     */
    public WebSocketSession getSession(String playerId) {
        return playerSessions.get(playerId);
    }

    /**
     * Removes a player session by player ID.
     */
    public void removeSession(String playerId) {
        WebSocketSession session = playerSessions.remove(playerId);
        if (session != null) {
            sessionToPlayer.remove(session.getId());
        }
    }

    /**
     * Removes a session by WebSocketSession reference.
     * O(1) lookup using reverse mapping.
     * Used for cleanup when connection is closed.
     * @return the playerId that was associated with the session, or null if not found
     */
    public String removeBySession(WebSocketSession session) {
        String playerId = sessionToPlayer.remove(session.getId());
        if (playerId != null) {
            playerSessions.remove(playerId);
        }
        return playerId;
    }

    /**
     * Finds player ID by WebSocketSession.
     * O(1) lookup using reverse mapping.
     */
    public String findPlayerIdBySession(WebSocketSession session) {
        return sessionToPlayer.get(session.getId());
    }

    /**
     * Sends a message to a specific player.
     */
    public void sendToPlayer(String playerId, JSONObject message) throws IOException {
        WebSocketSession session = playerSessions.get(playerId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message.toString()));
        }
    }

    /**
     * Broadcasts a message to all players in a room.
     */
    public void broadcastToRoom(GameRoom room, JSONObject message) throws IOException {
        for (Player player : room.getPlayers()) {
            sendToPlayer(player.getId(), message);
        }
    }

    /**
     * Sends a message to a WebSocket session.
     */
    public void sendMessage(WebSocketSession session, JSONObject message) throws IOException {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message.toString()));
        }
    }
}
