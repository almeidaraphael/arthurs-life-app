package com.lemonqwest.app.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.infrastructure.database.dao.UserDao
import com.lemonqwest.app.testutils.EntityTestFactory
import com.lemonqwest.app.testutils.ViewModelTestBase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseIntegrationUserTaskRelationshipTest : ViewModelTestBase() {
    private lateinit var database: LemonQwestDatabase
    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao

    @BeforeEach
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LemonQwestDatabase::class.java,
        ).allowMainThreadQueries().build()
        userDao = database.userDao()
        taskDao = database.taskDao()
    }

    @AfterEach
    fun teardown() {
        database.close()
    }

    @Test
    fun getUserTasks_returnsCorrectTasks() = runViewModelTest {
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
