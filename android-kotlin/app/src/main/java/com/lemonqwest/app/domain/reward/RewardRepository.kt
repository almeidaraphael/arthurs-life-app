package com.lemonqwest.app.domain.reward

/**
 * Repository interface for reward management operations in LemonQwest reward system.
 *
 * This interface defines the contract for reward data operations, supporting the
 * CRUD operations needed for the reward management system. The implementation
 * handles data persistence while this interface maintains domain separation.
 *
 * Follows the Repository pattern used throughout the application for consistent
 * data access abstraction.
 */
interface RewardRepository {
    /**
     * Retrieves a reward by its unique identifier.
     *
     * @param id Unique reward identifier
     * @return Reward if found, null otherwise
     */
    suspend fun findById(id: String): Reward?

    /**
     * Retrieves all rewards in the system.
     *
     * @return List of all rewards
     */
    suspend fun getAllRewards(): List<Reward>

    /**
     * Retrieves all available rewards (can be redeemed).
     *
     * @return List of available rewards
     */
    suspend fun getAvailableRewards(): List<Reward>

    /**
     * Retrieves rewards by category.
     *
     * @param category Reward category to filter by
     * @return List of rewards in the specified category
     */
    suspend fun findByCategory(category: RewardCategory): List<Reward>

    /**
     * Retrieves available rewards by category.
     *
     * @param category Reward category to filter by
     * @return List of available rewards in the specified category
     */
    suspend fun findAvailableByCategory(category: RewardCategory): List<Reward>

    /**
     * Retrieves custom rewards created by a specific caregiver.
     *
     * @param createdByUserId ID of the caregiver who created the rewards
     * @return List of custom rewards created by the caregiver
     */
    suspend fun findCustomByCreator(createdByUserId: String): List<Reward>

    /**
     * Retrieves all predefined (non-custom) rewards.
     *
     * @return List of predefined rewards
     */
    suspend fun getPredefinedRewards(): List<Reward>

    /**
     * Retrieves all custom rewards.
     *
     * @return List of custom rewards
     */
    suspend fun getCustomRewards(): List<Reward>

    /**
     * Retrieves rewards that require approval for redemption.
     *
     * @return List of rewards requiring approval
     */
    suspend fun getApprovalRequiredRewards(): List<Reward>

    /**
     * Retrieves rewards affordable for a given token amount.
     *
     * @param tokenAmount Number of tokens available
     * @return List of rewards that can be afforded with the token amount
     */
    suspend fun findAffordableRewards(tokenAmount: Int): List<Reward>

    /**
     * Retrieves available rewards affordable for a given token amount.
     *
     * @param tokenAmount Number of tokens available
     * @return List of available rewards that can be afforded with the token amount
     */
    suspend fun findAvailableAffordableRewards(tokenAmount: Int): List<Reward>

    /**
     * Saves a new reward or updates an existing one.
     *
     * @param reward Reward to save or update
     */
    suspend fun saveReward(reward: Reward)

    /**
     * Updates an existing reward.
     *
     * @param reward Reward with updated information
     */
    suspend fun updateReward(reward: Reward)

    /**
     * Deletes a reward by its identifier.
     *
     * @param rewardId ID of the reward to delete
     */
    suspend fun deleteReward(rewardId: String)

    /**
     * Updates the availability status of a reward.
     *
     * @param rewardId ID of the reward to update
     * @param isAvailable New availability status
     */
    suspend fun updateRewardAvailability(rewardId: String, isAvailable: Boolean)

    /**
     * Updates the token cost of a reward.
     *
     * @param rewardId ID of the reward to update
     * @param newTokenCost New token cost
     */
    suspend fun updateRewardTokenCost(rewardId: String, newTokenCost: Int)

    /**
     * Counts total custom rewards created by a caregiver.
     *
     * @param createdByUserId ID of the caregiver
     * @return Number of custom rewards created
     */
    suspend fun countCustomRewardsByCreator(createdByUserId: String): Int

    /**
     * Counts total rewards in a specific category.
     *
     * @param category Reward category to count
     * @return Number of rewards in the category
     */
    suspend fun countRewardsByCategory(category: RewardCategory): Int
}
