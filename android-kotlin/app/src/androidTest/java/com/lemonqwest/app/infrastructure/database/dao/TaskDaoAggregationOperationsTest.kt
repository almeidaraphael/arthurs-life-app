package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("TaskDaoAggregationOperationsTest")
class TaskDaoAggregationOperationsTest : DatabaseTestBase() {
    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
        val testTasks = listOf(
            EntityTestFactory.createTestTaskEntity(
                id = "task-1",
                tokenReward = 5,
                isCompleted = true,
                assignedToUserId = "user-1",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "task-2",
                tokenReward = 10,
                isCompleted = true,
                assignedToUserId = "user-1",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "task-3",
                tokenReward = 15,
                isCompleted = false,
                assignedToUserId = "user-1",
            ),
            EntityTestFactory.createTestTaskEntity(
                id = "task-4",
                tokenReward = 8,
                isCompleted = true,
                assignedToUserId = "user-2",
            ),
        )
        testTasks.forEach { taskDao.insert(it) }
    }

    @Test
    fun shouldCountCompletedTasksForUser() = runTest {
        val user1CompletedCount = taskDao.countCompletedTasks("user-1")
        val user2CompletedCount = taskDao.countCompletedTasks("user-2")
        assertEquals(2, user1CompletedCount)
        assertEquals(1, user2CompletedCount)
    }

    @Test
    fun shouldReturnZeroForUserWithNoCompletedTasks() = runTest {
        val completedCount = taskDao.countCompletedTasks("user-with-no-tasks")
        assertEquals(0, completedCount)
    }

    @Test
    fun shouldCountTokensEarnedForUser() = runTest {
        val user1TokensEarned = taskDao.countTokensEarned("user-1")
        val user2TokensEarned = taskDao.countTokensEarned("user-2")
        assertEquals(15, user1TokensEarned)
        assertEquals(8, user2TokensEarned)
    }

    @Test
    fun shouldReturnZeroTokensForUserWithNoCompletedTasks() = runTest {
        val tokensEarned = taskDao.countTokensEarned("user-with-no-tasks")
        assertEquals(0, tokensEarned ?: 0)
    }

    @Test
    fun shouldHandleNullResultForUserWithNoTasks() = runTest {
        val tokensEarned = taskDao.countTokensEarned("user-with-no-tasks")
        assertEquals(0, tokensEarned ?: 0)
    }
}
