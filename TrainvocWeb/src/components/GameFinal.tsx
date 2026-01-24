import React from 'react'
import { motion } from 'framer-motion'
import { Trophy, Medal } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { cn } from '@/lib/utils'
import type { Player } from '../interfaces/game'

interface GameFinalProps {
  players: Player[]
}

const medalColors = ['bg-yellow-400', 'bg-gray-300', 'bg-orange-400']

/**
 * Final game results display.
 * Memoized since it renders once and shouldn't re-render.
 */
const GameFinal: React.FC<GameFinalProps> = React.memo(({ players }) => {
  return (
    <div className="max-w-lg mx-auto mt-6">
      <Card className="p-6 bg-yellow-50 dark:bg-yellow-900/10">
        <div className="flex items-center justify-center gap-2 mb-4">
          <Trophy className="h-8 w-8 text-yellow-500" />
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
            Oyun Bitti!
          </h2>
        </div>

        <h3 className="text-lg font-semibold text-center mb-4 text-gray-700 dark:text-gray-300">
          Final Sıralaması
        </h3>

        <div className="space-y-3">
          {players.map((player, idx) => (
            <motion.div
              key={player.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: idx * 0.1 }}
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

GameFinal.displayName = 'GameFinal'

export default GameFinal
