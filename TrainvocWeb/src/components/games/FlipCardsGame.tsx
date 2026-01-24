import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { RotateCcw, Clock, Trophy } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Celebration } from '@/components/game/Celebration'

interface FlipCard {
  id: string
  content: string
  pairId: string
  type: 'word' | 'translation'
}

interface FlipCardsGameProps {
  words: Array<{ word: string; translation: string }>
  gridSize?: '4x4' | '4x6' | '6x6'
  onComplete?: (moves: number, timeSeconds: number) => void
}

export function FlipCardsGame({
  words,
  gridSize = '4x4',
  onComplete,
}: FlipCardsGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const [cards, setCards] = useState<FlipCard[]>([])
  const [flippedCards, setFlippedCards] = useState<string[]>([])
  const [matchedPairs, setMatchedPairs] = useState<string[]>([])
  const [moves, setMoves] = useState(0)
  const [time, setTime] = useState(0)
  const [gameComplete, setGameComplete] = useState(false)
  const [showCelebration, setShowCelebration] = useState(false)
  const [isChecking, setIsChecking] = useState(false)

  const content = {
    en: {
      title: 'Memory Match',
      moves: 'Moves',
      time: 'Time',
      pairs: 'Pairs',
      complete: 'Congratulations!',
      playAgain: 'Play Again',
      foundAll: 'You found all pairs!',
    },
    tr: {
      title: 'Hafıza Eşleştirme',
      moves: 'Hamle',
      time: 'Süre',
      pairs: 'Eşleşme',
      complete: 'Tebrikler!',
      playAgain: 'Tekrar Oyna',
      foundAll: 'Tüm eşleşmeleri buldun!',
    },
  }

  const t = content[lang]

  // Get grid dimensions
  const getGridConfig = () => {
    switch (gridSize) {
      case '4x4':
        return { cols: 4, rows: 4, pairs: 8 }
      case '4x6':
        return { cols: 4, rows: 6, pairs: 12 }
      case '6x6':
        return { cols: 6, rows: 6, pairs: 18 }
      default:
        return { cols: 4, rows: 4, pairs: 8 }
    }
  }

  const { cols, pairs } = getGridConfig()

  // Initialize cards
  useEffect(() => {
    const selectedWords = words.slice(0, pairs)
    const cardPairs: FlipCard[] = []

    selectedWords.forEach((word, index) => {
      cardPairs.push({
        id: `word-${index}`,
        content: word.word,
        pairId: `pair-${index}`,
        type: 'word',
      })
      cardPairs.push({
        id: `trans-${index}`,
        content: word.translation,
        pairId: `pair-${index}`,
        type: 'translation',
      })
    })

    // Shuffle cards
    const shuffled = cardPairs.sort(() => Math.random() - 0.5)
    setCards(shuffled)
  }, [words, pairs])

  // Timer
  useEffect(() => {
    if (gameComplete) return

    const timer = setInterval(() => {
      setTime((prev) => prev + 1)
    }, 1000)

    return () => clearInterval(timer)
  }, [gameComplete])

  // Check for matches
  useEffect(() => {
    if (flippedCards.length !== 2) return

    setIsChecking(true)
    const [first, second] = flippedCards
    const firstCard = cards.find((c) => c.id === first)
    const secondCard = cards.find((c) => c.id === second)

    if (firstCard && secondCard && firstCard.pairId === secondCard.pairId) {
      // Match found
      setMatchedPairs((prev) => [...prev, firstCard.pairId])
      setFlippedCards([])
      setIsChecking(false)

      // Check if game complete
      if (matchedPairs.length + 1 === pairs) {
        setGameComplete(true)
        setShowCelebration(true)
        onComplete?.(moves + 1, time)
      }
    } else {
      // No match - flip back after delay
      setTimeout(() => {
        setFlippedCards([])
        setIsChecking(false)
      }, 1000)
    }
  }, [flippedCards, cards, matchedPairs, pairs, moves, time, onComplete])

  const handleCardClick = (cardId: string) => {
    if (isChecking) return
    if (flippedCards.includes(cardId)) return
    if (matchedPairs.includes(cards.find((c) => c.id === cardId)?.pairId ?? '')) return
    if (flippedCards.length >= 2) return

    setFlippedCards((prev) => [...prev, cardId])
    if (flippedCards.length === 1) {
      setMoves((prev) => prev + 1)
    }
  }

  const isFlipped = (cardId: string) => {
    return flippedCards.includes(cardId) || matchedPairs.includes(cards.find((c) => c.id === cardId)?.pairId ?? '')
  }

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins}:${secs.toString().padStart(2, '0')}`
  }

  const resetGame = () => {
    setCards(cards.sort(() => Math.random() - 0.5))
    setFlippedCards([])
    setMatchedPairs([])
    setMoves(0)
    setTime(0)
    setGameComplete(false)
    setShowCelebration(false)
  }

  if (gameComplete) {
    return (
      <div className="min-h-screen flex items-center justify-center p-4 bg-gradient-to-br from-brand-500 to-purple-600">
        <Celebration show={showCelebration} />
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
        >
          <Card className="p-8 text-center max-w-md">
            <Trophy className="h-16 w-16 mx-auto text-yellow-500 mb-4" />
            <h2 className="text-3xl font-game text-gray-900 dark:text-white mb-2">
              {t.complete}
            </h2>
            <p className="text-gray-600 dark:text-gray-400 mb-6">{t.foundAll}</p>
            <div className="flex justify-center gap-8 mb-6">
              <div className="text-center">
                <p className="text-2xl font-bold text-brand-600">{moves}</p>
                <p className="text-sm text-gray-500">{t.moves}</p>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold text-brand-600">{formatTime(time)}</p>
                <p className="text-sm text-gray-500">{t.time}</p>
              </div>
            </div>
            <Button onClick={resetGame} className="w-full gap-2">
              <RotateCcw className="h-4 w-4" />
              {t.playAgain}
            </Button>
          </Card>
        </motion.div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-500 to-purple-600 p-4">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-game text-white">{t.title}</h1>
          <div className="flex gap-4">
            <Badge variant="secondary" className="text-lg px-4 py-2">
              {t.moves}: {moves}
            </Badge>
            <Badge variant="secondary" className="text-lg px-4 py-2 flex items-center gap-2">
              <Clock className="h-4 w-4" />
              {formatTime(time)}
            </Badge>
            <Badge variant="secondary" className="text-lg px-4 py-2">
              {t.pairs}: {matchedPairs.length}/{pairs}
            </Badge>
          </div>
        </div>

        {/* Card Grid */}
        <div
          className="grid gap-3"
          style={{ gridTemplateColumns: `repeat(${cols}, 1fr)` }}
        >
          {cards.map((card) => {
            const flipped = isFlipped(card.id)
            const matched = matchedPairs.includes(card.pairId)

            return (
              <motion.div
                key={card.id}
                className="aspect-square perspective-1000"
                whileHover={!flipped ? { scale: 1.05 } : {}}
                whileTap={!flipped ? { scale: 0.95 } : {}}
              >
                <motion.div
                  className={`relative w-full h-full cursor-pointer preserve-3d transition-transform duration-500 ${
                    flipped ? 'rotate-y-180' : ''
                  }`}
                  onClick={() => handleCardClick(card.id)}
                >
                  {/* Front (hidden) */}
                  <div className="absolute inset-0 backface-hidden rounded-xl bg-gradient-to-br from-brand-400 to-brand-600 shadow-lg flex items-center justify-center">
                    <div className="text-4xl text-white/30">?</div>
                  </div>

                  {/* Back (content) */}
                  <div
                    className={`absolute inset-0 backface-hidden rotate-y-180 rounded-xl shadow-lg flex items-center justify-center p-2 ${
                      matched
                        ? 'bg-green-500'
                        : card.type === 'word'
                        ? 'bg-white dark:bg-gray-800'
                        : 'bg-yellow-100 dark:bg-yellow-900'
                    }`}
                  >
                    <p
                      className={`text-center font-semibold ${
                        matched ? 'text-white' : 'text-gray-900 dark:text-white'
                      } ${cards.length > 16 ? 'text-sm' : 'text-base'}`}
                    >
                      {card.content}
                    </p>
                  </div>
                </motion.div>
              </motion.div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
