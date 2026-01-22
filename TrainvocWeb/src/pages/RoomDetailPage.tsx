import React, {useEffect, useState} from 'react';
import {Alert, Box, List, ListItem, ListItemText, Typography} from '@mui/material';
import {useTranslation} from 'react-i18next';
import {useParams} from 'react-router-dom';

import SharedButton from '../components/shared/Button';
import api from '../api';
import type {GameRoom, Player} from '../interfaces/game';

const RoomDetailPage = () => {
    const {t} = useTranslation();
    const {roomCode} = useParams();
    const [room, setRoom] = useState<GameRoom | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!roomCode) return;
        setLoading(true);
        setError('');
        setRoom(null);
        api.get(`/api/game/${roomCode}`)
            .then(res => setRoom(res.data))
            .catch(() => setError(t('error')))
            .finally(() => setLoading(false));
    }, [roomCode, t]);

    return (
        <Box maxWidth={{xs: '100%', sm: 500}} mx="auto" mt={{xs: 2, md: 6}} px={{xs: 1, sm: 0}}>
            <Typography variant="h4" fontWeight={700} gutterBottom
                        sx={{fontSize: {xs: 22, sm: 28}}}>{t('roomDetails')}</Typography>
            {loading && <Typography>{t('loading')}</Typography>}
            {error && <Alert severity="error">{error}</Alert>}
            {room && (
                <>
                    <Typography sx={{fontSize: {xs: 15, sm: 17}}}>{t('roomCode')}: <b>{room.roomCode}</b></Typography>
                    <Typography sx={{fontSize: {xs: 15, sm: 17}}}>{t('settings')}:</Typography>
                    <div>
                        <ul style={{paddingLeft: 18, fontSize: 15, marginBottom: 8}}>
                            <li>{t('questionCount') || 'Soru Sayısı'}: {room.totalQuestionCount}</li>
                            <li>{t('timePerQuestion') || 'Soru Süresi'}: {room.questionDuration}</li>
                        </ul>
                    </div>
                    <Typography mt={2} sx={{fontSize: {xs: 15, sm: 17}}}>{t('players') || 'Oyuncular'}:</Typography>
                    <List sx={{bgcolor: 'grey.50', borderRadius: 2, px: 1, py: 0.5, mb: 2}}>
                        {room.players?.map((p: Player) => (
                            <ListItem key={p.id || p.name} sx={{py: 0.5}}>
                                <ListItemText primary={p.name} sx={{fontSize: {xs: 14, sm: 16}}}/>
                            </ListItem>
                        ))}
                    </List>
                    <Box mt={3}>
                        <SharedButton variant="primary" style={{
                            width: '100%',
                            padding: '14px 0',
                            fontSize: 15
                        }}>{t('submit')}</SharedButton>
                    </Box>
                </>
            )}
        </Box>
    );
};

export default RoomDetailPage;
