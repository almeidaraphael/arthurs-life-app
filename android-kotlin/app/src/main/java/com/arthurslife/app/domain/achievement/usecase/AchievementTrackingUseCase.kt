package com.arthurslife.app.domain.achievement.usecase

import com.arthurslife.app.domain.achievement.Achievement
import com.arthurslife.app.domain.achievement.AchievementRepository
import com.arthurslife.app.domain.achievement.AchievementType
import com.arthurslife.app.domain.task.TaskRepository
import javax.inject.Inject

// Constants for achievement tracking
private const val DAILY_TASK_COMPLETION_THRESHOLD = 6
private const val DAILY_TASK_PROGRESS_VALUE = 3
private const val PERFECT_WEEK_THRESHOLD = 10
private const val PERFECT_WEEK_PROGRESS = 7
private const val SPEED_DEMON_THRESHOLD = 8
private const val SPEED_DEMON_PROGRESS = 5
private const val EARLY_BIRD_THRESHOLD = 5
private const val EARLY_BIRD_PROGRESS = 3

/**
 * Use case for tracking and updating achievement progress in Arthur's Life MVP.
 *
 * This use case handles the logic for checking achievement conditions and
 * updating progress when users complete tasks or other activities.
 * It coordinates between the task system and achievement system to
 * ensure achievements are properly tracked and unlocked.
 */
