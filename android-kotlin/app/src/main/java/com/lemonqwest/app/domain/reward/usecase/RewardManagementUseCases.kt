package com.lemonqwest.app.domain.reward.usecase

import com.lemonqwest.app.domain.common.DomainException
import com.lemonqwest.app.domain.reward.CannotDeletePredefinedRewardException
import com.lemonqwest.app.domain.reward.InvalidRewardDataException
import com.lemonqwest.app.domain.reward.InvalidTokenCostException
import com.lemonqwest.app.domain.reward.Reward
import com.lemonqwest.app.domain.reward.RewardCategory
import com.lemonqwest.app.domain.reward.RewardNotFoundException
import com.lemonqwest.app.domain.reward.RewardRepository
import com.lemonqwest.app.domain.reward.RewardRepositoryException
import javax.inject.Inject

/**
 * Collection of use cases for reward management operations in LemonQwest reward system.
 *
 * This class provides the core business logic for reward management operations
 * that caregivers need to create, update, and manage rewards for children.
 * All operations include proper validation and error handling.
 */
class RewardManagementUseCases
@Inject
constructor(
    private val rewardRepository: RewardRepository,
) {
    /**
     * Creates a new custom reward and saves it to the repository.
     *
     * @param title Reward title
     * @param description Reward description
     * @param category Reward category
     * @param tokenCost Token cost for redemption
     * @param createdByUserId ID of the caregiver creating the reward
     * @param requiresApproval Whether the reward requires approval for redemption
     * @return Result with the created reward or error
     */
    suspend fun createCustomReward(
        title: String,
        description: String,
        category: RewardCategory,
        tokenCost: Int,
        createdByUserId: String,
        requiresApproval: Boolean = false,
    ): Result<Reward> {
        return try {
            if (title.isBlank()) {
                return Result.failure(InvalidRewardDataException("Reward title cannot be blank"))
            }
            if (description.isBlank()) {
                return Result.failure(
                    InvalidRewardDataException("Reward description cannot be blank"),
                )
            }
            if (createdByUserId.isBlank()) {
                return Result.failure(InvalidRewardDataException("Creator user ID cannot be blank"))
            }

            val reward = Reward.createCustom(
                title = title.trim(),
                description = description.trim(),
                category = category,
                tokenCost = tokenCost,
                createdByUserId = createdByUserId,
                requiresApproval = requiresApproval,
            )

            rewardRepository.saveReward(reward)
            Result.success(reward)
        } catch (e: InvalidRewardDataException) {
            Result.failure(e)
        } catch (e: InvalidTokenCostException) {
            Result.failure(e)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Updates an existing reward's details.
     *
     * @param rewardId ID of the reward to update
     * @param title New reward title
     * @param description New reward description
     * @param tokenCost New token cost
     * @return Result with the updated reward or error
     */
    suspend fun updateReward(
        rewardId: String,
        title: String,
        description: String,
        tokenCost: Int,
    ): Result<Reward> {
        return try {
            val existingReward = rewardRepository.findById(rewardId)
                ?: return Result.failure(RewardNotFoundException(rewardId))

            if (title.isBlank()) {
                return Result.failure(InvalidRewardDataException("Reward title cannot be blank"))
            }
            if (description.isBlank()) {
                return Result.failure(
                    InvalidRewardDataException("Reward description cannot be blank"),
                )
            }

            val updatedReward = existingReward.copy(
                title = title.trim(),
                description = description.trim(),
                tokenCost = tokenCost,
            )

            rewardRepository.updateReward(updatedReward)
            Result.success(updatedReward)
        } catch (e: InvalidRewardDataException) {
            Result.failure(e)
        } catch (e: InvalidTokenCostException) {
            Result.failure(e)
        } catch (e: RewardNotFoundException) {
            Result.failure(e)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a reward. Only custom rewards can be deleted.
     *
     * @param rewardId ID of the reward to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteReward(rewardId: String): Result<Unit> {
        return try {
            val existingReward = rewardRepository.findById(rewardId)
                ?: return Result.failure(RewardNotFoundException(rewardId))

            if (!existingReward.isCustom) {
                return Result.failure(CannotDeletePredefinedRewardException(rewardId))
            }

            rewardRepository.deleteReward(rewardId)
            Result.success(Unit)
        } catch (e: RewardNotFoundException) {
            Result.failure(e)
        } catch (e: CannotDeletePredefinedRewardException) {
            Result.failure(e)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Updates the availability status of a reward.
     *
     * @param rewardId ID of the reward to update
     * @param isAvailable New availability status
     * @return Result with the updated reward or error
     */
    suspend fun updateRewardAvailability(
        rewardId: String,
        isAvailable: Boolean,
    ): Result<Reward> {
        return try {
            val existingReward = rewardRepository.findById(rewardId)
                ?: return Result.failure(RewardNotFoundException(rewardId))

            val updatedReward = existingReward.updateAvailability(isAvailable)
            rewardRepository.updateReward(updatedReward)
            Result.success(updatedReward)
        } catch (e: RewardNotFoundException) {
            Result.failure(e)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Updates the token cost of a reward.
     *
     * @param rewardId ID of the reward to update
     * @param newTokenCost New token cost
     * @return Result with the updated reward or error
     */
    suspend fun updateRewardTokenCost(
        rewardId: String,
        newTokenCost: Int,
    ): Result<Reward> {
        return try {
            val existingReward = rewardRepository.findById(rewardId)
                ?: return Result.failure(RewardNotFoundException(rewardId))

            val updatedReward = existingReward.updateTokenCost(newTokenCost)
            rewardRepository.updateReward(updatedReward)
            Result.success(updatedReward)
        } catch (e: RewardNotFoundException) {
            Result.failure(e)
        } catch (e: InvalidTokenCostException) {
            Result.failure(e)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets all rewards in the system.
     *
     * @return Result with list of all rewards or error
     */
    suspend fun getAllRewards(): Result<List<Reward>> {
        return try {
            val rewards = rewardRepository.getAllRewards()
            Result.success(rewards)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets all available rewards.
     *
     * @return Result with list of available rewards or error
     */
    suspend fun getAvailableRewards(): Result<List<Reward>> {
        return try {
            val rewards = rewardRepository.getAvailableRewards()
            Result.success(rewards)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets rewards by category.
     *
     * @param category Reward category to filter by
     * @return Result with list of rewards in the category or error
     */
    suspend fun getRewardsByCategory(category: RewardCategory): Result<List<Reward>> {
        return try {
            val rewards = rewardRepository.findByCategory(category)
            Result.success(rewards)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets custom rewards created by a specific caregiver.
     *
     * @param createdByUserId ID of the caregiver who created the rewards
     * @return Result with list of custom rewards or error
     */
    suspend fun getCustomRewardsByCreator(createdByUserId: String): Result<List<Reward>> {
        return try {
            val rewards = rewardRepository.findCustomByCreator(createdByUserId)
            Result.success(rewards)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets rewards affordable for a given token amount.
     *
     * @param tokenAmount Number of tokens available
     * @return Result with list of affordable rewards or error
     */
    suspend fun getAffordableRewards(tokenAmount: Int): Result<List<Reward>> {
        return try {
            val rewards = rewardRepository.findAvailableAffordableRewards(tokenAmount)
            Result.success(rewards)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets reward management statistics for a caregiver.
     *
     * @param createdByUserId ID of the caregiver
     * @return Result with reward statistics or error
     */
    suspend fun getRewardStats(createdByUserId: String): Result<RewardStats> {
        return try {
            val totalCustomRewards = rewardRepository.countCustomRewardsByCreator(createdByUserId)
            val customRewards = rewardRepository.findCustomByCreator(createdByUserId)
            val availableCustomRewards = customRewards.count { it.isAvailable }
            val totalPredefinedRewards = rewardRepository.getPredefinedRewards().size

            val stats = RewardStats(
                totalCustomRewards = totalCustomRewards,
                availableCustomRewards = availableCustomRewards,
                totalPredefinedRewards = totalPredefinedRewards,
                totalRewards = totalCustomRewards + totalPredefinedRewards,
            )

            Result.success(stats)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }
}

/**
 * Data class for reward management statistics.
 *
 * @property totalCustomRewards Total number of custom rewards created
 * @property availableCustomRewards Number of available custom rewards
 * @property totalPredefinedRewards Total number of predefined rewards
 * @property totalRewards Total number of all rewards
 */
data class RewardStats(
    val totalCustomRewards: Int,
    val availableCustomRewards: Int,
    val totalPredefinedRewards: Int,
    val totalRewards: Int,
)
