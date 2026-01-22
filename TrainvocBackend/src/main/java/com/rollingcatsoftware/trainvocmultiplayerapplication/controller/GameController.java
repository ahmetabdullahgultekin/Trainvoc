package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.AnswerRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.mapper.GameMapper;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.ErrorResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.GameRoomResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.PlayerResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.RoomListItemResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.util.ScoreCalculator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@Validated
public class GameController {
    private final GameService gameService;
    private final PlayerRepository playerRepo;
    private final GameMapper gameMapper;

    public GameController(GameService gameService, PlayerRepository playerRepo, GameMapper gameMapper) {
        this.gameService = gameService;
        this.playerRepo = playerRepo;
        this.gameMapper = gameMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<GameRoomResponse> createRoom(
            @RequestParam @NotBlank(message = "Host name is required") @Size(min = 2, max = 30, message = "Host name must be between 2 and 30 characters") String hostName,
            @RequestParam(required = false) String avatarId,
            @RequestParam(defaultValue = "true") boolean hostWantsToJoin,
            @RequestParam(required = false) String hashedPassword,
            @RequestBody @Valid QuizSettings settings
    ) {
        Integer avatarIndex = parseAvatarId(avatarId);
        GameRoom room = gameService.createRoom(hostName, avatarIndex, settings, hostWantsToJoin, hashedPassword);
        return ResponseEntity.ok(gameMapper.toGameRoomResponse(room));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(
            @RequestParam @NotBlank(message = "Room code is required") String roomCode,
            @RequestParam @NotBlank(message = "Player name is required") @Size(min = 2, max = 30, message = "Player name must be between 2 and 30 characters") String playerName,
            @RequestParam(required = false) String avatarId,
            @RequestParam(required = false) String hashedPassword) {
        Integer avatarIndex = parseAvatarId(avatarId);

        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(ErrorResponse.of("Forbidden", "Room password is incorrect.", 403));
        }

        Player player = gameService.joinRoom(roomCode, playerName, avatarIndex);
        if (player == null) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.of("Room not found or player could not be added. Please check the room code and player name.")
            );
        }
        return ResponseEntity.ok(gameMapper.toPlayerResponse(player));
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<GameRoomResponse> getRoom(@PathVariable @NotBlank String roomCode) {
        GameRoom room = gameService.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameMapper.toGameRoomResponse(room));
    }

    @GetMapping("/rooms")
    public List<RoomListItemResponse> getAllRooms() {
        return gameMapper.toRoomListItemResponseList(gameService.getAllRooms());
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerResponse>> getPlayers(@RequestParam @NotBlank String roomCode) {
        List<Player> players = gameService.getPlayersByRoomCode(roomCode);
        if (players == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameMapper.toPlayerResponseList(players));
    }

    @PostMapping("/rooms/{roomCode}/start")
    public ResponseEntity<?> startRoom(
            @PathVariable @NotBlank String roomCode,
            @RequestParam(required = false) String hashedPassword) {
        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(ErrorResponse.of("Forbidden", "Room password is incorrect.", 403));
        }
        boolean started = gameService.startRoom(roomCode);
        if (started) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/rooms/{roomCode}/disband")
    public ResponseEntity<?> disbandRoom(
            @PathVariable @NotBlank String roomCode,
            @RequestParam(required = false) String hashedPassword) {
        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(ErrorResponse.of("Forbidden", "Room password is incorrect.", 403));
        }
        boolean deleted = gameService.disbandRoom(roomCode);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/rooms/{roomCode}/leave")
    public ResponseEntity<?> leaveRoom(
            @PathVariable @NotBlank String roomCode,
            @RequestParam @NotBlank String playerId) {
        boolean removed = gameService.leaveRoom(roomCode, playerId);
        if (removed) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(@RequestBody @Valid AnswerRequest answerRequest) {
        var room = gameService.getRoom(answerRequest.getRoomCode());
        if (room == null) {
            return ResponseEntity.status(404).body(ErrorResponse.of("Not Found", "Room not found.", 404));
        }

        if (room.getCurrentState() != GameState.QUESTION) {
            return ResponseEntity.status(403).body(ErrorResponse.of("Forbidden", "Cannot submit answer at this time. Answers can only be submitted during the question phase.", 403));
        }

        List<Player> players = room.getPlayers();
        Player player = players.stream()
                .filter(p -> p.getId().equals(answerRequest.getPlayerId()))
                .findFirst().orElse(null);
        if (player == null) {
            return ResponseEntity.status(404).body(ErrorResponse.of("Not Found", "Player not found.", 404));
        }

        if (player.getCurrentAnsweredQuestionIndex() != null &&
                player.getCurrentAnsweredQuestionIndex().equals(room.getCurrentQuestionIndex())) {
            return ResponseEntity.status(403).body(ErrorResponse.of("Forbidden", "You have already answered this question.", 403));
        }

        int maxTime = room.getQuestionDuration();
        int calculatedScore = ScoreCalculator.calculate(
                answerRequest.isCorrect(),
                answerRequest.getAnswerTime(),
                answerRequest.getOptionPickRate(),
                maxTime
        );
        player.setScore(player.getScore() + calculatedScore);
        player.setTotalAnswerTime(answerRequest.getAnswerTime());
        player.setCurrentAnsweredQuestionIndex(room.getCurrentQuestionIndex());
        playerRepo.save(player);

        boolean allAnswered = players.stream().allMatch(p ->
                p.getCurrentAnsweredQuestionIndex() != null &&
                        p.getCurrentAnsweredQuestionIndex().equals(room.getCurrentQuestionIndex()));
        if (allAnswered) {
            room.setCurrentState(GameState.ANSWER_REVEAL);
            room.setStateStartTime(LocalDateTime.now());
            gameService.saveRoom(room);
        }

        List<Player> updatedPlayers = playerRepo.findByRoom(room);
        return ResponseEntity.ok(Collections.singletonMap("players", gameMapper.toPlayerResponseList(updatedPlayers)));
    }

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

    @PostMapping("/next")
    public ResponseEntity<?> nextQuestion(
            @RequestParam @NotBlank String roomCode,
            @RequestParam(required = false) String hashedPassword) {
        GameRoom room = gameService.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.status(404).body(ErrorResponse.of("Not Found", "Room not found.", 404));
        }

        if (!gameService.checkRoomPassword(roomCode, hashedPassword)) {
            return ResponseEntity.status(403).body(ErrorResponse.of("Forbidden", "Room password is incorrect.", 403));
        }

        if (room.getCurrentState() != GameState.ANSWER_REVEAL) {
            return ResponseEntity.status(403).body(ErrorResponse.of("Forbidden", "Cannot advance to next question. All players must answer and answers must be revealed.", 403));
        }

        boolean advanced = gameService.goToNextQuestion(room);
        if (!advanced) {
            return ResponseEntity.status(400).body(ErrorResponse.of("Could not advance to next question. The game may have ended."));
        }

        var stateInfo = gameService.getGameState(roomCode, null);
        return ResponseEntity.ok(stateInfo);
    }

    private Integer parseAvatarId(String avatarId) {
        if (avatarId == null) {
            return null;
        }
        try {
            return Integer.valueOf(avatarId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
