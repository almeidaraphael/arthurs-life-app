package com.arthurslife.app.domain.task

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.shouldBeAssignedTo
import com.arthurslife.app.domain.shouldBeCompletable
import com.arthurslife.app.domain.shouldBeCompleted
import com.arthurslife.app.domain.shouldHaveCompletedCount
import com.arthurslife.app.domain.shouldHaveDefaultReward
import com.arthurslife.app.domain.shouldHaveProperties
import com.arthurslife.app.domain.shouldNotBeCompletable
import com.arthurslife.app.domain.shouldNotBeCompleted
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

/**
 * Comprehensive test suite for the Task domain entity.
 *
 * Tests cover:
 * - Task creation and initialization
 * - Task lifecycle (creation, completion, undo)
 * - Category-based token rewards
 * - Task assignment to users
 * - Task state transitions
 * - Data validation and business rules
 * - Edge cases and error conditions
 */
@DisplayName("Task Domain Entity Tests")
class TaskTest {

    @Nested
    @DisplayName("Task Creation")
    inner class TaskCreation {

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

    @Nested
    @DisplayName("Task Categories and Rewards")
    inner class TaskCategoriesAndRewards {

        @ParameterizedTest
        @EnumSource(TaskCategory::class)
        @DisplayName("Should assign correct default rewards for each category")
        fun shouldAssignCorrectDefaultRewards(category: TaskCategory) {
            val expectedReward = when (category) {
                TaskCategory.PERSONAL_CARE -> 5
                TaskCategory.HOUSEHOLD -> 10
                TaskCategory.HOMEWORK -> 15
            }

            val task = TestDataFactory.createTask(category = category)

            assertEquals(category, task.category, "Task should have correct category")
            assertEquals(
                expectedReward,
                task.tokenReward,
                "Task should have correct default reward",
            )
            task.shouldHaveDefaultReward()
        }

        @Test
        @DisplayName("Should validate personal care task properties")
        fun shouldValidatePersonalCareTaskProperties() {
            val task = TestDataFactory.createTask(
                title = "Brush teeth",
                category = TaskCategory.PERSONAL_CARE,
            )

            task.shouldHaveProperties("Brush teeth", TaskCategory.PERSONAL_CARE, 5)
            assertEquals("Personal Care", task.category.displayName)
            assertEquals("Daily hygiene and self-care tasks", task.category.description)
        }

        @Test
        @DisplayName("Should validate household task properties")
        fun shouldValidateHouseholdTaskProperties() {
            val task = TestDataFactory.createTask(
                title = "Clean room",
                category = TaskCategory.HOUSEHOLD,
            )

            task.shouldHaveProperties("Clean room", TaskCategory.HOUSEHOLD, 10)
            assertEquals("Household", task.category.displayName)
            assertEquals("Chores and family responsibilities", task.category.description)
        }

        @Test
        @DisplayName("Should validate homework task properties")
        fun shouldValidateHomeworkTaskProperties() {
            val task = TestDataFactory.createTask(
                title = "Math homework",
                category = TaskCategory.HOMEWORK,
            )

            task.shouldHaveProperties("Math homework", TaskCategory.HOMEWORK, 15)
            assertEquals("Homework", task.category.displayName)
            assertEquals("Educational and learning activities", task.category.description)
        }

        @Test
        @DisplayName("Should handle custom token rewards")
        fun shouldHandleCustomTokenRewards() {
            val customReward = 25
            val task = Task(
                title = "Special task",
                category = TaskCategory.PERSONAL_CARE,
                tokenReward = customReward,
                assignedToUserId = UUID.randomUUID().toString(),
            )

            assertEquals(customReward, task.tokenReward, "Task should use custom reward")
            assertNotEquals(
                task.category.defaultTokenReward,
                task.tokenReward,
                "Custom reward should differ from default",
            )
        }
    }

    @Nested
    @DisplayName("Task Lifecycle")
    inner class TaskLifecycle {

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

    @Nested
    @DisplayName("Task Assignment")
    inner class TaskAssignment {

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

    @Nested
    @DisplayName("Task Collections")
    inner class TaskCollections {

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

    @Nested
    @DisplayName("Data Validation")
    inner class DataValidation {

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

    @Nested
    @DisplayName("Business Rules")
    inner class BusinessRules {

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

    @Nested
    @DisplayName("Edge Cases")
    inner class EdgeCases {

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

    @Nested
    @DisplayName("Property-Based Testing")
    inner class PropertyBasedTesting {

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 5, 10, 15, 25, 50, 100])
        @DisplayName("Should handle various token reward values")
        fun shouldHandleVariousTokenRewardValues(rewardAmount: Int) {
            val task = Task(
                title = "Variable Reward Task",
                category = TaskCategory.PERSONAL_CARE,
                tokenReward = rewardAmount,
                assignedToUserId = UUID.randomUUID().toString(),
            )

            assertEquals(rewardAmount, task.tokenReward, "Task should have specified reward amount")
            assertTrue(task.tokenReward >= 0, "Token reward should be non-negative")
        }

        @ParameterizedTest
        @ValueSource(
            strings = ["", "A", "Simple Task", "Complex Task with Special Characters!", "Task with 数字 and symbols @#$%"],
        )
        @DisplayName("Should handle various task title formats")
        fun shouldHandleVariousTaskTitleFormats(title: String) {
            val task = TestDataFactory.createTask(title = title)

            assertEquals(title, task.title, "Task should preserve title exactly")
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        @DisplayName("Should handle both completion states")
        fun shouldHandleBothCompletionStates(isCompleted: Boolean) {
            val task = TestDataFactory.createTask(isCompleted = isCompleted)

            assertEquals(
                isCompleted,
                task.isCompleted,
                "Task should have specified completion state",
            )
            assertEquals(
                !isCompleted,
                task.canBeCompleted(),
                "Task completability should be inverse of completion state",
            )
        }
    }

    @Nested
    @DisplayName("Random Data Testing")
    inner class RandomDataTesting {

        @Test
        @DisplayName("Should generate diverse random tasks")
        fun shouldGenerateDiverseRandomTasks() {
            val randomTasks = (1..50).map { TestDataFactory.createRandomTask() }

            // Verify diversity
            val categories = randomTasks.map { it.category }.toSet()
            val completionStates = randomTasks.map { it.isCompleted }.toSet()

            assertTrue(categories.size > 1, "Should generate tasks with different categories")
            assertTrue(
                completionStates.size > 1,
                "Should generate tasks with different completion states",
            )

            // Verify all tasks have valid properties
            randomTasks.forEach { task ->
                assertNotNull(task.id, "Random task should have ID")
                assertTrue(task.title.isNotBlank(), "Random task should have non-blank title")
                assertTrue(task.tokenReward >= 0, "Random task should have non-negative reward")
                assertNotNull(
                    UUID.fromString(task.assignedToUserId),
                    "Random task should have valid user ID",
                )
            }
        }
    }
}
