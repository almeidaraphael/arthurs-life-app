package com.lemonqwest.app.domain.task

/**
 * Data source interface for task persistence operations in LemonQwest MVP.
 *
 * This interface defines the contract for task data access operations, abstracting
 * the underlying storage mechanism (in-memory, database, etc.) from the repository layer.
 *
 * The interface follows the same pattern as UserDataSource in the existing codebase,
 * maintaining consistency in the data access layer architecture.
 */
interface TaskDataSource {
    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id Unique task identifier
     * @return Task if found, null otherwise
     */
    suspend fun findById(id: String): Task?

    /**
     * Retrieves all tasks assigned to a specific user.
     *
     * @param userId ID of the user whose tasks to retrieve
     * @return List of tasks assigned to the user
     */
    suspend fun findByUserId(userId: String): List<Task>

    /**
     * Retrieves all tasks in the system.
     *
     * @return List of all tasks
     */
    suspend fun getAllTasks(): List<Task>

    /**
     * Retrieves tasks by category.
     *
     * @param category Task category to filter by
     * @return List of tasks in the specified category
     */
    suspend fun findByCategory(category: TaskCategory): List<Task>

    /**
     * Retrieves completed tasks for a specific user.
     *
     * @param userId ID of the user whose completed tasks to retrieve
     * @return List of completed tasks for the user
     */
    suspend fun findCompletedByUserId(userId: String): List<Task>

    /**
     * Retrieves incomplete tasks for a specific user.
     *
     * @param userId ID of the user whose incomplete tasks to retrieve
     * @return List of incomplete tasks for the user
     */
    suspend fun findIncompleteByUserId(userId: String): List<Task>

    /**
     * Saves a new task or updates an existing one.
     *
     * @param task Task to save or update
     */
    suspend fun saveTask(task: Task)

    /**
     * Deletes a task by its identifier.
     *
     * @param taskId ID of the task to delete
     */
    suspend fun deleteTask(taskId: String)

    /**
     * Counts total tasks completed by a user.
     *
     * @param userId ID of the user
     * @return Number of completed tasks
     */
    suspend fun countCompletedTasks(userId: String): Int

    /**
     * Counts total tokens earned by a user from completed tasks.
     *
     * @param userId ID of the user
     * @return Total tokens earned from task completion
     */
    suspend fun countTokensEarned(userId: String): Int
}
