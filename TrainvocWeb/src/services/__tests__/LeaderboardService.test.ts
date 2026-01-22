import { describe, it, expect, vi, beforeEach } from 'vitest';
import { LeaderboardService, type LeaderboardEntry } from '../LeaderboardService';
import api from '../../api';

// Mock the api module
vi.mock('../../api', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
    },
}));

// Mock console.error to suppress test output
vi.spyOn(console, 'error').mockImplementation(() => {});

describe('LeaderboardService', () => {
    const mockEntry: LeaderboardEntry = {
        id: 1,
        nickname: 'TestPlayer',
        score: 5000,
        gamesPlayed: 50,
        wins: 30,
        averageScore: 100,
        rank: 1,
    };

    const mockLeaderboard: LeaderboardEntry[] = [
        mockEntry,
        { ...mockEntry, id: 2, nickname: 'Player2', score: 4000, rank: 2 },
        { ...mockEntry, id: 3, nickname: 'Player3', score: 3000, rank: 3 },
    ];

    beforeEach(() => {
        vi.clearAllMocks();
    });

    describe('getLeaderboard', () => {
        it('should fetch leaderboard with default limit', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard });

            const result = await LeaderboardService.getLeaderboard();

            expect(api.get).toHaveBeenCalledWith('/api/leaderboard', {
                params: { limit: 100 },
            });
            expect(result).toEqual(mockLeaderboard);
        });

        it('should fetch leaderboard with custom limit', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard.slice(0, 2) });

            const result = await LeaderboardService.getLeaderboard(2);

            expect(api.get).toHaveBeenCalledWith('/api/leaderboard', {
                params: { limit: 2 },
            });
            expect(result).toHaveLength(2);
        });

        it('should return empty array on error', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Network error'));

            const result = await LeaderboardService.getLeaderboard();

            expect(result).toEqual([]);
            expect(console.error).toHaveBeenCalled();
        });
    });

    describe('getTopPlayers', () => {
        it('should fetch top N players', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard });

            const result = await LeaderboardService.getTopPlayers(2);

            expect(result).toHaveLength(2);
            expect(result[0].nickname).toBe('TestPlayer');
            expect(result[1].nickname).toBe('Player2');
        });

        it('should return all players if count exceeds available', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: mockLeaderboard });

            const result = await LeaderboardService.getTopPlayers(100);

            expect(result).toHaveLength(3);
        });
    });

    describe('updateScore', () => {
        it('should update player score', async () => {
            vi.mocked(api.post).mockResolvedValueOnce({});

            await LeaderboardService.updateScore('TestPlayer', 500, true);

            expect(api.post).toHaveBeenCalledWith('/api/leaderboard/update', {
                nickname: 'TestPlayer',
                score: 500,
                isWin: true,
            });
        });

        it('should handle update errors gracefully', async () => {
            vi.mocked(api.post).mockRejectedValueOnce(new Error('Update failed'));

            await LeaderboardService.updateScore('TestPlayer', 500, false);

            expect(console.error).toHaveBeenCalled();
        });
    });

    describe('getPlayerRank', () => {
        it('should fetch player rank', async () => {
            vi.mocked(api.get).mockResolvedValueOnce({ data: { rank: 5 } });

            const rank = await LeaderboardService.getPlayerRank('TestPlayer');

            expect(api.get).toHaveBeenCalledWith('/api/leaderboard/rank', {
                params: { nickname: 'TestPlayer' },
            });
            expect(rank).toBe(5);
        });

        it('should return null when player not found', async () => {
            vi.mocked(api.get).mockRejectedValueOnce(new Error('Not found'));

            const rank = await LeaderboardService.getPlayerRank('Unknown');

            expect(rank).toBeNull();
        });
    });

    describe('calculateWinRate', () => {
        it('should calculate correct win rate', () => {
            const entry = { ...mockEntry, gamesPlayed: 100, wins: 75 };

            const winRate = LeaderboardService.calculateWinRate(entry);

            expect(winRate).toBe(75);
        });

        it('should return 0 for player with no games', () => {
            const entry = { ...mockEntry, gamesPlayed: 0, wins: 0 };

            const winRate = LeaderboardService.calculateWinRate(entry);

            expect(winRate).toBe(0);
        });

        it('should round to nearest integer', () => {
            const entry = { ...mockEntry, gamesPlayed: 3, wins: 1 };

            const winRate = LeaderboardService.calculateWinRate(entry);

            expect(winRate).toBe(33);
        });
    });

    describe('formatScore', () => {
        it('should format millions', () => {
            expect(LeaderboardService.formatScore(1500000)).toBe('1.5M');
            expect(LeaderboardService.formatScore(2000000)).toBe('2.0M');
        });

        it('should format thousands', () => {
            expect(LeaderboardService.formatScore(1500)).toBe('1.5K');
            expect(LeaderboardService.formatScore(50000)).toBe('50.0K');
        });

        it('should return raw number for small scores', () => {
            expect(LeaderboardService.formatScore(500)).toBe('500');
            expect(LeaderboardService.formatScore(0)).toBe('0');
        });

        it('should handle edge cases', () => {
            expect(LeaderboardService.formatScore(999)).toBe('999');
            expect(LeaderboardService.formatScore(1000)).toBe('1.0K');
            expect(LeaderboardService.formatScore(999999)).toBe('1000.0K');
        });
    });
});
