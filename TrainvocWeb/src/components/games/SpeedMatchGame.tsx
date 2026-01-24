import { useState, useEffect, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Zap, Clock, Flame, RotateCcw, Trophy, Star } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Celebration } from '@/components/game/Celebration'

interface WordPair {
  id: string
  word: string
  translation: string
}

interface SpeedMatchGameProps {
  words: WordPair[]
  duration?: number
  onComplete?: (results: { score: number; matchCount: number; maxCombo: number }) => void
}

export function SpeedMatchGame({
  words,
  duration = 60,
  onComplete,
}: SpeedMatchGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const [timeLeft, setTimeLeft] = useState(duration)
  const [score, setScore] = useState(0)
  const [combo, setCombo] = useState(0)
  const [maxCombo, setMaxCombo] = useState(0)
  const [matchCount, setMatchCount] = useState(0)
  const [selectedLeft, setSelectedLeft] = useState<string | null>(null)
  const [selectedRight, setSelectedRight] = useState<string | null>(null)
  const [matchedPairs, setMatchedPairs] = useState<string[]>([])
  const [wrongPair, setWrongPair] = useState<{ left?: string; right?: string } | null>(null)
  const [gameComplete, setGameComplete] = useState(false)
  const [showCelebration, setShowCelebration] = useState(false)
  const [leftWords, setLeftWords] = useState<WordPair[]>([])
  const [rightWords, setRightWords] = useState<WordPair[]>([])

  const content = {
    en: {
      title: 'Speed Match',
      instruction: 'Match words with their translations as fast as you can!',
      timeLeft: 'Time',
      score: 'Score',
      combo: 'Combo',
      matches: 'Matches',
      gameOver: 'Time\'s Up!',
      finalScore: 'Final Score',
      playAgain: 'Play Again',
      bonusTime: '+2s',
    },
    tr: {
      title: 'Hızlı Eşleştirme',
      instruction: 'Kelimeleri çevirileriyle olabildiğince hızlı eşleştir!',
      timeLeft: 'Süre',
      score: 'Puan',
      combo: 'Kombo',
      matches: 'Eşleşme',
      gameOver: 'Süre Doldu!',
      finalScore: 'Son Puan',
      playAgain: 'Tekrar Oyna',
      bonusTime: '+2sn',
    },
  }

  const t = content[lang]

  const initializeWords = useCallback(() => {
    const shuffledWords = [...words].sort(() => Math.random() - 0.5).slice(0, 6)
    setLeftWords(shuffledWords)
    setRightWords([...shuffledWords].sort(() => Math.random() - 0.5))
    setMatchedPairs([])
  }, [words])

  useEffect(() => {
    initializeWords()
  }, [initializeWords])

  useEffect(() => {
    if (gameComplete) return

    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          setGameComplete(true)
          onComplete?.({ score, matchCount, maxCombo })
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(timer)
  }, [gameComplete, score, matchCount, maxCombo, onComplete])

  useEffect(() => {
    if (!selectedLeft || !selectedRight) return

    const leftWord = leftWords.find((w) => w.id === selectedLeft)
    const rightWord = rightWords.find((w) => w.id === selectedRight)

    if (leftWord && rightWord && leftWord.id === rightWord.id) {
      setMatchedPairs((prev) => [...prev, leftWord.id])
      setMatchCount((prev) => prev + 1)

      const newCombo = combo + 1
      setCombo(newCombo)
      setMaxCombo((prev) => Math.max(prev, newCombo))

      const comboBonus = newCombo >= 3 ? 50 : newCombo >= 2 ? 25 : 0
      setScore((prev) => prev + 100 + comboBonus)

      if (newCombo >= 3) {
        setTimeLeft((prev) => prev + 2)
        setShowCelebration(true)
        setTimeout(() => setShowCelebration(false), 1000)
      }

      if (matchedPairs.length + 1 >= leftWords.length) {
        setTimeout(() => initializeWords(), 500)
      }
    } else {
      setWrongPair({ left: selectedLeft, right: selectedRight })
      setCombo(0)
      setTimeout(() => setWrongPair(null), 500)
    }

    setTimeout(() => {
      setSelectedLeft(null)
      setSelectedRight(null)
    }, 300)
  }, [selectedLeft, selectedRight, leftWords, rightWords, combo, matchedPairs, initializeWords])

  const handleLeftClick = (id: string) => {
    if (matchedPairs.includes(id) || selectedLeft === id) return
    setSelectedLeft(id)
  }

  const handleRightClick = (id: string) => {
    if (matchedPairs.includes(id) || selectedRight === id) return
    setSelectedRight(id)
  }

  const resetGame = () => {
    setTimeLeft(duration)
    setScore(0)
    setCombo(0)
    setMaxCombo(0)
    setMatchCount(0)
    setSelectedLeft(null)
    setSelectedRight(null)
    setMatchedPairs([])
    setGameComplete(false)
    initializeWords()
  }

  if (gameComplete) {
    return (
      <div className="min-h-screen flex items-center justify-center p-4 bg-gradient-to-br from-brand-500 to-purple-600">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
        >
          <Card className="p-8 text-center max-w-md">
            <Trophy className="h-16 w-16 mx-auto text-yellow-500 mb-4" />
            <h2 className="text-3xl font-game text-gray-900 dark:text-white mb-2">
              {t.gameOver}
            </h2>
            <div className="flex items-center justify-center gap-2 mb-6">
              <Star className="h-8 w-8 text-yellow-500" />
              <span className="text-4xl font-bold text-brand-600">{score}</span>
            </div>
            <div className="flex justify-center gap-8 mb-6">
              <div className="text-center">
                <p className="text-2xl font-bold text-brand-600">{matchCount}</p>
                <p className="text-sm text-gray-500">{t.matches}</p>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold text-orange-500">{maxCombo}x</p>
                <p className="text-sm text-gray-500">{t.combo}</p>
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

  const isCritical = timeLeft <= 10

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-500 to-purple-600 p-4">
      <Celebration show={showCelebration} />

      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-game text-white flex items-center gap-2">
            <Zap className="h-6 w-6" />
            {t.title}
          </h1>
          <div className="flex gap-4">
            <Badge
              variant="secondary"
              className={`text-lg px-4 py-2 flex items-center gap-2 ${isCritical ? 'bg-red-500 text-white animate-pulse' : ''}`}
            >
              <Clock className="h-4 w-4" />
              {timeLeft}s
            </Badge>
            <div className="flex items-center gap-2 px-4 py-2 rounded-full bg-brand-100 dark:bg-brand-900/30">
              <Star className="h-5 w-5 text-brand-500" />
              <span className="font-bold text-xl text-brand-600">{score}</span>
            </div>
            {combo >= 2 && (
              <Badge className="bg-orange-500 text-white flex items-center gap-1">
                <Flame className="h-4 w-4" />
                {combo}x
              </Badge>
            )}
          </div>
        </div>

        <p className="text-white/80 text-center mb-6">{t.instruction}</p>

        {/* Game Grid */}
        <div className="grid grid-cols-2 gap-8">
          {/* Left column - English words */}
          <div className="space-y-3">
            {leftWords.map((word) => {
              const isMatched = matchedPairs.includes(word.id)
              const isSelected = selectedLeft === word.id
              const isWrong = wrongPair?.left === word.id

              return (
                <motion.button
                  key={`left-${word.id}`}
                  onClick={() => handleLeftClick(word.id)}
                  disabled={isMatched}
                  className={`w-full p-4 rounded-xl text-lg font-semibold transition-all ${
                    isMatched
                      ? 'bg-green-500 text-white opacity-50 cursor-not-allowed'
                      : isWrong
                      ? 'bg-red-500 text-white'
                      : isSelected
                      ? 'bg-brand-600 text-white ring-4 ring-white'
                      : 'bg-white dark:bg-gray-800 text-gray-900 dark:text-white hover:bg-brand-100 dark:hover:bg-brand-900'
                  }`}
                  animate={isWrong ? { x: [0, -10, 10, -10, 10, 0] } : {}}
                  transition={{ duration: 0.4 }}
                >
                  {word.word}
                </motion.button>
              )
            })}
          </div>

          {/* Right column - Translations */}
          <div className="space-y-3">
            {rightWords.map((word) => {
              const isMatched = matchedPairs.includes(word.id)
              const isSelected = selectedRight === word.id
              const isWrong = wrongPair?.right === word.id

              return (
                <motion.button
                  key={`right-${word.id}`}
                  onClick={() => handleRightClick(word.id)}
                  disabled={isMatched}
                  className={`w-full p-4 rounded-xl text-lg font-semibold transition-all ${
                    isMatched
                      ? 'bg-green-500 text-white opacity-50 cursor-not-allowed'
                      : isWrong
                      ? 'bg-red-500 text-white'
                      : isSelected
                      ? 'bg-brand-600 text-white ring-4 ring-white'
                      : 'bg-white dark:bg-gray-800 text-gray-900 dark:text-white hover:bg-brand-100 dark:hover:bg-brand-900'
                  }`}
                  animate={isWrong ? { x: [0, -10, 10, -10, 10, 0] } : {}}
                  transition={{ duration: 0.4 }}
                >
                  {word.translation}
                </motion.button>
              )
            })}
          </div>
        </div>

        {/* Bonus time indicator */}
        <AnimatePresence>
          {showCelebration && (
            <motion.div
              initial={{ opacity: 0, y: 20, scale: 0.8 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -20 }}
              className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-50"
            >
              <div className="bg-green-500 text-white px-6 py-3 rounded-full text-2xl font-bold flex items-center gap-2">
                <Flame className="h-6 w-6" />
                {t.bonusTime}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  )
}
