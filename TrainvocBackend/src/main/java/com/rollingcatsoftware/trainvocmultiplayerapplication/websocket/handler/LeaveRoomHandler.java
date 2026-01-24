package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Handles player leave requests via WebSocket.
 * Broadcasts player departure to remaining players.
 * Disbands room if no players remain.
 */
@Component
public class LeaveRoomHandler implements WebSocketMessageHandler {

    private final RoomService roomService;
    private final PlayerService playerService;
    private final WebSocketContext context;

    public LeaveRoomHandler(RoomService roomService, PlayerService playerService, WebSocketContext context) {
        this.roomService = roomService;
        this.playerService = playerService;
        this.context = context;
    }

    @Override
    public String getMessageType() {
        return "leave";
    }

    @Override
    public void handle(WebSocketSession session, JSONObject message, WebSocketContext ctx) throws Exception {
        String roomCode = message.getString("roomCode");
        String playerId = message.getString("playerId");

        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            sendError(session, ctx, "Room not found.");
            return;
        }

        // Get player before removing
        Player leavingPlayer = playerService.findById(playerId);
        if (leavingPlayer == null) {
            sendError(session, ctx, "Player not found.");
            return;
        }

        String leavingPlayerName = leavingPlayer.getName();

        // Remove player from room
        boolean removed = playerService.leaveRoom(roomCode, playerId);
        if (!removed) {
            sendError(session, ctx, "Failed to leave room.");
            return;
        }

        // Unregister session
        ctx.removeSession(playerId);

        // Confirm leave to the leaving player
        JSONObject leaveConfirm = new JSONObject();
        leaveConfirm.put("type", "leftRoom");
        leaveConfirm.put("roomCode", roomCode);
        ctx.sendMessage(session, leaveConfirm);

        // Check remaining players
        room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            return; // Room was already deleted
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
        playerLeft.put("playerName", leavingPlayerName);

        context.broadcastToRoom(room, playerLeft);

        // Also send updated player list
        JSONObject playersUpdate = new JSONObject();
        playersUpdate.put("type", "playersUpdate");
        playersUpdate.put("players", buildPlayersArray(remainingPlayers));

        context.broadcastToRoom(room, playersUpdate);
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

    private void sendError(WebSocketSession session, WebSocketContext ctx, String msg) throws Exception {
        JSONObject error = new JSONObject();
        error.put("type", "error");
        error.put("message", msg);
        ctx.sendMessage(session, error);
    }
}
