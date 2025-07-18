package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeCompletable
import com.lemonqwest.app.domain.shouldBeCompleted
import com.lemonqwest.app.domain.shouldNotBeCompletable
import com.lemonqwest.app.domain.shouldNotBeCompleted
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for Task lifecycle and state transitions.
 *
 * Tests cover:
 * - Task completion marking
 * - Task incomplete marking
 * - Multiple completion state changes
 * - Task completion logic validation
 * - Property preservation during state transitions
 */
@DisplayName("Task Lifecycle Tests")
class TaskLifecycleTest {

    @Test
    @DisplayName("Should mark task as completed")
    fun shouldMarkTaskAsCompleted() {
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
    fun shouldMarkTaskAsIncomplete() {
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
    fun shouldHandleMultipleCompletionStateChanges() {
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
    fun shouldValidateTaskCompletionLogic() {
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
    fun shouldPreserveTaskPropertiesDuringStateTransitions() {
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
