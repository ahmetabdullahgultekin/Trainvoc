import { useEffect, useRef, useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation } from 'react-router-dom'
import { AlertCircle, Loader2, Wifi, WifiOff } from 'lucide-react'
import api from '../api'
import { Button } from '@/components/ui/button'
import GameStartCountdown from '../components/GameStartCountdown'
import GameQuestion from '../components/GameQuestion'
import GameRanking from '../components/GameRanking'
import GameFinal from '../components/GameFinal'
import { useWebSocket } from '../hooks/useWebSocket'
import type { LobbyData, Player } from '../interfaces/game'
import type { QuizQuestion } from '../interfaces/gameExtra'
import type { WebSocketMessage, PlayerRanking } from '../services/WebSocketService'

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

  const [step, setStep] = useState<GameStepType>(GameStep.lobby)
  const [current, setCurrent] = useState(0)
  const [questions, setQuestions] = useState<QuizQuestion[]>([])
  const [players, setPlayers] = useState<Player[]>([])
  const [lobby, setLobby] = useState<LobbyData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [answerGiven, setAnswerGiven] = useState(false)
  const [showNext, setShowNext] = useState(false)
  const [localTimeLeft, setLocalTimeLeft] = useState<number>(0)
  const [lastAnswerResult, setLastAnswerResult] = useState<{ correct: boolean; scoreChange: number } | null>(null)
  const initialFetchDone = useRef(false)
  const answerStartTime = useRef<number>(Date.now())

  // WebSocket event handlers
  const handleGameStateChanged = useCallback((state: number, remainingTime: number) => {
    console.log('Game state changed:', state, 'remainingTime:', remainingTime)
    setStep(state as GameStepType)
    setLocalTimeLeft(remainingTime)

    // Reset answer state on new question
    if (state === GameStep.question || state === GameStep.countdown) {
      setAnswerGiven(false)
      setShowNext(false)
      setLastAnswerResult(null)
      answerStartTime.current = Date.now()
    }
  }, [])

  const handleQuestion = useCallback((question: { text: string; options: string[] }, questionIndex: number) => {
    console.log('Question received:', questionIndex, question.text)
    setCurrent(questionIndex)
    answerStartTime.current = Date.now()
  }, [])

  const handleAnswerResult = useCallback((correct: boolean, _correctIndex: number, score: number) => {
    console.log('Answer result:', correct, 'score:', score)
    setLastAnswerResult({ correct, scoreChange: score })
    setAnswerGiven(true)
    setShowNext(true)
  }, [])

  const handleRankings = useCallback((rankingPlayers: PlayerRanking[]) => {
    console.log('Rankings received:', rankingPlayers)
    setPlayers(rankingPlayers.map(p => ({
      id: p.id,
      name: p.name,
      avatarId: p.avatarId,
      score: p.score
    })))
  }, [])

  const handleGameEnded = useCallback((finalRankings: PlayerRanking[]) => {
    console.log('Game ended:', finalRankings)
    setStep(GameStep.final)
    setPlayers(finalRankings.map(p => ({
      id: p.id,
      name: p.name,
      avatarId: p.avatarId,
      score: p.score
    })))
  }, [])

  const handlePlayersUpdate = useCallback((updatedPlayers: Player[]) => {
    setPlayers(updatedPlayers)
  }, [])

  const handleMessage = useCallback((message: WebSocketMessage) => {
    // Handle questions array from start game
    if (message.type === 'questions' && Array.isArray(message.questions)) {
      const qs = message.questions as Array<{
        index: number
        text: string
        english: string
        options: string[]
        correctMeaning: string
      }>
      setQuestions(qs.map(q => ({
        english: q.english || q.text,
        correctMeaning: q.correctMeaning,
        options: q.options
      })))
    }

    // Handle question index update
    if (message.type === 'questionIndex' && typeof message.index === 'number') {
      setCurrent(message.index)
    }
  }, [])

  const handleError = useCallback((errorMsg: string) => {
    console.error('WebSocket error:', errorMsg)
    setError(errorMsg)
  }, [])

  // Initialize WebSocket connection
  const { connectionState, isConnected, connect, submitAnswer } = useWebSocket({
    autoConnect: true,
    handlers: {
      onGameStateChanged: handleGameStateChanged,
      onQuestion: handleQuestion,
      onAnswerResult: handleAnswerResult,
      onRankings: handleRankings,
      onGameEnded: handleGameEnded,
      onPlayersUpdate: handlePlayersUpdate,
      onMessage: handleMessage,
      onError: handleError,
    },
  })

  // Initial data fetch (one-time)
  useEffect(() => {
    if (!roomCode || !playerId) return
    if (initialFetchDone.current) return
    initialFetchDone.current = true

    const fetchGameData = async () => {
      setLoading(true)
      setError('')
      try {
        // Fetch questions
        const qRes = await api.get(`/api/quiz/all-questions?roomCode=${roomCode}`)
        const questionsData = Array.isArray(qRes.data) ? qRes.data : [qRes.data]
        setQuestions(questionsData)

        // Fetch players
        const pRes = await api.get(`/api/game/players?roomCode=${roomCode}`)
        const playersData = Array.isArray(pRes.data) ? pRes.data : pRes.data.players
        setPlayers(playersData || [])

        // Fetch current state
        const sRes = await api.get(`/api/game/state?roomCode=${roomCode}&playerId=${playerId}`)
        setStep(typeof sRes.data.state === 'number' ? sRes.data.state : GameStep.lobby)
        setCurrent(sRes.data.currentQuestionIndex || 0)
        setLocalTimeLeft(sRes.data.remainingTime || 0)

        if (sRes.data.lobby) {
          setLobby(sRes.data.lobby)
        }
      } catch {
        setError('Could not fetch game data.')
      } finally {
        setLoading(false)
      }
    }

    fetchGameData()
  }, [roomCode, playerId])

  // Ensure WebSocket is connected
  useEffect(() => {
    if (connectionState === 'disconnected') {
      connect()
    }
  }, [connectionState, connect])

  // Local timer countdown (synced with server updates)
  useEffect(() => {
    if (step !== GameStep.question && step !== GameStep.countdown) return
    if (localTimeLeft <= 0) return

    const timer = setInterval(() => {
      setLocalTimeLeft(prev => (prev > 0 ? prev - 1 : 0))
    }, 1000)

    return () => clearInterval(timer)
  }, [step, localTimeLeft])

  const handleCountdownComplete = useCallback(() => {
    setStep(GameStep.question)
    answerStartTime.current = Date.now()
  }, [])

  const handleAnswer = useCallback(async (answer: string) => {
    if (answerGiven) return

    const answerTime = Math.floor((Date.now() - answerStartTime.current) / 1000)
    const currentQuestion = questions[current]
    const answerIndex = currentQuestion?.options?.indexOf(answer) ?? -1
    const isCorrect = answer === currentQuestion?.correctMeaning

    setAnswerGiven(true)

    // Submit via WebSocket
    submitAnswer(roomCode, playerId, answerIndex, answerTime)

    // Also send via REST for backward compatibility and to update server state
    try {
      const optionCount = currentQuestion?.options?.length || 4
      const optionPickRate = 1 / optionCount

      await api.post(`/api/game/answer`, {
        roomCode,
        playerId,
        answer,
        answerTime,
        isCorrect,
        optionPickRate
      })

      // Fetch updated players
      const pRes = await api.get(`/api/game/players?roomCode=${roomCode}`)
      const playersData = Array.isArray(pRes.data) ? pRes.data : pRes.data.players
      setPlayers(playersData || [])
    } catch (e) {
      console.error('Error sending answer:', e)
    }

    setShowNext(true)
  }, [answerGiven, questions, current, roomCode, playerId, submitAnswer])

  const handleNext = useCallback(async () => {
    setAnswerGiven(false)
    setShowNext(false)
    setLastAnswerResult(null)

    if (current < questions.length - 1) {
      try {
        await api.post(`/api/game/next?roomCode=${roomCode}&playerId=${playerId}`)
      } catch (e) {
        console.error('Error sending next:', e)
      }
    } else {
      setStep(GameStep.final)
    }
  }, [current, questions.length, roomCode, playerId])

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
      {/* Connection status */}
      <div className="flex justify-end mb-2">
        {isConnected ? (
          <div className="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
            <Wifi className="h-3 w-3" />
            <span>Connected</span>
          </div>
        ) : (
          <div className="flex items-center gap-1 text-xs text-red-600 dark:text-red-400">
            <WifiOff className="h-3 w-3" />
            <span>Disconnected</span>
          </div>
        )}
      </div>

      {step === GameStep.answer_reveal && (
        <div className="mb-6">
          <GameRanking players={sortedPlayers} />
          {lastAnswerResult && (
            <div className={`mt-4 p-3 rounded-lg text-center ${lastAnswerResult.correct ? 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300' : 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300'}`}>
              {lastAnswerResult.correct ? 'Correct!' : 'Wrong!'} {lastAnswerResult.scoreChange > 0 ? '+' : ''}{lastAnswerResult.scoreChange} points
            </div>
          )}
          {showNext && (
            <div className="mt-4 text-center">
              <Button onClick={handleNext} size="lg">
                {current < questions.length - 1 ? 'Next Question' : 'See Results'}
              </Button>
            </div>
          )}
        </div>
      )}

      {step === GameStep.countdown && <GameStartCountdown onComplete={handleCountdownComplete} />}

      {(step === GameStep.question || (answerGiven && step !== GameStep.answer_reveal && step !== GameStep.final)) && (
        !questions[current] ? (
          <div className="flex items-center gap-2 p-4 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300 rounded-lg">
            <AlertCircle className="h-5 w-5" />
            <span>Question not found (index: {current})</span>
          </div>
        ) : (
          <GameQuestion
            question={questions[current]?.english}
            options={questions[current]?.options}
            onAnswer={handleAnswer}
            timeLimit={lobby?.questionDuration ?? 60}
            timeLeft={localTimeLeft}
            answered={answerGiven}
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

      {step === GameStep.ranking && (
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-center mb-4">Final Rankings</h2>
          <GameRanking players={sortedPlayers} />
        </div>
      )}
    </div>
  )
}

export default GamePage
