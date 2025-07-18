package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("TaskDaoPerformanceTest")
class TaskDaoPerformanceTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
    }

    @Test
    fun shouldHandleManyInsertions() = runTest {
        val startTime = System.currentTimeMillis()
        val tasks = (1..1000).map { index ->
            EntityTestFactory.createTestTaskEntity(
                id = "task-$index",
                title = "Task $index",
                tokenReward = index % 20,
                isCompleted = index % 3 == 0,
                assignedToUserId = "user-${index % 10}",
            )
        }
        tasks.forEach { taskDao.insert(it) }
        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        DatabaseAssertions.verifyTaskCount(taskDao, 1000)
        println("Inserted 1000 tasks in ${executionTime}ms")
        val queryStartTime = System.currentTimeMillis()
        val allTasks = taskDao.getAllTasks()
        val user1Tasks = taskDao.findByUserId("user-1")
        val completedTasks = taskDao.findCompletedByUserId("user-1")
        val queryEndTime = System.currentTimeMillis()
        val queryTime = queryEndTime - queryStartTime
        assertEquals(1000, allTasks.size)
        assertEquals(100, user1Tasks.size)
        assertTrue(completedTasks.isNotEmpty())
        println("Executed complex queries on 1000 tasks in ${queryTime}ms")
    }

    @Test
    fun shouldHandleComplexAggregations() = runTest {
        val tasks = (1..500).map { index ->
            EntityTestFactory.createTestTaskEntity(
                id = "task-$index",
                tokenReward = index % 50,
                isCompleted = index % 2 == 0,
                assignedToUserId = "user-${index % 5}",
            )
        }
        tasks.forEach { taskDao.insert(it) }
        val startTime = System.currentTimeMillis()
        val user0CompletedCount = taskDao.countCompletedTasks("user-0")
        val user1TokensEarned = taskDao.countTokensEarned("user-1")
        val user2CompletedTasks = taskDao.findCompletedByUserId("user-2")
        val user3IncompleteTasks = taskDao.findIncompleteByUserId("user-3")
        val endTime = System.currentTimeMillis()
        assertTrue(user0CompletedCount > 0)
        assertTrue((user1TokensEarned ?: 0) > 0)
        assertTrue(user2CompletedTasks.isNotEmpty())
        assertTrue(user3IncompleteTasks.isNotEmpty())
        println("Executed aggregation queries in ${endTime - startTime}ms")
    }
}
