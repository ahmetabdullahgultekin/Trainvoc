import React, {useEffect, useState} from 'react';
import {Alert, Box, Divider, Fade, Grid, Paper, TextField, Typography} from '@mui/material';
import {useTranslation} from 'react-i18next';
import SharedButton from '../components/shared/Button';
import api from '../api';
import {useLocation, useNavigate} from 'react-router-dom';
import RoomCard from '../components/shared/RoomCard';
import type {GameRoom} from '../interfaces/game';
import GroupIcon from '@mui/icons-material/Group';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import RocketLaunchIcon from '@mui/icons-material/RocketLaunch';
import useNick from '../components/shared/useNick.ts';
import useProfile from '../components/shared/useProfile';
import {hashPassword} from '../components/shared/hashPassword';

const JoinRoomPage = () => {
    const {t} = useTranslation();
    const [roomCode, setRoomCode] = useState('');
    const [playerName, setPlayerName] = useState('');
    const [player, setPlayer] = useState<any>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [rooms, setRooms] = useState<GameRoom[]>([]);
    const [joiningRoom, setJoiningRoom] = useState<string | null>(null);
    const navigate = useNavigate();
    const location = useLocation();
    const [nick, setNick] = useNick();
    const {avatar} = useProfile();
    const [roomPassword, setRoomPassword] = useState('');

    // Avatar id'si için örnek (profilde seçili olanı kullan):
    const avatarId = avatar ? avatar : (Math.floor(Math.random() * 10) + 1).toString();

    useEffect(() => {
        setLoading(true);
        api.get('/api/game/rooms')
            .then(res => setRooms(res.data))
            .catch(() => setRooms([]))
            .finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        // URL'den roomCode parametresini al ve inputa yaz
        const params = new URLSearchParams(location.search);
        const code = params.get('roomCode');
        if (code) setRoomCode(code);
    }, [location.search]);

    useEffect(() => {
        setPlayerName(nick);
    }, [nick]);

    const handleJoin = async () => {
        setLoading(true);
        setError('');
        setPlayer(null);
        try {
            // Önce odadaki oyuncu isimlerini kontrol et
            const roomRes = await api.get(`/api/game/${roomCode}`);
            const players = roomRes.data?.players || [];
            if (players.some((p: any) => p.name?.trim().toLowerCase() === playerName.trim().toLowerCase())) {
                setError('Bu isimle zaten bir oyuncu var, lobiye katılamazsınız.');
                setLoading(false);
                return;
            }
            setNick(playerName); // nick'i localStorage'a kaydet
            // Şifre hashle
            const hashedPassword = roomPassword ? await hashPassword(roomPassword) : '';
            // avatarId ve hashedPassword'ü gönder
            const res = await api.post(`/api/game/join?roomCode=${encodeURIComponent(roomCode)}&playerName=${encodeURIComponent(playerName)}&avatarId=${avatarId}` + (hashedPassword ? `&hashedPassword=${hashedPassword}` : ''));
            setPlayer(res.data);
            // Başarılı katılım sonrası lobi ekranına yönlendir
            setTimeout(() => {
                navigate(`/play/lobby?roomCode=${encodeURIComponent(roomCode)}&playerId=${encodeURIComponent(res.data.id)}`);
            }, 1000);
        } catch (e: any) {
            // Backend'den gelen hata mesajını kontrol et
            const apiError = e?.response?.data?.error;
            if (apiError === 'RoomPasswordRequired') {
                setError('Bu oda için şifre gereklidir.');
            } else if (apiError === 'InvalidRoomPassword') {
                setError('Şifre yanlış.');
            } else if (apiError === 'RoomNotFound') {
                setError('Oda bulunamadı.');
            } else {
                setError(t('error'));
            }
        } finally {
            setLoading(false);
        }
    };

    const handleRoomJoin = async (roomCode: string) => {
        setJoiningRoom(roomCode);
        setRoomCode(roomCode);
        await handleJoin();
        setJoiningRoom(null);
    };

    return (
        <Box maxWidth={1100} mx="auto" mt={4} px={{xs: 1, sm: 2, md: 0}}>
            <Fade in timeout={800}>
                <Paper elevation={4} sx={{p: {xs: 2, sm: 3, md: 4}, borderRadius: {xs: 2, sm: 4}, mb: 4}}>
                    <Typography variant="h2" align="center" color="primary.main" fontWeight={700} gutterBottom
                                sx={{fontSize: {xs: 24, sm: 32, md: 38}}}>
                        Odaya Katıl
                    </Typography>
                    <Typography variant="h5" align="center" color="text.secondary" mb={4}
                                sx={{fontSize: {xs: 15, sm: 20}}}>
                        Mevcut odalardan birine katılmak için oda kodunu gir ya da listeden bir odayı seç!
                    </Typography>
                    <Divider sx={{mb: 4}}/>
                    <Grid container columns={12} columnSpacing={2} justifyContent="center">
                        <Grid sx={{display: {xs: 'none', sm: 'block'}}}>
                            <Fade in timeout={1000}><Paper elevation={2}
                                                           sx={{
                                                               p: {xs: 2, sm: 3},
                                                               textAlign: 'center',
                                                               borderRadius: 3
                                                           }}>
                                <GroupIcon color="primary" sx={{fontSize: {xs: 36, sm: 48}}}/>
                                <Typography variant="h6" fontWeight={600} mt={2}
                                            sx={{fontSize: {xs: 15, sm: 18}}}>Topluluk</Typography>
                                <Typography color="text.secondary" sx={{fontSize: {xs: 13, sm: 15}}}>Birlikte öğren,
                                    birlikte yarış!</Typography>
                            </Paper></Fade>
                        </Grid>
                        <Grid sx={{display: {xs: 'none', sm: 'block'}}}>
                            <Fade in timeout={1200}><Paper elevation={2}
                                                           sx={{
                                                               p: {xs: 2, sm: 3},
                                                               textAlign: 'center',
                                                               borderRadius: 3
                                                           }}>
                                <EmojiEventsIcon color="warning" sx={{fontSize: {xs: 36, sm: 48}}}/>
                                <Typography variant="h6" fontWeight={600} mt={2}
                                            sx={{fontSize: {xs: 15, sm: 18}}}>Rekabet</Typography>
                                <Typography color="text.secondary" sx={{fontSize: {xs: 13, sm: 15}}}>En iyi oyuncularla
                                    yarış!</Typography>
                            </Paper></Fade>
                        </Grid>
                        <Grid sx={{display: {xs: 'none', sm: 'block'}}}>
                            <Fade in timeout={1400}><Paper elevation={2}
                                                           sx={{
                                                               p: {xs: 2, sm: 3},
                                                               textAlign: 'center',
                                                               borderRadius: 3
                                                           }}>
                                <RocketLaunchIcon color="success" sx={{fontSize: {xs: 36, sm: 48}}}/>
                                <Typography variant="h6" fontWeight={600} mt={2} sx={{fontSize: {xs: 15, sm: 18}}}>Hızlı
                                    Katılım</Typography>
                                <Typography color="text.secondary" sx={{fontSize: {xs: 13, sm: 15}}}>Odaya anında katıl,
                                    hemen başla!</Typography>
                            </Paper></Fade>
                        </Grid>
                    </Grid>
                </Paper>
            </Fade>
            <Box maxWidth={400} mx="auto" mt={6}>
                <Typography variant="h4" gutterBottom sx={{fontSize: {xs: 20, sm: 28}}}>{t('joinRoom')}</Typography>
                <TextField
                    label={t('roomCode')}
                    value={roomCode}
                    onChange={e => setRoomCode(e.target.value)}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label={t('playerName')}
                    value={playerName}
                    onChange={e => {
                        setPlayerName(e.target.value);
                        setNick(e.target.value);
                    }}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Oda Şifresi (varsa)"
                    type="password"
                    value={roomPassword}
                    onChange={e => setRoomPassword(e.target.value)}
                    fullWidth
                    margin="normal"
                    size="small"
                />
                <div style={{marginTop: 24}}>
                    <SharedButton variant="primary" onClick={handleJoin}
                                  disabled={loading || !roomCode || !playerName}>{t('submit')}</SharedButton>
                </div>
                {loading && <Typography>{t('loading')}</Typography>}
                {error && <Alert severity="error">{error}</Alert>}
                {player && (
                    <Alert severity="success" sx={{mt: 2}}>
                        {t('joinSuccess') || 'Odaya başarıyla katıldınız!'}<br/>
                        {t('playerName')}: <b>{player.name}</b>
                    </Alert>
                )}
            </Box>
            <Divider sx={{my: 4}}/>
            <Box maxWidth={900} mx="auto" mt={4}>
                <Typography variant="h5" fontWeight={700} mb={3} color="primary.main" sx={{fontSize: {xs: 18, sm: 24}}}>Aktif
                    Odalar</Typography>
                {rooms.length === 0 ? (
                    <Typography color="text.secondary">Şu anda aktif oda yok. Hemen bir oda oluştur!</Typography>
                ) : (
                    <Box display="grid" gridTemplateColumns={{xs: '1fr', sm: '1fr 1fr', md: '1fr 1fr 1fr'}} gap={3}>
                        {rooms.map((room, idx) => (
                            <RoomCard
                                key={room.roomCode}
                                room={room}
                                idx={idx}
                                t={t}
                                joiningRoom={joiningRoom}
                                onJoin={handleRoomJoin}
                            />
                        ))}
                    </Box>
                )}
            </Box>
        </Box>
    );
};

export default JoinRoomPage;
