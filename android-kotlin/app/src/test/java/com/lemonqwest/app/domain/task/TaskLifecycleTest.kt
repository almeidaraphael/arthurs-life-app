package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeCompletable
import com.lemonqwest.app.domain.shouldBeCompleted
import com.lemonqwest.app.domain.shouldNotBeCompletable
import com.lemonqwest.app.domain.shouldNotBeCompleted
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for Task lifecycle and state transitions with complete test isolation.
 *
 * Tests cover:
 * - Task completion marking with test isolation
 * - Task incomplete marking with parallel safety
 * - Multiple completion state changes with modern patterns
 * - Task completion logic validation with thread safety
 * - Property preservation during state transitions with complete isolation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Lifecycle Tests")
class TaskLifecycleTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should mark task as completed")
    fun shouldMarkTaskAsCompleted() = runTest {
        val task = TestDataFactory.createTask()

        task.shouldNotBeCompleted()
        task.shouldBeCompletable()

        val completedTask = task.markCompleted()

        completedTask.shouldBeCompleted()
        completedTask.shouldNotBeCompletable()

        // Original task should remain unchanged (immutability)
        task.shouldNotBeCompleted()
    }

    @Test
    @DisplayName("Should mark task as incomplete")
    fun shouldMarkTaskAsIncomplete() = runTest {
        val completedTask = TestDataFactory.createCompletedTask()

        completedTask.shouldBeCompleted()
        completedTask.shouldNotBeCompletable()

        val incompleteTask = completedTask.markIncomplete()

        incompleteTask.shouldNotBeCompleted()
        incompleteTask.shouldBeCompletable()

        // Original task should remain unchanged (immutability)
        completedTask.shouldBeCompleted()
    }

    @Test
    @DisplayName("Should handle multiple completion state changes")
    fun shouldHandleMultipleCompletionStateChanges() = runTest {
        val originalTask = TestDataFactory.createTask()

        val completed = originalTask.markCompleted()
        val incomplete = completed.markIncomplete()
        val reCompleted = incomplete.markCompleted()

        // Verify final state
        reCompleted.shouldBeCompleted()
        reCompleted.shouldNotBeCompletable()

        // Verify intermediate states remain unchanged
        originalTask.shouldNotBeCompleted()
        completed.shouldBeCompleted()
        incomplete.shouldNotBeCompleted()
    }

    @Test
    @DisplayName("Should validate task completion logic")
    fun shouldValidateTaskCompletionLogic() = runTest {
        val task = TestDataFactory.createTask()

        // Initially not completed and can be completed
        assertFalse(task.isCompleted, "New task should not be completed")
        assertTrue(task.canBeCompleted(), "New task should be completable")

        val completedTask = task.markCompleted()

        // After completion, is completed and cannot be completed again
        assertTrue(completedTask.isCompleted, "Completed task should be completed")
        assertFalse(completedTask.canBeCompleted(), "Completed task should not be completable")
    }

    @Test
    @DisplayName("Should preserve task properties during state transitions")
    fun shouldPreserveTaskPropertiesDuringStateTransitions() = runTest {
        val originalTask = TestDataFactory.createTask(
            title = "Original Title",
            category = TaskCategory.HOMEWORK,
        )

        val completedTask = originalTask.markCompleted()
        val incompleteTask = completedTask.markIncomplete()

        // All versions should have same core properties
        assertEquals(originalTask.id, completedTask.id, "ID should remain unchanged")
        assertEquals(originalTask.title, completedTask.title, "Title should remain unchanged")
        assertEquals(
            originalTask.category,
            completedTask.category,
            "Category should remain unchanged",
        )
        assertEquals(
            originalTask.tokenReward,
            completedTask.tokenReward,
            "Reward should remain unchanged",
        )
        assertEquals(
            originalTask.assignedToUserId,
            completedTask.assignedToUserId,
            "Assignment should remain unchanged",
        )

        assertEquals(
            originalTask.id,
            incompleteTask.id,
            "ID should remain unchanged after undo",
        )
        assertEquals(
            originalTask.title,
            incompleteTask.title,
            "Title should remain unchanged after undo",
        )
        assertEquals(
            originalTask.category,
            incompleteTask.category,
            "Category should remain unchanged after undo",
        )
        assertEquals(
            originalTask.tokenReward,
            incompleteTask.tokenReward,
            "Reward should remain unchanged after undo",
        )
        assertEquals(
            originalTask.assignedToUserId,
            incompleteTask.assignedToUserId,
            "Assignment should remain unchanged after undo",
        )
    }
}
