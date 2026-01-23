import { useState, useEffect, useRef, useCallback } from 'react';
import type { Player, LobbyData } from '../interfaces/game';
import type { QuizQuestion } from '../interfaces/gameExtra';
import { WebSocketService, ConnectionState, Question as WSQuestion, PlayerRanking } from '../services/WebSocketService';
import { GameService } from '../services';

export const GameStep = {
    lobby: 0,
    countdown: 1,
    question: 2,
    answer_reveal: 3,
    ranking: 4,
    final: 5
} as const;

export type GameStepType = typeof GameStep[keyof typeof GameStep];

interface GameStateData {
    step: GameStepType;
    currentQuestionIndex: number;
    remainingTime: number | null;
    questions: QuizQuestion[];
    players: Player[];
    lobby: LobbyData | null;
}

interface UseGameStateOptions {
    roomCode: string;
    playerId: string;
    enabled?: boolean;
}

interface UseGameStateResult extends GameStateData {
    loading: boolean;
    error: string | null;
    connectionState: ConnectionState;
    refresh: () => Promise<void>;
    localTimeLeft: number | null;
    submitAnswer: (answerIndex: number) => void;
    answerSubmitted: boolean;
    lastAnswerCorrect: boolean | null;
    lastAnswerScore: number | null;
}

/**
 * Hook for managing game state with WebSocket synchronization.
 * Replaces polling with real-time WebSocket events for instant updates.
 */
