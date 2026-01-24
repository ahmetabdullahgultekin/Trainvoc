import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Users, Trophy, Rocket, Plus, Clock, HelpCircle, BarChart3, Lock } from 'lucide-react'
import api from '../api'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import useNick from '../components/shared/useNick'
import useProfile from '../components/shared/useProfile'
import type { GameRoom } from '../interfaces/game'

function CreateRoomPage() {
  const { t, i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'
  const navigate = useNavigate()
  const [hostName, setHostName] = useState('')
  const [room, setRoom] = useState<GameRoom | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [rooms, setRooms] = useState<GameRoom[]>([])
  const [roomsLoading, setRoomsLoading] = useState(true)
  const [nick, setNick] = useNick()
  const { avatar } = useProfile()
  const avatarId = avatar ? avatar : (Math.floor(Math.random() * 10) + 1).toString()

  const [settings, setSettings] = useState({
    questionDuration: 60,
    optionCount: 4,
    level: 'A1',
    totalQuestionCount: 5,
  })
  const [roomPassword, setRoomPassword] = useState('')

  const content = {
    en: {
      title: 'Create Room',
      subtitle: 'Create your own room, choose settings, and invite friends!',
      yourRoom: 'Your Room',
      yourRoomDesc: 'Start a game with your own rules!',
      invite: 'Invite',
      inviteDesc: 'Invite friends, compete together!',
      quickStart: 'Quick Start',
      quickStartDesc: 'Create room, start immediately!',
      hostName: 'Your Name',
      questionDuration: 'Question Duration',
      optionCount: 'Options Count',
      level: 'Level',
      totalQuestions: 'Total Questions',
      roomPassword: 'Room Password (optional)',
      roomPasswordHint: 'Set a password to protect your room.',
      createRoom: 'Create Room',
      activeRooms: 'Active Rooms',
      noRooms: 'No active rooms. Create one now!',
      roomCreated: 'Room created!',
      roomCode: 'Room Code',
    },
    tr: {
      title: 'Oda Oluştur',
      subtitle: 'Kendi odanı oluştur, ayarlarını seç ve arkadaşlarını davet et!',
      yourRoom: 'Kendi Odan',
      yourRoomDesc: 'Kendi kurallarınla oyun başlat!',
      invite: 'Davet Et',
      inviteDesc: 'Arkadaşlarını davet et, birlikte yarış!',
      quickStart: 'Hızlı Başlangıç',
      quickStartDesc: 'Odanı oluştur, hemen başla!',
      hostName: 'İsminiz',
      questionDuration: 'Soru Süresi',
      optionCount: 'Şık Sayısı',
      level: 'Seviye',
      totalQuestions: 'Toplam Soru',
      roomPassword: 'Oda Şifresi (isteğe bağlı)',
      roomPasswordHint: 'Odanı korumak için şifre belirleyebilirsin.',
      createRoom: 'Oda Oluştur',
      activeRooms: 'Aktif Odalar',
      noRooms: 'Şu anda aktif oda yok. Hemen bir oda oluştur!',
      roomCreated: 'Oda oluşturuldu!',
      roomCode: 'Oda Kodu',
    },
  }

  const txt = content[lang]

  const levels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2', 'Mixed']
  const optionCounts = [2, 3, 4]
  const durations = [30, 60, 90, 120]
  const questionCounts = [5, 10, 15, 20, 25]

  useEffect(() => {
    setRoomsLoading(true)
    api.get('/api/game/rooms')
      .then(res => setRooms(res.data))
      .catch(() => setRooms([]))
      .finally(() => setRoomsLoading(false))
  }, [room])

  useEffect(() => {
    setHostName(nick)
  }, [nick])

  const handleCreate = async () => {
    setLoading(true)
    setError('')
    setRoom(null)
    try {
      setNick(hostName)
      const res = await api.post(
        `/api/game/create?hostName=${encodeURIComponent(hostName)}&hostWantsToJoin=true&avatarId=${avatarId}${roomPassword ? `&password=${encodeURIComponent(roomPassword)}` : ''}`,
        settings
      )
      setRoom(res.data)
      const roomCode = res.data.roomCode
      const playerId = res.data.player?.id
      if (res.data.token) {
        localStorage.setItem('token', res.data.token)
      }
      if (roomCode && playerId) {
        navigate(`/play/lobby?roomCode=${encodeURIComponent(roomCode)}&playerId=${encodeURIComponent(playerId)}`)
      }
    } catch {
      setError(t('error'))
    } finally {
      setLoading(false)
    }
  }

  const features = [
    { icon: Users, title: txt.yourRoom, description: txt.yourRoomDesc, color: 'text-brand-500' },
    { icon: Trophy, title: txt.invite, description: txt.inviteDesc, color: 'text-yellow-500' },
    { icon: Rocket, title: txt.quickStart, description: txt.quickStartDesc, color: 'text-green-500' },
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

        {/* Create Room Form */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <Card className="p-6 md:p-8">
            <div className="flex items-center gap-2 mb-6">
              <Plus className="h-5 w-5 text-brand-500" />
              <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
                {txt.createRoom}
              </h2>
            </div>

            <div className="space-y-6">
              {/* Host Name */}
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  {txt.hostName}
                </label>
                <Input
                  value={hostName}
                  onChange={(e) => {
                    setHostName(e.target.value)
                    setNick(e.target.value)
                  }}
                  placeholder={txt.hostName}
                />
              </div>

              {/* Settings Grid */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                {/* Question Duration */}
                <div>
                  <label className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    <Clock className="h-4 w-4" />
                    {txt.questionDuration}
                  </label>
                  <div className="flex flex-wrap gap-2">
                    {durations.map((d) => (
                      <button
                        key={d}
                        onClick={() => setSettings(s => ({ ...s, questionDuration: d }))}
                        className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                          settings.questionDuration === d
                            ? 'bg-brand-500 text-white'
                            : 'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'
                        }`}
                      >
                        {d}s
                      </button>
                    ))}
                  </div>
                </div>

                {/* Option Count */}
                <div>
                  <label className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    <HelpCircle className="h-4 w-4" />
                    {txt.optionCount}
                  </label>
                  <div className="flex flex-wrap gap-2">
                    {optionCounts.map((c) => (
                      <button
                        key={c}
                        onClick={() => setSettings(s => ({ ...s, optionCount: c }))}
                        className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                          settings.optionCount === c
                            ? 'bg-brand-500 text-white'
                            : 'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'
                        }`}
                      >
                        {c}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Level */}
                <div>
                  <label className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    <BarChart3 className="h-4 w-4" />
                    {txt.level}
                  </label>
                  <div className="flex flex-wrap gap-2">
                    {levels.map((l) => (
                      <button
                        key={l}
                        onClick={() => setSettings(s => ({ ...s, level: l }))}
                        className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                          settings.level === l
                            ? 'bg-brand-500 text-white'
                            : 'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'
                        }`}
                      >
                        {l}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Total Questions */}
                <div>
                  <label className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    {txt.totalQuestions}
                  </label>
                  <div className="flex flex-wrap gap-2">
                    {questionCounts.map((q) => (
                      <button
                        key={q}
                        onClick={() => setSettings(s => ({ ...s, totalQuestionCount: q }))}
                        className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                          settings.totalQuestionCount === q
                            ? 'bg-brand-500 text-white'
                            : 'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700'
                        }`}
                      >
                        {q}
                      </button>
                    ))}
                  </div>
                </div>
              </div>

              {/* Room Password */}
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
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  {txt.roomPasswordHint}
                </p>
              </div>

              {/* Submit */}
              <Button
                className="w-full gap-2"
                size="lg"
                onClick={handleCreate}
                disabled={loading || !hostName}
              >
                <Plus className="h-5 w-5" />
                {loading ? t('loading') : txt.createRoom}
              </Button>

              {error && (
                <div className="p-4 rounded-lg bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800">
                  <p className="text-red-600 dark:text-red-400 text-sm">{error}</p>
                </div>
              )}

              {room && (
                <div className="p-4 rounded-lg bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800">
                  <p className="text-green-600 dark:text-green-400 text-sm">
                    {txt.roomCreated}<br />
                    {txt.roomCode}: <span className="font-bold">{room.roomCode}</span>
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
                    onClick={() => navigate(`/play/join?roomCode=${room.roomCode}`)}
                  >
                    {lang === 'tr' ? 'Katıl' : 'Join'}
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

export default CreateRoomPage
