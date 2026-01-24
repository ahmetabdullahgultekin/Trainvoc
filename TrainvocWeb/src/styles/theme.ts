/**
 * Design tokens and theme configuration for Trainvoc
 * These values mirror the Tailwind config for use in JS/TS
 */

export const colors = {
  // Kahoot-inspired game answer colors
  game: {
    red: '#E21B3C',
    blue: '#1368CE',
    yellow: '#D89E00',
    green: '#26890C',
  },

  // Brand colors (Indigo/Purple)
  brand: {
    50: '#EEF2FF',
    100: '#E0E7FF',
    200: '#C7D2FE',
    300: '#A5B4FC',
    400: '#818CF8',
    500: '#6366F1',
    600: '#4F46E5',
    700: '#4338CA',
    800: '#3730A3',
    900: '#312E81',
    950: '#1E1B4B',
  },

  // Semantic colors
  success: {
    50: '#F0FDF4',
    100: '#DCFCE7',
    500: '#22C55E',
    600: '#16A34A',
  },
  warning: {
    50: '#FFFBEB',
    100: '#FEF3C7',
    500: '#F59E0B',
    600: '#D97706',
  },
  error: {
    50: '#FEF2F2',
    100: '#FEE2E2',
    500: '#EF4444',
    600: '#DC2626',
  },

  // Neutral colors
  neutral: {
    50: '#F8FAFC',
    100: '#F1F5F9',
    200: '#E2E8F0',
    300: '#CBD5E1',
    400: '#94A3B8',
    500: '#64748B',
    600: '#475569',
    700: '#334155',
    800: '#1E293B',
    900: '#0F172A',
    950: '#020617',
  },

  // Light mode
  light: {
    background: '#FFFFFF',
    surface: '#F8FAFC',
    card: '#FFFFFF',
    border: '#E2E8F0',
    text: {
      primary: '#0F172A',
      secondary: '#475569',
      muted: '#94A3B8',
    },
  },

  // Dark mode
  dark: {
    background: '#0F0F23',
    surface: '#1A1A2E',
    card: '#16213E',
    border: '#334155',
    text: {
      primary: '#F8FAFC',
      secondary: '#CBD5E1',
      muted: '#64748B',
    },
  },
} as const

// Answer button color configuration
export const answerColors = [
  { name: 'red', bg: colors.game.red, icon: 'triangle' },
  { name: 'blue', bg: colors.game.blue, icon: 'diamond' },
  { name: 'yellow', bg: colors.game.yellow, icon: 'circle' },
  { name: 'green', bg: colors.game.green, icon: 'square' },
] as const

// Game difficulty configuration
export const difficultyConfig = {
  easy: {
    color: colors.success[500],
    bgColor: colors.success[50],
    label: { en: 'Easy', tr: 'Kolay' },
    points: 10,
  },
  medium: {
    color: colors.warning[500],
    bgColor: colors.warning[50],
    label: { en: 'Medium', tr: 'Orta' },
    points: 20,
  },
  hard: {
    color: colors.error[500],
    bgColor: colors.error[50],
    label: { en: 'Hard', tr: 'Zor' },
    points: 30,
  },
} as const

// Game category configuration
export const categoryConfig = {
  vocabulary: {
    icon: 'BookOpen',
    color: colors.brand[500],
    label: { en: 'Vocabulary', tr: 'Kelime' },
  },
  memory: {
    icon: 'Brain',
    color: colors.game.blue,
    label: { en: 'Memory', tr: 'Hafıza' },
  },
  speed: {
    icon: 'Zap',
    color: colors.game.yellow,
    label: { en: 'Speed', tr: 'Hız' },
  },
  listening: {
    icon: 'Headphones',
    color: colors.game.green,
    label: { en: 'Listening', tr: 'Dinleme' },
  },
} as const

