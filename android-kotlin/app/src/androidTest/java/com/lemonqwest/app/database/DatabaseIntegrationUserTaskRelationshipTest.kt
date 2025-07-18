package com.lemonqwest.app.database

import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.infrastructure.database.dao.UserDao
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Database integration tests for User-Task relationship operations with complete test isolation.
 *
 * These tests verify that User and Task entity relationships work correctly
 * with fresh in-memory databases per test.
 */
class DatabaseIntegrationUserTaskRelationshipTest : DatabaseTestBase() {
    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao

    @BeforeEach
    fun setupDaos() {
        userDao = database.userDao()
        taskDao = database.taskDao()
    }

    @Test
    fun getUserTasks_returnsCorrectTasks() = runTest {
        val userId = "test-user-3"
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = userId,
            name = "Test User 3",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)
        val task1 = EntityTestFactory.createTestTaskEntity(
            id = "task-1",
            title = "Task 1",
            assignedToUserId = userId,
        ).copy(category = "PERSONAL_CARE")
        val task2 = EntityTestFactory.createTestTaskEntity(
            id = "task-2",
            title = "Task 2",
            assignedToUserId = userId,
        ).copy(tokenReward = 10, isCompleted = true)
        taskDao.insert(task1)
        taskDao.insert(task2)
        val userTasks = taskDao.findByUserId(userId)
        val completedTasks = taskDao.findCompletedByUserId(userId)
        assertEquals(2, userTasks.size)
        assertEquals(1, completedTasks.size)
        assertTrue(completedTasks.first().isCompleted)
    }
}
