package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DisplayName("TaskDaoReadOperationsTest")
class TaskDaoReadOperationsTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
        val testTasks = listOf(
            EntityTestFactory.createTestTaskEntity(
                id = "task-1",
                title = "Brush teeth",
                category = TaskCategory.PERSONAL_CARE,
                tokenReward = 5,
                isCompleted = false,
                assignedToUserId = "user-1",
                customIconName = "icon_brush",
                customColorHex = "#FFD700",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "task-2",
                title = "Make bed",
                category = TaskCategory.HOUSEHOLD,
                tokenReward = 10,
                isCompleted = true,
                assignedToUserId = "user-1",
                customIconName = "icon_bed",
                customColorHex = "#00FF00",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "task-3",
                title = "Math homework",
                category = TaskCategory.HOMEWORK,
                tokenReward = 15,
                isCompleted = false,
                assignedToUserId = "user-2",
                customIconName = "icon_math",
                customColorHex = "#0000FF",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "task-4",
                title = "Reading assignment",
                category = TaskCategory.HOMEWORK,
                tokenReward = 12,
                isCompleted = true,
                assignedToUserId = "user-2",
                customIconName = "icon_book",
                customColorHex = "#FF00FF",
            ),
        )
        testTasks.forEach { taskDao.insert(it) }
    }

    @Test
    fun shouldFindTaskById() = runTest {
        val task = taskDao.findById("task-1")
        assertNotNull(task)
        assertEquals("task-1", task.id)
    }

    @Test
    fun shouldReturnNullForNonExistentId() = runTest {
        val task = taskDao.findById("non-existent")
        assertNull(task)
    }

    @Test
    fun shouldFindTasksByUserId() = runTest {
        val user1Tasks = taskDao.findByUserId("user-1")
        val user2Tasks = taskDao.findByUserId("user-2")
        assertEquals(2, user1Tasks.size)
        assertEquals(2, user2Tasks.size)
        assertTrue(user1Tasks.all { it.assignedToUserId == "user-1" })
        assertTrue(user2Tasks.all { it.assignedToUserId == "user-2" })
    }

    @Test
    fun shouldReturnEmptyListForUserWithNoTasks() = runTest {
        val tasks = taskDao.findByUserId("user-with-no-tasks")
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun shouldFindTasksByCategory() = runTest {
        val personalCareTasks = taskDao.findByCategory("PERSONAL_CARE")
        val householdTasks = taskDao.findByCategory("HOUSEHOLD")
        val homeworkTasks = taskDao.findByCategory("HOMEWORK")
        assertEquals(1, personalCareTasks.size)
        assertEquals(1, householdTasks.size)
        assertEquals(2, homeworkTasks.size)
        assertTrue(personalCareTasks.all { it.category == "PERSONAL_CARE" })
        assertTrue(householdTasks.all { it.category == "HOUSEHOLD" })
        assertTrue(homeworkTasks.all { it.category == "HOMEWORK" })
    }

    @Test
    fun shouldFindCompletedTasksByUserId() = runTest {
        val user1CompletedTasks = taskDao.findCompletedByUserId("user-1")
        val user2CompletedTasks = taskDao.findCompletedByUserId("user-2")
        assertEquals(1, user1CompletedTasks.size)
        assertEquals(1, user2CompletedTasks.size)
        assertTrue(user1CompletedTasks.all { it.isCompleted })
        assertTrue(user2CompletedTasks.all { it.isCompleted })
        assertEquals("Make bed", user1CompletedTasks[0].title)
        assertEquals("Reading assignment", user2CompletedTasks[0].title)
    }

    @Test
    fun shouldFindIncompleteTasksByUserId() = runTest {
        val user1IncompleteTasks = taskDao.findIncompleteByUserId("user-1")
        val user2IncompleteTasks = taskDao.findIncompleteByUserId("user-2")
        assertEquals(1, user1IncompleteTasks.size)
        assertEquals(1, user2IncompleteTasks.size)
        assertTrue(user1IncompleteTasks.all { !it.isCompleted })
        assertTrue(user2IncompleteTasks.all { !it.isCompleted })
        assertEquals("Brush teeth", user1IncompleteTasks[0].title)
        assertEquals("Math homework", user2IncompleteTasks[0].title)
    }

    @Test
    fun shouldGetAllTasks() = runTest {
        val allTasks = taskDao.getAllTasks()
        assertEquals(4, allTasks.size)
        val taskIds = allTasks.map { it.id }
        assertTrue(taskIds.contains("task-1"))
        assertTrue(taskIds.contains("task-2"))
        assertTrue(taskIds.contains("task-3"))
        assertTrue(taskIds.contains("task-4"))
    }

    @Test
    fun shouldOrderTasksByCreationDateDesc() = runTest {
        val now = System.currentTimeMillis()
        val tasks = listOf(
            EntityTestFactory.createTestTaskEntity(
                id = "old-task",
                createdAt = now - 3000,
                customIconName = "icon_old",
                customColorHex = "#CCCCCC",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "newest-task",
                createdAt = now,
                customIconName = "icon_new",
                customColorHex = "#00FFFF",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "middle-task",
                createdAt = now - 1000,
                customIconName = "icon_middle",
                customColorHex = "#FFCC00",
            ),
        )
        taskDao.deleteAll()
        tasks.forEach { taskDao.insert(it) }
        val orderedTasks = taskDao.getAllTasks()
        assertEquals(3, orderedTasks.size)
        assertEquals("newest-task", orderedTasks[0].id)
        assertEquals("middle-task", orderedTasks[1].id)
        assertEquals("old-task", orderedTasks[2].id)
    }
}
