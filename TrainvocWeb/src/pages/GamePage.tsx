import { useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation } from 'react-router-dom'
import { AlertCircle, Loader2 } from 'lucide-react'
import api from '../api'
import { Button } from '@/components/ui/button'
import GameStartCountdown from '../components/GameStartCountdown'
import GameQuestion from '../components/GameQuestion'
import GameRanking from '../components/GameRanking'
import GameFinal from '../components/GameFinal'
import type { LobbyData, Player } from '../interfaces/game'
import type { QuizQuestion } from '../interfaces/gameExtra'

function useQuery() {
  return new URLSearchParams(useLocation().search)
}

const GameStep = {
  lobby: 0,
  countdown: 1,
  question: 2,
  answer_reveal: 3,
  ranking: 4,
  final: 5
} as const
type GameStepType = typeof GameStep[keyof typeof GameStep]

const GamePage: React.FC = () => {
  const { t } = useTranslation()
  const query = useQuery()
  const roomCode = query.get('roomCode') || ''
  const playerId = query.get('playerId') || ''

  const [step, setStep] = useState<GameStepType>(GameStep.countdown)
  const [current, setCurrent] = useState(0)
  const [, setAnswers] = useState<string[]>([])
  const [questions, setQuestions] = useState<QuizQuestion[]>([])
  const [players, setPlayers] = useState<Player[]>([])
  const [lobby, setLobby] = useState<LobbyData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [, setShowRanking] = useState(false)
  const [answerGiven, setAnswerGiven] = useState(false)
  const [showNext, setShowNext] = useState(false)
  const [serverTimeLeft, setServerTimeLeft] = useState<number | null>(null)
  const [localTimeLeft, setLocalTimeLeft] = useState<number | null>(null)
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null)

  useEffect(() => {
    const fetchGameData = async () => {
      setLoading(true)
      setError('')
      try {
        const qRes = await api.get(`/api/quiz/all-questions?roomCode=${roomCode}`)
        const questionsData = Array.isArray(qRes.data) ? qRes.data : [qRes.data]
        setQuestions(questionsData)
        const pRes = await api.get(`/api/game/players?roomCode=${roomCode}`)
        const playersData = Array.isArray(pRes.data) ? pRes.data : pRes.data.players
        setPlayers(playersData || [])
      } catch {
        setError('Veriler alınamadı.')
      } finally {
        setLoading(false)
      }
    }
    if (roomCode) fetchGameData().then(() => setStep(GameStep.countdown))
  }, [roomCode])

  const handleCountdownComplete = () => setStep(GameStep.question)

  const handleAnswer = async (answer: string, answerTime: number) => {
    setAnswers(prev => [...prev, answer])
    setAnswerGiven(true)
    setShowNext(false)
    setShowRanking(true)
    try {
      await api.post(`/api/game/answer`, {
        roomCode,
        playerId,
        answer,
        answerTime
      })
      const pRes = await api.get(`/api/game/players?roomCode=${roomCode}`)
      const playersData = Array.isArray(pRes.data) ? pRes.data : pRes.data.players
      setPlayers(playersData || [])
    } catch (e) {
      console.error('Error sending answer:', e)
    }
    setShowNext(true)
  }

  const handleNext = async () => {
    setAnswerGiven(false)
    setShowNext(false)
    setShowRanking(false)
    if (current < questions.length - 1) {
      try {
        await api.post(`/api/game/next?roomCode=${roomCode}&playerId=${playerId}`)
      } catch (e) {
        console.error('Error sending next:', e)
      }
    } else {
      setStep(GameStep.final)
    }
  }

  useEffect(() => {
    if (!roomCode || !playerId) return
    let isMounted = true
    const fetchState = async () => {
      try {
        const res = await api.get(`/api/game/state?roomCode=${roomCode}&playerId=${playerId}`)
        if (!isMounted) return
        setStep(typeof res.data.state === 'number' ? res.data.state : GameStep.lobby)
        setCurrent(res.data.currentQuestionIndex || 0)
        setServerTimeLeft(res.data.remainingTime)
        setLocalTimeLeft(res.data.remainingTime)
        if (Array.isArray(res.data.questions) && res.data.questions.length > 0) {
          setQuestions(res.data.questions)
        }
        if (Array.isArray(res.data.players) && res.data.players.length > 0) {
          setPlayers(res.data.players.map((p: Partial<Player> & { playerId?: string }) => ({
            ...p,
            id: p.playerId ?? p.id ?? '',
            name: p.name ?? '',
            score: p.score ?? 0
          })))
        } else if (Array.isArray(res.data.scores) && res.data.scores.length > 0) {
          setPlayers(res.data.scores.map((s: { playerId: string; name: string; score: number }) => ({
            id: s.playerId,
            name: s.name,
            score: s.score
          })))
        }
        if (res.data.lobby) {
          setLobby(res.data.lobby)
        }
      } catch {
        // Error handling
      }
    }
    fetchState()
    intervalRef.current = setInterval(() => {
      fetchState()
    }, 1000)
    return () => {
      isMounted = false
      if (intervalRef.current) clearInterval(intervalRef.current)
    }
  }, [roomCode, playerId])

  useEffect(() => {
    if (serverTimeLeft == null) return
    setLocalTimeLeft(serverTimeLeft)
    if (serverTimeLeft <= 0) return
    const timer = setInterval(() => {
      setLocalTimeLeft(prev => (prev && prev > 0 ? prev - 1 : 0))
    }, 1000)
    return () => clearInterval(timer)
  }, [serverTimeLeft])

  const sortedPlayers = [...players].sort((a, b) => (b.score ?? 0) - (a.score ?? 0)).map((p) => ({
    ...p,
    isYou: p.id === playerId || p.name === playerId,
  }))

  if (!roomCode || !playerId) {
    return (
      <div className="flex items-center gap-2 p-4 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300 rounded-lg max-w-lg mx-auto mt-6">
        <AlertCircle className="h-5 w-5" />
        <span>{t('error')}: roomCode/playerName missing</span>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center gap-2 p-4 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 rounded-lg max-w-lg mx-auto mt-6">
        <Loader2 className="h-5 w-5 animate-spin" />
        <span>{t('loading')}</span>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex items-center gap-2 p-4 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300 rounded-lg max-w-lg mx-auto mt-6">
        <AlertCircle className="h-5 w-5" />
        <span>{error}</span>
      </div>
    )
  }

  return (
    <div className="max-w-2xl mx-auto mt-6 px-4">
      {step === GameStep.answer_reveal && (
        <div className="mb-6">
          <GameRanking players={sortedPlayers} />
          {showNext && (
            <div className="mt-4 text-center">
              <Button onClick={handleNext} size="lg">
                Sonraki Soru
              </Button>
            </div>
          )}
        </div>
      )}

      {step === GameStep.countdown && <GameStartCountdown onComplete={handleCountdownComplete} />}

      {(step === GameStep.question || answerGiven) && (
        !questions[current] ? (
          <div className="flex items-center gap-2 p-4 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300 rounded-lg">
            <AlertCircle className="h-5 w-5" />
            <span>Soru bulunamadı (index: {current})</span>
          </div>
        ) : (
          <GameQuestion
            question={questions[current]?.english}
            options={questions[current]?.options}
            onAnswer={handleAnswer}
            timeLimit={lobby?.questionDuration ?? 60}
            timeLeft={localTimeLeft ?? 0}
            answered={step === GameStep.answer_reveal}
            correctMeaning={questions[current]?.correctMeaning}
            key={current}
          />
        )
      )}

      {step === GameStep.final && <GameFinal players={sortedPlayers} />}

      {step === GameStep.lobby && (
        <div className="flex items-center justify-center gap-2 p-4 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 rounded-lg">
          <Loader2 className="h-5 w-5 animate-spin" />
          <span>{t('gameStarting')}</span>
        </div>
      )}

      {step === GameStep.answer_reveal && !showNext && (
        <div className="flex items-center justify-center gap-2 p-4 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 rounded-lg">
          <Loader2 className="h-5 w-5 animate-spin" />
          <span>{t('loading')}</span>
        </div>
      )}
    </div>
  )
}

export default GamePage
