import { motion } from 'framer-motion'
import {
  ListChecks, Layers, Timer, TextCursor, Shuffle,
  Headphones, Image, Keyboard, Rocket, Lightbulb,
  type LucideIcon
} from 'lucide-react'
import { cn } from '@/lib/utils'
import { cardHoverVariants } from '@/lib/animations'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'

export type GameTypeId =
  | 'multiple-choice'
  | 'flip-cards'
  | 'speed-match'
  | 'fill-in-blank'
  | 'word-scramble'
  | 'listening-quiz'
  | 'picture-match'
  | 'spelling-challenge'
  | 'translation-race'
  | 'context-clues'

export type Difficulty = 'easy' | 'medium' | 'hard'
export type Category = 'vocabulary' | 'memory' | 'speed' | 'listening'

interface GameType {
  id: GameTypeId
  name: { en: string; tr: string }
  description: { en: string; tr: string }
  difficulty: Difficulty
  category: Category
  icon: LucideIcon
  color: string
}

export const gameTypes: Record<GameTypeId, GameType> = {
  'multiple-choice': {
    id: 'multiple-choice',
    name: { en: 'Multiple Choice', tr: 'Çoktan Seçmeli' },
    description: {
      en: 'Select the correct translation from 4 options',
      tr: '4 seçenek arasından doğru çeviriyi seçin'
    },
    difficulty: 'easy',
    category: 'vocabulary',
    icon: ListChecks,
    color: 'from-indigo-500 to-purple-500',
  },
  'flip-cards': {
    id: 'flip-cards',
    name: { en: 'Flip Cards', tr: 'Kart Eşleştirme' },
    description: {
      en: 'Match word pairs in a memory game',
      tr: 'Hafıza oyununda kelime çiftlerini eşleştirin'
    },
    difficulty: 'easy',
    category: 'memory',
    icon: Layers,
    color: 'from-blue-500 to-cyan-500',
  },
  'speed-match': {
    id: 'speed-match',
    name: { en: 'Speed Match', tr: 'Hız Eşleştirme' },
    description: {
      en: '60 seconds to match as many words as possible',
      tr: '60 saniyede mümkün olduğunca çok kelime eşleştirin'
    },
    difficulty: 'hard',
    category: 'speed',
    icon: Timer,
    color: 'from-red-500 to-orange-500',
  },
  'fill-in-blank': {
    id: 'fill-in-blank',
    name: { en: 'Fill in the Blank', tr: 'Boşluk Doldurma' },
    description: {
      en: 'Complete sentences with missing words',
      tr: 'Eksik kelimeleri tamamlayın'
    },
    difficulty: 'medium',
    category: 'vocabulary',
    icon: TextCursor,
    color: 'from-green-500 to-emerald-500',
  },
  'word-scramble': {
    id: 'word-scramble',
    name: { en: 'Word Scramble', tr: 'Kelime Bulmaca' },
    description: {
      en: 'Unscramble the letters to form words',
      tr: 'Harfleri düzenleyerek kelime oluşturun'
    },
    difficulty: 'medium',
    category: 'vocabulary',
    icon: Shuffle,
    color: 'from-yellow-500 to-amber-500',
  },
  'listening-quiz': {
    id: 'listening-quiz',
    name: { en: 'Listening Quiz', tr: 'Dinleme Testi' },
    description: {
      en: 'Listen to the word and select the meaning',
      tr: 'Kelimeyi dinleyin ve anlamını seçin'
    },
    difficulty: 'medium',
    category: 'listening',
    icon: Headphones,
    color: 'from-pink-500 to-rose-500',
  },
  'picture-match': {
    id: 'picture-match',
    name: { en: 'Picture Match', tr: 'Resim Eşleştirme' },
    description: {
      en: 'Match words with their visual representations',
      tr: 'Kelimeleri görsel temsillerle eşleştirin'
    },
    difficulty: 'easy',
    category: 'memory',
    icon: Image,
    color: 'from-teal-500 to-green-500',
  },
  'spelling-challenge': {
    id: 'spelling-challenge',
    name: { en: 'Spelling Challenge', tr: 'Yazım Yarışması' },
    description: {
      en: 'Type the correct spelling of the word',
      tr: 'Kelimenin doğru yazımını yazın'
    },
    difficulty: 'hard',
    category: 'vocabulary',
    icon: Keyboard,
    color: 'from-violet-500 to-purple-500',
  },
  'translation-race': {
    id: 'translation-race',
    name: { en: 'Translation Race', tr: 'Çeviri Yarışı' },
    description: {
      en: '90 seconds of fast-paced translation challenges',
      tr: '90 saniyelik hızlı çeviri mücadelesi'
    },
    difficulty: 'hard',
    category: 'speed',
    icon: Rocket,
    color: 'from-orange-500 to-red-500',
  },
  'context-clues': {
    id: 'context-clues',
    name: { en: 'Context Clues', tr: 'Bağlam İpuçları' },
    description: {
      en: 'Guess words from progressive hints',
      tr: 'İlerleyen ipuçlarından kelimeleri tahmin edin'
    },
    difficulty: 'medium',
    category: 'vocabulary',
    icon: Lightbulb,
    color: 'from-amber-500 to-yellow-500',
  },
}

interface GameCardProps {
  gameType: GameTypeId
  lang?: 'en' | 'tr'
  onPlay?: () => void
  className?: string
}

export function GameCard({ gameType, lang = 'en', onPlay, className }: GameCardProps) {
  const game = gameTypes[gameType]
  const Icon = game.icon

  return (
    <motion.div
      className={cn(
        'relative overflow-hidden rounded-2xl bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 p-6',
        'hover:shadow-card-hover transition-shadow',
        className
      )}
      variants={cardHoverVariants}
      initial="rest"
      whileHover="hover"
      whileTap="tap"
    >
      {/* Gradient accent */}
      <div
        className={cn(
          'absolute top-0 left-0 right-0 h-1 bg-gradient-to-r',
          game.color
        )}
      />

      {/* Content */}
      <div className="flex flex-col h-full">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div
            className={cn(
              'p-3 rounded-xl bg-gradient-to-br',
              game.color
            )}
          >
            <Icon className="h-6 w-6 text-white" />
          </div>
          <Badge variant={game.difficulty}>{game.difficulty}</Badge>
        </div>

        {/* Title & Description */}
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
          {game.name[lang]}
        </h3>
        <p className="text-sm text-gray-500 dark:text-gray-400 flex-1 mb-4">
          {game.description[lang]}
        </p>

        {/* Play button */}
        <Button
          onClick={onPlay}
          className="w-full"
          size="lg"
        >
          {lang === 'tr' ? 'Oyna' : 'Play'}
        </Button>
      </div>
    </motion.div>
  )
}
