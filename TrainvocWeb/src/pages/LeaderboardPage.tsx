import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import { Trophy, Search, Loader2 } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import api from '../api'

interface Player {
  id: string
  name: string
  score: number
}

const LeaderboardPage: React.FC = () => {
  const { t } = useTranslation()
  const [roomCode, setRoomCode] = useState('')
  const [players, setPlayers] = useState<Player[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const fetchLeaderboard = async () => {
    setLoading(true)
    setError('')
    try {
      const res = await api.get(`/api/leaderboard?roomCode=${roomCode}`)
      setPlayers(res.data)
    } catch {
      setError(t('error'))
      setPlayers([])
    } finally {
      setLoading(false)
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="max-w-lg mx-auto mt-6 px-4"
    >
      <Card className="p-6 bg-gradient-to-br from-yellow-50 to-brand-50 dark:from-gray-800 dark:to-brand-900/20">
        <div className="flex items-center gap-2 mb-6">
          <Trophy className="h-8 w-8 text-yellow-500" />
          <h1 className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-white">
            {t('leaderboard')}
          </h1>
        </div>

        <div className="flex gap-3 mb-6 flex-col sm:flex-row">
          <Input
            placeholder={t('roomCode')}
            value={roomCode}
            onChange={e => setRoomCode(e.target.value)}
            className="flex-1"
          />
          <Button onClick={fetchLeaderboard} className="min-w-[120px]">
            <Search className="h-4 w-4 mr-2" />
            {t('submit')}
          </Button>
        </div>

        {error && (
          <p className="text-red-500 mb-4">{error}</p>
        )}

        {loading && (
          <div className="flex items-center justify-center gap-2 py-4">
            <Loader2 className="h-5 w-5 animate-spin" />
            <span>{t('loading')}</span>
          </div>
        )}

        {players.length > 0 && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="overflow-hidden rounded-xl border border-gray-200 dark:border-gray-700"
          >
            <table className="w-full">
              <thead className="bg-gray-100 dark:bg-gray-800">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-bold text-gray-700 dark:text-gray-300">
                    #
                  </th>
                  <th className="px-4 py-3 text-left text-sm font-bold text-gray-700 dark:text-gray-300">
                    {t('playerName')}
                  </th>
                  <th className="px-4 py-3 text-left text-sm font-bold text-gray-700 dark:text-gray-300">
                    {t('score')}
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white dark:bg-gray-900">
                {players.map((player, idx) => (
                  <tr
                    key={player.id}
                    className="border-t border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
                  >
                    <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-400">
                      {idx + 1}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-900 dark:text-white font-medium">
                      {player.name}
                    </td>
                    <td className="px-4 py-3 text-sm text-brand-600 dark:text-brand-400 font-bold">
                      {player.score}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </motion.div>
        )}
      </Card>
    </motion.div>
  )
}

export default LeaderboardPage
