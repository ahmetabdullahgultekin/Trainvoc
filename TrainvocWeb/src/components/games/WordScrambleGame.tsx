import { useState, useMemo } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Shuffle, Check, X, RotateCcw, Eye, Star } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Celebration } from '@/components/game/Celebration'

interface WordScrambleQuestion {
  id: string
  word: string
  translation: string
  hint?: string
}

interface WordScrambleGameProps {
  questions: WordScrambleQuestion[]
  onComplete?: (results: { correctCount: number; totalScore: number }) => void
}

export function WordScrambleGame({ questions, onComplete }: WordScrambleGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const [currentIndex, setCurrentIndex] = useState(0)
  const [userInput, setUserInput] = useState('')
  const [score, setScore] = useState(0)
  const [correctCount, setCorrectCount] = useState(0)
  const [showResult, setShowResult] = useState(false)
  const [isCorrect, setIsCorrect] = useState(false)
  const [hintsUsed, setHintsUsed] = useState(0)
  const [revealedLetters, setRevealedLetters] = useState<number[]>([])
  const [showCelebration, setShowCelebration] = useState(false)
  const [gameComplete, setGameComplete] = useState(false)

  const currentQuestion = questions[currentIndex]

  const content = {
    en: {
      title: 'Word Scramble',
      instruction: 'Unscramble the letters to form the correct word',
      meaning: 'Meaning',
      typeAnswer: 'Type your answer...',
      check: 'Check',
      correct: 'Correct!',
      wrong: 'Wrong!',
      answer: 'Answer',
      next: 'Next',
      hint: 'Hint',
      hintPenalty: '-20 points',
      score: 'Score',
      questionOf: 'of',
      complete: 'Game Complete!',
      playAgain: 'Play Again',
    },
    tr: {
      title: 'Kelime Karıştırma',
      instruction: 'Harfleri doğru sıraya koy ve kelimeyi oluştur',
      meaning: 'Anlam',
      typeAnswer: 'Cevabını yaz...',
      check: 'Kontrol Et',
      correct: 'Doğru!',
      wrong: 'Yanlış!',
      answer: 'Cevap',
      next: 'Sonraki',
      hint: 'İpucu',
      hintPenalty: '-20 puan',
      score: 'Puan',
      questionOf: '/',
      complete: 'Oyun Bitti!',
      playAgain: 'Tekrar Oyna',
    },
  }

  const t = content[lang]

  // Scramble the word
  const scrambledWord = useMemo(() => {
    const letters = currentQuestion.word.split('')
    let scrambled = [...letters]
    for (let i = 0; i < 10; i++) {
      scrambled = scrambled.sort(() => Math.random() - 0.5)
      if (scrambled.join('') !== currentQuestion.word) break
    }
    return scrambled.join('')
  }, [currentQuestion])

  const handleCheck = () => {
    const correct = userInput.toLowerCase().trim() === currentQuestion.word.toLowerCase()
    setIsCorrect(correct)
    setShowResult(true)

    if (correct) {
      const pointsEarned = Math.max(10, 100 - hintsUsed * 20)
      setScore((prev) => prev + pointsEarned)
      setCorrectCount((prev) => prev + 1)
      setShowCelebration(true)
      setTimeout(() => setShowCelebration(false), 2000)
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
      onComplete?.({ correctCount, totalScore: score })
    } else {
      setCurrentIndex((prev) => prev + 1)
      setUserInput('')
      setShowResult(false)
      setRevealedLetters([])
      setHintsUsed(0)
    }
  }

  const getHintDisplay = () => {
    return currentQuestion.word
      .split('')
      .map((letter, i) => (revealedLetters.includes(i) ? letter : '_'))
      .join(' ')
  }

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

      <div className="max-w-xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-game text-white flex items-center gap-2">
            <Shuffle className="h-6 w-6" />
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

        {/* Game Card */}
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

              {/* Scrambled word display */}
              <div className="bg-gray-100 dark:bg-gray-800 rounded-xl p-6 mb-6 text-center">
                <div className="flex justify-center gap-2 flex-wrap">
                  {scrambledWord.split('').map((letter, index) => (
                    <motion.div
                      key={index}
                      initial={{ rotateY: 0 }}
                      animate={{ rotateY: [0, 180, 360] }}
                      transition={{ delay: index * 0.1, duration: 0.5 }}
                      className="w-12 h-12 bg-brand-500 text-white rounded-lg flex items-center justify-center text-2xl font-bold shadow-lg"
                    >
                      {letter.toUpperCase()}
                    </motion.div>
                  ))}
                </div>
              </div>

              {/* Meaning */}
              <div className="mb-6">
                <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">{t.meaning}:</p>
                <p className="text-lg font-medium text-gray-900 dark:text-white">
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

              {/* Input and actions */}
              {!showResult ? (
                <div className="space-y-4">
                  <Input
                    value={userInput}
                    onChange={(e) => setUserInput(e.target.value)}
                    placeholder={t.typeAnswer}
                    className="text-lg text-center"
                    onKeyDown={(e) => e.key === 'Enter' && userInput && handleCheck()}
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
                      <span className="text-xs text-red-500">{t.hintPenalty}</span>
                    </Button>
                    <Button
                      className="flex-1 gap-2"
                      onClick={handleCheck}
                      disabled={!userInput}
                    >
                      <Check className="h-4 w-4" />
                      {t.check}
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
                        {isCorrect ? t.correct : t.wrong}
                      </span>
                    </div>
                    {!isCorrect && (
                      <p className="text-gray-600 dark:text-gray-400">
                        {t.answer}: <span className="font-bold">{currentQuestion.word}</span>
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
