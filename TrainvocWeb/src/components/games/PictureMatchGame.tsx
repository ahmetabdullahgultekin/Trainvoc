import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Image, Check, X, RotateCcw, Flame, Star, Clock } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Celebration } from '@/components/game/Celebration'

interface PictureMatchQuestion {
  id: string
  imageUrl: string
  correctWord: string
  options: string[]
}

interface PictureMatchGameProps {
  questions: PictureMatchQuestion[]
  questionDuration?: number
  onComplete?: (results: { correctCount: number; totalScore: number; maxStreak: number }) => void
}

export function PictureMatchGame({
  questions,
  questionDuration = 15,
  onComplete,
}: PictureMatchGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const [currentIndex, setCurrentIndex] = useState(0)
  const [score, setScore] = useState(0)
  const [streak, setStreak] = useState(0)
  const [maxStreak, setMaxStreak] = useState(0)
  const [correctCount, setCorrectCount] = useState(0)
  const [selectedAnswer, setSelectedAnswer] = useState<string | null>(null)
  const [showResult, setShowResult] = useState(false)
  const [isCorrect, setIsCorrect] = useState(false)
  const [showCelebration, setShowCelebration] = useState(false)
  const [timeLeft, setTimeLeft] = useState(questionDuration)
  const [gameComplete, setGameComplete] = useState(false)

  const currentQuestion = questions[currentIndex]

  const content = {
    en: {
      title: 'Picture Match',
      instruction: 'What is shown in the picture?',
      correct: 'Correct!',
      wrong: 'Wrong!',
      timeUp: 'Time up!',
      next: 'Next',
      questionOf: 'of',
      score: 'Score',
      streak: 'Streak',
      complete: 'Game Complete!',
      playAgain: 'Play Again',
      answer: 'Answer',
      maxStreak: 'Max Streak',
    },
    tr: {
      title: 'Resim Eşleştirme',
      instruction: 'Resimde ne gösteriliyor?',
      correct: 'Doğru!',
      wrong: 'Yanlış!',
      timeUp: 'Süre doldu!',
      next: 'Sonraki',
      questionOf: '/',
      score: 'Puan',
      streak: 'Seri',
      complete: 'Oyun Bitti!',
      playAgain: 'Tekrar Oyna',
      answer: 'Cevap',
      maxStreak: 'Maks Seri',
    },
  }

  const t = content[lang]

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

  const handleTimeout = () => {
    if (selectedAnswer !== null) return
    setShowResult(true)
    setIsCorrect(false)
    setStreak(0)
  }

  const handleAnswer = (answer: string) => {
    if (selectedAnswer !== null || showResult) return

    const correct = answer === currentQuestion.correctWord
    setSelectedAnswer(answer)
    setIsCorrect(correct)
    setShowResult(true)

    if (correct) {
      const newStreak = streak + 1
      setStreak(newStreak)
      setMaxStreak((prev) => Math.max(prev, newStreak))
      setCorrectCount((prev) => prev + 1)

      const timeBonus = Math.floor((timeLeft / questionDuration) * 50)
      const streakBonus = newStreak >= 3 ? 50 : newStreak >= 2 ? 25 : 0
      setScore((prev) => prev + 100 + timeBonus + streakBonus)

      if (newStreak >= 3) {
        setShowCelebration(true)
        setTimeout(() => setShowCelebration(false), 1500)
      }
    } else {
      setStreak(0)
    }
  }

  const handleNext = () => {
    if (currentIndex + 1 >= questions.length) {
      setGameComplete(true)
      onComplete?.({ correctCount, totalScore: score, maxStreak })
    } else {
      setCurrentIndex((prev) => prev + 1)
      setSelectedAnswer(null)
      setShowResult(false)
      setTimeLeft(questionDuration)
    }
  }

  const isWarning = timeLeft <= 5 && timeLeft > 3
  const isCritical = timeLeft <= 3

  if (gameComplete) {
    return (
      <div className="min-h-screen flex items-center justify-center p-4 bg-gradient-to-br from-brand-500 to-purple-600">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
        >
          <Card className="p-8 text-center max-w-md">
            <h2 className="text-3xl font-game text-gray-900 dark:text-white mb-6">
              {t.complete}
            </h2>
            <div className="flex items-center justify-center gap-2 mb-6">
              <Star className="h-8 w-8 text-yellow-500" />
              <span className="text-4xl font-bold text-brand-600">{score}</span>
            </div>
            <div className="flex justify-center gap-8 my-6">
              <div className="text-center">
                <p className="text-3xl font-bold text-green-500">{correctCount}</p>
                <p className="text-sm text-gray-500">{t.correct}</p>
              </div>
              <div className="text-center">
                <p className="text-3xl font-bold text-orange-500">{maxStreak}x</p>
                <p className="text-sm text-gray-500">{t.maxStreak}</p>
              </div>
            </div>
            <Button onClick={() => window.location.reload()} className="w-full gap-2">
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
      <Celebration show={showCelebration} />

      <div className="max-w-xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-game text-white flex items-center gap-2">
            <Image className="h-6 w-6" />
            {t.title}
          </h1>
          <div className="flex gap-4">
            <Badge variant="secondary" className="text-lg px-4 py-2">
              {currentIndex + 1} {t.questionOf} {questions.length}
            </Badge>
            <div className="flex items-center gap-2 px-4 py-2 rounded-full bg-brand-100 dark:bg-brand-900/30">
              <Star className="h-5 w-5 text-brand-500" />
              <span className="font-bold text-xl text-brand-600">{score}</span>
            </div>
            {streak >= 2 && (
              <Badge className="bg-orange-500 text-white flex items-center gap-1">
                <Flame className="h-4 w-4" />
                {streak}x
              </Badge>
            )}
          </div>
        </div>

        {/* Timer */}
        <div className="flex justify-center mb-6">
          <motion.div
            className={`w-16 h-16 rounded-full flex items-center justify-center text-2xl font-bold ${
              isCritical ? 'bg-red-500 text-white' : isWarning ? 'bg-yellow-500 text-white' : 'bg-white text-gray-900'
            }`}
            animate={isCritical ? { scale: [1, 1.1, 1] } : {}}
            transition={{ repeat: Infinity, duration: 0.5 }}
          >
            <Clock className="h-4 w-4 mr-1" />
            {timeLeft}
          </motion.div>
        </div>

        {/* Question Card */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestion.id}
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95 }}
          >
            <Card className="p-4 md:p-6 mb-6">
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
                {t.instruction}
              </p>

              {/* Image */}
              <div className="relative aspect-video bg-gray-100 dark:bg-gray-800 rounded-xl overflow-hidden mb-4">
                <img
                  src={currentQuestion.imageUrl}
                  alt="Question"
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    e.currentTarget.src = `https://placehold.co/400x300/6366f1/white?text=${encodeURIComponent(currentQuestion.correctWord)}`
                  }}
                />
              </div>

              {/* Answer Options */}
              {!showResult ? (
                <div className="grid grid-cols-2 gap-3">
                  {currentQuestion.options.map((option) => (
                    <motion.button
                      key={option}
                      onClick={() => handleAnswer(option)}
                      className="p-4 rounded-xl text-lg font-semibold bg-gray-100 dark:bg-gray-800 text-gray-900 dark:text-white hover:bg-brand-100 dark:hover:bg-brand-900 transition-colors"
                      whileHover={{ scale: 1.02 }}
                      whileTap={{ scale: 0.98 }}
                    >
                      {option}
                    </motion.button>
                  ))}
                </div>
              ) : (
                <motion.div
                  initial={{ opacity: 0, scale: 0.9 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="text-center"
                >
                  <div className={`mb-4 p-4 rounded-xl ${isCorrect ? 'bg-green-100 dark:bg-green-900/30' : 'bg-red-100 dark:bg-red-900/30'}`}>
                    <div className="flex items-center justify-center gap-2 mb-2">
                      {isCorrect ? (
                        <>
                          <Check className="h-6 w-6 text-green-500" />
                          {streak >= 3 && <Flame className="h-6 w-6 text-orange-500" />}
                        </>
                      ) : (
                        <X className="h-6 w-6 text-red-500" />
                      )}
                      <span className={`text-xl font-bold ${isCorrect ? 'text-green-600' : 'text-red-600'}`}>
                        {selectedAnswer === null ? t.timeUp : isCorrect ? t.correct : t.wrong}
                      </span>
                    </div>
                    {!isCorrect && (
                      <p className="text-gray-600 dark:text-gray-400">
                        {t.answer}: <span className="font-bold">{currentQuestion.correctWord}</span>
                      </p>
                    )}
                  </div>
                  <Button className="w-full" onClick={handleNext}>
                    {t.next}
                  </Button>
                </motion.div>
              )}
            </Card>
          </motion.div>
        </AnimatePresence>
      </div>
    </div>
  )
}
