import React, {useEffect, useState} from 'react';
import FullscreenIcon from '@mui/icons-material/Fullscreen';
import FullscreenExitIcon from '@mui/icons-material/FullscreenExit';
import {IconButton, Tooltip} from '@mui/material';
import {isFullscreen as checkFullscreen, toggleFullscreen, onFullscreenChange} from '../../utils/fullscreen';

const FullscreenButton: React.FC = () => {
    const [isFullscreen, setIsFullscreen] = useState(checkFullscreen());

    useEffect(() => {
        return onFullscreenChange(setIsFullscreen);
    }, []);

    const handleToggleFullscreen = () => {
        toggleFullscreen();
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

