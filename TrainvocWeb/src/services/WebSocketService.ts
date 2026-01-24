/**
 * WebSocket Service for real-time game communication.
 *
 * Provides a singleton WebSocket connection with:
 * - Automatic reconnection with exponential backoff
 * - Message queuing during disconnection
 * - Type-safe event handling
 * - Connection state management
 */

export type ConnectionState = 'disconnected' | 'connecting' | 'connected' | 'error';

export interface WebSocketMessage {
    type: string;
    [key: string]: unknown;
}

export interface GameEventHandlers {
    onConnect?: () => void;
    onDisconnect?: (reason?: string) => void;
    onError?: (error: string) => void;
    onRoomCreated?: (roomCode: string, playerId: string) => void;
    onRoomJoined?: (roomCode: string, playerId: string) => void;
    onPlayerJoined?: (playerId: string, playerName: string) => void;
    onPlayerLeft?: (playerId: string) => void;
    onPlayersUpdate?: (players: Player[]) => void;
    onGameStateChanged?: (state: number, remainingTime: number) => void;
    onQuestion?: (question: Question, questionIndex: number) => void;
    onAnswerResult?: (correct: boolean, correctIndex: number, score: number) => void;
    onRankings?: (players: PlayerRanking[]) => void;
    onGameEnded?: (finalRankings: PlayerRanking[]) => void;
    onMessage?: (message: WebSocketMessage) => void;
}

export interface Player {
    id: string;
    name: string;
    avatarId?: number;
    score?: number;
}

export interface PlayerRanking extends Player {
    rank: number;
    correctCount: number;
}

export interface Question {
    text: string;
    options: string[];
}

class WebSocketServiceClass {
    private static instance: WebSocketServiceClass;
    private socket: WebSocket | null = null;
    private connectionState: ConnectionState = 'disconnected';
    private messageQueue: WebSocketMessage[] = [];
    private reconnectAttempts = 0;
    private maxReconnectAttempts = 5;
    private reconnectDelay = 1000;
    private reconnectTimeout: ReturnType<typeof setTimeout> | null = null;
    private handlers: GameEventHandlers = {};
    private stateListeners: Set<(state: ConnectionState) => void> = new Set();
    private baseUrl: string = '';

    private constructor() {}

    static getInstance(): WebSocketServiceClass {
        if (!WebSocketServiceClass.instance) {
            WebSocketServiceClass.instance = new WebSocketServiceClass();
        }
        return WebSocketServiceClass.instance;
    }

