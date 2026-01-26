package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.AnswerRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.mapper.GameMapper;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.ErrorResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.GameRoomResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.PlayerResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.RoomListItemResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import jakarta.validation.Valid;
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
    private final PlayerService playerService;
    private final RoomService roomService;

    public GameController(GameService gameService, GameMapper gameMapper,
                          PlayerService playerService, RoomService roomService) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
        this.playerService = playerService;
        this.roomService = roomService;
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

    // ============ Write Operations (REST alternatives to WebSocket) ============

    /**
     * Leave a room.
     */
    @PostMapping("/rooms/{roomCode}/leave")
    public ResponseEntity<?> leaveRoom(
            @PathVariable @NotBlank String roomCode,
            @RequestParam @NotBlank String playerId) {
        boolean success = gameService.leaveRoom(roomCode, playerId);
        if (!success) {
            return ResponseEntity.status(404)
                    .body(ErrorResponse.of("Not Found", "Room or player not found.", 404));
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Disband a room (host only).
     */
    @PostMapping("/rooms/{roomCode}/disband")
    public ResponseEntity<?> disbandRoom(@PathVariable @NotBlank String roomCode) {
        boolean success = gameService.disbandRoom(roomCode);
        if (!success) {
            return ResponseEntity.status(404)
                    .body(ErrorResponse.of("Not Found", "Room not found.", 404));
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Start a game (host only).
     */
    @PostMapping("/rooms/{roomCode}/start")
    public ResponseEntity<?> startGame(@PathVariable @NotBlank String roomCode) {
        boolean success = gameService.startRoom(roomCode);
        if (!success) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of("Bad Request", "Cannot start game. Room may not exist or is not in LOBBY state.", 400));
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Advance to next question (host only).
     */
    @PostMapping("/next")
    public ResponseEntity<?> nextQuestion(@RequestParam @NotBlank String roomCode) {
        GameRoom room = gameService.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.status(404)
                    .body(ErrorResponse.of("Not Found", "Room not found.", 404));
        }

        boolean success = gameService.goToNextQuestion(room);
        if (!success) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of("Bad Request", "Cannot advance to next question.", 400));
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Submit an answer.
     */
    @PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(@Valid @RequestBody AnswerRequest request) {
        GameRoom room = roomService.findByRoomCode(request.getRoomCode());
        if (room == null) {
            return ResponseEntity.status(404)
                    .body(new AnswerResponse(false, "Room not found.", null, null));
        }

        // Verify game is in QUESTION state
        if (room.getCurrentState() != GameState.QUESTION) {
            return ResponseEntity.badRequest()
                    .body(new AnswerResponse(false, "Cannot submit answer at this time.", null, null));
        }

        Player player = playerService.findById(request.getPlayerId());
        if (player == null || !player.getRoom().getRoomCode().equals(request.getRoomCode())) {
            return ResponseEntity.status(404)
                    .body(new AnswerResponse(false, "Player not found in this room.", null, null));
        }

        // Check if player already answered this question
        Integer answeredIndex = player.getCurrentAnsweredQuestionIndex();
        int currentQuestion = room.getCurrentQuestionIndex();
        if (answeredIndex != null && answeredIndex == currentQuestion) {
            return ResponseEntity.badRequest()
                    .body(new AnswerResponse(false, "Already answered this question.", player.getScore(), null));
        }

        // Mark player as having answered
        player.setCurrentAnsweredQuestionIndex(currentQuestion);

        // Calculate score
        boolean isCorrect = request.isCorrect();
        int scoreChange = calculateScore(isCorrect, request.getAnswerTime(), room.getQuestionDuration());

        // Update player stats
        player.setScore(player.getScore() + scoreChange);
        if (isCorrect) {
            player.setCorrectCount(player.getCorrectCount() + 1);
        } else {
            player.setWrongCount(player.getWrongCount() + 1);
        }
        player.setTotalAnswerTime(player.getTotalAnswerTime() + request.getAnswerTime());

        playerService.save(player);

        return ResponseEntity.ok(new AnswerResponse(true, "Answer submitted.", player.getScore(), isCorrect));
    }

    private int calculateScore(boolean isCorrect, int answerTime, int questionDuration) {
        if (!isCorrect) {
            return GameConstants.MIN_SCORE;
        }

        int score = GameConstants.BASE_CORRECT_SCORE;

        if (questionDuration > 0 && answerTime >= 0) {
            double timeRatio = 1.0 - ((double) answerTime / questionDuration);
            int timeBonus = (int) (timeRatio * GameConstants.TIME_BONUS_MAX);
            score += Math.max(0, timeBonus);
        }

        return score;
    }

    // Response record for answer submissions
    private record AnswerResponse(boolean success, String message, Integer score, Boolean correct) {}
}
