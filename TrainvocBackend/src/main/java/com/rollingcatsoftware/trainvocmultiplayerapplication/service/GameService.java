package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.exception.RoomPasswordException;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {
    // Yeni avatar dizisi: emoji listesi
    private static final String[] ALLOWED_AVATARS = {
            "🦊", "🐱", "🐶", "🐵", "🐸", "🐼", "🐧", "🐯", "🦁", "🐮",
            "🐨", "🐰", "🐻", "🐷", "🐔", "🦄", "🐙", "🐢", "🐳", "🐝"
    };
    private final GameRoomRepository gameRoomRepo;
    private final PlayerRepository playerRepo;

    public GameService(GameRoomRepository gameRoomRepo, PlayerRepository playerRepo, QuestionRepository questionRepo) {
        this.gameRoomRepo = gameRoomRepo;
        this.playerRepo = playerRepo;
    }

    private int getRandomAvatarId() {
        return (int) (Math.random() * ALLOWED_AVATARS.length);
    }

    private boolean isValidAvatarId(Integer avatarId) {
        return avatarId != null && avatarId >= 0 && avatarId < ALLOWED_AVATARS.length;
    }

    private Player createPlayer(GameRoom room, String name, Integer avatarId) {
        Player player = new Player();
        player.setId(UUID.randomUUID().toString());
        player.setRoom(room);
        player.setName(name);
        player.setScore(0);
        player.setCorrectCount(0);
        player.setWrongCount(0);
        player.setTotalAnswerTime(0);
        if (isValidAvatarId(avatarId)) {
            player.setAvatarId(avatarId);
        } else {
            player.setAvatarId(getRandomAvatarId());
        }
        return player;
    }

    // Timing-safe equals
    private boolean timingSafeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    public GameRoom createRoom(String hostName, Integer avatarId, QuizSettings settings, boolean hostWantsToJoin, String hashedPassword) {
        GameRoom room = new GameRoom();
        room.setRoomCode(UUID.randomUUID().toString().substring(0, 5).toUpperCase());
        room.setCurrentQuestionIndex(0);
        room.setStarted(false);
        room.setQuestionDuration(settings.getQuestionDuration());
        room.setOptionCount(settings.getOptionCount());
        room.setLevel(settings.getLevel());
        room.setTotalQuestionCount(settings.getTotalQuestionCount());
        room.setLastUsed(LocalDateTime.now());
        room.setHashedPassword(hashedPassword);
        room = gameRoomRepo.save(room);
        Player host = createPlayer(room, hostName, avatarId);
        playerRepo.save(host);
        room.getPlayers().clear();
        if (hostWantsToJoin) {
            room.getPlayers().add(host);
        }
        room.setHostId(host.getId());
        gameRoomRepo.save(room);
        return room;
    }

    public Player joinRoom(String roomCode, String playerName, Integer avatarId) {
        GameRoom room = gameRoomRepo.findById(roomCode).orElse(null);
        if (room == null) return null;
        if (room.getCurrentState() != GameState.LOBBY) {
            // Sadece lobby state'inde katılım mümkün
            throw new IllegalStateException("Oyun başladıktan sonra odaya katılım mümkün değildir.");
        }
        Player player = createPlayer(room, playerName, avatarId);
        return playerRepo.save(player);
    }

    public GameRoom getRoom(String roomCode) {
        updateLastUsed(roomCode);
        return gameRoomRepo.findById(roomCode).orElse(null);
    }

    public void saveRoom(GameRoom room) {
        gameRoomRepo.save(room);
    }

    public List<GameRoom> getAllRooms() {
        return gameRoomRepo.findAll();
    }

    public void updateLastUsed(String roomCode) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room != null) {
            room.setLastUsed(LocalDateTime.now());
            gameRoomRepo.save(room);
        }
    }

    // Odayı başlatırken state'i 'COUNTDOWN' yap ve stateStartTime'ı başlat
    public boolean startRoom(String roomCode) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room != null) {
            room.setStarted(true);
            room.setCurrentState(GameState.COUNTDOWN);
            room.setStateStartTime(java.time.LocalDateTime.now());
            gameRoomRepo.save(room);
            return true;
        }
        return false;
    }

    public boolean disbandRoom(String roomCode) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room != null) {
            gameRoomRepo.delete(room);
            return true;
        }
        return false;
    }

    public boolean leaveRoom(String roomCode, String playerId) {
        Player player = playerRepo.findById(playerId).orElse(null);
        if (player != null && player.getRoom() != null && player.getRoom().getRoomCode().equals(roomCode)) {
            playerRepo.delete(player);
            return true;
        }
        return false;
    }

    public boolean checkRoomPassword(String roomCode, String hashedPassword) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room == null) return false;
        String roomHash = room.getHashedPassword();
        if (roomHash == null || roomHash.isEmpty()) return true; // Şifresiz oda
        return timingSafeEquals(roomHash, hashedPassword);
    }

    public void throwIfRoomPasswordInvalid(String roomCode, String hashedPassword) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room == null) throw new RoomPasswordException("RoomNotFound", "Oda bulunamadı.");
        String roomHash = room.getHashedPassword();
        if (roomHash == null || roomHash.isEmpty()) return; // Şifresiz oda
        if (hashedPassword == null || hashedPassword.isEmpty())
            throw new RoomPasswordException("RoomPasswordRequired", "Bu oda için şifre gereklidir.");
        if (!timingSafeEquals(roomHash, hashedPassword))
            throw new RoomPasswordException("InvalidRoomPassword", "Şifre yanlış.");
    }

    public List<Player> getPlayersByRoomCode(String roomCode) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room == null) return null;
        return playerRepo.findByRoom(room);
    }

    // Oyun state ve kalan süreyi döndüren metod
    public java.util.Map<String, Object> getGameState(String roomCode, String playerId) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room == null) return null;
        // Eğer playerId null ise oyuncu kontrolü yapmadan devam et
        Player player = null;
        if (playerId != null) {
            player = playerRepo.findById(playerId).orElse(null);
            if (player == null || !player.getRoom().getRoomCode().equals(roomCode)) return null;
        }

        // State ve süre yönetimi
        GameState state = room.getCurrentState();
        LocalDateTime stateStart = room.getStateStartTime();
        int countdownSeconds = 3;
        int questionSeconds = room.getQuestionDuration();
        int answerRevealSeconds = 0; // Artık answer_reveal'da süre yok
        int rankingSeconds = 10;
        int remainingTime = 0;
        LocalDateTime now = LocalDateTime.now();
        if (state == GameState.COUNTDOWN) {
            long elapsed = java.time.Duration.between(stateStart, now).getSeconds();
            remainingTime = (int) (countdownSeconds - elapsed);
            if (remainingTime <= 0) {
                // Countdown bitti, QUESTION state'ine geç
                room.setCurrentState(GameState.QUESTION);
                room.setStateStartTime(now);
                gameRoomRepo.save(room);
                state = GameState.QUESTION;
                remainingTime = questionSeconds;
            }
        } else if (state == GameState.QUESTION) {
            long elapsed = java.time.Duration.between(stateStart, now).getSeconds();
            remainingTime = (int) (questionSeconds - elapsed);
            if (remainingTime <= 0) {
                // Soru süresi bitti, ANSWER_REVEAL state'ine geç
                room.setCurrentState(GameState.ANSWER_REVEAL);
                room.setStateStartTime(now);
                gameRoomRepo.save(room);
                state = GameState.ANSWER_REVEAL;
                remainingTime = answerRevealSeconds;
            }
        } else if (state == GameState.ANSWER_REVEAL) {
            remainingTime = 0; // Süre yok
            // Sonraki soruya geçiş host tarafından tetiklenecek, otomatik geçiş yok
        } else if (state == GameState.RANKING) {
            long elapsed = java.time.Duration.between(stateStart, now).getSeconds();
            remainingTime = (int) (rankingSeconds - elapsed);
            if (remainingTime <= 0) {
                room.setCurrentState(GameState.FINAL);
                room.setStateStartTime(now);
                gameRoomRepo.save(room);
                state = GameState.FINAL;
                remainingTime = 0;
            }
        } else if (state == GameState.FINAL) {
            remainingTime = 0;
        } else {
            // LOBBY veya bilinmeyen state
            remainingTime = 0;
        }

        java.util.List<java.util.Map<String, Object>> scores = new java.util.ArrayList<>();
        for (Player p : playerRepo.findByRoom(room)) {
            java.util.Map<String, Object> score = new java.util.HashMap<>();
            score.put("playerId", p.getId());
            score.put("name", p.getName());
            score.put("score", p.getScore());
            scores.add(score);
        }
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        // JSON response için küçük harfli, veritabanı için enum büyük harfli kullanılmalı
        result.put("state", state.ordinal()); // Artık int olarak dönüyor
        result.put("remainingTime", remainingTime);
        result.put("players", scores);
        result.put("currentQuestionIndex", room.getCurrentQuestionIndex());
        result.put("totalQuestionCount", room.getTotalQuestionCount());
        return result;
    }

    // Sadece state ve kalan süre döndüren sade endpoint için metod
    public java.util.Map<String, Object> getSimpleState(String roomCode, String playerId) {
        GameRoom room = gameRoomRepo.findByRoomCode(roomCode);
        if (room == null) return null;
        Player player = playerRepo.findById(playerId).orElse(null);
        if (player == null || !player.getRoom().getRoomCode().equals(roomCode)) return null;

        GameState state = room.getCurrentState();
        LocalDateTime stateStart = room.getStateStartTime();
        int countdownSeconds = 3;
        int questionSeconds = room.getQuestionDuration();
        int answerRevealSeconds = 0; // Artık answer_reveal'da süre yok
        int rankingSeconds = 10;
        int remainingTime = 0;
        LocalDateTime now = LocalDateTime.now();
        if (state == GameState.COUNTDOWN) {
            long elapsed = java.time.Duration.between(stateStart, now).getSeconds();
            remainingTime = (int) (countdownSeconds - elapsed);
            if (remainingTime <= 0) {
                room.setCurrentState(GameState.QUESTION);
                room.setStateStartTime(now);
                gameRoomRepo.save(room);
                state = GameState.QUESTION;
                remainingTime = questionSeconds;
            }
        } else if (state == GameState.QUESTION) {
            long elapsed = java.time.Duration.between(stateStart, now).getSeconds();
            remainingTime = (int) (questionSeconds - elapsed);
            if (remainingTime <= 0) {
                room.setCurrentState(GameState.ANSWER_REVEAL);
                room.setStateStartTime(now);
                gameRoomRepo.save(room);
                state = GameState.ANSWER_REVEAL;
                remainingTime = answerRevealSeconds;
            }
        } else if (state == GameState.ANSWER_REVEAL) {
            remainingTime = 0; // Süre yok
            // Sonraki soruya geçiş host tarafından tetiklenecek, otomatik geçiş yok
        } else if (state == GameState.RANKING) {
            long elapsed = java.time.Duration.between(stateStart, now).getSeconds();
            remainingTime = (int) (rankingSeconds - elapsed);
            if (remainingTime <= 0) {
                room.setCurrentState(GameState.FINAL);
                room.setStateStartTime(now);
                gameRoomRepo.save(room);
                state = GameState.FINAL;
                remainingTime = 0;
            }
        } else if (state == GameState.FINAL) {
            remainingTime = 0;
        } else {
            // LOBBY veya bilinmeyen state
            remainingTime = 0;
        }
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        // JSON response için küçük harfli, veritabanı için enum büyük harfli kullanılmalı
        result.put("state", state.name().toLowerCase());
        result.put("remainingTime", remainingTime);
        return result;
    }

    /**
     * Oda için yeni soruya geçişi ve state güncellemelerini yönetir.
     *
     * @param room Oyun odası
     * @return true: yeni soruya geçildi, false: oyun bitti veya geçilemedi
     */
    public boolean goToNextQuestion(GameRoom room) {
        if (room == null) return false;
        int currentIndex = room.getCurrentQuestionIndex();
        int total = room.getTotalQuestionCount();
        // Son sorudaysa, RANKING state'ine geç
        if (currentIndex >= total - 1) {
            room.setCurrentState(GameState.RANKING);
            room.setStateStartTime(LocalDateTime.now());
            gameRoomRepo.save(room);
            return false; // Artık yeni soru yok
        } else {
            // Yeni soruya geç
            room.setCurrentQuestionIndex(currentIndex + 1);
            room.setCurrentState(GameState.COUNTDOWN);
            room.setStateStartTime(LocalDateTime.now());
            gameRoomRepo.save(room);
            // Oyuncuların currentAnsweredQuestionIndex değerlerini sıfırla
            List<Player> players = playerRepo.findByRoom(room);
            for (Player p : players) {
                p.setCurrentAnsweredQuestionIndex(null);
                playerRepo.save(p);
            }
            return true;
        }
    }
}
