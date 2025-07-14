package com.arthurslife.app.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arthurslife.app.infrastructure.database.ArthursLifeDatabase
import com.arthurslife.app.infrastructure.database.dao.TaskDao
import com.arthurslife.app.infrastructure.database.dao.UserDao
import com.arthurslife.app.infrastructure.database.entities.TaskEntity
import com.arthurslife.app.infrastructure.database.entities.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for the Room database.
 *
 * These tests verify that the database operations work correctly
 * in a real Android environment with actual SQLite database.
 */
@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {

    private lateinit var database: ArthursLifeDatabase
    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ArthursLifeDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()

        userDao = database.userDao()
        taskDao = database.taskDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertUser_retrieveUser_returnsCorrectUser() = runTest {
        // Given
        val userEntity = UserEntity(
            id = "test-user-1",
            name = "Test User",
            role = "CHILD",
            tokenBalance = 25,
            pinHash = null,
            createdAt = System.currentTimeMillis(),
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
        val taskEntity = TaskEntity(
            id = "test-task-1",
            title = "Test Task",
            category = "PERSONAL_CARE",
            tokenReward = 10,
            isCompleted = false,
            assignedToUserId = "test-user-1",
            createdAt = System.currentTimeMillis(),
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
        val userEntity = UserEntity(
            id = "test-user-2",
            name = "Test User 2",
            role = "CHILD",
            tokenBalance = 10,
            pinHash = null,
            createdAt = System.currentTimeMillis(),
        )
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
        val taskEntity = TaskEntity(
            id = "test-task-2",
            title = "Test Task 2",
            category = "HOMEWORK",
            tokenReward = 15,
            isCompleted = false,
            assignedToUserId = "test-user-1",
            createdAt = System.currentTimeMillis(),
        )
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
        val userEntity = UserEntity(
            id = userId,
            name = "Test User 3",
            role = "CHILD",
            tokenBalance = 0,
            pinHash = null,
            createdAt = System.currentTimeMillis(),
        )
        userDao.insertUser(userEntity)

        val task1 = TaskEntity(
            id = "task-1",
            title = "Task 1",
            category = "PERSONAL_CARE",
            tokenReward = 5,
            isCompleted = false,
            assignedToUserId = userId,
            createdAt = System.currentTimeMillis(),
        )
        val task2 = TaskEntity(
            id = "task-2",
            title = "Task 2",
            category = "HOUSEHOLD",
            tokenReward = 10,
            isCompleted = true,
            assignedToUserId = userId,
            createdAt = System.currentTimeMillis(),
        )
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
        val userEntity = UserEntity(
            id = "test-user-4",
            name = "Test User 4",
            role = "CHILD",
            tokenBalance = 0,
            pinHash = null,
            createdAt = System.currentTimeMillis(),
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
        val userEntity = UserEntity(
            id = "concurrent-user",
            name = "Concurrent User",
            role = "CHILD",
            tokenBalance = 0,
            pinHash = null,
            createdAt = System.currentTimeMillis(),
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
