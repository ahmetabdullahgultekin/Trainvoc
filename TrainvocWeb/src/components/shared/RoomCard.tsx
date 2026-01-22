import React from 'react';
import {Avatar, Box, Button, Fade, Paper, Typography} from '@mui/material';
import type {GameRoom} from '../../interfaces/game';

interface RoomCardProps {
    room: GameRoom;
    idx: number;
    t: (key: string) => string;
    joiningRoom: string | null;
    onJoin: (roomCode: string) => void;
}

// RoomCard'ın içini güncelledim, tüm metinler ve bilgiler burada tek tip gösterilecek şekilde ayarlandı.
const RoomCard: React.FC<RoomCardProps> = ({room, idx, t, joiningRoom, onJoin}) => {
    // Parametreleri insancıllaştır ve çevir
    // Seviye ve parametre çevirileri
    const levelMap: Record<string, string> = {
        a1: t('levelA1') || 'Başlangıç (A1)',
        a2: t('levelA2') || 'Temel (A2)',
        b1: t('levelB1') || 'Orta (B1)',
        b2: t('levelB2') || 'İleri-Orta (B2)',
        c1: t('levelC1') || 'İleri (C1)',
        c2: t('levelC2') || 'Usta (C2)',
        easy: t('easy') || 'Kolay',
        medium: t('medium') || 'Orta',
        hard: t('hard') || 'Zor',
    };
    let levelLabel = room.level || '-';
    if (room.level && levelMap[room.level.toLowerCase()]) {
        levelLabel = levelMap[room.level.toLowerCase()];
    }
    const playersLabel = room.players?.length?.toLocaleString() || '1';

    return (
        <Fade in timeout={600 + idx * 100}>
            <Paper elevation={4} sx={{
                p: 0,
                borderRadius: 4,
                boxShadow: '0 4px 24px 0 rgba(80,80,180,0.10)',
                overflow: 'hidden',
                display: 'flex',
                flexDirection: 'column',
                minHeight: 240,
                height: '100%',
                border: '1.5px solid',
                borderColor: 'primary.light',
                transition: 'transform 0.2s, box-shadow 0.2s',
                ':hover': {
                    transform: 'translateY(-4px) scale(1.03)',
                    boxShadow: '0 8px 32px 0 rgba(80,80,180,0.18)',
                    borderColor: 'primary.main',
                },
            }}>
                <Box sx={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 2,
                    bgcolor: 'primary.50',
                    px: 3,
                    py: 2,
                    borderBottom: '1px solid',
                    borderColor: 'primary.light',
                }}>
                    <Avatar sx={{
                        bgcolor: 'primary.main',
                        width: 56,
                        height: 56,
                        fontWeight: 700,
                        fontSize: 22
                    }}>{room.roomCode?.slice(0, 2) || '?'}</Avatar>
                    <Box>
                        <Typography variant="subtitle2" color="primary" fontWeight={700} letterSpacing={1}
                                    mb={0.5}>{t('roomCode') || 'Oda Kodu'}: <b>{room.roomCode || '-'}</b></Typography>
                        <Typography color="text.secondary"
                                    fontSize={14}>{t('players') || t('playersLabel') || 'Oyuncu Sayısı'}: {playersLabel}</Typography>
                    </Box>
                </Box>
                <Box sx={{flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between', p: 3}}>
                    <Box>
                        <Typography color="text.secondary" fontSize={15} mb={1}>
                            {t('level') || t('levelLabel') || 'Seviye'}: <b>{levelLabel}</b>
                        </Typography>
                        <Typography color="text.secondary" fontSize={15}>
                            {t('questionCount') || t('questionCountLabel') || 'Soru Sayısı'}: <b>{room.totalQuestionCount ?? '-'} {t('questions') ? t('questions') : 'Soru'}</b> | {t('timePerQuestion') || t('timePerQuestionLabel') || 'Süre'}: <b>{room.questionDuration ? `${room.questionDuration} ${t('seconds') || 'saniye'}` : '-'}</b>
                        </Typography>
                    </Box>
                    <Button
                        variant="contained"
                        color="primary"
                        fullWidth
                        sx={{mt: 2, fontWeight: 700, fontSize: 16}}
                        disabled={joiningRoom === room.roomCode}
                        onClick={() => onJoin(room.roomCode)}
                    >
                        {joiningRoom === room.roomCode ? t('joining') || 'Katılıyor...' : t('join') || 'Katıl'}
                    </Button>
                </Box>
            </Paper>
        </Fade>
    );
};

export default RoomCard;
