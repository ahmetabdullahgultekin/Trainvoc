package com.gultekinahmetabdullah.trainvoc.ui.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsRating

/**
 * Review Queue (design doc §4 / §9 S2): a flip-card session over the words that
 * are due today. Front shows the lemma; tapping reveals the meaning + senses;
 * the four FSRS ratings persist the card and advance. Ends on a session summary;
 * shows an "all caught up" empty state when nothing is due.
 *
 * Reachable only while `srs_engine_enabled` is on — the Home entry point is gated.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewQueueScreen(
    onExit: () -> Unit,
    viewModel: ReviewQueueViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.review_queue_title)) },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.review_close_desc)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val current = state) {
                is ReviewUiState.Loading -> CircularProgressIndicator()

                is ReviewUiState.Empty -> EmptyState()

                is ReviewUiState.Summary -> SummaryState(
                    summary = current.summary,
                    onDone = onExit
                )

                is ReviewUiState.Active -> ActiveCard(
                    state = current,
                    onReveal = viewModel::reveal,
                    onRate = viewModel::rateCard,
                    onSkip = viewModel::skipCard
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "🎉", style = MaterialTheme.typography.displayMedium)
        Text(
            text = stringResource(R.string.review_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.review_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SummaryState(summary: ReviewSummary, onDone: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "✅", style = MaterialTheme.typography.displaySmall)
        Text(
            text = stringResource(R.string.review_summary_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.review_summary_reviewed, summary.reviewedCount),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(
                R.string.review_summary_retention,
                (summary.retentionRate * 100).toInt()
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onDone, modifier = Modifier.heightIn(min = 48.dp)) {
            Text(stringResource(R.string.review_summary_done))
        }
    }
}

@Composable
private fun ActiveCard(
    state: ReviewUiState.Active,
    onReveal: () -> Unit,
    onRate: (FsrsRating) -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress: how many cards remain in this session.
        LinearProgressIndicator(
            progress = { progressFraction(state) },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.review_progress_remaining, state.remaining),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FlipCard(
            card = state.card,
            isRevealed = state.isRevealed,
            onClick = onReveal,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        if (state.isRevealed) {
            RatingBar(onRate = onRate)
        } else {
            Button(
                onClick = onReveal,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(stringResource(R.string.review_show_answer))
            }
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(stringResource(R.string.review_skip))
            }
        }
    }
}

@Composable
private fun FlipCard(
    card: ReviewCard,
    isRevealed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val frontDesc = stringResource(R.string.review_card_front_desc, card.lemma)
    val backDesc = stringResource(R.string.review_card_back_desc, card.lemma)
    Card(
        modifier = modifier
            .clickable(enabled = !isRevealed, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = if (isRevealed) backDesc else frontDesc
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = card.lemma,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            if (isRevealed) {
                Spacer(Modifier.size(16.dp))
                Text(
                    text = card.meaning,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                card.senses.forEach { sense ->
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = sense.translations.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Spacer(Modifier.size(12.dp))
                Text(
                    text = stringResource(R.string.review_tap_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RatingBar(onRate: (FsrsRating) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RatingButton(
            label = stringResource(R.string.review_rating_again),
            container = MaterialTheme.colorScheme.errorContainer,
            content = MaterialTheme.colorScheme.onErrorContainer,
            onClick = { onRate(FsrsRating.AGAIN) },
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = stringResource(R.string.review_rating_hard),
            container = MaterialTheme.colorScheme.tertiaryContainer,
            content = MaterialTheme.colorScheme.onTertiaryContainer,
            onClick = { onRate(FsrsRating.HARD) },
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = stringResource(R.string.review_rating_good),
            container = MaterialTheme.colorScheme.secondaryContainer,
            content = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = { onRate(FsrsRating.GOOD) },
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = stringResource(R.string.review_rating_easy),
            container = MaterialTheme.colorScheme.primaryContainer,
            content = MaterialTheme.colorScheme.onPrimaryContainer,
            onClick = { onRate(FsrsRating.EASY) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RatingButton(
    label: String,
    container: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content
        )
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

/** Fraction of the session already completed, for the top progress bar. */
private fun progressFraction(state: ReviewUiState.Active): Float {
    val total = state.reviewedCount + state.remaining
    return if (total > 0) state.reviewedCount.toFloat() / total else 0f
}
