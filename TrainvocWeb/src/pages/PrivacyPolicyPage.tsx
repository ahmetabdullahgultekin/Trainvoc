import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Shield } from 'lucide-react'
import { Header, Footer } from '@/components/layout'
import { Card } from '@/components/ui/card'

function PrivacyPolicyPage() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const content = {
    en: {
      title: 'Privacy Policy',
      lastUpdated: 'Last Updated: January 2026',
      intro: 'At Trainvoc, we take your privacy seriously. This Privacy Policy explains how we collect, use, and protect your personal information.',
      sections: [
        {
          title: '1. Information We Collect',
          content: `We collect information you provide directly to us, such as:
• Username and display name
• Game progress and scores
• Device information for app functionality
• Usage data to improve our services`
        },
        {
          title: '2. How We Use Your Information',
          content: `We use the information we collect to:
• Provide and maintain our services
• Track your learning progress
• Display leaderboards and rankings
• Improve and personalize your experience
• Send notifications (if enabled)`
        },
        {
          title: '3. Data Storage',
          content: `Your data is stored securely:
• Local data is stored on your device
• Multiplayer data is stored on our servers
• We use industry-standard security measures
• Data is retained only as long as necessary`
        },
        {
          title: '4. Third-Party Services',
          content: `We may use third-party services that collect information:
• Google Play Services (for Android app)
• Analytics services (anonymized usage data)
• These services have their own privacy policies`
        },
        {
          title: '5. Your Rights',
          content: `You have the right to:
• Access your personal data
• Request deletion of your data
• Opt-out of non-essential data collection
• Contact us with privacy concerns`
        },
        {
          title: '6. Contact Us',
          content: 'For privacy-related questions, contact us at: rollingcat.help@gmail.com'
        }
      ]
    },
    tr: {
      title: 'Gizlilik Politikası',
      lastUpdated: 'Son Güncelleme: Ocak 2026',
      intro: 'Trainvoc olarak gizliliğinizi ciddiye alıyoruz. Bu Gizlilik Politikası, kişisel bilgilerinizi nasıl topladığımızı, kullandığımızı ve koruduğumuzu açıklar.',
      sections: [
        {
          title: '1. Topladığımız Bilgiler',
          content: `Doğrudan bize sağladığınız bilgileri topluyoruz:
• Kullanıcı adı ve görünen ad
• Oyun ilerlemesi ve puanlar
• Uygulama işlevselliği için cihaz bilgileri
• Hizmetlerimizi geliştirmek için kullanım verileri`
        },
        {
          title: '2. Bilgilerinizi Nasıl Kullanıyoruz',
          content: `Topladığımız bilgileri şu amaçlarla kullanıyoruz:
• Hizmetlerimizi sağlamak ve sürdürmek
• Öğrenme ilerlemenizi takip etmek
• Liderlik tablolarını ve sıralamaları görüntülemek
• Deneyiminizi iyileştirmek ve kişiselleştirmek
• Bildirim göndermek (etkinleştirilmişse)`
        },
        {
          title: '3. Veri Depolama',
          content: `Verileriniz güvenli bir şekilde saklanır:
• Yerel veriler cihazınızda saklanır
• Çok oyunculu veriler sunucularımızda saklanır
• Endüstri standardı güvenlik önlemleri kullanıyoruz
• Veriler yalnızca gerekli olduğu sürece saklanır`
        },
        {
          title: '4. Üçüncü Taraf Hizmetleri',
          content: `Bilgi toplayan üçüncü taraf hizmetleri kullanabiliriz:
• Google Play Hizmetleri (Android uygulaması için)
• Analitik hizmetleri (anonimleştirilmiş kullanım verileri)
• Bu hizmetlerin kendi gizlilik politikaları vardır`
        },
        {
          title: '5. Haklarınız',
          content: `Şu haklara sahipsiniz:
• Kişisel verilerinize erişim
• Verilerinizin silinmesini talep etme
• Zorunlu olmayan veri toplamayı reddetme
• Gizlilik endişeleriniz için bizimle iletişime geçme`
        },
        {
          title: '6. İletişim',
          content: 'Gizlilikle ilgili sorularınız için bize şu adresten ulaşın: rollingcat.help@gmail.com'
        }
      ]
    }
  }

  const t = content[lang]

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Header />

      <main className="pt-24 pb-16">
        <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-center mb-12"
          >
            <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-brand-100 dark:bg-brand-900/30 flex items-center justify-center">
              <Shield className="h-8 w-8 text-brand-600 dark:text-brand-400" />
            </div>
            <h1 className="text-4xl md:text-5xl font-display font-bold text-gray-900 dark:text-white mb-4">
              {t.title}
            </h1>
            <p className="text-sm text-gray-500 dark:text-gray-400">
              {t.lastUpdated}
            </p>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
          >
            <Card className="p-6 md:p-8 mb-8">
              <p className="text-gray-600 dark:text-gray-300 text-lg leading-relaxed">
                {t.intro}
              </p>
            </Card>

            <div className="space-y-6">
              {t.sections.map((section, index) => (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.2 + index * 0.05 }}
                >
                  <Card className="p-6">
                    <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
                      {section.title}
                    </h2>
                    <p className="text-gray-600 dark:text-gray-300 whitespace-pre-line">
                      {section.content}
                    </p>
                  </Card>
                </motion.div>
              ))}
            </div>
          </motion.div>
        </div>
      </main>

      <Footer />
    </div>
  )
}

export default PrivacyPolicyPage
