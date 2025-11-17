import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

@Composable
fun QuizMenuScreen(onQuizSelected: (Quiz) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_2),
                contentScale = ContentScale.FillBounds,
                alpha = Alpha.surfaceLight
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.mediumLarge),
        ) {
            Text(
                text = stringResource(id = R.string.select_quiz_type),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = Spacing.large)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(Quiz.quizTypes.size) { index ->
                    val quiz = Quiz.quizTypes[index]
                    AnimatedQuizCard(
                        title = stringResource(
                            id = when (quiz.name) {
                                "Multiple Choice" -> R.string.quiz_multiple_choice
                                "True/False" -> R.string.quiz_true_false
                                "Matching" -> R.string.quiz_matching
                                else -> R.string.quiz_generic
                            }, quiz.name
                        ),
                        description = stringResource(
                            id = when (quiz.name) {
                                "Multiple Choice" -> R.string.quiz_multiple_choice_desc
                                "True/False" -> R.string.quiz_true_false_desc
                                "Matching" -> R.string.quiz_matching_desc
                                else -> R.string.quiz_generic_desc
                            }, quiz.description
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onQuizSelected(quiz) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedQuizCard(title: String, description: String, color: Color, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.05f else 1f,
        animationSpec = tween(200),
        label = ""
    )
    val animatedElevation by animateFloatAsState(
        targetValue = if (pressed) 14f else 6f,
        animationSpec = tween(200),
        label = ""
    )
    val animatedColor by animateColorAsState(
        targetValue = if (pressed) color.copy(alpha = 0.97f) else color,
        animationSpec = tween(200),
        label = ""
    )
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
            .padding(Spacing.extraSmall)
            .shadow(animatedElevation.dp, RoundedCornerShape(18.dp))
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = animatedColor)
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.mediumLarge)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
        }
    }
}