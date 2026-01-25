import { useState, useEffect, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Headphones, Volume2, VolumeX, Check, X, RotateCcw, Play, Star, Clock } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { AnswerGrid } from '@/components/game/AnswerButton'
import { Celebration } from '@/components/game/Celebration'

interface ListeningQuestion {
  id: string
  word: string
  correctTranslation: string
  options: string[]
}

interface ListeningQuizGameProps {
  questions: ListeningQuestion[]
  questionDuration?: number
  maxPlays?: number
  onComplete?: (results: { correctCount: number; totalScore: number }) => void
}

export function ListeningQuizGame({
  questions,
  questionDuration = 30,
  maxPlays = 3,
  onComplete,
}: ListeningQuizGameProps) {
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
  const [playsRemaining, setPlaysRemaining] = useState(maxPlays)
  const [isPlaying, setIsPlaying] = useState(false)
  const [speechSupported, setSpeechSupported] = useState(true)

  const currentQuestion = questions[currentIndex]
  const correctIndex = currentQuestion.options.indexOf(currentQuestion.correctTranslation)

  const content = {
    en: {
      title: 'Listening Quiz',
      instruction: 'Listen to the word and select its meaning',
      playSound: 'Play',
      playsLeft: 'plays left',
      correct: 'Correct!',
      wrong: 'Wrong!',
      timeUp: 'Time up!',
      next: 'Next',
      questionOf: 'of',
      score: 'Score',
      complete: 'Game Complete!',
      playAgain: 'Play Again',
      answer: 'Answer',
      theWord: 'The word was',
      noSpeech: 'Speech synthesis not supported in this browser',
    },
    tr: {
      title: 'Dinleme Testi',
      instruction: 'Kelimeyi dinle ve anlamını seç',
      playSound: 'Oynat',
      playsLeft: 'hak kaldı',
      correct: 'Doğru!',
      wrong: 'Yanlış!',
      timeUp: 'Süre doldu!',
      next: 'Sonraki',
      questionOf: '/',
      score: 'Puan',
      complete: 'Oyun Bitti!',
      playAgain: 'Tekrar Oyna',
      answer: 'Cevap',
      theWord: 'Kelime şuydu',
      noSpeech: 'Bu tarayıcı ses sentezini desteklemiyor',
    },
  }

  const t = content[lang]

  // Check for speech synthesis support
  useEffect(() => {
    if (!('speechSynthesis' in window)) {
      setSpeechSupported(false)
    }
  }, [])

  // Speak the word
  const speakWord = useCallback(() => {
    if (!speechSupported || playsRemaining <= 0 || isPlaying) return

    setIsPlaying(true)
    setPlaysRemaining((prev) => prev - 1)

    const utterance = new SpeechSynthesisUtterance(currentQuestion.word)
    utterance.lang = 'en-US'
    utterance.rate = 0.8

    utterance.onend = () => setIsPlaying(false)
    utterance.onerror = () => setIsPlaying(false)

    window.speechSynthesis.speak(utterance)
  }, [currentQuestion.word, playsRemaining, isPlaying, speechSupported])

  // Auto-play on question load
  useEffect(() => {
    if (speechSupported && !showResult && playsRemaining === maxPlays) {
      const timeout = setTimeout(() => speakWord(), 500)
      return () => clearTimeout(timeout)
    }
  }, [currentIndex])

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
    const correct = answer === currentQuestion.correctTranslation
    setSelectedIndex(index)
    setIsCorrect(correct)
    setShowResult(true)

    if (correct) {
      const timeBonus = Math.floor((timeLeft / questionDuration) * 50)
      const playBonus = playsRemaining * 10
      setScore((prev) => prev + 100 + timeBonus + playBonus)
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
      setPlaysRemaining(maxPlays)
    }
  }

  const resetGame = () => {
    setCurrentIndex(0)
    setScore(0)
    setCorrectCount(0)
    setSelectedIndex(null)
    setShowResult(false)
    setIsCorrect(false)
    setShowCelebration(false)
    setTimeLeft(questionDuration)
    setGameComplete(false)
    setPlaysRemaining(maxPlays)
    setIsPlaying(false)
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

      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-game text-white flex items-center gap-2">
            <Headphones className="h-6 w-6" />
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

        {/* Sound Player Card */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestion.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            <Card className="p-6 md:p-8 mb-6">
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
                {t.instruction}
              </p>

              {!speechSupported ? (
                <div className="text-center p-6 bg-red-50 dark:bg-red-900/20 rounded-xl">
                  <VolumeX className="h-12 w-12 mx-auto text-red-500 mb-2" />
                  <p className="text-red-600 dark:text-red-400">{t.noSpeech}</p>
                </div>
              ) : (
                <div className="text-center">
                  <motion.button
                    onClick={speakWord}
                    disabled={playsRemaining <= 0 || isPlaying}
                    className={`w-32 h-32 mx-auto rounded-full flex items-center justify-center transition-all ${
                      isPlaying
                        ? 'bg-brand-600 animate-pulse'
                        : playsRemaining > 0
                        ? 'bg-brand-500 hover:bg-brand-600'
                        : 'bg-gray-400 cursor-not-allowed'
                    }`}
                    whileHover={playsRemaining > 0 && !isPlaying ? { scale: 1.05 } : {}}
                    whileTap={playsRemaining > 0 && !isPlaying ? { scale: 0.95 } : {}}
                  >
                    {isPlaying ? (
                      <Volume2 className="h-16 w-16 text-white animate-bounce" />
                    ) : (
                      <Play className="h-16 w-16 text-white ml-2" />
                    )}
                  </motion.button>
                  <p className="text-gray-500 dark:text-gray-400 mt-4">
                    {playsRemaining} {t.playsLeft}
                  </p>
                </div>
              )}

              {showResult && (
                <div className="mt-6 p-4 bg-gray-100 dark:bg-gray-800 rounded-xl text-center">
                  <p className="text-gray-500 dark:text-gray-400 text-sm">{t.theWord}:</p>
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">
                    {currentQuestion.word}
                  </p>
                </div>
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
                  {t.answer}: <span className="font-bold">{currentQuestion.correctTranslation}</span>
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
