import { lazy, Suspense } from 'react'
import { Route, Routes, useLocation } from 'react-router-dom'
import { AnimatePresence, motion } from 'framer-motion'
import ScrollToTop from './components/shared/ScrollToTop'
import ErrorBoundary from './components/shared/ErrorBoundary'
import { InstallPrompt } from './components/shared/InstallPrompt'

// Lazy load pages
const HomePage = lazy(() => import('./pages/HomePage'))
const AboutPage = lazy(() => import('./pages/AboutPage'))
const ContactPage = lazy(() => import('./pages/ContactPage'))
const MobileAppPage = lazy(() => import('./pages/MobileAppPage'))
const PlayLayout = lazy(() => import('./pages/PlayLayout'))
const GameMenuPage = lazy(() => import('./pages/GameMenuPage'))
const OfflinePage = lazy(() => import('./pages/OfflinePage'))

// Loading spinner
function LoadingSpinner() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
      <div className="flex flex-col items-center gap-4">
        <div className="w-12 h-12 border-4 border-brand-200 border-t-brand-500 rounded-full animate-spin" />
        <p className="text-gray-500 dark:text-gray-400">Loading...</p>
      </div>
    </div>
  )
}

// Page wrapper with animation
function PageWrapper({ children }: { children: React.ReactNode }) {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.3 }}
    >
      {children}
    </motion.div>
  )
}

function App() {
  const location = useLocation()

  return (
    <ErrorBoundary>
      <ScrollToTop />
      <Suspense fallback={<LoadingSpinner />}>
        <AnimatePresence mode="wait">
          <Routes location={location} key={location.pathname}>
            {/* Home - uses its own layout */}
            <Route
              path="/"
              element={
                <PageWrapper>
                  <HomePage />
                </PageWrapper>
              }
            />

            {/* Static pages - will get layout from individual components */}
            <Route
              path="/about"
              element={
                <PageWrapper>
                  <AboutPage />
                </PageWrapper>
              }
            />
            <Route
              path="/contact"
              element={
                <PageWrapper>
                  <ContactPage />
                </PageWrapper>
              }
            />
            <Route
              path="/mobile"
              element={
                <PageWrapper>
                  <MobileAppPage />
                </PageWrapper>
              }
            />

            {/* Game routes */}
            <Route path="/play/*" element={<PlayLayout />} />
            <Route path="/games" element={<GameMenuPage />} />

            {/* PWA offline page */}
            <Route path="/offline" element={<OfflinePage />} />
          </Routes>
        </AnimatePresence>
      </Suspense>

      {/* PWA Install Prompt */}
      <InstallPrompt />
    </ErrorBoundary>
  )
}

export default App
