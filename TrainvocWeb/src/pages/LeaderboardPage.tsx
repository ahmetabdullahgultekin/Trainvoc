import React, {useState} from 'react';
import {
    Box,
    Fade,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography,
    useTheme
} from '@mui/material';
import {useTranslation} from 'react-i18next';
import SharedButton from '../components/shared/Button';
import api from '../api';
import Lottie from 'lottie-react';
import rocketAnim from '../animations/rocket.json';

interface Player {
    id: string;
    name: string;
    score: number;
}

const LeaderboardPage: React.FC = () => {
    const {t} = useTranslation();
    const [roomCode, setRoomCode] = useState('');
    const [players, setPlayers] = useState<Player[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const theme = useTheme();

    const fetchLeaderboard = async () => {
        setLoading(true);
        setError('');
        try {
            const res = await api.get(`/api/leaderboard?roomCode=${roomCode}`);
            setPlayers(res.data);
        } catch (e) {
            setError(t('error'));
            setPlayers([]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Fade in timeout={800}>
            <Box maxWidth={{xs: '100%', sm: 500}} mx="auto" mt={{xs: 2, md: 6}} px={{xs: 1, sm: 0}}
                 sx={{
                     background: `linear-gradient(135deg, #fffbe7 0%, ${theme.palette.primary.light} 100%)`,
                     borderRadius: {xs: 2, sm: 4},
                     boxShadow: 3,
                     position: 'relative',
                     overflow: 'hidden',
                 }}>
                <Box sx={{position: 'absolute', right: 0, top: 0, width: {xs: 100, sm: 140}, opacity: 0.15, zIndex: 0}}>
                    <Lottie animationData={rocketAnim} loop={true}/>
                </Box>
                <Typography variant="h4" gutterBottom sx={{
                    fontSize: {xs: 22, sm: 28},
                    zIndex: 1,
                    position: 'relative'
                }}>{t('leaderboard')}</Typography>
                <Box display="flex" gap={2} mb={3} flexDirection={{xs: 'column', sm: 'row'}}
                     sx={{zIndex: 1, position: 'relative'}}>
                    <TextField
                        label={t('roomCode')}
                        value={roomCode}
                        onChange={e => setRoomCode(e.target.value)}
                        fullWidth
                        size="small"
                    />
                    <SharedButton variant="primary" onClick={fetchLeaderboard}
                                  style={{minWidth: 120}}>{t('submit')}</SharedButton>
                </Box>
                {error && <Typography color="error">{error}</Typography>}
                {loading && <Typography>{t('loading')}</Typography>}
                {players.length > 0 && (
                    <Fade in timeout={1000}>
                        <TableContainer component={Paper} sx={{
                            borderRadius: {xs: 2, sm: 4},
                            zIndex: 1,
                            position: 'relative',
                            boxShadow: 6
                        }}>
                            <Table size="small">
                                <TableHead>
                                    <TableRow>
                                        <TableCell sx={{fontWeight: 700, fontSize: {xs: 14, sm: 16}}}>#</TableCell>
                                        <TableCell
                                            sx={{
                                                fontWeight: 700,
                                                fontSize: {xs: 14, sm: 16}
                                            }}>{t('playerName')}</TableCell>
                                        <TableCell
                                            sx={{fontWeight: 700, fontSize: {xs: 14, sm: 16}}}>{t('score')}</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {players.map((player, idx) => (
                                        <TableRow key={player.id} sx={{
                                            transition: 'background 0.2s',
                                            '&:hover': {background: theme.palette.action.hover}
                                        }}>
                                            <TableCell sx={{fontSize: {xs: 13, sm: 15}}}>{idx + 1}</TableCell>
                                            <TableCell sx={{fontSize: {xs: 13, sm: 15}}}>{player.name}</TableCell>
                                            <TableCell sx={{fontSize: {xs: 13, sm: 15}}}>{player.score}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Fade>
                )}
            </Box>
        </Fade>
    );
};

export default LeaderboardPage;
