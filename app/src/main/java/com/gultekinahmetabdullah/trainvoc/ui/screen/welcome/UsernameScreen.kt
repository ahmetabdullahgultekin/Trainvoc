package com.gultekinahmetabdullah.trainvoc.ui.screen.welcome

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import java.util.Locale

@Composable
fun UsernameScreen(navController: NavController) {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    val context = navController.context
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    var selectedLanguage by remember {
        mutableStateOf(sharedPreferences.getString("language", "tr") ?: "tr")
    }
    val activity = context as? Activity

    val composition by
    rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_typing.json"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lottie Animation
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(400.dp)
        )

        Text(
            text = stringResource(id = R.string.username_prompt),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.your_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language selection
        val languageOptions = listOf("tr", "en")
        if (selectedLanguage.isBlank()) selectedLanguage = "tr"
        Text(text = stringResource(id = R.string.choose_language))
        Row {
            RadioButton(
                selected = selectedLanguage == "tr",
                onClick = { selectedLanguage = "tr" }
            )
            Text(
                text = stringResource(id = R.string.turkish),
                modifier = Modifier.padding(end = 16.dp)
            )
            RadioButton(
                selected = selectedLanguage == "en",
                onClick = { selectedLanguage = "en" }
            )
            Text(text = stringResource(id = R.string.english))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.text.isNotBlank()) {
                    sharedPreferences.edit { putString("username", username.text) }
                    sharedPreferences.edit { putString("language", selectedLanguage) }
                    // Set locale
                    val locale = Locale(selectedLanguage.ifBlank { "tr" })
                    Locale.setDefault(locale)
                    val res = context.resources
                    val config = Configuration(res.configuration)
                    config.setLocale(locale)
                    res.updateConfiguration(config, res.displayMetrics)
                    activity?.recreate()
                    navController.navigate(Route.MAIN)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.continue_text))
        }
    }
}
