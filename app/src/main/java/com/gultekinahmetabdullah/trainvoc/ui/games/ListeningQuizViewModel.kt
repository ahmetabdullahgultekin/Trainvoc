package com.gultekinahmetabdullah.trainvoc.ui.games

import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.games.ListeningQuizGame
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListeningQuizViewModel @Inject constructor(
    private val listeningQuizGame: ListeningQuizGame,
    private val gamificationDao: GamificationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListeningQuizUiState>(ListeningQuizUiState.Loading)
    val uiState: StateFlow<ListeningQuizUiState> = _uiState.asStateFlow()

    // TTS will be initialized in the UI layer
    private var textToSpeech: TextToSpeech? = null

    fun setTextToSpeech(tts: TextToSpeech) {
        textToSpeech = tts
    }

    fun startGame(difficulty: String = "medium") {
        viewModelScope.launch {
            try {
                _uiState.value = ListeningQuizUiState.Loading
                val gameState = listeningQuizGame.startGame(difficulty = difficulty)
                _uiState.value = ListeningQuizUiState.Playing(gameState)
            } catch (e: Exception) {
                _uiState.value = ListeningQuizUiState.Error(e.message ?: "Failed to start game")
            }
        }
    }

    fun playAudio() {
        val currentState = (_uiState.value as? ListeningQuizUiState.Playing)?.gameState ?: return
        val question = currentState.currentQuestion ?: return

        // Mark audio as played
        val newState = listeningQuizGame.markAudioPlayed(currentState)
        _uiState.value = ListeningQuizUiState.Playing(newState)

        // Play audio using TTS
        playTextToSpeech(question)
    }

    fun replayAudio() {
        val currentState = (_uiState.value as? ListeningQuizUiState.Playing)?.gameState ?: return
        val question = currentState.currentQuestion ?: return

        if (!currentState.canReplayAudio) return

        val newState = listeningQuizGame.replayAudio(currentState)
        _uiState.value = ListeningQuizUiState.Playing(newState)

        playTextToSpeech(question)
    }

    private fun playTextToSpeech(question: ListeningQuizGame.ListeningQuestion) {
        textToSpeech?.let { tts ->
            val (text, locale) = when (question.questionType) {
                ListeningQuizGame.QuestionType.WORD_TO_TRANSLATION,
                ListeningQuizGame.QuestionType.WORD_TO_SPELLING -> {
                    Pair(question.word.english, java.util.Locale.US)
                }
                ListeningQuizGame.QuestionType.TRANSLATION_TO_WORD -> {
                    Pair(question.word.turkish, java.util.Locale("tr", "TR"))
                }
            }

            tts.language = locale
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun selectAnswer(answer: String) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? ListeningQuizUiState.Playing)?.gameState ?: return@launch
            val question = currentState.currentQuestion ?: return@launch
            val isCorrect = listeningQuizGame.checkAnswer(question, answer)

            // Show feedback
            _uiState.value = ListeningQuizUiState.ShowingFeedback(
                gameState = currentState,
                selectedAnswer = answer,
                isCorrect = isCorrect
            )

            // Wait for feedback animation
            kotlinx.coroutines.delay(1000)

            // Move to next question
            val newGameState = listeningQuizGame.answerQuestion(currentState, answer)

            if (newGameState.isComplete) {
                listeningQuizGame.saveGameResult(newGameState)
                checkAchievements(newGameState)
                _uiState.value = ListeningQuizUiState.Complete(newGameState)
            } else {
                _uiState.value = ListeningQuizUiState.Playing(newGameState)
            }
        }
    }

    fun playAgain(difficulty: String = "medium") {
        startGame(difficulty)
    }

    private suspend fun checkAchievements(gameState: ListeningQuizGame.GameState) {
        // Check for perfect score
        if (gameState.correctAnswers == gameState.totalQuestions && gameState.totalQuestions >= 5) {
            unlockAchievement(Achievement.LISTENING_PERFECT)
        }

        // Check for completing without replays
        if (gameState.correctAnswers == gameState.totalQuestions && gameState.canReplay == 3) {
            unlockAchievement(Achievement.LISTENING_NO_REPLAY)
        }
    }

    private suspend fun unlockAchievement(achievement: Achievement) {
        try {
            gamificationDao.insertAchievement(
                com.gultekinahmetabdullah.trainvoc.gamification.UserAchievement(
                    achievementId = achievement.id,
                    progress = achievement.maxProgress,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            // Achievement already unlocked or error - ignore
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}

sealed class ListeningQuizUiState {
    object Loading : ListeningQuizUiState()
    data class Playing(val gameState: ListeningQuizGame.GameState) : ListeningQuizUiState()
    data class ShowingFeedback(
        val gameState: ListeningQuizGame.GameState,
        val selectedAnswer: String,
        val isCorrect: Boolean
    ) : ListeningQuizUiState()
    data class Complete(val gameState: ListeningQuizGame.GameState) : ListeningQuizUiState()
    data class Error(val message: String) : ListeningQuizUiState()
}
