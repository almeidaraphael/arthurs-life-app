package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeAssignedTo
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Test suite for Task edge cases and boundary conditions.
 *
 * Tests cover:
 * - Very long task titles
 * - Zero and large token rewards
 * - Rapid task creation scenarios
 * - Tasks with creation time in past
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Edge Cases Tests")
class TaskEdgeCasesTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should handle very long task titles")
    fun shouldHandleVeryLongTaskTitles() {
        val longTitle = "A".repeat(1000)
        val task = TestDataFactory.createTask(title = longTitle)

        assertEquals(longTitle, task.title, "Should handle very long task titles")
    }

    @Test
    @DisplayName("Should handle zero token reward")
    fun shouldHandleZeroTokenReward() {
        val task = Task(
            title = "Free Task",
            category = TaskCategory.PERSONAL_CARE,
            tokenReward = 0,
            assignedToUserId = UUID.randomUUID().toString(),
        )

        assertEquals(0, task.tokenReward, "Should handle zero token reward")
    }

    @Test
    @DisplayName("Should handle large token rewards")
    fun shouldHandleLargeTokenRewards() {
        val largeReward = 1000000
        val task = Task(
            title = "Million Token Task",
            category = TaskCategory.HOMEWORK,
            tokenReward = largeReward,
            assignedToUserId = UUID.randomUUID().toString(),
        )

        assertEquals(largeReward, task.tokenReward, "Should handle large token rewards")
    }

    @Test
    @DisplayName("Should handle rapid task creation")
    fun shouldHandleRapidTaskCreation() {
        val userId = UUID.randomUUID().toString()
        val tasks = (1..1000).map { index ->
            TestDataFactory.createTask(
                title = "Task $index",
                assignedToUserId = userId,
            )
        }

        val uniqueIds = tasks.map { it.id }.toSet()
        assertEquals(1000, uniqueIds.size, "All tasks should have unique IDs")

        // Verify all tasks are properly assigned
        tasks.forEach { task ->
            task.shouldBeAssignedTo(userId)
        }
    }

    @Test
    @DisplayName("Should handle tasks with creation time in past")
    fun shouldHandleTasksWithCreationTimeInPast() {
        val pastTime = System.currentTimeMillis() - 86400000 // 24 hours ago
        val task = Task(
            title = "Past Task",
            category = TaskCategory.PERSONAL_CARE,
            tokenReward = 5,
            assignedToUserId = UUID.randomUUID().toString(),
            createdAt = pastTime,
        )

        assertEquals(pastTime, task.createdAt, "Should handle tasks with past creation time")
    }
}
