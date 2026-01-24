import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { Menu, X, ArrowLeft, Play, Users, DoorOpen, User } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

const sidebarItems = [
  { label: 'Ana Sayfaya Dön', icon: ArrowLeft, path: '/' },
  { label: 'Oyun Kur', icon: Play, path: '/play/create' },
  { label: 'Oyuna Katıl', icon: Users, path: '/play/join' },
  { label: 'Lobi', icon: DoorOpen, path: '/play/lobby' },
  { label: 'Profilim', icon: User, path: '/play/profile' },
]

const PlaySidebar: React.FC = () => {
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()

  const handleNavigate = (path: string) => {
    navigate(path)
    setOpen(false)
  }

  return (
    <>
      {/* Menu button */}
      <Button
        variant="outline"
        size="icon"
        onClick={() => setOpen(true)}
        className={cn(
          'fixed top-4 right-4 z-[1301] bg-white shadow-md',
          'w-11 h-11 sm:w-13 sm:h-13',
          open && 'hidden'
        )}
      >
        <Menu className="h-6 w-6 sm:h-7 sm:w-7" />
      </Button>

      {/* Backdrop */}
      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setOpen(false)}
            className="fixed inset-0 bg-black/50 z-[1300]"
          />
        )}
      </AnimatePresence>

      {/* Drawer */}
      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ x: '100%' }}
            animate={{ x: 0 }}
            exit={{ x: '100%' }}
            transition={{ type: 'spring', damping: 25, stiffness: 300 }}
            className="fixed right-0 top-0 h-full w-[220px] sm:w-[270px] md:w-[300px] bg-white dark:bg-gray-900 shadow-xl z-[1301] flex flex-col"
          >
            {/* Header */}
            <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
              <h2 className="text-lg font-bold text-brand-600 dark:text-brand-400">
                Oyun Menüsü
              </h2>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setOpen(false)}
              >
                <X className="h-5 w-5" />
              </Button>
            </div>

            {/* Navigation items */}
            <nav className="flex-1 p-3 space-y-2">
              {sidebarItems.map((item) => {
                const isActive = location.pathname === item.path
                const Icon = item.icon

                return (
                  <button
                    key={item.path}
                    onClick={() => handleNavigate(item.path)}
                    className={cn(
                      'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all',
                      'text-left font-medium',
                      isActive
                        ? 'bg-brand-500 text-white shadow-md'
                        : 'bg-gray-100 dark:bg-gray-800 text-brand-600 dark:text-brand-400 hover:bg-brand-100 dark:hover:bg-brand-900/30'
                    )}
                  >
                    <Icon className="h-5 w-5 flex-shrink-0" />
                    <span className="truncate">{item.label}</span>
                  </button>
                )
              })}
            </nav>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  )
}

export default PlaySidebar
