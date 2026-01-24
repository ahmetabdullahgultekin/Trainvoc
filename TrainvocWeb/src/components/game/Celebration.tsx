import { useEffect, useState } from 'react'
import Confetti from 'react-confetti'
import { motion, AnimatePresence } from 'framer-motion'
import { cn } from '@/lib/utils'
import { scorePopupVariants, popVariants } from '@/lib/animations'

interface CelebrationProps {
  show: boolean
  score?: number
  message?: string
  onComplete?: () => void
  duration?: number
}

export function Celebration({
  show,
  score,
  message,
  onComplete,
  duration = 2000,
}: CelebrationProps) {
  const [windowSize, setWindowSize] = useState({ width: 0, height: 0 })
  const [isActive, setIsActive] = useState(false)

  useEffect(() => {
    setWindowSize({
      width: window.innerWidth,
      height: window.innerHeight,
    })

    const handleResize = () => {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight,
      })
    }

    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [])

  useEffect(() => {
    if (show) {
      setIsActive(true)
      const timer = setTimeout(() => {
        setIsActive(false)
        onComplete?.()
      }, duration)
      return () => clearTimeout(timer)
    }
  }, [show, duration, onComplete])

  if (!show && !isActive) return null

  return (
    <>
      {/* Confetti */}
      <Confetti
        width={windowSize.width}
        height={windowSize.height}
        recycle={false}
        numberOfPieces={200}
        gravity={0.3}
        colors={['#6366F1', '#22C55E', '#F59E0B', '#EF4444', '#8B5CF6']}
      />

      {/* Score popup */}
      <AnimatePresence>
        {show && (
          <motion.div
            className="fixed inset-0 z-50 flex items-center justify-center pointer-events-none"
            variants={scorePopupVariants}
            initial="initial"
            animate="animate"
          >
            {score !== undefined && (
              <div className="text-center">
                <motion.div
                  className="text-6xl font-game text-success-500"
                  variants={popVariants}
                >
                  +{score}
                </motion.div>
                {message && (
                  <motion.p
                    className="mt-2 text-2xl font-bold text-white"
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0, transition: { delay: 0.2 } }}
                  >
                    {message}
                  </motion.p>
                )}
              </div>
            )}
          </motion.div>
        )}
      </AnimatePresence>
    </>
  )
}

// Simple score popup without confetti
interface ScorePopupProps {
  score: number
  isCorrect: boolean
  className?: string
}

export function ScorePopup({ score, isCorrect, className }: ScorePopupProps) {
  return (
    <motion.div
      className={cn(
        'absolute font-game text-3xl',
        isCorrect ? 'text-success-500' : 'text-error-500',
        className
      )}
      variants={scorePopupVariants}
      initial="initial"
      animate="animate"
    >
      {isCorrect ? `+${score}` : `-${Math.abs(score)}`}
    </motion.div>
  )
}
