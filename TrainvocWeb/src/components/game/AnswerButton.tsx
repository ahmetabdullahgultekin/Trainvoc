import { forwardRef } from 'react'
import { motion, type HTMLMotionProps } from 'framer-motion'
import { Triangle, Diamond, Circle, Square } from 'lucide-react'
import { cn } from '@/lib/utils'
import { answerButtonVariants } from '@/lib/animations'

type AnswerColor = 'red' | 'blue' | 'yellow' | 'green'

interface AnswerButtonProps extends Omit<HTMLMotionProps<'button'>, 'children'> {
  color: AnswerColor
  text: string
  index: number
  isSelected?: boolean
  isCorrect?: boolean
  isWrong?: boolean
  isDisabled?: boolean
  showResult?: boolean
}

const colorConfig: Record<AnswerColor, { bg: string; hover: string; shadow: string; icon: typeof Triangle }> = {
  red: {
    bg: 'bg-game-red',
    hover: 'hover:bg-red-600',
    shadow: 'shadow-answer-red',
    icon: Triangle,
  },
  blue: {
    bg: 'bg-game-blue',
    hover: 'hover:bg-blue-700',
    shadow: 'shadow-answer-blue',
    icon: Diamond,
  },
  yellow: {
    bg: 'bg-game-yellow',
    hover: 'hover:bg-yellow-600',
    shadow: 'shadow-answer-yellow',
    icon: Circle,
  },
  green: {
    bg: 'bg-game-green',
    hover: 'hover:bg-green-700',
    shadow: 'shadow-answer-green',
    icon: Square,
  },
}

const AnswerButton = forwardRef<HTMLButtonElement, AnswerButtonProps>(
  (
    {
      color,
      text,
      index,
      isSelected,
      isCorrect,
      isWrong,
      isDisabled,
      showResult,
      className,
      ...props
    },
    ref
  ) => {
    const config = colorConfig[color]
    const Icon = config.icon

    // Determine animation state
    let animationState = 'animate'
    if (showResult) {
      if (isCorrect) animationState = 'correct'
      else if (isWrong) animationState = 'incorrect'
      else animationState = 'disabled'
    }

    return (
      <motion.button
        ref={ref}
        className={cn(
          'relative flex items-center justify-center gap-4 p-6 rounded-2xl text-white font-bold text-xl transition-colors',
          config.bg,
          !isDisabled && config.hover,
          config.shadow,
          isSelected && 'ring-4 ring-white ring-offset-2',
          isDisabled && 'opacity-50 cursor-not-allowed',
          className
        )}
        variants={answerButtonVariants}
        initial="initial"
        animate={animationState}
        whileHover={!isDisabled ? 'hover' : undefined}
        whileTap={!isDisabled ? 'tap' : undefined}
        custom={index}
        disabled={isDisabled}
        {...props}
      >
        <Icon className="h-8 w-8 flex-shrink-0" />
        <span className="flex-1 text-center line-clamp-2">{text}</span>
      </motion.button>
    )
  }
)
AnswerButton.displayName = 'AnswerButton'

// Grid of 4 answer buttons (2x2 Kahoot-style)
interface AnswerGridProps {
  options: string[]
  onSelect: (index: number) => void
  selectedIndex?: number
  correctIndex?: number
  showResult?: boolean
  disabled?: boolean
}

const answerColors: AnswerColor[] = ['red', 'blue', 'yellow', 'green']

export function AnswerGrid({
  options,
  onSelect,
  selectedIndex,
  correctIndex,
  showResult,
  disabled,
}: AnswerGridProps) {
  return (
    <div className="grid grid-cols-2 gap-4">
      {options.slice(0, 4).map((option, index) => (
        <AnswerButton
          key={index}
          color={answerColors[index]}
          text={option}
          index={index}
          isSelected={selectedIndex === index}
          isCorrect={showResult && correctIndex === index}
          isWrong={showResult && selectedIndex === index && selectedIndex !== correctIndex}
          isDisabled={disabled || showResult}
          showResult={showResult}
          onClick={() => onSelect(index)}
        />
      ))}
    </div>
  )
}

export { AnswerButton }
