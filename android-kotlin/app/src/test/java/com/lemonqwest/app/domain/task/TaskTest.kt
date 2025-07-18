package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Comprehensive test suite for the Task domain entity.
 *
 * Tests cover:
 * - Task basic entity behavior
 * - Task domain logic
 * - Task state management
 * - Core task functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Domain Entity Tests")
class TaskTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var task: Task
    private lateinit var userId: String

    @BeforeEach
    fun setUp() {
        userId = UUID.randomUUID().toString()
        task = TestDataFactory.createTask(assignedToUserId = userId)
    }

    @Test
    @DisplayName("Should have valid task entity")
    fun shouldHaveValidTaskEntity() = runTest {
        assertNotNull(task.id, "Task should have an ID")
        assertTrue(task.id.isNotBlank(), "Task ID should not be blank")
        assertNotNull(task.title, "Task should have a title")
        assertNotNull(task.category, "Task should have a category")
        assertNotNull(task.assignedToUserId, "Task should have an assigned user ID")
        assertEquals(userId, task.assignedToUserId, "Task should be assigned to correct user")
    }

    @Test
    @DisplayName("Should have valid task state")
    fun shouldHaveValidTaskState() = runTest {
        assertFalse(task.isCompleted, "New task should not be completed")
        assertTrue(task.tokenReward > 0, "Task should have positive token reward")
        assertTrue(task.createdAt > 0, "Task should have creation timestamp")
        assertNull(task.completedAt, "New task should not have completion timestamp")
    }

    @Test
    @DisplayName("Should handle task completion correctly")
    fun shouldHandleTaskCompletionCorrectly() = runTest {
        assertTrue(task.canBeCompleted(), "New task should be able to be completed")

        val completedTask = task.markCompleted()

        assertTrue(completedTask.isCompleted, "Completed task should be marked as completed")
        assertNotNull(completedTask.completedAt, "Completed task should have completion timestamp")
        assertTrue(completedTask.completedAt!! > 0, "Completion timestamp should be positive")
        assertFalse(
            completedTask.canBeCompleted(),
            "Completed task should not be able to be completed again",
        )
    }

    @Test
    @DisplayName("Should handle task incompletion correctly")
    fun shouldHandleTaskIncompletionCorrectly() = runTest {
        val completedTask = task.markCompleted()
        val incompletedTask = completedTask.markIncomplete()

        assertFalse(
            incompletedTask.isCompleted,
            "Incompleted task should not be marked as completed",
        )
        assertNull(
            incompletedTask.completedAt,
            "Incompleted task should not have completion timestamp",
        )
        assertTrue(
            incompletedTask.canBeCompleted(),
            "Incompleted task should be able to be completed",
        )
    }

    @Test
    @DisplayName("Should create task with factory method")
    fun shouldCreateTaskWithFactoryMethod() = runTest {
        val title = "Test Task"
        val category = TaskCategory.HOUSEHOLD
        val newTask = Task.create(
            title = title,
            category = category,
            assignedToUserId = userId,
        )

        assertEquals(title, newTask.title, "Task should have correct title")
        assertEquals(category, newTask.category, "Task should have correct category")
        assertEquals(
            category.defaultTokenReward,
            newTask.tokenReward,
            "Task should have category default token reward",
        )
        assertEquals(userId, newTask.assignedToUserId, "Task should be assigned to correct user")
        assertFalse(newTask.isCompleted, "New task should not be completed")
    }

    // Note: Detailed test coverage is provided by specialized test files:
    // - TaskCreationTest.kt: Task creation and initialization
    // - TaskLifecycleTest.kt: Task lifecycle and state transitions
    // - TaskCategoriesAndRewardsTest.kt: Category-based rewards
    // - TaskAssignmentTest.kt: Task assignment functionality
    // - TaskBusinessRulesTest.kt: Business rule validation
    // - TaskDataValidationTest.kt: Data validation rules
    // - TaskEdgeCasesTest.kt: Edge cases and error conditions
    // - TaskCollectionsTest.kt: Task collection operations
}
