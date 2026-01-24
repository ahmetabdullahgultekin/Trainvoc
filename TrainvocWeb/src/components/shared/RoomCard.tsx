import React from 'react'
import { motion } from 'framer-motion'
import { Users, Clock, BookOpen, Loader2 } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import type { GameRoom } from '../../interfaces/game'

interface RoomCardProps {
  room: GameRoom
  idx: number
  t: (key: string) => string
  joiningRoom: string | null
  onJoin: (roomCode: string) => void
}

/**
 * Room card component displayed in room lists.
 * Memoized to prevent unnecessary re-renders when parent updates.
 */
const RoomCard: React.FC<RoomCardProps> = React.memo(({ room, idx, t, joiningRoom, onJoin }) => {
  const levelMap: Record<string, string> = {
    a1: t('levelA1'),
    a2: t('levelA2'),
    b1: t('levelB1'),
    b2: t('levelB2'),
    c1: t('levelC1'),
    c2: t('levelC2'),
    easy: t('easy'),
    medium: t('medium'),
    hard: t('hard'),
  }

  const levelLabel = room.level && levelMap[room.level.toLowerCase()]
    ? levelMap[room.level.toLowerCase()]
    : room.level || '-'
  const playersLabel = room.players?.length?.toLocaleString() || '1'

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: idx * 0.1 }}
      whileHover={{ y: -4, scale: 1.02 }}
    >
      <Card className="overflow-hidden border-2 border-brand-200 dark:border-brand-800 hover:border-brand-500 transition-all min-h-[240px] flex flex-col">
        {/* Header */}
        <div className="flex items-center gap-3 bg-brand-50 dark:bg-brand-900/30 px-4 py-3 border-b border-brand-200 dark:border-brand-800">
          <Avatar className="w-14 h-14 bg-brand-500">
            <AvatarFallback className="text-white font-bold text-xl">
              {room.roomCode?.slice(0, 2) || '?'}
            </AvatarFallback>
          </Avatar>
          <div>
            <p className="text-sm font-bold text-brand-600 dark:text-brand-400 tracking-wide">
              {t('roomCode')}: <span className="font-bold">{room.roomCode || '-'}</span>
            </p>
            <div className="flex items-center gap-1 text-gray-600 dark:text-gray-400 text-sm">
              <Users className="h-4 w-4" />
              <span>{t('players')}: {playersLabel}</span>
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="flex-1 flex flex-col justify-between p-4">
          <div className="space-y-2">
            <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400 text-sm">
              <BookOpen className="h-4 w-4" />
              <span>{t('level')}: <strong>{levelLabel}</strong></span>
            </div>
            <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400 text-sm">
              <Clock className="h-4 w-4" />
              <span>
                {t('questionCount')}: <strong>{room.totalQuestionCount ?? '-'} {t('questions')}</strong>
                {' | '}
                {t('timePerQuestion')}: <strong>{room.questionDuration ? `${room.questionDuration} ${t('seconds')}` : '-'}</strong>
              </span>
            </div>
          </div>

          <Button
            className="w-full mt-4 font-bold"
            disabled={joiningRoom === room.roomCode}
            onClick={() => onJoin(room.roomCode)}
          >
            {joiningRoom === room.roomCode ? (
              <>
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                {t('joining')}
              </>
            ) : (
              t('join')
            )}
          </Button>
        </div>
      </Card>
    </motion.div>
  )
})

RoomCard.displayName = 'RoomCard'

export default RoomCard
