package com.lemonqwest.app.database

import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Database integration tests for Task entity persistence with complete test isolation.
 *
 * These tests verify that Task entity operations work correctly
 * with fresh in-memory databases per test.
 */
class DatabaseIntegrationTaskEntityTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    fun setupDao() {
        taskDao = database.taskDao()
    }

    @Test
    fun insertTask_retrieveTask_returnsCorrectTask() = runTest {
        val taskEntity = EntityTestFactory.createTestTaskEntity(
            id = "test-task-1",
            title = "Test Task",
            assignedToUserId = "test-user-1",
        )
        taskDao.insert(taskEntity)
        val retrievedTask = taskDao.findById("test-task-1")
        assertNotNull(retrievedTask)
        assertEquals(taskEntity.id, retrievedTask?.id)
        assertEquals(taskEntity.title, retrievedTask?.title)
        assertEquals(taskEntity.category, retrievedTask?.category)
        assertEquals(taskEntity.tokenReward, retrievedTask?.tokenReward)
        assertEquals(taskEntity.isCompleted, retrievedTask?.isCompleted)
    }

    @Test
    fun completeTask_updatesCorrectly() = runTest {
        val taskEntity = EntityTestFactory.createTestTaskEntity(
            id = "test-task-2",
            title = "Test Task 2",
            assignedToUserId = "test-user-1",
        ).copy(category = "HOMEWORK", tokenReward = 15)
        taskDao.insert(taskEntity)
        val completedTask = taskEntity.copy(isCompleted = true)
        taskDao.update(completedTask)
        val retrievedTask = taskDao.findById("test-task-2")
        assertNotNull(retrievedTask)
        assertEquals(true, retrievedTask?.isCompleted)
    }
}
