import { useState, useEffect, useRef } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Keyboard, Check, X, RotateCcw, Send, Eye, Star, Clock } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Celebration } from '@/components/game/Celebration'

interface SpellingQuestion {
  id: string
  word: string
  translation: string
  hint?: string
}

interface SpellingChallengeGameProps {
  questions: SpellingQuestion[]
  questionDuration?: number
  onComplete?: (results: { correctCount: number; totalScore: number; perfectCount: number }) => void
}

export function SpellingChallengeGame({
  questions,
  questionDuration = 45,
  onComplete,
}: SpellingChallengeGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'
  const inputRef = useRef<HTMLInputElement>(null)

  const [currentIndex, setCurrentIndex] = useState(0)
  const [userInput, setUserInput] = useState('')
  const [score, setScore] = useState(0)
  const [correctCount, setCorrectCount] = useState(0)
  const [perfectCount, setPerfectCount] = useState(0)
  const [showResult, setShowResult] = useState(false)
  const [isCorrect, setIsCorrect] = useState(false)
  const [hintsUsed, setHintsUsed] = useState(0)
  const [revealedLetters, setRevealedLetters] = useState<number[]>([])
  const [showCelebration, setShowCelebration] = useState(false)
  const [timeLeft, setTimeLeft] = useState(questionDuration)
  const [gameComplete, setGameComplete] = useState(false)

  const currentQuestion = questions[currentIndex]

  const content = {
    en: {
      title: 'Spelling Challenge',
      instruction: 'Type the correct English spelling',
      meaning: 'Meaning',
      typeSpelling: 'Type the spelling...',
      submit: 'Submit',
      correct: 'Correct!',
      perfectSpelling: 'Perfect spelling!',
      wrong: 'Wrong!',
      timeUp: 'Time up!',
      next: 'Next',
      questionOf: 'of',
      score: 'Score',
      hint: 'Hint',
      complete: 'Game Complete!',
      playAgain: 'Play Again',
      answer: 'Correct spelling',
      perfect: 'Perfect',
    },
    tr: {
      title: 'Yazım Mücadelesi',
      instruction: 'Doğru İngilizce yazımını yaz',
      meaning: 'Anlam',
      typeSpelling: 'Yazımı yaz...',
      submit: 'Gönder',
      correct: 'Doğru!',
      perfectSpelling: 'Mükemmel yazım!',
      wrong: 'Yanlış!',
      timeUp: 'Süre doldu!',
      next: 'Sonraki',
      questionOf: '/',
      score: 'Puan',
      hint: 'İpucu',
      complete: 'Oyun Bitti!',
      playAgain: 'Tekrar Oyna',
      answer: 'Doğru yazım',
      perfect: 'Mükemmel',
    },
  }

  const t = content[lang]

  useEffect(() => {
    inputRef.current?.focus()
  }, [currentIndex])

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
    if (showResult) return
    setShowResult(true)
    setIsCorrect(false)
  }

  const handleSubmit = () => {
    if (!userInput.trim() || showResult) return

    const correct = userInput.toLowerCase().trim() === currentQuestion.word.toLowerCase()
    setIsCorrect(correct)
    setShowResult(true)

    if (correct) {
      const isPerfect = hintsUsed === 0
      const baseScore = 100
      const timeBonus = Math.floor((timeLeft / questionDuration) * 50)
      const hintPenalty = hintsUsed * 15
      const perfectBonus = isPerfect ? 50 : 0

      setScore((prev) => prev + baseScore + timeBonus + perfectBonus - hintPenalty)
      setCorrectCount((prev) => prev + 1)

      if (isPerfect) {
        setPerfectCount((prev) => prev + 1)
      }

      setShowCelebration(true)
      setTimeout(() => setShowCelebration(false), 1500)
    }
  }

  const handleHint = () => {
    const word = currentQuestion.word
    const unrevealedPositions = word
      .split('')
      .map((_, i) => i)
      .filter((i) => !revealedLetters.includes(i))

    if (unrevealedPositions.length > 0) {
      const randomPos = unrevealedPositions[Math.floor(Math.random() * unrevealedPositions.length)]
      setRevealedLetters((prev) => [...prev, randomPos])
      setHintsUsed((prev) => prev + 1)
    }
  }

  const handleNext = () => {
    if (currentIndex + 1 >= questions.length) {
      setGameComplete(true)
      onComplete?.({ correctCount, totalScore: score, perfectCount })
    } else {
      setCurrentIndex((prev) => prev + 1)
      setUserInput('')
      setShowResult(false)
      setRevealedLetters([])
      setHintsUsed(0)
      setTimeLeft(questionDuration)
    }
  }

  const resetGame = () => {
    setCurrentIndex(0)
    setUserInput('')
    setScore(0)
    setCorrectCount(0)
    setPerfectCount(0)
    setShowResult(false)
    setIsCorrect(false)
    setHintsUsed(0)
    setRevealedLetters([])
    setShowCelebration(false)
    setTimeLeft(questionDuration)
    setGameComplete(false)
  }

  const getHintDisplay = () => {
    return currentQuestion.word
      .split('')
      .map((letter, i) => (revealedLetters.includes(i) ? letter.toUpperCase() : '_'))
      .join(' ')
  }

  const renderLetterFeedback = () => {
    const userLetters = userInput.toLowerCase().split('')
    const correctLetters = currentQuestion.word.toLowerCase().split('')

    return (
      <div className="flex flex-wrap justify-center gap-1 mb-4">
        {correctLetters.map((letter, index) => {
          const userLetter = userLetters[index]?.toLowerCase()
          const isCorrectLetter = userLetter === letter
          const hasTyped = index < userLetters.length

          return (
            <div
              key={index}
              className={`w-10 h-12 rounded-lg flex items-center justify-center text-lg font-bold border-2 transition-colors ${
                !hasTyped
                  ? 'border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-800 text-gray-400'
                  : isCorrectLetter
                  ? 'border-green-500 bg-green-100 dark:bg-green-900/30 text-green-600'
                  : 'border-red-500 bg-red-100 dark:bg-red-900/30 text-red-600'
              }`}
            >
              {hasTyped ? userLetters[index].toUpperCase() : revealedLetters.includes(index) ? letter.toUpperCase() : ''}
            </div>
          )
        })}
      </div>
    )
  }

  const isWarning = timeLeft <= 10 && timeLeft > 5
  const isCritical = timeLeft <= 5

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
                <p className="text-3xl font-bold text-yellow-500">{perfectCount}</p>
                <p className="text-sm text-gray-500">{t.perfect}</p>
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
      <Celebration show={showCelebration} />

      <div className="max-w-xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-game text-white flex items-center gap-2">
            <Keyboard className="h-6 w-6" />
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
          </div>
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

        {/* Question Card */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestion.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            <Card className="p-6 md:p-8">
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-2">
                {t.instruction}
              </p>

              {/* Meaning */}
              <div className="bg-gray-100 dark:bg-gray-800 rounded-xl p-6 mb-6 text-center">
                <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">{t.meaning}:</p>
                <p className="text-2xl font-bold text-gray-900 dark:text-white">
                  {currentQuestion.translation}
                </p>
              </div>

              {/* Hint display */}
              {revealedLetters.length > 0 && (
                <div className="mb-4 p-3 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg">
                  <p className="text-center font-mono text-xl tracking-widest text-yellow-700 dark:text-yellow-400">
                    {getHintDisplay()}
                  </p>
                </div>
              )}

              {/* Letter feedback while typing */}
              {userInput.length > 0 && !showResult && renderLetterFeedback()}

              {/* Input and actions */}
              {!showResult ? (
                <div className="space-y-4">
                  <Input
                    ref={inputRef}
                    value={userInput}
                    onChange={(e) => setUserInput(e.target.value)}
                    placeholder={t.typeSpelling}
                    className="text-lg text-center font-mono tracking-wider"
                    onKeyDown={(e) => e.key === 'Enter' && userInput && handleSubmit()}
                    autoComplete="off"
                    autoCorrect="off"
                    spellCheck={false}
                  />
                  <div className="flex gap-3">
                    <Button
                      variant="outline"
                      className="gap-2"
                      onClick={handleHint}
                      disabled={revealedLetters.length >= currentQuestion.word.length - 1}
                    >
                      <Eye className="h-4 w-4" />
                      {t.hint}
                    </Button>
                    <Button
                      className="flex-1 gap-2"
                      onClick={handleSubmit}
                      disabled={!userInput}
                    >
                      <Send className="h-4 w-4" />
                      {t.submit}
                    </Button>
                  </div>
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
                        <Check className="h-6 w-6 text-green-500" />
                      ) : (
                        <X className="h-6 w-6 text-red-500" />
                      )}
                      <span className={`text-xl font-bold ${isCorrect ? 'text-green-600' : 'text-red-600'}`}>
                        {timeLeft === 0 && !userInput ? t.timeUp : isCorrect ? (hintsUsed === 0 ? t.perfectSpelling : t.correct) : t.wrong}
                      </span>
                    </div>
                    {!isCorrect && (
                      <p className="text-gray-600 dark:text-gray-400">
                        {t.answer}: <span className="font-bold font-mono tracking-wider">{currentQuestion.word}</span>
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
