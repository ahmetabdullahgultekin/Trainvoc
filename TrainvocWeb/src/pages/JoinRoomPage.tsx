import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Users, Trophy, Rocket, LogIn, Lock } from 'lucide-react'
import api from '../api'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import useNick from '../components/shared/useNick'
import useProfile from '../components/shared/useProfile'
import type { GameRoom, Player } from '../interfaces/game'

function JoinRoomPage() {
  const { t, i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'
  const navigate = useNavigate()
  const location = useLocation()
  const [roomCode, setRoomCode] = useState('')
  const [playerName, setPlayerName] = useState('')
  const [player, setPlayer] = useState<Player | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [rooms, setRooms] = useState<GameRoom[]>([])
  const [roomsLoading, setRoomsLoading] = useState(true)
  const [nick, setNick] = useNick()
  const { avatar } = useProfile()
  const [roomPassword, setRoomPassword] = useState('')

  const avatarId = avatar ? avatar : (Math.floor(Math.random() * 10) + 1).toString()

  const content = {
    en: {
      title: 'Join Room',
      subtitle: 'Enter a room code or select from active rooms to join!',
      community: 'Community',
      communityDesc: 'Learn together, compete together!',
      competition: 'Competition',
      competitionDesc: 'Compete with the best players!',
      quickJoin: 'Quick Join',
      quickJoinDesc: 'Join room instantly, start immediately!',
      roomCode: 'Room Code',
      playerName: 'Your Name',
      roomPassword: 'Room Password (if any)',
      joinRoom: 'Join Room',
      activeRooms: 'Active Rooms',
      noRooms: 'No active rooms right now. Create one!',
      joinSuccess: 'Successfully joined!',
      duplicateName: 'A player with this name already exists, you cannot join the lobby.',
      passwordRequired: 'This room requires a password.',
      invalidPassword: 'Wrong password.',
      roomNotFound: 'Room not found.',
    },
    tr: {
      title: 'Odaya Katıl',
      subtitle: 'Oda kodunu gir ya da listeden bir odayı seç!',
      community: 'Topluluk',
      communityDesc: 'Birlikte öğren, birlikte yarış!',
      competition: 'Rekabet',
      competitionDesc: 'En iyi oyuncularla yarış!',
      quickJoin: 'Hızlı Katılım',
      quickJoinDesc: 'Odaya anında katıl, hemen başla!',
      roomCode: 'Oda Kodu',
      playerName: 'İsminiz',
      roomPassword: 'Oda Şifresi (varsa)',
      joinRoom: 'Odaya Katıl',
      activeRooms: 'Aktif Odalar',
      noRooms: 'Şu anda aktif oda yok. Hemen bir oda oluştur!',
      joinSuccess: 'Başarıyla katıldınız!',
      duplicateName: 'Bu isimle zaten bir oyuncu var, lobiye katılamazsınız.',
      passwordRequired: 'Bu oda için şifre gereklidir.',
      invalidPassword: 'Şifre yanlış.',
      roomNotFound: 'Oda bulunamadı.',
    },
  }

  const txt = content[lang]

  useEffect(() => {
    setRoomsLoading(true)
    api.get('/api/game/rooms')
      .then(res => setRooms(res.data))
      .catch(() => setRooms([]))
      .finally(() => setRoomsLoading(false))
  }, [])

  useEffect(() => {
    const params = new URLSearchParams(location.search)
    const code = params.get('roomCode')
    if (code) setRoomCode(code)
  }, [location.search])

  useEffect(() => {
    setPlayerName(nick)
  }, [nick])

  const handleJoin = async () => {
    setLoading(true)
    setError('')
    setPlayer(null)
    try {
      // Check for duplicate names
      const roomRes = await api.get(`/api/game/${roomCode}`)
      const players = roomRes.data?.players || []
      if (players.some((p: Player) => p.name?.trim().toLowerCase() === playerName.trim().toLowerCase())) {
        setError(txt.duplicateName)
        setLoading(false)
        return
      }
      setNick(playerName)
      const res = await api.post(
        `/api/game/join?roomCode=${encodeURIComponent(roomCode)}&playerName=${encodeURIComponent(playerName)}&avatarId=${avatarId}${roomPassword ? `&password=${encodeURIComponent(roomPassword)}` : ''}`
      )
      setPlayer(res.data.player)
      if (res.data.token) {
        localStorage.setItem('token', res.data.token)
      }
      setTimeout(() => {
        navigate(`/play/lobby?roomCode=${encodeURIComponent(roomCode)}&playerId=${encodeURIComponent(res.data.player.id)}`)
      }, 1000)
    } catch (e: unknown) {
      const axiosError = e as { response?: { data?: { error?: string } } }
      const apiError = axiosError?.response?.data?.error
      if (apiError === 'RoomPasswordRequired') {
        setError(txt.passwordRequired)
      } else if (apiError === 'InvalidRoomPassword') {
        setError(txt.invalidPassword)
      } else if (apiError === 'RoomNotFound') {
        setError(txt.roomNotFound)
      } else {
        setError(t('error'))
      }
    } finally {
      setLoading(false)
    }
  }

  const features = [
    { icon: Users, title: txt.community, description: txt.communityDesc, color: 'text-brand-500' },
    { icon: Trophy, title: txt.competition, description: txt.competitionDesc, color: 'text-yellow-500' },
    { icon: Rocket, title: txt.quickJoin, description: txt.quickJoinDesc, color: 'text-green-500' },
  ]

  return (
    <div className="p-4 md:p-6 lg:p-8 pb-20 md:pb-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center mb-8"
        >
          <h1 className="text-3xl md:text-4xl font-display font-bold text-gray-900 dark:text-white mb-2">
            {txt.title}
          </h1>
          <p className="text-gray-600 dark:text-gray-400">
            {txt.subtitle}
          </p>
        </motion.div>

        {/* Features */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8"
        >
          {features.map((feature, index) => {
            const Icon = feature.icon
            return (
              <Card key={index} className="p-4 text-center">
                <Icon className={`h-10 w-10 mx-auto mb-3 ${feature.color}`} />
                <h3 className="font-semibold text-gray-900 dark:text-white mb-1">
                  {feature.title}
                </h3>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  {feature.description}
                </p>
              </Card>
            )
          })}
        </motion.div>

        {/* Join Form */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <Card className="p-6 md:p-8 max-w-md mx-auto">
            <div className="flex items-center gap-2 mb-6">
              <LogIn className="h-5 w-5 text-brand-500" />
              <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
                {txt.joinRoom}
              </h2>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  {txt.roomCode}
                </label>
                <Input
                  value={roomCode}
                  onChange={(e) => setRoomCode(e.target.value.toUpperCase())}
                  placeholder="ABCD12"
                  className="font-mono text-lg tracking-wider"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  {txt.playerName}
                </label>
                <Input
                  value={playerName}
                  onChange={(e) => {
                    setPlayerName(e.target.value)
                    setNick(e.target.value)
                  }}
                  placeholder={txt.playerName}
                />
              </div>

              <div>
                <label className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  <Lock className="h-4 w-4" />
                  {txt.roomPassword}
                </label>
                <Input
                  type="password"
                  value={roomPassword}
                  onChange={(e) => setRoomPassword(e.target.value)}
                  placeholder="••••••••"
                />
              </div>

              <Button
                className="w-full gap-2"
                size="lg"
                onClick={handleJoin}
                disabled={loading || !roomCode || !playerName}
              >
                <LogIn className="h-5 w-5" />
                {loading ? t('loading') : txt.joinRoom}
              </Button>

              {error && (
                <div className="p-4 rounded-lg bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800">
                  <p className="text-red-600 dark:text-red-400 text-sm">{error}</p>
                </div>
              )}

              {player && (
                <div className="p-4 rounded-lg bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800">
                  <p className="text-green-600 dark:text-green-400 text-sm">
                    {txt.joinSuccess}<br />
                    {txt.playerName}: <span className="font-bold">{player.name}</span>
                  </p>
                </div>
              )}
            </div>
          </Card>
        </motion.div>

        {/* Active Rooms */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="mt-8"
        >
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
            {txt.activeRooms}
          </h2>

          {roomsLoading ? (
            <div className="flex justify-center py-8">
              <div className="w-8 h-8 border-4 border-brand-200 border-t-brand-500 rounded-full animate-spin" />
            </div>
          ) : rooms.length === 0 ? (
            <Card className="p-6 text-center">
              <p className="text-gray-500 dark:text-gray-400">{txt.noRooms}</p>
            </Card>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {rooms.map((room) => (
                <Card key={room.roomCode} className="p-4 hover:shadow-lg transition-shadow">
                  <div className="flex items-center justify-between mb-3">
                    <h3 className="font-game text-lg text-brand-600 dark:text-brand-400">
                      {room.roomCode}
                    </h3>
                    <Badge variant="secondary">
                      {room.players?.length ?? 0} {lang === 'tr' ? 'oyuncu' : 'players'}
                    </Badge>
                  </div>
                  <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400 mb-3">
                    <Users className="h-4 w-4" />
                    <span>{room.players?.find(p => p.id === room.hostId)?.name ?? 'Host'}</span>
                  </div>
                  <Button
                    variant="outline"
                    className="w-full"
                    onClick={() => {
                      setRoomCode(room.roomCode)
                      window.scrollTo({ top: 0, behavior: 'smooth' })
                    }}
                  >
                    {lang === 'tr' ? 'Seç' : 'Select'}
                  </Button>
                </Card>
              ))}
            </div>
          )}
        </motion.div>
      </div>
    </div>
  )
}

export default JoinRoomPage
