package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Question card displaying the word to be defined.
 * Updated to match UI/UX Improvement Plan specifications:
 * - Surface with elevation 2
 * - Padding: 24dp
 * - Corner radius: 16dp
 * - Typography: headlineMedium for question word
 */
@Composable
fun QuizQuestionCard(word: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.large),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = Elevation.level2
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.which_one_is_the_correct_meaning),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = word,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
