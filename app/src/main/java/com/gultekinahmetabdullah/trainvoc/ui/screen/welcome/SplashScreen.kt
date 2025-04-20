package com.gultekinahmetabdullah.trainvoc.ui.screen.welcome

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = navController.context
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Lottie Animation State
    val composition by
    rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_rolling_cat.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 3,
        isPlaying = true,
        speed = 1f,
    )

    // Launch effect to navigate after animation
    LaunchedEffect(true) {
        delay(3000) // Wait for animation to finish
        val username = sharedPreferences.getString("username", null)
        val destination =
            if (username.isNullOrEmpty()) Route.WELCOME.name else Route.MAIN.name
        navController.navigate(destination)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_4), // Replace with your image resource
                contentScale = ContentScale.FillBounds
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                modifier = Modifier.size(96.dp),
                progress = { progress }
            )
        }
    }
}