export interface Player {
    id: string;
    name: string;
    score: number;
    correctCount?: number;
    wrongCount?: number;
    totalAnswerTime?: number;
    answers?: any[];
    room?: GameRoom;
    isTop3?: boolean;
    isYou?: boolean;
}

export interface GameRoom {
    roomCode: string;
    players: Player[];
    currentQuestionIndex: number;
    started: boolean;
    hostId: string;
    questionDuration: number;
    optionCount: number;
    level: string;
    totalQuestionCount: number;
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
