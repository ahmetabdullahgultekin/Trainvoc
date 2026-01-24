import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Lightbulb, Eye } from 'lucide-react'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'

interface HintButtonProps {
  hints: string[]
  maxHints?: number
  penalty?: number
  onUseHint?: (hintIndex: number) => void
  disabled?: boolean
  className?: string
}

export function HintButton({
  hints,
  maxHints = 3,
  penalty = 2,
  onUseHint,
  disabled,
  className,
}: HintButtonProps) {
  const [usedHints, setUsedHints] = useState<number[]>([])

  const handleUseHint = () => {
    if (disabled || usedHints.length >= maxHints) return

    const nextHintIndex = usedHints.length
    if (nextHintIndex < hints.length) {
      setUsedHints([...usedHints, nextHintIndex])
      onUseHint?.(nextHintIndex)
    }
  }

  const remainingHints = maxHints - usedHints.length

  return (
    <div className={cn('space-y-2', className)}>
      {/* Hint button */}
      <Button
        variant="outline"
        onClick={handleUseHint}
        disabled={disabled || remainingHints === 0}
        className="gap-2"
      >
        <Lightbulb className="h-4 w-4" />
        <span>
          Use Hint ({remainingHints} left)
        </span>
        {penalty > 0 && (
          <span className="text-error-500 text-xs">-{penalty} pts</span>
        )}
      </Button>

      {/* Revealed hints */}
      <AnimatePresence>
        {usedHints.map((hintIndex) => (
          <motion.div
            key={hintIndex}
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="flex items-start gap-2 p-3 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg border border-yellow-200 dark:border-yellow-800"
          >
            <Lightbulb className="h-4 w-4 text-yellow-500 flex-shrink-0 mt-0.5" />
            <p className="text-sm text-yellow-800 dark:text-yellow-200">
              {hints[hintIndex]}
            </p>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  )
}

// Letter reveal button for spelling games
interface LetterRevealButtonProps {
  word: string
  revealedIndices: number[]
  onReveal?: (index: number) => void
  penalty?: number
  disabled?: boolean
  className?: string
}

export function LetterRevealButton({
  word,
  revealedIndices,
  onReveal,
  penalty = 1,
  disabled,
  className,
}: LetterRevealButtonProps) {
  const handleReveal = () => {
    if (disabled) return

    // Find next unrevealed index
    for (let i = 0; i < word.length; i++) {
      if (!revealedIndices.includes(i)) {
        onReveal?.(i)
        break
      }
    }
  }

  const remainingLetters = word.length - revealedIndices.length

  return (
    <div className={cn('space-y-3', className)}>
      {/* Word display */}
      <div className="flex justify-center gap-1">
        {word.split('').map((letter, index) => (
          <motion.span
            key={index}
            initial={false}
            animate={{
              backgroundColor: revealedIndices.includes(index)
                ? 'rgb(34 197 94 / 0.2)'
                : 'transparent',
            }}
            className={cn(
              'w-10 h-12 flex items-center justify-center text-2xl font-bold rounded-lg border-2',
              revealedIndices.includes(index)
                ? 'border-success-500 text-success-600'
                : 'border-gray-300 dark:border-gray-600 text-transparent'
            )}
          >
            {revealedIndices.includes(index) ? letter.toUpperCase() : '_'}
          </motion.span>
        ))}
      </div>

      {/* Reveal button */}
      <Button
        variant="outline"
        size="sm"
        onClick={handleReveal}
        disabled={disabled || remainingLetters === 0}
        className="gap-2"
      >
        <Eye className="h-4 w-4" />
        <span>Reveal Letter</span>
        {penalty > 0 && (
          <span className="text-error-500 text-xs">-{penalty} pts</span>
        )}
      </Button>
    </div>
  )
}
