import { Component } from 'react'
import type { ErrorInfo, ReactNode } from 'react'
import { AlertTriangle } from 'lucide-react'
import i18n from '@/i18n'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

interface ErrorBoundaryProps {
  children: ReactNode
  fallback?: ReactNode
  onError?: (error: Error, errorInfo: ErrorInfo) => void
}

interface ErrorBoundaryState {
  hasError: boolean
  error: Error | null
}

/**
 * Error Boundary component for catching and handling React errors gracefully.
 * Prevents the entire app from crashing when a component throws an error.
 */
class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    // Log error to console in development
    if (import.meta.env.MODE === 'development') {
      console.error('ErrorBoundary caught an error:', error)
      console.error('Component stack:', errorInfo.componentStack)
    }

    // Call optional error handler
    this.props.onError?.(error, errorInfo)
  }

  handleRetry = (): void => {
    this.setState({ hasError: false, error: null })
  }

  handleGoHome = (): void => {
    window.location.href = '/'
  }

  render(): ReactNode {
    if (this.state.hasError) {
      // Custom fallback provided
      if (this.props.fallback) {
        return this.props.fallback
      }

      // Default error UI
      const t = i18n.t.bind(i18n)
      return (
        <div className="max-w-md mx-auto px-4">
          <div className="flex flex-col items-center justify-center min-h-[60vh] text-center gap-4">
            <AlertTriangle className="h-20 w-20 text-red-500" />
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
              {t('errorBoundary.title', 'Something Went Wrong')}
            </h1>
            <p className="text-gray-600 dark:text-gray-400 mb-2">
              {t('unexpectedError')}
            </p>
            {import.meta.env.MODE === 'development' && this.state.error && (
              <Card className="p-4 w-full overflow-auto text-left bg-gray-100 dark:bg-gray-800">
                <pre className="text-xs font-mono text-gray-700 dark:text-gray-300">
                  {this.state.error.message}
                </pre>
              </Card>
            )}
            <div className="flex gap-3">
              <Button onClick={this.handleRetry}>
                {t('errorBoundary.retry', 'Try Again')}
              </Button>
              <Button variant="outline" onClick={this.handleGoHome}>
                {t('errorBoundary.goHome', 'Go to Home')}
              </Button>
            </div>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}

export default ErrorBoundary
