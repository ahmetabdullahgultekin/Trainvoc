import { useState, useEffect, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Star, Clock, Flame } from 'lucide-react'
import { AnswerGrid } from '@/components/game/AnswerButton'
import { Celebration } from '@/components/game/Celebration'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'

export interface Word {
  id: string
  word: string
  translation: string
  pronunciation?: string
  level?: string
}

export interface MultipleChoiceQuestion {
  id: string
  word: Word
  options: string[]
  correctIndex: number
  direction: 'en-tr' | 'tr-en'
}

interface MultipleChoiceGameProps {
  questions: MultipleChoiceQuestion[]
  questionDuration?: number
  onComplete?: (results: GameResults) => void
  onAnswer?: (questionId: string, correct: boolean, timeMs: number) => void
}

interface GameResults {
  correctCount: number
  wrongCount: number
  totalScore: number
  answers: Array<{
    questionId: string
    correct: boolean
    timeMs: number
    selectedIndex: number
  }>
}

export function MultipleChoiceGame({
  questions,
  questionDuration = 30,
  onComplete,
  onAnswer,
}: MultipleChoiceGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const [currentIndex, setCurrentIndex] = useState(0)
  const [score, setScore] = useState(0)
  const [streak, setStreak] = useState(0)
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null)
  const [showResult, setShowResult] = useState(false)
  const [isCorrect, setIsCorrect] = useState(false)
  const [showCelebration, setShowCelebration] = useState(false)
  const [timeLeft, setTimeLeft] = useState(questionDuration)
  const [gameComplete, setGameComplete] = useState(false)
  const [answers, setAnswers] = useState<GameResults['answers']>([])

  const currentQuestion = questions[currentIndex]

  const content = {
    en: {
      question: 'What is the translation?',
      correct: 'Correct!',
      wrong: 'Wrong!',
      timeUp: 'Time up!',
      streak: 'Streak',
      score: 'Score',
      questionOf: 'of',
      gameComplete: 'Game Complete!',
      finalScore: 'Final Score',
      correct_answers: 'Correct',
      wrong_answers: 'Wrong',
      playAgain: 'Play Again',
    },
    tr: {
      question: 'Çevirisi nedir?',
      correct: 'Doğru!',
      wrong: 'Yanlış!',
      timeUp: 'Süre doldu!',
      streak: 'Seri',
      score: 'Puan',
      questionOf: '/',
      gameComplete: 'Oyun Bitti!',
      finalScore: 'Son Puan',
      correct_answers: 'Doğru',
      wrong_answers: 'Yanlış',
      playAgain: 'Tekrar Oyna',
    },
  }

  const t = content[lang]

  // Timer countdown
  useEffect(() => {
    if (showResult || gameComplete) return

    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          handleTimeout()
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(timer)
  }, [currentIndex, showResult, gameComplete])

  const handleTimeout = useCallback(() => {
    if (selectedIndex !== null) return

    const answer = {
      questionId: currentQuestion.id,
      correct: false,
      timeMs: questionDuration * 1000,
      selectedIndex: -1,
    }
    setAnswers((prev) => [...prev, answer])
    onAnswer?.(currentQuestion.id, false, questionDuration * 1000)

    setStreak(0)
    setShowResult(true)
    setIsCorrect(false)

    setTimeout(() => {
      goToNext()
    }, 2000)
  }, [currentQuestion, questionDuration, selectedIndex, onAnswer])

  const handleAnswer = (index: number) => {
    if (selectedIndex !== null || showResult) return

    const timeMs = (questionDuration - timeLeft) * 1000
    const correct = index === currentQuestion.correctIndex

    setSelectedIndex(index)
    setIsCorrect(correct)
    setShowResult(true)

    const answer = {
      questionId: currentQuestion.id,
      correct,
      timeMs,
      selectedIndex: index,
    }
    setAnswers((prev) => [...prev, answer])
    onAnswer?.(currentQuestion.id, correct, timeMs)

    if (correct) {
      const timeBonus = Math.floor((timeLeft / questionDuration) * 100)
      const streakBonus = streak >= 3 ? 50 : streak >= 2 ? 25 : 0
      const pointsEarned = 100 + timeBonus + streakBonus

      setScore((prev) => prev + pointsEarned)
      setStreak((prev) => prev + 1)

      if (streak >= 2) {
        setShowCelebration(true)
        setTimeout(() => setShowCelebration(false), 2000)
      }
    } else {
      setStreak(0)
    }

    setTimeout(() => {
      goToNext()
    }, 2000)
  }

  const goToNext = () => {
    if (currentIndex + 1 >= questions.length) {
      setGameComplete(true)
      const results: GameResults = {
        correctCount: answers.filter((a) => a.correct).length + (isCorrect ? 1 : 0),
        wrongCount: answers.filter((a) => !a.correct).length + (!isCorrect ? 1 : 0),
        totalScore: score,
        answers: [...answers],
      }
      onComplete?.(results)
    } else {
      setCurrentIndex((prev) => prev + 1)
      setSelectedIndex(null)
      setShowResult(false)
      setTimeLeft(questionDuration)
    }
  }

  const resetGame = () => {
    setCurrentIndex(0)
    setScore(0)
    setStreak(0)
    setSelectedIndex(null)
    setShowResult(false)
    setIsCorrect(false)
    setShowCelebration(false)
    setTimeLeft(questionDuration)
    setGameComplete(false)
    setAnswers([])
  }

  if (gameComplete) {
    const correctCount = answers.filter((a) => a.correct).length
    const wrongCount = answers.filter((a) => !a.correct).length

    return (
      <div className="min-h-screen flex items-center justify-center p-4 bg-gradient-to-br from-brand-500 to-purple-600">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
        >
          <Card className="p-8 text-center max-w-md">
            <h2 className="text-3xl font-game text-gray-900 dark:text-white mb-6">
              {t.gameComplete}
            </h2>
            <div className="flex items-center justify-center gap-2 mb-6">
              <Star className="h-8 w-8 text-yellow-500" />
              <span className="text-4xl font-bold text-brand-600">{score}</span>
            </div>
            <div className="flex justify-center gap-8 my-6">
              <div className="text-center">
                <p className="text-3xl font-bold text-green-500">{correctCount}</p>
                <p className="text-sm text-gray-500">{t.correct_answers}</p>
              </div>
              <div className="text-center">
                <p className="text-3xl font-bold text-red-500">{wrongCount}</p>
                <p className="text-sm text-gray-500">{t.wrong_answers}</p>
              </div>
            </div>
            <Button onClick={resetGame} className="w-full">
              {t.playAgain}
            </Button>
          </Card>
        </motion.div>
      </div>
    )
  }

  const isWarning = timeLeft <= 10 && timeLeft > 5
  const isCritical = timeLeft <= 5

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-500 to-purple-600 p-4">
      <Celebration show={showCelebration} />

      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <Badge variant="secondary" className="text-lg px-4 py-2">
            {currentIndex + 1} {t.questionOf} {questions.length}
          </Badge>
          <div className="flex items-center gap-2 px-4 py-2 rounded-full bg-brand-100 dark:bg-brand-900/30">
            <Star className="h-5 w-5 text-brand-500" />
            <span className="font-bold text-xl text-brand-600 dark:text-brand-400">{score}</span>
          </div>
          {streak >= 2 && (
            <Badge className="bg-orange-500 text-white animate-pulse flex items-center gap-1">
              <Flame className="h-4 w-4" />
              {streak}x
            </Badge>
          )}
        </div>

        {/* Timer */}
        <div className="flex justify-center mb-6">
          <motion.div
            className={`w-20 h-20 rounded-full flex items-center justify-center text-3xl font-bold ${
              isCritical ? 'bg-red-500 text-white' : isWarning ? 'bg-yellow-500 text-white' : 'bg-white text-gray-900'
            }`}
            animate={isCritical ? { scale: [1, 1.1, 1] } : {}}
            transition={{ repeat: Infinity, duration: 0.5 }}
          >
            <Clock className="h-5 w-5 mr-1" />
            {timeLeft}
          </motion.div>
        </div>

        {/* Question */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestion.id}
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
          >
            <Card className="p-8 text-center bg-gradient-to-br from-brand-500 to-brand-600 mb-6">
              <p className="text-white/80 text-sm mb-2">{t.question}</p>
              <h2 className="text-4xl md:text-5xl font-game text-white">
                {currentQuestion.direction === 'en-tr' ? currentQuestion.word.word : currentQuestion.word.translation}
              </h2>
              {currentQuestion.word.pronunciation && (
                <p className="text-brand-200 text-lg mt-2">/{currentQuestion.word.pronunciation}/</p>
              )}
            </Card>
          </motion.div>
        </AnimatePresence>

        {/* Answer Options */}
        <AnswerGrid
          options={currentQuestion.options}
          onSelect={handleAnswer}
          selectedIndex={selectedIndex ?? undefined}
          correctIndex={currentQuestion.correctIndex}
          showResult={showResult}
          disabled={showResult}
        />

        {/* Result feedback */}
        <AnimatePresence>
          {showResult && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0 }}
              className="mt-6 text-center"
            >
              <p className={`text-2xl font-bold ${isCorrect ? 'text-green-400' : 'text-red-400'}`}>
                {selectedIndex === null ? t.timeUp : isCorrect ? t.correct : t.wrong}
              </p>
              {!isCorrect && (
                <p className="text-white/80 mt-2">
                  {currentQuestion.direction === 'en-tr' ? currentQuestion.word.translation : currentQuestion.word.word}
                </p>
              )}
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  )
}
