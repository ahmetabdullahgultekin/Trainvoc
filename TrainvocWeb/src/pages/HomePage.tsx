import React from 'react';
import {Box, Divider, Fade, Paper, Typography, useTheme} from '@mui/material';
import {useTranslation} from 'react-i18next';
import GroupIcon from '@mui/icons-material/Group';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import RocketLaunchIcon from '@mui/icons-material/RocketLaunch';
import Grid from '@mui/material/Grid';
import Lottie from 'lottie-react';
import rocketAnim from '../animations/rocket.json';

const HomePage: React.FC = () => {
    useTranslation();
    const theme = useTheme();

    return (
        <Box
            maxWidth={{xs: '100%', md: 1100}}
            mx="auto"
            mt={{xs: 2, md: 4}}
            px={{xs: 1, sm: 2, md: 0}}
            sx={{
                background: `linear-gradient(135deg, ${theme.palette.primary.light} 0%, #e3f2fd 100%)`,
                borderRadius: {xs: 2, md: 6},
                boxShadow: 3,
                overflow: 'hidden',
                position: 'relative',
            }}
        >
            <Fade in timeout={800}>
                <Paper elevation={4} sx={{
                    p: {xs: 2, sm: 3, md: 4},
                    borderRadius: {xs: 2, sm: 4},
                    mb: 4,
                    position: 'relative',
                    overflow: 'hidden'
                }}>
                    <Box sx={{
                        position: 'absolute',
                        right: 0,
                        top: 0,
                        width: {xs: 120, sm: 180},
                        opacity: 0.18,
                        zIndex: 0
                    }}>
                        <Lottie animationData={rocketAnim} loop={true}/>
                    </Box>
                    <Typography variant="h3" align="center" color="primary.main" fontWeight={700} gutterBottom
                                sx={{fontSize: {xs: 28, sm: 36, md: 40}, zIndex: 1, position: 'relative'}}>
                        🚀 TrainVoc
                    </Typography>
                    <Typography variant="h6" align="center" color="text.secondary" mb={4}
                                sx={{fontSize: {xs: 16, sm: 20}, zIndex: 1, position: 'relative'}}>
                        İngilizce-Türkçe kelime ezberini eğlenceli, sosyal ve sürdürülebilir hale getiren yeni nesil
                        platform.
                    </Typography>
                    <Divider sx={{mb: 4, zIndex: 1, position: 'relative'}}/>
                    <Grid container spacing={2} justifyContent="center" sx={{zIndex: 1, position: 'relative'}}>
                        <Grid sx={{display: {xs: 'none', sm: 'block'}}}>
                            <Fade in timeout={1000}><Paper elevation={2}
                                                           sx={{
                                                               p: {xs: 2, sm: 3},
                                                               textAlign: 'center',
                                                               borderRadius: 3,
                                                               transition: 'transform 0.3s',
                                                               '&:hover': {transform: 'scale(1.05)', boxShadow: 6},
                                                           }}>
                                <GroupIcon color="primary" sx={{fontSize: {xs: 36, sm: 48}}}/>
                                <Typography variant="h6" fontWeight={600} mt={2} sx={{fontSize: {xs: 15, sm: 18}}}>Sosyal
                                    Oyun</Typography>
                                <Typography color="text.secondary" sx={{fontSize: {xs: 13, sm: 15}}}>Arkadaşlarınla veya
                                    toplulukla yarış!</Typography>
                            </Paper></Fade>
                        </Grid>
                        <Grid sx={{display: {xs: 'none', sm: 'block'}}}>
                            <Fade in timeout={1200}><Paper elevation={2}
                                                           sx={{
                                                               p: {xs: 2, sm: 3},
                                                               textAlign: 'center',
                                                               borderRadius: 3,
                                                               transition: 'transform 0.3s',
                                                               '&:hover': {transform: 'scale(1.05)', boxShadow: 6},
                                                           }}>
                                <EmojiEventsIcon color="warning" sx={{fontSize: {xs: 36, sm: 48}}}/>
                                <Typography variant="h6" fontWeight={600} mt={2} sx={{fontSize: {xs: 15, sm: 18}}}>Liderlik
                                    Tablosu</Typography>
                                <Typography color="text.secondary" sx={{fontSize: {xs: 13, sm: 15}}}>En iyi oyuncularla
                                    rekabet et, skorunu yükselt!</Typography>
                            </Paper></Fade>
                        </Grid>
                        <Grid sx={{display: {xs: 'none', sm: 'block'}}}>
                            <Fade in timeout={1400}><Paper elevation={2}
                                                           sx={{
                                                               p: {xs: 2, sm: 3},
                                                               textAlign: 'center',
                                                               borderRadius: 3,
                                                               transition: 'transform 0.3s',
                                                               '&:hover': {transform: 'scale(1.05)', boxShadow: 6},
                                                           }}>
                                <RocketLaunchIcon color="success" sx={{fontSize: {xs: 36, sm: 48}}}/>
                                <Typography variant="h6" fontWeight={600} mt={2} sx={{fontSize: {xs: 15, sm: 18}}}>Hızlı
                                    ve Modern</Typography>
                                <Typography color="text.secondary" sx={{fontSize: {xs: 13, sm: 15}}}>Her cihazda hızlı,
                                    modern ve erişilebilir deneyim.</Typography>
                            </Paper></Fade>
                        </Grid>
                    </Grid>
                </Paper>
            </Fade>
            <Fade in timeout={1200}>
                <Box>
                    {/* Aktif Odalar ve RoomCard kaldırıldı */}
                </Box>
            </Fade>
        </Box>
    );
};

export default HomePage;
