import React, {useEffect, useRef, useState} from 'react';
import {Alert, Box} from '@mui/material';
import {useTranslation} from 'react-i18next';
import {useLocation} from 'react-router-dom';
import api from '../api';
import GameStartCountdown from '../components/GameStartCountdown';
import GameQuestion from '../components/GameQuestion';
import GameRanking from '../components/GameRanking';
import GameFinal from '../components/GameFinal';
import type {LobbyData, Player} from "../interfaces/game.ts";
import type {QuizQuestion} from "../interfaces/gameExtra.ts";

function useQuery() {
    return new URLSearchParams(useLocation().search);
}

// Oyun adımlarını enum yerine nesne olarak tanımla (ORDINAL uyumlu)
const GameStep = {
    lobby: 0,
    countdown: 1,
    question: 2,
    answer_reveal: 3,
    ranking: 4,
    final: 5
} as const;
type GameStepType = typeof GameStep[keyof typeof GameStep];

const GamePage: React.FC = () => {
    const {t} = useTranslation();
    const query = useQuery();
    const roomCode = query.get('roomCode') || '';
    const playerId = query.get('playerId') || '';

    const [step, setStep] = useState<GameStepType>(GameStep.countdown);
    const [current, setCurrent] = useState(0);
    const [, setAnswers] = useState<string[]>([]);
    const [questions, setQuestions] = useState<QuizQuestion[]>([]);
    const [players, setPlayers] = useState<Player[]>([]);
    const [lobby, setLobby] = useState<LobbyData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [, setShowRanking] = useState(false);
    const [answerGiven, setAnswerGiven] = useState(false); // Cevap verildi mi?
    const [showNext, setShowNext] = useState(false); // Next butonu gösterilsin mi?
    const [serverTimeLeft, setServerTimeLeft] = useState<number | null>(null);
    const [localTimeLeft, setLocalTimeLeft] = useState<number | null>(null);
    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

    useEffect(() => {
        const fetchGameData = async () => {
            setLoading(true);
            setError('');
            try {
                const qRes = await api.get(`/api/quiz/all-questions?roomCode=${roomCode}`);
                const questionsData = Array.isArray(qRes.data) ? qRes.data : [qRes.data];
                setQuestions(questionsData);
                const pRes = await api.get(`/api/game/players?roomCode=${roomCode}`);
                let playersData = Array.isArray(pRes.data) ? pRes.data : pRes.data.players;
                setPlayers(playersData || []);
            } catch (e: unknown) {
                setError('Veriler alınamadı.');
            } finally {
                setLoading(false);
            }
        };
        if (roomCode) fetchGameData().then(() =>
            setStep(GameStep.countdown)
        );
    }, [roomCode]);

    // Oyun başlatma geri sayımından sonra ilk soruya geç
    const handleCountdownComplete = () => setStep(GameStep.question);

    // Soru cevaplandığında
    const handleAnswer = async (answer: string, answerTime: number) => {
        setAnswers(prev => [...prev, answer]);
        setAnswerGiven(true);
        setShowNext(false);
        setShowRanking(true);
        // currentQuestion değişkeni kaldırıldı (kullanılmıyor)
        // Skor hesaplama kaldırıldı, backend hesaplayacak
        try {
            await api.post(`/api/game/answer`, {
                roomCode,
                playerId,
                answer,
                answerTime
            });
            // Skor ve oyuncu listesini güncelle
            const pRes = await api.get(`/api/game/players?roomCode=${roomCode}`);
            let playersData = Array.isArray(pRes.data) ? pRes.data : pRes.data.players;
            setPlayers(playersData || []);
        } catch (e) {
            console.error('Error sending answer:', e);
        }
        setShowNext(true); // Next butonunu göster
    };

    // Next butonuna basınca bir sonraki soruya geç
    const handleNext = async () => {
        setAnswerGiven(false);
        setShowNext(false);
        setShowRanking(false);
        if (current < questions.length - 1) {
            // Backend'e sonraki soruya geçildiğini bildir (query parametreleri ile)
            try {
                await api.post(`/api/game/next?roomCode=${roomCode}&playerId=${playerId}`);
            } catch (e) {
                console.error('Error sending next:', e);
            }
            // Backend polling ile state güncellenecek
        } else {
            setStep(GameStep.final);
        }
    };

    // Backend'den gelen state'i doğrudan sayı olarak kullan
    useEffect(() => {
        if (!roomCode || !playerId) return;
        let isMounted = true;
        const fetchState = async () => {
            try {
                const res = await api.get(`/api/game/state?roomCode=${roomCode}&playerId=${playerId}`);
                if (!isMounted) return;
                setStep(typeof res.data.state === 'number' ? res.data.state : GameStep.lobby);
                setCurrent(res.data.currentQuestionIndex || 0);
                setServerTimeLeft(res.data.remainingTime);
                setLocalTimeLeft(res.data.remainingTime);
                if (Array.isArray(res.data.questions) && res.data.questions.length > 0) {
                    setQuestions(res.data.questions);
                }
                if (Array.isArray(res.data.players) && res.data.players.length > 0) {
                    setPlayers(res.data.players.map((p: Partial<Player> & { playerId?: string }) => ({
                        ...p,
                        id: p.playerId ?? p.id ?? '',
                        name: p.name ?? '',
                        score: p.score ?? 0
                    })));
                } else if (Array.isArray(res.data.scores) && res.data.scores.length > 0) {
                    setPlayers(res.data.scores.map((s: { playerId: string; name: string; score: number }) => ({
                        id: s.playerId,
                        name: s.name,
                        score: s.score
                    })));
                }
                // Lobby bilgilerini güncelle
                if (res.data.lobby) {
                    setLobby(res.data.lobby);
                }
            } catch (e) {
                // Hata yönetimi
            }
        };
        fetchState();
        intervalRef.current = setInterval(() => {
            fetchState();
        }, 1000);
        return () => {
            isMounted = false;
            if (intervalRef.current) clearInterval(intervalRef.current);
        };
    }, [roomCode, playerId]);

    // Local sayaç sadece serverTimeLeft değiştiğinde baştan başlar
    useEffect(() => {
        if (serverTimeLeft == null) return;
        setLocalTimeLeft(serverTimeLeft);
        if (serverTimeLeft <= 0) return;
        const timer = setInterval(() => {
            setLocalTimeLeft(prev => (prev && prev > 0 ? prev - 1 : 0));
        }, 1000);
        return () => clearInterval(timer);
    }, [serverTimeLeft]);

    // Sıralama için oyuncuları skora göre sırala
    const sortedPlayers = [...players].sort((a, b) => (b.score ?? 0) - (a.score ?? 0)).map((p) => ({
        ...p,
        isYou: p.id === playerId || p.name === playerId,
    }));

    if (!roomCode || !playerId) {
        return <Alert severity="error">{t('error')}: roomCode/playerName missing</Alert>;
    }
    if (loading) {
        return <Alert severity="info">{t('loading')}</Alert>;
    }
    if (error) {
        return <Alert severity="error">{error}</Alert>;
    }

    return (
        <Box maxWidth={600} mx="auto" mt={6}>
            {/* Skorunuz kutusu kaldırıldı, çünkü userScore artık yok */}
            {/* Cevap verildiyse liderlik tablosunu sorunun üstünde göster */}
            {step === GameStep.answer_reveal && (
                <Box className="ranking-animate" mb={3}>
                    <GameRanking players={sortedPlayers}/>
                    {showNext && (
                        <Box mt={2} textAlign="center">
                            <button onClick={handleNext} className="next-btn" style={{
                                padding: '12px 32px',
                                fontSize: 20,
                                background: '#1976d2',
                                color: '#fff',
                                border: 'none',
                                borderRadius: 8,
                                cursor: 'pointer',
                                fontWeight: 700,
                                boxShadow: '0 2px 8px #1976d233',
                                transition: 'background 0.2s'
                            }}>
                                Sonraki Soru
                            </button>
                        </Box>
                    )}
                </Box>
            )}
            {/* Soru kutusu her zaman ekranda, cevap verildiyse animasyon class'ı ekle */}
            {step === GameStep.countdown && <GameStartCountdown onComplete={handleCountdownComplete}/>}
            {(step === GameStep.question || answerGiven) && (
                !questions[current] ? (
                    <Alert severity="error">Soru bulunamadı (index: {current})</Alert>
                ) : (
                    <Box className={answerGiven ? "question-animate" : undefined}>
                        <GameQuestion
                            question={questions[current]?.english}
                            options={questions[current]?.options}
                            onAnswer={handleAnswer}
                            timeLimit={lobby?.questionDuration ?? 60}
                            timeLeft={localTimeLeft ?? 0}
                            answered={step === GameStep.answer_reveal}
                            correctMeaning={questions[current]?.correctMeaning}
                            key={current}
                        />
                    </Box>
                )
            )}
            {step === GameStep.final && <GameFinal players={sortedPlayers}/>}
            {step === GameStep.lobby && (
                <Alert severity="info">{t('gameStarting')}</Alert>
            )}
            {step === GameStep.answer_reveal && !showNext && (
                <Alert severity="info">{t('loading')}</Alert>
            )}
        </Box>
    );
};

export default GamePage;
