package com.gultekinahmetabdullah.trainvoc.images

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Word image entity for storing images associated with words
 * Supports both API-fetched and user-uploaded images
 */
@Entity(
    tableName = "word_images",
    indices = [
        Index(value = ["word_id"]),
        Index(value = ["source"]),
        Index(value = ["last_updated"])
    ]
)
data class WordImage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "word_id")
    val wordId: String,

    @ColumnInfo(name = "word_text")
    val wordText: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String? = null,

    @ColumnInfo(name = "source")
    val source: ImageSource,  // UNSPLASH, PIXABAY, PEXELS, USER_UPLOAD

    @ColumnInfo(name = "cached_file_path")
    val cachedFilePath: String? = null,

    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long = 0,

    @ColumnInfo(name = "attribution")
    val attribution: String? = null,  // Credit the photographer

    @ColumnInfo(name = "photographer")
    val photographer: String? = null,

    @ColumnInfo(name = "photographer_url")
    val photographerUrl: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_primary")
    val isPrimary: Boolean = true,  // Primary image for the word

    @ColumnInfo(name = "access_count")
    val accessCount: Int = 0
)

/**
 * Image source enum
 */
enum class ImageSource {
    UNSPLASH,      // Unsplash API (free, high quality)
    PIXABAY,       // Pixabay API (free, unlimited)
    PEXELS,        // Pexels API (free, high quality)
    USER_UPLOAD,   // User-uploaded image
    MANUAL         // Manually curated image
}
