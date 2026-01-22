package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Handles room join requests via WebSocket.
 */
@Component
public class JoinRoomHandler implements WebSocketMessageHandler {

    private final GameService gameService;

    public JoinRoomHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String getMessageType() {
        return "join";
    }

    @Override
    public void handle(WebSocketSession session, JSONObject message, WebSocketContext context) throws Exception {
        String roomCode = message.getString("roomCode");
        String name = message.getString("name");
        Integer avatarId = extractAvatarId(message);

        Player player = gameService.joinRoom(roomCode, name, avatarId);

        if (player != null) {
            context.registerSession(player.getId(), session);

            JSONObject response = new JSONObject();
            response.put("type", "roomJoined");
            response.put("roomCode", roomCode);
            response.put("playerId", player.getId());
            context.sendMessage(session, response);
        } else {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("type", "error");
            errorResponse.put("message", "Failed to join room. Room may not exist.");
            context.sendMessage(session, errorResponse);
        }
    }

    private Integer extractAvatarId(JSONObject message) {
        if (message.has("avatarId") && !message.isNull("avatarId")) {
            return message.getInt("avatarId");
        }
        return null;
    }
}
