package com.gultekinahmetabdullah.trainvoc.audio

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import kotlinx.coroutines.launch

/**
 * Audio button component for playing word pronunciation
 * Integrates with feature flags and shows loading/error states
 */
@Composable
fun AudioButton(
    wordId: String,
    wordText: String,
    modifier: Modifier = Modifier,
    featureFlags: FeatureFlagManager,
    ttsService: TextToSpeechService,
    showLabel: Boolean = false,
    iconSize: IconButtonDefaults.IconSize = IconButtonDefaults.MediumSize
) {
    val scope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Check if feature is enabled
    val audioEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.AUDIO_PRONUNCIATION)

    // Initialize TTS
    LaunchedEffect(Unit) {
        if (audioEnabled) {
            ttsService.initialize()
        }
    }

    // Animation for playing state
    val infiniteTransition = rememberInfiniteTransition(label = "audio_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    if (!audioEnabled) {
        // Feature disabled - show nothing or disabled state
        return
    }

    if (showLabel) {
        // Button with label
        FilledTonalButton(
            onClick = {
                scope.launch {
                    isLoading = true
                    error = null
                    val result = ttsService.speak(wordText, wordId = wordId)
                    isLoading = false
                    result.onFailure {
                        error = it.message
                    }
                }
            },
            modifier = modifier,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = "Play pronunciation",
                    modifier = if (isPlaying) Modifier.scale(scale) else Modifier
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pronounce")
        }
    } else {
        // Icon button only
        Box(contentAlignment = Alignment.Center) {
            IconButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        error = null
                        val result = ttsService.speak(wordText, wordId = wordId)
                        isLoading = false
                        result.onFailure {
                            error = it.message
                        }
                    }
                },
                modifier = modifier,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Play pronunciation",
                        modifier = if (isPlaying) Modifier.scale(scale) else Modifier,
                        tint = if (error != null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            LocalContentColor.current
                        }
                    )
                }
            }

            // Show error tooltip
            error?.let { errorMsg ->
                Snackbar(
                    modifier = Modifier.padding(top = 48.dp),
                    action = {
                        TextButton(onClick = { error = null }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(errorMsg)
                }
            }
        }
    }
}

/**
 * Compact audio icon for word cards
 */
@Composable
fun CompactAudioIcon(
    wordId: String,
    wordText: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val featureFlags: FeatureFlagManager = hiltViewModel()
    val ttsService: TextToSpeechService = hiltViewModel()
    val scope = rememberCoroutineScope()

    val audioEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.AUDIO_PRONUNCIATION)

    if (!audioEnabled) return

    IconButton(
        onClick = {
            scope.launch {
                ttsService.speak(wordText, wordId = wordId)
            }
        },
        modifier = modifier.size(32.dp)
    ) {
        Icon(
            Icons.Default.VolumeUp,
            contentDescription = "Play",
            modifier = Modifier.size(18.dp),
            tint = tint
        )
    }
}

/**
 * Audio speed control for premium users
 */
@Composable
fun AudioSpeedControl(
    ttsService: TextToSpeechService,
    featureFlags: FeatureFlagManager,
    modifier: Modifier = Modifier
) {
    val speedControlEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.AUDIO_SPEED_CONTROL)

    if (!speedControlEnabled) return

    var currentSpeed by remember { mutableStateOf(1.0f) }

    Column(modifier = modifier) {
        Text(
            "Playback Speed",
            style = MaterialTheme.typography.labelMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            val speeds = listOf(0.5f to "0.5x", 0.75f to "0.75x", 1.0f to "1x", 1.5f to "1.5x", 2.0f to "2x")

            speeds.forEach { (speed, label) ->
                FilterChip(
                    selected = currentSpeed == speed,
                    onClick = {
                        currentSpeed = speed
                        ttsService.setSpeed(speed)
                    },
                    label = { Text(label) }
                )
            }
        }
    }
}
