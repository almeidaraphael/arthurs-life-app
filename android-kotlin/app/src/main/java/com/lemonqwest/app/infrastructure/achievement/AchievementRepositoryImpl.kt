package com.lemonqwest.app.infrastructure.achievement

import com.lemonqwest.app.domain.achievement.Achievement
import com.lemonqwest.app.domain.achievement.AchievementCategory
import com.lemonqwest.app.domain.achievement.AchievementDataSource
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.achievement.AchievementType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AchievementRepository that delegates to AchievementDataSource.
 *
 * This implementation follows the Repository pattern used throughout the application,
 * providing a layer of abstraction between the domain and data access layers.
 * It delegates all operations to the injected AchievementDataSource implementation.
 */
@Singleton
class AchievementRepositoryImpl
@Inject
constructor(
    private val achievementDataSource: AchievementDataSource,
) : AchievementRepository {

    override suspend fun findById(id: String): Achievement? = achievementDataSource.findById(id)

    override suspend fun findByUserId(userId: String): List<Achievement> = achievementDataSource.findByUserId(
        userId,
    )

    override suspend fun findUnlockedByUserId(userId: String): List<Achievement> =
        achievementDataSource.findUnlockedByUserId(userId)

    override suspend fun findByUserIdAndCategory(
        userId: String,
        category: AchievementCategory,
    ): List<Achievement> = achievementDataSource.findByUserIdAndCategory(userId, category)

    override suspend fun findByUserIdAndType(
        userId: String,
        type: AchievementType,
    ): Achievement? = achievementDataSource.findByUserIdAndType(userId, type)

    override suspend fun saveAchievement(achievement: Achievement) =
        achievementDataSource.saveAchievement(achievement)

    override suspend fun updateAchievement(achievement: Achievement) =
        achievementDataSource.saveAchievement(achievement)

    override suspend fun initializeAchievementsForUser(userId: String) =
        achievementDataSource.initializeAchievementsForUser(userId)

    override suspend fun countUnlockedAchievements(userId: String): Int =
        achievementDataSource.countUnlockedAchievements(userId)
}
