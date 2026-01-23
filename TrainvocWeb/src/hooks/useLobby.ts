import { useState, useEffect, useCallback, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import type { LobbyData, Player, GameRoom } from '../interfaces/game';
import { WebSocketService, ConnectionState, Player as WSPlayer } from '../services/WebSocketService';
import { RoomService } from '../services';

/** Converts GameRoom to LobbyData format */
function toLobbyData(room: GameRoom): LobbyData {
    return {
        players: room.players,
        hostId: room.hostId,
        roomCode: room.roomCode,
        gameStarted: room.started || room.gameStarted || false,
        questionDuration: room.questionDuration,
        optionCount: room.optionCount,
        level: room.level,
        totalQuestionCount: room.totalQuestionCount,
    };
}

interface UseLobbyOptions {
    roomCode: string | null;
    playerId: string | null;
    onGameStart?: () => void;
}

interface UseLobbyResult {
    lobby: LobbyData | null;
    players: Player[];
    isHost: boolean;
    loading: boolean;
    error: string | null;
    connectionState: ConnectionState;
    refresh: () => Promise<void>;
    startGame: () => void;
    disbandLobby: () => void;
    leaveLobby: () => void;
    starting: boolean;
}

/**
 * Hook for lobby management using WebSocket for real-time updates.
 * Replaces polling with WebSocket events for instant updates.
 */
export function useLobby(options: UseLobbyOptions): UseLobbyResult {
    const {
        roomCode,
        playerId,
        onGameStart,
    } = options;

    const navigate = useNavigate();
    const [lobby, setLobby] = useState<LobbyData | null>(null);
    const [loading, setLoading] = useState(true);
    const [starting, setStarting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [connectionState, setConnectionState] = useState<ConnectionState>(
        WebSocketService.getConnectionState()
    );
    const mountedRef = useRef(true);

    // Subscribe to connection state
    useEffect(() => {
        const unsubscribe = WebSocketService.onStateChange(setConnectionState);
        return unsubscribe;
    }, []);

    // Initial fetch via REST (for initial state) + WebSocket setup
    useEffect(() => {
        if (!roomCode || !playerId) {
            setError('Room code or player ID missing');
            setLoading(false);
            return;
        }

        mountedRef.current = true;

        // Fetch initial lobby state via REST
        const fetchInitialState = async () => {
            try {
                const room = await RoomService.fetchRoom(roomCode);
                if (!mountedRef.current || !room) return;

                const lobbyData = toLobbyData(room);
                setLobby(lobbyData);
                setError(null);

                if (lobbyData.gameStarted) {
                    onGameStart?.();
                    navigate(`/play/game?roomCode=${roomCode}&playerId=${playerId}`);
                }
            } catch (err) {
                if (mountedRef.current) {
                    setError('Failed to fetch lobby');
                }
            } finally {
                if (mountedRef.current) {
                    setLoading(false);
                }
            }
        };

        fetchInitialState();

        // Set up WebSocket handlers for real-time updates
        WebSocketService.setHandlers({
            onConnect: () => {
                setError(null);
            },
            onDisconnect: (reason) => {
                if (reason && mountedRef.current) {
                    setError('Disconnected: ' + reason);
                }
            },
            onError: (errorMsg) => {
                if (mountedRef.current) {
                    setError(errorMsg);
                    setStarting(false);
                }
            },
            onPlayersUpdate: (wsPlayers: WSPlayer[]) => {
                if (!mountedRef.current) return;
                setLobby(prev => prev ? {
                    ...prev,
                    players: wsPlayers.map(p => ({
                        id: p.id,
                        name: p.name,
                        avatarId: p.avatarId || 0,
                        score: p.score || 0
                    }))
                } : null);
            },
            onPlayerJoined: (newPlayerId, playerName) => {
                if (!mountedRef.current) return;
                setLobby(prev => {
                    if (!prev) return null;
                    if (prev.players.some(p => p.id === newPlayerId)) return prev;
                    return {
                        ...prev,
                        players: [...prev.players, { id: newPlayerId, name: playerName, avatarId: 0, score: 0 }]
                    };
                });
            },
            onPlayerLeft: (leftPlayerId) => {
                if (!mountedRef.current) return;
                setLobby(prev => prev ? {
                    ...prev,
                    players: prev.players.filter(p => p.id !== leftPlayerId)
                } : null);
            },
            onGameStateChanged: (state, _remainingTime) => {
                if (!mountedRef.current) return;
                if (state > 0) {
                    // Game has started (state 0 is lobby)
                    setStarting(false);
                    onGameStart?.();
                    navigate(`/play/game?roomCode=${roomCode}&playerId=${playerId}`);
                }
            },
        });

        // Connect to WebSocket if not connected
        if (connectionState === 'disconnected') {
            WebSocketService.connect();
        }

        return () => {
            mountedRef.current = false;
        };
    }, [roomCode, playerId, navigate, onGameStart, connectionState]);

    const refresh = useCallback(async () => {
        if (!roomCode) return;
        setLoading(true);

        try {
            const room = await RoomService.fetchRoom(roomCode);
            if (!mountedRef.current || !room) return;

            const lobbyData = toLobbyData(room);
            setLobby(lobbyData);
            setError(null);
        } catch (err) {
            if (mountedRef.current) {
                setError('Failed to refresh lobby');
            }
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    }, [roomCode]);

    const startGame = useCallback(() => {
        if (!roomCode) return;

        setStarting(true);
        setError(null);

        // Use WebSocket to start game
        WebSocketService.startGame(roomCode);
    }, [roomCode]);

    const disbandLobby = useCallback(() => {
        if (!roomCode || !playerId) return;

        WebSocketService.leaveRoom(roomCode, playerId);
        navigate('/play');
    }, [roomCode, playerId, navigate]);

    const leaveLobby = useCallback(() => {
        if (!roomCode || !playerId) return;

        WebSocketService.leaveRoom(roomCode, playerId);
        navigate('/play');
    }, [roomCode, playerId, navigate]);

    // Sort players with host first
    const players: Player[] = lobby ? (() => {
        const hostPlayer = lobby.players.find(p => p.id === lobby.hostId);
        const otherPlayers = lobby.players.filter(p => p.id !== lobby.hostId);
        return hostPlayer ? [hostPlayer, ...otherPlayers] : lobby.players;
    })() : [];

    const isHost = lobby?.hostId === playerId;

    return {
        lobby,
        players,
        isHost,
        loading,
        error,
        connectionState,
        refresh,
        startGame,
        disbandLobby,
        leaveLobby,
        starting,
    };
}

export default useLobby;
