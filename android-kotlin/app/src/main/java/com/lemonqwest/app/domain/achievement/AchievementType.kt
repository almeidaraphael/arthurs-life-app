package com.lemonqwest.app.domain.achievement

import kotlinx.serialization.Serializable

/**
 * Defines the five core achievement types for LemonQwest MVP.
 *
 * Each achievement type represents a specific milestone that encourages
 * different aspects of engagement with the task system:
 * - First Steps: Introduces the concept of task completion
 * - Task Master: Encourages daily routine completion
 * - 3-Day Streak: Promotes consistency and habit formation
 * - Century Club: Rewards long-term engagement (scaled down for MVP)
 * - Token Collector: Motivates participation in the token economy
 *
 * These achievements provide immediate feedback and motivation while
 * keeping the gamification system simple for the MVP.
 */
@Serializable
enum class AchievementType(
    val displayName: String,
    val description: String,
    val target: Int,
    val category: AchievementCategory,
) {
    /**
     * First task completion achievement.
     * Unlocks immediately when user completes their first task.
     */
    FIRST_STEPS(
        displayName = "First Steps",
        description = "Complete your first task",
        target = 1,
        category = AchievementCategory.MILESTONE,
    ),

    /**
     * Daily task completion achievement.
     * Unlocks when user completes all tasks assigned for a single day.
     */
    TASK_MASTER(
        displayName = "Task Master",
        description = "Complete all daily tasks",
        target = 1,
        category = AchievementCategory.DAILY,
    ),

    /**
     * Consistency achievement for 3-day streak.
     * Unlocks when user completes at least one task per day for 3 consecutive days.
     */
    THREE_DAY_STREAK(
        displayName = "3-Day Streak",
        description = "Complete tasks for 3 consecutive days",
        target = 3,
        category = AchievementCategory.CONSISTENCY,
    ),

    /**
     * Total task completion achievement (scaled down for MVP).
     * Unlocks when user completes 10 total tasks.
     */
    CENTURY_CLUB(
        displayName = "Century Club",
        description = "Complete 10 total tasks",
        target = 10,
        category = AchievementCategory.MILESTONE,
    ),

    /**
     * Token accumulation achievement.
     * Unlocks when user earns 50 total tokens from task completion.
     */
    TOKEN_COLLECTOR(
        displayName = "Token Collector",
        description = "Earn 50 total tokens",
        target = 50,
        category = AchievementCategory.ECONOMY,
    ),

    /**
     * Perfect week achievement.
     * Unlocks when user completes at least one task for 7 consecutive days.
     */
    PERFECT_WEEK(
        displayName = "Perfect Week Badge",
        description = "Complete tasks for 7 consecutive days",
        target = 7,
        category = AchievementCategory.CONSISTENCY,
    ),

    /**
     * Speed demon achievement.
     * Unlocks when user completes 5 tasks in a single day.
     */
    SPEED_DEMON(
        displayName = "Speed Demon Trophy",
        description = "Complete 5 tasks in one day",
        target = 5,
        category = AchievementCategory.DAILY,
    ),

    /**
     * Big spender achievement.
     * Unlocks when user spends 100 total tokens on rewards.
     */
    BIG_SPENDER(
        displayName = "Big Spender Badge",
        description = "Spend 100 total tokens on rewards",
        target = 100,
        category = AchievementCategory.ECONOMY,
    ),

    /**
     * Task champion achievement.
     * Unlocks when user completes 25 total tasks.
     */
    TASK_CHAMPION(
        displayName = "Task Champion Trophy",
        description = "Complete 25 total tasks",
        target = 25,
        category = AchievementCategory.MILESTONE,
    ),

    /**
     * Early bird achievement.
     * Unlocks when user completes 3 tasks before noon.
     */
    EARLY_BIRD(
        displayName = "Early Bird Badge",
        description = "Complete 3 tasks before noon",
        target = 3,
        category = AchievementCategory.DAILY,
    ),
}

/**
 * Categories for organizing achievements by their purpose.
 */
@Serializable
enum class AchievementCategory(
    val displayName: String,
    val description: String,
) {
    /**
     * Major milestone achievements for significant accomplishments.
     */
    MILESTONE(
        displayName = "Milestones",
        description = "Major accomplishments and first-time achievements",
    ),

    /**
     * Daily routine and completion achievements.
     */
    DAILY(
        displayName = "Daily Goals",
        description = "Daily task completion and routine achievements",
    ),

    /**
     * Consistency and habit-building achievements.
     */
    CONSISTENCY(
        displayName = "Consistency",
        description = "Streak-based achievements for building habits",
    ),

    /**
     * Token economy participation achievements.
     */
    ECONOMY(
        displayName = "Token Economy",
        description = "Achievements related to earning and managing tokens",
    ),
}
