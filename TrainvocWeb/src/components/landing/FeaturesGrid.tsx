import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import {
  Users, Trophy, Zap, Brain, Globe, Smartphone,
  BarChart3, Gamepad2
} from 'lucide-react'
import { cn } from '@/lib/utils'
import { staggerContainerVariants, staggerItemVariants } from '@/lib/animations'

interface Feature {
  icon: typeof Users
  title: { en: string; tr: string }
  description: { en: string; tr: string }
  color: string
  size?: 'sm' | 'md' | 'lg'
}

const features: Feature[] = [
  {
    icon: Gamepad2,
    title: { en: '10 Game Modes', tr: '10 Oyun Modu' },
    description: {
      en: 'From multiple choice to speed matching, find your favorite way to learn',
      tr: 'Çoktan seçmeliden hız eşleştirmeye, en sevdiğiniz öğrenme yöntemini bulun'
    },
    color: 'from-brand-500 to-purple-500',
    size: 'lg',
  },
  {
    icon: Users,
    title: { en: 'Multiplayer', tr: 'Çok Oyunculu' },
    description: {
      en: 'Compete with friends in real-time vocabulary battles',
      tr: 'Arkadaşlarınızla gerçek zamanlı kelime savaşlarında yarışın'
    },
    color: 'from-blue-500 to-cyan-500',
    size: 'md',
  },
  {
    icon: Trophy,
    title: { en: 'Leaderboards', tr: 'Liderlik Tabloları' },
    description: {
      en: 'Climb the ranks and become the vocabulary champion',
      tr: 'Sıralamalarda yükselin ve kelime şampiyonu olun'
    },
    color: 'from-yellow-500 to-orange-500',
    size: 'md',
  },
  {
    icon: Brain,
    title: { en: 'Smart Learning', tr: 'Akıllı Öğrenme' },
    description: {
      en: 'Adaptive difficulty that grows with you',
      tr: 'Sizinle birlikte büyüyen adaptif zorluk'
    },
    color: 'from-pink-500 to-rose-500',
  },
  {
    icon: Zap,
    title: { en: 'Fast & Fun', tr: 'Hızlı ve Eğlenceli' },
    description: {
      en: 'Quick sessions that fit your schedule',
      tr: 'Programınıza uyan hızlı oturumlar'
    },
    color: 'from-green-500 to-emerald-500',
  },
  {
    icon: Globe,
    title: { en: 'Bilingual', tr: 'İki Dilli' },
    description: {
      en: 'Full English and Turkish support',
      tr: 'Tam İngilizce ve Türkçe desteği'
    },
    color: 'from-violet-500 to-purple-500',
  },
  {
    icon: BarChart3,
    title: { en: 'Progress Tracking', tr: 'İlerleme Takibi' },
    description: {
      en: 'See your improvement over time',
      tr: 'Zaman içindeki gelişiminizi görün'
    },
    color: 'from-teal-500 to-cyan-500',
  },
  {
    icon: Smartphone,
    title: { en: 'Cross-Platform', tr: 'Çoklu Platform' },
    description: {
      en: 'Play on web or download the mobile app',
      tr: 'Web\'de oynayın veya mobil uygulamayı indirin'
    },
    color: 'from-orange-500 to-red-500',
  },
]

export function FeaturesGrid() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  return (
    <section className="py-24 bg-gray-50 dark:bg-gray-900/50">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        {/* Section header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="text-center mb-16"
        >
          <h2 className="text-3xl md:text-4xl font-display font-bold text-gray-900 dark:text-white mb-4">
            {lang === 'tr' ? 'Neden Trainvoc?' : 'Why Trainvoc?'}
          </h2>
          <p className="text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
            {lang === 'tr'
              ? 'Kelime öğrenmeyi eğlenceli ve etkili hale getiren özelliklerle dolu'
              : 'Packed with features that make vocabulary learning fun and effective'}
          </p>
        </motion.div>

        {/* Bento grid */}
        <motion.div
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4"
          variants={staggerContainerVariants}
          initial="initial"
          whileInView="animate"
          viewport={{ once: true }}
        >
          {features.map((feature) => {
            const Icon = feature.icon
            const isLarge = feature.size === 'lg'
            const isMedium = feature.size === 'md'

            return (
              <motion.div
                key={feature.title.en}
                variants={staggerItemVariants}
                className={cn(
                  'group relative overflow-hidden rounded-2xl bg-white dark:bg-gray-800 p-6 shadow-card hover:shadow-card-hover transition-shadow',
                  isLarge && 'lg:col-span-2 lg:row-span-2',
                  isMedium && 'lg:col-span-2'
                )}
              >
                {/* Gradient background on hover */}
                <div
                  className={cn(
                    'absolute inset-0 bg-gradient-to-br opacity-0 group-hover:opacity-5 transition-opacity',
                    feature.color
                  )}
                />

                {/* Content */}
                <div className={cn('relative z-10', isLarge && 'flex flex-col h-full')}>
                  <div
                    className={cn(
                      'inline-flex items-center justify-center rounded-xl bg-gradient-to-br mb-4',
                      feature.color,
                      isLarge ? 'w-16 h-16' : 'w-12 h-12'
                    )}
                  >
                    <Icon className={cn('text-white', isLarge ? 'h-8 w-8' : 'h-6 w-6')} />
                  </div>

                  <h3 className={cn(
                    'font-semibold text-gray-900 dark:text-white mb-2',
                    isLarge ? 'text-2xl' : 'text-lg'
                  )}>
                    {feature.title[lang]}
                  </h3>

                  <p className={cn(
                    'text-gray-600 dark:text-gray-400',
                    isLarge ? 'text-base flex-1' : 'text-sm'
                  )}>
                    {feature.description[lang]}
                  </p>
                </div>
              </motion.div>
            )
          })}
        </motion.div>
      </div>
    </section>
  )
}
