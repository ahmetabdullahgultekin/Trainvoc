import React from 'react';
import {Avatar, Box, Grid, Paper, Typography} from '@mui/material';
import type {Player} from '../interfaces/game';

interface GameRankingProps {
    players: Player[]; // Sıralı, en yüksekten en düşüğe
}

const medalColors = ['#ffd700', '#c0c0c0', '#cd7f32'];

const GameRanking: React.FC<GameRankingProps> = ({players}) => {
    return (
        <Box maxWidth={500} mx="auto" mt={6}>
            <Paper elevation={4} sx={{p: 4, borderRadius: 4}}>
                <Typography variant="h5" fontWeight={700} mb={2} align="center">Soru Sıralaması</Typography>
                <Grid container direction="column" spacing={2}>
                    {players.map((player, idx) => (
                        <Grid key={player.id}>
                            <Box display="flex" alignItems="center" bgcolor={player.isYou ? '#e3f2fd' : 'transparent'}
                                 p={1} borderRadius={2}>
                                <Avatar sx={{bgcolor: idx < 3 ? medalColors[idx] : '#bdbdbd', mr: 2}}>
                                    {idx < 3 ? idx + 1 : null}
                                </Avatar>
                                <Typography fontWeight={player.isYou ? 700 : 500}
                                            color={idx < 3 ? 'primary' : 'text.primary'}>
                                    {player.name} {player.isYou && <span style={{color: '#1976d2'}}>(sen)</span>}
                                </Typography>
                                <Box flexGrow={1}/>
                                <Typography fontWeight={700}>{player.score} puan</Typography>
                            </Box>
                        </Grid>
                    ))}
                </Grid>
            </Paper>
        </Box>
    );
};

export default GameRanking;
