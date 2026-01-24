package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Handles room join requests via WebSocket.
 * Broadcasts player join to existing players in the room.
 */
@Component
public class JoinRoomHandler implements WebSocketMessageHandler {

    private final GameService gameService;
    private final RoomService roomService;
    private final PlayerService playerService;
    private final WebSocketContext wsContext;

    public JoinRoomHandler(GameService gameService, RoomService roomService,
                          PlayerService playerService, WebSocketContext wsContext) {
        this.gameService = gameService;
        this.roomService = roomService;
        this.playerService = playerService;
        this.wsContext = wsContext;
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

        // Get room before joining to know existing players
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            sendError(session, context, "Room not found.");
            return;
        }

        Player player = gameService.joinRoom(roomCode, name, avatarId);

        if (player != null) {
            context.registerSession(player.getId(), session);

            // Send confirmation to the joining player
            JSONObject response = new JSONObject();
            response.put("type", "roomJoined");
            response.put("roomCode", roomCode);
            response.put("playerId", player.getId());
            context.sendMessage(session, response);

            // Broadcast playerJoined to all players in room (including the new one)
            JSONObject playerJoined = new JSONObject();
            playerJoined.put("type", "playerJoined");
            playerJoined.put("playerId", player.getId());
            playerJoined.put("playerName", player.getName());
            playerJoined.put("avatarId", player.getAvatarId());

            wsContext.broadcastToRoom(room, playerJoined);

            // Also send full players list update
            List<Player> allPlayers = playerService.getPlayersByRoom(room);
            JSONObject playersUpdate = new JSONObject();
            playersUpdate.put("type", "playersUpdate");
            playersUpdate.put("players", buildPlayersArray(allPlayers));

            wsContext.broadcastToRoom(room, playersUpdate);
        } else {
            sendError(session, context, "Failed to join room. Room may not exist or game already started.");
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

    private Integer extractAvatarId(JSONObject message) {
        if (message.has("avatarId") && !message.isNull("avatarId")) {
            return message.getInt("avatarId");
        }
        return null;
    }

    private void sendError(WebSocketSession session, WebSocketContext context, String msg) throws Exception {
        JSONObject error = new JSONObject();
        error.put("type", "error");
        error.put("message", msg);
        context.sendMessage(session, error);
    }
}
