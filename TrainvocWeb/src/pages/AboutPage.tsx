import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Users, Target, Sparkles, Globe } from 'lucide-react'
import { Header, Footer } from '@/components/layout'
import { Card } from '@/components/ui/card'

function AboutPage() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const content = {
    en: {
      title: 'About Trainvoc',
      subtitle: 'Learn vocabulary through play',
      description1:
        'Trainvoc is a next-generation platform designed to make English-Turkish vocabulary learning fun, social, and sustainable. Our goal is to help users improve their vocabulary skills in both individual and multiplayer modes, turning learning into a gaming experience.',
      description2:
        'On our platform, you can compete with friends, see your place on the leaderboard, and practice anywhere with our mobile app. As the Rolling Cat Software team, we continuously improve our platform with user feedback.',
      cta: 'Follow us for more information and updates!',
      mission: 'Our Mission',
      missionText: 'To make language learning accessible, enjoyable, and effective for everyone through gamification and social interaction.',
      vision: 'Our Vision',
      visionText: 'A world where learning a new language is as fun as playing your favorite game.',
    },
    tr: {
      title: 'Trainvoc Hakkında',
      subtitle: 'Oynayarak kelime öğren',
      description1:
        'Trainvoc, İngilizce-Türkçe kelime öğrenimini eğlenceli, sosyal ve sürdürülebilir hale getirmek için geliştirilmiş yeni nesil bir platformdur. Amacımız, kullanıcıların hem bireysel hem de çok oyunculu modlarda kelime bilgisini geliştirmesini sağlamak ve öğrenmeyi bir oyun deneyimine dönüştürmektir.',
      description2:
        'Platformumuzda arkadaşlarınla yarışabilir, liderlik tablosunda yerini görebilir ve mobil uygulamamız sayesinde her yerde pratik yapabilirsin. Rolling Cat Software ekibi olarak, kullanıcılarımızın geri bildirimleriyle platformumuzu sürekli geliştiriyoruz.',
      cta: 'Daha fazla bilgi ve güncellemeler için bizi takip etmeye devam et!',
      mission: 'Misyonumuz',
      missionText: 'Oyunlaştırma ve sosyal etkileşim yoluyla dil öğrenmeyi herkes için erişilebilir, eğlenceli ve etkili hale getirmek.',
      vision: 'Vizyonumuz',
      visionText: 'Yeni bir dil öğrenmenin, en sevdiğin oyunu oynamak kadar eğlenceli olduğu bir dünya.',
    },
  }

  const t = content[lang]

  const values = [
    {
      icon: Users,
      title: lang === 'tr' ? 'Topluluk' : 'Community',
      description: lang === 'tr' ? 'Öğrenmeyi sosyal bir deneyime dönüştürüyoruz' : 'We transform learning into a social experience',
    },
    {
      icon: Target,
      title: lang === 'tr' ? 'Hedef Odaklı' : 'Goal-Oriented',
      description: lang === 'tr' ? 'İlerlemenizi takip edin ve hedeflerinize ulaşın' : 'Track your progress and achieve your goals',
    },
    {
      icon: Sparkles,
      title: lang === 'tr' ? 'Eğlenceli' : 'Fun',
      description: lang === 'tr' ? 'Oyunlar aracılığıyla öğrenmenin tadını çıkarın' : 'Enjoy learning through games',
    },
    {
      icon: Globe,
      title: lang === 'tr' ? 'Erişilebilir' : 'Accessible',
      description: lang === 'tr' ? 'Her yerden, her cihazdan erişin' : 'Access from anywhere, on any device',
    },
  ]

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Header />

      <main className="pt-24 pb-16">
        <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8">
          {/* Hero section */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-center mb-12"
          >
            <h1 className="text-4xl md:text-5xl font-display font-bold text-gray-900 dark:text-white mb-4">
              {t.title}
            </h1>
            <p className="text-xl text-brand-600 dark:text-brand-400 font-medium">
              {t.subtitle}
            </p>
          </motion.div>

          {/* Description cards */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="space-y-6 mb-12"
          >
            <Card className="p-6 md:p-8">
              <p className="text-gray-600 dark:text-gray-300 text-lg leading-relaxed">
                {t.description1}
              </p>
            </Card>

            <Card className="p-6 md:p-8">
              <p className="text-gray-600 dark:text-gray-300 text-lg leading-relaxed">
                {t.description2}
              </p>
            </Card>
          </motion.div>

          {/* Mission & Vision */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="grid md:grid-cols-2 gap-6 mb-12"
          >
            <Card className="p-6 bg-brand-50 dark:bg-brand-900/20 border-brand-200 dark:border-brand-800">
              <h3 className="text-xl font-semibold text-brand-700 dark:text-brand-300 mb-3">
                {t.mission}
              </h3>
              <p className="text-gray-600 dark:text-gray-300">
                {t.missionText}
              </p>
            </Card>

            <Card className="p-6 bg-purple-50 dark:bg-purple-900/20 border-purple-200 dark:border-purple-800">
              <h3 className="text-xl font-semibold text-purple-700 dark:text-purple-300 mb-3">
                {t.vision}
              </h3>
              <p className="text-gray-600 dark:text-gray-300">
                {t.visionText}
              </p>
            </Card>
          </motion.div>

          {/* Values */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <h2 className="text-2xl font-display font-bold text-gray-900 dark:text-white mb-6 text-center">
              {lang === 'tr' ? 'Değerlerimiz' : 'Our Values'}
            </h2>
            <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {values.map((value, index) => {
                const Icon = value.icon
                return (
                  <motion.div
                    key={index}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.4 + index * 0.1 }}
                  >
                    <Card className="p-6 text-center hover:shadow-lg transition-shadow">
                      <div className="w-12 h-12 mx-auto mb-4 rounded-full bg-brand-100 dark:bg-brand-900/30 flex items-center justify-center">
                        <Icon className="h-6 w-6 text-brand-600 dark:text-brand-400" />
                      </div>
                      <h3 className="font-semibold text-gray-900 dark:text-white mb-2">
                        {value.title}
                      </h3>
                      <p className="text-sm text-gray-500 dark:text-gray-400">
                        {value.description}
                      </p>
                    </Card>
                  </motion.div>
                )
              })}
            </div>
          </motion.div>

          {/* CTA */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.6 }}
            className="mt-12 text-center"
          >
            <p className="text-gray-500 dark:text-gray-400 italic">
              {t.cta}
            </p>
          </motion.div>
        </div>
      </main>

      <Footer />
    </div>
  )
}

export default AboutPage
