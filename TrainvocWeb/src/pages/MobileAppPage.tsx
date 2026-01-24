import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Smartphone, Download, Star, Users, Gamepad2, CheckCircle } from 'lucide-react'
import { Header, Footer } from '@/components/layout'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'

function MobileAppPage() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const content = {
    en: {
      title: 'Mobile App',
      subtitle: 'Learn vocabulary anytime, anywhere',
      description:
        'Continue your vocabulary learning journey anywhere with the Trainvoc mobile app! Our app is available for free on the Play Store for Android devices.',
      downloadAndroid: 'Download on Play Store',
      comingSoon: 'Coming Soon on App Store',
      features: 'App Features',
      rating: '4.8 Rating',
      downloads: '1000+ Downloads',
    },
    tr: {
      title: 'Mobil Uygulama',
      subtitle: 'Her yerde, her zaman kelime öğren',
      description:
        "Trainvoc'un mobil uygulaması ile kelime öğrenimini her yerde sürdürebilirsin! Uygulamamız Android cihazlar için Play Store'da ücretsiz olarak sunulmaktadır.",
      downloadAndroid: "Play Store'da İndir",
      comingSoon: "App Store'da Çok Yakında",
      features: 'Uygulama Özellikleri',
      rating: '4.8 Puan',
      downloads: '1000+ İndirme',
    },
  }

  const t = content[lang]

  const features = [
    {
      icon: Gamepad2,
      title: lang === 'tr' ? '10 Oyun Modu' : '10 Game Modes',
      description: lang === 'tr' ? 'Farklı öğrenme stillerine uygun oyunlar' : 'Games for different learning styles',
    },
    {
      icon: Users,
      title: lang === 'tr' ? 'Çevrimdışı Mod' : 'Offline Mode',
      description: lang === 'tr' ? 'İnternet olmadan da öğren' : 'Learn without internet connection',
    },
    {
      icon: Star,
      title: lang === 'tr' ? 'İlerleme Takibi' : 'Progress Tracking',
      description: lang === 'tr' ? 'Öğrenim geçmişini takip et' : 'Track your learning history',
    },
    {
      icon: CheckCircle,
      title: lang === 'tr' ? 'Günlük Hatırlatıcılar' : 'Daily Reminders',
      description: lang === 'tr' ? 'Düzenli pratik yap' : 'Practice regularly',
    },
  ]

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Header />

      <main className="pt-24 pb-16">
        <div className="mx-auto max-w-5xl px-4 sm:px-6 lg:px-8">
          {/* Hero section */}
          <div className="grid md:grid-cols-2 gap-12 items-center mb-16">
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
            >
              <Badge variant="secondary" className="mb-4">
                {lang === 'tr' ? 'Android için Mevcut' : 'Available for Android'}
              </Badge>
              <h1 className="text-4xl md:text-5xl font-display font-bold text-gray-900 dark:text-white mb-4">
                {t.title}
              </h1>
              <p className="text-xl text-brand-600 dark:text-brand-400 font-medium mb-4">
                {t.subtitle}
              </p>
              <p className="text-gray-600 dark:text-gray-300 mb-6">
                {t.description}
              </p>

              {/* Stats */}
              <div className="flex gap-6 mb-8">
                <div className="flex items-center gap-2">
                  <Star className="h-5 w-5 text-yellow-500 fill-yellow-500" />
                  <span className="font-medium text-gray-900 dark:text-white">
                    {t.rating}
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <Download className="h-5 w-5 text-brand-500" />
                  <span className="font-medium text-gray-900 dark:text-white">
                    {t.downloads}
                  </span>
                </div>
              </div>

              {/* Download buttons */}
              <div className="flex flex-col sm:flex-row gap-4">
                <a
                  href="https://play.google.com/store/apps/details?id=com.rollingcat.trainvoc"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <Button size="lg" className="gap-2 w-full sm:w-auto">
                    <Download className="h-5 w-5" />
                    {t.downloadAndroid}
                  </Button>
                </a>
                <Button
                  variant="outline"
                  size="lg"
                  className="gap-2"
                  disabled
                >
                  <Smartphone className="h-5 w-5" />
                  {t.comingSoon}
                </Button>
              </div>
            </motion.div>

            {/* Phone mockup */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.2 }}
              className="flex justify-center"
            >
              <div className="relative">
                <div className="w-64 h-[500px] bg-gradient-to-br from-brand-500 to-purple-600 rounded-[3rem] p-3 shadow-2xl">
                  <div className="w-full h-full bg-gray-900 rounded-[2.5rem] overflow-hidden flex flex-col items-center justify-center">
                    <div className="text-center p-6">
                      <Gamepad2 className="h-16 w-16 text-brand-400 mx-auto mb-4" />
                      <h3 className="text-xl font-game text-white mb-2">
                        Trainvoc
                      </h3>
                      <p className="text-gray-400 text-sm">
                        {lang === 'tr' ? 'Oynayarak Öğren' : 'Learn by Playing'}
                      </p>
                    </div>
                  </div>
                </div>
                {/* Decorative elements */}
                <div className="absolute -top-4 -right-4 w-24 h-24 bg-brand-200 dark:bg-brand-800 rounded-full blur-2xl opacity-50" />
                <div className="absolute -bottom-4 -left-4 w-32 h-32 bg-purple-200 dark:bg-purple-800 rounded-full blur-2xl opacity-50" />
              </div>
            </motion.div>
          </div>

          {/* Features */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <h2 className="text-2xl font-display font-bold text-gray-900 dark:text-white mb-8 text-center">
              {t.features}
            </h2>
            <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {features.map((feature, index) => {
                const Icon = feature.icon
                return (
                  <motion.div
                    key={index}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.4 + index * 0.1 }}
                  >
                    <Card className="p-6 text-center hover:shadow-lg transition-shadow h-full">
                      <div className="w-12 h-12 mx-auto mb-4 rounded-full bg-brand-100 dark:bg-brand-900/30 flex items-center justify-center">
                        <Icon className="h-6 w-6 text-brand-600 dark:text-brand-400" />
                      </div>
                      <h3 className="font-semibold text-gray-900 dark:text-white mb-2">
                        {feature.title}
                      </h3>
                      <p className="text-sm text-gray-500 dark:text-gray-400">
                        {feature.description}
                      </p>
                    </Card>
                  </motion.div>
                )
              })}
            </div>
          </motion.div>
        </div>
      </main>

      <Footer />
    </div>
  )
}

export default MobileAppPage
