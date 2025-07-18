package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeAssignedTo
import com.lemonqwest.app.domain.shouldBeCompleted
import com.lemonqwest.app.domain.shouldHaveCompletedCount
import com.lemonqwest.app.domain.shouldNotBeCompleted
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Focused test suite for Task collection operations and filtering.
 *
 * Tests cover:
 * - Task list operations
 * - Category distribution validation
 * - Token reward calculations
 * - Completion status filtering
 * - Category-based filtering
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Collections Tests")
class TaskCollectionsTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should handle task list operations")
    fun shouldHandleTaskListOperations() {
        val userId = UUID.randomUUID().toString()
        val tasks = TestDataFactory.createTaskList(
            assignedToUserId = userId,
            includeCompleted = true,
        )

        assertEquals(9, tasks.size, "Should create 9 tasks (3 per category)")
        tasks.shouldHaveCompletedCount(3)

        // Verify all tasks are assigned to the same user
        tasks.forEach { task ->
            task.shouldBeAssignedTo(userId)
        }
    }

    @Test
    @DisplayName("Should create tasks for all categories")
    fun shouldCreateTasksForAllCategories() {
        val tasks = TestDataFactory.createTaskList()

        val categoryDistribution = tasks.groupingBy { it.category }.eachCount()

        assertEquals(
            3,
            categoryDistribution[TaskCategory.PERSONAL_CARE],
            "Should have 3 personal care tasks",
        )
        assertEquals(
            3,
            categoryDistribution[TaskCategory.HOUSEHOLD],
            "Should have 3 household tasks",
        )
        assertEquals(
            3,
            categoryDistribution[TaskCategory.HOMEWORK],
            "Should have 3 homework tasks",
        )
    }

    @Test
    @DisplayName("Should calculate total token rewards for task list")
    fun shouldCalculateTotalTokenRewardsForTaskList() {
        val tasks = TestDataFactory.createTaskList(includeCompleted = false)
        val totalRewards = tasks.sumOf { it.tokenReward }

        // 3 personal care (5 each) + 3 household (10 each) + 3 homework (15 each) = 15 + 30 + 45 = 90
        assertEquals(90, totalRewards, "Total rewards should be 90 tokens")
    }

    @Test
    @DisplayName("Should filter tasks by completion status")
    fun shouldFilterTasksByCompletionStatus() {
        val tasks = TestDataFactory.createTaskList(includeCompleted = true)

        val completedTasks = tasks.filter { it.isCompleted }
        val incompleteTasks = tasks.filter { !it.isCompleted }

        assertEquals(3, completedTasks.size, "Should have 3 completed tasks")
        assertEquals(6, incompleteTasks.size, "Should have 6 incomplete tasks")

        completedTasks.forEach { it.shouldBeCompleted() }
        incompleteTasks.forEach { it.shouldNotBeCompleted() }
    }

    @Test
    @DisplayName("Should filter tasks by category")
    fun shouldFilterTasksByCategory() {
        val tasks = TestDataFactory.createTaskList()

        val personalCareTasks = tasks.filter { it.category == TaskCategory.PERSONAL_CARE }
        val householdTasks = tasks.filter { it.category == TaskCategory.HOUSEHOLD }
        val homeworkTasks = tasks.filter { it.category == TaskCategory.HOMEWORK }

        assertEquals(3, personalCareTasks.size, "Should have 3 personal care tasks")
        assertEquals(3, householdTasks.size, "Should have 3 household tasks")
        assertEquals(3, homeworkTasks.size, "Should have 3 homework tasks")

        personalCareTasks.forEach {
            assertEquals(
                5,
                it.tokenReward,
                "Personal care tasks should have 5 tokens",
            )
        }
        householdTasks.forEach {
            assertEquals(
                10,
                it.tokenReward,
                "Household tasks should have 10 tokens",
            )
        }
        homeworkTasks.forEach {
            assertEquals(
                15,
                it.tokenReward,
                "Homework tasks should have 15 tokens",
            )
        }
    }
}
