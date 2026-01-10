package com.gultekinahmetabdullah.trainvoc.images

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for word image operations
 */
@Dao
interface WordImageDao {

    @Query("SELECT * FROM word_images WHERE word_id = :wordId AND is_primary = 1 LIMIT 1")
    suspend fun getPrimaryImage(wordId: String): WordImage?

    @Query("SELECT * FROM word_images WHERE word_id = :wordId AND is_primary = 1 LIMIT 1")
    fun getPrimaryImageFlow(wordId: String): Flow<WordImage?>

    @Query("SELECT * FROM word_images WHERE word_id = :wordId")
    suspend fun getAllImagesForWord(wordId: String): List<WordImage>

    @Query("SELECT * FROM word_images WHERE word_id = :wordId")
    fun getAllImagesForWordFlow(wordId: String): Flow<List<WordImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: WordImage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<WordImage>)

    @Update
    suspend fun updateImage(image: WordImage)

    @Query("UPDATE word_images SET access_count = access_count + 1 WHERE id = :imageId")
    suspend fun recordAccess(imageId: Long)

    @Query("UPDATE word_images SET is_primary = 0 WHERE word_id = :wordId")
    suspend fun clearPrimaryImage(wordId: String)

    @Query("UPDATE word_images SET is_primary = 1 WHERE id = :imageId")
    suspend fun setPrimaryImage(imageId: Long)

    @Query("DELETE FROM word_images WHERE id = :imageId")
    suspend fun deleteImage(imageId: Long)

    @Query("DELETE FROM word_images WHERE word_id = :wordId")
    suspend fun deleteAllImagesForWord(wordId: String)

    @Query("SELECT COUNT(*) FROM word_images")
    suspend fun getImageCount(): Int

    @Query("SELECT SUM(file_size_bytes) FROM word_images")
    suspend fun getTotalCacheSize(): Long?

    @Query("SELECT * FROM word_images WHERE source = :source")
    suspend fun getImagesBySource(source: ImageSource): List<WordImage>

    @Query("SELECT * FROM word_images ORDER BY access_count DESC LIMIT :limit")
    suspend fun getMostAccessedImages(limit: Int = 100): List<WordImage>

    @Query("""
        SELECT * FROM word_images
        WHERE cached_file_path IS NOT NULL
        ORDER BY last_updated DESC
        LIMIT :limit
    """)
    suspend fun getCachedImages(limit: Int = 100): List<WordImage>

    @Query("DELETE FROM word_images WHERE last_updated < :beforeTimestamp")
    suspend fun deleteOldImages(beforeTimestamp: Long)

    @Query("DELETE FROM word_images")
    suspend fun clearAllImages()

    @Query("SELECT COUNT(*) FROM word_images WHERE cached_file_path IS NOT NULL")
    suspend fun getCachedImageCount(): Int

    @Query("UPDATE word_images SET cached_file_path = NULL, file_size_bytes = 0")
    suspend fun clearAllCachedPaths()

    @Query("UPDATE word_images SET cached_file_path = :filePath, file_size_bytes = :fileSize WHERE id = :imageId")
    suspend fun setCachedFilePath(imageId: Long, filePath: String, fileSize: Long)
}
