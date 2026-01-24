import React from 'react'
import { motion } from 'framer-motion'
import { Medal } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { cn } from '@/lib/utils'
import type { Player } from '../interfaces/game'

interface GameRankingProps {
  players: Player[]
}

const medalColors = ['bg-yellow-400', 'bg-gray-300', 'bg-orange-400']

/**
 * Displays player rankings during/after game.
 * Memoized to prevent re-renders when game state updates but rankings don't change.
 */
const GameRanking: React.FC<GameRankingProps> = React.memo(({ players }) => {
  return (
    <div className="max-w-lg mx-auto mt-6">
      <Card className="p-6">
        <h2 className="text-xl font-bold text-center mb-4 text-gray-900 dark:text-white">
          Soru Sıralaması
        </h2>

        <div className="space-y-3">
          {players.map((player, idx) => (
            <motion.div
              key={player.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: idx * 0.05 }}
              className={cn(
                'flex items-center p-3 rounded-xl',
                player.isYou && 'bg-brand-100 dark:bg-brand-900/30'
              )}
            >
              <Avatar className={cn(
                'mr-3',
                idx < 3 ? medalColors[idx] : 'bg-gray-400'
              )}>
                <AvatarFallback className="text-white font-bold">
                  {idx < 3 ? (
                    <Medal className="h-5 w-5" />
                  ) : (
                    idx + 1
                  )}
                </AvatarFallback>
              </Avatar>

              <span className={cn(
                'flex-1',
                player.isYou ? 'font-bold' : 'font-medium',
                idx < 3 ? 'text-brand-600 dark:text-brand-400' : 'text-gray-700 dark:text-gray-300'
              )}>
                {player.name}
                {player.isYou && (
                  <span className="ml-1 text-brand-500">(sen)</span>
                )}
              </span>

              <span className="font-bold text-gray-900 dark:text-white">
                {player.score} puan
              </span>
            </motion.div>
          ))}
        </div>
      </Card>
    </div>
  )
})

GameRanking.displayName = 'GameRanking'

export default GameRanking
