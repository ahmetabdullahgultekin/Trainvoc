package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebSocketContext.
 * Tests session management, concurrency safety, and scalability.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("WebSocketContext Tests")
class WebSocketContextTest {

    private WebSocketContext context;

    @Mock
    private WebSocketSession session1;

    @Mock
    private WebSocketSession session2;

    @Mock
    private WebSocketSession session3;

    @BeforeEach
    void setUp() {
        context = new WebSocketContext();

        // Setup session mocks
        when(session1.getId()).thenReturn("session-1");
        when(session1.isOpen()).thenReturn(true);
        when(session2.getId()).thenReturn("session-2");
        when(session2.isOpen()).thenReturn(true);
        when(session3.getId()).thenReturn("session-3");
        when(session3.isOpen()).thenReturn(true);
    }

    @Nested
    @DisplayName("Session Registration")
    class SessionRegistration {

        @Test
        @DisplayName("registers session and retrieves it by player ID")
        void registersAndRetrievesSession() {
            context.registerSession("player-1", session1);

            WebSocketSession retrieved = context.getSession("player-1");
            assertThat(retrieved).isEqualTo(session1);
        }

        @Test
        @DisplayName("returns null for unregistered player")
        void returnsNullForUnregistered() {
            WebSocketSession retrieved = context.getSession("unknown");
            assertThat(retrieved).isNull();
        }

        @Test
        @DisplayName("overwrites previous session for same player")
        void overwritesPreviousSession() {
            context.registerSession("player-1", session1);
            context.registerSession("player-1", session2);

            WebSocketSession retrieved = context.getSession("player-1");
            assertThat(retrieved).isEqualTo(session2);
        }
    }

    @Nested
    @DisplayName("Session Removal")
    class SessionRemoval {

        @Test
        @DisplayName("removes session by player ID")
        void removesByPlayerId() {
            context.registerSession("player-1", session1);
            context.removeSession("player-1");

            assertThat(context.getSession("player-1")).isNull();
        }

        @Test
        @DisplayName("removes session by WebSocketSession reference (O(1))")
        void removesBySession() {
            context.registerSession("player-1", session1);

            String playerId = context.removeBySession(session1);

            assertThat(playerId).isEqualTo("player-1");
            assertThat(context.getSession("player-1")).isNull();
        }

        @Test
        @DisplayName("returns null when removing unknown session")
        void returnsNullForUnknownSession() {
            String playerId = context.removeBySession(session1);
            assertThat(playerId).isNull();
        }
    }

    @Nested
    @DisplayName("Reverse Lookup - O(1) Performance")
    class ReverseLookup {

        @Test
        @DisplayName("finds player ID by session in O(1)")
        void findsPlayerIdBySession() {
            context.registerSession("player-1", session1);
            context.registerSession("player-2", session2);

            String found = context.findPlayerIdBySession(session1);
            assertThat(found).isEqualTo("player-1");

            found = context.findPlayerIdBySession(session2);
            assertThat(found).isEqualTo("player-2");
        }

        @Test
        @DisplayName("returns null for unknown session")
        void returnsNullForUnknownSession() {
            String found = context.findPlayerIdBySession(session1);
            assertThat(found).isNull();
        }
    }

    @Nested
    @DisplayName("Message Sending")
    class MessageSending {

        @Test
        @DisplayName("sends message to specific player")
        void sendsToPlayer() throws Exception {
            context.registerSession("player-1", session1);

            JSONObject message = new JSONObject();
            message.put("type", "test");

            context.sendToPlayer("player-1", message);

            verify(session1).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("does not send to closed session")
        void doesNotSendToClosedSession() throws Exception {
            when(session1.isOpen()).thenReturn(false);
            context.registerSession("player-1", session1);

            JSONObject message = new JSONObject();
            message.put("type", "test");

            context.sendToPlayer("player-1", message);

            verify(session1, never()).sendMessage(any());
        }

        @Test
        @DisplayName("handles send to unknown player gracefully")
        void handlesSendToUnknownPlayer() throws Exception {
            JSONObject message = new JSONObject();
            message.put("type", "test");

            // Should not throw
            context.sendToPlayer("unknown", message);
        }
    }

    @Nested
    @DisplayName("Broadcasting")
    class Broadcasting {

