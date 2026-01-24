import { Route, Routes } from 'react-router-dom'
import { GameLayout } from '@/components/layout'
import { lazy, Suspense } from 'react'

// Lazy load play pages
const PlayPage = lazy(() => import('./PlayPage'))
const CreateRoomPage = lazy(() => import('./CreateRoomPage'))
const JoinRoomPage = lazy(() => import('./JoinRoomPage'))
const LobbyPage = lazy(() => import('./LobbyPage'))
const ProfilePage = lazy(() => import('./ProfilePage'))
const LeaderboardPage = lazy(() => import('./LeaderboardPage'))
const RoomDetailPage = lazy(() => import('./RoomDetailPage'))
const GamePage = lazy(() => import('./GamePage'))

// Loading spinner for nested routes
function GameLoadingSpinner() {
  return (
    <div className="flex items-center justify-center min-h-[400px]">
      <div className="w-8 h-8 border-4 border-brand-200 border-t-brand-500 rounded-full animate-spin" />
    </div>
  )
}

function PlayLayout() {
  return (
    <GameLayout>
      <Suspense fallback={<GameLoadingSpinner />}>
        <Routes>
          <Route index element={<PlayPage />} />
          <Route path="create" element={<CreateRoomPage />} />
          <Route path="join" element={<JoinRoomPage />} />
          <Route path="lobby" element={<LobbyPage />} />
          <Route path="profile" element={<ProfilePage />} />
          <Route path="leaderboard" element={<LeaderboardPage />} />
          <Route path="room/:roomCode" element={<RoomDetailPage />} />
          <Route path="game" element={<GamePage />} />
        </Routes>
      </Suspense>
    </GameLayout>
  )
}

export default PlayLayout
