import { useEffect, useState, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { useParams, useNavigate, useLocation } from 'react-router-dom'
import { AlertCircle, Users } from 'lucide-react'
import api from '../api'
import { Card } from '@/components/ui/card'
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
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const pollIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null)

  // Fetch room data and poll for game start
  useEffect(() => {
    if (!roomCode) return

    const fetchRoom = async () => {
      try {
        const res = await api.get(`/api/game/${roomCode}`)
        setRoom(res.data)

        // Check if game has started - redirect to game page
        if (res.data.started || res.data.currentState > 0) {
          if (pollIntervalRef.current) {
            clearInterval(pollIntervalRef.current)
          }
          navigate(`/play/game?roomCode=${roomCode}&playerId=${playerId}`)
        }
      } catch {
        setError(t('error'))
      } finally {
        setLoading(false)
      }
    }

    // Initial fetch
    setLoading(true)
    setError('')
    fetchRoom()

    // Poll every 2 seconds to detect game start
    pollIntervalRef.current = setInterval(fetchRoom, 2000)

    return () => {
      if (pollIntervalRef.current) {
        clearInterval(pollIntervalRef.current)
      }
    }
  }, [roomCode, playerId, navigate, t])

  return (
    <div className="max-w-lg mx-auto mt-6 px-4">
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
                {room.players?.map((p: Player) => (
                  <div
                    key={p.id || p.name}
                    className="py-2 px-3 text-gray-700 dark:text-gray-300"
                  >
                    {p.name}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </Card>
      )}
    </div>
  )
}

export default RoomDetailPage
