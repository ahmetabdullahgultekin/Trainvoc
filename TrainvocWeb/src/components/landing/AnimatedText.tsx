import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { cn } from '@/lib/utils'

interface AnimatedTextProps {
  text: string
  className?: string
  delay?: number
}

// Typewriter effect
export function TypewriterText({ text, className, delay = 0 }: AnimatedTextProps) {
  const [displayText, setDisplayText] = useState('')
  const [currentIndex, setCurrentIndex] = useState(0)

  useEffect(() => {
    if (currentIndex >= text.length) return

    const timeout = setTimeout(() => {
      setDisplayText(text.slice(0, currentIndex + 1))
      setCurrentIndex(currentIndex + 1)
    }, 50 + delay * 1000)

    return () => clearTimeout(timeout)
  }, [currentIndex, text, delay])

  return (
    <span className={cn('font-mono', className)}>
      {displayText}
      <motion.span
        animate={{ opacity: [1, 0, 1] }}
        transition={{ duration: 0.8, repeat: Infinity }}
        className="inline-block w-0.5 h-6 bg-brand-500 ml-1"
      />
    </span>
  )
}

// Staggered word reveal
interface StaggeredTextProps {
  text: string
  className?: string
  wordClassName?: string
}

export function StaggeredText({ text, className, wordClassName }: StaggeredTextProps) {
  const words = text.split(' ')

  return (
    <span className={className}>
      {words.map((word, index) => (
        <motion.span
          key={index}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: index * 0.1 }}
          className={cn('inline-block mr-2', wordClassName)}
        >
          {word}
        </motion.span>
      ))}
    </span>
  )
}

// Rotating words (like "Learn to [Read/Write/Speak]")
interface RotatingWordsProps {
  words: string[]
  interval?: number
  className?: string
}

export function RotatingWords({ words, interval = 2000, className }: RotatingWordsProps) {
  const [currentIndex, setCurrentIndex] = useState(0)

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % words.length)
    }, interval)

    return () => clearInterval(timer)
  }, [words.length, interval])

  return (
    <span className={cn('relative inline-block min-w-[150px]', className)}>
      <AnimatePresence mode="wait">
        <motion.span
          key={currentIndex}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -20 }}
          transition={{ duration: 0.3 }}
          className="absolute left-0 top-0"
        >
          {words[currentIndex]}
        </motion.span>
      </AnimatePresence>
    </span>
  )
}

// Letter-by-letter animation
interface AnimatedLettersProps {
  text: string
  className?: string
  staggerDelay?: number
}

export function AnimatedLetters({ text, className, staggerDelay = 0.03 }: AnimatedLettersProps) {
  return (
    <span className={className}>
      {text.split('').map((letter, index) => (
        <motion.span
          key={index}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: index * staggerDelay }}
          className="inline-block"
        >
          {letter === ' ' ? '\u00A0' : letter}
        </motion.span>
      ))}
    </span>
  )
}

// Gradient text with animation
interface GradientTextProps {
  text: string
  className?: string
}

export function AnimatedGradientText({ text, className }: GradientTextProps) {
  return (
    <motion.span
      className={cn(
        'inline-block bg-clip-text text-transparent bg-gradient-to-r from-brand-500 via-purple-500 to-brand-500 bg-[length:200%_100%]',
        className
      )}
      animate={{
        backgroundPosition: ['0% 50%', '100% 50%', '0% 50%'],
      }}
      transition={{
        duration: 5,
        repeat: Infinity,
        ease: 'linear',
      }}
    >
      {text}
    </motion.span>
  )
}

export { TypewriterText as AnimatedText }
