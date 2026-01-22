package com.gultekinahmetabdullah.trainvoc.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.gultekinahmetabdullah.trainvoc.MainActivity
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Streak tracking widget for home screen
 * Shows current streak with fire emoji and motivational message
 */
class StreakWidgetProvider : AppWidgetProvider() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { widgetId ->
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_UPDATE_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, StreakWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        scope.launch {
            try {
                val database = AppDatabase.DatabaseBuilder.getInstance(context)
                val dao = database.gamificationDao()
                val streak = dao.getStreakTracking() ?: return@launch

                val currentStreak = if (streak.isStreakValid()) streak.currentStreak else 0
                val statusMessage = when {
                    streak.isActiveToday() -> "Great! Keep it up! 🎉"
                    streak.daysUntilBreak() == 0 -> "Practice today to continue!"
                    else -> "Day ${currentStreak} streak! 🔥"
                }

                val views = RemoteViews(context.packageName, R.layout.widget_streak_layout).apply {
                    // Update streak count
                    setTextViewText(R.id.widget_streak_count, currentStreak.toString())

                    // Update status message
                    setTextViewText(R.id.widget_streak_message, statusMessage)

                    // Update stats
                    setTextViewText(R.id.widget_longest_streak, "Longest: ${streak.longestStreak}")
                    setTextViewText(R.id.widget_active_days, "Active: ${streak.totalActiveDays}")

                    // Set click intent to open app
                    val intent = Intent(context, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    setOnClickPendingIntent(R.id.widget_streak_container, pendingIntent)
                }

                appWidgetManager.updateAppWidget(widgetId, views)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating streak widget", e)
            }
        }
    }

    companion object {
        private const val TAG = "StreakWidgetProvider"
        const val ACTION_UPDATE_WIDGET = "com.gultekinahmetabdullah.trainvoc.ACTION_UPDATE_STREAK_WIDGET"

        /**
         * Request widget update from anywhere in the app
         */
        fun requestUpdate(context: Context) {
            val intent = Intent(context, StreakWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }
    }
}
