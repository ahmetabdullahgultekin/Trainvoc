import { motion } from 'framer-motion'
import { Volume2, VolumeX } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { cn } from '@/lib/utils'
import { scaleVariants } from '@/lib/animations'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

interface QuestionCardProps {
  question: string
  questionType?: 'word' | 'sentence' | 'listening'
  pronunciation?: string
  imageUrl?: string
  onPlayAudio?: () => void
  audioPlaying?: boolean
  className?: string
}

export function QuestionCard({
  question,
  questionType = 'word',
  pronunciation,
  imageUrl,
  onPlayAudio,
  audioPlaying,
  className,
}: QuestionCardProps) {
  const { t } = useTranslation()

  return (
    <motion.div
      variants={scaleVariants}
      initial="initial"
      animate="animate"
      className={cn('w-full', className)}
    >
      <Card className="p-8 text-center bg-gradient-to-br from-brand-500 to-brand-600">
        {/* Image if provided */}
        {imageUrl && (
          <div className="mb-4">
            <img
              src={imageUrl}
              alt="Question visual"
              className="w-32 h-32 object-cover rounded-xl mx-auto"
            />
          </div>
        )}

        {/* Main question */}
        <h2
          className={cn(
            'font-bold text-white',
            questionType === 'word' ? 'text-4xl md:text-5xl font-game' : 'text-2xl md:text-3xl'
          )}
        >
          {question}
        </h2>

        {/* Pronunciation */}
        {pronunciation && (
          <p className="mt-2 text-brand-200 text-lg">/{pronunciation}/</p>
        )}

        {/* Audio button for listening quiz */}
        {questionType === 'listening' && onPlayAudio && (
          <Button
            variant="secondary"
            size="lg"
            onClick={onPlayAudio}
            className="mt-4"
          >
            {audioPlaying ? (
              <>
                <VolumeX className="mr-2 h-5 w-5" />
                {t('game.stopAudio', 'Stop')}
              </>
            ) : (
              <>
                <Volume2 className="mr-2 h-5 w-5" />
                {t('game.playAudio', 'Play Audio')}
              </>
            )}
          </Button>
        )}
      </Card>
    </motion.div>
  )
}

// Question with blank for fill-in-the-blank games
interface BlankQuestionProps {
  sentence: string
  blankWord: string
  showAnswer?: boolean
  className?: string
}

export function BlankQuestion({
  sentence,
  blankWord,
  showAnswer,
  className,
}: BlankQuestionProps) {
  // Split sentence at the blank position (marked with ___)
  const parts = sentence.split('___')

  return (
    <Card className={cn('p-8', className)}>
      <p className="text-xl md:text-2xl text-gray-900 dark:text-white text-center leading-relaxed">
        {parts[0]}
        <span
          className={cn(
            'inline-block min-w-[100px] mx-2 px-4 py-1 rounded-lg font-bold',
            showAnswer
              ? 'bg-success-100 text-success-600 dark:bg-success-900/30 dark:text-success-400'
              : 'bg-brand-100 dark:bg-brand-900/30 text-brand-600 dark:text-brand-400 border-2 border-dashed border-brand-300 dark:border-brand-700'
          )}
        >
          {showAnswer ? blankWord : '?????'}
        </span>
        {parts[1]}
      </p>
    </Card>
  )
}

// Scrambled word display
interface ScrambledWordProps {
  letters: string[]
  selectedIndices: number[]
  onSelectLetter: (index: number) => void
  className?: string
}

export function ScrambledWord({
  letters,
  selectedIndices,
  onSelectLetter,
  className,
}: ScrambledWordProps) {
  return (
    <div className={cn('flex flex-wrap justify-center gap-2', className)}>
      {letters.map((letter, index) => {
        const isSelected = selectedIndices.includes(index)

        return (
          <motion.button
            key={index}
            onClick={() => !isSelected && onSelectLetter(index)}
            disabled={isSelected}
            className={cn(
              'w-12 h-12 rounded-xl font-bold text-xl flex items-center justify-center transition-all',
              isSelected
                ? 'bg-gray-200 dark:bg-gray-700 text-gray-400 cursor-not-allowed'
                : 'bg-brand-500 text-white hover:bg-brand-600 cursor-pointer shadow-md hover:shadow-lg'
            )}
            whileHover={!isSelected ? { scale: 1.1 } : undefined}
            whileTap={!isSelected ? { scale: 0.95 } : undefined}
          >
            {letter.toUpperCase()}
          </motion.button>
        )
      })}
    </div>
  )
}
