import { useState, useEffect, useRef, useCallback } from 'react';
import { Player, LobbyData } from '../interfaces/game';
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
    questions: any[];
    players: Player[];
    lobby: LobbyData | null;
}

interface UseGameStateOptions {
    roomCode: string;
    playerId: string;
    pollInterval?: number;
    enabled?: boolean;
}

interface UseGameStateResult extends GameStateData {
    loading: boolean;
    error: string | null;
    refresh: () => Promise<void>;
    localTimeLeft: number | null;
}

/**
 * Hook for managing game state with server synchronization.
 * Handles polling, local timer countdown, and state transitions.
 */
export function useGameState(options: UseGameStateOptions): UseGameStateResult {
    const {
        roomCode,
        playerId,
        pollInterval = 1000,
        enabled = true,
    } = options;

    const [step, setStep] = useState<GameStepType>(GameStep.lobby);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [remainingTime, setRemainingTime] = useState<number | null>(null);
    const [localTimeLeft, setLocalTimeLeft] = useState<number | null>(null);
    const [questions, setQuestions] = useState<any[]>([]);
    const [players, setPlayers] = useState<Player[]>([]);
    const [lobby, setLobby] = useState<LobbyData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
    const mountedRef = useRef(true);

    const fetchState = useCallback(async () => {
        if (!roomCode || !playerId) return;

        try {
            const data = await GameService.getGameState(roomCode, playerId);
            if (!mountedRef.current) return;

            setStep(typeof data.state === 'number' ? data.state : GameStep.lobby);
            setCurrentQuestionIndex(data.currentQuestionIndex || 0);
            setRemainingTime(data.remainingTime);
            setLocalTimeLeft(data.remainingTime);

            if (Array.isArray(data.questions) && data.questions.length > 0) {
                setQuestions(data.questions);
            }

            if (Array.isArray(data.players) && data.players.length > 0) {
                setPlayers(data.players.map((p: any) => ({
                    ...p,
                    id: p.playerId ?? p.id
                })));
            } else if (Array.isArray(data.scores) && data.scores.length > 0) {
                setPlayers(data.scores.map((s: any) => ({
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
    }, [roomCode, playerId]);

    const refresh = useCallback(async () => {
        setLoading(true);
        await fetchState();
    }, [fetchState]);

    // Polling effect
    useEffect(() => {
        if (!enabled || !roomCode || !playerId) return;

        mountedRef.current = true;
        fetchState();

        intervalRef.current = setInterval(fetchState, pollInterval);

        return () => {
            mountedRef.current = false;
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        };
    }, [fetchState, pollInterval, enabled, roomCode, playerId]);

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
        refresh,
    };
}

export default useGameState;
