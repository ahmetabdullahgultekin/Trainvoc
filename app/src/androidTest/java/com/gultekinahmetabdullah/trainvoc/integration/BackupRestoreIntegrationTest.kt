package com.gultekinahmetabdullah.trainvoc.integration

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.sync.ConflictStrategy
import com.gultekinahmetabdullah.trainvoc.sync.DataExporter
import com.gultekinahmetabdullah.trainvoc.sync.DataImporter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for backup and restore functionality
 *
 * Tests the complete flow from data export to import with encryption
 * Ensures data integrity is maintained across the entire process
 */
@RunWith(AndroidJUnit4::class)
class BackupRestoreIntegrationTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var exporter: DataExporter
    private lateinit var importer: DataImporter

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        // Create in-memory database for testing
        database = AppDatabase.DatabaseBuilder.createTestDatabase(context)

        exporter = DataExporter(context, database)
        importer = DataImporter(context, database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCompleteBackupRestoreFlow() = runBlocking {
        // Given: Database with sample words
        val sampleWords = createSampleWords(10)
        sampleWords.forEach { word ->
            database.wordDao().insertWord(word)
        }

        val originalWords = database.wordDao().getAllWords().first()
        assertEquals(10, originalWords.size)

        // When: Export data (unencrypted for simplicity in test)
        val exportResult = exporter.exportToJson(
            includeStatistics = true,
            includePreferences = true,
            encrypt = false
        )

        // Then: Export should succeed
        assertTrue(exportResult is com.gultekinahmetabdullah.trainvoc.sync.BackupResult.Success)
        val filePath = (exportResult as com.gultekinahmetabdullah.trainvoc.sync.BackupResult.Success).filePath

        // When: Clear database and import data back
        database.clearAllTables()
        val wordsAfterClear = database.wordDao().getAllWords().first()
        assertEquals(0, wordsAfterClear.size)

        val importResult = importer.importFromJson(
            filePath = filePath,
            conflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE
        )

        // Then: Import should succeed and restore all data
        assertTrue(importResult is com.gultekinahmetabdullah.trainvoc.sync.RestoreResult.Success)
        val restoredWords = database.wordDao().getAllWords().first()

        assertEquals(10, restoredWords.size)
        assertEquals(originalWords.map { it.word }.sorted(), restoredWords.map { it.word }.sorted())
    }

    @Test
    fun testEncryptedBackupRestoreFlow() = runBlocking {
        // Given: Database with words
        val sampleWords = createSampleWords(5)
        sampleWords.forEach { word ->
            database.wordDao().insertWord(word)
        }

        // When: Export with encryption
        val exportResult = exporter.exportToJson(encrypt = true)

        // Then: Export should succeed
        assertTrue(exportResult is com.gultekinahmetabdullah.trainvoc.sync.BackupResult.Success)
        val exportData = exportResult as com.gultekinahmetabdullah.trainvoc.sync.BackupResult.Success
        assertTrue(exportData.encrypted)
        assertTrue(exportData.filePath.endsWith(".enc"))

        // When: Import encrypted data
        database.clearAllTables()
        val importResult = importer.importFromJson(
            filePath = exportData.filePath,
            conflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE
        )

        // Then: Should decrypt and restore successfully
        assertTrue(importResult is com.gultekinahmetabdullah.trainvoc.sync.RestoreResult.Success)
        val restoredWords = database.wordDao().getAllWords().first()
        assertEquals(5, restoredWords.size)
    }

    @Test
    fun testMergeConflictStrategy() = runBlocking {
        // Given: Database with existing words
        val existingWords = listOf(
            Word("hello", "greeting", WordLevel.A1, null, 1, 0),
            Word("world", "planet", WordLevel.A1, null, 2, 0)
        )
        existingWords.forEach { database.wordDao().insertWord(it) }

        // And: Backup with overlapping and new words
        val backupWords = listOf(
            Word("hello", "UPDATED greeting", WordLevel.A2, null, 1, 0),  // Conflict
            Word("goodbye", "farewell", WordLevel.A1, null, 3, 0)         // New
        )
        backupWords.forEach { database.wordDao().insertWord(it) }

        val exportResult = exporter.exportToJson(encrypt = false)
        assertTrue(exportResult is com.gultekinahmetabdullah.trainvoc.sync.BackupResult.Success)

        // When: Restore original data and merge
        database.clearAllTables()
        existingWords.forEach { database.wordDao().insertWord(it) }

        val importResult = importer.importFromJson(
            filePath = (exportResult as com.gultekinahmetabdullah.trainvoc.sync.BackupResult.Success).filePath,
            conflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE
        )

        // Then: Should have merged data
        assertTrue(importResult is com.gultekinahmetabdullah.trainvoc.sync.RestoreResult.Success)
        val finalWords = database.wordDao().getAllWords().first()

        // Should have all unique words
        assertTrue(finalWords.any { it.word == "hello" })
        assertTrue(finalWords.any { it.word == "world" })
        assertTrue(finalWords.any { it.word == "goodbye" })
    }

    // Helper functions

    private fun createSampleWords(count: Int): List<Word> {
        return (1..count).map { index ->
            Word(
                word = "testword$index",
                meaning = "test meaning $index",
                level = WordLevel.values()[index % WordLevel.values().size],
                lastReviewed = null,
                statId = index,
                secondsSpent = 0
            )
        }
    }
}
