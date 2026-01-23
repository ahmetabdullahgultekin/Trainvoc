import { describe, it, expect, vi, beforeEach } from 'vitest';
import { LeaderboardService } from './LeaderboardService';
import type { LeaderboardEntry } from './LeaderboardService';
import api from '../api';

// Mock the api module
vi.mock('../api', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
    },
}));

describe('LeaderboardService', () => {
    const mockEntry: LeaderboardEntry = {
        id: 1,
        nickname: 'TestPlayer',
        score: 1500,
        gamesPlayed: 10,
        wins: 7,
        averageScore: 150,
        rank: 1,
    };

    const mockLeaderboard: LeaderboardEntry[] = [
        { ...mockEntry, id: 1, nickname: 'Alice', score: 5000, rank: 1 },
        { ...mockEntry, id: 2, nickname: 'Bob', score: 4500, rank: 2 },
        { ...mockEntry, id: 3, nickname: 'Charlie', score: 4000, rank: 3 },
    ];

    beforeEach(() => {
        vi.clearAllMocks();
    });

    describe('getLeaderboard', () => {
        it('fetches leaderboard with default limit', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard });

            const result = await LeaderboardService.getLeaderboard();

            expect(result).toEqual(mockLeaderboard);
            expect(api.get).toHaveBeenCalledWith('/api/leaderboard', {
                params: { limit: 100 },
            });
        });

        it('fetches leaderboard with custom limit', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard });

            await LeaderboardService.getLeaderboard(50);

            expect(api.get).toHaveBeenCalledWith('/api/leaderboard', {
                params: { limit: 50 },
            });
        });

        it('returns empty array on error', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Network error'));

            const result = await LeaderboardService.getLeaderboard();

            expect(result).toEqual([]);
        });
    });

    describe('getTopPlayers', () => {
        it('returns top N players from leaderboard', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard });

            const result = await LeaderboardService.getTopPlayers(2);

            expect(result).toHaveLength(2);
            expect(result[0].nickname).toBe('Alice');
            expect(result[1].nickname).toBe('Bob');
        });

        it('returns all players if count exceeds leaderboard size', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard });

            const result = await LeaderboardService.getTopPlayers(10);

            expect(result).toHaveLength(3);
        });

        it('uses default count of 10', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard });

            await LeaderboardService.getTopPlayers();

            expect(api.get).toHaveBeenCalledWith('/api/leaderboard', {
                params: { limit: 10 },
            });
        });
    });

    describe('updateScore', () => {
        it('posts score update', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({ data: {} });

            await LeaderboardService.updateScore('TestPlayer', 100, true);

            expect(api.post).toHaveBeenCalledWith('/api/leaderboard/update', {
                nickname: 'TestPlayer',
                score: 100,
                isWin: true,
            });
        });

        it('handles error silently', async () => {
            vi.mocked(api.post).mockRejectedValueOnce(new Error('Network error'));

            // Should not throw
            await expect(
                LeaderboardService.updateScore('TestPlayer', 100, false)
            ).resolves.toBeUndefined();
        });
    });

    describe('getPlayerRank', () => {
        it('returns player rank on success', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: { rank: 5 } });

            const result = await LeaderboardService.getPlayerRank('TestPlayer');

            expect(result).toBe(5);
            expect(api.get).toHaveBeenCalledWith('/api/leaderboard/rank', {
                params: { nickname: 'TestPlayer' },
            });
        });

        it('returns null on error', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Not found'));

            const result = await LeaderboardService.getPlayerRank('Unknown');

            expect(result).toBeNull();
        });
    });

    describe('calculateWinRate', () => {
        it('calculates correct win rate percentage', () => {
            const entry: LeaderboardEntry = {
                ...mockEntry,
                gamesPlayed: 20,
                wins: 15,
            };

            const result = LeaderboardService.calculateWinRate(entry);

            expect(result).toBe(75);
        });

        it('returns 0 when no games played', () => {
            const entry: LeaderboardEntry = {
                ...mockEntry,
                gamesPlayed: 0,
                wins: 0,
            };

            const result = LeaderboardService.calculateWinRate(entry);

            expect(result).toBe(0);
        });

        it('rounds to nearest integer', () => {
            const entry: LeaderboardEntry = {
                ...mockEntry,
                gamesPlayed: 3,
                wins: 1,
            };

            const result = LeaderboardService.calculateWinRate(entry);

            expect(result).toBe(33); // 33.33... rounded
        });
    });

    describe('formatScore', () => {
        it('formats millions with M suffix', () => {
            expect(LeaderboardService.formatScore(1500000)).toBe('1.5M');
            expect(LeaderboardService.formatScore(2000000)).toBe('2.0M');
        });

        it('formats thousands with K suffix', () => {
            expect(LeaderboardService.formatScore(1500)).toBe('1.5K');
            expect(LeaderboardService.formatScore(25000)).toBe('25.0K');
        });

        it('returns number as string for small values', () => {
            expect(LeaderboardService.formatScore(500)).toBe('500');
            expect(LeaderboardService.formatScore(0)).toBe('0');
            expect(LeaderboardService.formatScore(999)).toBe('999');
        });

        it('handles edge cases', () => {
            expect(LeaderboardService.formatScore(1000)).toBe('1.0K');
            expect(LeaderboardService.formatScore(1000000)).toBe('1.0M');
        });
    });
});
