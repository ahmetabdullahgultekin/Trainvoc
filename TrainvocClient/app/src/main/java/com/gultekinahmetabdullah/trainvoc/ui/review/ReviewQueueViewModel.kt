package com.gultekinahmetabdullah.trainvoc.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.word.groupBySense
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsRating
import com.gultekinahmetabdullah.trainvoc.srs.domain.ISrsSchedulerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the Review Queue (design doc §4 / §9 S2).
 *
 * A session snapshots the due queue once via [ISrsSchedulerService.dueQueue] so
 * the ordered list stays stable while cards are rated (rating writes back to the
 * same rows). Each card is joined with its dictionary entry (lemma + senses) via
 * [WordDao]. Rating delegates to the shared [ISrsSchedulerService.rate] — the
 * same FSRS + persist path the quiz hook (S3) uses — then advances; the session
 * ends on a [ReviewUiState.Summary].
 *
 * The whole surface is reached only when `srs_engine_enabled` is on (the Home
 * entry is gated); reached with an empty schedule it simply shows [ReviewUiState.Empty].
 */
@HiltViewModel
class ReviewQueueViewModel @Inject constructor(
    private val srsScheduler: ISrsSchedulerService,
    private val wordDao: WordDao,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    /** wordIds still to review this session; [index] points at the current one. */
    private var queue: List<Long> = emptyList()
    private var index: Int = 0
    private var reviewedCount: Int = 0
    private var recalledCount: Int = 0

    init {
        startSession()
    }

    /** Snapshot the due queue and show the first card, or the empty state. */
    fun startSession() {
        _uiState.value = ReviewUiState.Loading
        viewModelScope.launch(dispatchers.io) {
            queue = srsScheduler.dueQueue().map { it.wordId }
            index = 0
            reviewedCount = 0
            recalledCount = 0
            showCurrentOrFinish()
        }
    }

    /** Reveal the back of the current card (meaning + senses). */
    fun reveal() {
        val current = _uiState.value
        if (current is ReviewUiState.Active && !current.isRevealed) {
            _uiState.value = current.copy(isRevealed = true)
        }
    }

    /**
     * Rate the current card (Again/Hard/Good/Easy), persist via FSRS, and advance.
     * Ignored unless a card is currently shown.
     */
    fun rateCard(rating: FsrsRating) {
        val current = _uiState.value
        if (current !is ReviewUiState.Active) return
        val wordId = current.card.wordId
        _uiState.value = ReviewUiState.Loading
        viewModelScope.launch(dispatchers.io) {
            srsScheduler.rate(wordId, rating)
            reviewedCount++
            if (rating != FsrsRating.AGAIN) recalledCount++
            index++
            showCurrentOrFinish()
        }
    }

    /**
     * Skip the current card without rating it — it stays due and reappears in a
     * later session (design doc §7 `skipCard`).
     */
    fun skipCard() {
        val current = _uiState.value
        if (current !is ReviewUiState.Active) return
        _uiState.value = ReviewUiState.Loading
        viewModelScope.launch(dispatchers.io) {
            index++
            showCurrentOrFinish()
        }
    }

    /**
     * Emit the next loadable card, skipping ids whose word row is gone, or the
     * session summary / empty state when the queue is exhausted.
     */
    private suspend fun showCurrentOrFinish() {
        while (index < queue.size) {
            val card = loadCard(queue[index])
            if (card != null) {
                _uiState.value = ReviewUiState.Active(
                    card = card,
                    isRevealed = false,
                    remaining = queue.size - index,
                    reviewedCount = reviewedCount
                )
                return
            }
            // Word row missing (e.g. deleted) — drop it and try the next.
            index++
        }
        _uiState.value = if (reviewedCount == 0) {
            ReviewUiState.Empty
        } else {
            ReviewUiState.Summary(ReviewSummary(reviewedCount, recalledCount))
        }
    }

    /** Join a scheduled wordId with its dictionary entry (lemma + senses). */
    private suspend fun loadCard(wordId: Long): ReviewCard? {
        val word = wordDao.getWordById(wordId) ?: return null
        val senses = wordDao.getTranslationsForWord(wordId).groupBySense().map { group ->
            ReviewSense(
                senseIndex = group.senseIndex,
                note = group.note,
                translations = group.translations.map { it.word }
            )
        }
        return ReviewCard(
            wordId = wordId,
            lemma = word.word,
            meaning = word.meaning,
            senses = senses
        )
    }
}
