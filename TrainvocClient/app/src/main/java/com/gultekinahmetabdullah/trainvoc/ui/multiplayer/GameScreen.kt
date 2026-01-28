package com.gultekinahmetabdullah.trainvoc.ui.multiplayer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.PlayerInfo
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.QuestionInfo
import com.gultekinahmetabdullah.trainvoc.multiplayer.websocket.GameState
import com.gultekinahmetabdullah.trainvoc.ui.components.RollingCatLoaderWithText
import com.gultekinahmetabdullah.trainvoc.ui.components.LoaderSize

/**
 * Game Screen - Active multiplayer quiz gameplay.
 *
 * Shows:
 * - Countdown before game starts
 * - Questions with multiple choice answers
 * - Timer and score
 * - Answer reveal and rankings between questions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    roomCode: String,
    gameState: GameState,
    currentQuestion: QuestionInfo?,
    timeRemaining: Int,
    players: List<PlayerInfo>,
    selectedAnswer: Int?,
    onSubmitAnswer: (Int) -> Unit,
    onLeaveGame: () -> Unit,
    onGameEnded: () -> Unit
) {
    // Navigate when game ends
    LaunchedEffect(gameState) {
        if (gameState == GameState.FINAL) {
            onGameEnded()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Room: $roomCode",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
                    IconButton(onClick = onLeaveGame) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Leave game")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (gameState) {
                GameState.COUNTDOWN -> {
                    CountdownScreen(timeRemaining = timeRemaining)
                }
                GameState.QUESTION -> {
                    QuestionScreen(
                        question = currentQuestion,
                        timeRemaining = timeRemaining,
                        selectedAnswer = selectedAnswer,
                        onAnswerSelected = onSubmitAnswer
                    )
                }
                GameState.ANSWER_REVEAL -> {
                    AnswerRevealScreen(
                        question = currentQuestion,
                        selectedAnswer = selectedAnswer
                    )
                }
                GameState.RANKING -> {
                    RankingScreen(players = players)
                }
                else -> {
                    // Loading or waiting state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        RollingCatLoaderWithText(
                            message = "Loading...",
                            size = LoaderSize.medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownScreen(timeRemaining: Int) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = repeatable(
            iterations = 1,
            animation = tween(900, easing = FastOutSlowInEasing)
        ),
        label = "countdown_scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Get Ready!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = timeRemaining.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.scale(scale)
            )
        }
    }
}

@Composable
private fun QuestionScreen(
    question: QuestionInfo?,
    timeRemaining: Int,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    if (question == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            RollingCatLoaderWithText(
                message = "Loading question...",
                size = LoaderSize.medium
            )
        }
        return
    }

    val timerColor by animateColorAsState(
        targetValue = when {
            timeRemaining <= 5 -> MaterialTheme.colorScheme.error
            timeRemaining <= 10 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        },
        label = "timer_color"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Timer Bar
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = timerColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${timeRemaining}s",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = timerColor
                    )
                }
                Text(
                    text = "Question ${question.index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LinearProgressIndicator(
                progress = { timeRemaining / 30f }, // Assuming 30s max
                modifier = Modifier.fillMaxWidth(),
                color = timerColor,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Question
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Answer Options
        val answerColors = listOf(
            Color(0xFFE53935), // Red
            Color(0xFF1E88E5), // Blue
            Color(0xFFFDD835), // Yellow
            Color(0xFF43A047)  // Green
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswer == index
                val buttonColor = answerColors.getOrElse(index) { MaterialTheme.colorScheme.primary }

                Button(
                    onClick = { onAnswerSelected(index) },
                    enabled = selectedAnswer == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected)
                            buttonColor.copy(alpha = 0.7f)
                        else
                            buttonColor,
                        disabledContainerColor = if (isSelected)
                            buttonColor.copy(alpha = 0.5f)
                        else
                            buttonColor.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        if (selectedAnswer != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Answer submitted! Waiting for others...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AnswerRevealScreen(
    question: QuestionInfo?,
    selectedAnswer: Int?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Answer Reveal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (question != null) {
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Show correct answer (would need to be provided by server)
                Text(
                    text = "Your answer: ${question.options.getOrNull(selectedAnswer ?: -1) ?: "None"}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RollingCatLoaderWithText(
                message = "Loading rankings...",
                size = LoaderSize.small
            )
        }
    }
}

@Composable
private fun RankingScreen(players: List<PlayerInfo>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Scoreboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        players.sortedByDescending { it.score }.forEachIndexed { index, player ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (index) {
                        0 -> MaterialTheme.colorScheme.primaryContainer
                        1 -> MaterialTheme.colorScheme.secondaryContainer
                        2 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${index + 1}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // Avatar emoji (fixes #207)
                    Text(
                        text = getAvatarEmoji(player.avatarId),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${player.score} pts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Next question coming up...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Map avatar ID to emoji for multiplayer player display */
private fun getAvatarEmoji(avatarId: Int): String {
    val avatars = listOf("🦊", "🐱", "🐶", "🐻", "🐼", "🐨", "🦁", "🐯", "🐸", "🦉",
        "🐺", "🦄", "🐲", "🦅", "🐧", "🐙", "🦋", "🌟", "🎯", "🚀")
    return avatars.getOrElse(avatarId % avatars.size) { "🦊" }
}
