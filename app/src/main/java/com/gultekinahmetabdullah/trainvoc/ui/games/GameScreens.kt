package com.gultekinahmetabdullah.trainvoc.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Common Game Screen Template
 * Base layout used by all game types
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenTemplate(
    title: String,
    onNavigateBack: () -> Unit,
    progress: Float,
    score: Int,
    timeRemaining: Int? = null,
    onPause: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (onPause != null) {
                        IconButton(onClick = onPause) {
                            Icon(Icons.Default.Pause, "Pause")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress bar and stats
            GameProgressBar(
                progress = progress,
                score = score,
                timeRemaining = timeRemaining
            )

            // Game content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun GameProgressBar(
    progress: Float,
    score: Int,
    timeRemaining: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            if (timeRemaining != null) {
                Text(
                    text = "Time: ${timeRemaining}s",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (timeRemaining < 10) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Multiple Choice Option Button
 */
@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean = false,
    isCorrect: Boolean? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isCorrect == true -> Color(0xFF10B981)
        isCorrect == false -> Color(0xFFEF4444)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        isCorrect != null -> Color.White
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Game Result Dialog
 */
@Composable
fun GameResultDialog(
    isComplete: Boolean,
    correctAnswers: Int,
    totalQuestions: Int,
    score: Int,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    if (isComplete) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = "🎉 Game Complete!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$correctAnswers / $totalQuestions",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score: $score",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val accuracy = if (totalQuestions > 0)
                        (correctAnswers.toFloat() / totalQuestions * 100).toInt()
                    else 0
                    Text(
                        text = "Accuracy: $accuracy%",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            confirmButton = {
                Button(onClick = onPlayAgain) {
                    Text("Play Again")
                }
            },
            dismissButton = {
                TextButton(onClick = onMainMenu) {
                    Text("Main Menu")
                }
            }
        )
    }
}

/**
 * Flip Card Component for Memory Game
 */
@Composable
fun FlipCard(
    content: String,
    isFlipped: Boolean,
    isMatched: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped || isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(enabled = !isMatched) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isMatched)
                Color(0xFF10B981)
            else
                MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isFlipped || isMatched) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

/**
 * Timer Display Component
 */
@Composable
fun TimerDisplay(
    timeRemaining: Int,
    modifier: Modifier = Modifier
) {
    val progress = (timeRemaining / 60f).coerceIn(0f, 1f)
    val color = when {
        timeRemaining < 10 -> Color(0xFFEF4444)
        timeRemaining < 30 -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(60.dp),
            color = color,
            strokeWidth = 6.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${timeRemaining}s",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Combo Display Animation
 */
@Composable
fun ComboDisplay(
    combo: Int,
    modifier: Modifier = Modifier
) {
    if (combo >= 3) {
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(combo) {
            visible = true
            delay(2000)
            visible = false
        }

        AnimatedVisibility(
            visible = visible,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Card(
                modifier = modifier,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF59E0B)
                )
            ) {
                Text(
                    text = "🔥 ${combo}x COMBO!",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Input Field for Spelling/Scramble Games
 */
@Composable
fun GameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Hint Button
 */
@Composable
fun HintButton(
    onClick: () -> Unit,
    hintsRemaining: Int,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = hintsRemaining > 0
    ) {
        Icon(Icons.Default.Lightbulb, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Hint ($hintsRemaining)")
    }
}

/**
 * Pause Dialog
 */
@Composable
fun PauseDialog(
    isPaused: Boolean,
    onResume: () -> Unit,
    onQuit: () -> Unit
) {
    if (isPaused) {
        AlertDialog(
            onDismissRequest = onResume,
            title = { Text("Game Paused") },
            text = { Text("Take a break! Resume when ready.") },
            confirmButton = {
                Button(onClick = onResume) {
                    Text("Resume")
                }
            },
            dismissButton = {
                TextButton(onClick = onQuit) {
                    Text("Quit")
                }
            }
        )
    }
}

/**
 * Achievement Unlocked Popup
 */
@Composable
fun AchievementPopup(
    achievementName: String?,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(achievementName) {
        if (achievementName != null) {
            visible = true
            delay(3000)
            visible = false
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible && achievementName != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF59E0B)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Stars,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                Column {
                    Text(
                        text = "Achievement Unlocked!",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                    Text(
                        text = achievementName ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Loading State
 */
@Composable
fun GameLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading game...",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * Difficulty Selection Dialog
 */
@Composable
fun DifficultySelectionDialog(
    onDifficultySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Difficulty") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DifficultyOption("Easy", "A1-A2 words", onClick = { onDifficultySelected("easy") })
                DifficultyOption("Medium", "A2-B1 words", onClick = { onDifficultySelected("medium") })
                DifficultyOption("Hard", "B2-C2 words", onClick = { onDifficultySelected("hard") })
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DifficultyOption(
    level: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = level,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
