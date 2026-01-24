import { useEffect, useRef, useState } from 'react'
import { motion } from 'framer-motion'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

interface GameQuestionProps {
  question: string
  options: string[]
  onAnswer: (answer: string, answerTime: number) => void
  timeLimit: number
  timeLeft: number
  answered?: boolean
  correctMeaning?: string
  selectedAnswer?: string | null
}

const GameQuestion: React.FC<GameQuestionProps> = ({
  question,
  options,
  onAnswer,
  timeLimit,
  timeLeft: initialTimeLeft,
  answered,
  correctMeaning,
  selectedAnswer
}) => {
  const [selected, setSelected] = useState<string | null>(selectedAnswer ?? null)
  const [localTimeLeft, setLocalTimeLeft] = useState<number>(initialTimeLeft)
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null)

  useEffect(() => {
    setLocalTimeLeft(initialTimeLeft)
  }, [initialTimeLeft])

  useEffect(() => {
    if (answered || selected !== null) return
    setLocalTimeLeft(initialTimeLeft)
    if (timerRef.current) clearInterval(timerRef.current)
    let last = Date.now()
    timerRef.current = setInterval(() => {
      setLocalTimeLeft(prev => {
        const now = Date.now()
        const elapsed = (now - last) / 1000
        last = now
        const next = prev - elapsed
        if (next <= 0) {
          clearInterval(timerRef.current!)
          return 0
        }
        return next
      })
    }, 50)
    return () => {
      if (timerRef.current) clearInterval(timerRef.current)
    }
  }, [initialTimeLeft, answered, selected])

  useEffect(() => {
    if (localTimeLeft <= 0 && selected === null && !answered) {
      onAnswer('', timeLimit)
    }
  }, [localTimeLeft, selected, answered, onAnswer, timeLimit])

  const progressValue = Math.max(0, Math.min(100, (localTimeLeft / timeLimit) * 100))
  const isCritical = localTimeLeft <= 5

  const handleSelect = (opt: string) => {
    if (selected !== null) return
    setSelected(opt)
    if (timerRef.current) clearInterval(timerRef.current)
    const usedTime = timeLimit - localTimeLeft
    onAnswer(opt, usedTime)
  }

  const getOptionStyle = (opt: string) => {
    if (answered && selected) {
      if (opt === selected && selected !== correctMeaning) {
        return 'bg-red-500 text-white border-red-500 hover:bg-red-500'
      } else if (opt === correctMeaning) {
        return 'bg-green-500 text-white border-green-500 hover:bg-green-500'
      } else {
        return 'bg-gray-100 text-gray-400 border-gray-200'
      }
    } else if (selected === opt) {
      return 'bg-brand-600 text-white border-brand-600'
    }
    return 'bg-white dark:bg-gray-800 hover:bg-brand-50 dark:hover:bg-brand-900/20'
  }

  return (
    <Card className="max-w-lg mx-auto mt-6 p-6">
      <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4">
        {question}
      </h2>

      {/* Progress bar */}
      <div className="w-full h-2 bg-gray-200 dark:bg-gray-700 rounded-full mb-2 overflow-hidden">
        <motion.div
          className={cn(
            'h-full rounded-full transition-colors',
            isCritical ? 'bg-red-500' : 'bg-brand-500'
          )}
          style={{ width: `${progressValue}%` }}
          transition={{ duration: 0.1 }}
        />
      </div>

      <p className="text-sm text-gray-500 dark:text-gray-400 text-right mb-4">
        {Math.ceil(localTimeLeft)} sn
      </p>

      {/* Options */}
      <div className="space-y-3">
        {options.map(opt => (
          <Button
            key={opt}
            variant="outline"
            className={cn(
              'w-full py-4 text-lg font-semibold transition-all',
              getOptionStyle(opt)
            )}
            onClick={() => {
              if (!answered && selected === null && localTimeLeft > 0) {
                handleSelect(opt)
              }
            }}
            disabled={!answered && (selected !== null || localTimeLeft === 0)}
          >
            {opt}
          </Button>
        ))}
      </div>

      <p className={cn(
        'mt-4 text-xl font-bold text-center',
        isCritical ? 'text-red-500 animate-pulse' : 'text-brand-600'
      )}>
        Kalan süre: {Math.ceil(localTimeLeft)} sn
      </p>
    </Card>
  )
}

export default GameQuestion
