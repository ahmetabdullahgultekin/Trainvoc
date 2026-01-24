import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { WebSocketService } from './WebSocketService';
import type { GameEventHandlers } from './WebSocketService';

// Mock WebSocket
class MockWebSocket {
    static CONNECTING = 0;
    static OPEN = 1;
    static CLOSING = 2;
    static CLOSED = 3;

    readyState = MockWebSocket.CONNECTING;
    onopen: ((event: Event) => void) | null = null;
    onclose: ((event: CloseEvent) => void) | null = null;
    onerror: ((event: Event) => void) | null = null;
    onmessage: ((event: MessageEvent) => void) | null = null;

    url: string;
    sentMessages: string[] = [];

    constructor(url: string) {
        this.url = url;
        // Simulate connection after a tick
        setTimeout(() => {
            this.readyState = MockWebSocket.OPEN;
            this.onopen?.(new Event('open'));
        }, 0);
    }

    send(message: string): void {
        this.sentMessages.push(message);
    }

    close(code?: number, reason?: string): void {
        this.readyState = MockWebSocket.CLOSED;
        this.onclose?.({
            code: code || 1000,
            reason: reason || '',
        } as CloseEvent);
    }

    // Helper to simulate receiving a message
    simulateMessage(data: object): void {
        this.onmessage?.({
            data: JSON.stringify(data),
        } as MessageEvent);
    }

    // Helper to simulate error
    simulateError(): void {
        this.onerror?.(new Event('error'));
    }
}

