package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R

@Composable
fun HomeScreen(
    onNavigateToQuiz: () -> Unit,
    onNavigateToStory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    // Lottie Animation State
    val composition by
    rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_diamond.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
    )

    // Button Scaling Animation
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val isScreenVisible = rememberUpdatedState(true)
    DisposableEffect(isScreenVisible.value) {
        onDispose {
            // Here you could pause Lottie if needed
        }
    }

    // Root container with background image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_3),
                contentScale = ContentScale.FillBounds
            )
            .padding(WindowInsets.systemBars.asPaddingValues())
            .testTag("HomeScreenRoot")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_icon_desc),
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp)
                    .testTag("AppLogo")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Message
            Text(
                text = stringResource(id = R.string.home_welcome),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.testTag("WelcomeText")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.home_subtitle),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .testTag("SubtitleText"),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Lottie Animation
            val lottieDesc = stringResource(id = R.string.lottie_desc)
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(200.dp)
                    .semantics {
                        contentDescription = lottieDesc
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Call-to-Action Button
            DebouncedButton(
                onClick = onNavigateToQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scaleAnim.value)
                    .testTag("QuizButton"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = stringResource(id = R.string.start_quiz), fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
            }
            DebouncedButton(
                onClick = onNavigateToStory,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scaleAnim.value)
                    .testTag("StoryButton"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text(text = stringResource(id = R.string.story_mode), fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeNavButton(
                    stringResource(id = R.string.settings),
                    Icons.Default.Settings,
                    onNavigateToSettings,
                    "SettingsButton"
                )
                HomeNavButton(
                    stringResource(id = R.string.help),
                    Icons.Default.Info,
                    onNavigateToHelp,
                    "HelpButton"
                )
                HomeNavButton(
                    stringResource(id = R.string.stats),
                    Icons.Default.CheckCircle,
                    onNavigateToStats,
                    "StatsButton"
                )
            }
        }
    }
}

@Composable
fun DebouncedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    colors: androidx.compose.material3.ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable () -> Unit
) {
    var isEnabled by remember { mutableStateOf(true) }
    Button(
        onClick = {
            if (isEnabled) {
                isEnabled = false
                onClick()
            }
        },
        enabled = isEnabled,
        modifier = modifier,
        shape = shape,
        colors = colors
    ) {
        content()
    }
    if (!isEnabled) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(500)
            isEnabled = true
        }
    }
}

// Reusable Navigation Button
@Composable
fun HomeNavButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(110.dp) // Biraz daha büyük kare
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(28.dp), // Daha yumuşak köşeler
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.85f
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .padding(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}