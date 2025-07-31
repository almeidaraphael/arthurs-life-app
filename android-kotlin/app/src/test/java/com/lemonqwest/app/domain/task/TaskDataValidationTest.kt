package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Test suite for Task data validation rules.
 *
 * Tests cover:
 * - Task title validation
 * - UUID format validation for IDs
 * - Token reward validation
 * - Special character handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Data Validation Tests")
class TaskDataValidationTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should validate task title is not blank")
    fun shouldValidateTaskTitleNotBlank() {
        val task = TestDataFactory.createTask(title = "Valid Title")
        assertTrue(task.title.isNotBlank(), "Task title should not be blank")
    }

    @Test
    @DisplayName("Should handle empty task title gracefully")
    fun shouldHandleEmptyTaskTitle() {
        val task = TestDataFactory.createTask(title = "")
        assertEquals("", task.title, "Should accept empty title (handled by validation layer)")
    }

    @Test
    @DisplayName("Should validate UUID format for task ID")
    fun shouldValidateUuidFormatForTaskId() {
        val task = TestDataFactory.createTask()
        assertNotNull(UUID.fromString(task.id), "Auto-generated task ID should be valid UUID")
    }

    @Test
    @DisplayName("Should validate UUID format for assigned user ID")
    fun shouldValidateUuidFormatForAssignedUserId() {
        val userId = UUID.randomUUID().toString()
        val task = TestDataFactory.createTask(assignedToUserId = userId)

        assertNotNull(
            UUID.fromString(task.assignedToUserId),
            "Assigned user ID should be valid UUID",
        )
    }

    @Test
    @DisplayName("Should validate token reward is non-negative")
    fun shouldValidateTokenRewardIsNonNegative() {
        val task = TestDataFactory.createTask()
        assertTrue(task.tokenReward >= 0, "Token reward should be non-negative")
    }

    @Test
    @DisplayName("Should handle special characters in task title")
    fun shouldHandleSpecialCharactersInTaskTitle() {
        val specialTitles = listOf(
            "Math homework (fractions)",
            "Clean room & organize toys",
            "Brush teeth - morning routine",
            "Help with cooking 🍳",
            "Read book: Harry Potter",
        )

        specialTitles.forEach { title ->
            val task = TestDataFactory.createTask(title = title)
            assertEquals(title, task.title, "Should handle special characters in title: $title")
        }
    }
}
