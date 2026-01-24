import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { FileText } from 'lucide-react'
import { Header, Footer } from '@/components/layout'
import { Card } from '@/components/ui/card'

function TermsOfServicePage() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'

  const content = {
    en: {
      title: 'Terms of Service',
      lastUpdated: 'Last Updated: January 2026',
      intro: 'Welcome to Trainvoc. By using our services, you agree to these terms. Please read them carefully.',
      sections: [
        {
          title: '1. Acceptance of Terms',
          content: `By accessing or using Trainvoc, you agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use our services.`
        },
        {
          title: '2. Description of Service',
          content: `Trainvoc provides:
• Vocabulary learning games and quizzes
• Multiplayer game rooms
• Progress tracking and statistics
• Mobile and web applications
We reserve the right to modify or discontinue features at any time.`
        },
        {
          title: '3. User Accounts',
          content: `When using Trainvoc:
• You are responsible for maintaining your account security
• You must provide accurate information
• You may not share your account with others
• You must be at least 13 years old to use our services`
        },
        {
          title: '4. Acceptable Use',
          content: `You agree not to:
• Use the service for any illegal purpose
• Harass, abuse, or harm other users
• Attempt to cheat or manipulate game scores
• Interfere with the proper functioning of the service
• Create multiple accounts to gain unfair advantages`
        },
        {
          title: '5. Intellectual Property',
          content: `All content on Trainvoc, including but not limited to text, graphics, logos, and software, is the property of Rolling Cat Software and is protected by intellectual property laws. The application source code is available under the MIT License.`
        },
        {
          title: '6. Limitation of Liability',
          content: `Trainvoc is provided "as is" without warranties of any kind. We are not liable for any indirect, incidental, or consequential damages arising from your use of the service.`
        },
        {
          title: '7. Changes to Terms',
          content: `We may update these terms from time to time. Continued use of the service after changes constitutes acceptance of the new terms.`
        },
        {
          title: '8. Contact',
          content: 'For questions about these terms, contact us at: rollingcat.help@gmail.com'
        }
      ]
    },
    tr: {
      title: 'Kullanım Şartları',
      lastUpdated: 'Son Güncelleme: Ocak 2026',
      intro: 'Trainvoc\'a hoş geldiniz. Hizmetlerimizi kullanarak bu şartları kabul etmiş olursunuz. Lütfen dikkatli okuyunuz.',
      sections: [
        {
          title: '1. Şartların Kabulü',
          content: `Trainvoc\'a erişerek veya kullanarak bu Kullanım Şartlarına bağlı olmayı kabul edersiniz. Bu şartları kabul etmiyorsanız, lütfen hizmetlerimizi kullanmayınız.`
        },
        {
          title: '2. Hizmet Tanımı',
          content: `Trainvoc şunları sağlar:
• Kelime öğrenme oyunları ve testleri
• Çok oyunculu oyun odaları
• İlerleme takibi ve istatistikler
• Mobil ve web uygulamaları
Özellikleri herhangi bir zamanda değiştirme veya durdurma hakkını saklı tutarız.`
        },
        {
          title: '3. Kullanıcı Hesapları',
          content: `Trainvoc\'u kullanırken:
• Hesap güvenliğinizi korumaktan siz sorumlusunuz
• Doğru bilgi sağlamalısınız
• Hesabınızı başkalarıyla paylaşamazsınız
• Hizmetlerimizi kullanmak için en az 13 yaşında olmalısınız`
        },
        {
          title: '4. Kabul Edilebilir Kullanım',
          content: `Şunları yapmamayı kabul edersiniz:
• Hizmeti yasadışı amaçlarla kullanmak
• Diğer kullanıcıları taciz etmek, kötüye kullanmak veya zarar vermek
• Hile yapmaya veya oyun puanlarını manipüle etmeye çalışmak
• Hizmetin düzgün çalışmasını engellemek
• Haksız avantaj elde etmek için birden fazla hesap oluşturmak`
        },
        {
          title: '5. Fikri Mülkiyet',
          content: `Trainvoc\'taki tüm içerik, metin, grafikler, logolar ve yazılım dahil ancak bunlarla sınırlı olmamak üzere, Rolling Cat Software\'in mülkiyetindedir ve fikri mülkiyet yasalarıyla korunmaktadır. Uygulama kaynak kodu MIT Lisansı altında mevcuttur.`
        },
        {
          title: '6. Sorumluluk Sınırlaması',
          content: `Trainvoc herhangi bir garanti olmaksızın "olduğu gibi" sağlanmaktadır. Hizmeti kullanmanızdan kaynaklanan dolaylı, arızi veya sonuç olarak ortaya çıkan zararlardan sorumlu değiliz.`
        },
        {
          title: '7. Şartlardaki Değişiklikler',
          content: `Bu şartları zaman zaman güncelleyebiliriz. Değişikliklerden sonra hizmeti kullanmaya devam etmeniz, yeni şartları kabul ettiğiniz anlamına gelir.`
        },
        {
          title: '8. İletişim',
          content: 'Bu şartlarla ilgili sorularınız için bize şu adresten ulaşın: rollingcat.help@gmail.com'
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
              <FileText className="h-8 w-8 text-brand-600 dark:text-brand-400" />
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

export default TermsOfServicePage
