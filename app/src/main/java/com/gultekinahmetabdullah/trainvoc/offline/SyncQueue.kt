package com.gultekinahmetabdullah.trainvoc.offline

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an action that needs to be synced to the server when online
 * Part of offline-first architecture for seamless offline/online experience
 */
@Entity(tableName = "sync_queue")
data class SyncQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Type of action to sync */
    val actionType: SyncAction,

    /** Type of entity being synced */
    val entityType: EntityType,

    /** ID of the entity */
    val entityId: String,

    /** Serialized JSON data for the entity */
    val entityData: String,

    /** When the action was queued */
    val timestamp: Long = System.currentTimeMillis(),

    /** Whether this has been successfully synced */
    val synced: Boolean = false,

    /** Number of sync attempts */
    val attemptCount: Int = 0,

    /** Last error message if sync failed */
    val lastError: String? = null,

    /** Last attempt timestamp */
    val lastAttempt: Long? = null,

    /** Priority (higher = more important) */
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
