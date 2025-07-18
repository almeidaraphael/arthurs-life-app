package com.lemonqwest.app.infrastructure.database

import com.lemonqwest.app.domain.reward.Reward
import com.lemonqwest.app.domain.reward.RewardCategory
import com.lemonqwest.app.domain.reward.RewardDataSource
import com.lemonqwest.app.infrastructure.database.dao.RewardDao
import com.lemonqwest.app.infrastructure.database.entities.RewardEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room database implementation of RewardDataSource for LemonQwest MVP.
 *
 * This implementation provides reward persistence using Room database,
 * replacing the in-memory storage with proper data persistence.
 * It includes conversion between domain models and database entities.
 *
 * The implementation ensures thread-safe operations through Room's
 * built-in coroutine support and proper database transactions.
 */
@Singleton
class RoomRewardDataSource
@Inject
constructor(
    private val rewardDao: RewardDao,
) : RewardDataSource {

    override suspend fun findById(id: String): Reward? =
        rewardDao.findById(id)?.toDomain()

    override suspend fun getAllRewards(): List<Reward> =
        rewardDao.getAllRewards().map { it.toDomain() }

    override suspend fun getAvailableRewards(): List<Reward> =
        rewardDao.getAvailableRewards().map { it.toDomain() }

    override suspend fun findByCategory(category: RewardCategory): List<Reward> =
        rewardDao.findByCategory(category.name).map { it.toDomain() }

    override suspend fun findAvailableByCategory(category: RewardCategory): List<Reward> =
        rewardDao.findAvailableByCategory(category.name).map { it.toDomain() }

    override suspend fun findCustomByCreator(createdByUserId: String): List<Reward> =
        rewardDao.findCustomByCreator(createdByUserId).map { it.toDomain() }

    override suspend fun getPredefinedRewards(): List<Reward> =
        rewardDao.getPredefinedRewards().map { it.toDomain() }

    override suspend fun getCustomRewards(): List<Reward> =
        rewardDao.getCustomRewards().map { it.toDomain() }

    override suspend fun getApprovalRequiredRewards(): List<Reward> =
        rewardDao.getApprovalRequiredRewards().map { it.toDomain() }

    override suspend fun findAffordableRewards(tokenAmount: Int): List<Reward> =
        rewardDao.findAffordableRewards(tokenAmount).map { it.toDomain() }

    override suspend fun findAvailableAffordableRewards(tokenAmount: Int): List<Reward> =
        rewardDao.findAvailableAffordableRewards(tokenAmount).map { it.toDomain() }

    override suspend fun saveReward(reward: Reward) {
        rewardDao.insert(RewardEntity.fromDomain(reward))
    }

    override suspend fun deleteReward(rewardId: String) {
        rewardDao.deleteById(rewardId)
    }

    override suspend fun updateRewardAvailability(rewardId: String, isAvailable: Boolean) {
        rewardDao.updateAvailability(rewardId, isAvailable)
    }

    override suspend fun updateRewardTokenCost(rewardId: String, newTokenCost: Int) {
        rewardDao.updateTokenCost(rewardId, newTokenCost)
    }

    override suspend fun countCustomRewardsByCreator(createdByUserId: String): Int =
        rewardDao.countCustomRewardsByCreator(createdByUserId)

    override suspend fun countRewardsByCategory(category: RewardCategory): Int =
        rewardDao.countRewardsByCategory(category.name)
}
