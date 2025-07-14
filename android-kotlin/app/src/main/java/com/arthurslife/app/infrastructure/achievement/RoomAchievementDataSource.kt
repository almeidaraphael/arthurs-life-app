package com.arthurslife.app.infrastructure.achievement

import com.arthurslife.app.domain.achievement.Achievement
import com.arthurslife.app.domain.achievement.AchievementCategory
import com.arthurslife.app.domain.achievement.AchievementDataSource
import com.arthurslife.app.domain.achievement.AchievementType
import com.arthurslife.app.infrastructure.database.dao.AchievementDao
import com.arthurslife.app.infrastructure.database.entities.AchievementEntity
import javax.inject.Inject

/**
 * Room database implementation of AchievementDataSource.
 *
 * This implementation provides persistent storage for achievements using Room database,
 * replacing the in-memory storage for production use. It maintains thread safety
 * through Room's built-in coroutine support and provides reliable data persistence
 * across app restarts.
 */
class RoomAchievementDataSource @Inject constructor(
    private val achievementDao: AchievementDao,
) : AchievementDataSource {

    override suspend fun findById(achievementId: String): Achievement? {
        return achievementDao.findById(achievementId)?.toDomain()
    }

    override suspend fun findByUserId(userId: String): List<Achievement> {
        return achievementDao.findByUserId(userId).map { it.toDomain() }
    }

    override suspend fun findUnlockedByUserId(userId: String): List<Achievement> {
        return achievementDao.findUnlockedByUserId(userId).map { it.toDomain() }
    }

    override suspend fun findByUserIdAndCategory(userId: String, category: AchievementCategory): List<Achievement> {
        val achievementTypes = AchievementType.entries
            .filter { it.category == category }
            .map { it.name }

        return achievementDao.findByUserIdAndCategory(userId, achievementTypes)
            .map { it.toDomain() }
    }

    override suspend fun findByUserIdAndType(userId: String, type: AchievementType): Achievement? {
        return achievementDao.findByUserIdAndType(userId, type.name)?.toDomain()
    }

    override suspend fun saveAchievement(achievement: Achievement) {
        val entity = AchievementEntity.fromDomain(achievement)
        achievementDao.insertAchievement(entity)
    }

    override suspend fun initializeAchievementsForUser(userId: String) {
        // Check if achievements are already initialized
        if (achievementDao.hasAchievements(userId)) {
            return
        }

        // Create initial achievements for all types
        val initialAchievements = AchievementType.entries.map { type ->
            Achievement.create(
                userId = userId,
                type = type,
            )
        }

        // Save all achievements to database
        val entities = initialAchievements.map { AchievementEntity.fromDomain(it) }
        achievementDao.insertAchievements(entities)
    }

    override suspend fun countUnlockedAchievements(userId: String): Int {
        return achievementDao.countUnlockedByUserId(userId)
    }
}