export function useGameState(options: UseGameStateOptions): UseGameStateResult {
    const {
        roomCode,
        playerId,
        enabled = true,
    } = options;

    const [step, setStep] = useState<GameStepType>(GameStep.lobby);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [remainingTime, setRemainingTime] = useState<number | null>(null);
    const [localTimeLeft, setLocalTimeLeft] = useState<number | null>(null);
    const [questions, setQuestions] = useState<QuizQuestion[]>([]);
    const [players, setPlayers] = useState<Player[]>([]);
    const [lobby, setLobby] = useState<LobbyData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [connectionState, setConnectionState] = useState<ConnectionState>(
        WebSocketService.getConnectionState()
    );
    const [answerSubmitted, setAnswerSubmitted] = useState(false);
    const [lastAnswerCorrect, setLastAnswerCorrect] = useState<boolean | null>(null);
    const [lastAnswerScore, setLastAnswerScore] = useState<number | null>(null);

    const mountedRef = useRef(true);
    const answerStartTime = useRef<number>(Date.now());

    // Subscribe to connection state
    useEffect(() => {
        const unsubscribe = WebSocketService.onStateChange(setConnectionState);
        return unsubscribe;
    }, []);

    // Initial fetch + WebSocket setup
    useEffect(() => {
        if (!enabled || !roomCode || !playerId) return;

        mountedRef.current = true;

        // Fetch initial state via REST
        const fetchInitialState = async () => {
            try {
                const data = await GameService.getGameState(roomCode, playerId);
                if (!mountedRef.current || !data) return;

                const stateValue = typeof data.state === 'number' ? data.state : GameStep.lobby;
                setStep(stateValue as GameStepType);
                setCurrentQuestionIndex(data.currentQuestionIndex || 0);
                setRemainingTime(data.remainingTime);
                setLocalTimeLeft(data.remainingTime);

                if (Array.isArray(data.questions) && data.questions.length > 0) {
                    setQuestions(data.questions);
                }

                if (Array.isArray(data.players) && data.players.length > 0) {
                    setPlayers(data.players.map((p) => ({
                        ...p,
                        id: p.playerId ?? p.id ?? '',
                        name: p.name ?? '',
                        score: p.score ?? 0
                    })));
                } else if (Array.isArray(data.scores) && data.scores.length > 0) {
                    setPlayers(data.scores.map((s) => ({
                        id: s.playerId,
                        name: s.name,
                        score: s.score
                    })));
                }

                if (data.lobby) {
                    setLobby(data.lobby);
                }

                setError(null);
            } catch (err) {
                if (mountedRef.current) {
                    setError('Failed to fetch game state');
                }
            } finally {
                if (mountedRef.current) {
                    setLoading(false);
                }
            }
        };

        fetchInitialState();

        // Set up WebSocket handlers for real-time game updates
        WebSocketService.setHandlers({
            onConnect: () => {
                setError(null);
            },
            onDisconnect: (reason) => {
                if (reason && mountedRef.current) {
                    setError('Disconnected: ' + reason);
                }
            },
            onError: (errorMsg) => {
                if (mountedRef.current) {
                    setError(errorMsg);
                }
            },
            onGameStateChanged: (state, time) => {
                if (!mountedRef.current) return;
                setStep(state as GameStepType);
                setRemainingTime(time);
                setLocalTimeLeft(time);

                // Reset answer state for new question
                if (state === GameStep.question) {
                    setAnswerSubmitted(false);
                    setLastAnswerCorrect(null);
                    setLastAnswerScore(null);
                    answerStartTime.current = Date.now();
                }
            },
            onQuestion: (question: WSQuestion, questionIndex: number) => {
                if (!mountedRef.current) return;
                setCurrentQuestionIndex(questionIndex);
                setQuestions(prev => {
                    const newQuestions = [...prev];
                    newQuestions[questionIndex] = {
                        question: question.text,
                        options: question.options,
                        correctIndex: -1 // Server won't send this until reveal
                    };
                    return newQuestions;
                });
                setAnswerSubmitted(false);
                answerStartTime.current = Date.now();
            },
            onAnswerResult: (correct: boolean, _correctIndex: number, score: number) => {
                if (!mountedRef.current) return;
                setLastAnswerCorrect(correct);
                setLastAnswerScore(score);
            },
            onRankings: (rankings: PlayerRanking[]) => {
                if (!mountedRef.current) return;
                setPlayers(rankings.map(r => ({
                    id: r.id,
                    name: r.name,
                    avatarId: r.avatarId,
                    score: r.score
                })));
            },
            onPlayersUpdate: (updatedPlayers) => {
                if (!mountedRef.current) return;
                setPlayers(updatedPlayers.map(p => ({
                    id: p.id,
                    name: p.name,
                    avatarId: p.avatarId,
                    score: p.score || 0
                })));
            },
            onGameEnded: (finalRankings: PlayerRanking[]) => {
                if (!mountedRef.current) return;
                setStep(GameStep.final);
                setPlayers(finalRankings.map(r => ({
                    id: r.id,
                    name: r.name,
                    avatarId: r.avatarId,
                    score: r.score
                })));
            },
        });

        // Connect to WebSocket if not connected
        if (connectionState === 'disconnected') {
            WebSocketService.connect();
        }

        return () => {
            mountedRef.current = false;
        };
    }, [enabled, roomCode, playerId, connectionState]);

    // Local timer countdown
    useEffect(() => {
        if (remainingTime == null) return;

        setLocalTimeLeft(remainingTime);
        if (remainingTime <= 0) return;

        const timer = setInterval(() => {
            setLocalTimeLeft(prev => (prev && prev > 0 ? prev - 1 : 0));
        }, 1000);

        return () => clearInterval(timer);
    }, [remainingTime]);

    const refresh = useCallback(async () => {
        if (!roomCode || !playerId) return;
        setLoading(true);

        try {
            const data = await GameService.getGameState(roomCode, playerId);
            if (!mountedRef.current || !data) return;

            const stateValue = typeof data.state === 'number' ? data.state : GameStep.lobby;
            setStep(stateValue as GameStepType);
            setCurrentQuestionIndex(data.currentQuestionIndex || 0);
            setRemainingTime(data.remainingTime);
            setLocalTimeLeft(data.remainingTime);

            if (Array.isArray(data.players)) {
                setPlayers(data.players.map((p) => ({
                    id: p.playerId ?? p.id ?? '',
                    name: p.name ?? '',
                    score: p.score ?? 0
                })));
            }

            setError(null);
        } catch (err) {
            if (mountedRef.current) {
                setError('Failed to refresh game state');
            }
        } finally {
            if (mountedRef.current) {
                setLoading(false);
            }
        }
    }, [roomCode, playerId]);

    const submitAnswer = useCallback((answerIndex: number) => {
        if (answerSubmitted || !roomCode || !playerId) return;

        const answerTime = Date.now() - answerStartTime.current;
        setAnswerSubmitted(true);

        WebSocketService.submitAnswer(roomCode, playerId, answerIndex, answerTime);
    }, [roomCode, playerId, answerSubmitted]);

    return {
        step,
        currentQuestionIndex,
        remainingTime,
        localTimeLeft,
        questions,
        players,
        lobby,
        loading,
        error,
        connectionState,
        refresh,
        submitAnswer,
        answerSubmitted,
        lastAnswerCorrect,
        lastAnswerScore,
    };
}

export default useGameState;
