import React, {useState} from 'react';
import {Avatar, Box, Button, Grid, Paper, TextField, Typography} from '@mui/material';
import useProfile from '../components/shared/useProfile.ts';

const avatarList = [
    '🦊', '🐱', '🐶', '🐵', '🐸', '🐼', '🐧', '🐯', '🦁', '🐮', '🐨', '🐰', '🐻', '🐷', '🐔', '🦄', '🐙', '🐢', '🐳', '🐝'
];

const ProfilePage: React.FC = () => {
    const {nick, setNick, avatar, setAvatar} = useProfile();
    // avatar int id olarak tutuluyor
    const [tempNick, setTempNick] = useState(nick);
    const [tempAvatar, setTempAvatar] = useState(
        avatar !== undefined && avatar !== null && avatar !== '' && !isNaN(Number(avatar))
            ? Number(avatar)
            : 0
    );

    const handleSave = () => {
        setNick(tempNick);
        setAvatar(tempAvatar.toString()); // int id olarak kaydet
    };

    return (
        <Box maxWidth={{xs: '100%', sm: 400}} mx="auto" mt={{xs: 2, md: 6}} px={{xs: 1, sm: 0}}>
            <Paper elevation={4} sx={{p: {xs: 2, sm: 4}, borderRadius: {xs: 2, sm: 4}}}>
                <Typography variant="h4" fontWeight={700} gutterBottom sx={{fontSize: {xs: 22, sm: 28}}}>Profilini
                    Düzenle</Typography>
                <Box display="flex" flexDirection="column" alignItems="center" mb={3}>
                    <Avatar sx={{width: 80, height: 80, fontSize: 48, mb: 2}}>
                        {tempAvatar}
                    </Avatar>
                    <Grid container spacing={1} justifyContent="center">
                        {avatarList.map((a, index) => (
                            <Grid sx={{display: 'flex', justifyContent: 'center'}} key={a}>
                                <Button onClick={() => setTempAvatar(index)}
                                        variant={tempAvatar === index ? 'contained' : 'outlined'}
                                        sx={{minWidth: 36, fontSize: 22, px: 0, py: 0.5}}>
                                    {a}
                                </Button>
                            </Grid>
                        ))}
                    </Grid>
                </Box>
                <TextField
                    label="Nick"
                    value={tempNick}
                    onChange={e => setTempNick(e.target.value)}
                    fullWidth
                    margin="normal"
                    size="small"
                />
                <Button variant="contained" color="primary" fullWidth sx={{mt: 2, py: 1.2, fontSize: {xs: 15, sm: 17}}}
                        onClick={handleSave}>
                    Kaydet
                </Button>
            </Paper>
        </Box>
    );
};

export default ProfilePage;
