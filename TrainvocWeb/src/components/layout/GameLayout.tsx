import type { ReactNode } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowLeft, Home, Settings, Users, Trophy, User } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { ThemeToggleSimple } from '@/components/ui/theme-toggle'

interface GameLayoutProps {
  children: ReactNode
  title?: string
  showBackButton?: boolean
  showNav?: boolean
  fullScreen?: boolean
  className?: string
}

const sideNavItems = [
  { icon: Home, label: { en: 'Lobby', tr: 'Lobi' }, href: '/play' },
  { icon: Users, label: { en: 'Rooms', tr: 'Odalar' }, href: '/play/lobby' },
  { icon: Trophy, label: { en: 'Leaderboard', tr: 'Sıralama' }, href: '/play/leaderboard' },
  { icon: User, label: { en: 'Profile', tr: 'Profil' }, href: '/play/profile' },
]

export function GameLayout({
  children,
  title,
  showBackButton = true,
  showNav = true,
  fullScreen = false,
  className,
}: GameLayoutProps) {
  const navigate = useNavigate()
  const location = useLocation()
  const { i18n } = useTranslation()
  const currentLang = i18n.language as 'en' | 'tr'

  if (fullScreen) {
    return (
      <div className="min-h-screen bg-gradient-dark">
        {children}
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Top bar */}
      <header className="fixed top-0 left-0 right-0 z-50 h-14 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
        <div className="flex items-center justify-between h-full px-4">
          {/* Left side */}
          <div className="flex items-center gap-4">
            {showBackButton && (
              <Button
                variant="ghost"
                size="icon-sm"
                onClick={() => navigate(-1)}
              >
                <ArrowLeft className="h-5 w-5" />
              </Button>
            )}
            <Link to="/" className="flex items-center gap-2">
              <span className="font-game text-xl text-brand-600 dark:text-brand-400">
                Trainvoc
              </span>
            </Link>
          </div>

          {/* Title */}
          {title && (
            <h1 className="absolute left-1/2 -translate-x-1/2 font-semibold text-gray-900 dark:text-white">
              {title}
            </h1>
          )}

          {/* Right side */}
          <div className="flex items-center gap-2">
            <ThemeToggleSimple />
            <Button variant="ghost" size="icon-sm">
              <Settings className="h-5 w-5" />
            </Button>
          </div>
        </div>
      </header>

      {/* Main content area */}
      <div className={cn('pt-14', showNav && 'md:pl-20')}>
        {/* Side navigation (desktop) */}
        {showNav && (
          <aside className="hidden md:flex fixed left-0 top-14 bottom-0 w-20 flex-col items-center py-4 gap-2 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700">
            {sideNavItems.map((item) => {
              const Icon = item.icon
              const isActive = location.pathname === item.href

              return (
                <Link
                  key={item.href}
                  to={item.href}
                  className={cn(
                    'flex flex-col items-center gap-1 p-3 rounded-xl transition-colors',
                    isActive
                      ? 'bg-brand-100 dark:bg-brand-900/30 text-brand-600 dark:text-brand-400'
                      : 'text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-700 hover:text-gray-900 dark:hover:text-white'
                  )}
                >
                  <Icon className="h-6 w-6" />
                  <span className="text-xs font-medium">{item.label[currentLang]}</span>
                </Link>
              )
            })}
          </aside>
        )}

        {/* Page content */}
        <main className={cn('min-h-[calc(100vh-3.5rem)]', className)}>
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
          >
            {children}
          </motion.div>
        </main>

        {/* Bottom navigation (mobile) */}
        {showNav && (
          <nav className="md:hidden fixed bottom-0 left-0 right-0 h-16 bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 safe-bottom">
            <div className="flex items-center justify-around h-full">
              {sideNavItems.map((item) => {
                const Icon = item.icon
                const isActive = location.pathname === item.href

                return (
                  <Link
                    key={item.href}
                    to={item.href}
                    className={cn(
                      'flex flex-col items-center gap-1 p-2 rounded-lg transition-colors',
                      isActive
                        ? 'text-brand-600 dark:text-brand-400'
                        : 'text-gray-500 hover:text-gray-900 dark:hover:text-white'
                    )}
                  >
                    <Icon className="h-5 w-5" />
                    <span className="text-xs">{item.label[currentLang]}</span>
                  </Link>
                )
              })}
            </div>
          </nav>
        )}
      </div>
    </div>
  )
}

// Simple centered layout for game screens
interface GameScreenLayoutProps {
  children: ReactNode
  className?: string
}

export function GameScreenLayout({ children, className }: GameScreenLayoutProps) {
  return (
    <div
      className={cn(
        'min-h-screen flex flex-col items-center justify-center p-4 bg-gradient-to-br from-brand-500 to-purple-600',
        className
      )}
    >
      {children}
    </div>
  )
}
