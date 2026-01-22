import React from 'react';
import {Box, Button, Paper, Typography} from '@mui/material';

const MobileAppPage: React.FC = () => {
    return (
        <Box maxWidth={{xs: '100%', sm: 600}} mx="auto" mt={{xs: 2, md: 6}} px={{xs: 1, sm: 0}}>
            <Paper elevation={4} sx={{p: {xs: 2, sm: 4}, borderRadius: {xs: 2, sm: 4}}}>
                <Typography variant="h4" fontWeight={700} gutterBottom sx={{fontSize: {xs: 22, sm: 28}}}>Mobil
                    Uygulama</Typography>
                <Typography mb={2} sx={{fontSize: {xs: 15, sm: 17}}}>
                    TrainVoc'un mobil uygulaması ile kelime öğrenimini her yerde sürdürebilirsin! Uygulamamız Android
                    cihazlar için Play Store'da ücretsiz olarak sunulmaktadır.
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    href="https://play.google.com/store/apps/details?id=com.rollingcat.trainvoc"
                    target="_blank"
                    sx={{mb: 2, fontSize: {xs: 15, sm: 17}, py: 1.1}}
                >
                    Play Store'da Aç
                </Button>
                <Typography color="text.secondary" sx={{fontSize: {xs: 14, sm: 15}}}>
                    IOS uygulamamız çok yakında App Store'da olacak!
                </Typography>
            </Paper>
        </Box>
    );
};

export default MobileAppPage;
