package com.lemonqwest.app.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lemonqwest.app.infrastructure.database.entities.TaskEntity

/**
 * Data Access Object for task database operations in LemonQwest MVP.
 *
 * This DAO provides the database operations needed for task management,
 * following Room database patterns used throughout the application.
 * All operations are suspend functions to support coroutine-based async operations.
 */
@Dao
interface TaskDao {
    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id Task identifier
     * @return TaskEntity if found, null otherwise
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun findById(id: String): TaskEntity?

    /**
     * Retrieves all tasks assigned to a specific user.
     *
     * @param userId ID of the user whose tasks to retrieve
     * @return List of tasks assigned to the user
     */
    @Query("SELECT * FROM tasks WHERE assignedToUserId = :userId ORDER BY createdAt DESC")
    suspend fun findByUserId(userId: String): List<TaskEntity>

    /**
     * Retrieves all tasks in the system.
     *
     * @return List of all tasks ordered by creation date
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    suspend fun getAllTasks(): List<TaskEntity>

    /**
     * Retrieves tasks by category.
     *
     * @param category Task category name
     * @return List of tasks in the specified category
     */
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    suspend fun findByCategory(category: String): List<TaskEntity>

    /**
     * Retrieves completed tasks for a specific user.
     *
     * @param userId ID of the user whose completed tasks to retrieve
     * @return List of completed tasks for the user
     */
    @Query(
        "SELECT * FROM tasks WHERE assignedToUserId = :userId AND isCompleted = 1 ORDER BY createdAt DESC",
    )
    suspend fun findCompletedByUserId(userId: String): List<TaskEntity>

    /**
     * Retrieves incomplete tasks for a specific user.
     *
     * @param userId ID of the user whose incomplete tasks to retrieve
     * @return List of incomplete tasks for the user
     */
    @Query(
        "SELECT * FROM tasks WHERE assignedToUserId = :userId AND isCompleted = 0 ORDER BY createdAt DESC",
    )
    suspend fun findIncompleteByUserId(userId: String): List<TaskEntity>

    /**
     * Counts total tasks completed by a user.
     *
     * @param userId ID of the user
     * @return Number of completed tasks
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE assignedToUserId = :userId AND isCompleted = 1")
    suspend fun countCompletedTasks(userId: String): Int

    /**
     * Counts total tokens earned by a user from completed tasks.
     *
     * @param userId ID of the user
     * @return Total tokens earned from task completion
     */
    @Query(
        "SELECT SUM(tokenReward) FROM tasks WHERE assignedToUserId = :userId AND isCompleted = 1",
    )
    suspend fun countTokensEarned(userId: String): Int?

    /**
     * Inserts a new task into the database.
     *
     * @param task Task to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    /**
     * Updates an existing task in the database.
     *
     * @param task Task with updated information
     */
    @Update
    suspend fun update(task: TaskEntity)

    /**
     * Deletes a task by its identifier.
     *
     * @param taskId ID of the task to delete
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: String)

    /**
     * Deletes all tasks (useful for testing or data reset).
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}
