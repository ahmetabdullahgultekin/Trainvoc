package com.rollingcatsoftware.trainvocmultiplayerapplication.performance;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler.WebSocketContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Performance benchmark tests.
 * Measures response times and throughput for critical operations.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Performance Benchmark Tests")
@Tag("performance")
@Disabled("Requires database connection - run with 'gradle test -Ptags=performance' when DB is available")
class PerformanceBenchmarkTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private RoomService roomService;

    private WebSocketContext wsContext;

    // Performance thresholds (in milliseconds)
    private static final long ROOM_CREATION_THRESHOLD = 100;
    private static final long PLAYER_JOIN_THRESHOLD = 50;
    private static final long SESSION_LOOKUP_THRESHOLD = 1;
    private static final long BROADCAST_THRESHOLD = 100;

    @BeforeEach
    void setUp() {
        wsContext = new WebSocketContext();
    }

    @Nested
    @DisplayName("Room Operations Performance")
    class RoomOperationsPerformance {

        @Test
        @DisplayName("room creation completes within threshold")
        void roomCreationWithinThreshold() {
            QuizSettings settings = new QuizSettings();

            long startTime = System.currentTimeMillis();
            GameRoom room = gameService.createRoom("PerfHost", 1, settings, true, null);
            long duration = System.currentTimeMillis() - startTime;

            assertThat(room).isNotNull();
            assertThat(duration).isLessThan(ROOM_CREATION_THRESHOLD);

            System.out.println("Room creation time: " + duration + "ms (threshold: " + ROOM_CREATION_THRESHOLD + "ms)");
        }

        @Test
        @DisplayName("batch room creation maintains consistent performance")
        void batchRoomCreationPerformance() {
            QuizSettings settings = new QuizSettings();
            int batchSize = 100;
            List<Long> times = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                long startTime = System.nanoTime();
                gameService.createRoom("BatchHost" + i, 1, settings, true, null);
                times.add(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
            }

            double avgTime = times.stream().mapToLong(Long::longValue).average().orElse(0);
            long maxTime = times.stream().mapToLong(Long::longValue).max().orElse(0);
            long p95Time = times.stream().sorted().skip((long) (batchSize * 0.95)).findFirst().orElse(0L);

            System.out.println("Batch room creation (" + batchSize + " rooms):");
            System.out.println("  Average: " + String.format("%.2f", avgTime) + "ms");
            System.out.println("  Max: " + maxTime + "ms");
            System.out.println("  P95: " + p95Time + "ms");

            assertThat(avgTime).isLessThan(ROOM_CREATION_THRESHOLD);
        }

        @Test
        @DisplayName("room lookup is O(1)")
        void roomLookupPerformance() {
            // Create rooms first
            List<String> roomCodes = new ArrayList<>();
            QuizSettings settings = new QuizSettings();
            for (int i = 0; i < 1000; i++) {
                GameRoom room = gameService.createRoom("LookupHost" + i, 1, settings, true, null);
                roomCodes.add(room.getRoomCode());
            }

            // Measure lookup time
            long totalLookupTime = 0;
            for (String code : roomCodes) {
                long start = System.nanoTime();
                roomService.findByRoomCode(code);
                totalLookupTime += System.nanoTime() - start;
            }

            double avgLookupTime = TimeUnit.NANOSECONDS.toMicros(totalLookupTime) / (double) roomCodes.size();
            System.out.println("Average room lookup time: " + String.format("%.2f", avgLookupTime) + " microseconds");

            // Lookup should be very fast (database indexed)
            assertThat(avgLookupTime).isLessThan(1000); // Less than 1ms average
        }
    }

    @Nested
    @DisplayName("Player Operations Performance")
    class PlayerOperationsPerformance {

        @Test
        @DisplayName("player join completes within threshold")
        void playerJoinWithinThreshold() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("JoinPerfHost", 1, settings, true, null);

            long startTime = System.currentTimeMillis();
            Player player = gameService.joinRoom(room.getRoomCode(), "JoinPerfPlayer", 1);
            long duration = System.currentTimeMillis() - startTime;

            assertThat(player).isNotNull();
            assertThat(duration).isLessThan(PLAYER_JOIN_THRESHOLD);

            System.out.println("Player join time: " + duration + "ms (threshold: " + PLAYER_JOIN_THRESHOLD + "ms)");
        }

        @Test
        @DisplayName("concurrent player joins scale well")
        void concurrentPlayerJoinsScaleWell() throws Exception {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("ConcJoinHost", 1, settings, true, null);

            int playerCount = 50;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(playerCount);
            ExecutorService executor = Executors.newFixedThreadPool(playerCount);
            AtomicLong totalTime = new AtomicLong(0);

            for (int i = 0; i < playerCount; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        long start = System.nanoTime();
                        gameService.joinRoom(room.getRoomCode(), "ConcPlayer" + index, index);
                        totalTime.addAndGet(System.nanoTime() - start);
                    } catch (Exception e) {
                        // Expected for some concurrent operations
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            long overallStart = System.currentTimeMillis();
            startLatch.countDown();
            doneLatch.await(60, TimeUnit.SECONDS);
            long overallDuration = System.currentTimeMillis() - overallStart;
            executor.shutdown();

            double avgTime = TimeUnit.NANOSECONDS.toMillis(totalTime.get()) / (double) playerCount;
            double throughput = playerCount / (overallDuration / 1000.0);

            System.out.println("Concurrent player joins (" + playerCount + " players):");
            System.out.println("  Total time: " + overallDuration + "ms");
            System.out.println("  Average per join: " + String.format("%.2f", avgTime) + "ms");
            System.out.println("  Throughput: " + String.format("%.2f", throughput) + " joins/second");

            // Should complete in reasonable time
            assertThat(overallDuration).isLessThan(10000); // Less than 10 seconds
        }
    }

    @Nested
    @DisplayName("WebSocket Context Performance")
    class WebSocketContextPerformance {

        @Test
        @DisplayName("session registration is O(1)")
        void sessionRegistrationIsConstantTime() {
            int sessionCount = 10000;
            List<WebSocketSession> sessions = new ArrayList<>();

            // Create mock sessions
            for (int i = 0; i < sessionCount; i++) {
                WebSocketSession mockSession = mock(WebSocketSession.class);
                when(mockSession.getId()).thenReturn("session-" + i);
                sessions.add(mockSession);
            }

            // Measure registration time
            long startTime = System.nanoTime();
            for (int i = 0; i < sessionCount; i++) {
                wsContext.registerSession("player-" + i, sessions.get(i));
            }
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            double avgTimePerOp = duration / (double) sessionCount;
            System.out.println("Session registration for " + sessionCount + " sessions:");
            System.out.println("  Total time: " + duration + "ms");
            System.out.println("  Average per operation: " + String.format("%.4f", avgTimePerOp) + "ms");

            assertThat(duration).isLessThan(2000); // Less than 2 seconds for 10K operations
        }

        @Test
        @DisplayName("session lookup is O(1)")
        void sessionLookupIsConstantTime() {
            int sessionCount = 10000;

            // Register sessions first
            for (int i = 0; i < sessionCount; i++) {
                WebSocketSession mockSession = mock(WebSocketSession.class);
                when(mockSession.getId()).thenReturn("session-" + i);
                wsContext.registerSession("player-" + i, mockSession);
            }

            // Measure lookup time
            long startTime = System.nanoTime();
            for (int i = 0; i < sessionCount; i++) {
                wsContext.getSession("player-" + i);
            }
            long duration = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);

            double avgTimePerLookup = duration / (double) sessionCount;
            System.out.println("Session lookup for " + sessionCount + " sessions:");
            System.out.println("  Total time: " + TimeUnit.MICROSECONDS.toMillis(duration) + "ms");
            System.out.println("  Average per lookup: " + String.format("%.2f", avgTimePerLookup) + " microseconds");

            assertThat(avgTimePerLookup).isLessThan(SESSION_LOOKUP_THRESHOLD * 1000); // Less than 1ms per lookup
        }

        @Test
        @DisplayName("reverse lookup (findPlayerIdBySession) is O(1)")
        void reverseLookupIsConstantTime() {
            int sessionCount = 10000;
            List<WebSocketSession> sessions = new ArrayList<>();

            // Register sessions first
            for (int i = 0; i < sessionCount; i++) {
                WebSocketSession mockSession = mock(WebSocketSession.class);
                when(mockSession.getId()).thenReturn("session-" + i);
                sessions.add(mockSession);
                wsContext.registerSession("player-" + i, mockSession);
            }

            // Measure reverse lookup time
            long startTime = System.nanoTime();
            for (WebSocketSession session : sessions) {
                wsContext.findPlayerIdBySession(session);
            }
            long duration = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);

            double avgTimePerLookup = duration / (double) sessionCount;
            System.out.println("Reverse session lookup for " + sessionCount + " sessions:");
            System.out.println("  Total time: " + TimeUnit.MICROSECONDS.toMillis(duration) + "ms");
            System.out.println("  Average per lookup: " + String.format("%.2f", avgTimePerLookup) + " microseconds");

            assertThat(avgTimePerLookup).isLessThan(SESSION_LOOKUP_THRESHOLD * 1000);
        }
    }

    @Nested
    @DisplayName("Memory Usage")
    class MemoryUsage {

        @Test
        @DisplayName("handles large number of rooms without excessive memory")
        void handlesLargeRoomCountMemory() {
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

            int roomCount = 500;
            QuizSettings settings = new QuizSettings();
            for (int i = 0; i < roomCount; i++) {
                gameService.createRoom("MemHost" + i, 1, settings, true, null);
            }

            runtime.gc();
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = memoryAfter - memoryBefore;
            double memoryPerRoom = memoryUsed / (double) roomCount;

            System.out.println("Memory usage for " + roomCount + " rooms:");
            System.out.println("  Total memory used: " + (memoryUsed / 1024 / 1024) + " MB");
            System.out.println("  Average per room: " + String.format("%.2f", memoryPerRoom / 1024) + " KB");

            // Reasonable memory usage per room
            assertThat(memoryPerRoom).isLessThan(100 * 1024); // Less than 100KB per room
        }
    }

    @Nested
    @DisplayName("Throughput Tests")
    class ThroughputTests {

        @Test
        @DisplayName("measures operations per second")
        void measuresOperationsPerSecond() throws Exception {
            QuizSettings settings = new QuizSettings();
            int operationCount = 100;
            int threadCount = 10;

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(operationCount);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            for (int i = 0; i < operationCount; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        gameService.createRoom("ThroughputHost" + index, 1, settings, true, null);
                    } catch (Exception e) {
                        // Ignore
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            long startTime = System.currentTimeMillis();
            startLatch.countDown();
            doneLatch.await(120, TimeUnit.SECONDS);
            long duration = System.currentTimeMillis() - startTime;
            executor.shutdown();

            double throughput = operationCount / (duration / 1000.0);
            System.out.println("Throughput test:");
            System.out.println("  Operations: " + operationCount);
            System.out.println("  Duration: " + duration + "ms");
            System.out.println("  Throughput: " + String.format("%.2f", throughput) + " operations/second");

            assertThat(throughput).isGreaterThan(5); // At least 5 operations per second
        }
    }
}
