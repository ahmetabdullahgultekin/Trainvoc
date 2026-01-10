package com.gultekinahmetabdullah.trainvoc.features.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Global admin controls for features
 * Admin can enable/disable features globally, set rollout percentage, and daily limits
 */
@Entity(tableName = "feature_flags_global")
data class GlobalFeatureFlag(
    @PrimaryKey
    @ColumnInfo(name = "feature_key")
    val featureKey: String,  // FeatureFlag.key

    @ColumnInfo(name = "enabled")
    val enabled: Boolean,  // Global on/off

    @ColumnInfo(name = "rollout_percentage")
    val rolloutPercentage: Int = 100,  // 0-100, for gradual rollout (A/B testing)

    @ColumnInfo(name = "max_daily_usage")
    val maxDailyUsage: Int? = null,  // Max API calls per day (null = unlimited)

    @ColumnInfo(name = "current_daily_usage")
    val currentDailyUsage: Int = 0,  // Current usage today

    @ColumnInfo(name = "last_reset_date")
    val lastResetDate: Long = System.currentTimeMillis(),  // For daily reset

    @ColumnInfo(name = "total_cost")
    val totalCost: Double = 0.0,  // Estimated total cost ($)

    @ColumnInfo(name = "notes")
    val notes: String? = null,  // Admin notes

    @ColumnInfo(name = "last_modified")
    val lastModified: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "modified_by")
    val modifiedBy: String? = null  // Who changed it
)
