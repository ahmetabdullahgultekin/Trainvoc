import type { Variants, Transition } from 'framer-motion'

// Common transitions
export const springTransition: Transition = {
  type: 'spring',
  stiffness: 400,
  damping: 30,
}

export const smoothTransition: Transition = {
  type: 'tween',
  ease: [0.4, 0, 0.2, 1],
  duration: 0.3,
}

export const bounceTransition: Transition = {
  type: 'spring',
  stiffness: 300,
  damping: 20,
}

// Page transition variants
export const pageVariants: Variants = {
  initial: {
    opacity: 0,
    y: 20,
  },
  animate: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.4,
      ease: [0.4, 0, 0.2, 1],
    },
  },
  exit: {
    opacity: 0,
    y: -20,
    transition: {
      duration: 0.3,
    },
  },
}

// Fade variants
export const fadeVariants: Variants = {
  initial: { opacity: 0 },
  animate: { opacity: 1, transition: { duration: 0.3 } },
  exit: { opacity: 0, transition: { duration: 0.2 } },
}

// Slide variants
export const slideUpVariants: Variants = {
  initial: { opacity: 0, y: 40 },
  animate: { opacity: 1, y: 0, transition: smoothTransition },
  exit: { opacity: 0, y: -20, transition: { duration: 0.2 } },
}

export const slideDownVariants: Variants = {
  initial: { opacity: 0, y: -40 },
  animate: { opacity: 1, y: 0, transition: smoothTransition },
  exit: { opacity: 0, y: 20, transition: { duration: 0.2 } },
}

export const slideInRightVariants: Variants = {
  initial: { opacity: 0, x: 40 },
  animate: { opacity: 1, x: 0, transition: smoothTransition },
  exit: { opacity: 0, x: -40, transition: { duration: 0.2 } },
}

export const slideInLeftVariants: Variants = {
  initial: { opacity: 0, x: -40 },
  animate: { opacity: 1, x: 0, transition: smoothTransition },
  exit: { opacity: 0, x: 40, transition: { duration: 0.2 } },
}

// Scale variants
export const scaleVariants: Variants = {
  initial: { opacity: 0, scale: 0.9 },
  animate: { opacity: 1, scale: 1, transition: springTransition },
  exit: { opacity: 0, scale: 0.9, transition: { duration: 0.2 } },
}

export const popVariants: Variants = {
  initial: { opacity: 0, scale: 0.5 },
  animate: {
    opacity: 1,
    scale: 1,
    transition: {
      type: 'spring',
      stiffness: 500,
      damping: 25,
    }
  },
  exit: { opacity: 0, scale: 0.5, transition: { duration: 0.15 } },
}

// Stagger container variants
export const staggerContainerVariants: Variants = {
  initial: {},
  animate: {
    transition: {
      staggerChildren: 0.1,
      delayChildren: 0.1,
    },
  },
}

export const staggerItemVariants: Variants = {
  initial: { opacity: 0, y: 20 },
  animate: {
    opacity: 1,
    y: 0,
    transition: smoothTransition,
  },
}

// Card hover variants
export const cardHoverVariants: Variants = {
  rest: { scale: 1, y: 0 },
  hover: {
    scale: 1.02,
    y: -4,
    transition: smoothTransition,
  },
  tap: { scale: 0.98 },
}

// Button variants
export const buttonVariants: Variants = {
  rest: { scale: 1 },
  hover: { scale: 1.05 },
  tap: { scale: 0.95 },
}

// Answer button variants (Kahoot-style)
export const answerButtonVariants: Variants = {
  initial: { opacity: 0, y: 20 },
  animate: {
    opacity: 1,
    y: 0,
    transition: bounceTransition,
  },
  hover: {
    scale: 1.03,
    transition: { duration: 0.2 },
  },
  tap: { scale: 0.97 },
  correct: {
    scale: [1, 1.1, 1],
    backgroundColor: '#22C55E',
    transition: { duration: 0.5 },
  },
  incorrect: {
    x: [-4, 4, -4, 4, 0],
    backgroundColor: '#EF4444',
    transition: { duration: 0.4 },
  },
  disabled: {
    opacity: 0.5,
    scale: 0.98,
  },
}

// Countdown variants
export const countdownVariants: Variants = {
  initial: { scale: 0, opacity: 0 },
  animate: {
    scale: 1,
    opacity: 1,
    transition: {
      type: 'spring',
      stiffness: 400,
      damping: 20,
    },
  },
  exit: {
    scale: 2,
    opacity: 0,
    transition: { duration: 0.3 },
  },
}

