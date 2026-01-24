import { useEffect, useState, useRef, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { useParams, useNavigate, useLocation } from 'react-router-dom'
import { AlertCircle, Users, Wifi, WifiOff } from 'lucide-react'
import api from '../api'
import { Card } from '@/components/ui/card'
import { useWebSocket } from '../hooks/useWebSocket'
import type { GameRoom, Player } from '../interfaces/game'

function useQuery() {
  return new URLSearchParams(useLocation().search)
}

const RoomDetailPage = () => {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const query = useQuery()
  const { roomCode } = useParams()
  const playerId = query.get('playerId') || ''
  const [room, setRoom] = useState<GameRoom | null>(null)
  const [players, setPlayers] = useState<Player[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const initialFetchDone = useRef(false)

  // WebSocket event handlers
  const handleGameStateChanged = useCallback((state: number, remainingTime: number) => {
    console.log('Game state changed:', state, 'remainingTime:', remainingTime)
    // State > 0 means game has started (COUNTDOWN or later)
    if (state > 0) {
      navigate(`/play/game?roomCode=${roomCode}&playerId=${playerId}`)
    }
  }, [navigate, roomCode, playerId])

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

  const handleError = useCallback((errorMsg: string) => {
    console.error('WebSocket error:', errorMsg)
    setError(errorMsg)
  }, [])

  // Initialize WebSocket connection
  const { connectionState, isConnected, connect } = useWebSocket({
    autoConnect: true,
    handlers: {
      onGameStateChanged: handleGameStateChanged,
      onPlayersUpdate: handlePlayersUpdate,
      onPlayerJoined: handlePlayerJoined,
      onPlayerLeft: handlePlayerLeft,
      onError: handleError,
    },
  })

  // Ensure WebSocket is connected
  useEffect(() => {
    if (connectionState === 'disconnected') {
      connect()
    }
  }, [connectionState, connect])

  // Fetch room data once on mount
  useEffect(() => {
    if (!roomCode) return
    if (initialFetchDone.current) return
    initialFetchDone.current = true

    const fetchRoom = async () => {
      setLoading(true)
      try {
        const res = await api.get(`/api/game/${roomCode}`)
        setRoom(res.data)
        setPlayers(res.data.players || [])

        // Check if game has already started
        if (res.data.started || res.data.currentState > 0) {
          navigate(`/play/game?roomCode=${roomCode}&playerId=${playerId}`)
        }
      } catch {
        setError(t('error'))
      } finally {
        setLoading(false)
      }
    }

    fetchRoom()
  }, [roomCode, playerId, navigate, t])

  return (
    <div className="max-w-lg mx-auto mt-6 px-4">
      {/* Connection status */}
      <div className="flex justify-end mb-2">
        {isConnected ? (
          <div className="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
            <Wifi className="h-3 w-3" />
            <span>Connected</span>
          </div>
        ) : (
          <div className="flex items-center gap-1 text-xs text-red-600 dark:text-red-400">
            <WifiOff className="h-3 w-3" />
            <span>Disconnected</span>
          </div>
        )}
      </div>

      <h1 className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-white mb-6">
        {t('roomDetails')}
      </h1>

      {loading && (
        <p className="text-gray-500 dark:text-gray-400">{t('loading')}</p>
      )}

      {error && (
        <div className="flex items-center gap-2 p-4 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300 rounded-lg">
          <AlertCircle className="h-5 w-5" />
          <span>{error}</span>
        </div>
      )}

      {room && (
        <Card className="p-6">
          <div className="space-y-4">
            <div>
              <span className="text-gray-600 dark:text-gray-400">{t('roomCode')}: </span>
              <span className="font-bold text-gray-900 dark:text-white">{room.roomCode}</span>
            </div>

            <div>
              <span className="text-gray-600 dark:text-gray-400">{t('settings')}:</span>
              <ul className="list-disc list-inside mt-2 text-gray-700 dark:text-gray-300">
                <li>{t('questionCount')}: {room.totalQuestionCount}</li>
                <li>{t('timePerQuestion')}: {room.questionDuration}</li>
              </ul>
            </div>

            <div>
              <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400 mb-2">
                <Users className="h-4 w-4" />
                <span>{t('players')}:</span>
              </div>
              <div className="bg-gray-50 dark:bg-gray-800 rounded-lg p-3">
                {players.length > 0 ? (
                  players.map((p: Player) => (
                    <div
                      key={p.id || p.name}
                      className="py-2 px-3 text-gray-700 dark:text-gray-300"
                    >
                      {p.name}
                    </div>
                  ))
                ) : room.players?.map((p: Player) => (
                  <div
                    key={p.id || p.name}
                    className="py-2 px-3 text-gray-700 dark:text-gray-300"
                  >
                    {p.name}
                  </div>
                ))}
              </div>
            </div>

            <div className="flex items-center justify-center gap-2 p-4 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 rounded-lg">
              <div className="w-4 h-4 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
              <span>{t('waitingForHost')}</span>
            </div>
          </div>
        </Card>
      )}
    </div>
  )
}

export default RoomDetailPage
