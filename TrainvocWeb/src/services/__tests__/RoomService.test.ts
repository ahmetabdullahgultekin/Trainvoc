import { describe, it, expect, vi, beforeEach } from 'vitest';
import { RoomService } from '../RoomService';
import api from '../../api';
import type { GameRoom } from '../../interfaces/game';

// Mock the api module
vi.mock('../../api', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
    },
}));

// Mock hashPassword
vi.mock('../../components/shared/hashPassword', () => ({
    hashPassword: vi.fn((password: string) => Promise.resolve(`hashed_${password}`)),
}));

describe('RoomService', () => {
    const mockRoom: GameRoom = {
        roomCode: 'ABC123',
        players: [{ id: '1', name: 'Test Player', score: 0 }],
        currentQuestionIndex: 0,
        started: false,
        hostId: '1',
        questionDuration: 60,
        optionCount: 4,
        level: 'all',
        totalQuestionCount: 10,
    };

    beforeEach(() => {
        vi.clearAllMocks();
    });

    describe('fetchRooms', () => {
        it('should fetch all rooms successfully', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: [mockRoom] });

            const rooms = await RoomService.fetchRooms();

            expect(api.get).toHaveBeenCalledWith('/api/game/rooms');
            expect(rooms).toEqual([mockRoom]);
        });

        it('should return empty array on error', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Network error'));

            const rooms = await RoomService.fetchRooms();

            expect(rooms).toEqual([]);
        });
    });

    describe('fetchRoom', () => {
        it('should fetch a single room by code', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockRoom });

            const room = await RoomService.fetchRoom('ABC123');

            expect(api.get).toHaveBeenCalledWith('/api/game/ABC123');
            expect(room).toEqual(mockRoom);
        });

        it('should return null when room not found', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Not found'));

            const room = await RoomService.fetchRoom('INVALID');

            expect(room).toBeNull();
        });
    });

    describe('roomExists', () => {
        it('should return true when room exists', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockRoom });

            const exists = await RoomService.roomExists('ABC123');

            expect(exists).toBe(true);
        });

        it('should return false when room does not exist', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Not found'));

            const exists = await RoomService.roomExists('INVALID');

            expect(exists).toBe(false);
        });
    });

    describe('requiresPassword', () => {
        it('should return true when room has password', async () => {
            const roomWithPassword = { ...mockRoom, hashedPassword: 'hash123' };
            vi.mocked(api.get).mockResolvedValueOnce({ data: roomWithPassword });

            const requires = await RoomService.requiresPassword('ABC123');

            expect(requires).toBe(true);
        });

        it('should return false when room has no password', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockRoom });

            const requires = await RoomService.requiresPassword('ABC123');

            expect(requires).toBe(false);
        });
    });

    describe('filterByStatus', () => {
        const rooms: GameRoom[] = [
            { ...mockRoom, roomCode: 'A', started: false },
            { ...mockRoom, roomCode: 'B', started: true },
            { ...mockRoom, roomCode: 'C', started: false },
        ];

        it('should filter waiting rooms', () => {
            const waiting = RoomService.filterByStatus(rooms, false);

            expect(waiting).toHaveLength(2);
            expect(waiting.map(r => r.roomCode)).toEqual(['A', 'C']);
        });

        it('should filter started rooms', () => {
            const started = RoomService.filterByStatus(rooms, true);

            expect(started).toHaveLength(1);
            expect(started[0].roomCode).toBe('B');
        });
    });

    describe('filterAvailable', () => {
        it('should filter rooms that are not started and have available slots', () => {
            const rooms: GameRoom[] = [
                { ...mockRoom, roomCode: 'A', started: false, players: [] },
                { ...mockRoom, roomCode: 'B', started: true, players: [] },
                { ...mockRoom, roomCode: 'C', started: false, players: Array(10).fill({}) },
            ];

            const available = RoomService.filterAvailable(rooms, 10);

            expect(available).toHaveLength(1);
            expect(available[0].roomCode).toBe('A');
        });
    });

    describe('sortByPlayerCount', () => {
        it('should sort rooms by player count descending', () => {
            const rooms: GameRoom[] = [
                { ...mockRoom, roomCode: 'A', players: [{ id: '1', name: 'P1', score: 0 }] },
                { ...mockRoom, roomCode: 'B', players: Array(5).fill({ id: '1', name: 'P', score: 0 }) },
                { ...mockRoom, roomCode: 'C', players: Array(3).fill({ id: '1', name: 'P', score: 0 }) },
            ];

            const sorted = RoomService.sortByPlayerCount(rooms);

            expect(sorted.map(r => r.roomCode)).toEqual(['B', 'C', 'A']);
        });

        it('should not mutate original array', () => {
            const rooms: GameRoom[] = [
                { ...mockRoom, roomCode: 'A', players: [] },
                { ...mockRoom, roomCode: 'B', players: Array(5).fill({}) },
            ];

            const sorted = RoomService.sortByPlayerCount(rooms);

            expect(rooms[0].roomCode).toBe('A');
            expect(sorted[0].roomCode).toBe('B');
        });
    });

    describe('startGame', () => {
        it('should start game without password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await RoomService.startGame('ABC123');

            expect(api.post).toHaveBeenCalledWith('/api/game/rooms/ABC123/start');
        });

        it('should start game with password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await RoomService.startGame('ABC123', 'secret');

            expect(api.post).toHaveBeenCalledWith(
                '/api/game/rooms/ABC123/start?hashedPassword=hashed_secret'
            );
        });
    });

    describe('disbandRoom', () => {
        it('should disband room without password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await RoomService.disbandRoom('ABC123');

            expect(api.post).toHaveBeenCalledWith('/api/game/rooms/ABC123/disband');
        });

        it('should disband room with password', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await RoomService.disbandRoom('ABC123', 'secret');

            expect(api.post).toHaveBeenCalledWith(
                '/api/game/rooms/ABC123/disband?hashedPassword=hashed_secret'
            );
        });
    });

    describe('leaveRoom', () => {
        it('should leave room', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await RoomService.leaveRoom('ABC123', 'player1');

            expect(api.post).toHaveBeenCalledWith(
                '/api/game/rooms/ABC123/leave?playerId=player1'
            );
        });
    });
});
