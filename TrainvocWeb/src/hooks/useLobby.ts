import { useState, useEffect, useCallback, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import type { LobbyData, Player, GameRoom } from '../interfaces/game';
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
    pollInterval?: number;
    onGameStart?: () => void;
}

interface UseLobbyResult {
    lobby: LobbyData | null;
    players: Player[];
    isHost: boolean;
    loading: boolean;
    error: string | null;
    refresh: () => Promise<void>;
    startGame: (password?: string) => Promise<void>;
    disbandLobby: (password?: string) => Promise<void>;
    leaveLobby: () => Promise<void>;
    starting: boolean;
}

/**
 * Hook for lobby management with polling and game start handling.
 */
export function useLobby(options: UseLobbyOptions): UseLobbyResult {
    const {
        roomCode,
        playerId,
        pollInterval = 2000,
        onGameStart,
    } = options;

    const navigate = useNavigate();
    const [lobby, setLobby] = useState<LobbyData | null>(null);
    const [loading, setLoading] = useState(true);
    const [starting, setStarting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const mountedRef = useRef(true);

    const fetchLobby = useCallback(async () => {
        if (!roomCode) return;

        try {
            const room = await RoomService.fetchRoom(roomCode);
            if (!mountedRef.current || !room) return;

            const lobbyData = toLobbyData(room);
            setLobby(lobbyData);
            setError(null);

            if (lobbyData.gameStarted) {
                onGameStart?.();
                navigate(`/game?roomCode=${roomCode}&playerId=${playerId}`);
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
    }, [roomCode, playerId, navigate, onGameStart]);

    const refresh = useCallback(async () => {
        setLoading(true);
        await fetchLobby();
    }, [fetchLobby]);

    const startGame = useCallback(async (password?: string) => {
        if (!roomCode) return;

        setStarting(true);
        setError(null);

        try {
            await RoomService.startGame(roomCode, password);
            navigate(`/play/game?roomCode=${roomCode}&playerId=${playerId}`);
        } catch (err) {
            setError('Failed to start game');
        } finally {
            setStarting(false);
        }
    }, [roomCode, playerId, navigate]);

    const disbandLobby = useCallback(async (password?: string) => {
        if (!roomCode) return;

        try {
            await RoomService.disbandRoom(roomCode, password);
            navigate('/play');
        } catch (err) {
            setError('Failed to disband lobby');
        }
    }, [roomCode, navigate]);

    const leaveLobby = useCallback(async () => {
        if (!roomCode || !playerId) return;

        try {
            await RoomService.leaveRoom(roomCode, playerId);
            navigate('/play');
        } catch (err) {
            setError('Failed to leave lobby');
        }
    }, [roomCode, playerId, navigate]);

    // Polling effect
    useEffect(() => {
        if (!roomCode || !playerId) {
            setError('Room code or player ID missing');
            setLoading(false);
            return;
        }

        mountedRef.current = true;
        fetchLobby();

        const interval = setInterval(fetchLobby, pollInterval);

        return () => {
            mountedRef.current = false;
            clearInterval(interval);
        };
    }, [roomCode, playerId, pollInterval, fetchLobby]);

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
        refresh,
        startGame,
        disbandLobby,
        leaveLobby,
        starting,
    };
}

export default useLobby;
