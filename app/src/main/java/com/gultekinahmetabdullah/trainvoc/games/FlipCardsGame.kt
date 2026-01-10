package com.gultekinahmetabdullah.trainvoc.games

import com.gultekinahmetabdullah.trainvoc.data.Word
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Flip Cards (Memory Match) Game Logic
 *
 * Classic memory game where players flip cards to find matching pairs.
 * Matches English words with their Turkish translations.
 */
@Singleton
class FlipCardsGame @Inject constructor(
    private val gamesDao: GamesDao
) {

    data class Card(
        val id: Int,
        val content: String,
        val type: CardType, // English or Turkish
        val wordId: Long, // Reference to the word
        val isFlipped: Boolean = false,
        val isMatched: Boolean = false
    )

    enum class CardType {
        ENGLISH, TURKISH
    }

    data class GameState(
        val cards: List<Card>,
        val gridSize: String, // "4x4", "4x6", "6x6"
        val flippedCards: List<Int> = emptyList(), // Indices of currently flipped cards
        val matchedPairs: Int = 0,
        val moves: Int = 0,
        val startTime: Long = System.currentTimeMillis(),
        val bestMoves: Int? = null
    ) {
        val totalPairs: Int
            get() = cards.size / 2

        val isComplete: Boolean
            get() = matchedPairs >= totalPairs

        val accuracy: Float
            get() = if (moves == 0) 100f
                    else (matchedPairs.toFloat() / moves) * 100f

        fun getCard(index: Int): Card? = cards.getOrNull(index)

        fun canFlipCard(index: Int): Boolean {
            val card = getCard(index) ?: return false
            return !card.isFlipped && !card.isMatched && flippedCards.size < 2
        }
    }

    /**
     * Start a new Flip Cards game
     * @param gridSize Grid size: "4x4" (8 pairs), "4x6" (12 pairs), "6x6" (18 pairs)
     */
    suspend fun startGame(
        gridSize: String = "4x4",
        difficulty: String = "medium"
    ): GameState {
        val pairCount = when (gridSize) {
            "4x4" -> 8
            "4x6" -> 12
            "6x6" -> 18
            else -> 8
        }

        val words = when (difficulty) {
            "easy" -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "A2",
                limit = pairCount
            )
            "medium" -> gamesDao.getWordsForGames(
                minLevel = "A2",
                maxLevel = "B1",
                limit = pairCount
            )
            "hard" -> gamesDao.getWordsForGames(
                minLevel = "B2",
                maxLevel = "C2",
                limit = pairCount
            )
            else -> gamesDao.getWordsForGames(
                minLevel = "A1",
                maxLevel = "B1",
                limit = pairCount
            )
        }

        // Create pairs of cards (English + Turkish)
        val cards = mutableListOf<Card>()
        words.forEachIndexed { index, word ->
            // English card
            cards.add(
                Card(
                    id = index * 2,
                    content = word.english,
                    type = CardType.ENGLISH,
                    wordId = word.id
                )
            )
            // Turkish card
            cards.add(
                Card(
                    id = index * 2 + 1,
                    content = word.turkish,
                    type = CardType.TURKISH,
                    wordId = word.id
                )
            )
        }

        // Shuffle cards
        val shuffledCards = cards.shuffled()

        // Get best moves for this grid size
        val bestMoves = gamesDao.getBestMoves(gridSize = gridSize)

        return GameState(
            cards = shuffledCards,
            gridSize = gridSize,
            bestMoves = bestMoves
        )
    }

    /**
     * Flip a card
     */
    fun flipCard(gameState: GameState, cardIndex: Int): GameState {
        if (!gameState.canFlipCard(cardIndex)) return gameState

        val updatedCards = gameState.cards.mapIndexed { index, card ->
            if (index == cardIndex) card.copy(isFlipped = true) else card
        }

        val newFlippedCards = gameState.flippedCards + cardIndex

        // Check for match if two cards are flipped
        if (newFlippedCards.size == 2) {
            val card1 = updatedCards[newFlippedCards[0]]
            val card2 = updatedCards[newFlippedCards[1]]

            val isMatch = card1.wordId == card2.wordId && card1.type != card2.type

            if (isMatch) {
                // Match found!
                val matchedCards = updatedCards.map { card ->
                    if (card.wordId == card1.wordId) {
                        card.copy(isMatched = true)
                    } else {
                        card
                    }
                }

                return gameState.copy(
                    cards = matchedCards,
                    flippedCards = emptyList(),
                    matchedPairs = gameState.matchedPairs + 1,
                    moves = gameState.moves + 1
                )
            }
        }

        return gameState.copy(
            cards = updatedCards,
            flippedCards = newFlippedCards,
            moves = if (newFlippedCards.size == 2) gameState.moves + 1 else gameState.moves
        )
    }

    /**
     * Hide unmatched flipped cards
     * Call this after showing two non-matching cards for a brief moment
     */
    fun hideUnmatchedCards(gameState: GameState): GameState {
        if (gameState.flippedCards.size != 2) return gameState

        val updatedCards = gameState.cards.map { card ->
            if (!card.isMatched) card.copy(isFlipped = false) else card
        }

        return gameState.copy(
            cards = updatedCards,
            flippedCards = emptyList()
        )
    }

    /**
     * Save game results to database
     */
    suspend fun saveGameResult(gameState: GameState) {
        if (!gameState.isComplete) return

        val timeSeconds = ((System.currentTimeMillis() - gameState.startTime) / 1000).toInt()

        // Save game session
        val session = GameSession(
            gameType = "flip_cards",
            difficulty = gameState.gridSize,
            totalQuestions = gameState.totalPairs,
            correctAnswers = gameState.matchedPairs,
            timeSeconds = timeSeconds,
            completed = true,
            completedAt = System.currentTimeMillis()
        )
        gamesDao.insertGameSession(session)

        // Save flip card stats
        val stats = FlipCardGameStats(
            gridSize = gameState.gridSize,
            totalPairs = gameState.totalPairs,
            moves = gameState.moves,
            timeSeconds = timeSeconds,
            completed = true,
            completedAt = System.currentTimeMillis()
        )
        gamesDao.insertFlipCardStats(stats)
    }

    /**
     * Check if this is a new best score
     */
    fun isNewBest(gameState: GameState): Boolean {
        return gameState.bestMoves == null || gameState.moves < gameState.bestMoves
    }
}
