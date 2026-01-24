import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@/lib/utils'

const badgeVariants = cva(
  'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2',
  {
    variants: {
      variant: {
        default:
          'bg-brand-100 text-brand-700 dark:bg-brand-900 dark:text-brand-300',
        secondary:
          'bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300',
        destructive:
          'bg-error-100 text-error-700 dark:bg-error-900 dark:text-error-300',
        success:
          'bg-success-50 text-success-600 dark:bg-success-900 dark:text-success-300',
        warning:
          'bg-warning-50 text-warning-600 dark:bg-warning-900 dark:text-warning-300',
        outline:
          'border border-gray-200 text-gray-700 dark:border-gray-700 dark:text-gray-300',
        // Game difficulty badges
        easy: 'bg-success-50 text-success-600 border border-success-200',
        medium: 'bg-warning-50 text-warning-600 border border-warning-200',
        hard: 'bg-error-50 text-error-600 border border-error-200',
      },
    },
    defaultVariants: {
      variant: 'default',
    },
  }
)

export interface BadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof badgeVariants> {}

function Badge({ className, variant, ...props }: BadgeProps) {
  return (
    <div className={cn(badgeVariants({ variant }), className)} {...props} />
  )
}

export { Badge, badgeVariants }
