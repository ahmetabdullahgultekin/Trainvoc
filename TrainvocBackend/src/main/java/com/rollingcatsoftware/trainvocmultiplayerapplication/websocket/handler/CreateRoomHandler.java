package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Handles room creation requests via WebSocket.
 */
@Component
public class CreateRoomHandler implements WebSocketMessageHandler {

    private final GameService gameService;

    public CreateRoomHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public String getMessageType() {
        return "create";
    }

    @Override
    public void handle(WebSocketSession session, JSONObject message, WebSocketContext context) throws Exception {
        String hostName = message.getString("name");
        Integer avatarId = extractAvatarId(message);
        String hashedPassword = extractHashedPassword(message);
        QuizSettings settings = extractSettings(message);
        boolean hostWantsToJoin = settings.getTotalQuestionCount() > 0; // Use settings if available

        if (message.has("settings")) {
            JSONObject settingsJson = message.getJSONObject("settings");
            hostWantsToJoin = settingsJson.optBoolean("hostWantsToJoin", true);
        }

        GameRoom room = gameService.createRoom(hostName, avatarId, settings, hostWantsToJoin, hashedPassword);

        Player host = null;
        if (hostWantsToJoin && !room.getPlayers().isEmpty()) {
            host = room.getPlayers().getFirst();
            context.registerSession(host.getId(), session);
        }

        JSONObject response = new JSONObject();
        response.put("type", "roomCreated");
        response.put("roomCode", room.getRoomCode());
        response.put("playerId", host != null ? host.getId() : JSONObject.NULL);

        context.sendMessage(session, response);
    }

    private Integer extractAvatarId(JSONObject message) {
        if (message.has("avatarId") && !message.isNull("avatarId")) {
            return message.getInt("avatarId");
        }
        return null;
    }

    private String extractHashedPassword(JSONObject message) {
        if (message.has("hashedPassword") && !message.isNull("hashedPassword")) {
            return message.getString("hashedPassword");
        }
        return null;
    }

    private QuizSettings extractSettings(JSONObject message) {
        JSONObject settingsJson = message.has("settings") ? message.getJSONObject("settings") : new JSONObject();
        QuizSettings settings = new QuizSettings();
        settings.setQuestionDuration(settingsJson.optInt("questionDuration", 60));
        settings.setOptionCount(settingsJson.optInt("optionCount", 4));
        settings.setLevel(settingsJson.optString("level", "A1"));
        settings.setTotalQuestionCount(settingsJson.optInt("totalQuestionCount", 5));
        return settings;
    }
}
