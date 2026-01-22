package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context shared among WebSocket message handlers.
 * Provides access to session registry and utility methods.
 */
public class WebSocketContext {

    private final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();

    /**
     * Registers a player session.
     */
    public void registerSession(String playerId, WebSocketSession session) {
        playerSessions.put(playerId, session);
    }

    /**
     * Gets a player's WebSocket session.
     */
    public WebSocketSession getSession(String playerId) {
        return playerSessions.get(playerId);
    }

    /**
     * Removes a player session.
     */
    public void removeSession(String playerId) {
        playerSessions.remove(playerId);
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
