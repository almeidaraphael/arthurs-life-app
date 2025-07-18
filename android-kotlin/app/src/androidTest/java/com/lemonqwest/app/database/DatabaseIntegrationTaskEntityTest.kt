package com.lemonqwest.app.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.testutils.EntityTestFactory
import com.lemonqwest.app.testutils.ViewModelTestBase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseIntegrationTaskEntityTest : ViewModelTestBase() {
    private lateinit var database: LemonQwestDatabase
    private lateinit var taskDao: TaskDao

    @BeforeEach
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LemonQwestDatabase::class.java,
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
    }

    @AfterEach
    fun teardown() {
        database.close()
    }

    @Test
    fun insertTask_retrieveTask_returnsCorrectTask() = runViewModelTest {
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
    fun completeTask_updatesCorrectly() = runViewModelTest {
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
