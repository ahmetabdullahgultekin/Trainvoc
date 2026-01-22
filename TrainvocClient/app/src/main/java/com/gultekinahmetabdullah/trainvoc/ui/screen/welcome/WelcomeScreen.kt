package com.gultekinahmetabdullah.trainvoc.ui.screen.welcome

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel

@Composable
fun WelcomeScreen(
    navController: NavController,
    scaffoldPadding: PaddingValues,
    settingsViewModel: SettingsViewModel
) {
    val composition by
    rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_man_greetings.json"))

    val context = navController.context
    val selectedLanguage by settingsViewModel.language.collectAsState()
    val activity = context as? Activity

    fun onLanguageSelected(lang: LanguagePreference) {
        settingsViewModel.setLanguage(lang)
        // Manually recreate activity for WelcomeScreen since the languageChanged
        // flow listener in SettingsScreen may not be active during onboarding
        activity?.recreate()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = stringResource(id = R.string.welcome_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(horizontal = Spacing.mediumLarge)
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        // Lottie Animation
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(260.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.large))

        // Language selection
        Text(
            text = stringResource(id = R.string.choose_language),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(Spacing.small))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedLanguage == LanguagePreference.TURKISH,
                onClick = { onLanguageSelected(LanguagePreference.TURKISH) }
            )
            Text(
                text = stringResource(id = R.string.turkish),
                modifier = Modifier.padding(end = Spacing.mediumLarge),
                style = MaterialTheme.typography.bodyMedium
            )
            RadioButton(
                selected = selectedLanguage == LanguagePreference.ENGLISH,
                onClick = { onLanguageSelected(LanguagePreference.ENGLISH) }
            )
            Text(
                text = stringResource(id = R.string.english),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(Spacing.large))

        Button(onClick = {
            navController.navigate(Route.USERNAME)
        }, shape = MaterialTheme.shapes.large) {
            Text(
                stringResource(id = R.string.lets_get_down_to_business),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}