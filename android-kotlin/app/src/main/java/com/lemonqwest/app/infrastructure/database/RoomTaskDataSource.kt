package com.lemonqwest.app.infrastructure.database

import com.lemonqwest.app.domain.task.Task
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.TaskDataSource
import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.infrastructure.database.entities.TaskEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room database implementation of TaskDataSource for LemonQwest MVP.
 *
 * This implementation provides task persistence using Room database,
 * replacing the in-memory storage with proper data persistence.
 * It includes conversion between domain models and database entities.
 *
 * The implementation ensures thread-safe operations through Room's
 * built-in coroutine support and proper database transactions.
 */
@Singleton
class RoomTaskDataSource
@Inject
constructor(
    private val taskDao: TaskDao,
) : TaskDataSource {

    override suspend fun findById(id: String): Task? =
        taskDao.findById(id)?.toDomain()

    override suspend fun findByUserId(userId: String): List<Task> =
        taskDao.findByUserId(userId).map { it.toDomain() }

    override suspend fun getAllTasks(): List<Task> =
        taskDao.getAllTasks().map { it.toDomain() }

    override suspend fun findByCategory(category: TaskCategory): List<Task> =
        taskDao.findByCategory(category.name).map { it.toDomain() }

    override suspend fun findCompletedByUserId(userId: String): List<Task> =
        taskDao.findCompletedByUserId(userId).map { it.toDomain() }

    override suspend fun findIncompleteByUserId(userId: String): List<Task> =
        taskDao.findIncompleteByUserId(userId).map { it.toDomain() }

    override suspend fun saveTask(task: Task) {
        taskDao.insert(TaskEntity.fromDomain(task))
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteById(taskId)
    }

    override suspend fun countCompletedTasks(userId: String): Int =
        taskDao.countCompletedTasks(userId)

    override suspend fun countTokensEarned(userId: String): Int =
        taskDao.countTokensEarned(userId) ?: 0
}