// Game type configuration
export const gameTypes = {
  multipleChoice: {
    id: 'multiple-choice',
    name: { en: 'Multiple Choice', tr: 'Çoktan Seçmeli' },
    description: {
      en: 'Select the correct translation from 4 options',
      tr: '4 seçenek arasından doğru çeviriyi seçin'
    },
    difficulty: 'easy',
    category: 'vocabulary',
    icon: 'ListChecks',
    timeLimit: 15,
  },
  flipCards: {
    id: 'flip-cards',
    name: { en: 'Flip Cards', tr: 'Kart Eşleştirme' },
    description: {
      en: 'Match word pairs in a memory game',
      tr: 'Hafıza oyununda kelime çiftlerini eşleştirin'
    },
    difficulty: 'easy',
    category: 'memory',
    icon: 'Layers',
    timeLimit: 120,
  },
  speedMatch: {
    id: 'speed-match',
    name: { en: 'Speed Match', tr: 'Hız Eşleştirme' },
    description: {
      en: '60 seconds to match as many words as possible',
      tr: '60 saniyede mümkün olduğunca çok kelime eşleştirin'
    },
    difficulty: 'hard',
    category: 'speed',
    icon: 'Timer',
    timeLimit: 60,
  },
  fillInTheBlank: {
    id: 'fill-in-blank',
    name: { en: 'Fill in the Blank', tr: 'Boşluk Doldurma' },
    description: {
      en: 'Complete sentences with missing words',
      tr: 'Eksik kelimeleri tamamlayın'
    },
    difficulty: 'medium',
    category: 'vocabulary',
    icon: 'TextCursor',
    timeLimit: 20,
  },
  wordScramble: {
    id: 'word-scramble',
    name: { en: 'Word Scramble', tr: 'Kelime Bulmaca' },
    description: {
      en: 'Unscramble the letters to form words',
      tr: 'Harfleri düzenleyerek kelime oluşturun'
    },
    difficulty: 'medium',
    category: 'vocabulary',
    icon: 'Shuffle',
    timeLimit: 30,
  },
  listeningQuiz: {
    id: 'listening-quiz',
    name: { en: 'Listening Quiz', tr: 'Dinleme Testi' },
    description: {
      en: 'Listen to the word and select the meaning',
      tr: 'Kelimeyi dinleyin ve anlamını seçin'
    },
    difficulty: 'medium',
    category: 'listening',
    icon: 'Headphones',
    timeLimit: 20,
  },
  pictureMatch: {
    id: 'picture-match',
    name: { en: 'Picture Match', tr: 'Resim Eşleştirme' },
    description: {
      en: 'Match words with their visual representations',
      tr: 'Kelimeleri görsel temsillerle eşleştirin'
    },
    difficulty: 'easy',
    category: 'memory',
    icon: 'Image',
    timeLimit: 15,
  },
  spellingChallenge: {
    id: 'spelling-challenge',
    name: { en: 'Spelling Challenge', tr: 'Yazım Yarışması' },
    description: {
      en: 'Type the correct spelling of the word',
      tr: 'Kelimenin doğru yazımını yazın'
    },
    difficulty: 'hard',
    category: 'vocabulary',
    icon: 'Keyboard',
    timeLimit: 25,
  },
  translationRace: {
    id: 'translation-race',
    name: { en: 'Translation Race', tr: 'Çeviri Yarışı' },
    description: {
      en: '90 seconds of fast-paced translation challenges',
      tr: '90 saniyelik hızlı çeviri mücadelesi'
    },
    difficulty: 'hard',
    category: 'speed',
    icon: 'Rocket',
    timeLimit: 90,
  },
  contextClues: {
    id: 'context-clues',
    name: { en: 'Context Clues', tr: 'Bağlam İpuçları' },
    description: {
      en: 'Guess words from progressive hints',
      tr: 'İlerleyen ipuçlarından kelimeleri tahmin edin'
    },
    difficulty: 'medium',
    category: 'vocabulary',
    icon: 'Lightbulb',
    timeLimit: 45,
  },
} as const

// Typography
export const typography = {
  fontFamily: {
    display: 'Poppins, system-ui, sans-serif',
    body: 'Inter, system-ui, sans-serif',
    game: 'Fredoka One, Poppins, sans-serif',
    mono: 'JetBrains Mono, Consolas, monospace',
  },
  fontSize: {
    xs: '0.75rem',     // 12px
    sm: '0.875rem',    // 14px
    base: '1rem',      // 16px
    lg: '1.125rem',    // 18px
    xl: '1.25rem',     // 20px
    '2xl': '1.5rem',   // 24px
    '3xl': '1.875rem', // 30px
    '4xl': '2.25rem',  // 36px
    '5xl': '3rem',     // 48px
    '6xl': '3.75rem',  // 60px
    '7xl': '4.5rem',   // 72px
  },
} as const

// Spacing
export const spacing = {
  px: '1px',
  0: '0',
  0.5: '0.125rem',
  1: '0.25rem',
  1.5: '0.375rem',
  2: '0.5rem',
  2.5: '0.625rem',
  3: '0.75rem',
  3.5: '0.875rem',
  4: '1rem',
  5: '1.25rem',
  6: '1.5rem',
  7: '1.75rem',
  8: '2rem',
  9: '2.25rem',
  10: '2.5rem',
  11: '2.75rem',
  12: '3rem',
  14: '3.5rem',
  16: '4rem',
  20: '5rem',
  24: '6rem',
  28: '7rem',
  32: '8rem',
  36: '9rem',
  40: '10rem',
  44: '11rem',
  48: '12rem',
  52: '13rem',
  56: '14rem',
  60: '15rem',
  64: '16rem',
  72: '18rem',
  80: '20rem',
  96: '24rem',
} as const

// Border radius
export const borderRadius = {
  none: '0',
  sm: '0.125rem',
  DEFAULT: '0.25rem',
  md: '0.375rem',
  lg: '0.5rem',
  xl: '0.75rem',
  '2xl': '1rem',
  '3xl': '1.5rem',
  '4xl': '2rem',
  full: '9999px',
} as const

// Shadows
export const shadows = {
  sm: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
  DEFAULT: '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
  md: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
  lg: '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
  xl: '0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)',
  '2xl': '0 25px 50px -12px rgb(0 0 0 / 0.25)',
  game: '0 8px 32px -8px rgba(99, 102, 241, 0.4)',
  glass: '0 8px 32px rgba(0, 0, 0, 0.1)',
} as const

// Z-index scale
export const zIndex = {
  behind: -1,
  base: 0,
  dropdown: 1000,
  sticky: 1100,
  fixed: 1200,
  modal: 1300,
  popover: 1400,
  tooltip: 1500,
} as const

// Breakpoints
export const breakpoints = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1536px',
} as const

// Export default theme object
export const theme = {
  colors,
  typography,
  spacing,
  borderRadius,
  shadows,
  zIndex,
  breakpoints,
  answerColors,
  difficultyConfig,
  categoryConfig,
  gameTypes,
} as const

export default theme
