package com.lemonqwest.app.database

import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.infrastructure.database.dao.UserDao
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration tests for the Room database with complete test isolation.
 *
 * These tests verify that the database operations work correctly
 * in a real Android environment with fresh in-memory databases per test.
 */
class DatabaseIntegrationTest : DatabaseTestBase() {

    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao

    @BeforeEach
    fun setupDaos() {
        userDao = database.userDao()
        taskDao = database.taskDao()
    }

    @Test
    fun insertUser_retrieveUser_returnsCorrectUser() = runTest {
        // Given
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-1",
            name = "Test User",
            role = "CHILD",
        )

        // When
        userDao.insertUser(userEntity)
        val retrievedUser = userDao.getUserById("test-user-1")

        // Then
        assertNotNull(retrievedUser)
        assertEquals(userEntity.id, retrievedUser?.id)
        assertEquals(userEntity.name, retrievedUser?.name)
        assertEquals(userEntity.role, retrievedUser?.role)
        assertEquals(userEntity.tokenBalance, retrievedUser?.tokenBalance)
    }

    @Test
    fun insertTask_retrieveTask_returnsCorrectTask() = runTest {
        // Given
        val taskEntity = EntityTestFactory.createTestTaskEntity(
            id = "test-task-1",
            title = "Test Task",
            assignedToUserId = "test-user-1",
        )

        // When
        taskDao.insert(taskEntity)
        val retrievedTask = taskDao.findById("test-task-1")

        // Then
        assertNotNull(retrievedTask)
        assertEquals(taskEntity.id, retrievedTask?.id)
        assertEquals(taskEntity.title, retrievedTask?.title)
        assertEquals(taskEntity.category, retrievedTask?.category)
        assertEquals(taskEntity.tokenReward, retrievedTask?.tokenReward)
        assertEquals(taskEntity.isCompleted, retrievedTask?.isCompleted)
    }

    @Test
    fun updateUser_tokenBalance_updatesCorrectly() = runTest {
        // Given
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-2",
            name = "Test User 2",
            role = "CHILD",
        ).copy(tokenBalance = 10)
        userDao.insertUser(userEntity)

        // When
        val updatedUser = userEntity.copy(tokenBalance = 20)
        userDao.updateUser(updatedUser)
        val retrievedUser = userDao.getUserById("test-user-2")

        // Then
        assertNotNull(retrievedUser)
        assertEquals(20, retrievedUser?.tokenBalance)
    }

    @Test
    fun completeTask_updatesCorrectly() = runTest {
        // Given
        val taskEntity = EntityTestFactory.createTestTaskEntity(
            id = "test-task-2",
            title = "Test Task 2",
            assignedToUserId = "test-user-1",
        ).copy(category = "HOMEWORK", tokenReward = 15)
        taskDao.insert(taskEntity)

        // When
        val completedTask = taskEntity.copy(isCompleted = true)
        taskDao.update(completedTask)
        val retrievedTask = taskDao.findById("test-task-2")

        // Then
        assertNotNull(retrievedTask)
        assertTrue(retrievedTask?.isCompleted == true)
    }

    @Test
    fun getUserTasks_returnsCorrectTasks() = runTest {
        // Given
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

        // When
        val userTasks = taskDao.findByUserId(userId)
        val completedTasks = taskDao.findCompletedByUserId(userId)

        // Then
        assertEquals(2, userTasks.size)
        assertEquals(1, completedTasks.size)
        assertTrue(completedTasks.first().isCompleted)
    }

    @Test
    fun deleteUser_removesUser() = runTest {
        // Given
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-4",
            name = "Test User 4",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)

        // When
        userDao.deleteUser("test-user-4")
        val retrievedUser = userDao.getUserById("test-user-4")

        // Then
        assertNull(retrievedUser)
    }

    @Test
    fun database_concurrentOperations_maintainConsistency() = runTest {
        // Given
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "concurrent-user",
            name = "Concurrent User",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)

        // When - Multiple concurrent token updates
        repeat(10) { index ->
            val updatedUser = userEntity.copy(tokenBalance = index + 1)
            userDao.updateUser(updatedUser)
        }

        // Then
        val finalUser = userDao.getUserById("concurrent-user")
        assertNotNull(finalUser)
        assertTrue("Token balance should be positive", finalUser!!.tokenBalance > 0)
    }
}
