import React, {useEffect, useState} from 'react';
import FullscreenIcon from '@mui/icons-material/Fullscreen';
import FullscreenExitIcon from '@mui/icons-material/FullscreenExit';
import {IconButton, Tooltip} from '@mui/material';

const FullscreenButton: React.FC = () => {
    const [isFullscreen, setIsFullscreen] = useState(false);

    useEffect(() => {
        const onFullscreenChange = () => {
            setIsFullscreen(!!document.fullscreenElement);
        };
        document.addEventListener('fullscreenchange', onFullscreenChange);
        return () => document.removeEventListener('fullscreenchange', onFullscreenChange);
    }, []);

    const handleToggleFullscreen = () => {
        const elem = document.documentElement;
        if (!isFullscreen) {
            if (elem.requestFullscreen) elem.requestFullscreen();
            else if ((elem as any).webkitRequestFullscreen) (elem as any).webkitRequestFullscreen();
            else if ((elem as any).msRequestFullscreen) (elem as any).msRequestFullscreen();
        } else {
            if (document.exitFullscreen) document.exitFullscreen();
            else if ((document as any).webkitExitFullscreen) (document as any).webkitExitFullscreen();
            else if ((document as any).msExitFullscreen) (document as any).msExitFullscreen();
        }
    };

    return (
        <Tooltip title={isFullscreen ? 'Tam ekrandan çık' : 'Tam ekrana geç'}>
            <IconButton
                onClick={handleToggleFullscreen}
                sx={{position: 'fixed', top: 16, left: 16, zIndex: 12000, bgcolor: 'white', boxShadow: 2}}
            >
                {isFullscreen ? <FullscreenExitIcon/> : <FullscreenIcon/>}
            </IconButton>
        </Tooltip>
    );
};

export default FullscreenButton;

