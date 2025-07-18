package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeAssignedTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

/**
 * Focused test suite for Task assignment functionality.
 *
 * Tests cover:
 * - Task assignment to users
 * - Task reassignment handling
 * - Assignment preservation during completion
 * - Assignment validation in factory methods
 */
@DisplayName("Task Assignment Tests")
class TaskAssignmentTest {

    @Test
    @DisplayName("Should assign task to user")
    fun shouldAssignTaskToUser() {
        val userId = UUID.randomUUID().toString()
        val task = TestDataFactory.createTask(assignedToUserId = userId)

        task.shouldBeAssignedTo(userId)
    }

    @Test
    @DisplayName("Should handle task reassignment")
    fun shouldHandleTaskReassignment() {
        val originalUserId = UUID.randomUUID().toString()
        val newUserId = UUID.randomUUID().toString()

        val originalTask = TestDataFactory.createTask(assignedToUserId = originalUserId)
        val reassignedTask = originalTask.copy(assignedToUserId = newUserId)

        originalTask.shouldBeAssignedTo(originalUserId)
        reassignedTask.shouldBeAssignedTo(newUserId)
    }

    @Test
    @DisplayName("Should preserve assignment during completion")
    fun shouldPreserveAssignmentDuringCompletion() {
        val userId = UUID.randomUUID().toString()
        val task = TestDataFactory.createTask(assignedToUserId = userId)
        val completedTask = task.markCompleted()

        task.shouldBeAssignedTo(userId)
        completedTask.shouldBeAssignedTo(userId)
    }

    @Test
    @DisplayName("Should validate assignment in task creation factory")
    fun shouldValidateAssignmentInTaskCreationFactory() {
        val userId = UUID.randomUUID().toString()
        val task = Task.create(
            title = "Test Task",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = userId,
        )

        task.shouldBeAssignedTo(userId)
    }
}
