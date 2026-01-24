import { motion, AnimatePresence, LayoutGroup } from 'framer-motion'
import { Trophy, TrendingUp, TrendingDown, Minus } from 'lucide-react'
import { cn } from '@/lib/utils'
import { leaderboardItemVariants } from '@/lib/animations'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'

interface Player {
  id: string
  name: string
  score: number
  avatar?: string
  previousRank?: number
}

interface LeaderboardProps {
  players: Player[]
  currentPlayerId?: string
  showTrend?: boolean
  maxVisible?: number
  className?: string
}

export function Leaderboard({
  players,
  currentPlayerId,
  showTrend = true,
  maxVisible = 10,
  className,
}: LeaderboardProps) {
  // Sort players by score descending
  const sortedPlayers = [...players].sort((a, b) => b.score - a.score)
  const visiblePlayers = sortedPlayers.slice(0, maxVisible)

  const getRankTrend = (player: Player, currentRank: number) => {
    if (!showTrend || player.previousRank === undefined) return null
    const diff = player.previousRank - currentRank
    if (diff > 0) return { direction: 'up', value: diff }
    if (diff < 0) return { direction: 'down', value: Math.abs(diff) }
    return { direction: 'same', value: 0 }
  }

  return (
    <div className={cn('w-full max-w-md mx-auto', className)}>
      <LayoutGroup>
        <AnimatePresence mode="popLayout">
          {visiblePlayers.map((player, index) => {
            const rank = index + 1
            const trend = getRankTrend(player, rank)
            const isCurrentPlayer = player.id === currentPlayerId

            return (
              <motion.div
                key={player.id}
                layout
                layoutId={player.id}
                variants={leaderboardItemVariants}
                initial="initial"
                animate="animate"
                exit="exit"
                className={cn(
                  'flex items-center gap-3 p-3 mb-2 rounded-xl transition-colors',
                  isCurrentPlayer
                    ? 'bg-brand-100 dark:bg-brand-900/30 border-2 border-brand-500'
                    : 'bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700'
                )}
              >
                {/* Rank */}
                <div className="flex-shrink-0 w-8 text-center">
                  {rank === 1 ? (
                    <Trophy className="h-6 w-6 text-yellow-500 mx-auto" />
                  ) : (
                    <span
                      className={cn(
                        'font-bold text-lg',
                        rank === 2 && 'text-gray-400',
                        rank === 3 && 'text-orange-400',
                        rank > 3 && 'text-gray-500'
                      )}
                    >
                      {rank}
                    </span>
                  )}
                </div>

                {/* Avatar */}
                <Avatar className="h-10 w-10 flex-shrink-0">
                  <AvatarImage src={player.avatar} alt={player.name} />
                  <AvatarFallback>
                    {player.name.charAt(0).toUpperCase()}
                  </AvatarFallback>
                </Avatar>

                {/* Name */}
                <div className="flex-1 min-w-0">
                  <p
                    className={cn(
                      'font-medium truncate',
                      isCurrentPlayer
                        ? 'text-brand-700 dark:text-brand-300'
                        : 'text-gray-900 dark:text-white'
                    )}
                  >
                    {player.name}
                    {isCurrentPlayer && (
                      <span className="ml-1 text-xs">(You)</span>
                    )}
                  </p>
                </div>

                {/* Trend indicator */}
                {trend && (
                  <div className="flex-shrink-0">
                    {trend.direction === 'up' && (
                      <div className="flex items-center text-success-500">
                        <TrendingUp className="h-4 w-4" />
                        <span className="text-xs ml-0.5">{trend.value}</span>
                      </div>
                    )}
                    {trend.direction === 'down' && (
                      <div className="flex items-center text-error-500">
                        <TrendingDown className="h-4 w-4" />
                        <span className="text-xs ml-0.5">{trend.value}</span>
                      </div>
                    )}
                    {trend.direction === 'same' && (
                      <Minus className="h-4 w-4 text-gray-400" />
                    )}
                  </div>
                )}

                {/* Score */}
                <div className="flex-shrink-0 text-right">
                  <motion.span
                    key={player.score}
                    initial={{ scale: 1.2, color: '#22C55E' }}
                    animate={{ scale: 1, color: 'inherit' }}
                    className="font-bold text-lg text-brand-600 dark:text-brand-400"
                  >
                    {player.score.toLocaleString()}
                  </motion.span>
                </div>
              </motion.div>
            )
          })}
        </AnimatePresence>
      </LayoutGroup>

      {/* Show more indicator */}
      {players.length > maxVisible && (
        <p className="text-center text-sm text-gray-500 dark:text-gray-400 mt-4">
          +{players.length - maxVisible} more players
        </p>
      )}
    </div>
  )
}
