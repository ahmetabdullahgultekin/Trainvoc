import { Link } from 'react-router-dom'
import { Gamepad2 } from 'lucide-react'
import { motion } from 'framer-motion'
import { cn } from '@/lib/utils'

interface PlayButtonProps {
  /** Whether to render as full width (for mobile drawer) */
  fullWidth?: boolean
  /** Optional click handler */
  onClick?: () => void
}

/**
 * Animated "Play" button used in navigation.
 * Extracted to avoid code duplication between desktop and mobile nav.
 */
const PlayButton: React.FC<PlayButtonProps> = ({ fullWidth = false, onClick }) => {
  return (
    <motion.div
      whileHover={{ scale: 1.07 }}
      whileTap={{ scale: 0.95 }}
    >
      <Link
        to="/play"
        onClick={onClick}
        className={cn(
          'inline-flex items-center gap-2 px-4 py-2 font-bold rounded-xl',
          'bg-gradient-to-r from-blue-500 to-cyan-400 text-white shadow-lg',
          'hover:from-cyan-400 hover:to-blue-500 transition-all',
          'animate-pulse',
          fullWidth && 'w-full justify-center py-3 my-2'
        )}
      >
        Oyna
        <span className="ml-1 px-2 py-0.5 text-xs font-medium bg-blue-600/50 rounded-lg">
          Oyun Alanına Git!
        </span>
        <Gamepad2 className="ml-1 h-7 w-7" />
      </Link>
    </motion.div>
  )
}

export default PlayButton
