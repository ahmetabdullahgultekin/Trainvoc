import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Header, Footer } from '@/components/layout'
import { GameCard, gameTypes, type GameTypeId, type Category } from '@/components/game/GameCard'
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { staggerContainerVariants, staggerItemVariants } from '@/lib/animations'

const categories: { id: Category | 'all'; label: { en: string; tr: string } }[] = [
  { id: 'all', label: { en: 'All Games', tr: 'Tüm Oyunlar' } },
  { id: 'vocabulary', label: { en: 'Vocabulary', tr: 'Kelime' } },
  { id: 'memory', label: { en: 'Memory', tr: 'Hafıza' } },
  { id: 'speed', label: { en: 'Speed', tr: 'Hız' } },
  { id: 'listening', label: { en: 'Listening', tr: 'Dinleme' } },
]

function GameMenuPage() {
  const navigate = useNavigate()
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'
  const [selectedCategory, setSelectedCategory] = useState<Category | 'all'>('all')

  const filteredGames = Object.values(gameTypes).filter((game) => {
    if (selectedCategory === 'all') return true
    return game.category === selectedCategory
  })

  const handlePlayGame = (gameId: GameTypeId) => {
    // Navigate to the game or create room with this game type
    navigate(`/play/create?gameType=${gameId}`)
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Header />

      <main className="pt-24 pb-16">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          {/* Page header */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-center mb-12"
          >
            <h1 className="text-4xl md:text-5xl font-display font-bold text-gray-900 dark:text-white mb-4">
              {lang === 'tr' ? 'Oyun Seç' : 'Choose Your Game'}
            </h1>
            <p className="text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
              {lang === 'tr'
                ? '10 farklı oyun modundan birini seçin ve kelime haznenizi geliştirin'
                : 'Select from 10 different game modes and improve your vocabulary'}
            </p>
          </motion.div>

          {/* Category tabs */}
          <div className="flex justify-center mb-8">
            <Tabs
              value={selectedCategory}
              onValueChange={(value) => setSelectedCategory(value as Category | 'all')}
            >
              <TabsList className="flex-wrap justify-center">
                {categories.map((cat) => (
                  <TabsTrigger key={cat.id} value={cat.id}>
                    {cat.label[lang]}
                  </TabsTrigger>
                ))}
              </TabsList>
            </Tabs>
          </div>

          {/* Game cards grid */}
          <motion.div
            className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
            variants={staggerContainerVariants}
            initial="initial"
            animate="animate"
            key={selectedCategory} // Re-animate when category changes
          >
            {filteredGames.map((game) => (
              <motion.div key={game.id} variants={staggerItemVariants}>
                <GameCard
                  gameType={game.id}
                  lang={lang}
                  onPlay={() => handlePlayGame(game.id)}
                />
              </motion.div>
            ))}
          </motion.div>

          {/* Empty state */}
          {filteredGames.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-500 dark:text-gray-400">
                {lang === 'tr'
                  ? 'Bu kategoride oyun bulunamadı'
                  : 'No games found in this category'}
              </p>
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  )
}

export default GameMenuPage
