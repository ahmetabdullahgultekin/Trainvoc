import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Play, ArrowRight, Sparkles } from 'lucide-react'
import { Button } from '@/components/ui/button'

export function CTASection() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const content = {
    en: {
      title: 'Ready to Level Up Your Vocabulary?',
      subtitle: 'Join thousands of learners who are mastering English-Turkish vocabulary through fun games.',
      button: 'Start Playing Now',
      free: 'Free to play',
    },
    tr: {
      title: 'Kelime Haznenizi Geliştirmeye Hazır mısınız?',
      subtitle: 'Eğlenceli oyunlarla İngilizce-Türkçe kelime öğrenen binlerce öğrenciye katılın.',
      button: 'Hemen Oynamaya Başla',
      free: 'Ücretsiz oyna',
    },
  }

  const t = content[lang]

  return (
    <section className="py-24 relative overflow-hidden">
      {/* Background */}
      <div className="absolute inset-0 bg-gradient-to-br from-brand-500 via-brand-600 to-purple-600" />
      <div className="absolute inset-0 bg-mesh-pattern opacity-20" />

      {/* Animated circles */}
      <motion.div
        className="absolute top-0 left-0 w-96 h-96 bg-white/10 rounded-full blur-3xl"
        animate={{
          x: [0, 100, 0],
          y: [0, 50, 0],
        }}
        transition={{ duration: 15, repeat: Infinity, ease: 'easeInOut' }}
      />
      <motion.div
        className="absolute bottom-0 right-0 w-80 h-80 bg-purple-300/20 rounded-full blur-3xl"
        animate={{
          x: [0, -50, 0],
          y: [0, -100, 0],
        }}
        transition={{ duration: 12, repeat: Infinity, ease: 'easeInOut' }}
      />

      <div className="relative z-10 mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 text-center">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
        >
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/10 text-white/90 text-sm font-medium mb-8">
            <Sparkles className="h-4 w-4" />
            {t.free}
          </div>

          <h2 className="text-4xl md:text-5xl font-display font-bold text-white mb-6">
            {t.title}
          </h2>

          <p className="text-lg md:text-xl text-white/80 mb-10 max-w-2xl mx-auto">
            {t.subtitle}
          </p>

          <Link to="/play">
            <Button
              size="xl"
              className="bg-white text-brand-600 hover:bg-gray-100 gap-2 group"
            >
              <Play className="h-5 w-5" />
              {t.button}
              <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
            </Button>
          </Link>
        </motion.div>
      </div>
    </section>
  )
}
