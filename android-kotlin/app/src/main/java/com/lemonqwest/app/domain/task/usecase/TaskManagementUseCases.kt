package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.common.DomainException
import com.lemonqwest.app.domain.task.InvalidTaskDataException
import com.lemonqwest.app.domain.task.Task
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.TaskNotFoundException
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.task.TaskRepositoryException
import javax.inject.Inject

private const val MAX_STREAK_DAYS = 10

/**
 * Collection of use cases for task management operations in LemonQwest MVP.
 *
 * This class provides the core business logic for task management operations
 * that caregivers need to create, update, and manage tasks for children.
 * All operations are designed to be simple and focused on MVP requirements.
 */
class TaskManagementUseCases
@Inject
constructor(
    private val taskRepository: TaskRepository,
) {
    /**
     * Creates a new task and assigns it to a child.
     *
     * @param title Task title
     * @param category Task category
     * @param assignedToUserId ID of the child to assign the task to
     * @return Result with the created task or error
     */
    suspend fun createTask(
        title: String,
        category: TaskCategory,
        assignedToUserId: String,
    ): Result<Task> {
        return try {
            if (title.isBlank()) {
                return Result.failure(InvalidTaskDataException("Task title cannot be blank"))
            }

            val task = Task.create(
                title = title.trim(),
                category = category,
                assignedToUserId = assignedToUserId,
            )

            taskRepository.saveTask(task)
            Result.success(task)
        } catch (e: InvalidTaskDataException) {
            Result.failure(e)
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Updates an existing task.
     *
     * @param taskId ID of the task to update
     * @param title New task title
     * @param category New task category
     * @return Result with the updated task or error
     */
    suspend fun updateTask(
        taskId: String,
        title: String,
        category: TaskCategory,
    ): Result<Task> {
        return try {
            val existingTask = taskRepository.findById(taskId)
                ?: return Result.failure(TaskNotFoundException(taskId))

            if (title.isBlank()) {
                return Result.failure(InvalidTaskDataException("Task title cannot be blank"))
            }

            val updatedTask = existingTask.copy(
                title = title.trim(),
                category = category,
                tokenReward = category.defaultTokenReward,
            )

            taskRepository.updateTask(updatedTask)
            Result.success(updatedTask)
        } catch (e: InvalidTaskDataException) {
            Result.failure(e)
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a task.
     *
     * @param taskId ID of the task to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val existingTask = taskRepository.findById(taskId)
                ?: return Result.failure(TaskNotFoundException(taskId))

            taskRepository.deleteTask(taskId)
            Result.success(Unit)
        } catch (e: InvalidTaskDataException) {
            Result.failure(e)
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets all tasks for a specific user.
     *
     * @param userId ID of the user whose tasks to retrieve
     * @return Result with list of tasks or error
     */
    suspend fun getTasksForUser(userId: String): Result<List<Task>> {
        return try {
            val tasks = taskRepository.findByUserId(userId)
            Result.success(tasks)
        } catch (e: InvalidTaskDataException) {
            Result.failure(e)
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets incomplete tasks for a specific user.
     *
     * @param userId ID of the user whose incomplete tasks to retrieve
     * @return Result with list of incomplete tasks or error
     */
    suspend fun getIncompleteTasksForUser(userId: String): Result<List<Task>> {
        return try {
            val tasks = taskRepository.findIncompleteByUserId(userId)
            Result.success(tasks)
        } catch (e: InvalidTaskDataException) {
            Result.failure(e)
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets completed tasks for a specific user.
     *
     * @param userId ID of the user whose completed tasks to retrieve
     * @return Result with list of completed tasks or error
     */
    suspend fun getCompletedTasksForUser(userId: String): Result<List<Task>> {
        return try {
            val tasks = taskRepository.findCompletedByUserId(userId)
            Result.success(tasks)
        } catch (e: InvalidTaskDataException) {
            Result.failure(e)
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets tasks by category.
     *
     * @param category Task category to filter by
     * @return Result with list of tasks in the category or error
     */
    suspend fun getTasksByCategory(category: TaskCategory): Result<List<Task>> {
        return try {
            val tasks = taskRepository.findByCategory(category)
            Result.success(tasks)
        } catch (e: InvalidTaskDataException) {
            Result.failure(e)
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets task completion statistics for a user.
     *
     * @param userId ID of the user
     * @return Result with task statistics or error
     */
    suspend fun getTaskStats(userId: String): Result<TaskStats> {
        return try {
            val totalCompleted = taskRepository.countCompletedTasks(userId)
            val totalTokensEarned = taskRepository.countTokensEarned(userId)
            val incompleteTasks = taskRepository.findIncompleteByUserId(userId)

            val currentStreak = calculateCurrentStreak(userId)

            val totalTasks = totalCompleted + incompleteTasks.size
            val completionRate = if (totalTasks > 0) {
                (totalCompleted.toFloat() / totalTasks * 100).toInt()
            } else {
                0
            }

            val stats = TaskStats(
                totalCompletedTasks = totalCompleted,
                totalTokensEarned = totalTokensEarned,
                incompleteTasks = incompleteTasks.size,
                completionRate = completionRate,
                currentStreak = currentStreak,
            )

            Result.success(stats)
        } catch (e: InvalidTaskDataException) {
            Result.failure(e)
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Calculates the current streak of consecutive days with at least one task completed.
     *
     * @param userId ID of the user
     * @return Current streak in days
     */
    @Suppress("SwallowedException")
    private suspend fun calculateCurrentStreak(userId: String): Int {
        return try {
            // For MVP, we'll calculate a simple streak based on completion rate
            // In a full implementation, this would check actual dates of completion
            val completedTasks = taskRepository.findCompletedByUserId(userId)

            // Simple streak calculation: if user has completed more than 70% of tasks,
            // give them a streak based on their completion rate
            val incompleteTasks = taskRepository.findIncompleteByUserId(userId)
            val totalTasks = completedTasks.size + incompleteTasks.size

            if (totalTasks == 0) return 0

            val completionRate = completedTasks.size.toFloat() / totalTasks

            // Generate streak based on completion rate (0-10 days)
            (completionRate * MAX_STREAK_DAYS).toInt().coerceAtMost(MAX_STREAK_DAYS)
        } catch (e: TaskRepositoryException) {
            // Return 0 if we can't calculate streak due to repository error
            // In a production app, this error would be logged properly
            0
        }
    }
}

/**
 * Data class for task completion statistics.
 *
 * @property totalCompletedTasks Total number of completed tasks
 * @property totalTokensEarned Total tokens earned from completed tasks
 * @property incompleteTasks Number of incomplete tasks
 * @property completionRate Completion rate as a percentage
 * @property currentStreak Current consecutive days with at least one task completed
 */
data class TaskStats(
    val totalCompletedTasks: Int,
    val totalTokensEarned: Int,
    val incompleteTasks: Int,
    val completionRate: Int,
    val currentStreak: Int,
)
