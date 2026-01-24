package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizQuestion;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.QuizService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StartGameHandler.
 * Tests game start validation, authorization, and state transitions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StartGameHandler Tests")
class StartGameHandlerTest {

    @Mock
    private RoomService roomService;

    @Mock
    private PlayerService playerService;

    @Mock
    private QuizService quizService;

    @Mock
    private WebSocketContext context;

    @Mock
    private WebSocketSession session;

    private StartGameHandler startGameHandler;
    private GameRoom testRoom;
    private Player hostPlayer;
    private Player guestPlayer;

    @BeforeEach
    void setUp() {
        startGameHandler = new StartGameHandler(roomService, playerService, quizService, context);

        hostPlayer = new Player();
        hostPlayer.setId("host-1");
        hostPlayer.setName("HostPlayer");

        guestPlayer = new Player();
        guestPlayer.setId("guest-1");
        guestPlayer.setName("GuestPlayer");

        testRoom = new GameRoom();
        testRoom.setRoomCode("START1");
        testRoom.setHostId("host-1");
        testRoom.setCurrentState(GameState.LOBBY);
        testRoom.setStarted(false);
        testRoom.setLevel("A1");
        testRoom.setOptionCount(4);
        testRoom.setTotalQuestionCount(5);
        testRoom.setPlayers(new ArrayList<>(List.of(hostPlayer, guestPlayer)));

        hostPlayer.setRoom(testRoom);
        guestPlayer.setRoom(testRoom);
    }

    @Nested
    @DisplayName("Message Type")
    class MessageType {

        @Test
        @DisplayName("returns 'start' as message type")
        void returnsStartAsMessageType() {
            assertThat(startGameHandler.getMessageType()).isEqualTo("start");
        }
    }

    @Nested
    @DisplayName("Successful Game Start")
    class SuccessfulGameStart {

        @Test
        @DisplayName("starts game and generates questions")
        void startsGameAndGeneratesQuestions() throws Exception {
            List<QuizQuestion> mockQuestions = List.of(createMockQuestion(0));
            when(roomService.findByRoomCode("START1")).thenReturn(testRoom);
            when(quizService.generateQuestions(anyString(), anyInt(), anyInt())).thenReturn(mockQuestions);

            JSONObject message = new JSONObject();
            message.put("roomCode", "START1");

            startGameHandler.handle(session, message, context);

            verify(roomService).startRoom("START1");
            verify(quizService).generateQuestions("A1", 4, 5);
        }

        @Test
        @DisplayName("broadcasts game start to all players")
        void broadcastsGameStartToAllPlayers() throws Exception {
            List<QuizQuestion> mockQuestions = List.of(createMockQuestion(0));
            when(roomService.findByRoomCode("START1")).thenReturn(testRoom);
            when(quizService.generateQuestions(anyString(), anyInt(), anyInt())).thenReturn(mockQuestions);

            JSONObject message = new JSONObject();
            message.put("roomCode", "START1");

            startGameHandler.handle(session, message, context);

            // Should broadcast gameStateChanged and questions
            verify(context, atLeast(2)).broadcastToRoom(eq(testRoom), any(JSONObject.class));
        }
    }

    private QuizQuestion createMockQuestion(int index) {
        return new QuizQuestion(
                "test" + index,
                "test-answer",
                List.of("opt1", "opt2", "opt3", "opt4")
        );
    }

    @Nested
    @DisplayName("State Validation")
    class StateValidation {

        @Test
        @DisplayName("rejects start when room not found")
        void rejectsWhenRoomNotFound() throws Exception {
            when(roomService.findByRoomCode("INVALID")).thenReturn(null);

            JSONObject message = new JSONObject();
            message.put("roomCode", "INVALID");

            startGameHandler.handle(session, message, context);

            verify(roomService, never()).startRoom(any());

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context).sendMessage(eq(session), captor.capture());
            assertThat(captor.getValue().getString("type")).isEqualTo("error");
            assertThat(captor.getValue().getString("message")).contains("Room not found");
        }

        @Test
        @DisplayName("rejects start when game already started")
        void rejectsWhenAlreadyStarted() throws Exception {
            testRoom.setStarted(true);
            testRoom.setCurrentState(GameState.QUESTION);
            when(roomService.findByRoomCode("START1")).thenReturn(testRoom);

            JSONObject message = new JSONObject();
            message.put("roomCode", "START1");

            startGameHandler.handle(session, message, context);

            verify(roomService, never()).startRoom(any());

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context).sendMessage(eq(session), captor.capture());
            assertThat(captor.getValue().getString("type")).isEqualTo("error");
            assertThat(captor.getValue().getString("message")).contains("already started");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("handles concurrent start requests")
        void handlesConcurrentStartRequests() throws Exception {
            List<QuizQuestion> mockQuestions = List.of(createMockQuestion(0));
            when(roomService.findByRoomCode("START1")).thenReturn(testRoom);
            when(quizService.generateQuestions(anyString(), anyInt(), anyInt())).thenReturn(mockQuestions);

            JSONObject message = new JSONObject();
            message.put("roomCode", "START1");

            // First request should succeed
            startGameHandler.handle(session, message, context);
            verify(roomService, times(1)).startRoom("START1");

            // Mark as started
            testRoom.setStarted(true);
            testRoom.setCurrentState(GameState.COUNTDOWN);

            // Second request should be rejected
            startGameHandler.handle(session, message, context);
            verify(roomService, times(1)).startRoom("START1"); // Still only 1 call
        }

        @Test
        @DisplayName("broadcasts countdown state with correct values")
        void broadcastsCountdownState() throws Exception {
            List<QuizQuestion> mockQuestions = List.of(createMockQuestion(0), createMockQuestion(1));
            when(roomService.findByRoomCode("START1")).thenReturn(testRoom);
            when(quizService.generateQuestions(anyString(), anyInt(), anyInt())).thenReturn(mockQuestions);

            JSONObject message = new JSONObject();
            message.put("roomCode", "START1");

            startGameHandler.handle(session, message, context);

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context, atLeast(1)).broadcastToRoom(eq(testRoom), captor.capture());

            // Find the gameStateChanged message
            boolean foundStateChange = captor.getAllValues().stream()
                    .anyMatch(msg -> "gameStateChanged".equals(msg.optString("type")));
            assertThat(foundStateChange).isTrue();
        }

        @Test
        @DisplayName("includes questions in broadcast")
        void includesQuestionsInBroadcast() throws Exception {
            List<QuizQuestion> mockQuestions = List.of(createMockQuestion(0), createMockQuestion(1));
            when(roomService.findByRoomCode("START1")).thenReturn(testRoom);
            when(quizService.generateQuestions(anyString(), anyInt(), anyInt())).thenReturn(mockQuestions);

            JSONObject message = new JSONObject();
            message.put("roomCode", "START1");

            startGameHandler.handle(session, message, context);

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context, atLeast(1)).broadcastToRoom(eq(testRoom), captor.capture());

            // Find the questions message
            boolean foundQuestions = captor.getAllValues().stream()
                    .anyMatch(msg -> "questions".equals(msg.optString("type"))
                            && msg.has("totalCount")
                            && msg.getInt("totalCount") == 2);
            assertThat(foundQuestions).isTrue();
        }
    }
}
