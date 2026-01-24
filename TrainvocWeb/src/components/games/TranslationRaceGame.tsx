import { useState, useEffect, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Timer, Flame, Trophy, RotateCcw, TrendingUp, Star } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { AnswerGrid } from '@/components/game/AnswerButton'
import { Celebration } from '@/components/game/Celebration'

interface TranslationRaceQuestion {
  id: string
  word: string
  correctTranslation: string
  options: string[]
}

interface TranslationRaceGameProps {
  questions: TranslationRaceQuestion[]
  gameDuration?: number
  onComplete?: (results: {
    score: number
    answersCount: number
    correctCount: number
    maxCombo: number
    apm: number
  }) => void
}

export function TranslationRaceGame({
  questions,
  gameDuration = 90,
  onComplete,
}: TranslationRaceGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const [currentIndex, setCurrentIndex] = useState(0)
  const [score, setScore] = useState(0)
  const [combo, setCombo] = useState(0)
  const [maxCombo, setMaxCombo] = useState(0)
  const [correctCount, setCorrectCount] = useState(0)
  const [answersCount, setAnswersCount] = useState(0)
  const [timeLeft, setTimeLeft] = useState(gameDuration)
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null)
  const [showResult, setShowResult] = useState(false)
  const [showCelebration, setShowCelebration] = useState(false)
  const [gameComplete, setGameComplete] = useState(false)
  const [bonusTime, setBonusTime] = useState<number | null>(null)

  const currentQuestion = questions[currentIndex % questions.length]
  const correctIndex = currentQuestion.options.indexOf(currentQuestion.correctTranslation)

  const content = {
    en: {
      title: 'Translation Race',
      instruction: 'Answer as many as you can before time runs out!',
      score: 'Score',
      timeLeft: 'Time',
      answers: 'Answers',
      combo: 'Combo',
      apm: 'APM',
      gameOver: 'Time\'s Up!',
      finalScore: 'Final Score',
      correct: 'Correct',
      answered: 'Answered',
      maxCombo: 'Max Combo',
      playAgain: 'Play Again',
      bonusTime: '+2s',
    },
    tr: {
      title: 'Çeviri Yarışı',
      instruction: 'Süre bitmeden olabildiğince çok cevapla!',
      score: 'Puan',
      timeLeft: 'Süre',
      answers: 'Cevap',
      combo: 'Kombo',
      apm: 'DPD',
      gameOver: 'Süre Doldu!',
      finalScore: 'Son Puan',
      correct: 'Doğru',
      answered: 'Cevaplanan',
      maxCombo: 'Maks Kombo',
      playAgain: 'Tekrar Oyna',
      bonusTime: '+2sn',
    },
  }

  const t = content[lang]

  const elapsedTime = gameDuration - timeLeft
  const apm = elapsedTime > 0 ? Math.round((answersCount / elapsedTime) * 60) : 0

  useEffect(() => {
    if (gameComplete) return

    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          setGameComplete(true)
          onComplete?.({
            score,
            answersCount,
            correctCount,
            maxCombo,
            apm: answersCount > 0 ? Math.round((answersCount / gameDuration) * 60) : 0,
          })
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(timer)
  }, [gameComplete, score, answersCount, correctCount, maxCombo, gameDuration, onComplete])

  const handleAnswer = useCallback((index: number) => {
    if (selectedIndex !== null || showResult) return

    const answer = currentQuestion.options[index]
    const correct = answer === currentQuestion.correctTranslation
    setSelectedIndex(index)
    setShowResult(true)
    setAnswersCount((prev) => prev + 1)

    if (correct) {
      const newCombo = combo + 1
      setCombo(newCombo)
      setMaxCombo((prev) => Math.max(prev, newCombo))
      setCorrectCount((prev) => prev + 1)

      const baseScore = 50
      const comboMultiplier = Math.min(newCombo, 5)
      const earnedScore = baseScore * comboMultiplier
      setScore((prev) => prev + earnedScore)

      if (newCombo >= 3 && newCombo % 3 === 0) {
        setTimeLeft((prev) => prev + 2)
        setBonusTime(2)
        setTimeout(() => setBonusTime(null), 1000)
      }

      if (newCombo >= 5) {
        setShowCelebration(true)
        setTimeout(() => setShowCelebration(false), 1000)
      }
    } else {
      setCombo(0)
    }

    setTimeout(() => {
      setSelectedIndex(null)
      setShowResult(false)
      setCurrentIndex((prev) => prev + 1)
    }, 500)
  }, [selectedIndex, showResult, currentQuestion, combo])

  const resetGame = () => {
    setCurrentIndex(0)
    setScore(0)
    setCombo(0)
    setMaxCombo(0)
    setCorrectCount(0)
    setAnswersCount(0)
    setTimeLeft(gameDuration)
    setSelectedIndex(null)
    setShowResult(false)
    setGameComplete(false)
  }

  if (gameComplete) {
    const finalApm = answersCount > 0 ? Math.round((answersCount / gameDuration) * 60) : 0
    const accuracy = answersCount > 0 ? Math.round((correctCount / answersCount) * 100) : 0

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

            <div className="grid grid-cols-2 gap-4 my-6">
              <div className="bg-gray-100 dark:bg-gray-800 rounded-xl p-4">
                <p className="text-2xl font-bold text-green-500">{correctCount}/{answersCount}</p>
                <p className="text-xs text-gray-500">{t.correct} ({accuracy}%)</p>
              </div>
              <div className="bg-gray-100 dark:bg-gray-800 rounded-xl p-4">
                <p className="text-2xl font-bold text-orange-500">{maxCombo}x</p>
                <p className="text-xs text-gray-500">{t.maxCombo}</p>
              </div>
              <div className="bg-gray-100 dark:bg-gray-800 rounded-xl p-4 col-span-2">
                <div className="flex items-center justify-center gap-2">
                  <TrendingUp className="h-5 w-5 text-brand-500" />
                  <p className="text-2xl font-bold text-brand-600">{finalApm}</p>
                </div>
                <p className="text-xs text-gray-500">{t.apm}</p>
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

      <AnimatePresence>
        {bonusTime !== null && (
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.8 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -20 }}
            className="fixed top-1/4 left-1/2 -translate-x-1/2 z-50"
          >
            <div className="bg-green-500 text-white px-6 py-3 rounded-full text-2xl font-bold flex items-center gap-2">
              <Flame className="h-6 w-6" />
              {t.bonusTime}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-xl font-game text-white flex items-center gap-2">
            <Timer className="h-5 w-5" />
            {t.title}
          </h1>
          <div className="flex gap-2">
            <Badge
              variant="secondary"
              className={`text-lg px-3 py-1 flex items-center gap-1 ${isCritical ? 'bg-red-500 text-white animate-pulse' : ''}`}
            >
              <Timer className="h-4 w-4" />
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

        {/* Stats bar */}
        <div className="flex justify-center gap-4 mb-4">
          <Badge variant="outline" className="bg-white/10 text-white border-white/30">
            {t.answers}: {answersCount}
          </Badge>
          <Badge variant="outline" className="bg-white/10 text-white border-white/30">
            {t.apm}: {apm}
          </Badge>
        </div>

        {/* Progress bar (time) */}
        <div className="w-full h-2 bg-white/20 rounded-full mb-6 overflow-hidden">
          <motion.div
            className={`h-full rounded-full ${isCritical ? 'bg-red-500' : 'bg-white'}`}
            initial={{ width: '100%' }}
            animate={{ width: `${(timeLeft / gameDuration) * 100}%` }}
            transition={{ duration: 0.5 }}
          />
        </div>

        {/* Question Card */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentIndex}
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.2 }}
          >
            <Card className="p-6 mb-4">
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-2 text-center">
                {t.instruction}
              </p>
              <p className="text-2xl font-bold text-center text-gray-900 dark:text-white">
                {currentQuestion.word}
              </p>
            </Card>
          </motion.div>
        </AnimatePresence>

        {/* Answer Options */}
        <AnswerGrid
          options={currentQuestion.options}
          onSelect={handleAnswer}
          selectedIndex={selectedIndex ?? undefined}
          correctIndex={correctIndex}
          showResult={showResult}
          disabled={showResult}
        />
      </div>
    </div>
  )
}
