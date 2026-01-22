import React from 'react';
import {Box} from '@mui/material';
import PlaySidebar from '../components/PlaySidebar';
import {Route, Routes} from 'react-router-dom';
import CreateRoomPage from './CreateRoomPage';
import JoinRoomPage from './JoinRoomPage';
import LobbyPage from './LobbyPage';
import ProfilePage from './ProfilePage';
import PlayPage from './PlayPage';
import LeaderboardPage from './LeaderboardPage';
import RoomDetailPage from './RoomDetailPage';
import GamePage from './GamePage';

const PlayLayout: React.FC = () => {
    return (
        <>
            <PlaySidebar/>
            <Box minHeight="100vh" bgcolor="#f5f7fa">
                <Box ml={0} pt={4}>
                    <Routes>
                        <Route index element={<PlayPage/>}/>
                        <Route path="create" element={<CreateRoomPage/>}/>
                        <Route path="join" element={<JoinRoomPage/>}/>
                        <Route path="lobby" element={<LobbyPage/>}/>
                        <Route path="profile" element={<ProfilePage/>}/>
                        <Route path="leaderboard" element={<LeaderboardPage/>}/>
                        <Route path="room/:roomCode" element={<RoomDetailPage/>}/>
                        <Route path="game" element={<GamePage/>}/>
                    </Routes>
                </Box>
            </Box>
        </>
    );
};

export default PlayLayout;
