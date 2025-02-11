package com.gultekinahmetabdullah.trainvoc.ui.screen

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.classes.Route
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = navController.context
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Lottie Animation State
    val composition by
    rememberLottieComposition(LottieCompositionSpec.Asset("json/anime_rolling_cat.json"))
    //val progress by animateLottieCompositionAsState(composition)

    // Launch effect to navigate after animation
    LaunchedEffect(true) {
        delay(3000) // Wait for animation to finish
        val username = sharedPreferences.getString("username", null)
        val destination =
            if (username.isNullOrEmpty()) Route.WELCOME.name else Route.MANAGEMENT.name
        navController.navigate(destination)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier.size(300.dp),
            iterations = 3,
        )
    }
}