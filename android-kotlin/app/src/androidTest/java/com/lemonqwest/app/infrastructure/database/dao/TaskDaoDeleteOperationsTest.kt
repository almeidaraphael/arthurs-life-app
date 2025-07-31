package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("TaskDaoDeleteOperationsTest")
class TaskDaoDeleteOperationsTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
    }

    @Test
    fun shouldDeleteTaskById() = runTest {
        val task = EntityTestFactory.createTaskEntity(id = "task-1")
        taskDao.insert(task)
        DatabaseAssertions.verifyTaskExists(taskDao, "task-1")
        taskDao.deleteById("task-1")
        DatabaseAssertions.verifyTaskNotExists(taskDao, "task-1")
    }

    @Test
    fun shouldHandleDeletingNonExistentTask() = runTest {
        taskDao.deleteById("non-existent-task")
        DatabaseAssertions.verifyTaskCount(taskDao, 0)
    }

    @Test
    fun shouldDeleteOnlySpecifiedTask() = runTest {
        val tasks = listOf(
            EntityTestFactory.createTestTaskEntity(id = "task-1"),
            EntityTestFactory.createTestTaskEntity(id = "task-2"),
            EntityTestFactory.createTestTaskEntity(id = "task-3"),
        )
        tasks.forEach { taskDao.insert(it) }
        taskDao.deleteById("task-2")
        DatabaseAssertions.verifyTaskExists(taskDao, "task-1")
        DatabaseAssertions.verifyTaskNotExists(taskDao, "task-2")
        DatabaseAssertions.verifyTaskExists(taskDao, "task-3")
        DatabaseAssertions.verifyTaskCount(taskDao, 2)
    }

    @Test
    fun shouldDeleteAllTasks() = runTest {
        val tasks = listOf(
            EntityTestFactory.createTestTaskEntity(id = "task-1"),
            EntityTestFactory.createTestTaskEntity(id = "task-2"),
            EntityTestFactory.createTestTaskEntity(id = "task-3"),
        )
        tasks.forEach { taskDao.insert(it) }
        DatabaseAssertions.verifyTaskCount(taskDao, 3)
        taskDao.deleteAll()
        DatabaseAssertions.verifyTaskCount(taskDao, 0)
    }
}
