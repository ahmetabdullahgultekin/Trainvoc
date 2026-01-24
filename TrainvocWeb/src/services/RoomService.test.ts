import { describe, it, expect, vi, beforeEach } from 'vitest';
import { RoomService } from './RoomService';
import api from '../api';
import type { GameRoom } from '../interfaces/game';

// Mock the api module
vi.mock('../api', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
    },
}));

// Mock the hashPassword utility
vi.mock('../components/shared/hashPassword', () => ({
    hashPassword: vi.fn((password: string) => Promise.resolve(`hashed-${password}`)),
}));

describe('RoomService', () => {
    const mockRoom: GameRoom = {
        roomCode: 'ABC123',
        hostId: 'player-1',
        started: false,
        currentQuestionIndex: 0,
        players: [
            { id: 'player-1', name: 'Host', avatarId: 1, score: 0 },
            { id: 'player-2', name: 'Player2', avatarId: 2, score: 0 },
        ],
        hashedPassword: null,
        questionDuration: 30,
        optionCount: 4,
        level: 'A1',
        totalQuestionCount: 10,
    };

    beforeEach(() => {
        vi.clearAllMocks();
    });

    describe('fetchRooms', () => {
        it('returns rooms array on success', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: [mockRoom] });

            const result = await RoomService.fetchRooms();

            expect(result).toEqual([mockRoom]);
            expect(api.get).toHaveBeenCalledWith('/api/game/rooms');
        });

        it('returns empty array on error', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Network error'));

            const result = await RoomService.fetchRooms();

            expect(result).toEqual([]);
        });
    });

    describe('fetchRoom', () => {
        it('returns room on success', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockRoom });

            const result = await RoomService.fetchRoom('ABC123');

            expect(result).toEqual(mockRoom);
            expect(api.get).toHaveBeenCalledWith('/api/game/ABC123');
        });

        it('returns null on error', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Not found'));

            const result = await RoomService.fetchRoom('INVALID');

            expect(result).toBeNull();
        });
    });

    describe('roomExists', () => {
        it('returns true when room exists', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockRoom });

            const result = await RoomService.roomExists('ABC123');

            expect(result).toBe(true);
        });

        it('returns false when room does not exist', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Not found'));

            const result = await RoomService.roomExists('INVALID');

            expect(result).toBe(false);
        });
    });

    describe('requiresPassword', () => {
        it('returns true when room has password', async () => {
            const roomWithPassword = { ...mockRoom, hashedPassword: 'hashed' };
            vi.mocked(api.get).mockResolvedValueOnce({ data: roomWithPassword });

            const result = await RoomService.requiresPassword('ABC123');

            expect(result).toBe(true);
        });

        it('returns false when room has no password', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockRoom });

            const result = await RoomService.requiresPassword('ABC123');

            expect(result).toBe(false);
        });
    });

    describe('filterByStatus', () => {
        it('filters rooms by started status', () => {
            const rooms: GameRoom[] = [
                { ...mockRoom, roomCode: 'ABC', started: false },
                { ...mockRoom, roomCode: 'DEF', started: true },
                { ...mockRoom, roomCode: 'GHI', started: false },
            ];

            const notStarted = RoomService.filterByStatus(rooms, false);
            const started = RoomService.filterByStatus(rooms, true);

            expect(notStarted).toHaveLength(2);
            expect(started).toHaveLength(1);
            expect(started[0].roomCode).toBe('DEF');
        });
    });

    describe('filterAvailable', () => {
        it('filters rooms with available slots', () => {
            const fullRoom: GameRoom = {
                ...mockRoom,
                roomCode: 'FULL',
                players: Array(10).fill({ id: 'p', name: 'P', avatarId: 0, score: 0 }),
            };
            const rooms: GameRoom[] = [mockRoom, fullRoom];

            const result = RoomService.filterAvailable(rooms, 10);

            expect(result).toHaveLength(1);
            expect(result[0].roomCode).toBe('ABC123');
        });

        it('excludes started rooms', () => {
            const startedRoom: GameRoom = { ...mockRoom, roomCode: 'STARTED', started: true };
            const rooms: GameRoom[] = [mockRoom, startedRoom];

            const result = RoomService.filterAvailable(rooms);

            expect(result).toHaveLength(1);
            expect(result[0].roomCode).toBe('ABC123');
        });
    });

    describe('sortByPlayerCount', () => {
        it('sorts rooms by player count descending', () => {
            const rooms: GameRoom[] = [
                { ...mockRoom, roomCode: 'A', players: [{ id: '1', name: 'P1', avatarId: 0, score: 0 }] },
                { ...mockRoom, roomCode: 'B', players: Array(5).fill({ id: 'p', name: 'P', avatarId: 0, score: 0 }) },
                { ...mockRoom, roomCode: 'C', players: Array(3).fill({ id: 'p', name: 'P', avatarId: 0, score: 0 }) },
            ];

            const result = RoomService.sortByPlayerCount(rooms);

            expect(result[0].roomCode).toBe('B');
            expect(result[1].roomCode).toBe('C');
            expect(result[2].roomCode).toBe('A');
        });

        it('does not mutate original array', () => {
            const rooms: GameRoom[] = [mockRoom];
            const result = RoomService.sortByPlayerCount(rooms);

            expect(result).not.toBe(rooms);
        });
    });

    describe('sortByLastUsed', () => {
        it('sorts rooms by last used date descending', () => {
            const rooms: GameRoom[] = [
                { ...mockRoom, roomCode: 'A', lastUsed: '2024-01-01T00:00:00Z' },
                { ...mockRoom, roomCode: 'B', lastUsed: '2024-01-03T00:00:00Z' },
                { ...mockRoom, roomCode: 'C', lastUsed: '2024-01-02T00:00:00Z' },
            ];

            const result = RoomService.sortByLastUsed(rooms);

            expect(result[0].roomCode).toBe('B');
            expect(result[1].roomCode).toBe('C');
            expect(result[2].roomCode).toBe('A');
        });
    });

    describe('startGame', () => {
        it('calls start endpoint without password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: {} });

            await RoomService.startGame('ABC123');

            expect(api.post).toHaveBeenCalledWith('/api/game/rooms/ABC123/start');
        });

        it('includes password in URL when provided', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: {} });

            await RoomService.startGame('ABC123', 'secret');

            expect(api.post).toHaveBeenCalledWith(
                '/api/game/rooms/ABC123/start?password=secret'
            );
        });
    });

    describe('disbandRoom', () => {
        it('calls disband endpoint', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: {} });

            await RoomService.disbandRoom('ABC123');

            expect(api.post).toHaveBeenCalledWith('/api/game/rooms/ABC123/disband');
        });
    });

    describe('leaveRoom', () => {
        it('calls leave endpoint with player ID', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: {} });

            await RoomService.leaveRoom('ABC123', 'player-1');

            expect(api.post).toHaveBeenCalledWith(
                '/api/game/rooms/ABC123/leave?playerId=player-1'
            );
        });
    });
});
