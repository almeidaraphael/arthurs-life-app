package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeAssignedTo
import com.lemonqwest.app.domain.shouldBeCompletable
import com.lemonqwest.app.domain.shouldBeCompleted
import com.lemonqwest.app.domain.shouldHaveDefaultReward
import com.lemonqwest.app.domain.shouldHaveProperties
import com.lemonqwest.app.domain.shouldNotBeCompleted
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Focused test suite for Task creation functionality.
 *
 * Tests cover:
 * - Task creation with default values
 * - Task creation with custom parameters
 * - Task factory method usage
 * - Unique ID generation
 * - Custom ID acceptance
 * - Creation timestamp setting
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Creation Tests")
class TaskCreationTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should create task with default values")
    fun shouldCreateTaskWithDefaults() {
        val task = TestDataFactory.createTask()

        assertNotNull(task.id, "Task should have an ID")
        assertTrue(task.id.isNotBlank(), "Task ID should not be blank")
        assertEquals("Brush teeth", task.title, "Default task title should be 'Brush teeth'")
        assertEquals(
            TaskCategory.PERSONAL_CARE,
            task.category,
            "Default category should be PERSONAL_CARE",
        )
        task.shouldNotBeCompleted()
        task.shouldBeCompletable()
        task.shouldHaveDefaultReward()
    }

    @Test
    @DisplayName("Should create task with custom parameters")
    fun shouldCreateTaskWithCustomParameters() {
        val customTitle = "Complete math homework"
        val customCategory = TaskCategory.HOMEWORK
        val customUserId = UUID.randomUUID().toString()

        val task = TestDataFactory.createTask(
            title = customTitle,
            category = customCategory,
            assignedToUserId = customUserId,
            isCompleted = true,
        )

        task.shouldHaveProperties(
            customTitle,
            customCategory,
            customCategory.defaultTokenReward,
        )
        task.shouldBeAssignedTo(customUserId)
        task.shouldBeCompleted()
    }

    @Test
    @DisplayName("Should create task using factory method")
    fun shouldCreateTaskUsingFactory() {
        val userId = UUID.randomUUID().toString()
        val task = Task.create(
            title = "Make bed",
            category = TaskCategory.HOUSEHOLD,
            assignedToUserId = userId,
        )

        task.shouldHaveProperties("Make bed", TaskCategory.HOUSEHOLD, 10)
        task.shouldBeAssignedTo(userId)
        task.shouldNotBeCompleted()
        task.shouldHaveDefaultReward()
    }

    @Test
    @DisplayName("Should generate unique IDs for different tasks")
    fun shouldGenerateUniqueIds() {
        val task1 = TestDataFactory.createTask()
        val task2 = TestDataFactory.createTask()

        assertNotEquals(task1.id, task2.id, "Tasks should have unique IDs")
    }

    @Test
    @DisplayName("Should accept custom ID")
    fun shouldAcceptCustomId() {
        val customId = "custom-task-123"
        val task = TestDataFactory.createTask(id = customId)

        assertEquals(customId, task.id, "Task should use custom ID")
    }

    @Test
    @DisplayName("Should set creation timestamp")
    fun shouldSetCreationTimestamp() {
        val beforeCreation = System.currentTimeMillis()
        val task = TestDataFactory.createTask()
        val afterCreation = System.currentTimeMillis()

        assertTrue(
            task.createdAt >= beforeCreation,
            "Creation timestamp should be after test start",
        )
        assertTrue(
            task.createdAt <= afterCreation,
            "Creation timestamp should be before test end",
        )
    }
}
