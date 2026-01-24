import { motion } from 'framer-motion'
import { Volume2 } from 'lucide-react'
import { cn } from '@/lib/utils'
import { floatingVariants } from '@/lib/animations'

interface FloatingCardProps {
  word: string
  translation: string
  pronunciation?: string
  delay?: number
  className?: string
}

export function FloatingCard({
  word,
  translation,
  pronunciation,
  delay = 0,
  className,
}: FloatingCardProps) {
  return (
    <motion.div
      className={cn(
        'glass-card p-6 w-48 shadow-xl',
        className
      )}
      variants={floatingVariants}
      animate="animate"
      transition={{ delay }}
    >
      <div className="text-center">
        <div className="flex items-center justify-center gap-2 mb-2">
          <h3 className="text-xl font-bold text-gray-900 dark:text-white">
            {word}
          </h3>
          <button className="p-1 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
            <Volume2 className="h-4 w-4 text-brand-500" />
          </button>
        </div>
        {pronunciation && (
          <p className="text-xs text-gray-400 mb-1">/{pronunciation}/</p>
        )}
        <p className="text-gray-600 dark:text-gray-300">{translation}</p>
      </div>
    </motion.div>
  )
}

// Vocabulary preview card with more details
interface VocabCardProps {
  word: string
  translation: string
  pronunciation?: string
  example?: string
  partOfSpeech?: string
  className?: string
}

export function VocabCard({
  word,
  translation,
  pronunciation,
  example,
  partOfSpeech,
  className,
}: VocabCardProps) {
  return (
    <motion.div
      className={cn(
        'bg-white dark:bg-gray-800 rounded-2xl p-6 shadow-card border border-gray-200 dark:border-gray-700',
        className
      )}
      whileHover={{ y: -4 }}
      transition={{ duration: 0.2 }}
    >
      <div className="flex items-start justify-between mb-4">
        <div>
          <div className="flex items-center gap-2">
            <h3 className="text-2xl font-bold text-gray-900 dark:text-white">
              {word}
            </h3>
            <button className="p-1.5 rounded-full bg-brand-100 dark:bg-brand-900/30 hover:bg-brand-200 dark:hover:bg-brand-900/50 transition-colors">
              <Volume2 className="h-4 w-4 text-brand-600 dark:text-brand-400" />
            </button>
          </div>
          {pronunciation && (
            <p className="text-sm text-gray-400 mt-0.5">/{pronunciation}/</p>
          )}
        </div>
        {partOfSpeech && (
          <span className="text-xs font-medium px-2 py-1 bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 rounded-full">
            {partOfSpeech}
          </span>
        )}
      </div>

      <p className="text-lg text-brand-600 dark:text-brand-400 font-medium mb-3">
        {translation}
      </p>

      {example && (
        <div className="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-xl">
          <p className="text-sm text-gray-600 dark:text-gray-400 italic">
            "{example}"
          </p>
        </div>
      )}
    </motion.div>
  )
}
