import { useState } from 'react'
import { motion } from 'framer-motion'
import { useTranslation } from 'react-i18next'
import { Mail, MapPin, Globe, Send } from 'lucide-react'
import { Header, Footer } from '@/components/layout'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'

function ContactPage() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'en' | 'tr'
  const [formState, setFormState] = useState({
    name: '',
    email: '',
    message: '',
  })

  const content = {
    en: {
      title: 'Contact Us',
      subtitle: 'Get in touch with the Trainvoc team',
      description:
        'As Rolling Cat Software, we develop the Trainvoc platform. You can reach us for any questions, suggestions, and collaborations.',
      email: 'Email',
      web: 'Website',
      address: 'Address',
      addressValue: 'Istanbul, Turkey',
      feedback: 'Your feedback is very valuable to us!',
      formTitle: 'Send us a message',
      name: 'Name',
      yourEmail: 'Your Email',
      message: 'Message',
      send: 'Send Message',
    },
    tr: {
      title: 'İletişim',
      subtitle: 'Trainvoc ekibiyle iletişime geçin',
      description:
        'Rolling Cat Software olarak Trainvoc platformunu geliştiriyoruz. Her türlü soru, öneri ve iş birliği için bize ulaşabilirsiniz.',
      email: 'E-posta',
      web: 'Web Sitesi',
      address: 'Adres',
      addressValue: 'İstanbul, Türkiye',
      feedback: 'Geri bildirimleriniz bizim için çok değerli!',
      formTitle: 'Bize mesaj gönderin',
      name: 'İsim',
      yourEmail: 'E-posta Adresiniz',
      message: 'Mesaj',
      send: 'Mesaj Gönder',
    },
  }

  const t = content[lang]

  const contactInfo = [
    {
      icon: Mail,
      label: t.email,
      value: 'rollingcat.help@gmail.com',
      href: 'mailto:rollingcat.help@gmail.com',
    },
    {
      icon: Globe,
      label: t.web,
      value: 'www.rollingcatsoftware.com',
      href: 'https://www.rollingcatsoftware.com',
    },
    {
      icon: MapPin,
      label: t.address,
      value: t.addressValue,
    },
  ]

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    // Handle form submission
    console.log('Form submitted:', formState)
  }

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

          <div className="grid md:grid-cols-2 gap-8">
            {/* Contact info */}
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.1 }}
            >
              <Card className="p-6 md:p-8 h-full">
                <p className="text-gray-600 dark:text-gray-300 mb-6">
                  {t.description}
                </p>

                <div className="space-y-4">
                  {contactInfo.map((info, index) => {
                    const Icon = info.icon
                    return (
                      <div key={index} className="flex items-start gap-4">
                        <div className="p-2 rounded-lg bg-brand-100 dark:bg-brand-900/30">
                          <Icon className="h-5 w-5 text-brand-600 dark:text-brand-400" />
                        </div>
                        <div>
                          <p className="text-sm text-gray-500 dark:text-gray-400">
                            {info.label}
                          </p>
                          {info.href ? (
                            <a
                              href={info.href}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="font-medium text-gray-900 dark:text-white hover:text-brand-600 dark:hover:text-brand-400 transition-colors"
                            >
                              {info.value}
                            </a>
                          ) : (
                            <p className="font-medium text-gray-900 dark:text-white">
                              {info.value}
                            </p>
                          )}
                        </div>
                      </div>
                    )
                  })}
                </div>

                <p className="mt-6 text-gray-500 dark:text-gray-400 italic">
                  {t.feedback}
                </p>
              </Card>
            </motion.div>

            {/* Contact form */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.2 }}
            >
              <Card className="p-6 md:p-8">
                <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-6">
                  {t.formTitle}
                </h2>

                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                      {t.name}
                    </label>
                    <Input
                      type="text"
                      value={formState.name}
                      onChange={(e) =>
                        setFormState({ ...formState, name: e.target.value })
                      }
                      placeholder={t.name}
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                      {t.yourEmail}
                    </label>
                    <Input
                      type="email"
                      value={formState.email}
                      onChange={(e) =>
                        setFormState({ ...formState, email: e.target.value })
                      }
                      placeholder={t.yourEmail}
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                      {t.message}
                    </label>
                    <textarea
                      value={formState.message}
                      onChange={(e) =>
                        setFormState({ ...formState, message: e.target.value })
                      }
                      placeholder={t.message}
                      rows={4}
                      className="w-full rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 px-4 py-2 text-gray-900 dark:text-white placeholder:text-gray-400 focus:ring-2 focus:ring-brand-500 focus:border-transparent transition-colors resize-none"
                    />
                  </div>

                  <Button type="submit" className="w-full gap-2">
                    <Send className="h-4 w-4" />
                    {t.send}
                  </Button>
                </form>
              </Card>
            </motion.div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  )
}

export default ContactPage
