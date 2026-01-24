import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Plus, Users, Trophy, Gamepad2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

function PlayPage() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const content = {
    en: {
      welcome: 'Welcome to the Game Area',
      subtitle: 'Start playing vocabulary games with friends or practice solo',
      createRoom: 'Create Room',
      createRoomDesc: 'Start a new game room and invite friends',
      joinRoom: 'Join Room',
      joinRoomDesc: 'Join an existing game with a room code',
      browseGames: 'Browse Games',
      browseGamesDesc: 'Explore all 10 game modes',
      leaderboard: 'Leaderboard',
      leaderboardDesc: 'See top players worldwide',
    },
    tr: {
      welcome: 'Oyun Alanına Hoşgeldin',
      subtitle: 'Arkadaşlarınla kelime oyunları oyna veya tek başına pratik yap',
      createRoom: 'Oda Kur',
      createRoomDesc: 'Yeni bir oyun odası oluştur ve arkadaşlarını davet et',
      joinRoom: 'Odaya Katıl',
      joinRoomDesc: 'Oda koduyla mevcut bir oyuna katıl',
      browseGames: 'Oyunlara Göz At',
      browseGamesDesc: '10 farklı oyun modunu keşfet',
      leaderboard: 'Liderlik Tablosu',
      leaderboardDesc: 'Dünya genelindeki en iyi oyuncuları gör',
    },
  }

  const t = content[lang]

  const actions = [
    {
      icon: Plus,
      title: t.createRoom,
      description: t.createRoomDesc,
      href: '/play/create',
      color: 'bg-brand-500 hover:bg-brand-600',
    },
    {
      icon: Users,
      title: t.joinRoom,
      description: t.joinRoomDesc,
      href: '/play/join',
      color: 'bg-game-blue hover:bg-blue-700',
    },
    {
      icon: Gamepad2,
      title: t.browseGames,
      description: t.browseGamesDesc,
      href: '/games',
      color: 'bg-game-green hover:bg-green-700',
    },
    {
      icon: Trophy,
      title: t.leaderboard,
      description: t.leaderboardDesc,
      href: '/play/leaderboard',
      color: 'bg-game-yellow hover:bg-yellow-600',
    },
  ]

  return (
    <div className="p-4 md:p-6 lg:p-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center mb-8"
        >
          <h1 className="text-3xl md:text-4xl font-display font-bold text-gray-900 dark:text-white mb-2">
            {t.welcome}
          </h1>
          <p className="text-gray-600 dark:text-gray-400 text-lg">
            {t.subtitle}
          </p>
        </motion.div>

        {/* Action cards grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 md:gap-6">
          {actions.map((action, index) => {
            const Icon = action.icon
            return (
              <motion.div
                key={action.href}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
              >
                <Link to={action.href}>
                  <Card className="p-6 hover:shadow-lg transition-all cursor-pointer group">
                    <div className="flex items-start gap-4">
                      <div
                        className={`p-3 rounded-xl ${action.color} text-white transition-colors`}
                      >
                        <Icon className="h-6 w-6" />
                      </div>
                      <div className="flex-1">
                        <h3 className="font-semibold text-gray-900 dark:text-white group-hover:text-brand-600 dark:group-hover:text-brand-400 transition-colors">
                          {action.title}
                        </h3>
                        <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                          {action.description}
                        </p>
                      </div>
                    </div>
                  </Card>
                </Link>
              </motion.div>
            )
          })}
        </div>

        {/* Quick play section */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="mt-8"
        >
          <Card className="p-6 bg-gradient-to-br from-brand-500 to-purple-600 text-white border-0">
            <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
              <div>
                <h3 className="text-xl font-semibold mb-1">
                  {lang === 'tr' ? 'Hızlı Başla' : 'Quick Start'}
                </h3>
                <p className="text-white/80 text-sm">
                  {lang === 'tr'
                    ? 'Rastgele bir oyuna hemen katıl'
                    : 'Jump into a random game instantly'}
                </p>
              </div>
              <Link to="/play/lobby">
                <Button variant="secondary" size="lg" className="whitespace-nowrap">
                  {lang === 'tr' ? 'Lobiye Git' : 'Go to Lobby'}
                </Button>
              </Link>
            </div>
          </Card>
        </motion.div>
      </div>
    </div>
  )
}

export default PlayPage