describe('WebSocketService', () => {
    let mockWebSocketInstance: MockWebSocket | null = null;

    beforeEach(() => {
        vi.useFakeTimers();
        mockWebSocketInstance = null;

        // Mock global WebSocket
        vi.stubGlobal('WebSocket', class extends MockWebSocket {
            constructor(url: string) {
                super(url);
                mockWebSocketInstance = this;
            }
        });

        // Mock import.meta.env
        vi.stubGlobal('import', {
            meta: {
                env: {
                    VITE_API_URL: 'http://localhost:8080',
                },
            },
        });

        // Disconnect any previous connection and clear state
        WebSocketService.disconnect();
        WebSocketService.clearHandlers();
        WebSocketService.clearQueue();
    });

    afterEach(() => {
        vi.useRealTimers();
        vi.unstubAllGlobals();
    });

    describe('getConnectionState', () => {
        it('returns disconnected initially', () => {
            expect(WebSocketService.getConnectionState()).toBe('disconnected');
        });
    });

    describe('connect', () => {
        it('transitions to connecting state', () => {
            WebSocketService.connect('ws://localhost:8080');

            expect(WebSocketService.getConnectionState()).toBe('connecting');
        });

        it('transitions to connected on successful connection', async () => {
            WebSocketService.connect('ws://localhost:8080');

            // Wait for mock WebSocket to "connect"
            await vi.runAllTimersAsync();

            expect(WebSocketService.getConnectionState()).toBe('connected');
        });

        it('does not reconnect if already connected', async () => {
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            const firstInstance = mockWebSocketInstance;
            WebSocketService.connect('ws://localhost:8080');

            // Should not create a new instance
            expect(mockWebSocketInstance).toBe(firstInstance);
        });
    });

    describe('disconnect', () => {
        it('sets state to disconnected', async () => {
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            WebSocketService.disconnect();

            expect(WebSocketService.getConnectionState()).toBe('disconnected');
        });
    });

    describe('send', () => {
        it('sends message when connected', async () => {
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            const result = WebSocketService.send({ type: 'test', data: 'hello' });

            expect(result).toBe(true);
            expect(mockWebSocketInstance?.sentMessages).toContain(
                JSON.stringify({ type: 'test', data: 'hello' })
            );
        });

        it('queues message when not connected', () => {
            const result = WebSocketService.send({ type: 'test' });

            expect(result).toBe(false);
        });

        it('flushes queue on connection', async () => {
            // Send while disconnected
            WebSocketService.send({ type: 'queued', value: 1 });
            WebSocketService.send({ type: 'queued', value: 2 });

            // Now connect
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            // Queue should be flushed
            expect(mockWebSocketInstance?.sentMessages).toHaveLength(2);
        });
    });

    describe('setHandlers', () => {
        it('calls onConnect handler when connected', async () => {
            const handlers: GameEventHandlers = {
                onConnect: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            expect(handlers.onConnect).toHaveBeenCalled();
        });

        it('calls onDisconnect handler when disconnected', async () => {
            const handlers: GameEventHandlers = {
                onDisconnect: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            mockWebSocketInstance?.close(1000, 'Test close');

            expect(handlers.onDisconnect).toHaveBeenCalledWith('Test close');
        });

        it('calls onError handler on error', async () => {
            const handlers: GameEventHandlers = {
                onError: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            mockWebSocketInstance?.simulateError();

            expect(handlers.onError).toHaveBeenCalledWith('WebSocket connection error');
        });
    });

    describe('message handling', () => {
        it('handles roomCreated message', async () => {
            const handlers: GameEventHandlers = {
                onRoomCreated: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            mockWebSocketInstance?.simulateMessage({
                type: 'roomCreated',
                roomCode: 'ABC123',
                playerId: 'player-1',
            });

            expect(handlers.onRoomCreated).toHaveBeenCalledWith('ABC123', 'player-1');
        });

        it('handles roomJoined message', async () => {
            const handlers: GameEventHandlers = {
                onRoomJoined: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            mockWebSocketInstance?.simulateMessage({
                type: 'roomJoined',
                roomCode: 'ABC123',
                playerId: 'player-2',
            });

            expect(handlers.onRoomJoined).toHaveBeenCalledWith('ABC123', 'player-2');
        });

        it('handles playerJoined message', async () => {
            const handlers: GameEventHandlers = {
                onPlayerJoined: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            mockWebSocketInstance?.simulateMessage({
                type: 'playerJoined',
                playerId: 'player-3',
                playerName: 'NewPlayer',
            });

            expect(handlers.onPlayerJoined).toHaveBeenCalledWith('player-3', 'NewPlayer');
        });

        it('handles gameStateChanged message', async () => {
            const handlers: GameEventHandlers = {
                onGameStateChanged: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            mockWebSocketInstance?.simulateMessage({
                type: 'gameStateChanged',
                state: 2,
                remainingTime: 30,
            });

            expect(handlers.onGameStateChanged).toHaveBeenCalledWith(2, 30);
        });

        it('handles question message', async () => {
            const handlers: GameEventHandlers = {
                onQuestion: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            mockWebSocketInstance?.simulateMessage({
                type: 'question',
                text: 'What is hello?',
                options: ['merhaba', 'hoşça kal', 'teşekkürler', 'lütfen'],
                questionIndex: 0,
            });

            expect(handlers.onQuestion).toHaveBeenCalledWith(
                { text: 'What is hello?', options: ['merhaba', 'hoşça kal', 'teşekkürler', 'lütfen'] },
                0
            );
        });

        it('handles answerResult message', async () => {
            const handlers: GameEventHandlers = {
                onAnswerResult: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            mockWebSocketInstance?.simulateMessage({
                type: 'answerResult',
                correct: true,
                correctIndex: 0,
                score: 100,
            });

            expect(handlers.onAnswerResult).toHaveBeenCalledWith(true, 0, 100);
        });

        it('handles gameEnded message', async () => {
            const handlers: GameEventHandlers = {
                onGameEnded: vi.fn(),
            };

            WebSocketService.setHandlers(handlers);
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();

            const finalRankings = [
                { id: 'p1', name: 'Winner', avatarId: 1, score: 500, rank: 1, correctCount: 5 },
            ];

            mockWebSocketInstance?.simulateMessage({
                type: 'gameEnded',
                players: finalRankings,
            });

            expect(handlers.onGameEnded).toHaveBeenCalledWith(finalRankings);
        });
    });

    describe('game operations', () => {
        beforeEach(async () => {
            WebSocketService.connect('ws://localhost:8080');
            await vi.runAllTimersAsync();
        });

        it('createRoom sends correct message', () => {
            WebSocketService.createRoom('TestHost', 1, 'hashedPwd', {
                questionDuration: 30,
                optionCount: 4,
                level: 'A1',
                totalQuestionCount: 10,
                hostWantsToJoin: true,
            });

            const sent = JSON.parse(mockWebSocketInstance?.sentMessages[0] || '{}');
            expect(sent.type).toBe('create');
            expect(sent.name).toBe('TestHost');
            expect(sent.avatarId).toBe(1);
            expect(sent.hashedPassword).toBe('hashedPwd');
        });

        it('joinRoom sends correct message', () => {
            WebSocketService.joinRoom('ABC123', 'Player', 2, 'password');

            const sent = JSON.parse(mockWebSocketInstance?.sentMessages[0] || '{}');
            expect(sent.type).toBe('join');
            expect(sent.roomCode).toBe('ABC123');
            expect(sent.name).toBe('Player');
            expect(sent.avatarId).toBe(2);
            expect(sent.password).toBe('password');
        });

        it('leaveRoom sends correct message', () => {
            WebSocketService.leaveRoom('ABC123', 'player-1');

            const sent = JSON.parse(mockWebSocketInstance?.sentMessages[0] || '{}');
            expect(sent.type).toBe('leave');
            expect(sent.roomCode).toBe('ABC123');
            expect(sent.playerId).toBe('player-1');
        });

        it('startGame sends correct message', () => {
            WebSocketService.startGame('ABC123');

            const sent = JSON.parse(mockWebSocketInstance?.sentMessages[0] || '{}');
            expect(sent.type).toBe('start');
            expect(sent.roomCode).toBe('ABC123');
        });

        it('submitAnswer sends correct message', () => {
            WebSocketService.submitAnswer('ABC123', 'player-1', 2, 1500);

            const sent = JSON.parse(mockWebSocketInstance?.sentMessages[0] || '{}');
            expect(sent.type).toBe('answer');
            expect(sent.roomCode).toBe('ABC123');
            expect(sent.playerId).toBe('player-1');
            expect(sent.answerIndex).toBe(2);
            expect(sent.answerTime).toBe(1500);
        });
    });

    describe('onStateChange', () => {
        it('notifies listeners of state changes', async () => {
            const listener = vi.fn();

            WebSocketService.onStateChange(listener);

            // Should be called immediately with current state
            expect(listener).toHaveBeenCalledWith('disconnected');

            WebSocketService.connect('ws://localhost:8080');
            expect(listener).toHaveBeenCalledWith('connecting');

            await vi.runAllTimersAsync();
            expect(listener).toHaveBeenCalledWith('connected');
        });

        it('returns unsubscribe function', () => {
            const listener = vi.fn();

            const unsubscribe = WebSocketService.onStateChange(listener);
            listener.mockClear();

            unsubscribe();

            WebSocketService.connect('ws://localhost:8080');

            // Should not be called after unsubscribe
            expect(listener).not.toHaveBeenCalled();
        });
    });
});
