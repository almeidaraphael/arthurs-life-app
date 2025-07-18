package com.lemonqwest.app.infrastructure.reward

import com.lemonqwest.app.domain.reward.Reward
import com.lemonqwest.app.domain.reward.RewardCategory
import com.lemonqwest.app.domain.reward.RewardDataSource
import com.lemonqwest.app.domain.reward.RewardRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RewardRepository that delegates to RewardDataSource.
 *
 * This implementation follows the Repository pattern used throughout the application,
 * providing a layer of abstraction between the domain and data access layers.
 * It delegates all operations to the injected RewardDataSource implementation.
 *
 * The repository handles the coordination of data access operations while maintaining
 * clean separation between domain logic and data persistence concerns.
 */
@Singleton
class RewardRepositoryImpl
@Inject
constructor(
    private val rewardDataSource: RewardDataSource,
) : RewardRepository {

    override suspend fun findById(id: String): Reward? = rewardDataSource.findById(id)

    override suspend fun getAllRewards(): List<Reward> = rewardDataSource.getAllRewards()

    override suspend fun getAvailableRewards(): List<Reward> = rewardDataSource.getAvailableRewards()

    override suspend fun findByCategory(category: RewardCategory): List<Reward> = rewardDataSource.findByCategory(
        category,
    )

    override suspend fun findAvailableByCategory(category: RewardCategory): List<Reward> = rewardDataSource.findAvailableByCategory(
        category,
    )

    override suspend fun findCustomByCreator(createdByUserId: String): List<Reward> = rewardDataSource.findCustomByCreator(
        createdByUserId,
    )

    override suspend fun getPredefinedRewards(): List<Reward> = rewardDataSource.getPredefinedRewards()

    override suspend fun getCustomRewards(): List<Reward> = rewardDataSource.getCustomRewards()

    override suspend fun getApprovalRequiredRewards(): List<Reward> = rewardDataSource.getApprovalRequiredRewards()

    override suspend fun findAffordableRewards(tokenAmount: Int): List<Reward> = rewardDataSource.findAffordableRewards(
        tokenAmount,
    )

    override suspend fun findAvailableAffordableRewards(tokenAmount: Int): List<Reward> = rewardDataSource.findAvailableAffordableRewards(
        tokenAmount,
    )

    override suspend fun saveReward(reward: Reward) = rewardDataSource.saveReward(reward)

    override suspend fun updateReward(reward: Reward) = rewardDataSource.saveReward(reward)

    override suspend fun deleteReward(rewardId: String) = rewardDataSource.deleteReward(rewardId)

    override suspend fun updateRewardAvailability(rewardId: String, isAvailable: Boolean) = rewardDataSource.updateRewardAvailability(
        rewardId,
        isAvailable,
    )

    override suspend fun updateRewardTokenCost(rewardId: String, newTokenCost: Int) = rewardDataSource.updateRewardTokenCost(
        rewardId,
        newTokenCost,
    )

    override suspend fun countCustomRewardsByCreator(createdByUserId: String): Int = rewardDataSource.countCustomRewardsByCreator(
        createdByUserId,
    )

    override suspend fun countRewardsByCategory(category: RewardCategory): Int = rewardDataSource.countRewardsByCategory(
        category,
    )
}
