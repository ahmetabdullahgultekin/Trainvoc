package com.gultekinahmetabdullah.trainvoc.offline

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents an action that needs to be synced to the server when online
 * Part of offline-first architecture for seamless offline/online experience
 */
@Entity(
    tableName = "sync_queue",
    indices = [
        Index(value = ["synced"], name = "index_sync_queue_synced"),
        Index(value = ["entityType"], name = "index_sync_queue_entity_type"),
        Index(value = ["timestamp"], name = "index_sync_queue_timestamp"),
        Index(value = ["priority"], name = "index_sync_queue_priority")
    ]
)
data class SyncQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Type of action to sync */
    @ColumnInfo(name = "actionType")
    val actionType: SyncAction,

    /** Type of entity being synced */
    @ColumnInfo(name = "entityType")
    val entityType: EntityType,

    /** ID of the entity */
    @ColumnInfo(name = "entity_id")
    val entityId: String,

    /** Serialized JSON data for the entity */
    @ColumnInfo(name = "entity_data")
    val entityData: String,

    /** When the action was queued */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    /** Whether this has been successfully synced */
    @ColumnInfo(name = "synced", defaultValue = "0")
    val synced: Boolean = false,

    /** Number of sync attempts */
    @ColumnInfo(name = "attempt_count", defaultValue = "0")
    val attemptCount: Int = 0,

    /** Last error message if sync failed */
    @ColumnInfo(name = "last_error")
    val lastError: String? = null,

    /** Last attempt timestamp */
    @ColumnInfo(name = "last_attempt")
    val lastAttempt: Long? = null,

    /** Priority (higher = more important) */
    @ColumnInfo(name = "priority", defaultValue = "0")
    val priority: Int = 0
)

/**
 * Types of sync actions
 */
enum class SyncAction {
    CREATE,  // Create new entity on server
    UPDATE,  // Update existing entity
    DELETE   // Delete entity on server
}

/**
 * Types of entities that can be synced
 */
enum class EntityType {
    WORD,           // User's words
    STATISTIC,      // Learning statistics
    EXAM,           // Exam results
    ACHIEVEMENT,    // Achievement progress
    USER_PROFILE,   // User profile updates
    BACKUP,         // Cloud backup
    FEATURE_FLAG    // User feature preferences
}
