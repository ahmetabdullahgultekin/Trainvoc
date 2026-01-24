import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { UserPlus, Gamepad2, Trophy, ArrowRight } from 'lucide-react'
import { cn } from '@/lib/utils'
import { staggerContainerVariants, staggerItemVariants } from '@/lib/animations'

interface Step {
  icon: typeof UserPlus
  title: { en: string; tr: string }
  description: { en: string; tr: string }
  color: string
}

const steps: Step[] = [
  {
    icon: UserPlus,
    title: { en: 'Create or Join', tr: 'Oluştur veya Katıl' },
    description: {
      en: 'Create a room and invite friends, or join an existing game',
      tr: 'Oda oluşturup arkadaşlarınızı davet edin veya mevcut bir oyuna katılın'
    },
    color: 'from-brand-500 to-brand-600',
  },
  {
    icon: Gamepad2,
    title: { en: 'Play & Learn', tr: 'Oyna ve Öğren' },
    description: {
      en: 'Answer vocabulary questions and compete in real-time',
      tr: 'Kelime sorularını yanıtlayın ve gerçek zamanlı yarışın'
    },
    color: 'from-purple-500 to-purple-600',
  },
  {
    icon: Trophy,
    title: { en: 'Win & Improve', tr: 'Kazan ve Gelişir' },
    description: {
      en: 'Track your progress, earn achievements, and master vocabulary',
      tr: 'İlerlemenizi takip edin, başarılar kazanın ve kelime haznenizi geliştirin'
    },
    color: 'from-yellow-500 to-orange-500',
  },
]

export function HowItWorks() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  return (
    <section className="py-24 bg-white dark:bg-gray-900">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        {/* Section header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="text-center mb-16"
        >
          <h2 className="text-3xl md:text-4xl font-display font-bold text-gray-900 dark:text-white mb-4">
            {lang === 'tr' ? 'Nasıl Çalışır?' : 'How It Works'}
          </h2>
          <p className="text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
            {lang === 'tr'
              ? 'Üç basit adımda öğrenmeye başlayın'
              : 'Start learning in three simple steps'}
          </p>
        </motion.div>

        {/* Steps */}
        <motion.div
          className="relative grid grid-cols-1 md:grid-cols-3 gap-8"
          variants={staggerContainerVariants}
          initial="initial"
          whileInView="animate"
          viewport={{ once: true }}
        >
          {/* Connecting line (desktop) */}
          <div className="hidden md:block absolute top-16 left-1/6 right-1/6 h-0.5 bg-gray-200 dark:bg-gray-700" />

          {steps.map((step, index) => {
            const Icon = step.icon

            return (
              <motion.div
                key={index}
                variants={staggerItemVariants}
                className="relative flex flex-col items-center text-center"
              >
                {/* Step number */}
                <div className="relative">
                  <div
                    className={cn(
                      'w-32 h-32 rounded-full bg-gradient-to-br flex items-center justify-center shadow-lg mb-6',
                      step.color
                    )}
                  >
                    <Icon className="h-12 w-12 text-white" />
                  </div>
                  {/* Step number badge */}
                  <div className="absolute -top-2 -right-2 w-10 h-10 rounded-full bg-white dark:bg-gray-800 shadow-lg flex items-center justify-center">
                    <span className="text-lg font-bold text-brand-600 dark:text-brand-400">
                      {index + 1}
                    </span>
                  </div>
                </div>

                {/* Arrow (between steps on desktop) */}
                {index < steps.length - 1 && (
                  <div className="hidden md:flex absolute top-16 right-0 translate-x-1/2 -translate-y-1/2">
                    <ArrowRight className="h-6 w-6 text-gray-300 dark:text-gray-600" />
                  </div>
                )}

                {/* Content */}
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-3">
                  {step.title[lang]}
                </h3>
                <p className="text-gray-600 dark:text-gray-400 max-w-xs">
                  {step.description[lang]}
                </p>
              </motion.div>
            )
          })}
        </motion.div>
      </div>
    </section>
  )
}
