import React, {useEffect, useRef, useState} from 'react';
import {Box, Button, LinearProgress, Typography} from '@mui/material';

/** MUI Button color options */
type ButtonColor = 'inherit' | 'primary' | 'secondary' | 'success' | 'error' | 'info' | 'warning';

interface GameQuestionProps {
    question: string;
    options: string[];
    onAnswer: (answer: string, answerTime: number) => void;
    timeLimit: number; // toplam süre (sabit)
    timeLeft: number; // kalan süre (her tick değişir)
    answered?: boolean;
    correctMeaning?: string;
    selectedAnswer?: string | null;
}

const GameQuestion: React.FC<GameQuestionProps> = ({
                                                       question,
                                                       options,
                                                       onAnswer,
                                                       timeLimit,
                                                       timeLeft: initialTimeLeft,
                                                       answered,
                                                       correctMeaning,
                                                       selectedAnswer
                                                   }) => {
    const [selected, setSelected] = useState<string | null>(selectedAnswer ?? null);
    // Local sayaç (frontend'de akıcı geri sayım ve bar için)
    const [localTimeLeft, setLocalTimeLeft] = useState<number>(initialTimeLeft);
    const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

    // Progress bar ve sayaç sadece ilk renderda başlasın, backend'den gelen timeLeft değiştikçe sıfırlansın
    useEffect(() => {
        setLocalTimeLeft(initialTimeLeft);
    }, [initialTimeLeft]);

    // Sayaç başlatıcı ve durdurucu
    useEffect(() => {
        if (answered || selected !== null) return;
        setLocalTimeLeft(initialTimeLeft); // Her yeni soru veya timeLeft değişiminde sıfırla
        if (timerRef.current) clearInterval(timerRef.current);
        let last = Date.now();
        timerRef.current = setInterval(() => {
            setLocalTimeLeft(prev => {
                const now = Date.now();
                const elapsed = (now - last) / 1000;
                last = now;
                const next = prev - elapsed;
                if (next <= 0) {
                    clearInterval(timerRef.current!);
                    return 0;
                }
                return next;
            });
        }, 50);
        return () => {
            if (timerRef.current) clearInterval(timerRef.current);
        };
    }, [initialTimeLeft, answered, selected]);

    // Süre bitince otomatik cevap
    useEffect(() => {
        if (localTimeLeft <= 0 && selected === null && !answered) {
            onAnswer('', timeLimit);
        }
    }, [localTimeLeft, selected, answered, onAnswer, timeLimit]);

    // localTimeLeft doğrudan progress bar için kullanılacak
    const progressValue = Math.max(0, Math.min(100, (localTimeLeft / timeLimit) * 100));

    const handleSelect = (opt: string) => {
        if (selected !== null) return;
        setSelected(opt);
        if (timerRef.current) clearInterval(timerRef.current);
        const usedTime = timeLimit - localTimeLeft;
        onAnswer(opt, usedTime);
    };

    return (
        <Box maxWidth={500} mx="auto" mt={6} p={3} bgcolor="#f9f9f9" borderRadius={3} boxShadow={2}>
            <Typography variant="h5" fontWeight={700} mb={2} color="#222">
                {question}
            </Typography>
            <LinearProgress variant="determinate" value={progressValue} sx={{mb: 3, transition: 'width 0.1s linear'}}/>
            <Typography variant="body2" color="#888" mb={2} textAlign="right">
                {Math.ceil(localTimeLeft)} sn
            </Typography>
            {options.map(opt => {
                let btnColor: ButtonColor = 'primary';
                let btnBg = '#fff';
                let btnText = '#222';
                if (answered && selected) {
                    if (opt === selected && selected !== correctMeaning) {
                        // Yanlış işaretlenen şık kırmızı
                        btnColor = 'error';
                        btnBg = '#e53935';
                        btnText = '#fff';
                    } else if (opt === correctMeaning) {
                        // Doğru şık yeşil
                        btnColor = 'success';
                        btnBg = '#4caf50';
                        btnText = '#fff';
                    } else {
                        // Diğerleri gri
                        btnColor = 'inherit';
                        btnBg = '#eee';
                        btnText = '#888';
                    }
                } else if (selected === opt) {
                    // Sadece cevap verilmemişse işaretli şık mavi
                    btnColor = 'primary';
                    btnBg = '#fff';
                    btnText = '#222';
                }
                return (
                    <Button
                        key={opt}
                        variant={selected === opt ? 'contained' : 'outlined'}
                        color={btnColor}
                        fullWidth
                        sx={{
                            mb: 2,
                            fontWeight: 600,
                            fontSize: 18,
                            backgroundColor: btnBg,
                            color: btnText,
                            borderColor: btnColor === 'error' ? '#e53935' : btnColor === 'success' ? '#4caf50' : '#eee',
                            transition: 'all 0.3s',
                        }}
                        onClick={() => {
                            if (!answered && selected === null && localTimeLeft > 0) {
                                handleSelect(opt);
                            }
                        }}
                        disabled={(!answered && (selected !== null || localTimeLeft === 0))}
                    >
                        {opt}
                    </Button>
                );
            })}
            <Typography mt={2} color="#1976d2" fontWeight={700} fontSize={20} textAlign="center">
                Kalan süre: {Math.ceil(localTimeLeft)} sn
            </Typography>
        </Box>
    );
};

export default GameQuestion;
