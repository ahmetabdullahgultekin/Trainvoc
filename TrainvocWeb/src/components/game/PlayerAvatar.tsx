import { motion } from 'framer-motion'
import { Crown, Check, X, Clock } from 'lucide-react'
import { cn } from '@/lib/utils'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'

interface PlayerAvatarProps {
  name: string
  avatar?: string
  isHost?: boolean
  isReady?: boolean
  isCurrentPlayer?: boolean
  status?: 'waiting' | 'correct' | 'wrong' | 'thinking'
  size?: 'sm' | 'md' | 'lg'
  className?: string
}

const sizeConfig = {
  sm: { avatar: 'h-8 w-8', text: 'text-xs', icon: 'h-3 w-3' },
  md: { avatar: 'h-12 w-12', text: 'text-sm', icon: 'h-4 w-4' },
  lg: { avatar: 'h-16 w-16', text: 'text-base', icon: 'h-5 w-5' },
}

export function PlayerAvatar({
  name,
  avatar,
  isHost,
  isReady,
  isCurrentPlayer,
  status,
  size = 'md',
  className,
}: PlayerAvatarProps) {
  const config = sizeConfig[size]

  const getStatusIndicator = () => {
    switch (status) {
      case 'correct':
        return (
          <div className="absolute -bottom-1 -right-1 bg-success-500 rounded-full p-1">
            <Check className={cn(config.icon, 'text-white')} />
          </div>
        )
      case 'wrong':
        return (
          <div className="absolute -bottom-1 -right-1 bg-error-500 rounded-full p-1">
            <X className={cn(config.icon, 'text-white')} />
          </div>
        )
      case 'thinking':
        return (
          <motion.div
            className="absolute -bottom-1 -right-1 bg-warning-500 rounded-full p-1"
            animate={{ scale: [1, 1.2, 1] }}
            transition={{ repeat: Infinity, duration: 1 }}
          >
            <Clock className={cn(config.icon, 'text-white')} />
          </motion.div>
        )
      default:
        return null
    }
  }

  return (
    <div className={cn('flex flex-col items-center gap-1', className)}>
      <div className="relative">
        {/* Host crown */}
        {isHost && (
          <motion.div
            initial={{ y: -10, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            className="absolute -top-4 left-1/2 -translate-x-1/2"
          >
            <Crown className="h-5 w-5 text-yellow-500" />
          </motion.div>
        )}

        {/* Avatar */}
        <Avatar
          className={cn(
            config.avatar,
            'border-2',
            isCurrentPlayer
              ? 'border-brand-500 ring-2 ring-brand-300'
              : 'border-white dark:border-gray-700',
            isReady && 'ring-2 ring-success-400'
          )}
        >
          <AvatarImage src={avatar} alt={name} />
          <AvatarFallback className={config.text}>
            {name.charAt(0).toUpperCase()}
          </AvatarFallback>
        </Avatar>

        {/* Status indicator */}
        {getStatusIndicator()}

        {/* Ready indicator */}
        {isReady && !status && (
          <div className="absolute -bottom-1 -right-1 bg-success-500 rounded-full p-1">
            <Check className={cn(config.icon, 'text-white')} />
          </div>
        )}
      </div>

      {/* Name */}
      <p
        className={cn(
          'font-medium text-center truncate max-w-[80px]',
          config.text,
          isCurrentPlayer
            ? 'text-brand-600 dark:text-brand-400'
            : 'text-gray-700 dark:text-gray-300'
        )}
      >
        {name}
      </p>
    </div>
  )
}

// Player list for lobby
interface PlayerListProps {
  players: Array<{
    id: string
    name: string
    avatar?: string
    isHost?: boolean
    isReady?: boolean
  }>
  currentPlayerId?: string
  className?: string
}

export function PlayerList({ players, currentPlayerId, className }: PlayerListProps) {
  return (
    <div className={cn('flex flex-wrap gap-4 justify-center', className)}>
      {players.map((player) => (
        <PlayerAvatar
          key={player.id}
          name={player.name}
          avatar={player.avatar}
          isHost={player.isHost}
          isReady={player.isReady}
          isCurrentPlayer={player.id === currentPlayerId}
        />
      ))}
    </div>
  )
}
