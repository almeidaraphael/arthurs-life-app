package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("TaskDaoDataIntegrityTest")
class TaskDaoDataIntegrityTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
    }

    @Test
    fun shouldPreserveAllTaskFields() = runTest {
        val currentTime = System.currentTimeMillis()
        val task = EntityTestFactory.createTestTaskEntity(
            id = "test-task",
            title = "Test Task",
            assignedToUserId = "test-user",
        ).copy(
            category = "PERSONAL_CARE",
            tokenReward = 42,
            isCompleted = true,
            createdAt = currentTime,
            completedAt = currentTime,
            customIconName = "default_icon",
            customColorHex = "#FFFFFF",
        )
        taskDao.insert(task)
        val retrievedTask = taskDao.findById("test-task")
        assertNotNull(retrievedTask)
        assertEquals(task.id, retrievedTask.id)
        assertEquals(task.title, retrievedTask.title)
        assertEquals(task.category, retrievedTask.category)
        assertEquals(task.tokenReward, retrievedTask.tokenReward)
        assertEquals(task.isCompleted, retrievedTask.isCompleted)
        assertEquals(task.assignedToUserId, retrievedTask.assignedToUserId)
        assertEquals(task.createdAt, retrievedTask.createdAt)
    }

    @Test
    fun shouldHandleZeroTokenReward() = runTest {
        val task = EntityTestFactory.createTaskEntity(id = "zero-reward-task", tokenReward = 0)
        taskDao.insert(task)
        val retrievedTask = taskDao.findById("zero-reward-task")
        assertNotNull(retrievedTask)
        assertEquals(0, retrievedTask.tokenReward)
    }

    @Test
    fun shouldHandleLargeTokenReward() = runTest {
        val task = EntityTestFactory.createTaskEntity(id = "high-reward-task", tokenReward = 999999)
        taskDao.insert(task)
        val retrievedTask = taskDao.findById("high-reward-task")
        assertNotNull(retrievedTask)
        assertEquals(999999, retrievedTask.tokenReward)
    }

    @Test
    fun shouldHandleLongTaskTitles() = runTest {
        val longTitle = "This is a very long task title that might test the database's ability to handle longer strings without truncation or corruption"
        val task = EntityTestFactory.createTaskEntity(id = "long-title-task", title = longTitle)
        taskDao.insert(task)
        val retrievedTask = taskDao.findById("long-title-task")
        assertNotNull(retrievedTask)
        assertEquals(longTitle, retrievedTask.title)
    }
}
