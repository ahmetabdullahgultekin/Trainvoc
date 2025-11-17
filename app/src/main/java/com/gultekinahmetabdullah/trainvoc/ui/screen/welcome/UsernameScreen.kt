package com.gultekinahmetabdullah.trainvoc.ui.screen.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Hilt entry point for accessing PreferencesRepository in Compose UI.
 * This allows us to inject the repository without a ViewModel.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface PreferencesRepositoryEntryPoint {
    fun preferencesRepository(): IPreferencesRepository
}

@Composable
fun UsernameScreen(navController: NavController) {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    // Get PreferencesRepository through Hilt's entry point
    val preferencesRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            PreferencesRepositoryEntryPoint::class.java
        ).preferencesRepository()
    }

    val composition by
    rememberLottieComposition(LottieCompositionSpec.Asset("animations/anime_typing.json"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.mediumLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lottie Animation
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(260.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = stringResource(id = R.string.username_prompt),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = stringResource(id = R.string.welcome_description_2),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Spacing.small)
        )

        Spacer(modifier = Modifier.height(Spacing.mediumLarge))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.your_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(Spacing.large))

        Button(
            onClick = {
                if (username.text.isNotBlank()) {
                    preferencesRepository.setUsername(username.text)
                    navController.navigate(Route.MAIN)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                stringResource(id = R.string.continue_text),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}