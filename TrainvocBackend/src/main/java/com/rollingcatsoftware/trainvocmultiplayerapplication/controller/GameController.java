package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.mapper.GameMapper;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.ErrorResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.GameRoomResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.PlayerResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.RoomListItemResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for game room queries.
 *
 * NOTE: All game operations (create, join, leave, start, answer, next)
 * should be performed via WebSocket at /ws/game for real-time multiplayer support.
 *
 * This controller provides read-only endpoints for:
 * - Room discovery (listing available rooms)
 * - Room state queries
 * - Player information
 */
@RestController
@RequestMapping("/api/game")
@Validated
public class GameController {
    private final GameService gameService;
    private final GameMapper gameMapper;

    public GameController(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    /**
     * Get room details by room code.
     */
    @GetMapping("/{roomCode}")
    public ResponseEntity<GameRoomResponse> getRoom(@PathVariable @NotBlank String roomCode) {
        GameRoom room = gameService.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameMapper.toGameRoomResponse(room));
    }

    /**
     * List all available rooms for discovery.
     */
    @GetMapping("/rooms")
    public List<RoomListItemResponse> getAllRooms() {
        return gameMapper.toRoomListItemResponseList(gameService.getAllRooms());
    }

    /**
     * Get players in a specific room.
     */
    @GetMapping("/players")
    public ResponseEntity<List<PlayerResponse>> getPlayers(@RequestParam @NotBlank String roomCode) {
        List<Player> players = gameService.getPlayersByRoomCode(roomCode);
        if (players == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameMapper.toPlayerResponseList(players));
    }

    /**
     * Get detailed game state for a player.
     */
    @GetMapping("/state")
    public ResponseEntity<?> getGameState(
            @RequestParam @NotBlank String roomCode,
            @RequestParam String playerId) {
        var stateInfo = gameService.getGameState(roomCode, playerId);
        if (stateInfo == null) {
            return ResponseEntity.status(404).body(ErrorResponse.of("Not Found", "Room or player not found.", 404));
        }
        return ResponseEntity.ok(stateInfo);
    }

    /**
     * Get simplified game state for polling.
     */
    @GetMapping("/state-simple")
    public ResponseEntity<?> getSimpleState(
            @RequestParam @NotBlank String roomCode,
            @RequestParam String playerId) {
        var stateInfo = gameService.getSimpleState(roomCode, playerId);
        if (stateInfo == null) {
            return ResponseEntity.status(404).body(ErrorResponse.of("Not Found", "Room or player not found.", 404));
        }
        return ResponseEntity.ok(stateInfo);
    }
}
