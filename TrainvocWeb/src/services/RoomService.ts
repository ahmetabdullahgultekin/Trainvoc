import api from '../api';
import type { GameRoom } from '../interfaces/game';

/**
 * Service for room-related operations.
 */
export const RoomService = {
    /**
     * Fetches all available rooms.
     */
    async fetchRooms(): Promise<GameRoom[]> {
        try {
            const response = await api.get<GameRoom[]>('/api/game/rooms');
            return response.data;
        } catch (error) {
            console.error('Failed to fetch rooms:', error);
            return [];
        }
    },

    /**
     * Fetches a single room by code.
     */
    async fetchRoom(roomCode: string): Promise<GameRoom | null> {
        try {
            const response = await api.get<GameRoom>(`/api/game/${roomCode}`);
            return response.data;
        } catch {
            return null;
        }
    },

    /**
     * Checks if a room exists.
     */
    async roomExists(roomCode: string): Promise<boolean> {
        const room = await this.fetchRoom(roomCode);
        return room !== null;
    },

    /**
     * Checks if a room requires a password.
     */
    async requiresPassword(roomCode: string): Promise<boolean> {
        const room = await this.fetchRoom(roomCode);
        return room?.hashedPassword !== null && room?.hashedPassword !== undefined;
    },

    /**
     * Filters rooms by status.
     */
    filterByStatus(rooms: GameRoom[], started: boolean): GameRoom[] {
        return rooms.filter(room => room.started === started);
    },

    /**
     * Filters rooms with available slots.
     */
    filterAvailable(rooms: GameRoom[], maxPlayers: number = 10): GameRoom[] {
        return rooms.filter(room =>
            !room.started && room.players.length < maxPlayers
        );
    },

    /**
     * Sorts rooms by player count (descending).
     */
    sortByPlayerCount(rooms: GameRoom[]): GameRoom[] {
        return [...rooms].sort((a, b) => b.players.length - a.players.length);
    },

    /**
     * Sorts rooms by last used (most recent first).
     */
    sortByLastUsed(rooms: GameRoom[]): GameRoom[] {
        return [...rooms].sort((a, b) => {
            const dateA = new Date(a.lastUsed || 0);
            const dateB = new Date(b.lastUsed || 0);
            return dateB.getTime() - dateA.getTime();
        });
    },

    /**
     * Starts a game in a room (host only).
     * Password is sent raw and hashed server-side.
     */
    async startGame(roomCode: string, password?: string): Promise<void> {
        let url = `/api/game/rooms/${roomCode}/start`;
        if (password) {
            url += `?password=${encodeURIComponent(password)}`;
        }
        await api.post(url);
    },

    /**
     * Disbands/closes a room (host only).
     * Password is sent raw and hashed server-side.
     */
    async disbandRoom(roomCode: string, password?: string): Promise<void> {
        let url = `/api/game/rooms/${roomCode}/disband`;
        if (password) {
            url += `?password=${encodeURIComponent(password)}`;
        }
        await api.post(url);
    },

    /**
     * Leaves a room as a player.
     */
    async leaveRoom(roomCode: string, playerId: string): Promise<void> {
        await api.post(`/api/game/rooms/${roomCode}/leave?playerId=${playerId}`);
    },
};

export default RoomService;
