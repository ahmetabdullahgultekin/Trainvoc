import type {Player} from "./game";

export interface Answer {
    playerId: string;
    roomCode: string;
    questionId: string;
    selectedOptionIndex: number;
    timestamp: number;
}

export interface AnswerHistory {
    questionId: string;
    questionText: string;
    selectedOption: number;
    correctOption: number;
    correct: boolean;
    timestamp: number;
}

export interface Exam {
    exam: string;
}

export interface GameState {
    latestAnswers: Record<string, Answer>;
    answeredPlayers: Set<string>;
}

export interface PlayerAnswer {
    id: number;
    player: Player;
    questionId: string;
    selectedOptionIndex: number;
    correct: boolean;
    answerTimeMs: number;
}

export interface Question {
    id: string;
    text: string;
    options: string[];
    correctIndex: number;
}

export interface QuizQuestion {
    english: string;
    correctMeaning: string;
    options: string[];
}

export interface QuizSettings {
    questionDuration: number;
    optionCount: number;
    level: string;
    totalQuestionCount: number;
}

export interface Statistic {
    statId: number;
    correctCount: number;
    wrongCount: number;
    skippedCount: number;
    learned: boolean;
}

export interface Word {
    word: string;
    meaning: string;
    level: string;
    lastReviewed: number;
    statId: number;
    secondsSpent: number;
}

export interface WordExamCrossRef {
    word: string;
    exam: string;
}
