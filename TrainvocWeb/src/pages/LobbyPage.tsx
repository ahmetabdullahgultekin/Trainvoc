import { useEffect, useState, useCallback, useRef } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Users, Play, LogOut, Crown, Clock, HelpCircle, BarChart3, Lock, Wifi, WifiOff } from 'lucide-react'
import api from '../api'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Avatar } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { avatarList } from '../components/shared/useProfile'
import { exitFullscreen } from '../utils/fullscreen'
import { useWebSocket } from '../hooks/useWebSocket'
import type { LobbyData, Player } from '../interfaces/game'

function LobbyPage() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'
  const navigate = useNavigate()
  const location = useLocation()
  const [lobby, setLobby] = useState<LobbyData | null>(null)
  const [players, setPlayers] = useState<Player[]>([])
  const [loading, setLoading] = useState(true)
  const [starting, setStarting] = useState(false)
  const [error, setError] = useState('')
  const [showLeaveModal, setShowLeaveModal] = useState(false)
  const [roomPassword, setRoomPassword] = useState('')
  const initialFetchDone = useRef(false)

  const params = new URLSearchParams(location.search)
  const roomCode = params.get('roomCode')
  const playerId = params.get('playerId')

  const content = {
    en: {
      roomCode: 'Room Code',
      waitingPlayers: 'Waiting for players...',
      readyMessage: 'Ready? The game is about to start!',
      roomSettings: 'Room Settings',
      questionDuration: 'Question Duration',
      totalQuestions: 'Total Questions',
      optionCount: 'Options',
      level: 'Level',
      startGame: 'Start Game',
      starting: 'Starting...',
      leaveLobby: 'Leave Lobby',
      leaveConfirm: 'Are you sure?',
      leaveHostMessage: 'If you leave, the room will be disbanded and all players will be removed.',
      yesDisband: 'Yes, Disband',
      cancel: 'Cancel',
      hostPassword: 'Room Password (required for host)',
      lobbyNotFound: 'Lobby Not Found',
      lobbyNotFoundMessage: "You haven't joined a lobby yet or the lobby has expired.",
      joinGame: 'Join Game',
      missingInfo: 'Room code or player info is missing.',
      lobbyFetchError: 'Could not fetch lobby info.',
      disbandError: 'Could not disband lobby.',
      leaveError: 'Could not leave lobby.',
      connected: 'Connected',
      connecting: 'Connecting...',
      disconnected: 'Disconnected',
    },
    tr: {
      roomCode: 'Oda Kodu',
      waitingPlayers: 'Oyuncular bekleniyor...',
      readyMessage: 'Hazır mısın? Oyun birazdan başlıyor!',
      roomSettings: 'Oda Ayarları',
      questionDuration: 'Soru Süresi',
      totalQuestions: 'Toplam Soru',
      optionCount: 'Şık Sayısı',
      level: 'Seviye',
      startGame: 'Oyunu Başlat',
      starting: 'Başlatılıyor...',
      leaveLobby: 'Lobiden Çık',
      leaveConfirm: 'Emin misiniz?',
      leaveHostMessage: 'Lobiden çıkarsanız oda dağıtılacak ve tüm oyuncular atılacak.',
      yesDisband: 'Evet, Lobi Dağıtılsın',
      cancel: 'Vazgeç',
      hostPassword: 'Oda Şifresi (host için gerekli)',
      lobbyNotFound: 'Lobi Bulunamadı',
      lobbyNotFoundMessage: 'Henüz bir lobiye katılmadınız veya lobi süresi dolmuş olabilir.',
      joinGame: 'Oyuna Katıl',
      missingInfo: 'Oda kodu veya oyuncu bilgisi eksik.',
      lobbyFetchError: 'Lobi bilgisi alınamadı.',
      disbandError: 'Lobi dağıtılamadı.',
      leaveError: 'Lobiden çıkılamadı.',
      connected: 'Bağlı',
      connecting: 'Bağlanıyor...',
      disconnected: 'Bağlantı Kesildi',
    },
  }

  const txt = content[lang]

  // WebSocket event handlers
  const handlePlayersUpdate = useCallback((updatedPlayers: Player[]) => {
    setPlayers(updatedPlayers)
  }, [])

  const handlePlayerJoined = useCallback((newPlayerId: string, playerName: string) => {
    console.log('Player joined:', playerName, newPlayerId)
    // Players list will be updated via playersUpdate event
  }, [])

  const handlePlayerLeft = useCallback((leftPlayerId: string) => {
    console.log('Player left:', leftPlayerId)
    // Players list will be updated via playersUpdate event
  }, [])

  const handleGameStateChanged = useCallback((state: number, remainingTime: number) => {
    console.log('Game state changed:', state, 'remainingTime:', remainingTime)
    // State > 0 means game has started
    if (state > 0) {
      navigate(`/play/game?roomCode=${roomCode}&playerId=${playerId}`)
    }
  }, [navigate, roomCode, playerId])

  const handleError = useCallback((errorMsg: string) => {
    console.error('WebSocket error:', errorMsg)
    setError(errorMsg)
  }, [])

  // Initialize WebSocket connection
  const { connectionState, isConnected, connect, leaveRoom, startGame: wsStartGame } = useWebSocket({
    autoConnect: true,
    handlers: {
      onPlayersUpdate: handlePlayersUpdate,
      onPlayerJoined: handlePlayerJoined,
      onPlayerLeft: handlePlayerLeft,
      onGameStateChanged: handleGameStateChanged,
      onError: handleError,
    },
  })

  // Initial fetch of lobby data (one-time, not polling)
  useEffect(() => {
    if (!roomCode || !playerId) {
      setError(txt.missingInfo)
      return
    }

    if (initialFetchDone.current) return
    initialFetchDone.current = true

    setLoading(true)
    api.get(`/api/game/${roomCode}`)
      .then(res => {
        setLobby(res.data)
        setPlayers(res.data.players || [])
        // Check if game already started
        if (res.data.gameStarted || res.data.currentState > 0) {
          navigate(`/play/game?roomCode=${roomCode}&playerId=${playerId}`)
        }
      })
      .catch(() => setError(txt.lobbyFetchError))
      .finally(() => setLoading(false))
  }, [roomCode, playerId, navigate, txt.missingInfo, txt.lobbyFetchError])

  // Ensure WebSocket is connected
  useEffect(() => {
    if (connectionState === 'disconnected') {
      connect()
    }
  }, [connectionState, connect])

  const handleStartGame = useCallback(() => {
    if (!roomCode) return
    setStarting(true)
    setError('')

    // Start game via WebSocket
    wsStartGame(roomCode)

    // The navigation will happen via handleGameStateChanged when server broadcasts
  }, [roomCode, wsStartGame])

  const handleHostLeave = async () => {
    await exitFullscreen()
    try {
      if (roomCode && playerId) {
        leaveRoom(roomCode, playerId)
      }
      await api.post(`/api/game/rooms/${roomCode}/disband` + (roomPassword ? `?password=${encodeURIComponent(roomPassword)}` : ''))
      navigate('/play')
    } catch {
      setError(txt.disbandError)
    }
  }

  const handlePlayerLeave = async () => {
    await exitFullscreen()
    try {
      if (roomCode && playerId) {
        leaveRoom(roomCode, playerId)
        await api.post(`/api/game/rooms/${roomCode}/leave?playerId=${playerId}`)
      }
      navigate('/play')
    } catch {
      setError(txt.leaveError)
    }
  }

  if (loading && !error && lobby === null) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="w-12 h-12 border-4 border-brand-200 border-t-brand-500 rounded-full animate-spin" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="p-4">
        <Card className="p-6 border-red-200 bg-red-50 dark:bg-red-900/20 dark:border-red-800">
          <p className="text-red-600 dark:text-red-400">{error}</p>
        </Card>
      </div>
    )
  }

  if (!lobby) {
    return (
      <div className="flex items-center justify-center min-h-[60vh] p-4">
        <Card className="p-8 max-w-md text-center">
          <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
            <Users className="h-8 w-8 text-red-500" />
          </div>
          <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
            {txt.lobbyNotFound}
          </h2>
          <p className="text-gray-600 dark:text-gray-400 mb-6">
            {txt.lobbyNotFoundMessage}
          </p>
          <Button onClick={() => navigate('/play/join')}>
            {txt.joinGame}
          </Button>
        </Card>
      </div>
    )
  }

  const isHost = lobby.hostId === playerId
  const hostPlayer = players.find(p => p.id === lobby.hostId)
  const otherPlayers = players.filter(p => p.id !== lobby.hostId)
  const displayPlayers: Player[] = hostPlayer ? [hostPlayer, ...otherPlayers] : players

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 p-4 md:p-6">
      <div className="max-w-2xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <Card className="p-6 md:p-8">
            {/* Connection Status */}
            <div className="absolute top-4 right-4 flex items-center gap-2 text-xs">
              {isConnected ? (
                <>
                  <Wifi className="h-3 w-3 text-green-500" />
                  <span className="text-green-600 dark:text-green-400">{txt.connected}</span>
                </>
              ) : connectionState === 'connecting' ? (
                <>
                  <Wifi className="h-3 w-3 text-yellow-500 animate-pulse" />
                  <span className="text-yellow-600 dark:text-yellow-400">{txt.connecting}</span>
                </>
              ) : (
                <>
                  <WifiOff className="h-3 w-3 text-red-500" />
                  <span className="text-red-600 dark:text-red-400">{txt.disconnected}</span>
                </>
              )}
            </div>

            {/* Room Code */}
            <div className="text-center mb-6">
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">{txt.roomCode}</p>
              <h1 className="text-4xl md:text-5xl font-game text-brand-600 dark:text-brand-400">
                {lobby.roomCode}
              </h1>
            </div>

            {/* Waiting indicator */}
            <div className="flex flex-col items-center mb-6">
              <div className="flex items-center gap-3 mb-2">
                <motion.div
                  animate={{ rotate: 360 }}
                  transition={{ duration: 2, repeat: Infinity, ease: 'linear' }}
                >
                  <Users className="h-10 w-10 text-brand-500" />
                </motion.div>
                <motion.div
                  className="w-3 h-3 bg-yellow-400 rounded-full"
                  animate={{ y: [0, -8, 0] }}
                  transition={{ duration: 0.6, repeat: Infinity }}
                />
              </div>
              <h2 className="text-lg font-semibold text-brand-600 dark:text-brand-400">
                {txt.waitingPlayers}
              </h2>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                {txt.readyMessage}
              </p>
            </div>

            {/* Room Settings */}
            <div className="mb-6">
              <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3 flex items-center gap-2">
                <BarChart3 className="h-4 w-4" />
                {txt.roomSettings}
              </h3>
              <div className="grid grid-cols-2 gap-3">
                <div className="flex items-center gap-2 text-sm">
                  <Clock className="h-4 w-4 text-gray-400" />
                  <span className="text-gray-600 dark:text-gray-400">{txt.questionDuration}:</span>
                  <span className="font-medium text-gray-900 dark:text-white">{lobby.questionDuration ?? '-'}s</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <HelpCircle className="h-4 w-4 text-gray-400" />
                  <span className="text-gray-600 dark:text-gray-400">{txt.totalQuestions}:</span>
                  <span className="font-medium text-gray-900 dark:text-white">{lobby.totalQuestionCount ?? '-'}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <span className="text-gray-600 dark:text-gray-400">{txt.optionCount}:</span>
                  <span className="font-medium text-gray-900 dark:text-white">{lobby.optionCount ?? '-'}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <span className="text-gray-600 dark:text-gray-400">{txt.level}:</span>
                  <Badge variant="secondary">{lobby.level ?? '-'}</Badge>
                </div>
              </div>
            </div>

            {/* Players */}
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-6">
              <AnimatePresence>
                {displayPlayers.map((player, index) => {
                  const isPlayerHost = player.id === lobby.hostId
                  return (
                    <motion.div
                      key={player.id}
                      initial={{ opacity: 0, scale: 0.8 }}
                      animate={{ opacity: 1, scale: 1 }}
                      exit={{ opacity: 0, scale: 0.8 }}
                      transition={{ delay: index * 0.1 }}
                    >
                      <Card className={`p-4 text-center ${isPlayerHost ? 'bg-yellow-50 dark:bg-yellow-900/20 border-yellow-200 dark:border-yellow-800' : 'bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800'}`}>
                        <Avatar className={`h-14 w-14 mx-auto mb-2 text-2xl ${isPlayerHost ? 'bg-yellow-400' : 'bg-brand-500'}`}>
                          {typeof player.avatarId === 'number' || !isNaN(Number(player.avatarId))
                            ? avatarList[Number(player.avatarId) % avatarList.length]
                            : avatarList[0]}
                        </Avatar>
                        <p className="font-medium text-gray-900 dark:text-white text-sm truncate">
                          {player.name}
                        </p>
                        {isPlayerHost && (
                          <div className="flex items-center justify-center gap-1 mt-1 text-yellow-600 dark:text-yellow-400">
                            <Crown className="h-3 w-3" />
                            <span className="text-xs font-medium">Host</span>
                          </div>
                        )}
                      </Card>
                    </motion.div>
                  )
                })}
              </AnimatePresence>
            </div>

            {/* Actions */}
            {isHost ? (
              <div className="space-y-3">
                <Button
                  className="w-full gap-2"
                  size="lg"
                  onClick={handleStartGame}
                  disabled={starting || !isConnected}
                >
                  <Play className="h-5 w-5" />
                  {starting ? txt.starting : txt.startGame}
                </Button>
                <Button
                  variant="outline"
                  className="w-full gap-2 border-red-300 text-red-600 hover:bg-red-50 dark:border-red-800 dark:text-red-400 dark:hover:bg-red-900/20"
                  size="lg"
                  onClick={() => setShowLeaveModal(true)}
                >
                  <LogOut className="h-5 w-5" />
                  {txt.leaveLobby}
                </Button>
              </div>
            ) : (
              <Button
                variant="outline"
                className="w-full gap-2 border-red-300 text-red-600 hover:bg-red-50 dark:border-red-800 dark:text-red-400 dark:hover:bg-red-900/20"
                size="lg"
                onClick={handlePlayerLeave}
              >
                <LogOut className="h-5 w-5" />
                {txt.leaveLobby}
              </Button>
            )}
          </Card>

          {/* Host password input */}
          {isHost && (
            <Card className="p-4 mt-4">
              <div className="flex items-center gap-2 mb-2">
                <Lock className="h-4 w-4 text-gray-400" />
                <label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                  {txt.hostPassword}
                </label>
              </div>
              <Input
                type="password"
                value={roomPassword}
                onChange={(e) => setRoomPassword(e.target.value)}
                placeholder="••••••••"
              />
            </Card>
          )}
        </motion.div>

        {/* Leave confirmation dialog */}
        <Dialog open={showLeaveModal} onOpenChange={setShowLeaveModal}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{txt.leaveConfirm}</DialogTitle>
            </DialogHeader>
            <p className="text-gray-600 dark:text-gray-400">
              {txt.leaveHostMessage}
            </p>
            <DialogFooter>
              <Button variant="outline" onClick={() => setShowLeaveModal(false)}>
                {txt.cancel}
              </Button>
              <Button variant="destructive" onClick={handleHostLeave}>
                {txt.yesDisband}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  )
}

export default LobbyPage
