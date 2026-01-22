import type {Player} from "./game";

/**
 * Quiz question structure from API
 */
export interface QuizQuestion {
    english: string;
    correctMeaning: string;
    options: string[];
}

/**
 * Quiz settings for room configuration
 */
export interface QuizSettings {
    questionDuration: number;
    optionCount: number;
    level: string;
    totalQuestionCount: number;
}

/**
 * API response from /api/game/state endpoint
 */
export interface GameState {
    state: number;
    currentQuestionIndex: number;
    remainingTime: number | null;
    questions?: QuizQuestion[];
    players?: Array<Partial<Player> & { playerId?: string }>;
    scores?: Array<{ playerId: string; name: string; score: number }>;
    lobby?: {
        players: Player[];
        hostId: string;
        roomCode: string;
        gameStarted: boolean;
        questionDuration: number;
        optionCount: number;
        level: string;
        totalQuestionCount: number;
    };
}
