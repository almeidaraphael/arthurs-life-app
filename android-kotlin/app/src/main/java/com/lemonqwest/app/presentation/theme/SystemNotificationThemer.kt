package com.lemonqwest.app.presentation.theme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.lemonqwest.app.R

/**
 * Utility for applying theme-aware styling to system notifications.
 *
 * This class provides methods to create notifications that match the current
 * app theme, ensuring visual consistency between in-app UI and system notifications.
 */
object SystemNotificationThemer {

    // Color conversion constants
    private const val COLOR_COMPONENT_MAX = 255

    /**
     * Create a theme-aware notification builder.
     *
     * @param context Application context
     * @param currentTheme The current app theme
     * @param channelId The notification channel ID
     * @return NotificationCompat.Builder configured with theme colors
     */
    fun createThemedNotificationBuilder(
        context: Context,
        currentTheme: BaseAppTheme,
        channelId: String,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(getThemedNotificationIcon(currentTheme))
            .setColor(getThemedNotificationColor(currentTheme))
    }

    /**
     * Get the appropriate notification icon for the current theme.
     *
     * @param currentTheme The current app theme
     * @return Resource ID for the notification icon
     */
    private fun getThemedNotificationIcon(currentTheme: BaseAppTheme): Int {
        return when (currentTheme.displayName) {
            "Mario Classic" -> R.drawable.ic_mario_coin
            else -> R.drawable.ic_star // Default icon for Material themes
        }
    }

    /**
     * Get the notification color that matches the current theme.
     *
     * @param context Application context
     * @param currentTheme The current app theme
     * @return Color int for notification styling
     */
    private fun getThemedNotificationColor(currentTheme: BaseAppTheme): Int {
        // Convert Compose Color to Android color int
        val primaryColor = currentTheme.colorScheme.primary
        return android.graphics.Color.argb(
            (primaryColor.alpha * COLOR_COMPONENT_MAX).toInt(),
            (primaryColor.red * COLOR_COMPONENT_MAX).toInt(),
            (primaryColor.green * COLOR_COMPONENT_MAX).toInt(),
            (primaryColor.blue * COLOR_COMPONENT_MAX).toInt(),
        )
    }

    /**
     * Create theme-aware notification channels (Android 8.0+).
     *
     * @param currentTheme The current app theme
     * @param notificationManager NotificationManager instance
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createThemedNotificationChannels(
        currentTheme: BaseAppTheme,
        notificationManager: NotificationManager,
    ) {
        // Task completion notifications
        val taskChannel = NotificationChannel(
            CHANNEL_TASK_COMPLETION,
            "Task Completions",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Notifications for completed tasks and rewards"
            setShowBadge(true)
            enableLights(true)
            lightColor = getThemedNotificationColor(currentTheme)
        }

        // Achievement notifications
        val achievementChannel = NotificationChannel(
            CHANNEL_ACHIEVEMENTS,
            "Achievements",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Notifications for unlocked achievements"
            setShowBadge(true)
            enableLights(true)
            lightColor = getThemedNotificationColor(currentTheme)
        }

        // Theme-specific channel names for Mario theme
        if (currentTheme.displayName == "Mario Classic") {
            taskChannel.name = "Quest Completions"
            taskChannel.description = "Notifications for completed quests and coin rewards"
            achievementChannel.name = "Power-ups Unlocked"
            achievementChannel.description = "Notifications for unlocked power-ups"
        }

        notificationManager.createNotificationChannel(taskChannel)
        notificationManager.createNotificationChannel(achievementChannel)
    }

    /**
     * Update existing notification channels to match the current theme.
     *
     * @param currentTheme The current app theme
     * @param notificationManager NotificationManager instance
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNotificationChannelsForTheme(
        currentTheme: BaseAppTheme,
        notificationManager: NotificationManager,
    ) {
        // Re-create channels with updated theme settings
        createThemedNotificationChannels(currentTheme, notificationManager)
    }

    /**
     * Create a task completion notification with theme-aware styling.
     *
     * @param context Application context
     * @param currentTheme The current app theme
     * @param taskName Name of the completed task
     * @param tokensEarned Number of tokens earned
     * @return Themed NotificationCompat.Builder
     */
    fun createTaskCompletionNotification(
        context: Context,
        currentTheme: BaseAppTheme,
        taskName: String,
        tokensEarned: Int,
    ): NotificationCompat.Builder {
        val (title, content) = when (currentTheme.displayName) {
            "Mario Classic" -> {
                "Quest Complete! 🍄" to "You completed '$taskName' and earned $tokensEarned coins!"
            }
            else -> {
                "Task Complete! ⭐" to "You completed '$taskName' and earned $tokensEarned tokens!"
            }
        }

        return createThemedNotificationBuilder(context, currentTheme, CHANNEL_TASK_COMPLETION)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    /**
     * Create an achievement unlock notification with theme-aware styling.
     *
     * @param context Application context
     * @param currentTheme The current app theme
     * @param achievementName Name of the unlocked achievement
     * @return Themed NotificationCompat.Builder
     */
    fun createAchievementNotification(
        context: Context,
        currentTheme: BaseAppTheme,
        achievementName: String,
    ): NotificationCompat.Builder {
        val (title, content) = when (currentTheme.displayName) {
            "Mario Classic" -> {
                "Power-up Unlocked! 🌟" to "You unlocked the '$achievementName' power-up!"
            }
            else -> {
                "Achievement Unlocked! 🏆" to "You unlocked the '$achievementName' achievement!"
            }
        }

        return createThemedNotificationBuilder(context, currentTheme, CHANNEL_ACHIEVEMENTS)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    // Notification channel constants
    const val CHANNEL_TASK_COMPLETION = "task_completion"
    const val CHANNEL_ACHIEVEMENTS = "achievements"
}
