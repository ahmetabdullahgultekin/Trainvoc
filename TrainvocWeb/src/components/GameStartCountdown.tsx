import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'

interface GameStartCountdownProps {
  onComplete: () => void
}

const GameStartCountdown: React.FC<GameStartCountdownProps> = ({ onComplete }) => {
  const [count, setCount] = useState(3)
  const [done, setDone] = useState(false)

  useEffect(() => {
    if (count === 0 && !done) {
      setDone(true)
      onComplete()
      return
    }
    if (count > 0) {
      const timer = setTimeout(() => setCount(c => c - 1), 1000)
      return () => clearTimeout(timer)
    }
  }, [count, onComplete, done])

  return (
    <div className="flex flex-col items-center justify-center min-h-[40vh]">
      <AnimatePresence mode="wait">
        <motion.div
          key={count}
          initial={{ scale: 0.5, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          exit={{ scale: 1.5, opacity: 0 }}
          transition={{ duration: 0.3 }}
          className="text-center"
        >
          <h2 className="text-8xl font-bold text-brand-600 dark:text-brand-400">
            {count === 0 ? 'GO!' : count}
          </h2>
        </motion.div>
      </AnimatePresence>
      <p className="mt-4 text-gray-500 dark:text-gray-400">
        Oyun başlamak üzere...
      </p>
    </div>
  )
}

export default GameStartCountdown
