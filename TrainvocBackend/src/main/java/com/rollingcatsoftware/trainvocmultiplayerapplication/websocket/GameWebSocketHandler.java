package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Question;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.LeaderboardService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerAnswerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameService gameService;
    private final PlayerAnswerService playerAnswerService;
    private final LeaderboardService leaderboardService;
    private final Map<String, WebSocketSession> playerSessions = new HashMap<>();

    public GameWebSocketHandler(
            GameService gameService,
            PlayerAnswerService playerAnswerService,
            LeaderboardService leaderboardService
    ) {
        this.gameService = gameService;
        this.playerAnswerService = playerAnswerService;
        this.leaderboardService = leaderboardService;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        JSONObject json = new JSONObject(message.getPayload());
        String type = json.getString("type");

        switch (type) {
            case "create" -> {
                String hostName = json.getString("name");
                Integer avatarId = json.has("avatarId") && !json.isNull("avatarId") ? json.getInt("avatarId") : null;
                String hashedPassword = json.has("hashedPassword") && !json.isNull("hashedPassword") ? json.getString("hashedPassword") : null;
                JSONObject settingsJson = json.has("settings") ? json.getJSONObject("settings") : new JSONObject();
                QuizSettings settings = new QuizSettings();
                settings.setQuestionDuration(settingsJson.optInt("questionDuration", 60));
                settings.setOptionCount(settingsJson.optInt("optionCount", 4));
                settings.setLevel(settingsJson.optString("level", "A1"));
                settings.setTotalQuestionCount(settingsJson.optInt("totalQuestionCount", 5));
                boolean hostWantsToJoin = settingsJson.has("hostWantsToJoin") ? settingsJson.getBoolean("hostWantsToJoin") : true;
                GameRoom room = gameService.createRoom(hostName, avatarId, settings, hostWantsToJoin, hashedPassword);
                Player host = null;
                if (hostWantsToJoin && !room.getPlayers().isEmpty()) {
                    host = room.getPlayers().getFirst();
                    playerSessions.put(host.getId(), session);
                }
                JSONObject response = new JSONObject();
                response.put("type", "roomCreated");
                response.put("roomCode", room.getRoomCode());
                response.put("playerId", host != null ? host.getId() : JSONObject.NULL);
                session.sendMessage(new TextMessage(response.toString()));
            }
            case "join" -> {
                String roomCode = json.getString("roomCode");
                String name = json.getString("name");
                Integer avatarId = json.has("avatarId") && !json.isNull("avatarId") ? json.getInt("avatarId") : null;
                Player player = gameService.joinRoom(roomCode, name, avatarId);
                playerSessions.put(player.getId(), session);

                JSONObject response = new JSONObject();
                response.put("type", "roomJoined");
                response.put("roomCode", roomCode);
                response.put("playerId", player.getId());
                session.sendMessage(new TextMessage(response.toString()));
            }
            case "startQuiz" -> {
                String roomCode = json.getString("roomCode");
                GameRoom room = gameService.getRoom(roomCode);
                sendNextQuestion(room);
            }
            case "answer" -> {
                String roomCode = json.getString("roomCode");
                String playerId = json.getString("playerId");
                int selectedOptionIndex = json.getInt("selectedOptionIndex");

                GameRoom room = gameService.getRoom(roomCode);
                Player player = room.getPlayers().stream()
                        .filter(p -> p.getId().equals(playerId)).findFirst().orElse(null);

                // Demo: Soru DB'den veya room'dan alınmalı!
                Question question = null; // room.getQuestions().get(room.getCurrentQuestionIndex());
                long answerTime = 1000;

                playerAnswerService.saveAnswer(player, question, selectedOptionIndex, answerTime);

                // Skorlar ve istatistikler güncellenir.
                List<Player> topPlayers = leaderboardService.getTop3Players(room);
                JSONObject rankingMessage = new JSONObject();
                rankingMessage.put("type", "ranking");
                JSONArray arr = new JSONArray();
                for (Player p : topPlayers) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", p.getName());
                    obj.put("score", p.getScore());
                    arr.put(obj);
                }
                rankingMessage.put("players", arr);

                for (Player p : room.getPlayers()) {
                    WebSocketSession ws = playerSessions.get(p.getId());
                    if (ws != null && ws.isOpen()) {
                        ws.sendMessage(new TextMessage(rankingMessage.toString()));
                    }
                }
            }
            case "next" -> {
                String roomCode = json.getString("roomCode");
                GameRoom room = gameService.getRoom(roomCode);

                int currentIndex = room.getCurrentQuestionIndex();
                int totalQuestions = room.getTotalQuestionCount();

                if (currentIndex + 1 < totalQuestions) {
                    room.setCurrentQuestionIndex(currentIndex + 1);
                    gameService.saveRoom(room);
                    sendNextQuestion(room);
                } else {
                    sendFinalResults(room);
                }
            }
        }
    }

    private void sendNextQuestion(GameRoom room) throws Exception {
        // Gerçek projede: DB'den veya room içinden soruyu çek
        Question question = null; // room.getQuestions().get(room.getCurrentQuestionIndex());

        JSONObject questionJson = new JSONObject();
        questionJson.put("type", "question");
        questionJson.put("questionId", "q1");
        questionJson.put("text", "Translate: Cat");
        questionJson.put("options", new JSONArray(List.of("Kedi", "Köpek", "Balık", "Kuş")));
        questionJson.put("duration", room.getQuestionDuration());

        for (Player p : room.getPlayers()) {
            WebSocketSession ws = playerSessions.get(p.getId());
            if (ws != null && ws.isOpen()) {
                ws.sendMessage(new TextMessage(questionJson.toString()));
            }
        }
    }

    private void sendFinalResults(GameRoom room) throws Exception {
        List<Player> sorted = leaderboardService.getSortedPlayers(room);

        JSONObject message = new JSONObject();
        message.put("type", "finalResults");

        JSONArray arr = new JSONArray();
        for (Player p : sorted) {
            JSONObject obj = new JSONObject();
            obj.put("name", p.getName());
            obj.put("score", p.getScore());
            obj.put("correct", p.getCorrectCount());
            obj.put("wrong", p.getWrongCount());
            obj.put("totalTimeSeconds", p.getTotalAnswerTime() / 1000.0);
            arr.put(obj);
        }
        message.put("players", arr);

        for (Player p : room.getPlayers()) {
            WebSocketSession ws = playerSessions.get(p.getId());
            if (ws != null && ws.isOpen()) {
                ws.sendMessage(new TextMessage(message.toString()));
            }
        }
    }
}
