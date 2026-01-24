import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Search, Check, X, RotateCcw, Eye, Star } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Celebration } from '@/components/game/Celebration'

interface ContextCluesQuestion {
  id: string
  word: string
  translation: string
  clues: string[]
}

interface ContextCluesGameProps {
  questions: ContextCluesQuestion[]
  onComplete?: (results: { correctCount: number; totalScore: number }) => void
}

export function ContextCluesGame({
  questions,
  onComplete,
}: ContextCluesGameProps) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const [currentIndex, setCurrentIndex] = useState(0)
  const [userGuess, setUserGuess] = useState('')
  const [score, setScore] = useState(0)
  const [correctCount, setCorrectCount] = useState(0)
  const [revealedClues, setRevealedClues] = useState<number[]>([0])
  const [showResult, setShowResult] = useState(false)
  const [isCorrect, setIsCorrect] = useState(false)
  const [showCelebration, setShowCelebration] = useState(false)
  const [gameComplete, setGameComplete] = useState(false)
  const [attempts, setAttempts] = useState(0)

  const currentQuestion = questions[currentIndex]
  const maxClues = currentQuestion.clues.length

  const content = {
    en: {
      title: 'Context Clues',
      instruction: 'Use the clues to guess the word!',
      clue: 'Clue',
      yourGuess: 'Your guess...',
      submit: 'Submit',
      revealClue: 'Reveal Clue',
      cluesPenalty: '-20 points',
      correct: 'Correct!',
      wrong: 'Try again',
      giveUp: 'Give Up',
      answer: 'The word was',
      next: 'Next',
      questionOf: 'of',
      score: 'Score',
      complete: 'Game Complete!',
      playAgain: 'Play Again',
      attempts: 'Attempts',
      cluesUsed: 'Clues used',
    },
    tr: {
      title: 'Bağlam İpuçları',
      instruction: 'İpuçlarını kullanarak kelimeyi tahmin et!',
      clue: 'İpucu',
      yourGuess: 'Tahminin...',
      submit: 'Gönder',
      revealClue: 'İpucu Göster',
      cluesPenalty: '-20 puan',
      correct: 'Doğru!',
      wrong: 'Tekrar dene',
      giveUp: 'Vazgeç',
      answer: 'Kelime şuydu',
      next: 'Sonraki',
      questionOf: '/',
      score: 'Puan',
      complete: 'Oyun Bitti!',
      playAgain: 'Tekrar Oyna',
      attempts: 'Deneme',
      cluesUsed: 'Kullanılan ipucu',
    },
  }

  const t = content[lang]

  const handleGuess = () => {
    if (!userGuess.trim() || showResult) return

    const correct = userGuess.toLowerCase().trim() === currentQuestion.word.toLowerCase()
    setAttempts((prev) => prev + 1)

    if (correct) {
      setIsCorrect(true)
      setShowResult(true)

      const baseScore = 100
      const cluesPenalty = (revealedClues.length - 1) * 20
      const attemptsPenalty = (attempts) * 10
      const earnedScore = Math.max(10, baseScore - cluesPenalty - attemptsPenalty)

      setScore((prev) => prev + earnedScore)
      setCorrectCount((prev) => prev + 1)
      setShowCelebration(true)
      setTimeout(() => setShowCelebration(false), 1500)
    } else {
      setIsCorrect(false)
      setUserGuess('')
    }
  }

  const handleRevealClue = () => {
    if (revealedClues.length >= maxClues) return
    const nextClueIndex = revealedClues.length
    setRevealedClues((prev) => [...prev, nextClueIndex])
  }

  const handleGiveUp = () => {
    setIsCorrect(false)
    setShowResult(true)
  }

  const handleNext = () => {
    if (currentIndex + 1 >= questions.length) {
      setGameComplete(true)
      onComplete?.({ correctCount, totalScore: score })
    } else {
      setCurrentIndex((prev) => prev + 1)
      setUserGuess('')
      setRevealedClues([0])
      setShowResult(false)
      setAttempts(0)
    }
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
            <Search className="h-6 w-6" />
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

        {/* Question Card */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestion.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            <Card className="p-6 md:p-8">
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
                {t.instruction}
              </p>

              {/* Meaning */}
              <div className="bg-brand-50 dark:bg-brand-900/30 rounded-xl p-4 mb-6 text-center">
                <p className="text-lg font-medium text-brand-700 dark:text-brand-300">
                  "{currentQuestion.translation}"
                </p>
              </div>

              {/* Clues */}
              <div className="space-y-3 mb-6">
                {currentQuestion.clues.map((clue, index) => {
                  const isRevealed = revealedClues.includes(index)
                  return (
                    <motion.div
                      key={index}
                      initial={isRevealed ? { opacity: 0, x: -20 } : false}
                      animate={{ opacity: 1, x: 0 }}
                      className={`p-4 rounded-lg border-2 transition-all ${
                        isRevealed
                          ? 'bg-yellow-50 dark:bg-yellow-900/20 border-yellow-300 dark:border-yellow-700'
                          : 'bg-gray-100 dark:bg-gray-800 border-gray-200 dark:border-gray-700 opacity-50'
                      }`}
                    >
                      <div className="flex items-start gap-3">
                        <Badge
                          variant={isRevealed ? 'default' : 'secondary'}
                          className={isRevealed ? 'bg-yellow-500' : ''}
                        >
                          {t.clue} {index + 1}
                        </Badge>
                        {isRevealed ? (
                          <p className="text-gray-700 dark:text-gray-300 flex-1">{clue}</p>
                        ) : (
                          <p className="text-gray-400 dark:text-gray-500 flex-1 italic">???</p>
                        )}
                      </div>
                    </motion.div>
                  )
                })}
              </div>

              {/* Reveal clue button */}
              {!showResult && revealedClues.length < maxClues && (
                <Button
                  variant="outline"
                  className="w-full mb-4 gap-2"
                  onClick={handleRevealClue}
                >
                  <Eye className="h-4 w-4" />
                  {t.revealClue} ({t.cluesPenalty})
                </Button>
              )}

              {/* Input and submit */}
              {!showResult ? (
                <div className="space-y-4">
                  <Input
                    value={userGuess}
                    onChange={(e) => setUserGuess(e.target.value)}
                    placeholder={t.yourGuess}
                    className="text-lg text-center"
                    onKeyDown={(e) => e.key === 'Enter' && userGuess && handleGuess()}
                  />
                  <div className="flex gap-3">
                    <Button
                      variant="outline"
                      className="flex-1"
                      onClick={handleGiveUp}
                    >
                      {t.giveUp}
                    </Button>
                    <Button
                      className="flex-1 gap-2"
                      onClick={handleGuess}
                      disabled={!userGuess}
                    >
                      <Check className="h-4 w-4" />
                      {t.submit}
                    </Button>
                  </div>

                  {/* Attempt feedback */}
                  {attempts > 0 && !isCorrect && (
                    <motion.p
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      className="text-center text-orange-500 text-sm"
                    >
                      {t.wrong} ({t.attempts}: {attempts})
                    </motion.p>
                  )}
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
                        {isCorrect ? t.correct : t.answer}
                      </span>
                    </div>
                    <p className="text-2xl font-bold text-gray-900 dark:text-white">
                      {currentQuestion.word}
                    </p>
                    {isCorrect && (
                      <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">
                        {t.cluesUsed}: {revealedClues.length}/{maxClues} | {t.attempts}: {attempts}
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
