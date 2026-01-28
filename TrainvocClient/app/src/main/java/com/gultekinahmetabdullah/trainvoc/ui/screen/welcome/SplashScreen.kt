package com.gultekinahmetabdullah.trainvoc.ui.screen.welcome

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.config.SplashConfig
import com.gultekinahmetabdullah.trainvoc.ui.theme.Alpha
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    onPreload: (LottieCompositionResult, Painter) -> Unit = { _, _ -> }
) {
    val context = navController.context
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Splash Lottie
    val splashComposition by rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_rolling_cat.json"))
    val splashProgress by animateLottieCompositionAsState(
        splashComposition,
        iterations = 3,
        isPlaying = true,
        speed = 1f,
    )

    // HomeScreen için preload
    val homeLottieComposition =
        rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_diamond.json"))
    val homeBgPainter = painterResource(id = R.drawable.bg_3)
    // Preload callback
    LaunchedEffect(homeLottieComposition.value, homeBgPainter) {
        if (homeLottieComposition.value != null) {
            onPreload(homeLottieComposition, homeBgPainter)
        }
    }

    // Track whether navigation has already happened (to prevent double-navigate)
    var hasNavigated by remember { mutableStateOf(false) }

    val username = remember { sharedPreferences.getString("username", null) }
    val isReturningUser = !username.isNullOrEmpty()
    val destination = if (isReturningUser) Route.MAIN else Route.WELCOME

    // Navigate helper (prevents double navigation)
    val navigateAway: () -> Unit = remember(destination) {
        {
            if (!hasNavigated) {
                hasNavigated = true
                navController.navigate(destination)
            }
        }
    }

    // Launch effect to navigate after animation (auto-skip timer)
    LaunchedEffect(true) {
        // Shorter splash for returning users, full animation for new users
        val splashDuration = if (isReturningUser) SplashConfig.RETURNING_USER_DURATION_MS else SplashConfig.NEW_USER_DURATION_MS
        delay(splashDuration)
        navigateAway()
    }

    // Tap-to-skip splash (fixes #215)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { navigateAway() }
    ) {
        SplashAnimatedBackground()
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Responsive size: 20% of min dimension, clamped between 80dp and 160dp
            val minDimension = minOf(maxWidth, maxHeight)
            val animationSize = (minDimension * 0.2f).coerceIn(80.dp, 160.dp)

            LottieAnimation(
                composition = splashComposition,
                modifier = Modifier.size(animationSize),
                progress = { splashProgress }
            )
        }
    }
}

// Splashscreen için yavaş ve pastel renk geçişli arka plan animasyonu
@Composable
fun SplashAnimatedBackground(
    modifier: Modifier = Modifier,
    duration: Int = 3000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splashBg")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1080f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "spiralAngle"
    )
    val color1 by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary.copy(alpha = Alpha.surfaceMedium),
        targetValue = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f),
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "splashBg1"
    )
    val color2 by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
        targetValue = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f),
        animationSpec = infiniteRepeatable(
            animation = tween(duration + 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "splashBg2"
    )
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(color1, color2)
                )
            )
            .fillMaxSize()
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val center = this.center
            val spiralCount = 3
            val maxRadius = size.minDimension * 0.45f
            for (i in 0 until spiralCount) {
                val spiralColor = when (i) {
                    0 -> color1.copy(alpha = 0.25f)
                    1 -> color2.copy(alpha = Alpha.surfaceMedium)
                    else -> color1.copy(alpha = Alpha.surfaceLight)
                }
                val spiralAngle = angle + i * 120f
                drawSpiral(
                    center = center,
                    radius = maxRadius - i * 40f,
                    color = spiralColor,
                    angle = spiralAngle,
                    turns = 2 + i
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSpiral(
    center: androidx.compose.ui.geometry.Offset,
    radius: Float,
    color: androidx.compose.ui.graphics.Color,
    angle: Float,
    turns: Int = 3
) {
    val points = 200
    val spiralPath = androidx.compose.ui.graphics.Path()
    for (i in 0..points) {
        val t = (1f - i / points.toFloat()) * (turns * 2 * Math.PI) // yön tersine çevrildi
        val r = radius * (i / points.toFloat())
        val x = center.x + r * Math.cos(t + Math.toRadians(angle.toDouble())).toFloat()
        val y = center.y + r * Math.sin(t + Math.toRadians(angle.toDouble())).toFloat()
        if (i == 0) spiralPath.moveTo(x, y) else spiralPath.lineTo(x, y)
    }
    drawPath(
        path = spiralPath,
        color = color,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 12f)
    )
}