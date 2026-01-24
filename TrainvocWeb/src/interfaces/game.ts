/** Represents a player's answer to a question */
export interface PlayerAnswerRecord {
    questionId: string;
    selectedOptionIndex: number;
    correct: boolean;
    answerTimeMs: number;
}

export interface Player {
    id: string;
    name: string;
    score?: number;
    correctCount?: number;
    wrongCount?: number;
    totalAnswerTime?: number;
    answers?: PlayerAnswerRecord[];
    room?: GameRoom;
    isTop3?: boolean;
    isYou?: boolean;
    avatarId?: number | string;
}

export interface GameRoom {
    roomCode: string;
    players: Player[];
    currentQuestionIndex: number;
    started: boolean;
    gameStarted?: boolean; // Alias for compatibility with LobbyData
    hostId: string;
    questionDuration: number;
    optionCount: number;
    level: string;
    totalQuestionCount: number;
    hashedPassword?: string | null;
    lastUsed?: string | Date;
}

export interface LobbyData {
    players: Player[];
    hostId: string;
    roomCode: string;
    gameStarted: boolean;
    questionDuration: number;
    optionCount: number;
    level: string;
    totalQuestionCount: number;
}
