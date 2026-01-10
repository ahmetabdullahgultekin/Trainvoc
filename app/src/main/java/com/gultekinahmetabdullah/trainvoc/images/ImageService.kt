package com.gultekinahmetabdullah.trainvoc.images

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Image service for fetching and caching word images
 *
 * Features:
 * - Fetch images from Unsplash API (FREE, 5000/hr)
 * - Local caching with Coil
 * - Offline support
 * - Feature flag integration
 *
 * Note: In production, you would need to add your Unsplash API key
 */
@Singleton
class ImageService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordImageDao: WordImageDao,
    private val featureFlagManager: FeatureFlagManager
) {

    private val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)  // 25% of app memory
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)  // 2% of disk space
                    .build()
            }
            .respectCacheHeaders(false)
            .build()
    }

    private val cacheDir: File by lazy {
        File(context.cacheDir, "word_images").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Get primary image for a word
     * Fetches from cache or API if needed
     */
    suspend fun getImageForWord(
        wordText: String,
        wordId: String
    ): Result<WordImage> {
        // Check feature flag
        if (!featureFlagManager.isEnabled(FeatureFlag.IMAGES_VISUAL_AIDS)) {
            return Result.failure(Exception("Image feature is disabled"))
        }

        return try {
            // Check cache first
            val cached = wordImageDao.getPrimaryImage(wordId)
            if (cached != null) {
                wordImageDao.recordAccess(cached.id)
                return Result.success(cached)
            }

            // Fetch from API (mock implementation)
            // In production, you would call Unsplash API here
            val imageUrl = getUnsplashImageUrl(wordText)

            val wordImage = WordImage(
                wordId = wordId,
                wordText = wordText,
                imageUrl = imageUrl,
                thumbnailUrl = imageUrl,  // Same for now
                source = ImageSource.UNSPLASH,
                attribution = "Photo from Unsplash",
                photographer = "Community",
                isPrimary = true
            )

            wordImageDao.insertImage(wordImage)

            // Note: No API cost for Unsplash (free tier: 5000 requests/hour)
            // But we still track for analytics
            featureFlagManager.trackUsage(
                feature = FeatureFlag.IMAGES_VISUAL_AIDS,
                apiCalls = 1,
                estimatedCost = 0.0  // FREE!
            )

            Result.success(wordImage)
        } catch (e: Exception) {
            featureFlagManager.trackFailure(
                feature = FeatureFlag.IMAGES_VISUAL_AIDS,
                errorMessage = e.message
            )
            Result.failure(e)
        }
    }

    /**
     * Mock Unsplash URL generator
     * In production, replace with actual Unsplash API call
     */
    private fun getUnsplashImageUrl(query: String): String {
        // Using Unsplash's random photo API with search query
        // Replace with actual API call in production
        return "https://source.unsplash.com/800x600/?$query"
    }

    /**
     * Preload image into memory cache
     */
    suspend fun preloadImage(imageUrl: String) {
        withContext(Dispatchers.IO) {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()

            imageLoader.execute(request)
        }
    }

    /**
     * Download and cache image for offline use (Premium feature)
     */
    suspend fun downloadImageForOffline(
        wordImage: WordImage
    ): Result<String> {
        if (!featureFlagManager.isEnabled(FeatureFlag.OFFLINE_IMAGE_CACHE)) {
            return Result.failure(Exception("Offline image cache feature is disabled"))
        }

        return try {
            val outputFile = File(cacheDir, "${wordImage.wordId}.jpg")

            // Use Coil to download and cache
            val request = ImageRequest.Builder(context)
                .data(wordImage.imageUrl)
                .target(
                    onSuccess = { drawable ->
                        // Image loaded successfully
                    }
                )
                .build()

            imageLoader.execute(request)

            // Update database with cached file path
            val updated = wordImage.copy(
                cachedFilePath = outputFile.absolutePath,
                fileSizeBytes = outputFile.length()
            )
            wordImageDao.updateImage(updated)

            Result.success(outputFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): ImageCacheStats {
        val count = wordImageDao.getImageCount()
        val size = wordImageDao.getTotalCacheSize() ?: 0

        return ImageCacheStats(
            totalImages = count,
            totalSizeBytes = size,
            totalSizeMB = size / (1024.0 * 1024.0)
        )
    }

    /**
     * Clear all cached images
     */
    suspend fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
        wordImageDao.clearAllImages()
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
    }

    /**
     * Get ImageLoader for use in Compose
     */
    fun provideImageLoader(): ImageLoader = imageLoader
}

/**
 * Image cache statistics
 */
data class ImageCacheStats(
    val totalImages: Int,
    val totalSizeBytes: Long,
    val totalSizeMB: Double
)
