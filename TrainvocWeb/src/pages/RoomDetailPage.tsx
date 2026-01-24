import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useParams } from 'react-router-dom'
import { AlertCircle, Users } from 'lucide-react'
import api from '../api'
import { Card } from '@/components/ui/card'
import type { GameRoom, Player } from '../interfaces/game'

const RoomDetailPage = () => {
  const { t } = useTranslation()
  const { roomCode } = useParams()
  const [room, setRoom] = useState<GameRoom | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!roomCode) return
    setLoading(true)
    setError('')
    setRoom(null)
    api.get(`/api/game/${roomCode}`)
      .then(res => setRoom(res.data))
      .catch(() => setError(t('error')))
      .finally(() => setLoading(false))
  }, [roomCode, t])

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
