import { motion, AnimatePresence } from 'framer-motion'
import { Flame, Zap, Star } from 'lucide-react'
import { cn } from '@/lib/utils'
import { comboVariants, popVariants } from '@/lib/animations'

interface ComboIndicatorProps {
  combo: number
  className?: string
}

export function ComboIndicator({ combo, className }: ComboIndicatorProps) {
  if (combo < 2) return null

  const getComboConfig = (combo: number) => {
    if (combo >= 10) {
      return {
        icon: Star,
        color: 'text-purple-500',
        bgColor: 'bg-purple-100 dark:bg-purple-900/30',
        label: 'UNSTOPPABLE!',
      }
    }
    if (combo >= 5) {
      return {
        icon: Flame,
        color: 'text-orange-500',
        bgColor: 'bg-orange-100 dark:bg-orange-900/30',
        label: 'ON FIRE!',
      }
    }
    return {
      icon: Zap,
      color: 'text-yellow-500',
      bgColor: 'bg-yellow-100 dark:bg-yellow-900/30',
      label: 'COMBO',
    }
  }

  const config = getComboConfig(combo)
  const Icon = config.icon

  return (
    <AnimatePresence mode="wait">
      <motion.div
        key={combo}
        className={cn(
          'inline-flex items-center gap-2 px-4 py-2 rounded-full font-bold',
          config.bgColor,
          config.color,
          className
        )}
        variants={comboVariants}
        initial="initial"
        animate="animate"
        exit="exit"
      >
        <Icon className="h-5 w-5" />
        <span className="text-lg">{combo}x</span>
        <span className="text-sm">{config.label}</span>
      </motion.div>
    </AnimatePresence>
  )
}

// Streak indicator for consecutive correct answers
interface StreakIndicatorProps {
  streak: number
  className?: string
}

export function StreakIndicator({ streak, className }: StreakIndicatorProps) {
  if (streak < 1) return null

  return (
    <motion.div
      className={cn(
        'flex items-center gap-1',
        className
      )}
      variants={popVariants}
      initial="initial"
      animate="animate"
    >
      {Array.from({ length: Math.min(streak, 5) }).map((_, i) => (
        <motion.div
          key={i}
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ delay: i * 0.1 }}
        >
          <Flame
            className={cn(
              'h-5 w-5',
              streak >= 5 ? 'text-orange-500' : 'text-yellow-500'
            )}
          />
        </motion.div>
      ))}
      {streak > 5 && (
        <span className="ml-1 text-sm font-bold text-orange-500">
          +{streak - 5}
        </span>
      )}
    </motion.div>
  )
}
