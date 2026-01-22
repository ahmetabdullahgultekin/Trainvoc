package com.gultekinahmetabdullah.trainvoc.audio

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.gultekinahmetabdullah.trainvoc.config.CacheConfig
import com.gultekinahmetabdullah.trainvoc.config.TtsConfig
import com.gultekinahmetabdullah.trainvoc.utils.InputValidation

/**
 * Text-to-Speech service with feature flag integration and cost tracking
 *
 * Features:
 * - TTS for word pronunciation
 * - Multiple speed settings (0.5x, 1.0x, 1.5x, 2.0x)
 * - Audio caching to reduce costs
 * - Feature flag integration
 * - Cost tracking for analytics
 */
@Singleton
class TextToSpeechService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioCacheDao: AudioCacheDao,
    private val featureFlagManager: FeatureFlagManager
) {

    @Volatile
    private var tts: TextToSpeech? = null
    @Volatile
    private var mediaPlayer: MediaPlayer? = null
    @Volatile
    var isInitialized = false
        private set
    private var currentSpeed = 1.0f
    private val initMutex = Mutex()

    // Audio cache directory
    private val cacheDir: File by lazy {
        File(context.cacheDir, "audio_cache").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Initialize TTS engine
     */
    suspend fun initialize(): Boolean = suspendCancellableCoroutine { continuation ->
        tts = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            if (isInitialized) {
                tts?.language = Locale.US
                tts?.setSpeechRate(currentSpeed)
                continuation.resume(true)
            } else {
                continuation.resumeWithException(
                    Exception("TTS initialization failed with status: $status")
                )
            }
        }
    }

    /**
     * Speak text using TTS
     * Checks feature flags and tracks usage
     */
    suspend fun speak(
        text: String,
        language: String = "en",
        wordId: String? = null
    ): Result<Unit> {
        // Validate input text
        val validatedText = InputValidation.validateTTSText(text).getOrElse {
            return Result.failure(it)
        }

        // Check feature flag
        if (!featureFlagManager.isEnabled(FeatureFlag.AUDIO_PRONUNCIATION)) {
            return Result.failure(Exception("Audio pronunciation feature is disabled"))
        }

        return try {
            // Use mutex to prevent race condition during initialization
            if (!isInitialized) {
                initMutex.withLock {
                    if (!isInitialized) {
                        initialize()
                    }
                }
            }

            // Set language
            val locale = when (language) {
                "en" -> Locale.US
                "tr" -> Locale("tr", "TR")
                else -> Locale.US
            }
            tts?.language = locale

            // Speak (use validated text)
            tts?.speak(validatedText, TextToSpeech.QUEUE_FLUSH, null, validatedText)

            // Track usage (for analytics only - Android TTS is FREE)
            featureFlagManager.trackUsage(
                feature = FeatureFlag.AUDIO_PRONUNCIATION,
                apiCalls = 1,
                estimatedCost = TtsConfig.ANALYTICS_COST_PER_CALL
            )

            // Update cache access if wordId provided
            wordId?.let {
                audioCacheDao.recordAccess(it)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            // Track failure
            featureFlagManager.trackFailure(
                feature = FeatureFlag.AUDIO_PRONUNCIATION,
                errorMessage = e.message,
                apiCalls = 1,
                estimatedCost = TtsConfig.ANALYTICS_COST_PER_CALL
            )
            Result.failure(e)
        }
    }

    /**
     * Generate and cache audio file for a word
     * Returns file path if successful
     */
    suspend fun generateAndCacheAudio(
        text: String,
        wordId: String,
        language: String = "en"
    ): Result<String> {
        // Check feature flag
        if (!featureFlagManager.isEnabled(FeatureFlag.AUDIO_PRONUNCIATION)) {
            return Result.failure(Exception("Audio pronunciation feature is disabled"))
        }

        // Check cache first
        val cached = audioCacheDao.getAudioCache(wordId)
        if (cached?.cachedFilePath != null) {
            val file = File(cached.cachedFilePath)
            if (file.exists()) {
                audioCacheDao.recordAccess(wordId)
                return Result.success(cached.cachedFilePath)
            }
        }

        return try {
            if (!isInitialized) {
                initialize()
            }

            val outputFile = File(cacheDir, "$wordId.mp3")

            // Generate audio file
            val result = suspendCancellableCoroutine<Int> { continuation ->
                tts?.synthesizeToFile(
                    text,
                    null,
                    outputFile,
                    text
                )

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        continuation.resume(TextToSpeech.SUCCESS)
                    }

                    override fun onError(utteranceId: String?) {
                        continuation.resumeWithException(
                            Exception("TTS synthesis failed")
                        )
                    }
                })
            }

            if (result == TextToSpeech.SUCCESS && outputFile.exists()) {
                // Cache the audio
                val audioCache = AudioCache(
                    wordId = wordId,
                    wordText = text,
                    language = language,
                    ttsGenerated = true,
                    cachedFilePath = outputFile.absolutePath,
                    fileSizeBytes = outputFile.length()
                )
                audioCacheDao.insertAudioCache(audioCache)

                // Track usage (for analytics only - Android TTS is FREE)
                featureFlagManager.trackUsage(
                    feature = FeatureFlag.AUDIO_PRONUNCIATION,
                    apiCalls = 1,
                    estimatedCost = TtsConfig.ANALYTICS_COST_PER_CALL
                )

                // Check cache size and cleanup if needed
                manageCacheSize()

                Result.success(outputFile.absolutePath)
            } else {
                Result.failure(Exception("Failed to generate audio file"))
            }
        } catch (e: Exception) {
            featureFlagManager.trackFailure(
                feature = FeatureFlag.AUDIO_PRONUNCIATION,
                errorMessage = e.message
            )
            Result.failure(e)
        }
    }

    /**
     * Play cached audio file
     */
    suspend fun playCachedAudio(wordId: String): Result<Unit> {
        val cached = audioCacheDao.getAudioCache(wordId)
        if (cached?.cachedFilePath == null) {
            return Result.failure(Exception("No cached audio found for word: $wordId"))
        }

        return try {
            val file = File(cached.cachedFilePath)
            if (!file.exists()) {
                return Result.failure(Exception("Cached audio file not found"))
            }

            // Stop any current playback
            stopPlayback()

            // Play audio - use local variable first to ensure proper cleanup on exception
            val player = MediaPlayer().apply {
                setDataSource(cached.cachedFilePath)
                prepare()
            }

            try {
                player.start()
                mediaPlayer = player
            } catch (e: Exception) {
                // Release player on start failure to prevent memory leak
                player.release()
                throw e
            }

            audioCacheDao.recordAccess(wordId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Set speech speed
     * @param speed Multiplier (0.5 = slow, 1.0 = normal, 2.0 = fast)
     */
    fun setSpeed(speed: Float) {
        currentSpeed = speed.coerceIn(0.1f, 3.0f)
        tts?.setSpeechRate(currentSpeed)
    }

    /**
     * Stop current playback
     */
    fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        tts?.stop()
    }

    /**
     * Get playback state as Flow
     */
    fun getPlaybackState(): Flow<PlaybackState> = callbackFlow {
        val listener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                trySend(PlaybackState.Playing(utteranceId ?: ""))
            }

            override fun onDone(utteranceId: String?) {
                trySend(PlaybackState.Stopped)
            }

            override fun onError(utteranceId: String?) {
                trySend(PlaybackState.Error("Playback error"))
            }
        }

        tts?.setOnUtteranceProgressListener(listener)

        awaitClose {
            tts?.setOnUtteranceProgressListener(null)
        }
    }

    /**
     * Manage cache size - delete old entries if cache is too large
     */
    private suspend fun manageCacheSize() {
        val maxCacheSize = CacheConfig.AUDIO_CACHE_MAX_SIZE_BYTES
        val maxCacheCount = CacheConfig.AUDIO_CACHE_MAX_COUNT

        val currentSize = audioCacheDao.getTotalCacheSize() ?: 0
        val currentCount = audioCacheDao.getCacheCount()

        // Delete if cache is too large
        if (currentSize > maxCacheSize || currentCount > maxCacheCount) {
            val deleteCount = (currentCount * CacheConfig.CACHE_CLEANUP_PERCENTAGE).toInt()
            audioCacheDao.deleteLeastRecentlyUsed(deleteCount)
        }
    }

    /**
     * Clear all cached audio
     */
    suspend fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
        audioCacheDao.clearAllCache()
    }

    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): CacheStats {
        val size = audioCacheDao.getTotalCacheSize() ?: 0
        val count = audioCacheDao.getCacheCount()
        return CacheStats(
            totalSizeBytes = size,
            totalCount = count,
            totalSizeMB = size / (1024.0 * 1024.0)
        )
    }

    /**
     * Release resources
     */
    fun release() {
        stopPlayback()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}

/**
 * Playback state
 */
sealed class PlaybackState {
    object Stopped : PlaybackState()
    data class Playing(val utteranceId: String) : PlaybackState()
    data class Error(val message: String) : PlaybackState()
}

/**
 * Cache statistics
 */
data class CacheStats(
    val totalSizeBytes: Long,
    val totalCount: Int,
    val totalSizeMB: Double
)
