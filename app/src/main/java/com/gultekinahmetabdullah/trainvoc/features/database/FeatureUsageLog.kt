package com.gultekinahmetabdullah.trainvoc.features.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Usage tracking and cost monitoring for features
 * Logs every feature usage for analytics and cost estimation
 */
@Entity(
    tableName = "feature_usage_log",
    indices = [
        Index(value = ["feature_key"]),
        Index(value = ["timestamp"]),
        Index(value = ["success"])
    ]
)
data class FeatureUsageLog(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "feature_key")
    val featureKey: String,  // FeatureFlag.key

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "api_calls_made")
    val apiCallsMade: Int = 0,  // # of API calls for this usage

    @ColumnInfo(name = "estimated_cost")
    val estimatedCost: Double = 0.0,  // Cost for this usage ($)

    @ColumnInfo(name = "success")
    val success: Boolean = true,  // Did it work?

    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null,  // If failed, what was the error?

    @ColumnInfo(name = "user_id")
    val userId: String? = null  // For multi-user analytics (optional)
)
