package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeAssignedTo
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Focused test suite for Task assignment functionality with complete test isolation.
 *
 * Tests cover:
 * - Task assignment to users with test isolation
 * - Task reassignment handling with parallel safety
 * - Assignment preservation during completion with modern patterns
 * - Assignment validation in factory methods with thread safety
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Assignment Tests")
class TaskAssignmentTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should assign task to user")
    fun shouldAssignTaskToUser() = runTest {
        val userId = UUID.randomUUID().toString()
        val task = TestDataFactory.createTask(assignedToUserId = userId)

        task.shouldBeAssignedTo(userId)
    }

    @Test
    @DisplayName("Should handle task reassignment")
    fun shouldHandleTaskReassignment() = runTest {
        val originalUserId = UUID.randomUUID().toString()
        val newUserId = UUID.randomUUID().toString()

        val originalTask = TestDataFactory.createTask(assignedToUserId = originalUserId)
        val reassignedTask = originalTask.copy(assignedToUserId = newUserId)

        originalTask.shouldBeAssignedTo(originalUserId)
        reassignedTask.shouldBeAssignedTo(newUserId)
    }

    @Test
    @DisplayName("Should preserve assignment during completion")
    fun shouldPreserveAssignmentDuringCompletion() = runTest {
        val userId = UUID.randomUUID().toString()
        val task = TestDataFactory.createTask(assignedToUserId = userId)
        val completedTask = task.markCompleted()

        task.shouldBeAssignedTo(userId)
        completedTask.shouldBeAssignedTo(userId)
    }

    @Test
    @DisplayName("Should validate assignment in task creation factory")
    fun shouldValidateAssignmentInTaskCreationFactory() = runTest {
        val userId = UUID.randomUUID().toString()
        val task = Task.create(
            title = "Test Task",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = userId,
        )

        task.shouldBeAssignedTo(userId)
    }
}
