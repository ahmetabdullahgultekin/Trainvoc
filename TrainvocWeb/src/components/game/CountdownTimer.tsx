import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { cn } from '@/lib/utils'
import { countdownVariants, timerWarningVariants } from '@/lib/animations'
import { CircularProgress } from '@/components/ui/progress'

interface CountdownTimerProps {
  seconds: number
  onComplete?: () => void
  isPaused?: boolean
  className?: string
  size?: 'sm' | 'md' | 'lg'
}

export function CountdownTimer({
  seconds,
  onComplete,
  isPaused = false,
  className,
  size = 'md',
}: CountdownTimerProps) {
  const [timeLeft, setTimeLeft] = useState(seconds)

  useEffect(() => {
    setTimeLeft(seconds)
  }, [seconds])

  useEffect(() => {
    if (isPaused || timeLeft <= 0) return

    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(timer)
          onComplete?.()
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(timer)
  }, [isPaused, timeLeft, onComplete])

  const percentage = (timeLeft / seconds) * 100
  const isWarning = timeLeft <= 10 && timeLeft > 5
  const isCritical = timeLeft <= 5

  const variant = isCritical ? 'error' : isWarning ? 'warning' : 'brand'
  const sizeConfig = {
    sm: { size: 48, fontSize: 'text-lg' },
    md: { size: 80, fontSize: 'text-2xl' },
    lg: { size: 120, fontSize: 'text-4xl' },
  }

  return (
    <motion.div
      className={cn('relative inline-flex', className)}
      variants={timerWarningVariants}
      animate={isCritical ? 'critical' : isWarning ? 'warning' : 'normal'}
    >
      <CircularProgress
        value={percentage}
        size={sizeConfig[size].size}
        variant={variant}
        showValue={false}
      />
      <span
        className={cn(
          'absolute inset-0 flex items-center justify-center font-bold font-game',
          sizeConfig[size].fontSize,
          isCritical && 'text-error-500',
          isWarning && 'text-warning-500',
          !isWarning && !isCritical && 'text-gray-900 dark:text-white'
        )}
      >
        {timeLeft}
      </span>
    </motion.div>
  )
}

// Full-screen countdown overlay for game start
interface GameStartCountdownProps {
  onComplete?: () => void
}

export function GameStartCountdown({ onComplete }: GameStartCountdownProps) {
  const [count, setCount] = useState(3)

  useEffect(() => {
    if (count === 0) {
      onComplete?.()
      return
    }

    const timer = setTimeout(() => {
      setCount((prev) => prev - 1)
    }, 1000)

    return () => clearTimeout(timer)
  }, [count, onComplete])

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm">
      <AnimatePresence mode="wait">
        <motion.div
          key={count}
          variants={countdownVariants}
          initial="initial"
          animate="animate"
          exit="exit"
          className="text-white font-game"
        >
          {count === 0 ? (
            <span className="text-8xl text-success-500">GO!</span>
          ) : (
            <span className="text-9xl">{count}</span>
          )}
        </motion.div>
      </AnimatePresence>
    </div>
  )
}
