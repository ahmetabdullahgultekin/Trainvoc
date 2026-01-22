package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.AnswerRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;
    private final PlayerRepository playerRepo;

    public GameController(GameService gameService, PlayerRepository playerRepo) {
        this.gameService = gameService;
        this.playerRepo = playerRepo;
    }

    // Oda oluştur
    @PostMapping("/create")
    public ResponseEntity<GameRoom> createRoom(
            @RequestParam String hostName,
            @RequestParam(required = false) String avatarId,
            @RequestParam(defaultValue = "true") boolean hostWantsToJoin,
            @RequestParam(required = false) String hashedPassword,
            @RequestBody QuizSettings settings
    ) {
        Integer avatarIndex = null;
        if (avatarId != null) {
            try {
                avatarIndex = Integer.valueOf(avatarId);
            } catch (NumberFormatException _) {
            }
        }
        GameRoom room = gameService.createRoom(hostName, avatarIndex, settings, hostWantsToJoin, hashedPassword);
        return ResponseEntity.ok(room);
    }

    // Join a room (şifre kontrolü eklendi)
    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestParam String roomCode, @RequestParam String playerName, @RequestParam(required = false) String avatarId, @RequestParam(required = false) String hashedPassword) {
        Integer avatarIndex = null;
        if (avatarId != null) {
            try {
                avatarIndex = Integer.valueOf(avatarId);
            } catch (NumberFormatException _) {
            }
        }
        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Room password is incorrect."));
        }
        Player player = gameService.joinRoom(roomCode, playerName, avatarIndex);
        if (player == null) {
            // Return a more descriptive error message
            return ResponseEntity.badRequest().body(
                    java.util.Collections.singletonMap("error", "Room not found or player could not be added. Please check the room code and player name.")
            );
        }
        return ResponseEntity.ok(player);
    }

    // Oda detayları
    @GetMapping("/{roomCode}")
    public ResponseEntity<GameRoom> getRoom(@PathVariable String roomCode) {
        GameRoom room = gameService.getRoom(roomCode);
        if (room == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(room);
    }

    // List all rooms
    @GetMapping("/rooms")
    public List<GameRoom> getAllRooms() {
        return gameService.getAllRooms();
    }

    // Oda oyuncularını getir
    @GetMapping("/players")
    public ResponseEntity<List<Player>> getPlayers(@RequestParam String roomCode) {
        List<Player> players = gameService.getPlayersByRoomCode(roomCode);
        if (players == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(players);
    }

    // Odayı başlat (şifre kontrolü eklendi)
    @PostMapping("/rooms/{roomCode}/start")
    public ResponseEntity<?> startRoom(@PathVariable String roomCode, @RequestParam(required = false) String hashedPassword) {
        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Room password is incorrect."));
        }
        boolean started = gameService.startRoom(roomCode);
        if (started) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Odayı dağıt (şifre kontrolü eklendi)
    @PostMapping("/rooms/{roomCode}/disband")
    public ResponseEntity<?> disbandRoom(@PathVariable String roomCode, @RequestParam(required = false) String hashedPassword) {
        boolean passwordOk = gameService.checkRoomPassword(roomCode, hashedPassword);
        if (!passwordOk) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Room password is incorrect."));
        }
        boolean deleted = gameService.disbandRoom(roomCode);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Oyuncu lobiden ayrılır
    @PostMapping("/rooms/{roomCode}/leave")
    public ResponseEntity<?> leaveRoom(@PathVariable String roomCode, @RequestParam String playerId) {
        boolean removed = gameService.leaveRoom(roomCode, playerId);
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(@RequestBody AnswerRequest answerRequest) {
        var room = gameService.getRoom(answerRequest.getRoomCode());
        if (room == null) {
            return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "Oda bulunamadı."));
        }
        // Sadece QUESTION state'inde cevap kabul et
        if (room.getCurrentState() != com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState.QUESTION) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Şu anda cevap gönderilemez. Sadece soru aşamasında cevap verilebilir."));
        }
        List<Player> players = room.getPlayers();
        Player player = players.stream()
                .filter(p -> p.getId().equals(answerRequest.getPlayerId()))
                .findFirst().orElse(null);
        if (player == null) {
            return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "Oyuncu bulunamadı."));
        }
        // Oyuncu bu soruya zaten cevap verdiyse tekrar cevap vermesini engelle
        if (player.getCurrentAnsweredQuestionIndex() != null && player.getCurrentAnsweredQuestionIndex() == room.getCurrentQuestionIndex()) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Bu soruya zaten cevap verdiniz."));
        }
        // Skoru backend'de hesapla (oda ayarlarından maxTime alınır)
        int maxTime = room.getQuestionDuration();
        int calculatedScore = AnswerRequest.calculateScore(
                answerRequest.isCorrect(),
                answerRequest.getAnswerTime(),
                answerRequest.getOptionPickRate(),
                maxTime
        );
        player.setScore(player.getScore() + calculatedScore);
        player.setTotalAnswerTime(answerRequest.getAnswerTime());
        player.setCurrentAnsweredQuestionIndex(room.getCurrentQuestionIndex()); // Cevap verdi olarak işaretle
        playerRepo.save(player);
        // Tüm oyuncular cevapladıysa state'i ANSWER_REVEAL yap
        boolean allAnswered = players.stream().allMatch(p -> p.getCurrentAnsweredQuestionIndex() != null && p.getCurrentAnsweredQuestionIndex() == room.getCurrentQuestionIndex());
        if (allAnswered) {
            room.setCurrentState(com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState.ANSWER_REVEAL);
            room.setStateStartTime(java.time.LocalDateTime.now());
            gameService.saveRoom(room);
        }
        // Güncel oyuncu listesini veritabanından çekerek dön
        List<Player> updatedPlayers = playerRepo.findByRoom(room);
        return ResponseEntity.ok(java.util.Collections.singletonMap("players", updatedPlayers));
    }

    // Oyun state ve kalan süre endpointi
    @GetMapping("/state")
    public ResponseEntity<?> getGameState(@RequestParam String roomCode, @RequestParam String playerId) {
        var stateInfo = gameService.getGameState(roomCode, playerId);
        if (stateInfo == null) {
            return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "Oda veya oyuncu bulunamadı."));
        }
        return ResponseEntity.ok(stateInfo);
    }

    // Sadece state ve süre döndüren sade endpoint
    @GetMapping("/state-simple")
    public ResponseEntity<?> getSimpleState(@RequestParam String roomCode, @RequestParam String playerId) {
        var stateInfo = gameService.getSimpleState(roomCode, playerId);
        if (stateInfo == null) {
            return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "Oda veya oyuncu bulunamadı."));
        }
        return ResponseEntity.ok(stateInfo);
    }

    // Sonraki soruya geçiş endpointi
    @PostMapping("/next")
    public ResponseEntity<?> nextQuestion(@RequestParam String roomCode, @RequestParam(required = false) String hashedPassword) {
        GameRoom room = gameService.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "Oda bulunamadı."));
        }
        // Eğer şifreli oda ise kontrol et
        if (!gameService.checkRoomPassword(roomCode, hashedPassword)) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Room password is incorrect."));
        }
        // Sadece ANSWER_REVEAL veya benzeri state'te sonraki soruya geçilebilir
        if (room.getCurrentState() != com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState.ANSWER_REVEAL) {
            return ResponseEntity.status(403).body(java.util.Collections.singletonMap("error", "Şu anda sonraki soruya geçilemez. Tüm oyuncular cevaplamalı ve cevaplar açıklanıyor olmalı."));
        }
        boolean advanced = gameService.goToNextQuestion(room);
        if (!advanced) {
            return ResponseEntity.status(400).body(java.util.Collections.singletonMap("error", "Sonraki soruya geçilemedi. Oyun bitmiş olabilir veya başka bir hata oluştu."));
        }
        // Oda state'ini güncel olarak döndür
        var stateInfo = gameService.getGameState(roomCode, null);
        return ResponseEntity.ok(stateInfo);
    }
}
