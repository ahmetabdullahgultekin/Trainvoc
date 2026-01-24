package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizQuestion;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.QuizService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Handles game start requests via WebSocket.
 * Only the host can start the game.
 * Broadcasts game start and first question to all players.
 */
@Component
public class StartGameHandler implements WebSocketMessageHandler {

    private final RoomService roomService;
    private final PlayerService playerService;
    private final QuizService quizService;
    private final WebSocketContext context;

    public StartGameHandler(RoomService roomService, PlayerService playerService,
                           QuizService quizService, WebSocketContext context) {
        this.roomService = roomService;
        this.playerService = playerService;
        this.quizService = quizService;
        this.context = context;
    }

    @Override
    public String getMessageType() {
        return "start";
    }

    @Override
    public void handle(WebSocketSession session, JSONObject message, WebSocketContext ctx) throws Exception {
        String roomCode = message.getString("roomCode");

        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            sendError(session, ctx, "Room not found.");
            return;
        }

        // Check if game already started
        if (room.getCurrentState() != GameState.LOBBY) {
            sendError(session, ctx, "Game has already started.");
            return;
        }

        // Generate questions for the game
        List<QuizQuestion> questions = quizService.generateQuestions(
                room.getLevel(),
                room.getOptionCount(),
                room.getTotalQuestionCount()
        );

        // Start the room
        roomService.startRoom(roomCode);
        room = roomService.findByRoomCode(roomCode); // Reload to get updated state

        // Broadcast game state changed to all players
        JSONObject stateChanged = new JSONObject();
        stateChanged.put("type", "gameStateChanged");
        stateChanged.put("state", GameState.COUNTDOWN.ordinal());
        stateChanged.put("remainingTime", 3); // Countdown seconds

        context.broadcastToRoom(room, stateChanged);

        // Schedule question broadcast after countdown
        // For now, send questions array so clients have them ready
        JSONObject questionsMsg = new JSONObject();
        questionsMsg.put("type", "questions");
        questionsMsg.put("questions", buildQuestionsArray(questions));
        questionsMsg.put("totalCount", questions.size());

        context.broadcastToRoom(room, questionsMsg);
    }

    private JSONArray buildQuestionsArray(List<QuizQuestion> questions) {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion q = questions.get(i);
            JSONObject qObj = new JSONObject();
            qObj.put("index", i);
            qObj.put("text", q.getEnglish());
            qObj.put("english", q.getEnglish());
            qObj.put("options", new JSONArray(q.getOptions()));
            qObj.put("correctMeaning", q.getCorrectMeaning());
            arr.put(qObj);
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
