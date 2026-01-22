package com.gultekinahmetabdullah.trainvoc.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.gultekinahmetabdullah.trainvoc.MainActivity
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Daily goals widget for home screen
 * Shows progress on today's learning goals
 */
class DailyGoalsWidgetProvider : AppWidgetProvider() {

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
            val componentName = ComponentName(context, DailyGoalsWidgetProvider::class.java)
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
                val goals = dao.getDailyGoal() ?: return@launch

                val overallProgress = goals.getOverallProgress()
                val isComplete = goals.isAllGoalsMet()

                val views = RemoteViews(context.packageName, R.layout.widget_daily_goals_layout).apply {
                    // Update overall progress
                    setProgressBar(R.id.widget_overall_progress, 100, overallProgress, false)
                    setTextViewText(
                        R.id.widget_progress_text,
                        if (isComplete) "All goals complete! 🎉" else "$overallProgress%"
                    )

                    // Update individual goals
                    setProgressBar(R.id.widget_words_progress, 100, (goals.getWordsProgress() * 100).toInt(), false)
                    setTextViewText(R.id.widget_words_text, "${goals.wordsToday}/${goals.wordsGoal}")

                    setProgressBar(R.id.widget_reviews_progress, 100, (goals.getReviewsProgress() * 100).toInt(), false)
                    setTextViewText(R.id.widget_reviews_text, "${goals.reviewsToday}/${goals.reviewsGoal}")

                    setProgressBar(R.id.widget_quizzes_progress, 100, (goals.getQuizzesProgress() * 100).toInt(), false)
                    setTextViewText(R.id.widget_quizzes_text, "${goals.quizzesToday}/${goals.quizzesGoal}")

                    setProgressBar(R.id.widget_time_progress, 100, (goals.getTimeProgress() * 100).toInt(), false)
                    setTextViewText(R.id.widget_time_text, "${goals.timeTodayMinutes}/${goals.timeGoalMinutes}m")

                    // Set click intent to open app
                    val intent = Intent(context, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    setOnClickPendingIntent(R.id.widget_goals_container, pendingIntent)
                }

                appWidgetManager.updateAppWidget(widgetId, views)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.gultekinahmetabdullah.trainvoc.ACTION_UPDATE_GOALS_WIDGET"

        /**
         * Request widget update from anywhere in the app
         */
        fun requestUpdate(context: Context) {
            val intent = Intent(context, DailyGoalsWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }
    }
}
