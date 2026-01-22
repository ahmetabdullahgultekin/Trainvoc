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
import PlayButton from './PlayButton';

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
                            <PlayButton key={item.to} />
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
                                    <PlayButton fullWidth />
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
