import { motion } from 'framer-motion'
import { Trophy, Medal } from 'lucide-react'
import { cn } from '@/lib/utils'
import { podiumVariants, popVariants } from '@/lib/animations'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'

interface Player {
  id: string
  name: string
  score: number
  avatar?: string
}

interface PodiumProps {
  players: Player[]
  className?: string
}

export function Podium({ players, className }: PodiumProps) {
  // Take top 3 and reorder for podium display: 2nd, 1st, 3rd
  const topThree = players.slice(0, 3)
  const podiumOrder = [topThree[1], topThree[0], topThree[2]].filter(Boolean)

  const podiumConfig = [
    { place: 2, height: 120, color: 'podium-second', delay: 0.3 },
    { place: 1, height: 160, color: 'podium-first', delay: 0 },
    { place: 3, height: 80, color: 'podium-third', delay: 0.5 },
  ]

  return (
    <div className={cn('flex items-end justify-center gap-4', className)}>
      {podiumOrder.map((player, index) => {
        if (!player) return null
        const config = podiumConfig[index]

        return (
          <motion.div
            key={player.id}
            className="flex flex-col items-center"
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: config.delay + 0.2 }}
          >
            {/* Player info */}
            <motion.div
              className="mb-4 text-center"
              variants={popVariants}
              initial="initial"
              animate="animate"
              transition={{ delay: config.delay + 0.4 }}
            >
              <div className="relative">
                <Avatar className="h-16 w-16 border-4 border-white shadow-lg">
                  <AvatarImage src={player.avatar} alt={player.name} />
                  <AvatarFallback className="text-xl">
                    {player.name.charAt(0).toUpperCase()}
                  </AvatarFallback>
                </Avatar>
                {config.place === 1 && (
                  <Trophy className="absolute -top-2 -right-2 h-8 w-8 text-yellow-500" />
                )}
              </div>
              <p className="mt-2 font-bold text-gray-900 dark:text-white truncate max-w-[100px]">
                {player.name}
              </p>
              <p className="text-sm text-gray-500 dark:text-gray-400">
                {player.score.toLocaleString()} pts
              </p>
            </motion.div>

            {/* Podium bar */}
            <motion.div
              className={cn(
                'w-24 rounded-t-lg flex items-start justify-center pt-4',
                config.color
              )}
              variants={podiumVariants}
              initial="initial"
              animate="animate"
              custom={config.height}
            >
              <span className="text-3xl font-game text-white drop-shadow-lg">
                {config.place}
              </span>
            </motion.div>
          </motion.div>
        )
      })}
    </div>
  )
}

// Results table for all players
interface ResultsTableProps {
  players: Player[]
  currentPlayerId?: string
  className?: string
}

export function ResultsTable({ players, currentPlayerId, className }: ResultsTableProps) {
  return (
    <div className={cn('space-y-2', className)}>
      {players.map((player, index) => (
        <motion.div
          key={player.id}
          className={cn(
            'flex items-center gap-4 p-3 rounded-xl',
            player.id === currentPlayerId
              ? 'bg-brand-100 dark:bg-brand-900/30 border-2 border-brand-500'
              : 'bg-gray-100 dark:bg-gray-800'
          )}
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: index * 0.1 }}
        >
          {/* Rank */}
          <div className="flex-shrink-0 w-8 text-center">
            {index === 0 ? (
              <Trophy className="h-6 w-6 text-yellow-500 mx-auto" />
            ) : index === 1 ? (
              <Medal className="h-6 w-6 text-gray-400 mx-auto" />
            ) : index === 2 ? (
              <Medal className="h-6 w-6 text-orange-400 mx-auto" />
            ) : (
              <span className="font-bold text-gray-500">#{index + 1}</span>
            )}
          </div>

          {/* Avatar */}
          <Avatar className="h-10 w-10">
            <AvatarImage src={player.avatar} alt={player.name} />
            <AvatarFallback>{player.name.charAt(0).toUpperCase()}</AvatarFallback>
          </Avatar>

          {/* Name */}
          <span className="flex-1 font-medium text-gray-900 dark:text-white truncate">
            {player.name}
            {player.id === currentPlayerId && (
              <span className="ml-2 text-sm text-brand-500">(You)</span>
            )}
          </span>

          {/* Score */}
          <span className="font-bold text-brand-600 dark:text-brand-400">
            {player.score.toLocaleString()}
          </span>
        </motion.div>
      ))}
    </div>
  )
}
