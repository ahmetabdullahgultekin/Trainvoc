import React from 'react';
import { Button, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import { keyframes } from '@emotion/react';

const playPulse = keyframes`
  0% { box-shadow: 0 0 0 0 rgba(33, 150, 243, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(33, 150, 243, 0); }
  100% { box-shadow: 0 0 0 0 rgba(33, 150, 243, 0); }
`;

interface PlayButtonProps {
    /** Whether to render as full width (for mobile drawer) */
    fullWidth?: boolean;
    /** Optional click handler */
    onClick?: () => void;
}

/**
 * Animated "Play" button used in navigation.
 * Extracted to avoid code duplication between desktop and mobile nav.
 */
const PlayButton: React.FC<PlayButtonProps> = ({ fullWidth = false, onClick }) => {
    const baseStyles = {
        mx: fullWidth ? 0 : 1,
        my: fullWidth ? 1 : 0,
        px: 2,
        py: fullWidth ? 1.5 : undefined,
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
    };

    return (
        <Button
            color="inherit"
            component={RouterLink}
            to="/play"
            fullWidth={fullWidth}
            onClick={onClick}
            sx={baseStyles}
            endIcon={<SportsEsportsIcon sx={{ ml: 1, fontSize: 28 }} />}
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
    );
};

export default PlayButton;
