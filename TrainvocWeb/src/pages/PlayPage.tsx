import React from 'react';
import {Box, Paper, Typography} from '@mui/material';

const PlayPage: React.FC = () => {
    return (
        <Box maxWidth={{xs: '100%', sm: 700}} mx="auto" mt={{xs: 2, md: 6}} px={{xs: 1, sm: 0}}>
            <Paper elevation={4} sx={{p: {xs: 2, sm: 4}, borderRadius: {xs: 2, sm: 4}}}>
                <Typography variant="h4" fontWeight={700} gutterBottom sx={{fontSize: {xs: 22, sm: 28}}}>Oyun
                    Alanı</Typography>
                <Typography mb={2} sx={{fontSize: {xs: 15, sm: 17}}}>
                    Buradan TrainVoc çok oyunculu oyun alanına geçiş yapabilirsin. Oyun kurabilir, mevcut bir oyuna
                    katılabilir veya lobiye girebilirsin. Oyun başladığında, sadece oyun ekranı ve açılır/kapanır bir
                    yan menü (sidebar) görünür.
                </Typography>
                <Typography mb={2} sx={{fontSize: {xs: 15, sm: 17}}} component="div">
                    Oyun alanında:
                    <ul style={{margin: 0, paddingLeft: 18, fontSize: 15}}>
                        <li>Oyun kurabilir veya mevcut bir oyuna katılabilirsin.</li>
                        <li>Lobiye girdiğinde çıkış için onay gerekir.</li>
                        <li>Host başlatınca 3'ten geriye sayar ve oyun başlar.</li>
                        <li>Her soru sonunda ilk 3 vurgulu sıralama ve güncel liderlik tablosu gösterilir.</li>
                        <li>Oyun bitince final tablo gösterilir.</li>
                    </ul>
                </Typography>
                <Typography color="text.secondary" sx={{fontSize: {xs: 14, sm: 15}}}>
                    Oyun alanına başlamak için aşağıdaki menüyü kullanabilirsin.
                </Typography>
            </Paper>
        </Box>
    );
};

export default PlayPage;
