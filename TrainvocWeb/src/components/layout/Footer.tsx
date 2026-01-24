import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Gamepad2, Github, Twitter, Mail } from 'lucide-react'

const footerLinks = {
  product: [
    { name: { en: 'Play', tr: 'Oyna' }, href: '/play' },
    { name: { en: 'Mobile App', tr: 'Mobil Uygulama' }, href: '/mobile' },
    { name: { en: 'Leaderboard', tr: 'Liderlik Tablosu' }, href: '/play/leaderboard' },
  ],
  company: [
    { name: { en: 'About', tr: 'Hakkımızda' }, href: '/about' },
    { name: { en: 'Contact', tr: 'İletişim' }, href: '/contact' },
  ],
  legal: [
    { name: { en: 'Privacy Policy', tr: 'Gizlilik Politikası' }, href: '/privacy' },
    { name: { en: 'Terms of Service', tr: 'Kullanım Şartları' }, href: '/terms' },
  ],
}

export function Footer() {
  const { i18n } = useTranslation()
  const currentLang = i18n.language as 'en' | 'tr'
  const currentYear = new Date().getFullYear()

  return (
    <footer className="bg-gray-50 dark:bg-gray-900 border-t border-gray-200 dark:border-gray-800">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand */}
          <div className="col-span-1">
            <Link to="/" className="flex items-center gap-2 mb-4">
              <Gamepad2 className="h-8 w-8 text-brand-500" />
              <span className="font-game text-2xl text-brand-600 dark:text-brand-400">
                Trainvoc
              </span>
            </Link>
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">
              {currentLang === 'tr'
                ? 'Eğlenceli oyunlarla İngilizce-Türkçe kelime öğrenin.'
                : 'Learn English-Turkish vocabulary through fun games.'}
            </p>
            <div className="flex gap-4">
              <a
                href="https://github.com/trainvoc"
                target="_blank"
                rel="noopener noreferrer"
                className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <Github className="h-5 w-5" />
              </a>
              <a
                href="https://twitter.com/trainvoc"
                target="_blank"
                rel="noopener noreferrer"
                className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <Twitter className="h-5 w-5" />
              </a>
              <a
                href="mailto:contact@trainvoc.com"
                className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <Mail className="h-5 w-5" />
              </a>
            </div>
          </div>

          {/* Product Links */}
          <div>
            <h3 className="font-semibold text-gray-900 dark:text-white mb-4">
              {currentLang === 'tr' ? 'Ürün' : 'Product'}
            </h3>
            <ul className="space-y-2">
              {footerLinks.product.map((link) => (
                <li key={link.href}>
                  <Link
                    to={link.href}
                    className="text-sm text-gray-600 hover:text-brand-600 dark:text-gray-400 dark:hover:text-brand-400"
                  >
                    {link.name[currentLang]}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Company Links */}
          <div>
            <h3 className="font-semibold text-gray-900 dark:text-white mb-4">
              {currentLang === 'tr' ? 'Şirket' : 'Company'}
            </h3>
            <ul className="space-y-2">
              {footerLinks.company.map((link) => (
                <li key={link.href}>
                  <Link
                    to={link.href}
                    className="text-sm text-gray-600 hover:text-brand-600 dark:text-gray-400 dark:hover:text-brand-400"
                  >
                    {link.name[currentLang]}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Legal Links */}
          <div>
            <h3 className="font-semibold text-gray-900 dark:text-white mb-4">
              {currentLang === 'tr' ? 'Yasal' : 'Legal'}
            </h3>
            <ul className="space-y-2">
              {footerLinks.legal.map((link) => (
                <li key={link.href}>
                  <Link
                    to={link.href}
                    className="text-sm text-gray-600 hover:text-brand-600 dark:text-gray-400 dark:hover:text-brand-400"
                  >
                    {link.name[currentLang]}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Bottom bar */}
        <div className="mt-12 pt-8 border-t border-gray-200 dark:border-gray-800">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <p className="text-sm text-gray-500 dark:text-gray-400">
              {currentYear} Trainvoc. {currentLang === 'tr' ? 'Tüm hakları saklıdır.' : 'All rights reserved.'}
            </p>
            <p className="text-sm text-gray-500 dark:text-gray-400">
              {currentLang === 'tr' ? 'Sevgiyle yapıldı' : 'Made with'} ❤️ {currentLang === 'tr' ? 'Türkiye\'de' : 'in Turkey'}
            </p>
          </div>
        </div>
      </div>
    </footer>
  )
}
