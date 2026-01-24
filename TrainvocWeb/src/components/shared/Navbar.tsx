import { useState } from 'react'
import { Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { Menu, X, Globe } from 'lucide-react'
import { Button } from '@/components/ui/button'
import PlayButton from './PlayButton'
import { cn } from '@/lib/utils'

const Navbar: React.FC = () => {
  const [lang, setLang] = useState(localStorage.getItem('lang') || 'tr')
  const [mobileOpen, setMobileOpen] = useState(false)

  const handleLangChange = (newLang: string) => {
    setLang(newLang)
    localStorage.setItem('lang', newLang)
    window.location.reload()
  }

  const navItems = [
    { label: 'Ana Sayfa', to: '/' },
    { label: 'Hakkında', to: '/about' },
    { label: 'İletişim', to: '/contact' },
    { label: 'Mobil Uygulama', to: '/mobile' },
  ]

  return (
    <header className="bg-brand-600 text-white shadow-md">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="text-xl font-bold hover:opacity-90 transition-opacity">
            TrainVoc
          </Link>

          {/* Mobile menu button */}
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden text-white hover:bg-brand-500"
            onClick={() => setMobileOpen(!mobileOpen)}
          >
            {mobileOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
          </Button>

          {/* Desktop navigation */}
          <nav className="hidden md:flex items-center gap-2">
            {navItems.map((item) => (
              <Link
                key={item.to}
                to={item.to}
                className="px-3 py-2 rounded-lg text-white/90 hover:text-white hover:bg-brand-500 transition-colors"
              >
                {item.label}
              </Link>
            ))}

            <PlayButton />

            {/* Language selector */}
            <div className="flex items-center gap-1 ml-2">
              <Globe className="h-4 w-4 text-white/70" />
              <select
                value={lang}
                onChange={(e) => handleLangChange(e.target.value)}
                className="bg-brand-500 text-white border-none rounded px-2 py-1 text-sm cursor-pointer focus:outline-none focus:ring-2 focus:ring-white/30"
              >
                <option value="tr">TR</option>
                <option value="en">EN</option>
              </select>
            </div>
          </nav>
        </div>
      </div>

      {/* Mobile drawer */}
      <AnimatePresence>
        {mobileOpen && (
          <>
            {/* Backdrop */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setMobileOpen(false)}
              className="fixed inset-0 bg-black/50 z-40 md:hidden"
            />

            {/* Drawer */}
            <motion.div
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 300 }}
              className="fixed right-0 top-0 h-full w-[220px] bg-white dark:bg-gray-900 shadow-xl z-50 md:hidden"
            >
              <div className="p-4">
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={() => setMobileOpen(false)}
                  className="absolute top-4 right-4"
                >
                  <X className="h-5 w-5" />
                </Button>

                <nav className="mt-12 space-y-2">
                  {navItems.map((item) => (
                    <Link
                      key={item.to}
                      to={item.to}
                      onClick={() => setMobileOpen(false)}
                      className="block px-4 py-3 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                    >
                      {item.label}
                    </Link>
                  ))}

                  <div className="py-2">
                    <PlayButton fullWidth onClick={() => setMobileOpen(false)} />
                  </div>

                  {/* Language selector */}
                  <div className="px-4 py-3">
                    <label className="block text-sm text-gray-500 mb-1">Dil</label>
                    <select
                      value={lang}
                      onChange={(e) => handleLangChange(e.target.value)}
                      className={cn(
                        'w-full px-3 py-2 rounded-lg border border-gray-300 dark:border-gray-600',
                        'bg-white dark:bg-gray-800 text-gray-900 dark:text-white',
                        'focus:outline-none focus:ring-2 focus:ring-brand-500'
                      )}
                    >
                      <option value="tr">Türkçe</option>
                      <option value="en">English</option>
                    </select>
                  </div>
                </nav>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </header>
  )
}

export default Navbar