        @Test
        @DisplayName("broadcasts to all players in room")
        void broadcastsToRoom() throws Exception {
            context.registerSession("player-1", session1);
            context.registerSession("player-2", session2);

            Player p1 = new Player();
            p1.setId("player-1");
            Player p2 = new Player();
            p2.setId("player-2");

            GameRoom room = new GameRoom();
            room.setPlayers(List.of(p1, p2));

            JSONObject message = new JSONObject();
            message.put("type", "test");

            context.broadcastToRoom(room, message);

            verify(session1).sendMessage(any(TextMessage.class));
            verify(session2).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("skips players not in context during broadcast")
        void skipsUnknownPlayers() throws Exception {
            context.registerSession("player-1", session1);
            // player-2 not registered

            Player p1 = new Player();
            p1.setId("player-1");
            Player p2 = new Player();
            p2.setId("player-2");

            GameRoom room = new GameRoom();
            room.setPlayers(List.of(p1, p2));

            JSONObject message = new JSONObject();
            message.put("type", "test");

            context.broadcastToRoom(room, message);

            verify(session1).sendMessage(any(TextMessage.class));
            // Should not fail for missing player-2
        }
    }

    @Nested
    @DisplayName("Concurrency - Thread Safety")
    class ConcurrencyTests {

        @Test
        @DisplayName("handles concurrent registrations safely")
        void handlesConcurrentRegistrations() throws Exception {
            int threadCount = 100;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                final WebSocketSession mockSession = mock(WebSocketSession.class);
                when(mockSession.getId()).thenReturn("session-" + index);
                when(mockSession.isOpen()).thenReturn(true);

                executor.submit(() -> {
                    try {
                        startLatch.await();
                        context.registerSession("player-" + index, mockSession);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        // Should not happen
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            doneLatch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            assertThat(successCount.get()).isEqualTo(threadCount);
        }

        @Test
        @DisplayName("handles concurrent removals safely")
        void handlesConcurrentRemovals() throws Exception {
            // Register many sessions first
            List<WebSocketSession> sessions = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                WebSocketSession mockSession = mock(WebSocketSession.class);
                when(mockSession.getId()).thenReturn("session-" + i);
                sessions.add(mockSession);
                context.registerSession("player-" + i, mockSession);
            }

            int threadCount = 100;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        String removed = context.removeBySession(sessions.get(index));
                        if (removed != null) {
                            successCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        // Should not happen
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            doneLatch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            assertThat(successCount.get()).isEqualTo(100);
        }

        @Test
        @DisplayName("handles mixed concurrent operations safely")
        void handlesMixedConcurrentOperations() throws Exception {
            int operationCount = 1000;
            CountDownLatch doneLatch = new CountDownLatch(operationCount);
            ExecutorService executor = Executors.newFixedThreadPool(50);
            AtomicInteger errors = new AtomicInteger(0);

            for (int i = 0; i < operationCount; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        WebSocketSession mockSession = mock(WebSocketSession.class);
                        when(mockSession.getId()).thenReturn("session-" + index);
                        when(mockSession.isOpen()).thenReturn(true);

                        // Mix of operations
                        if (index % 3 == 0) {
                            context.registerSession("player-" + index, mockSession);
                        } else if (index % 3 == 1) {
                            context.removeSession("player-" + (index / 2));
                        } else {
                            context.findPlayerIdBySession(mockSession);
                        }
                    } catch (Exception e) {
                        errors.incrementAndGet();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            doneLatch.await(30, TimeUnit.SECONDS);
            executor.shutdown();

            assertThat(errors.get()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Scalability")
    class ScalabilityTests {

        @Test
        @DisplayName("handles large number of sessions efficiently")
        void handlesLargeNumberOfSessions() {
            int sessionCount = 10000;

            // Register many sessions
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < sessionCount; i++) {
                WebSocketSession mockSession = mock(WebSocketSession.class);
                when(mockSession.getId()).thenReturn("session-" + i);
                context.registerSession("player-" + i, mockSession);
            }
            long registrationTime = System.currentTimeMillis() - startTime;

            // Lookup should be O(1)
            startTime = System.currentTimeMillis();
            for (int i = 0; i < sessionCount; i++) {
                context.getSession("player-" + i);
            }
            long lookupTime = System.currentTimeMillis() - startTime;

            // Registration and lookup should be fast
            assertThat(registrationTime).isLessThan(5000); // Less than 5 seconds
            assertThat(lookupTime).isLessThan(1000); // Less than 1 second for 10K lookups

            System.out.println("Registration time for " + sessionCount + " sessions: " + registrationTime + "ms");
            System.out.println("Lookup time for " + sessionCount + " sessions: " + lookupTime + "ms");
        }
    }
}
