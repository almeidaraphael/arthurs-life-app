package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Test suite for Task business rules validation.
 *
 * Tests cover:
 * - Category-reward relationship enforcement
 * - Task immutability rules
 * - Task completion business logic
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Business Rules Tests")
class TaskBusinessRulesTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should enforce category-reward relationship")
    fun shouldEnforceCategoryRewardRelationship() {
        TaskCategory.values().forEach { category ->
            val task = Task.create(
                title = "Test Task",
                category = category,
                assignedToUserId = UUID.randomUUID().toString(),
            )

            assertEquals(
                category.defaultTokenReward,
                task.tokenReward,
                "Task should have default reward for category ${category.displayName}",
            )
        }
    }

    @Test
    @DisplayName("Should maintain task immutability")
    fun shouldMaintainTaskImmutability() {
        val originalTask = TestDataFactory.createTask()
        val originalTitle = originalTask.title
        val originalCompleted = originalTask.isCompleted

        // Modify task through copy
        val modifiedTask = originalTask.copy(title = "Modified Title", isCompleted = true)

        // Original should remain unchanged
        assertEquals(
            originalTitle,
            originalTask.title,
            "Original task title should remain unchanged",
        )
        assertEquals(
            originalCompleted,
            originalTask.isCompleted,
            "Original task completion should remain unchanged",
        )

        // Modified should have changes
        assertEquals(
            "Modified Title",
            modifiedTask.title,
            "Modified task should have new title",
        )
        assertTrue(modifiedTask.isCompleted, "Modified task should be completed")
    }

    @Test
    @DisplayName("Should validate task completion business logic")
    fun shouldValidateTaskCompletionBusinessLogic() {
        val task = TestDataFactory.createTask()

        // Business rule: can only complete incomplete tasks
        assertTrue(task.canBeCompleted(), "Incomplete task should be completable")

        val completedTask = task.markCompleted()

        // Business rule: cannot complete already completed tasks
        assertFalse(completedTask.canBeCompleted(), "Completed task should not be completable")
    }
}
