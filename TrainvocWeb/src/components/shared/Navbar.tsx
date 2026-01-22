import React from 'react';
import styles from './Navbar.module.css';
import {
    AppBar,
    Box,
    Button,
    Drawer,
    IconButton,
    List,
    ListItem,
    ListItemText,
    MenuItem,
    Select,
    type SelectChangeEvent,
    Toolbar,
    Typography
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import {Link as RouterLink} from 'react-router-dom';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import {keyframes} from '@emotion/react';

const playPulse = keyframes`
  0% { box-shadow: 0 0 0 0 rgba(33, 150, 243, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(33, 150, 243, 0); }
  100% { box-shadow: 0 0 0 0 rgba(33, 150, 243, 0); }
`;

const Navbar: React.FC = () => {
    const [lang, setLang] = React.useState(localStorage.getItem('lang') || 'tr');
    const [mobileOpen, setMobileOpen] = React.useState(false);

    const handleLangChange = (event: React.ChangeEvent<{ value: unknown }> | SelectChangeEvent<string>) => {
        const newLang = (event.target as HTMLInputElement).value as string;
        setLang(newLang);
        localStorage.setItem('lang', newLang);
        window.location.reload();
    };

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };

    const navItems = [
        {label: 'Ana Sayfa', to: '/'},
        {label: 'Hakkında', to: '/about'},
        {label: 'İletişim', to: '/contact'},
        {label: 'Mobil Uygulama', to: '/mobile'},
        {label: 'Oyna', to: '/play'},
    ];

    return (
        <AppBar position="static">
            <Toolbar
                sx={{display: {xs: 'flex', md: 'flex'}, flexDirection: {xs: 'row', md: 'row'}, alignItems: 'center'}}>
                <Typography variant="h6" component={RouterLink} to="/"
                            sx={{flexGrow: 1, color: 'inherit', textDecoration: 'none'}}>
                    TrainVoc
                </Typography>
                {/* Mobilde hamburger menü */}
                <Box sx={{display: {xs: 'flex', md: 'none'}}}>
                    <IconButton color="inherit" edge="end" onClick={handleDrawerToggle}>
                        <MenuIcon/>
                    </IconButton>
                </Box>
                {/* Masaüstünde menü */}
                <Box display={{xs: 'none', md: 'flex'}} alignItems="center">
                    {navItems.map((item) => (
                        item.to === '/play' ? (
                            <Button
                                key={item.to}
                                color="inherit"
                                component={RouterLink}
                                to={item.to}
                                sx={{
                                    mx: 1,
                                    px: 2,
                                    fontWeight: 700,
                                    borderRadius: 3,
                                    background: 'linear-gradient(90deg, #2196f3 0%, #21cbf3 100%)',
                                    color: '#fff',
                                    boxShadow: 3,
                                    animation: `${playPulse} 1.5s infinite`,
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 1,
                                    '&:hover': {
                                        background: 'linear-gradient(90deg, #21cbf3 0%, #2196f3 100%)',
                                        transform: 'scale(1.07)',
                                        boxShadow: 6,
                                    },
                                }}
                                endIcon={<SportsEsportsIcon sx={{ml: 1, fontSize: 28}}/>}
                            >
                                Oyna
                                <Typography
                                    component="span"
                                    sx={{
                                        ml: 1,
                                        fontSize: 13,
                                        fontWeight: 500,
                                        color: '#fff',
                                        background: 'rgba(33,150,243,0.7)',
                                        borderRadius: 2,
                                        px: 1,
                                        py: 0.2,
                                        letterSpacing: 0.5,
                                        animation: 'pulse 2s infinite',
                                    }}
                                >
                                    Oyun Alanına Git!
                                </Typography>
                            </Button>
                        ) : (
                            <Button key={item.to} color="inherit" component={RouterLink}
                                    to={item.to}>{item.label}</Button>
                        )
                    ))}
                    <Select
                        value={lang}
                        onChange={handleLangChange}
                        size="small"
                        className={styles.langSelect}
                        sx={{ml: 2}}
                    >
                        <MenuItem value="tr">TR</MenuItem>
                        <MenuItem value="en">EN</MenuItem>
                    </Select>
                </Box>
            </Toolbar>
            {/* Drawer içinde de aynı şekilde vurgulu buton */}
            <Drawer
                anchor="right"
                open={mobileOpen}
                onClose={handleDrawerToggle}
                sx={{display: {xs: 'block', md: 'none'}}}
            >
                <Box sx={{width: 220}} role="presentation" onClick={handleDrawerToggle}>
                    <List>
                        {navItems.map((item) => (
                            item.to === '/play' ? (
                                <ListItem key={item.to}>
                                    <Button
                                        color="inherit"
                                        component={RouterLink}
                                        to={item.to}
                                        fullWidth
                                        sx={{
                                            my: 1,
                                            py: 1.5,
                                            fontWeight: 700,
                                            borderRadius: 3,
                                            background: 'linear-gradient(90deg, #2196f3 0%, #21cbf3 100%)',
                                            color: '#fff',
                                            boxShadow: 3,
                                            animation: `${playPulse} 1.5s infinite`,
                                            display: 'flex',
                                            alignItems: 'center',
                                            gap: 1,
                                            '&:hover': {
                                                background: 'linear-gradient(90deg, #21cbf3 0%, #2196f3 100%)',
                                                transform: 'scale(1.07)',
                                                boxShadow: 6,
                                            },
                                        }}
                                        endIcon={<SportsEsportsIcon sx={{ml: 1, fontSize: 28}}/>}
                                    >
                                        Oyna
                                        <Typography
                                            component="span"
                                            sx={{
                                                ml: 1,
                                                fontSize: 13,
                                                fontWeight: 500,
                                                color: '#fff',
                                                background: 'rgba(33,150,243,0.7)',
                                                borderRadius: 2,
                                                px: 1,
                                                py: 0.2,
                                                letterSpacing: 0.5,
                                                animation: 'pulse 2s infinite',
                                            }}
                                        >
                                            Oyun Alanına Git!
                                        </Typography>
                                    </Button>
                                </ListItem>
                            ) : (
                                <ListItem key={item.to} component={RouterLink} to={item.to}>
                                    <ListItemText primary={item.label}/>
                                </ListItem>
                            )
                        ))}
                        <ListItem>
                            <Select
                                value={lang}
                                onChange={handleLangChange}
                                size="small"
                                className={styles.langSelect}
                                fullWidth
                            >
                                <MenuItem value="tr">TR</MenuItem>
                                <MenuItem value="en">EN</MenuItem>
                            </Select>
                        </ListItem>
                    </List>
                </Box>
            </Drawer>
        </AppBar>
    );
};

export default Navbar;