    /**
     * Connect to the WebSocket server.
     */
    connect(baseUrl?: string): void {
        if (this.connectionState === 'connected' || this.connectionState === 'connecting') {
            return;
        }

        // Determine WebSocket URL
        if (baseUrl) {
            this.baseUrl = baseUrl;
        } else if (!this.baseUrl) {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const host = import.meta.env.VITE_API_URL?.replace(/^https?:\/\//, '') || 'localhost:8080';
            this.baseUrl = `${protocol}//${host}`;
        }

        this.setConnectionState('connecting');

        try {
            this.socket = new WebSocket(`${this.baseUrl}/ws/game`);
            this.setupEventListeners();
        } catch (error) {
            console.error('WebSocket connection error:', error);
            this.setConnectionState('error');
            this.scheduleReconnect();
        }
    }

    /**
     * Disconnect from the WebSocket server.
     */
    disconnect(): void {
        this.clearReconnectTimeout();
        this.reconnectAttempts = 0;

        if (this.socket) {
            this.socket.close(1000, 'Client disconnect');
            this.socket = null;
        }

        this.setConnectionState('disconnected');
    }

    /**
     * Send a message through the WebSocket.
     */
    send(message: WebSocketMessage): boolean {
        if (this.connectionState !== 'connected' || !this.socket) {
            // Queue message for later
            this.messageQueue.push(message);
            return false;
        }

        try {
            this.socket.send(JSON.stringify(message));
            return true;
        } catch (error) {
            console.error('Failed to send message:', error);
            this.messageQueue.push(message);
            return false;
        }
    }

    /**
     * Register event handlers.
     */
    setHandlers(handlers: GameEventHandlers): void {
        this.handlers = { ...this.handlers, ...handlers };
    }

    /**
     * Clear all event handlers.
     */
    clearHandlers(): void {
        this.handlers = {};
    }

    /**
     * Clear the message queue. Useful for testing.
     */
    clearQueue(): void {
        this.messageQueue = [];
    }

    /**
     * Subscribe to connection state changes.
     */
    onStateChange(listener: (state: ConnectionState) => void): () => void {
        this.stateListeners.add(listener);
        // Immediately call with current state
        listener(this.connectionState);
        return () => this.stateListeners.delete(listener);
    }

    /**
     * Get current connection state.
     */
    getConnectionState(): ConnectionState {
        return this.connectionState;
    }

    // ============ Room Operations ============

    createRoom(name: string, avatarId: number, hashedPassword?: string, settings?: RoomSettings): void {
        this.send({
            type: 'create',
            name,
            avatarId,
            hashedPassword,
            settings: settings || {
                questionDuration: 30,
                optionCount: 4,
                level: 'A1',
                totalQuestionCount: 10,
                hostWantsToJoin: true
            }
        });
    }

    joinRoom(roomCode: string, name: string, avatarId: number, password?: string): void {
        this.send({
            type: 'join',
            roomCode,
            name,
            avatarId,
            password
        });
    }

    leaveRoom(roomCode: string, playerId: string): void {
        this.send({
            type: 'leave',
            roomCode,
            playerId
        });
    }

    startGame(roomCode: string): void {
        this.send({
            type: 'start',
            roomCode
        });
    }

    submitAnswer(roomCode: string, playerId: string, answerIndex: number, answerTime: number): void {
        this.send({
            type: 'answer',
            roomCode,
            playerId,
            answerIndex,
            answerTime
        });
    }

    // ============ Private Methods ============

    private setupEventListeners(): void {
        if (!this.socket) return;

        this.socket.onopen = () => {
            console.log('WebSocket connected');
            this.setConnectionState('connected');
            this.reconnectAttempts = 0;
            this.handlers.onConnect?.();
            this.flushMessageQueue();
        };

        this.socket.onclose = (event) => {
            console.log('WebSocket closed:', event.code, event.reason);
            this.setConnectionState('disconnected');
            this.handlers.onDisconnect?.(event.reason);

            if (event.code !== 1000) {
                // Abnormal closure - attempt reconnect
                this.scheduleReconnect();
            }
        };

        this.socket.onerror = (error) => {
            console.error('WebSocket error:', error);
            this.setConnectionState('error');
            this.handlers.onError?.('WebSocket connection error');
        };

        this.socket.onmessage = (event) => {
            try {
                const message = JSON.parse(event.data) as WebSocketMessage;
                this.handleMessage(message);
            } catch (error) {
                console.error('Failed to parse WebSocket message:', error);
            }
        };
    }

    private handleMessage(message: WebSocketMessage): void {
        // Always call generic message handler
        this.handlers.onMessage?.(message);

        switch (message.type) {
            case 'roomCreated':
                this.handlers.onRoomCreated?.(
                    message.roomCode as string,
                    message.playerId as string
                );
                break;

            case 'roomJoined':
                this.handlers.onRoomJoined?.(
                    message.roomCode as string,
                    message.playerId as string
                );
                break;

            case 'playerJoined':
                this.handlers.onPlayerJoined?.(
                    message.playerId as string,
                    message.playerName as string
                );
                break;

            case 'playerLeft':
                this.handlers.onPlayerLeft?.(message.playerId as string);
                break;

            case 'playersUpdate':
                this.handlers.onPlayersUpdate?.(message.players as Player[]);
                break;

            case 'gameStateChanged':
                this.handlers.onGameStateChanged?.(
                    message.state as number,
                    message.remainingTime as number
                );
                break;

            case 'question':
                this.handlers.onQuestion?.(
                    {
                        text: message.text as string,
                        options: message.options as string[]
                    },
                    message.questionIndex as number
                );
                break;

            case 'answerResult':
                this.handlers.onAnswerResult?.(
                    message.correct as boolean,
                    message.correctIndex as number,
                    message.score as number
                );
                break;

            case 'rankings':
                this.handlers.onRankings?.(message.players as PlayerRanking[]);
                break;

            case 'gameEnded':
                this.handlers.onGameEnded?.(message.players as PlayerRanking[]);
                break;

            case 'error':
                this.handlers.onError?.(message.message as string);
                break;

            default:
                console.log('Unknown message type:', message.type);
        }
    }

    private setConnectionState(state: ConnectionState): void {
        this.connectionState = state;
        this.stateListeners.forEach(listener => listener(state));
    }

    private scheduleReconnect(): void {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.log('Max reconnect attempts reached');
            return;
        }

        this.clearReconnectTimeout();

        const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts);
        console.log(`Scheduling reconnect in ${delay}ms (attempt ${this.reconnectAttempts + 1})`);

        this.reconnectTimeout = setTimeout(() => {
            this.reconnectAttempts++;
            this.connect();
        }, delay);
    }

    private clearReconnectTimeout(): void {
        if (this.reconnectTimeout) {
            clearTimeout(this.reconnectTimeout);
            this.reconnectTimeout = null;
        }
    }

    private flushMessageQueue(): void {
        while (this.messageQueue.length > 0) {
            const message = this.messageQueue.shift();
            if (message) {
                this.send(message);
            }
        }
    }
}

export interface RoomSettings {
    questionDuration: number;
    optionCount: number;
    level: string;
    totalQuestionCount: number;
    hostWantsToJoin: boolean;
}

export const WebSocketService = WebSocketServiceClass.getInstance();
export default WebSocketService;
