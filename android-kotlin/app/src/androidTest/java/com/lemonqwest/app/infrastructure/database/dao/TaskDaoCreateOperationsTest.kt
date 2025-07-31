package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("TaskDaoCreateOperationsTest")
class TaskDaoCreateOperationsTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
    }

    @Test
    fun shouldInsertTask() = runTest {
        val task = EntityTestFactory.createTestTaskEntity(
            id = "task-1",
            title = "Brush teeth",
            assignedToUserId = "user-1",
        ).copy(
            category = "PERSONAL_CARE",
            tokenReward = 5,
            customIconName = "default_icon",
            customColorHex = "#FFFFFF",
        )
        taskDao.insert(task)
        val retrievedTask = taskDao.findById("task-1")
        assertNotNull(retrievedTask)
        assertEquals(task.id, retrievedTask.id)
    }

    @Test
    fun shouldInsertCompletedTask() = runTest {
        val task = EntityTestFactory.createTestTaskEntity(
            id = "completed-task",
            title = "Make bed",
            assignedToUserId = "user-1",
        ).copy(
            category = "HOUSEHOLD",
            tokenReward = 10,
            isCompleted = true,
            completedAt = System.currentTimeMillis(),
            customIconName = "icon_bed",
            customColorHex = "#00FF00",
        )
        taskDao.insert(task)
        val retrievedTask = taskDao.findById("completed-task")
        assertNotNull(retrievedTask)
        assertEquals(true, retrievedTask.isCompleted)
    }

    @Test
    fun shouldReplaceTaskOnConflict() = runTest {
        val originalTask = EntityTestFactory.createTestTaskEntity(
            id = "task-1",
            title = "Original Title",
            assignedToUserId = "user-1",
        ).copy(
            category = "PERSONAL_CARE",
            tokenReward = 5,
            customIconName = "icon_brush",
            customColorHex = "#FFD700",
        )
        val updatedTask = EntityTestFactory.createTestTaskEntity(
            id = "task-1",
            title = "Updated Title",
            assignedToUserId = "user-1",
        ).copy(
            category = "PERSONAL_CARE",
            tokenReward = 10,
            customIconName = "icon_brush",
            customColorHex = "#FFD700",
        )
        taskDao.insert(originalTask)
        taskDao.insert(updatedTask)
        val retrievedTask = taskDao.findById("task-1")
        assertNotNull(retrievedTask)
        assertEquals("Updated Title", retrievedTask.title)
        assertEquals(10, retrievedTask.tokenReward)
    }

    @Test
    fun shouldInsertMultipleTasks() = runTest {
        val tasks = listOf(
            EntityTestFactory.createTestTaskEntity(
                id = "task-1",
                title = "Brush teeth",
                assignedToUserId = "user-1",
                customIconName = "default_icon",
                customColorHex = "#FFFFFF",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "task-2",
                title = "Make bed",
                assignedToUserId = "user-1",
                customIconName = "default_icon",
                customColorHex = "#FFFFFF",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "task-3",
                title = "Math homework",
                assignedToUserId = "user-2",
                customIconName = "default_icon",
                customColorHex = "#FFFFFF",
            ),
        )
        tasks.forEach { taskDao.insert(it) }
        DatabaseAssertions.verifyTaskCount(taskDao, 3)
        tasks.forEach { task -> DatabaseAssertions.verifyTaskExists(taskDao, task.id) }
    }
}
