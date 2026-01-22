import React, {useState} from 'react';
import {
    Box,
    Divider,
    Drawer,
    IconButton,
    List,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Typography
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import PlayCircleIcon from '@mui/icons-material/PlayCircle';
import MeetingRoomIcon from '@mui/icons-material/MeetingRoom';
import PersonIcon from '@mui/icons-material/Person';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import {useLocation, useNavigate} from 'react-router-dom';

const sidebarItems = [
    {label: 'Ana Sayfaya Dön', icon: <ArrowBackIcon/>, path: '/'},
    {label: 'Oyun Kur', icon: <PlayCircleIcon/>, path: '/play/create'},
    {label: 'Oyuna Katıl', icon: <GroupAddIcon/>, path: '/play/join'},
    {label: 'Lobi', icon: <MeetingRoomIcon/>, path: '/play/lobby'},
    {label: 'Profilim', icon: <PersonIcon/>, path: '/play/profile'},
];

const PlaySidebar: React.FC = () => {
    const [open, setOpen] = useState(true); // Başlangıçta kapalı
    const navigate = useNavigate();
    const location = useLocation();

    const handleNavigate = (path: string) => {
        navigate(path);
        setOpen(false);
    };

    return (
        <>
            <IconButton
                color="primary"
                aria-label="menu"
                onClick={() => setOpen(true)}
                sx={{
                    position: 'fixed',
                    top: 16,
                    right: 16,
                    left: 'auto',
                    zIndex: 1301,
                    bgcolor: '#fff',
                    border: '2px solid',
                    borderColor: open ? 'primary.main' : 'grey.300',
                    boxShadow: open ? 3 : 1,
                    transition: 'all 0.2s',
                    '&:hover': {
                        bgcolor: 'primary.light',
                        borderColor: 'primary.main',
                        cursor: 'pointer',
                    },
                    display: open ? 'none' : 'flex',
                    width: {xs: 44, sm: 52},
                    height: {xs: 44, sm: 52},
                }}
            >
                <MenuIcon sx={{fontSize: {xs: 28, sm: 32}}}/>
            </IconButton>
            <Drawer anchor="right" open={open} onClose={() => {
                setOpen(false);
                setTimeout(() => {
                    const main = document.getElementById('main-content');
                    if (main) main.focus();
                }, 100);
            }}>
                <Box width={{xs: 220, sm: 270, md: 300}} height="100vh" display="flex" flexDirection="column"
                     role="presentation" onClick={() => setOpen(false)} sx={{
                    p: 0,
                    flex: 1,
                    maxWidth: '100vw',
                    bgcolor: '#fff',
                    boxShadow: 3,
                    borderTopRightRadius: 16,
                    borderBottomRightRadius: 16,
                    overflowY: 'auto',
                    overflowX: 'hidden',
                    display: 'flex',
                    flexDirection: 'column',
                }}>
                    <Typography variant="h6" align="center" mt={2} mb={1} fontWeight={700} color="primary.main">
                        Oyun Menüsü
                    </Typography>
                    <Divider sx={{mb: 1, bgcolor: 'primary.light'}}/>
                    <List sx={{p: 0, flex: 1}}>
                        {sidebarItems.map((item) => (
                            <ListItemButton
                                key={item.label}
                                selected={location.pathname === item.path}
                                onClick={() => handleNavigate(item.path)}
                                sx={{
                                    borderRadius: 2,
                                    mb: 1.2,
                                    mx: 1.2,
                                    bgcolor: location.pathname === item.path ? 'primary.main' : 'grey.100',
                                    color: location.pathname === item.path ? '#fff' : 'primary.main',
                                    boxShadow: location.pathname === item.path ? 2 : 0,
                                    fontWeight: location.pathname === item.path ? 700 : 500,
                                    transition: 'background 0.2s, color 0.2s',
                                    '&:hover': {
                                        bgcolor: 'primary.light',
                                        color: 'primary.main',
                                        boxShadow: 1,
                                        cursor: 'pointer',
                                    },
                                    minHeight: 48,
                                    userSelect: 'none',
                                    WebkitUserSelect: 'none',
                                    MozUserSelect: 'none',
                                    msUserSelect: 'none',
                                }}
                                disableTouchRipple
                            >
                                <ListItemIcon sx={{
                                    color: location.pathname === item.path ? '#fff' : 'primary.main',
                                    minWidth: 36
                                }}>
                                    {item.icon}
                                </ListItemIcon>
                                <ListItemText
                                    primary={item.label}
                                    sx={{
                                        fontWeight: location.pathname === item.path ? 700 : 500,
                                        fontSize: 17,
                                        letterSpacing: 0.2,
                                        whiteSpace: 'nowrap',
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis',
                                        pointerEvents: 'none',
                                        userSelect: 'none',
                                    }}
                                />
                            </ListItemButton>
                        ))}
                    </List>
                </Box>
            </Drawer>
        </>
    );
};

export default PlaySidebar;