class AchievementTrackingUseCase
@Inject
constructor(
    private val achievementRepository: AchievementRepository,
    private val taskRepository: TaskRepository,
) {
    /**
     * Updates achievement progress after a task is completed.
     * Initializes achievements for the user if they don't exist yet.
     *
     * @param userId ID of the user who completed the task
     * @return List of newly unlocked achievements
     */
    suspend fun updateAchievementsAfterTaskCompletion(userId: String): List<Achievement> {
        // Initialize achievements for the user if they don't exist yet
        achievementRepository.initializeAchievementsForUser(userId)

        val newlyUnlocked = mutableListOf<Achievement>()

        // Update First Steps achievement
        newlyUnlocked.addAll(updateFirstStepsAchievement(userId))

        // Update Century Club achievement (total task completion)
        newlyUnlocked.addAll(updateCenturyClubAchievement(userId))

        // Update Token Collector achievement
        newlyUnlocked.addAll(updateTokenCollectorAchievement(userId))

        // Update Task Master achievement (all daily tasks completed)
        newlyUnlocked.addAll(updateTaskMasterAchievement(userId))

        // Update 3-Day Streak achievement
        newlyUnlocked.addAll(updateThreeDayStreakAchievement(userId))

        // Update new achievements
        newlyUnlocked.addAll(updateTaskChampionAchievement(userId))
        newlyUnlocked.addAll(updatePerfectWeekAchievement(userId))
        newlyUnlocked.addAll(updateSpeedDemonAchievement(userId))
        newlyUnlocked.addAll(updateEarlyBirdAchievement(userId))

        return newlyUnlocked
    }

    /**
     * Updates the First Steps achievement (complete first task).
     */
    private suspend fun updateFirstStepsAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.FIRST_STEPS)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        val completedTasks = taskRepository.countCompletedTasks(userId)
        if (completedTasks >= 1) {
            val unlockedAchievement = achievement.updateProgress(1).unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        }

        return emptyList()
    }

    /**
     * Updates the Century Club achievement (complete 10 total tasks).
     */
    private suspend fun updateCenturyClubAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.CENTURY_CLUB)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        val completedTasks = taskRepository.countCompletedTasks(userId)
        val updatedAchievement = achievement.updateProgress(completedTasks)

        if (updatedAchievement.canBeUnlocked()) {
            val unlockedAchievement = updatedAchievement.unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        } else if (updatedAchievement.progress != achievement.progress) {
            // Only update if progress has changed and it's not being unlocked
            achievementRepository.updateAchievement(updatedAchievement)
        }

        return emptyList()
    }

    /**
     * Updates the Token Collector achievement (earn 50 total tokens).
     */
    private suspend fun updateTokenCollectorAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.TOKEN_COLLECTOR)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        val tokensEarned = taskRepository.countTokensEarned(userId)
        val updatedAchievement = achievement.updateProgress(tokensEarned)

        if (updatedAchievement.canBeUnlocked()) {
            val unlockedAchievement = updatedAchievement.unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        } else if (updatedAchievement.progress != achievement.progress) {
            // Only update if progress has changed and it's not being unlocked
            achievementRepository.updateAchievement(updatedAchievement)
        }

        return emptyList()
    }

    /**
     * Updates the Task Master achievement (complete all daily tasks).
     * For MVP, this checks if user has no incomplete tasks.
     */
    private suspend fun updateTaskMasterAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.TASK_MASTER)
            ?: return emptyList()

        // Task Master can be unlocked multiple times, so we don't check if already unlocked
        val incompleteTasks = taskRepository.findIncompleteByUserId(userId)
        val allTasksComplete = incompleteTasks.isEmpty()

        if (allTasksComplete && !achievement.isUnlocked) {
            val unlockedAchievement = achievement.updateProgress(1).unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        }

        return emptyList()
    }

    /**
     * Updates the 3-Day Streak achievement.
     * For MVP simplicity, this is a placeholder implementation.
     * A full implementation would track daily completion over time.
     */
    private suspend fun updateThreeDayStreakAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.THREE_DAY_STREAK)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        // Simplified implementation: unlock after completing 6+ tasks total
        // In a full implementation, this would track daily completion patterns
        val completedTasks = taskRepository.countCompletedTasks(userId)
        if (completedTasks >= DAILY_TASK_COMPLETION_THRESHOLD) {
            val unlockedAchievement = achievement.updateProgress(DAILY_TASK_PROGRESS_VALUE).unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        }

        return emptyList()
    }

    /**
     * Gets all achievements for a user with current progress.
     *
     * @param userId ID of the user
     * @return List of all achievements for the user
     */
    suspend fun getAllAchievements(userId: String): List<Achievement> {
        return achievementRepository.findByUserId(userId)
    }

    /**
     * Gets unlocked achievements for a user.
     *
     * @param userId ID of the user
     * @return List of unlocked achievements for the user
     */
    suspend fun getUnlockedAchievements(userId: String): List<Achievement> {
        return achievementRepository.findUnlockedByUserId(userId)
    }

    /**
     * Updates the Task Champion achievement (complete 25 total tasks).
     */
    private suspend fun updateTaskChampionAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.TASK_CHAMPION)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        val completedTasks = taskRepository.countCompletedTasks(userId)
        val updatedAchievement = achievement.updateProgress(completedTasks)

        if (updatedAchievement.canBeUnlocked()) {
            val unlockedAchievement = updatedAchievement.unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        } else if (updatedAchievement.progress != achievement.progress) {
            achievementRepository.updateAchievement(updatedAchievement)
        }

        return emptyList()
    }

    /**
     * Updates the Perfect Week achievement (7-day streak).
     * Simplified implementation: unlock after completing 10+ tasks total.
     */
    private suspend fun updatePerfectWeekAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.PERFECT_WEEK)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        // Simplified implementation: unlock after completing 10+ tasks total
        val completedTasks = taskRepository.countCompletedTasks(userId)
        if (completedTasks >= PERFECT_WEEK_THRESHOLD) {
            val unlockedAchievement = achievement.updateProgress(PERFECT_WEEK_PROGRESS).unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        }

        return emptyList()
    }

    /**
     * Updates the Speed Demon achievement (5 tasks in one day).
     * Simplified implementation: unlock after completing 8+ tasks total.
     */
    private suspend fun updateSpeedDemonAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.SPEED_DEMON)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        // Simplified implementation: unlock after completing 8+ tasks total
        val completedTasks = taskRepository.countCompletedTasks(userId)
        if (completedTasks >= SPEED_DEMON_THRESHOLD) {
            val unlockedAchievement = achievement.updateProgress(SPEED_DEMON_PROGRESS).unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        }

        return emptyList()
    }

    /**
     * Updates the Early Bird achievement (3 tasks before noon).
     * Simplified implementation: unlock after completing 5+ tasks total.
     */
    private suspend fun updateEarlyBirdAchievement(userId: String): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.EARLY_BIRD)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        // Simplified implementation: unlock after completing 5+ tasks total
        val completedTasks = taskRepository.countCompletedTasks(userId)
        if (completedTasks >= EARLY_BIRD_THRESHOLD) {
            val unlockedAchievement = achievement.updateProgress(EARLY_BIRD_PROGRESS).unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        }

        return emptyList()
    }

    /**
     * Updates achievement progress after tokens are spent on rewards.
     * This method should be called whenever a user redeems a reward.
     *
     * @param userId ID of the user who spent tokens
     * @param tokensSpent Amount of tokens spent in this transaction
     * @return List of newly unlocked achievements
     */
    suspend fun updateAchievementsAfterTokenSpending(
        userId: String,
        tokensSpent: Int,
    ): List<Achievement> {
        // Initialize achievements for the user if they don't exist yet
        achievementRepository.initializeAchievementsForUser(userId)

        val newlyUnlocked = mutableListOf<Achievement>()

        // Update Big Spender achievement
        newlyUnlocked.addAll(updateBigSpenderAchievement(userId, tokensSpent))

        return newlyUnlocked
    }

    /**
     * Updates the Big Spender achievement (spend 100 total tokens on rewards).
     * This method tracks cumulative token spending and unlocks the achievement
     * when the user has spent 100 or more tokens total.
     *
     * @param userId ID of the user
     * @param tokensSpent Amount of tokens spent in this transaction
     * @return List containing the unlocked achievement if applicable
     */
    private suspend fun updateBigSpenderAchievement(
        userId: String,
        tokensSpent: Int,
    ): List<Achievement> {
        val achievement = achievementRepository.findByUserIdAndType(userId, AchievementType.BIG_SPENDER)
            ?: return emptyList()

        if (achievement.isUnlocked) return emptyList()

        // Update progress with the new spending amount
        // The progress represents the cumulative tokens spent
        val newProgress = achievement.progress + tokensSpent
        val updatedAchievement = achievement.updateProgress(newProgress)

        if (updatedAchievement.canBeUnlocked()) {
            val unlockedAchievement = updatedAchievement.unlock()
            achievementRepository.updateAchievement(unlockedAchievement)
            return listOf(unlockedAchievement)
        } else if (updatedAchievement.progress != achievement.progress) {
            // Only update if progress has changed
            achievementRepository.updateAchievement(updatedAchievement)
        }

        return emptyList()
    }
}
