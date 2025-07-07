package com.arthurslife.app.infrastructure.achievement

import com.arthurslife.app.domain.achievement.Achievement
import com.arthurslife.app.domain.achievement.AchievementCategory
import com.arthurslife.app.domain.achievement.AchievementDataSource
import com.arthurslife.app.domain.achievement.AchievementType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory implementation of AchievementDataSource for Arthur's Life MVP.
 *
 * This implementation provides achievement persistence using in-memory storage,
 * following the same pattern as other in-memory data sources in the application.
 * It automatically initializes achievements for the demo child user.
 */
@Singleton
class InMemoryAchievementDataSource
@Inject
constructor() : AchievementDataSource {

    private val mutex = Mutex()
    private val achievements = mutableListOf<Achievement>()

    companion object {
        // Child user ID from existing InMemoryUserDataSource
        private const val CHILD_USER_ID = "child-1"
    }

    init {
        // Initialize achievements for the demo child user
        achievements.addAll(createInitialAchievements(CHILD_USER_ID))
    }

    /**
     * Creates the initial set of achievements for a user.
     * All achievements start with zero progress and unlocked = false.
     */
    private fun createInitialAchievements(userId: String): List<Achievement> {
        return AchievementType.values().map { type ->
            Achievement.create(type, userId)
        }
    }

    override suspend fun findById(id: String): Achievement? =
        mutex.withLock {
            achievements.find { it.id == id }
        }

    override suspend fun findByUserId(userId: String): List<Achievement> =
        mutex.withLock {
            achievements.filter { it.userId == userId }
        }

    override suspend fun findUnlockedByUserId(userId: String): List<Achievement> =
        mutex.withLock {
            achievements.filter { it.userId == userId && it.isUnlocked }
        }

    override suspend fun findByUserIdAndCategory(
        userId: String,
        category: AchievementCategory,
    ): List<Achievement> =
        mutex.withLock {
            achievements.filter { it.userId == userId && it.type.category == category }
        }

    override suspend fun findByUserIdAndType(
        userId: String,
        type: AchievementType,
    ): Achievement? =
        mutex.withLock {
            achievements.find { it.userId == userId && it.type == type }
        }

    override suspend fun saveAchievement(achievement: Achievement): Unit =
        mutex.withLock {
            val existingIndex = achievements.indexOfFirst { it.id == achievement.id }
            if (existingIndex >= 0) {
                achievements[existingIndex] = achievement
            } else {
                achievements.add(achievement)
            }
        }

    override suspend fun initializeAchievementsForUser(userId: String): Unit =
        mutex.withLock {
            // Remove any existing achievements for this user
            achievements.removeIf { it.userId == userId }

            // Add new achievements for all types
            achievements.addAll(createInitialAchievements(userId))
        }

    override suspend fun countUnlockedAchievements(userId: String): Int =
        mutex.withLock {
            achievements.count { it.userId == userId && it.isUnlocked }
        }
}
