package com.lemonqwest.app.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lemonqwest.app.infrastructure.database.entities.AchievementEntity

/**
 * Data Access Object for achievement database operations.
 *
 * This DAO provides thread-safe database access for achievement persistence,
 * supporting all required operations for the achievement tracking system.
 * All suspend functions are designed to work with Room's coroutine support.
 */
@Dao
interface AchievementDao {

    /**
     * Inserts a new achievement into the database.
     *
     * @param achievement The achievement entity to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    /**
     * Inserts multiple achievements into the database.
     *
     * @param achievements List of achievement entities to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    /**
     * Updates an existing achievement in the database.
     *
     * @param achievement The achievement entity to update
     */
    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    /**
     * Finds an achievement by its unique ID.
     *
     * @param achievementId The unique identifier of the achievement
     * @return The achievement entity if found, null otherwise
     */
    @Query("SELECT * FROM achievements WHERE id = :achievementId")
    suspend fun findById(achievementId: String): AchievementEntity?

    /**
     * Finds all achievements for a specific user.
     *
     * @param userId The unique identifier of the user
     * @return List of all achievements for the user
     */
    @Query("SELECT * FROM achievements WHERE user_id = :userId ORDER BY id ASC")
    suspend fun findByUserId(userId: String): List<AchievementEntity>

    /**
     * Finds all unlocked achievements for a specific user.
     *
     * @param userId The unique identifier of the user
     * @return List of unlocked achievements for the user
     */
    @Query(
        "SELECT * FROM achievements WHERE user_id = :userId AND is_unlocked = 1 ORDER BY unlocked_at DESC",
    )
    suspend fun findUnlockedByUserId(userId: String): List<AchievementEntity>

    /**
     * Finds all locked achievements for a specific user.
     *
     * @param userId The unique identifier of the user
     * @return List of locked achievements for the user
     */
    @Query(
        "SELECT * FROM achievements WHERE user_id = :userId AND is_unlocked = 0 ORDER BY progress DESC",
    )
    suspend fun findLockedByUserId(userId: String): List<AchievementEntity>

    /**
     * Finds achievements by category for a specific user.
     *
     * @param userId The unique identifier of the user
     * @param category The achievement category to filter by
     * @return List of achievements in the specified category
     */
    @Query(
        "SELECT * FROM achievements WHERE user_id = :userId AND type IN (:achievementTypes) ORDER BY id ASC",
    )
    suspend fun findByUserIdAndCategory(
        userId: String,
        achievementTypes: List<String>,
    ): List<AchievementEntity>

    /**
     * Finds a specific achievement by user ID and achievement type.
     *
     * @param userId The unique identifier of the user
     * @param achievementType The type of achievement to find
     * @return The achievement entity if found, null otherwise
     */
    @Query("SELECT * FROM achievements WHERE user_id = :userId AND type = :achievementType")
    suspend fun findByUserIdAndType(userId: String, achievementType: String): AchievementEntity?

    /**
     * Counts the total number of achievements for a user.
     *
     * @param userId The unique identifier of the user
     * @return Total number of achievements for the user
     */
    @Query("SELECT COUNT(*) FROM achievements WHERE user_id = :userId")
    suspend fun countByUserId(userId: String): Int

    /**
     * Counts the number of unlocked achievements for a user.
     *
     * @param userId The unique identifier of the user
     * @return Number of unlocked achievements for the user
     */
    @Query("SELECT COUNT(*) FROM achievements WHERE user_id = :userId AND is_unlocked = 1")
    suspend fun countUnlockedByUserId(userId: String): Int

    /**
     * Checks if a user has any achievements initialized.
     *
     * @param userId The unique identifier of the user
     * @return True if user has achievements, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM achievements WHERE user_id = :userId)")
    suspend fun hasAchievements(userId: String): Boolean

    /**
     * Deletes all achievements for a specific user.
     * This method is primarily for testing and administrative purposes.
     *
     * @param userId The unique identifier of the user
     */
    @Query("DELETE FROM achievements WHERE user_id = :userId")
    suspend fun deleteByUserId(userId: String)

    /**
     * Deletes all achievements from the database.
     * This method is primarily for testing purposes.
     */
    @Query("DELETE FROM achievements")
    suspend fun deleteAll()
}
