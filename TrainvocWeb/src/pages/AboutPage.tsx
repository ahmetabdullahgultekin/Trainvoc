import React from 'react';
import {Box, Fade, Paper, Typography, useTheme} from '@mui/material';
import Lottie from 'lottie-react';
import rocketAnim from '../animations/rocket.json';

const AboutPage: React.FC = () => {
    const theme = useTheme();
    return (
        <Fade in timeout={800}>
            <Box maxWidth={{xs: '100%', sm: 700}} mx="auto" mt={{xs: 2, md: 6}} px={{xs: 1, sm: 0}}
                 sx={{
                     background: `linear-gradient(135deg, ${theme.palette.primary.light} 0%, #e3f2fd 100%)`,
                     borderRadius: {xs: 2, sm: 4},
                     boxShadow: 3,
                     position: 'relative',
                     overflow: 'hidden',
                 }}>
                <Box sx={{position: 'absolute', right: 0, top: 0, width: {xs: 100, sm: 160}, opacity: 0.15, zIndex: 0}}>
                    <Lottie animationData={rocketAnim} loop={true}/>
                </Box>
                <Paper elevation={4}
                       sx={{p: {xs: 2, sm: 4}, borderRadius: {xs: 2, sm: 4}, position: 'relative', zIndex: 1}}>
                    <Typography variant="h4" fontWeight={700} gutterBottom
                                sx={{fontSize: {xs: 22, sm: 28}}}>Hakkında</Typography>
                    <Typography mb={2} sx={{fontSize: {xs: 15, sm: 17}}}>
                        TrainVoc, İngilizce-Türkçe kelime öğrenimini eğlenceli, sosyal ve sürdürülebilir hale getirmek
                        için
                        geliştirilmiş yeni nesil bir platformdur. Amacımız, kullanıcıların hem bireysel hem de çok
                        oyunculu
                        modlarda kelime bilgisini geliştirmesini sağlamak ve öğrenmeyi bir oyun deneyimine
                        dönüştürmektir.
                    </Typography>
                    <Typography mb={2} sx={{fontSize: {xs: 15, sm: 17}}}>
                        Platformumuzda arkadaşlarınla yarışabilir, liderlik tablosunda yerini görebilir ve mobil
                        uygulamamız
                        sayesinde her yerde pratik yapabilirsin. Rolling Cat Software ekibi olarak, kullanıcılarımızın
                        geri
                        bildirimleriyle platformumuzu sürekli geliştiriyoruz.
                    </Typography>
                    <Typography color="text.secondary" sx={{fontSize: {xs: 14, sm: 15}}}>
                        Daha fazla bilgi ve güncellemeler için bizi takip etmeye devam et!
                    </Typography>
                </Paper>
            </Box>
        </Fade>
    );
};

export default AboutPage;
