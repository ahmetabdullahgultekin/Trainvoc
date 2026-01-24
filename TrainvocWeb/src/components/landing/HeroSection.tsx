import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Play, Smartphone, ArrowRight } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { FloatingCard } from './FloatingCard'

export function HeroSection() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const content = {
    en: {
      headline: 'Learn Vocabulary',
      highlight: 'Through Play',
      subheadline: 'Master English-Turkish vocabulary with fun multiplayer games. Compete with friends, track your progress, and have fun learning!',
      playNow: 'Play Now',
      getApp: 'Get the App',
    },
    tr: {
      headline: 'Kelime Öğren',
      highlight: 'Oynayarak',
      subheadline: 'Eğlenceli çok oyunculu oyunlarla İngilizce-Türkçe kelime haznenizi geliştirin. Arkadaşlarınızla yarışın, ilerlemenizi takip edin ve öğrenirken eğlenin!',
      playNow: 'Hemen Oyna',
      getApp: 'Uygulamayı İndir',
    },
  }

  const t = content[lang]

  return (
    <section className="relative min-h-screen flex items-center justify-center overflow-hidden pt-16">
      {/* Background gradient */}
      <div className="absolute inset-0 bg-gradient-hero" />
      <div className="absolute inset-0 bg-mesh-pattern opacity-50" />

      {/* Animated background circles */}
      <motion.div
        className="absolute top-20 left-10 w-72 h-72 bg-brand-500/20 rounded-full blur-3xl"
        animate={{
          x: [0, 50, 0],
          y: [0, 30, 0],
        }}
        transition={{ duration: 8, repeat: Infinity, ease: 'easeInOut' }}
      />
      <motion.div
        className="absolute bottom-20 right-10 w-96 h-96 bg-purple-500/20 rounded-full blur-3xl"
        animate={{
          x: [0, -30, 0],
          y: [0, -50, 0],
        }}
        transition={{ duration: 10, repeat: Infinity, ease: 'easeInOut' }}
      />

      <div className="relative z-10 mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-20">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          {/* Left side - Text content */}
          <motion.div
            initial={{ opacity: 0, x: -50 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.8 }}
            className="text-center lg:text-left"
          >
            <h1 className="text-5xl md:text-6xl lg:text-7xl font-display font-bold text-gray-900 dark:text-white mb-4">
              {t.headline}
              <br />
              <span className="gradient-text">{t.highlight}</span>
            </h1>

            <motion.p
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8, delay: 0.3 }}
              className="text-lg md:text-xl text-gray-600 dark:text-gray-300 mb-8 max-w-xl mx-auto lg:mx-0"
            >
              {t.subheadline}
            </motion.p>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8, delay: 0.5 }}
              className="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start"
            >
              <Link to="/play">
                <Button size="xl" className="gap-2 group">
                  <Play className="h-5 w-5" />
                  {t.playNow}
                  <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
                </Button>
              </Link>
              <Link to="/mobile">
                <Button variant="outline" size="xl" className="gap-2">
                  <Smartphone className="h-5 w-5" />
                  {t.getApp}
                </Button>
              </Link>
            </motion.div>

            {/* Stats */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8, delay: 0.7 }}
              className="mt-12 flex justify-center lg:justify-start gap-8"
            >
              <div className="text-center">
                <div className="text-3xl font-bold text-brand-600 dark:text-brand-400">10+</div>
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  {lang === 'tr' ? 'Oyun Modu' : 'Game Modes'}
                </div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-brand-600 dark:text-brand-400">5000+</div>
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  {lang === 'tr' ? 'Kelime' : 'Words'}
                </div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-brand-600 dark:text-brand-400">1000+</div>
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  {lang === 'tr' ? 'Oyuncu' : 'Players'}
                </div>
              </div>
            </motion.div>
          </motion.div>

          {/* Right side - Floating cards */}
          <motion.div
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.8, delay: 0.3 }}
            className="relative h-[500px] hidden lg:block"
          >
            <FloatingCard
              word="Hello"
              translation="Merhaba"
              className="absolute top-0 left-1/2 -translate-x-1/2"
              delay={0}
            />
            <FloatingCard
              word="Learn"
              translation="Öğren"
              className="absolute top-32 left-0"
              delay={0.5}
            />
            <FloatingCard
              word="Play"
              translation="Oyna"
              className="absolute top-32 right-0"
              delay={1}
            />
            <FloatingCard
              word="Win"
              translation="Kazan"
              className="absolute bottom-20 left-1/2 -translate-x-1/2"
              delay={1.5}
            />
          </motion.div>
        </div>
      </div>

      {/* Scroll indicator */}
      <motion.div
        className="absolute bottom-8 left-1/2 -translate-x-1/2"
        animate={{ y: [0, 10, 0] }}
        transition={{ duration: 2, repeat: Infinity }}
      >
        <div className="w-6 h-10 rounded-full border-2 border-gray-300 dark:border-gray-600 p-1">
          <motion.div
            className="w-2 h-2 bg-brand-500 rounded-full mx-auto"
            animate={{ y: [0, 16, 0] }}
            transition={{ duration: 2, repeat: Infinity }}
          />
        </div>
      </motion.div>
    </section>
  )
}
