package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.quiz.AvailableQuizCategory
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter

@Composable
fun QuizExamMenuScreen(onExamSelected: (QuizParameter) -> Unit) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .paint(
                painter = painterResource(id = R.drawable.bg_2),
                contentScale = ContentScale.Crop,
                alpha = 0.15f
            )
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.select_quiz_category),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            val categories = AvailableQuizCategory.getAll()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories.size) { index ->
                    val cat = categories[index]
                    val color =
                        if (cat.color == Color.Unspecified) MaterialTheme.colorScheme.primary else cat.color
                    AnimatedQuizCategoryCard(
                        title = stringResource(id = cat.titleRes),
                        description = stringResource(
                            id = cat.descriptionRes,
                            stringResource(id = cat.descriptionArgRes)
                        ),
                        color = color,
                        onClick = { onExamSelected(cat.onClickParam) },
                        enabled = cat.enabled
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedQuizCategoryCard(
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 1.07f else 1f,
        animationSpec = tween(200),
        label = ""
    )
    val animatedElevation by animateFloatAsState(
        targetValue = if (pressed && enabled) 16f else 6f,
        animationSpec = tween(200),
        label = ""
    )
    val disabledAlpha = 0.45f
    val disabledColor = color.copy(alpha = disabledAlpha)
    val cardColor = if (enabled) color else disabledColor
    val textColor =
        if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.5f
        )
    val borderColor =
        if (enabled) Color.Transparent else MaterialTheme.colorScheme.error.copy(alpha = 0.7f)

    Card(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .aspectRatio(1f) // Kartlar kare olacak
            .padding(vertical = 8.dp)
            .then(if (!enabled) Modifier.alpha(0.7f) else Modifier),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(animatedElevation.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = if (!enabled) BorderStroke(2.dp, borderColor) else null,
        enabled = enabled,
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Tüm içerik ortalı
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (!enabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.coming_soon),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}