// Score popup variants
export const scorePopupVariants: Variants = {
  initial: { opacity: 0, y: 0, scale: 0.5 },
  animate: {
    opacity: [0, 1, 1, 0],
    y: -60,
    scale: [0.5, 1.2, 1],
    transition: { duration: 1.5 },
  },
}

// Leaderboard position change variants
export const leaderboardItemVariants: Variants = {
  initial: { opacity: 0, x: -20 },
  animate: {
    opacity: 1,
    x: 0,
    transition: smoothTransition,
  },
  exit: { opacity: 0, x: 20 },
}

// Flip card variants
export const flipCardVariants: Variants = {
  front: {
    rotateY: 0,
    transition: { duration: 0.4 },
  },
  back: {
    rotateY: 180,
    transition: { duration: 0.4 },
  },
}

// Timer variants (for low time warning)
export const timerWarningVariants: Variants = {
  normal: { scale: 1, color: '#64748B' },
  warning: {
    scale: [1, 1.1, 1],
    color: '#F59E0B',
    transition: {
      scale: { repeat: Infinity, duration: 0.5 },
    },
  },
  critical: {
    scale: [1, 1.15, 1],
    color: '#EF4444',
    transition: {
      scale: { repeat: Infinity, duration: 0.3 },
    },
  },
}

// Modal/Dialog variants
export const modalOverlayVariants: Variants = {
  initial: { opacity: 0 },
  animate: { opacity: 1, transition: { duration: 0.2 } },
  exit: { opacity: 0, transition: { duration: 0.2 } },
}

export const modalContentVariants: Variants = {
  initial: { opacity: 0, scale: 0.95, y: 20 },
  animate: {
    opacity: 1,
    scale: 1,
    y: 0,
    transition: springTransition,
  },
  exit: {
    opacity: 0,
    scale: 0.95,
    y: 20,
    transition: { duration: 0.2 },
  },
}

// Podium animation variants
export const podiumVariants: Variants = {
  initial: { height: 0 },
  animate: (custom: number) => ({
    height: custom,
    transition: {
      type: 'spring',
      stiffness: 100,
      damping: 15,
      delay: custom === 150 ? 0.5 : custom === 200 ? 0 : 0.3,
    },
  }),
}

// Confetti particle variants
export const confettiVariants: Variants = {
  initial: {
    y: 0,
    opacity: 1,
    rotate: 0,
  },
  animate: (custom: { duration: number; rotation: number; x: number }) => ({
    y: -500,
    opacity: [1, 1, 0],
    rotate: custom.rotation,
    x: custom.x,
    transition: {
      duration: custom.duration,
      ease: 'easeOut',
    },
  }),
}

// Combo indicator variants
export const comboVariants: Variants = {
  initial: { scale: 0, opacity: 0 },
  animate: {
    scale: [0, 1.3, 1],
    opacity: 1,
    transition: {
      type: 'spring',
      stiffness: 500,
      damping: 20,
    },
  },
  exit: {
    scale: 0,
    opacity: 0,
    transition: { duration: 0.2 },
  },
}

// Floating animation (for hero section)
export const floatingVariants: Variants = {
  animate: {
    y: [0, -15, 0],
    transition: {
      duration: 4,
      repeat: Infinity,
      ease: 'easeInOut',
    },
  },
}

export const floatingDelayedVariants: Variants = {
  animate: {
    y: [0, -15, 0],
    transition: {
      duration: 4,
      repeat: Infinity,
      ease: 'easeInOut',
      delay: 1,
    },
  },
}

// Typewriter effect helper
export const typewriterVariants: Variants = {
  hidden: { opacity: 0 },
  visible: (custom: number) => ({
    opacity: 1,
    transition: { delay: custom * 0.05 },
  }),
}

// Pulse animation
export const pulseVariants: Variants = {
  animate: {
    scale: [1, 1.05, 1],
    opacity: [1, 0.8, 1],
    transition: {
      duration: 2,
      repeat: Infinity,
      ease: 'easeInOut',
    },
  },
}

// Glow animation
export const glowVariants: Variants = {
  animate: {
    boxShadow: [
      '0 0 20px rgba(99, 102, 241, 0.4)',
      '0 0 40px rgba(99, 102, 241, 0.6)',
      '0 0 20px rgba(99, 102, 241, 0.4)',
    ],
    transition: {
      duration: 2,
      repeat: Infinity,
      ease: 'easeInOut',
    },
  },
}
