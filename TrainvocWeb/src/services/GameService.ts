import api from '../api';
import type { GameRoom, Player } from '../interfaces/game';
import type { QuizSettings, GameState } from '../interfaces/gameExtra';

/**
 * Service for game-related API operations.
 * Abstracts API calls and provides type-safe methods.
 */
export const GameService = {
    /**
     * Creates a new game room.
     */
    async createRoom(
        hostName: string,
        settings: QuizSettings,
        avatarId?: number,
        hashedPassword?: string
    ): Promise<GameRoom> {
        const params = new URLSearchParams({
            hostName,
            hostWantsToJoin: 'true',
        });

        if (avatarId !== undefined) {
            params.append('avatarId', avatarId.toString());
        }
        if (hashedPassword) {
            params.append('hashedPassword', hashedPassword);
        }

        const response = await api.post<GameRoom>(
            `/api/game/create?${params.toString()}`,
            settings
        );
        return response.data;
    },

    /**
     * Joins an existing game room.
     */
    async joinRoom(
        roomCode: string,
        playerName: string,
        avatarId?: number,
        hashedPassword?: string
    ): Promise<Player> {
        const params = new URLSearchParams({
            roomCode,
            playerName,
        });

        if (avatarId !== undefined) {
            params.append('avatarId', avatarId.toString());
        }
        if (hashedPassword) {
            params.append('hashedPassword', hashedPassword);
        }

        const response = await api.post<Player>(
            `/api/game/join?${params.toString()}`
        );
        return response.data;
    },

    /**
     * Gets room details by code.
     */
    async getRoom(roomCode: string): Promise<GameRoom | null> {
        try {
            const response = await api.get<GameRoom>(`/api/game/${roomCode}`);
            return response.data;
        } catch {
            return null;
        }
    },

    /**
     * Gets all available rooms.
     */
    async getAllRooms(): Promise<GameRoom[]> {
        const response = await api.get<GameRoom[]>('/api/game/rooms');
        return response.data;
    },

    /**
     * Gets players in a room.
     */
    async getPlayers(roomCode: string): Promise<Player[]> {
        const response = await api.get<Player[]>('/api/game/players', {
            params: { roomCode },
        });
        return response.data;
    },

    /**
     * Starts a game room.
     */
    async startRoom(roomCode: string, hashedPassword?: string): Promise<void> {
        const params = new URLSearchParams();
        if (hashedPassword) {
            params.append('hashedPassword', hashedPassword);
        }

        await api.post(`/api/game/rooms/${roomCode}/start?${params.toString()}`);
    },

    /**
     * Disbands (deletes) a room.
     */
    async disbandRoom(roomCode: string, hashedPassword?: string): Promise<void> {
        const params = new URLSearchParams();
        if (hashedPassword) {
            params.append('hashedPassword', hashedPassword);
        }

        await api.post(`/api/game/rooms/${roomCode}/disband?${params.toString()}`);
    },

    /**
     * Leaves a room.
     */
    async leaveRoom(roomCode: string, playerId: string): Promise<void> {
        await api.post(`/api/game/rooms/${roomCode}/leave`, null, {
            params: { playerId },
        });
    },

    /**
     * Gets current game state.
     */
    async getGameState(
        roomCode: string,
        playerId: string
    ): Promise<GameState | null> {
        try {
            const response = await api.get<GameState>('/api/game/state', {
                params: { roomCode, playerId },
            });
            return response.data;
        } catch {
            return null;
        }
    },

    /**
     * Advances to the next question.
     */
    async nextQuestion(roomCode: string, hashedPassword?: string): Promise<GameState> {
        const params = new URLSearchParams({ roomCode });
        if (hashedPassword) {
            params.append('hashedPassword', hashedPassword);
        }

        const response = await api.post<GameState>(
            `/api/game/next?${params.toString()}`
        );
        return response.data;
    },

    /**
     * Submits an answer.
     */
    async submitAnswer(request: {
        roomCode: string;
        playerId: string;
        answer: string;
        answerTime: number;
        isCorrect: boolean;
        optionPickRate: number;
    }): Promise<{ players: Player[] }> {
        const response = await api.post<{ players: Player[] }>(
            '/api/game/answer',
            request
        );
        return response.data;
    },
};

export default GameService;
