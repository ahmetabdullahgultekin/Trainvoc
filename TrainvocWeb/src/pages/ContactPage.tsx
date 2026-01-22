import React from 'react';
import {Box, Fade, Paper, Typography, useTheme} from '@mui/material';
import Lottie from 'lottie-react';
import rocketAnim from '../animations/rocket.json';

const ContactPage: React.FC = () => {
    const theme = useTheme();
    return (
        <Fade in timeout={800}>
            <Box maxWidth={{xs: '100%', sm: 600}} mx="auto" mt={{xs: 2, md: 6}} px={{xs: 1, sm: 0}}
                 sx={{
                     background: `linear-gradient(135deg, #fffbe7 0%, ${theme.palette.primary.light} 100%)`,
                     borderRadius: {xs: 2, sm: 4},
                     boxShadow: 3,
                     position: 'relative',
                     overflow: 'hidden',
                 }}>
                <Box sx={{
                    position: 'absolute',
                    left: 0,
                    bottom: 0,
                    width: {xs: 100, sm: 140},
                    opacity: 0.18,
                    zIndex: 0
                }}>
                    <Lottie animationData={rocketAnim} loop={true}/>
                </Box>
                <Paper elevation={4}
                       sx={{p: {xs: 2, sm: 4}, borderRadius: {xs: 2, sm: 4}, position: 'relative', zIndex: 1}}>
                    <Typography variant="h4" fontWeight={700} gutterBottom
                                sx={{fontSize: {xs: 22, sm: 28}}}>İletişim</Typography>
                    <Typography mb={2} sx={{fontSize: {xs: 15, sm: 17}}}>
                        <b>Rolling Cat Software</b> olarak TrainVoc platformunu geliştiriyoruz. Her türlü soru, öneri ve
                        iş
                        birliği için bize ulaşabilirsiniz.
                    </Typography>
                    <Typography mb={1} sx={{fontSize: {xs: 15, sm: 17}}}>
                        <b>E-posta:</b> info@rollingcatsoftware.com
                    </Typography>
                    <Typography mb={1} sx={{fontSize: {xs: 15, sm: 17}}}>
                        <b>Web:</b> www.rollingcatsoftware.com
                    </Typography>
                    <Typography mb={1} sx={{fontSize: {xs: 15, sm: 17}}}>
                        <b>Adres:</b> İstanbul, Türkiye
                    </Typography>
                    <Typography mt={3} color="text.secondary" sx={{fontSize: {xs: 14, sm: 15}}}>
                        Geri bildirimleriniz bizim için çok değerli!
                    </Typography>
                </Paper>
            </Box>
        </Fade>
    );
};

export default ContactPage;
