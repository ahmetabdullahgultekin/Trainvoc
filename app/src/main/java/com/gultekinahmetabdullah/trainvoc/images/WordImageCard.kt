package com.gultekinahmetabdullah.trainvoc.images

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import kotlinx.coroutines.launch

/**
 * Word image card component
 * Displays image for a word with loading and error states
 */
@Composable
fun WordImageCard(
    wordId: String,
    wordText: String,
    imageService: ImageService,
    featureFlags: FeatureFlagManager,
    modifier: Modifier = Modifier,
    showAttribution: Boolean = true
) {
    val scope = rememberCoroutineScope()
    var wordImage by remember { mutableStateOf<WordImage?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val imageEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.IMAGES_VISUAL_AIDS)

    if (!imageEnabled) return

    // Load image
    LaunchedEffect(wordId) {
        isLoading = true
        error = null

        scope.launch {
            val result = imageService.getImageForWord(wordText, wordId)
            isLoading = false

            result.onSuccess {
                wordImage = it
            }.onFailure {
                error = it.message
            }
        }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text(
                        text = "Failed to load image",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                wordImage != null -> {
                    Column {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(wordImage!!.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = wordText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                            imageLoader = imageService.getImageLoader()
                        )

                        if (showAttribution && wordImage!!.photographer != null) {
                            Text(
                                text = "Photo by ${wordImage!!.photographer}",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact word image for word cards
 */
@Composable
fun CompactWordImage(
    wordId: String,
    wordText: String,
    imageService: ImageService,
    featureFlags: FeatureFlagManager,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var wordImage by remember { mutableStateOf<WordImage?>(null) }

    val imageEnabled by featureFlags.rememberFeatureEnabled(FeatureFlag.IMAGES_VISUAL_AIDS)

    if (!imageEnabled) return

    LaunchedEffect(wordId) {
        scope.launch {
            imageService.getImageForWord(wordText, wordId).onSuccess {
                wordImage = it
            }
        }
    }

    wordImage?.let { image ->
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.thumbnailUrl ?: image.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = wordText,
            modifier = modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            imageLoader = imageService.getImageLoader()
        )
    }
}
