package com.arthurslife.app.infrastructure.task

import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.TaskDataSource
import com.arthurslife.app.domain.task.TaskRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TaskRepository that delegates to TaskDataSource.
 *
 * This implementation follows the Repository pattern used throughout the application,
 * providing a layer of abstraction between the domain and data access layers.
 * It delegates all operations to the injected TaskDataSource implementation.
 *
 * The repository handles the coordination of data access operations while maintaining
 * clean separation between domain logic and data persistence concerns.
 */
@Singleton
class TaskRepositoryImpl
@Inject
constructor(
    private val taskDataSource: TaskDataSource,
) : TaskRepository {

    override suspend fun findById(id: String): Task? = taskDataSource.findById(id)

    override suspend fun findByUserId(userId: String): List<Task> = taskDataSource.findByUserId(
        userId,
    )

    override suspend fun getAllTasks(): List<Task> = taskDataSource.getAllTasks()

    override suspend fun findByCategory(category: TaskCategory): List<Task> = taskDataSource.findByCategory(
        category,
    )

    override suspend fun findCompletedByUserId(userId: String): List<Task> = taskDataSource.findCompletedByUserId(
        userId,
    )

    override suspend fun findIncompleteByUserId(userId: String): List<Task> = taskDataSource.findIncompleteByUserId(
        userId,
    )

    override suspend fun saveTask(task: Task) = taskDataSource.saveTask(task)

    override suspend fun updateTask(task: Task) = taskDataSource.saveTask(task)

    override suspend fun deleteTask(taskId: String) = taskDataSource.deleteTask(taskId)

    override suspend fun countCompletedTasks(userId: String): Int = taskDataSource.countCompletedTasks(
        userId,
    )

    override suspend fun countTokensEarned(userId: String): Int = taskDataSource.countTokensEarned(
        userId,
    )
}
