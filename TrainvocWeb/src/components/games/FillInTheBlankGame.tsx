import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { PenLine, Check, X, RotateCcw, Star, Clock } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { AnswerGrid } from '@/components/game/AnswerButton'
import { Celebration } from '@/components/game/Celebration'

interface FillInTheBlankQuestion {
  id: string
  sentence: string // Use _____ for blank
  correctAnswer: string
  options: string[]
  translation?: string
}

interface FillInTheBlankGameProps {
  questions: FillInTheBlankQuestion[]
  questionDuration?: number
  onComplete?: (results: { correctCount: number; totalScore: number }) => void
}

export function FillInTheBlankGame({
  questions,
  questionDuration = 30,
  onComplete,
}: FillInTheBlankGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const [currentIndex, setCurrentIndex] = useState(0)
  const [score, setScore] = useState(0)
  const [correctCount, setCorrectCount] = useState(0)
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null)
  const [showResult, setShowResult] = useState(false)
  const [isCorrect, setIsCorrect] = useState(false)
  const [showCelebration, setShowCelebration] = useState(false)
  const [timeLeft, setTimeLeft] = useState(questionDuration)
  const [gameComplete, setGameComplete] = useState(false)

  const currentQuestion = questions[currentIndex]
  const correctIndex = currentQuestion.options.indexOf(currentQuestion.correctAnswer)

  const content = {
    en: {
      title: 'Fill in the Blank',
      instruction: 'Select the correct word to complete the sentence',
      correct: 'Correct!',
      wrong: 'Wrong!',
      timeUp: 'Time up!',
      next: 'Next',
      questionOf: 'of',
      score: 'Score',
      complete: 'Game Complete!',
      playAgain: 'Play Again',
      answer: 'Answer',
    },
    tr: {
      title: 'Boşluk Doldurma',
      instruction: 'Cümleyi tamamlamak için doğru kelimeyi seç',
      correct: 'Doğru!',
      wrong: 'Yanlış!',
      timeUp: 'Süre doldu!',
      next: 'Sonraki',
      questionOf: '/',
      score: 'Puan',
      complete: 'Oyun Bitti!',
      playAgain: 'Tekrar Oyna',
      answer: 'Cevap',
    },
  }

  const t = content[lang]

  // Timer
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
    if (selectedIndex !== null) return
    setShowResult(true)
    setIsCorrect(false)
  }

  const handleAnswer = (index: number) => {
    if (selectedIndex !== null || showResult) return

    const answer = currentQuestion.options[index]
    const correct = answer === currentQuestion.correctAnswer
    setSelectedIndex(index)
    setIsCorrect(correct)
    setShowResult(true)

    if (correct) {
      const timeBonus = Math.floor((timeLeft / questionDuration) * 50)
      setScore((prev) => prev + 100 + timeBonus)
      setCorrectCount((prev) => prev + 1)
      setShowCelebration(true)
      setTimeout(() => setShowCelebration(false), 1500)
    }
  }

  const handleNext = () => {
    if (currentIndex + 1 >= questions.length) {
      setGameComplete(true)
      onComplete?.({ correctCount, totalScore: score })
    } else {
      setCurrentIndex((prev) => prev + 1)
      setSelectedIndex(null)
      setShowResult(false)
      setTimeLeft(questionDuration)
    }
  }

  // Highlight the blank in the sentence
  const renderSentence = () => {
    const parts = currentQuestion.sentence.split('_____')
    return (
      <p className="text-xl md:text-2xl text-gray-900 dark:text-white leading-relaxed">
        {parts[0]}
        <span className="inline-block min-w-[100px] mx-2 border-b-4 border-brand-500 bg-brand-50 dark:bg-brand-900/30 px-3 py-1 rounded">
          {showResult ? (
            <span className={isCorrect ? 'text-green-600' : 'text-red-600'}>
              {currentQuestion.correctAnswer}
            </span>
          ) : selectedIndex !== null ? (
            currentQuestion.options[selectedIndex]
          ) : (
            <span className="text-gray-400">?</span>
          )}
        </span>
        {parts[1]}
      </p>
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
            <p className="text-gray-600 dark:text-gray-400 mt-4 mb-6">
              {correctCount} / {questions.length} {t.correct}
            </p>
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

      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-game text-white flex items-center gap-2">
            <PenLine className="h-6 w-6" />
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
            <Card className="p-6 md:p-8 mb-6">
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
                {t.instruction}
              </p>
              {renderSentence()}
              {currentQuestion.translation && (
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-4 italic">
                  {currentQuestion.translation}
                </p>
              )}
            </Card>
          </motion.div>
        </AnimatePresence>

        {/* Answer Options */}
        {!showResult ? (
          <AnswerGrid
            options={currentQuestion.options}
            onSelect={handleAnswer}
            selectedIndex={selectedIndex ?? undefined}
            correctIndex={correctIndex}
            showResult={false}
            disabled={false}
          />
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
                  {selectedIndex === null ? t.timeUp : isCorrect ? t.correct : t.wrong}
                </span>
              </div>
              {!isCorrect && (
                <p className="text-gray-600 dark:text-gray-400">
                  {t.answer}: <span className="font-bold">{currentQuestion.correctAnswer}</span>
                </p>
              )}
            </div>
            <Button className="w-full" onClick={handleNext}>
              {t.next}
            </Button>
          </motion.div>
        )}
      </div>
    </div>
  )
}
