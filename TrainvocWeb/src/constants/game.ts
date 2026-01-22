/**
 * Game-related constants.
 * Centralized to avoid magic numbers throughout the codebase.
 */

/** Game state polling interval in milliseconds */
export const GAME_POLL_INTERVAL = 1000;

/** Lobby polling interval in milliseconds */
export const LOBBY_POLL_INTERVAL = 2000;

/** Room list refresh interval in milliseconds */
export const ROOM_REFRESH_INTERVAL = 5000;

/** Countdown duration before game starts (in seconds) */
export const COUNTDOWN_DURATION = 3;

/** Default question time limit (in seconds) */
export const DEFAULT_TIME_LIMIT = 60;

/** Game step enumeration matching backend state ordinals */
export const GameStep = {
    LOBBY: 0,
    COUNTDOWN: 1,
    QUESTION: 2,
    ANSWER_REVEAL: 3,
    RANKING: 4,
    FINAL: 5,
} as const;

export type GameStepType = typeof GameStep[keyof typeof GameStep];

/** Default quiz settings */
export const DEFAULT_QUIZ_SETTINGS = {
    questionDuration: 60,
    optionCount: 4,
    totalQuestionCount: 10,
    level: 'all',
} as const;

/** Room status labels */
export const ROOM_STATUS = {
    WAITING: 'waiting',
    STARTED: 'started',
    FINISHED: 'finished',
} as const;
