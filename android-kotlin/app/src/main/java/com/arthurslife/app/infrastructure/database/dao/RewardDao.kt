package com.arthurslife.app.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.arthurslife.app.infrastructure.database.entities.RewardEntity

/**
 * Data Access Object for reward database operations in Arthur's Life reward system.
 *
 * This DAO provides the database operations needed for reward management,
 * following Room database patterns used throughout the application.
 * All operations are suspend functions to support coroutine-based async operations.
 */
@Dao
interface RewardDao {
    /**
     * Retrieves a reward by its unique identifier.
     *
     * @param id Reward identifier
     * @return RewardEntity if found, null otherwise
     */
    @Query("SELECT * FROM rewards WHERE id = :id")
    suspend fun findById(id: String): RewardEntity?

    /**
     * Retrieves all rewards in the system.
     *
     * @return List of all rewards ordered by creation date
     */
    @Query("SELECT * FROM rewards ORDER BY createdAt DESC")
    suspend fun getAllRewards(): List<RewardEntity>

    /**
     * Retrieves all available rewards.
     *
     * @return List of available rewards ordered by creation date
     */
    @Query("SELECT * FROM rewards WHERE isAvailable = 1 ORDER BY createdAt DESC")
    suspend fun getAvailableRewards(): List<RewardEntity>

    /**
     * Retrieves rewards by category.
     *
     * @param category Reward category name
     * @return List of rewards in the specified category
     */
    @Query("SELECT * FROM rewards WHERE category = :category ORDER BY createdAt DESC")
    suspend fun findByCategory(category: String): List<RewardEntity>

    /**
     * Retrieves available rewards by category.
     *
     * @param category Reward category name
     * @return List of available rewards in the specified category
     */
    @Query(
        "SELECT * FROM rewards WHERE category = :category AND isAvailable = 1 ORDER BY createdAt DESC",
    )
    suspend fun findAvailableByCategory(category: String): List<RewardEntity>

    /**
     * Retrieves custom rewards created by a specific caregiver.
     *
     * @param createdByUserId ID of the caregiver who created the rewards
     * @return List of custom rewards created by the caregiver
     */
    @Query(
        "SELECT * FROM rewards WHERE isCustom = 1 AND createdByUserId = :createdByUserId ORDER BY createdAt DESC",
    )
    suspend fun findCustomByCreator(createdByUserId: String): List<RewardEntity>

    /**
     * Retrieves all predefined (non-custom) rewards.
     *
     * @return List of predefined rewards
     */
    @Query("SELECT * FROM rewards WHERE isCustom = 0 ORDER BY createdAt DESC")
    suspend fun getPredefinedRewards(): List<RewardEntity>

    /**
     * Retrieves all custom rewards.
     *
     * @return List of custom rewards
     */
    @Query("SELECT * FROM rewards WHERE isCustom = 1 ORDER BY createdAt DESC")
    suspend fun getCustomRewards(): List<RewardEntity>

    /**
     * Retrieves rewards that require approval for redemption.
     *
     * @return List of rewards requiring approval
     */
    @Query("SELECT * FROM rewards WHERE requiresApproval = 1 ORDER BY createdAt DESC")
    suspend fun getApprovalRequiredRewards(): List<RewardEntity>

    /**
     * Retrieves rewards affordable for a given token amount.
     *
     * @param tokenAmount Number of tokens available
     * @return List of rewards that can be afforded with the token amount
     */
    @Query("SELECT * FROM rewards WHERE tokenCost <= :tokenAmount ORDER BY tokenCost ASC")
    suspend fun findAffordableRewards(tokenAmount: Int): List<RewardEntity>

    /**
     * Retrieves available rewards affordable for a given token amount.
     *
     * @param tokenAmount Number of tokens available
     * @return List of available rewards that can be afforded with the token amount
     */
    @Query(
        "SELECT * FROM rewards WHERE tokenCost <= :tokenAmount AND isAvailable = 1 ORDER BY tokenCost ASC",
    )
    suspend fun findAvailableAffordableRewards(tokenAmount: Int): List<RewardEntity>

    /**
     * Counts total custom rewards created by a caregiver.
     *
     * @param createdByUserId ID of the caregiver
     * @return Number of custom rewards created
     */
    @Query("SELECT COUNT(*) FROM rewards WHERE isCustom = 1 AND createdByUserId = :createdByUserId")
    suspend fun countCustomRewardsByCreator(createdByUserId: String): Int

    /**
     * Counts total rewards in a specific category.
     *
     * @param category Reward category name
     * @return Number of rewards in the category
     */
    @Query("SELECT COUNT(*) FROM rewards WHERE category = :category")
    suspend fun countRewardsByCategory(category: String): Int

    /**
     * Inserts a new reward into the database.
     *
     * @param reward Reward to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reward: RewardEntity)

    /**
     * Updates an existing reward in the database.
     *
     * @param reward Reward with updated information
     */
    @Update
    suspend fun update(reward: RewardEntity)

    /**
     * Deletes a reward by its identifier.
     *
     * @param rewardId ID of the reward to delete
     */
    @Query("DELETE FROM rewards WHERE id = :rewardId")
    suspend fun deleteById(rewardId: String)

    /**
     * Updates the availability status of a reward.
     *
     * @param rewardId ID of the reward to update
     * @param isAvailable New availability status
     */
    @Query("UPDATE rewards SET isAvailable = :isAvailable WHERE id = :rewardId")
    suspend fun updateAvailability(rewardId: String, isAvailable: Boolean)

    /**
     * Updates the token cost of a reward.
     *
     * @param rewardId ID of the reward to update
     * @param tokenCost New token cost
     */
    @Query("UPDATE rewards SET tokenCost = :tokenCost WHERE id = :rewardId")
    suspend fun updateTokenCost(rewardId: String, tokenCost: Int)

    /**
     * Deletes all rewards (useful for testing or data reset).
     */
    @Query("DELETE FROM rewards")
    suspend fun deleteAll()
}
