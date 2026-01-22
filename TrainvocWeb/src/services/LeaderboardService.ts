import api from '../api';

export interface LeaderboardEntry {
    id: number;
    nickname: string;
    score: number;
    gamesPlayed: number;
    wins: number;
    averageScore: number;
    rank?: number;
}

/**
 * Service for leaderboard operations.
 */
export const LeaderboardService = {
    /**
     * Fetches the global leaderboard.
     */
    async getLeaderboard(limit: number = 100): Promise<LeaderboardEntry[]> {
        try {
            const response = await api.get<LeaderboardEntry[]>('/api/leaderboard', {
                params: { limit },
            });
            return response.data;
        } catch (error) {
            console.error('Failed to fetch leaderboard:', error);
            return [];
        }
    },

    /**
     * Fetches top N players.
     */
    async getTopPlayers(count: number = 10): Promise<LeaderboardEntry[]> {
        const leaderboard = await this.getLeaderboard(count);
        return leaderboard.slice(0, count);
    },

    /**
     * Updates a player's score on the leaderboard.
     */
    async updateScore(
        nickname: string,
        score: number,
        isWin: boolean
    ): Promise<void> {
        try {
            await api.post('/api/leaderboard/update', {
                nickname,
                score,
                isWin,
            });
        } catch (error) {
            console.error('Failed to update leaderboard:', error);
        }
    },

    /**
     * Gets a player's rank.
     */
    async getPlayerRank(nickname: string): Promise<number | null> {
        try {
            const response = await api.get<{ rank: number }>('/api/leaderboard/rank', {
                params: { nickname },
            });
            return response.data.rank;
        } catch {
            return null;
        }
    },

    /**
     * Calculates win rate percentage.
     */
    calculateWinRate(entry: LeaderboardEntry): number {
        if (entry.gamesPlayed === 0) return 0;
        return Math.round((entry.wins / entry.gamesPlayed) * 100);
    },

    /**
     * Formats score for display.
     */
    formatScore(score: number): string {
        if (score >= 1000000) {
            return `${(score / 1000000).toFixed(1)}M`;
        }
        if (score >= 1000) {
            return `${(score / 1000).toFixed(1)}K`;
        }
        return score.toString();
    },
};

export default LeaderboardService;
