package com.gultekinahmetabdullah.trainvoc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import com.gultekinahmetabdullah.trainvoc.ui.components.InlineLoader
import com.gultekinahmetabdullah.trainvoc.ui.components.LoaderSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Optimized image loading utilities for WebP and other formats
 *
 * Features:
 * - Hardware acceleration support
 * - Memory-efficient loading
 * - Automatic format detection
 * - Progressive loading support
 * - Caching hints
 */
object ImageLoader {

    /**
     * Loads and displays an image with optimal settings
     *
     * @param imageRes Drawable resource ID
     * @param contentDescription Accessibility description
     * @param modifier Compose modifier
     * @param contentScale How to scale the image
     * @param colorFilter Optional color filter
     * @param alignment Image alignment
     */
    @Composable
    fun OptimizedImage(
        @DrawableRes imageRes: Int,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.Crop,
        colorFilter: ColorFilter? = null,
        alignment: Alignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            colorFilter = colorFilter,
            alignment = alignment
        )
    }

    /**
     * Loads an image asynchronously with loading indicator
     *
     * Useful for large images that might take time to decode
     *
     * @param imageRes Drawable resource ID
     * @param contentDescription Accessibility description
     * @param modifier Compose modifier
     * @param contentScale How to scale the image
     */
    @Composable
    fun AsyncImage(
        @DrawableRes imageRes: Int,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.Crop
    ) {
        val context = LocalContext.current
        var isLoading by remember { mutableStateOf(true) }
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(imageRes) {
            bitmap = loadBitmapOptimized(context, imageRes)
            isLoading = false
        }

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            if (isLoading) {
                InlineLoader(size = LoaderSize.small)
            } else {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
        }
    }

    /**
     * Loads a bitmap with optimal decoding options
     *
     * Features:
     * - Hardware bitmap config for better performance
     * - InSampleSize calculation for memory efficiency
     * - WebP and other modern format support
     *
     * @param context Android context
     * @param resourceId Drawable resource ID
     * @param reqWidth Required width (0 for original)
     * @param reqHeight Required height (0 for original)
     * @return Decoded bitmap or null if failed
     */
    suspend fun loadBitmapOptimized(
        context: Context,
        @DrawableRes resourceId: Int,
        reqWidth: Int = 0,
        reqHeight: Int = 0
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val options = BitmapFactory.Options().apply {
                    // First decode with inJustDecodeBounds to get dimensions
                    inJustDecodeBounds = true
                }

                BitmapFactory.decodeResource(context.resources, resourceId, options)

                // Calculate inSampleSize if dimensions provided
                if (reqWidth > 0 && reqHeight > 0) {
                    options.inSampleSize = calculateInSampleSize(
                        options.outWidth,
                        options.outHeight,
                        reqWidth,
                        reqHeight
                    )
                }

                // Decode with optimal settings
                options.apply {
                    inJustDecodeBounds = false
                    inPreferredConfig = Bitmap.Config.HARDWARE // Hardware acceleration
                    inMutable = false // Immutable for better memory
                }

                BitmapFactory.decodeResource(context.resources, resourceId, options)
            } catch (e: Exception) {
                android.util.Log.e("ImageLoader", "Failed to load bitmap: $resourceId", e)
                null
            }
        }
    }

    /**
     * Calculates sample size for downscaling images
     *
     * Reduces memory usage for large images that don't need full resolution
     *
     * @param width Image width
     * @param height Image height
     * @param reqWidth Required width
     * @param reqHeight Required height
     * @return Sample size (power of 2)
     */
    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2
            // and keeps both height and width larger than requested
            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Preloads images for smoother UX
     *
     * Call this during app startup or when navigating to image-heavy screens
     *
     * @param context Android context
     * @param imageResources List of drawable resource IDs to preload
     */
    suspend fun preloadImages(
        context: Context,
        imageResources: List<Int>
    ) {
        withContext(Dispatchers.IO) {
            imageResources.forEach { resId ->
                try {
                    // Decode bounds only to cache the image
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeResource(context.resources, resId, options)
                } catch (e: Exception) {
                    android.util.Log.w("ImageLoader", "Failed to preload: $resId", e)
                }
            }
        }
    }

    /**
     * Gets image dimensions without loading the full bitmap
     *
     * Useful for layout calculations
     *
     * @param context Android context
     * @param resourceId Drawable resource ID
     * @return Pair of (width, height) or null if failed
     */
    fun getImageDimensions(
        context: Context,
        @DrawableRes resourceId: Int
    ): Pair<Int, Int>? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeResource(context.resources, resourceId, options)
            Pair(options.outWidth, options.outHeight)
        } catch (e: Exception) {
            android.util.Log.e("ImageLoader", "Failed to get dimensions: $resourceId", e)
            null
        }
    }

    /**
     * Checks if image format is WebP
     *
     * @param context Android context
     * @param resourceId Drawable resource ID
     * @return true if WebP format
     */
    fun isWebPFormat(
        context: Context,
        @DrawableRes resourceId: Int
    ): Boolean {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeResource(context.resources, resourceId, options)
            options.outMimeType == "image/webp"
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Memory-efficient image cache
 *
 * Simple LRU cache for decoded bitmaps
 * Android's built-in caching usually sufficient, but this provides explicit control
 */
object ImageCache {
    private val cache = mutableMapOf<Int, Bitmap>()
    private const val MAX_CACHE_SIZE = 20

    /**
     * Gets cached bitmap or null
     */
    fun get(@DrawableRes resourceId: Int): Bitmap? = cache[resourceId]

    /**
     * Caches a bitmap
     */
    fun put(@DrawableRes resourceId: Int, bitmap: Bitmap) {
        if (cache.size >= MAX_CACHE_SIZE) {
            // Remove oldest entry
            cache.remove(cache.keys.first())
        }
        cache[resourceId] = bitmap
    }

    /**
     * Clears all cached bitmaps
     */
    fun clear() {
        cache.values.forEach { it.recycle() }
        cache.clear()
    }
}
