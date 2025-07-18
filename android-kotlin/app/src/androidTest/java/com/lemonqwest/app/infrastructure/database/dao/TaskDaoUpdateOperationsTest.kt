package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("TaskDaoUpdateOperationsTest")
class TaskDaoUpdateOperationsTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
    }

    @Test
    fun shouldUpdateTask() = runTest {
        val originalTask = EntityTestFactory.createTaskEntity(
            id = "task-1",
            title = "Original Title",
            tokenReward = 5,
            isCompleted = false,
        )
        taskDao.insert(originalTask)
        val updatedTask = originalTask.copy(
            title = "Updated Title",
            tokenReward = 10,
            isCompleted = true,
        )
        taskDao.update(updatedTask)
        val retrievedTask = taskDao.findById("task-1")
        assertNotNull(retrievedTask)
        assertEquals("Updated Title", retrievedTask.title)
        assertEquals(10, retrievedTask.tokenReward)
        assertEquals(true, retrievedTask.isCompleted)
    }

    @Test
    fun shouldUpdateTaskCompletionStatus() = runTest {
        val task = EntityTestFactory.createTaskEntity(id = "task-1", isCompleted = false)
        taskDao.insert(task)
        val completedTask = task.copy(isCompleted = true)
        taskDao.update(completedTask)
        DatabaseAssertions.verifyTaskCompletion(taskDao, "task-1", true)
    }

    @Test
    fun shouldUpdateTaskCategory() = runTest {
        val task = EntityTestFactory.createTaskEntity(
            id = "task-1",
            category = com.lemonqwest.app.domain.task.TaskCategory.PERSONAL_CARE,
        )
        taskDao.insert(task)
        val updatedTask = task.copy(
            category = com.lemonqwest.app.domain.task.TaskCategory.HOUSEHOLD.name,
        )
        taskDao.update(updatedTask)
        val retrievedTask = taskDao.findById("task-1")
        assertNotNull(retrievedTask)
        assertEquals("HOUSEHOLD", retrievedTask.category)
    }

    @Test
    fun shouldUpdateTaskTokenReward() = runTest {
        val task = EntityTestFactory.createTaskEntity(id = "task-1", tokenReward = 5)
        taskDao.insert(task)
        val updatedTask = task.copy(tokenReward = 15)
        taskDao.update(updatedTask)
        val retrievedTask = taskDao.findById("task-1")
        assertNotNull(retrievedTask)
        assertEquals(15, retrievedTask.tokenReward)
    }
}
