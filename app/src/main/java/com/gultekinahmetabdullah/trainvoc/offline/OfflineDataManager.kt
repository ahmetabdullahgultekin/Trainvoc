package com.gultekinahmetabdullah.trainvoc.offline

import com.gultekinahmetabdullah.trainvoc.audio.AudioCacheDao
import com.gultekinahmetabdullah.trainvoc.audio.TextToSpeechService
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import com.gultekinahmetabdullah.trainvoc.images.ImageService
import com.gultekinahmetabdullah.trainvoc.images.WordImageDao
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages offline data download and caching
 * Ensures all necessary data is available for offline use
 */
@Singleton
class OfflineDataManager @Inject constructor(
    private val wordDao: WordDao,
    private val imageService: ImageService,
    private val ttsService: TextToSpeechService,
    private val audioCacheDao: AudioCacheDao,
    private val wordImageDao: WordImageDao,
    private val featureFlags: FeatureFlagManager,
    private val connectivityManager: NetworkConnectivityManager
) {

    private val _downloadProgress = MutableStateFlow<DownloadProgress>(DownloadProgress.Idle)
    val downloadProgress: StateFlow<DownloadProgress> = _downloadProgress.asStateFlow()

    /**
     * Download all word data for offline use
     * Premium feature
     */
    suspend fun downloadAllData(): Result<Unit> {
        if (!featureFlags.isEnabled(FeatureFlag.OFFLINE_MODE)) {
            return Result.failure(Exception("Offline mode is not enabled"))
        }

        if (!connectivityManager.isCurrentlyOnline()) {
            return Result.failure(Exception("Cannot download offline data while offline"))
        }

        return try {
            _downloadProgress.value = DownloadProgress.InProgress(0, "Preparing...")

            // Get all words (getAllWordsList returns List<Word> instead of Flow)
            val words = wordDao.getAllWordsList()
            val totalItems = words.size * 2 // Images + Audio
            var completed = 0

            // Download images (if enabled)
            if (featureFlags.isEnabled(FeatureFlag.OFFLINE_IMAGE_CACHE)) {
                words.forEach { word ->
                    _downloadProgress.value = DownloadProgress.InProgress(
                        progress = (completed.toFloat() / totalItems * 100).toInt(),
                        message = "Downloading image for ${word.word}..."
                    )

                    // Get image for word (use word.word as the primary key)
                    val existingImage = wordImageDao.getPrimaryImage(word.word)
                    if (existingImage != null) {
                        imageService.downloadImageForOffline(existingImage)
                    }

                    completed++
                }
            }

            // Download audio (if enabled)
            if (featureFlags.isEnabled(FeatureFlag.OFFLINE_AUDIO_CACHE)) {
                words.forEach { word ->
                    _downloadProgress.value = DownloadProgress.InProgress(
                        progress = (completed.toFloat() / totalItems * 100).toInt(),
                        message = "Downloading audio for ${word.word}..."
                    )

                    // Generate and cache audio (use word.word as the identifier)
                    ttsService.generateAndCacheAudio(word.word, word.word)

                    completed++
                }
            }

            _downloadProgress.value = DownloadProgress.Completed(totalItems)
            Result.success(Unit)

        } catch (e: Exception) {
            _downloadProgress.value = DownloadProgress.Failed(e.message ?: "Download failed")
            Result.failure(e)
        }
    }

    /**
     * Cancel ongoing download
     */
    fun cancelDownload() {
        _downloadProgress.value = DownloadProgress.Cancelled
    }

    /**
     * Get offline storage usage
     */
    suspend fun getOfflineStorageUsage(): StorageUsage {
        val audioCacheSize = audioCacheDao.getTotalCacheSize() ?: 0L
        val imageCount = wordImageDao.getCachedImageCount()

        return StorageUsage(
            audioBytes = audioCacheSize,
            imageCount = imageCount,
            estimatedImageBytes = imageCount * 200_000L // ~200KB per image average
        )
    }

    /**
     * Clear offline cache
     */
    suspend fun clearOfflineCache(includeAudio: Boolean = true, includeImages: Boolean = true) {
        if (includeAudio) {
            audioCacheDao.clearAllCache()
        }

        if (includeImages) {
            wordImageDao.clearAllCachedPaths()
        }

        _downloadProgress.value = DownloadProgress.Idle
    }

    /**
     * Check if word is fully available offline
     */
    suspend fun isWordAvailableOffline(wordId: String): Boolean {
        val hasAudio = audioCacheDao.getAudioCache(wordId) != null
        val hasImage = wordImageDao.getPrimaryImage(wordId)?.cachedFilePath != null

        return hasAudio && hasImage
    }
}

/**
 * Download progress states
 */
sealed class DownloadProgress {
    object Idle : DownloadProgress()
    data class InProgress(val progress: Int, val message: String) : DownloadProgress()
    data class Completed(val itemsDownloaded: Int) : DownloadProgress()
    data class Failed(val error: String) : DownloadProgress()
    object Cancelled : DownloadProgress()
}

/**
 * Storage usage information
 */
data class StorageUsage(
    val audioBytes: Long,
    val imageCount: Int,
    val estimatedImageBytes: Long
) {
    val totalBytes: Long get() = audioBytes + estimatedImageBytes
    val totalMB: Double get() = totalBytes / 1_000_000.0
}
