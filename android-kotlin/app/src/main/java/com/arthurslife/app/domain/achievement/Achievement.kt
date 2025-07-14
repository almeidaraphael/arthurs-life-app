package com.arthurslife.app.domain.achievement

import kotlinx.serialization.Serializable

/**
 * Core domain entity representing an achievement in the Arthur's Life MVP.
 *
 * This simplified achievement system focuses on the 5 core achievements defined
 * in the MVP to demonstrate gamification concepts without complexity.
 * Each achievement tracks progress toward a specific goal and provides
 * motivation for consistent task completion.
 *
 * @property id Unique identifier for the achievement
 * @property type The type of achievement (defines behavior and requirements)
 * @property isUnlocked Whether the achievement has been completed
 * @property progress Current progress toward the achievement goal
 * @property unlockedAt Timestamp when achievement was unlocked (null if not unlocked)
 * @property userId ID of the user who owns this achievement
 */
@Serializable
data class Achievement(
    val id: String,
    val type: AchievementType,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val unlockedAt: Long? = null,
    val userId: String,
) {
    /**
     * Gets the target value needed to unlock this achievement.
     */
    val target: Int get() = type.target

    /**
     * Gets the display name for this achievement.
     */
    val name: String get() = type.displayName

    /**
     * Gets the description for this achievement.
     */
    val description: String get() = type.description

    /**
     * Gets the progress percentage (0-100) toward completing this achievement.
     */
    val progressPercentage: Int get() = if (target > 0) {
        ((progress.toLong() * 100) / target).coerceAtMost(100L).toInt()
    } else {
        0
    }

    /**
     * Checks if this achievement is ready to be unlocked.
     */
    fun canBeUnlocked(): Boolean = !isUnlocked && progress >= target

    /**
     * Creates a copy of this achievement with updated progress.
     *
     * @param newProgress The new progress value
     * @return Updated achievement with new progress
     */
    fun updateProgress(
        newProgress: Int,
    ): Achievement = copy(progress = newProgress.coerceAtLeast(0))

    /**
     * Creates a copy of this achievement marked as unlocked.
     *
     * @return Unlocked achievement with timestamp
     */
    fun unlock(): Achievement = copy(
        isUnlocked = true,
        progress = target,
        unlockedAt = System.currentTimeMillis(),
    )

    companion object {
        /**
         * Creates a new achievement for a specific user.
         *
         * @param type Achievement type
         * @param userId ID of the user
         * @return New achievement instance
         */
        fun create(type: AchievementType, userId: String): Achievement = Achievement(
            id = "${type.name.lowercase()}-$userId",
            type = type,
            userId = userId,
        )
    }
}
