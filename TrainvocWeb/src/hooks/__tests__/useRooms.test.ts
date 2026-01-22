import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useRooms } from '../useRooms';
import { RoomService } from '../../services';
import type { GameRoom } from '../../interfaces/game';

// Mock the RoomService
vi.mock('../../services', () => ({
    RoomService: {
        fetchRooms: vi.fn(),
        filterByStatus: vi.fn((rooms, started) =>
            rooms.filter((r: GameRoom) => r.started === started)
        ),
        filterAvailable: vi.fn((rooms) =>
            rooms.filter((r: GameRoom) => !r.started)
        ),
    },
}));

describe('useRooms', () => {
    const mockRooms: GameRoom[] = [
        {
            roomCode: 'ABC123',
            players: [{ id: '1', name: 'Player 1', score: 0 }],
            currentQuestionIndex: 0,
            started: false,
            hostId: '1',
            questionDuration: 60,
            optionCount: 4,
            level: 'all',
            totalQuestionCount: 10,
        },
        {
            roomCode: 'DEF456',
            players: [{ id: '2', name: 'Player 2', score: 100 }],
            currentQuestionIndex: 5,
            started: true,
            hostId: '2',
            questionDuration: 30,
            optionCount: 4,
            level: 'A1',
            totalQuestionCount: 20,
        },
    ];

    beforeEach(() => {
        vi.mocked(RoomService.fetchRooms).mockResolvedValue(mockRooms);
    });

    afterEach(() => {
        vi.clearAllMocks();
    });

    it('should fetch rooms on mount', async () => {
        const { result } = renderHook(() => useRooms({ autoRefresh: false }));

        expect(result.current.loading).toBe(true);

        await waitFor(() => {
            expect(result.current.loading).toBe(false);
        });

        expect(RoomService.fetchRooms).toHaveBeenCalledTimes(1);
        expect(result.current.rooms).toEqual(mockRooms);
        expect(result.current.error).toBeNull();
    });

    it('should separate available and started rooms', async () => {
        const { result } = renderHook(() => useRooms({ autoRefresh: false }));

        await waitFor(() => {
            expect(result.current.loading).toBe(false);
        });

        expect(result.current.availableRooms).toHaveLength(1);
        expect(result.current.startedRooms).toHaveLength(1);
        expect(result.current.availableRooms[0].roomCode).toBe('ABC123');
        expect(result.current.startedRooms[0].roomCode).toBe('DEF456');
    });

    it('should handle fetch errors gracefully', async () => {
        vi.mocked(RoomService.fetchRooms).mockRejectedValueOnce(new Error('Network error'));

        const { result } = renderHook(() => useRooms({ autoRefresh: false }));

        await waitFor(() => {
            expect(result.current.loading).toBe(false);
        });

        expect(result.current.error).toBe('Failed to fetch rooms');
        expect(result.current.rooms).toEqual([]);
    });

    it('should allow manual refresh', async () => {
        const { result } = renderHook(() => useRooms({ autoRefresh: false }));

        await waitFor(() => {
            expect(result.current.loading).toBe(false);
        });

        expect(RoomService.fetchRooms).toHaveBeenCalledTimes(1);

        // Manually trigger refresh
        result.current.refresh();

        await waitFor(() => {
            expect(RoomService.fetchRooms).toHaveBeenCalledTimes(2);
        });
    });

    it('should return correct initial state', () => {
        vi.mocked(RoomService.fetchRooms).mockImplementation(() => new Promise(() => {}));

        const { result } = renderHook(() => useRooms({ autoRefresh: false }));

        expect(result.current.loading).toBe(true);
        expect(result.current.rooms).toEqual([]);
        expect(result.current.error).toBeNull();
        expect(result.current.availableRooms).toEqual([]);
        expect(result.current.startedRooms).toEqual([]);
    });
});
