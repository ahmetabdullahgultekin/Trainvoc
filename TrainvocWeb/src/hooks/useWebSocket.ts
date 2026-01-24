import { useState, useEffect, useCallback, useRef } from 'react';
import { WebSocketService } from '../services/WebSocketService';
import type { ConnectionState, GameEventHandlers, Player, PlayerRanking, Question, RoomSettings } from '../services/WebSocketService';

interface UseWebSocketOptions {
    autoConnect?: boolean;
    handlers?: GameEventHandlers;
}

interface UseWebSocketResult {
    connectionState: ConnectionState;
    isConnected: boolean;
    connect: () => void;
    disconnect: () => void;
    createRoom: (name: string, avatarId: number, hashedPassword?: string, settings?: RoomSettings) => void;
    joinRoom: (roomCode: string, name: string, avatarId: number, password?: string) => void;
    leaveRoom: (roomCode: string, playerId: string) => void;
    startGame: (roomCode: string) => void;
    submitAnswer: (roomCode: string, playerId: string, answerIndex: number, answerTime: number) => void;
}

/**
 * Hook for WebSocket communication with the game server.
 * Provides connection management and game operations.
 */
export function useWebSocket(options: UseWebSocketOptions = {}): UseWebSocketResult {
    const { autoConnect = false, handlers = {} } = options;
    const [connectionState, setConnectionState] = useState<ConnectionState>(
        WebSocketService.getConnectionState()
    );
    const handlersRef = useRef(handlers);

    // Update handlers ref when handlers change
    useEffect(() => {
        handlersRef.current = handlers;
    }, [handlers]);

    // Subscribe to connection state changes
    useEffect(() => {
        const unsubscribe = WebSocketService.onStateChange(setConnectionState);
        return unsubscribe;
    }, []);

    // Set up event handlers
    useEffect(() => {
        WebSocketService.setHandlers(handlersRef.current);
        return () => {
            // Don't clear handlers on unmount - they may be needed by other components
        };
    }, []);

    // Auto-connect if requested
    useEffect(() => {
        if (autoConnect && connectionState === 'disconnected') {
            WebSocketService.connect();
        }
    }, [autoConnect, connectionState]);

    const connect = useCallback(() => {
        WebSocketService.connect();
    }, []);

    const disconnect = useCallback(() => {
        WebSocketService.disconnect();
    }, []);

    const createRoom = useCallback((
        name: string,
        avatarId: number,
        hashedPassword?: string,
        settings?: RoomSettings
    ) => {
        WebSocketService.createRoom(name, avatarId, hashedPassword, settings);
    }, []);

    const joinRoom = useCallback((
        roomCode: string,
        name: string,
        avatarId: number,
        password?: string
    ) => {
        WebSocketService.joinRoom(roomCode, name, avatarId, password);
    }, []);

    const leaveRoom = useCallback((roomCode: string, playerId: string) => {
        WebSocketService.leaveRoom(roomCode, playerId);
    }, []);

    const startGame = useCallback((roomCode: string) => {
        WebSocketService.startGame(roomCode);
    }, []);

    const submitAnswer = useCallback((
        roomCode: string,
        playerId: string,
        answerIndex: number,
        answerTime: number
    ) => {
        WebSocketService.submitAnswer(roomCode, playerId, answerIndex, answerTime);
    }, []);

    return {
        connectionState,
        isConnected: connectionState === 'connected',
        connect,
        disconnect,
        createRoom,
        joinRoom,
        leaveRoom,
        startGame,
        submitAnswer,
    };
}

export type { ConnectionState, Player, PlayerRanking, Question, RoomSettings, GameEventHandlers };
export default useWebSocket;
