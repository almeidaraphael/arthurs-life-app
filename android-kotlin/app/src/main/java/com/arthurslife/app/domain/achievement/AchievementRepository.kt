package com.arthurslife.app.domain.achievement

/**
 * Repository interface for achievement management operations in Arthur's Life MVP.
 *
 * This interface defines the contract for achievement data operations,
 * supporting the basic operations needed for the MVP achievement system.
 * The implementation handles data persistence while this interface
 * maintains domain separation.
 */
interface AchievementRepository {
    /**
     * Retrieves an achievement by its unique identifier.
     *
     * @param id Unique achievement identifier
     * @return Achievement if found, null otherwise
     */
    suspend fun findById(id: String): Achievement?

    /**
     * Retrieves all achievements for a specific user.
     *
     * @param userId ID of the user whose achievements to retrieve
     * @return List of achievements for the user
     */
    suspend fun findByUserId(userId: String): List<Achievement>

    /**
     * Retrieves unlocked achievements for a specific user.
     *
     * @param userId ID of the user whose unlocked achievements to retrieve
     * @return List of unlocked achievements for the user
     */
    suspend fun findUnlockedByUserId(userId: String): List<Achievement>

    /**
     * Retrieves achievements by category for a specific user.
     *
     * @param userId ID of the user
     * @param category Achievement category to filter by
     * @return List of achievements in the specified category
     */
    suspend fun findByUserIdAndCategory(
        userId: String,
        category: AchievementCategory,
    ): List<Achievement>

    /**
     * Retrieves a specific achievement type for a user.
     *
     * @param userId ID of the user
     * @param type Achievement type to find
     * @return Achievement of the specified type for the user, null if not found
     */
    suspend fun findByUserIdAndType(userId: String, type: AchievementType): Achievement?

    /**
     * Saves a new achievement or updates an existing one.
     *
     * @param achievement Achievement to save or update
     */
    suspend fun saveAchievement(achievement: Achievement)

    /**
     * Updates an existing achievement.
     *
     * @param achievement Achievement with updated information
     */
    suspend fun updateAchievement(achievement: Achievement)

    /**
     * Initializes achievements for a new user.
     * Creates all achievement types with zero progress.
     *
     * @param userId ID of the user to initialize achievements for
     */
    suspend fun initializeAchievementsForUser(userId: String)

    /**
     * Counts total unlocked achievements for a user.
     *
     * @param userId ID of the user
     * @return Number of unlocked achievements
     */
    suspend fun countUnlockedAchievements(userId: String): Int
}
