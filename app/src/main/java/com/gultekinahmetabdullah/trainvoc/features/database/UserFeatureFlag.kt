package com.gultekinahmetabdullah.trainvoc.features.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User-level feature preferences
 * Users can opt-in/out of features they want to use
 */
@Entity(tableName = "feature_flags_user")
data class UserFeatureFlag(
    @PrimaryKey
    @ColumnInfo(name = "feature_key")
    val featureKey: String,  // FeatureFlag.key

    @ColumnInfo(name = "user_enabled")
    val userEnabled: Boolean,  // User's preference (opt-in/opt-out)

    @ColumnInfo(name = "has_used_feature")
    val hasUsedFeature: Boolean = false,  // Track if ever used

    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0,  // How many times used

    @ColumnInfo(name = "last_used")
    val lastUsed: Long? = null,  // Last usage timestamp

    @ColumnInfo(name = "feedback_provided")
    val feedbackProvided: Boolean = false,  // Did user rate this feature?

    @ColumnInfo(name = "feedback_rating")
    val feedbackRating: Int? = null  // 1-5 stars (nullable if no feedback)
)
