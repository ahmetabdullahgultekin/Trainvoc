package com.gultekinahmetabdullah.trainvoc.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data model for app update notes
 */
data class UpdateNotes(
    val currentVersion: String,
    val versionCode: Int,
    val releaseDate: String,
    val highlights: List<UpdateHighlight>,
    val upcomingFeatures: List<String>
)

/**
 * Data model for individual update highlight
 */
data class UpdateHighlight(
    val type: UpdateType,
    val title: String,
    val description: String
) {
    /**
     * Get the appropriate icon for this update type
     */
    fun getIcon(): ImageVector {
        return when (type) {
            UpdateType.NEW -> Icons.Default.NewReleases
            UpdateType.IMPROVED -> Icons.Default.Upgrade
            UpdateType.FIXED -> Icons.Default.BugReport
        }
    }
}

/**
 * Type of update
 */
enum class UpdateType {
    NEW,      // New feature
    IMPROVED, // Improvement to existing feature
    FIXED     // Bug fix
}
