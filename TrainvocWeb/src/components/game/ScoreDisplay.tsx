import { useEffect, useState, useRef } from 'react'
import { motion, useSpring, useTransform } from 'framer-motion'
import { Star, Trophy } from 'lucide-react'
import { cn } from '@/lib/utils'

interface ScoreDisplayProps {
  score: number
  previousScore?: number
  className?: string
}

export function ScoreDisplay({ score, previousScore = 0, className }: ScoreDisplayProps) {
  const [isAnimating, setIsAnimating] = useState(false)

  // Animated number using spring
  const spring = useSpring(previousScore, { stiffness: 100, damping: 20 })
  const displayValue = useTransform(spring, (latest) => Math.round(latest))

  useEffect(() => {
    if (score !== previousScore) {
      setIsAnimating(true)
      spring.set(score)
      const timer = setTimeout(() => setIsAnimating(false), 500)
      return () => clearTimeout(timer)
    }
  }, [score, previousScore, spring])

  return (
    <motion.div
      className={cn(
        'flex items-center gap-2 px-4 py-2 rounded-full bg-brand-100 dark:bg-brand-900/30',
        className
      )}
      animate={isAnimating ? { scale: [1, 1.1, 1] } : {}}
    >
      <Star className="h-5 w-5 text-brand-500" />
      <motion.span className="font-bold text-xl text-brand-600 dark:text-brand-400">
        {displayValue}
      </motion.span>
    </motion.div>
  )
}

// Detailed score breakdown
interface ScoreBreakdownProps {
  baseScore: number
  timeBonus?: number
  comboBonus?: number
  accuracy?: number
  className?: string
}

export function ScoreBreakdown({
  baseScore,
  timeBonus = 0,
  comboBonus = 0,
  accuracy,
  className,
}: ScoreBreakdownProps) {
  const total = baseScore + timeBonus + comboBonus

  return (
    <div className={cn('space-y-3 p-4 rounded-xl bg-gray-50 dark:bg-gray-800', className)}>
      {/* Base score */}
      <div className="flex justify-between items-center">
        <span className="text-gray-600 dark:text-gray-400">Base Score</span>
        <span className="font-semibold text-gray-900 dark:text-white">
          {baseScore.toLocaleString()}
        </span>
      </div>

      {/* Time bonus */}
      {timeBonus > 0 && (
        <div className="flex justify-between items-center text-success-600">
          <span>Time Bonus</span>
          <span className="font-semibold">+{timeBonus.toLocaleString()}</span>
        </div>
      )}

      {/* Combo bonus */}
      {comboBonus > 0 && (
        <div className="flex justify-between items-center text-warning-600">
          <span>Combo Bonus</span>
          <span className="font-semibold">+{comboBonus.toLocaleString()}</span>
        </div>
      )}

      {/* Accuracy */}
      {accuracy !== undefined && (
        <div className="flex justify-between items-center">
          <span className="text-gray-600 dark:text-gray-400">Accuracy</span>
          <span className="font-semibold text-brand-600">{accuracy}%</span>
        </div>
      )}

      {/* Divider */}
      <div className="border-t border-gray-200 dark:border-gray-700 my-2" />

      {/* Total */}
      <div className="flex justify-between items-center">
        <span className="text-lg font-bold text-gray-900 dark:text-white">Total</span>
        <div className="flex items-center gap-2">
          <Trophy className="h-5 w-5 text-yellow-500" />
          <span className="text-xl font-bold text-brand-600 dark:text-brand-400">
            {total.toLocaleString()}
          </span>
        </div>
      </div>
    </div>
  )
}

// Animated counter for final results
interface AnimatedCounterProps {
  value: number
  duration?: number
  className?: string
}

export function AnimatedCounter({ value, duration = 2, className }: AnimatedCounterProps) {
  const [count, setCount] = useState(0)
  const countRef = useRef<number>(0)

  useEffect(() => {
    const steps = 60 * duration
    const increment = value / steps
    let currentStep = 0

    const interval = setInterval(() => {
      currentStep++
      countRef.current = Math.min(Math.round(increment * currentStep), value)
      setCount(countRef.current)

      if (currentStep >= steps) {
        clearInterval(interval)
      }
    }, 1000 / 60)

    return () => clearInterval(interval)
  }, [value, duration])

  return (
    <span className={cn('font-game tabular-nums', className)}>
      {count.toLocaleString()}
    </span>
  )
}
