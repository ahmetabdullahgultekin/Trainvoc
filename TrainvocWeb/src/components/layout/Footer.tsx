import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Gamepad2, Mail } from 'lucide-react'

// lucide-react v1 removed all brand icons (trademark policy), so GitHub and
// Twitter/X are inlined here — the only two brand marks the app uses.
function GithubIcon({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true" className={className}>
      <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12" />
    </svg>
  )
}

function TwitterIcon({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true" className={className}>
      <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z" />
    </svg>
  )
}

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
                aria-label="GitHub"
                className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <GithubIcon className="h-5 w-5" />
              </a>
              <a
                href="https://twitter.com/trainvoc"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="Twitter"
                className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <TwitterIcon className="h-5 w-5" />
              </a>
              <a
                href="mailto:rollingcat.help@gmail.com"
                aria-label="Email"
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
              © 2024-{currentYear} Trainvoc. {currentLang === 'tr' ? 'Tüm hakları saklıdır.' : 'All rights reserved.'}
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
