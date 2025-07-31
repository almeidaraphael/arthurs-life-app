package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.infrastructure.database.entities.TaskEntity
import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive tests for TaskDao database operations.
 *
 * This test class covers all TaskDao operations including:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Query operations (by ID, user, category, completion status)
 * - Aggregation operations (counting tasks and tokens)
 * - Edge cases and error handling
 * - Performance and data integrity
 */
@DisplayName("TaskDaoTest")
class TaskDaoTest : DatabaseTestBase() {

    private lateinit var taskDao: TaskDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        taskDao = database.taskDao()
    }

    @Nested
    @DisplayName("Create Operations")
    inner class CreateOperations {

        @Test
        @DisplayName("Should insert task successfully")
        fun shouldInsertTask() = runTest {
            // Given
            val task = EntityTestFactory.createTestTaskEntity(
                id = "task-1",
                title = "Brush teeth",
                assignedToUserId = "user-1",
            ).copy(
                category = "PERSONAL_CARE",
                tokenReward = 5,
                customIconName = "default_icon",
                customColorHex = "#FFFFFF",
            )

            // When
            taskDao.insert(task)

            // Then
            val retrievedTask = taskDao.findById("task-1")
            assertNotNull(retrievedTask)
            assertEquals(task.id, retrievedTask.id)
            assertEquals(task.title, retrievedTask.title)
            assertEquals(task.category, retrievedTask.category)
            assertEquals(task.tokenReward, retrievedTask.tokenReward)
            assertEquals(task.assignedToUserId, retrievedTask.assignedToUserId)
            assertEquals(false, retrievedTask.isCompleted)
        }

        @Test
        @DisplayName("Should insert completed task")
        fun shouldInsertCompletedTask() = runTest {
            // Given
            val task = EntityTestFactory.createTestTaskEntity(
                id = "completed-task",
                title = "Make bed",
                assignedToUserId = "user-1",
            ).copy(
                category = TaskCategory.HOUSEHOLD.name,
                tokenReward = 10,
                isCompleted = true,
                completedAt = System.currentTimeMillis(),
                customIconName = "icon_bed",
                customColorHex = "#00FF00",
            )

            // When
            taskDao.insert(task)

            // Then
            val retrievedTask = taskDao.findById("completed-task")
            assertNotNull(retrievedTask)
            assertEquals(true, retrievedTask.isCompleted)
        }

        @Test
        @DisplayName("Should replace task on conflict")
        fun shouldReplaceTaskOnConflict() = runTest {
            // Given
            val originalTask = EntityTestFactory.createTestTaskEntity(
                id = "task-1",
                title = "Original Title",
                assignedToUserId = "user-1",
            ).copy(
                category = TaskCategory.PERSONAL_CARE.name,
                tokenReward = 5,
                customIconName = "icon_brush",
                customColorHex = "#FFD700",
            )
            val updatedTask = EntityTestFactory.createTestTaskEntity(
                id = "task-1",
                title = "Updated Title",
                assignedToUserId = "user-1",
            ).copy(
                category = TaskCategory.PERSONAL_CARE.name,
                tokenReward = 10,
                customIconName = "icon_brush",
                customColorHex = "#FFD700",
            )

            // When
            taskDao.insert(originalTask)
            taskDao.insert(updatedTask) // Should replace due to OnConflictStrategy.REPLACE

            // Then
            val retrievedTask = taskDao.findById("task-1")
            assertNotNull(retrievedTask)
            assertEquals("Updated Title", retrievedTask.title)
            assertEquals(10, retrievedTask.tokenReward)
        }

        @Test
        @DisplayName("Should handle multiple task insertions")
        fun shouldInsertMultipleTasks() = runTest {
            // Given
            val tasks = listOf(
                EntityTestFactory.createTestTaskEntity(
                    id = "task-1",
                    title = "Brush teeth",
                    assignedToUserId = "user-1",
                    customIconName = "default_icon",
                    customColorHex = "#FFFFFF",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "task-2",
                    title = "Make bed",
                    assignedToUserId = "user-1",
                    customIconName = "default_icon",
                    customColorHex = "#FFFFFF",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "task-3",
                    title = "Math homework",
                    assignedToUserId = "user-2",
                    customIconName = "default_icon",
                    customColorHex = "#FFFFFF",
                ),
            )

            // When
            tasks.forEach { taskDao.insert(it) }

            // Then
            DatabaseAssertions.verifyTaskCount(taskDao, 3)
            tasks.forEach { task ->
                DatabaseAssertions.verifyTaskExists(taskDao, task.id)
            }
        }
    }

    @Nested
    @DisplayName("Read Operations")
    inner class ReadOperations {

        @BeforeEach
        fun setupTestData() = runTest {
            val testTasks = listOf(
                EntityTestFactory.createTestTaskEntity(
                    id = "task-1",
                    title = "Brush teeth",
                    category = TaskCategory.PERSONAL_CARE,
                    tokenReward = 5,
                    isCompleted = false,
                    assignedToUserId = "user-1",
                    customIconName = "icon_brush",
                    customColorHex = "#FFD700",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "task-2",
                    title = "Make bed",
                    category = TaskCategory.HOUSEHOLD,
                    tokenReward = 10,
                    isCompleted = true,
                    assignedToUserId = "user-1",
                    customIconName = "icon_bed",
                    customColorHex = "#00FF00",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "task-3",
                    title = "Math homework",
                    category = TaskCategory.HOMEWORK,
                    tokenReward = 15,
                    isCompleted = false,
                    assignedToUserId = "user-2",
                    customIconName = "icon_math",
                    customColorHex = "#0000FF",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "task-4",
                    title = "Reading assignment",
                    category = TaskCategory.HOMEWORK,
                    tokenReward = 12,
                    isCompleted = true,
                    assignedToUserId = "user-2",
                    customIconName = "icon_book",
                    customColorHex = "#FF00FF",
                ),
            )
            testTasks.forEach { taskDao.insert(it) }
        }

        @Test
        @DisplayName("Should find task by ID")
        fun shouldFindTaskById() = runTest {
            // When
            val task = taskDao.findById("task-1")

            // Then
            assertNotNull(task)
            assertEquals("task-1", task.id)
            assertEquals("Brush teeth", task.title)
            assertEquals("PERSONAL_CARE", task.category)
            assertEquals(5, task.tokenReward)
            assertEquals("user-1", task.assignedToUserId)
            assertEquals(false, task.isCompleted)
        }

        @Test
        @DisplayName("Should return null for non-existent task ID")
        fun shouldReturnNullForNonExistentId() = runTest {
            // When
            val task = taskDao.findById("non-existent")

            // Then
            assertNull(task)
        }

        @Test
        @DisplayName("Should find tasks by user ID")
        fun shouldFindTasksByUserId() = runTest {
            // When
            val user1Tasks = taskDao.findByUserId("user-1")
            val user2Tasks = taskDao.findByUserId("user-2")

            // Then
            assertEquals(2, user1Tasks.size)
            assertEquals(2, user2Tasks.size)
            assertTrue(user1Tasks.all { it.assignedToUserId == "user-1" })
            assertTrue(user2Tasks.all { it.assignedToUserId == "user-2" })
        }

        @Test
        @DisplayName("Should return empty list for user with no tasks")
        fun shouldReturnEmptyListForUserWithNoTasks() = runTest {
            // When
            val tasks = taskDao.findByUserId("user-with-no-tasks")

            // Then
            assertTrue(tasks.isEmpty())
        }

        @Test
        @DisplayName("Should find tasks by category")
        fun shouldFindTasksByCategory() = runTest {
            // When
            val personalCareTasks = taskDao.findByCategory("PERSONAL_CARE")
            val householdTasks = taskDao.findByCategory("HOUSEHOLD")
            val homeworkTasks = taskDao.findByCategory("HOMEWORK")

            // Then
            assertEquals(1, personalCareTasks.size)
            assertEquals(1, householdTasks.size)
            assertEquals(2, homeworkTasks.size)
            assertTrue(personalCareTasks.all { it.category == "PERSONAL_CARE" })
            assertTrue(householdTasks.all { it.category == "HOUSEHOLD" })
            assertTrue(homeworkTasks.all { it.category == "HOMEWORK" })
        }

        @Test
        @DisplayName("Should find completed tasks by user ID")
        fun shouldFindCompletedTasksByUserId() = runTest {
            // When
            val user1CompletedTasks = taskDao.findCompletedByUserId("user-1")
            val user2CompletedTasks = taskDao.findCompletedByUserId("user-2")

            // Then
            assertEquals(1, user1CompletedTasks.size)
            assertEquals(1, user2CompletedTasks.size)
            assertTrue(user1CompletedTasks.all { it.isCompleted })
            assertTrue(user2CompletedTasks.all { it.isCompleted })
            assertEquals("Make bed", user1CompletedTasks[0].title)
            assertEquals("Reading assignment", user2CompletedTasks[0].title)
        }

        @Test
        @DisplayName("Should find incomplete tasks by user ID")
        fun shouldFindIncompleteTasksByUserId() = runTest {
            // When
            val user1IncompleteTasks = taskDao.findIncompleteByUserId("user-1")
            val user2IncompleteTasks = taskDao.findIncompleteByUserId("user-2")

            // Then
            assertEquals(1, user1IncompleteTasks.size)
            assertEquals(1, user2IncompleteTasks.size)
            assertTrue(user1IncompleteTasks.all { !it.isCompleted })
            assertTrue(user2IncompleteTasks.all { !it.isCompleted })
            assertEquals("Brush teeth", user1IncompleteTasks[0].title)
            assertEquals("Math homework", user2IncompleteTasks[0].title)
        }

        @Test
        @DisplayName("Should get all tasks")
        fun shouldGetAllTasks() = runTest {
            // When
            val allTasks = taskDao.getAllTasks()

            // Then
            assertEquals(4, allTasks.size)
            val taskIds = allTasks.map { it.id }
            assertTrue(taskIds.contains("task-1"))
            assertTrue(taskIds.contains("task-2"))
            assertTrue(taskIds.contains("task-3"))
            assertTrue(taskIds.contains("task-4"))
        }

        @Test
        @DisplayName("Should order tasks by creation date descending")
        fun shouldOrderTasksByCreationDateDesc() = runTest {
            // Given - Insert tasks with specific creation times
            val now = System.currentTimeMillis()
            val tasks = listOf(
                EntityTestFactory.createTestTaskEntity(
                    id = "old-task",
                    createdAt = now - 3000,
                    customIconName = "icon_old",
                    customColorHex = "#CCCCCC",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "newest-task",
                    createdAt = now,
                    customIconName = "icon_new",
                    customColorHex = "#00FFFF",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "middle-task",
                    createdAt = now - 1000,
                    customIconName = "icon_middle",
                    customColorHex = "#FFCC00",
                ),
            )

            // Clear existing data and insert new tasks
            taskDao.deleteAll()
            tasks.forEach { taskDao.insert(it) }

            // When
            val orderedTasks = taskDao.getAllTasks()

            // Then
            assertEquals(3, orderedTasks.size)
            assertEquals("newest-task", orderedTasks[0].id)
            assertEquals("middle-task", orderedTasks[1].id)
            assertEquals("old-task", orderedTasks[2].id)
        }
    }

    @Nested
    @DisplayName("Update Operations")
    inner class UpdateOperations {

        @Test
        @DisplayName("Should update task successfully")
        fun shouldUpdateTask() = runTest {
            // Given
            val originalTask = EntityTestFactory.createTaskEntity(
                id = "task-1",
                title = "Original Title",
                tokenReward = 5,
                isCompleted = false,
            )
            taskDao.insert(originalTask)

            val updatedTask = originalTask.copy(
                title = "Updated Title",
                tokenReward = 10,
                isCompleted = true,
            )

            // When
            taskDao.update(updatedTask)

            // Then
            val retrievedTask = taskDao.findById("task-1")
            assertNotNull(retrievedTask)
            assertEquals("Updated Title", retrievedTask.title)
            assertEquals(10, retrievedTask.tokenReward)
            assertEquals(true, retrievedTask.isCompleted)
        }

        @Test
        @DisplayName("Should update task completion status")
        fun shouldUpdateTaskCompletionStatus() = runTest {
            // Given
            val task = EntityTestFactory.createTaskEntity(
                id = "task-1",
                isCompleted = false,
            )
            taskDao.insert(task)

            // When - Mark task as completed
            val completedTask = task.copy(isCompleted = true)
            taskDao.update(completedTask)

            // Then
            DatabaseAssertions.verifyTaskCompletion(taskDao, "task-1", true)
        }

        @Test
        @DisplayName("Should update task category")
        fun shouldUpdateTaskCategory() = runTest {
            // Given
            val task = EntityTestFactory.createTaskEntity(
                id = "task-1",
                category = TaskCategory.PERSONAL_CARE,
            )
            taskDao.insert(task)

            // When
            val updatedTask = task.copy(category = TaskCategory.HOUSEHOLD.name)
            taskDao.update(updatedTask)

            // Then
            val retrievedTask = taskDao.findById("task-1")
            assertNotNull(retrievedTask)
            assertEquals("HOUSEHOLD", retrievedTask.category)
        }

        @Test
        @DisplayName("Should update task token reward")
        fun shouldUpdateTaskTokenReward() = runTest {
            // Given
            val task = EntityTestFactory.createTaskEntity(
                id = "task-1",
                tokenReward = 5,
            )
            taskDao.insert(task)

            // When
            val updatedTask = task.copy(tokenReward = 15)
            taskDao.update(updatedTask)

            // Then
            val retrievedTask = taskDao.findById("task-1")
            assertNotNull(retrievedTask)
            assertEquals(15, retrievedTask.tokenReward)
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    inner class DeleteOperations {

        @Test
        @DisplayName("Should delete task by ID")
        fun shouldDeleteTaskById() = runTest {
            // Given
            val task = EntityTestFactory.createTaskEntity(id = "task-1")
            taskDao.insert(task)
            DatabaseAssertions.verifyTaskExists(taskDao, "task-1")

            // When
            taskDao.deleteById("task-1")

            // Then
            DatabaseAssertions.verifyTaskNotExists(taskDao, "task-1")
        }

        @Test
        @DisplayName("Should handle deleting non-existent task")
        fun shouldHandleDeletingNonExistentTask() = runTest {
            // When - This should not throw an exception
            taskDao.deleteById("non-existent-task")

            // Then - Database should still be empty
            DatabaseAssertions.verifyTaskCount(taskDao, 0)
        }

        @Test
        @DisplayName("Should delete only specified task")
        fun shouldDeleteOnlySpecifiedTask() = runTest {
            // Given
            val tasks = listOf(
                EntityTestFactory.createTestTaskEntity(id = "task-1"),
                EntityTestFactory.createTestTaskEntity(id = "task-2"),
                EntityTestFactory.createTestTaskEntity(id = "task-3"),
            )
            tasks.forEach { taskDao.insert(it) }

            // When
            taskDao.deleteById("task-2")

            // Then
            DatabaseAssertions.verifyTaskExists(taskDao, "task-1")
            DatabaseAssertions.verifyTaskNotExists(taskDao, "task-2")
            DatabaseAssertions.verifyTaskExists(taskDao, "task-3")
            DatabaseAssertions.verifyTaskCount(taskDao, 2)
        }

        @Test
        @DisplayName("Should delete all tasks")
        fun shouldDeleteAllTasks() = runTest {
            // Given
            val tasks = listOf(
                EntityTestFactory.createTestTaskEntity(id = "task-1"),
                EntityTestFactory.createTestTaskEntity(id = "task-2"),
                EntityTestFactory.createTestTaskEntity(id = "task-3"),
            )
            tasks.forEach { taskDao.insert(it) }
            DatabaseAssertions.verifyTaskCount(taskDao, 3)

            // When
            taskDao.deleteAll()

            // Then
            DatabaseAssertions.verifyTaskCount(taskDao, 0)
        }
    }

    @Nested
    @DisplayName("Aggregation Operations")
    inner class AggregationOperations {

        @BeforeEach
        fun setupTestData() = runTest {
            val testTasks = listOf(
                EntityTestFactory.createTestTaskEntity(
                    id = "task-1",
                    tokenReward = 5,
                    isCompleted = true,
                    assignedToUserId = "user-1",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "task-2",
                    tokenReward = 10,
                    isCompleted = true,
                    assignedToUserId = "user-1",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "task-3",
                    tokenReward = 15,
                    isCompleted = false,
                    assignedToUserId = "user-1",
                ),
                EntityTestFactory.createTestTaskEntity(
                    id = "task-4",
                    tokenReward = 8,
                    isCompleted = true,
                    assignedToUserId = "user-2",
                ),
            )
            testTasks.forEach { taskDao.insert(it) }
        }

        @Test
        @DisplayName("Should count completed tasks for user")
        fun shouldCountCompletedTasksForUser() = runTest {
            // When
            val user1CompletedCount = taskDao.countCompletedTasks("user-1")
            val user2CompletedCount = taskDao.countCompletedTasks("user-2")

            // Then
            assertEquals(2, user1CompletedCount)
            assertEquals(1, user2CompletedCount)
        }

        @Test
        @DisplayName("Should return zero for user with no completed tasks")
        fun shouldReturnZeroForUserWithNoCompletedTasks() = runTest {
            // When
            val completedCount = taskDao.countCompletedTasks("user-with-no-tasks")

            // Then
            assertEquals(0, completedCount)
        }

        @Test
        @DisplayName("Should count tokens earned for user")
        fun shouldCountTokensEarnedForUser() = runTest {
            // When
            val user1TokensEarned = taskDao.countTokensEarned("user-1")
            val user2TokensEarned = taskDao.countTokensEarned("user-2")

            // Then
            assertEquals(15, user1TokensEarned) // 5 + 10 from completed tasks
            assertEquals(8, user2TokensEarned) // 8 from completed task
        }

        @Test
        @DisplayName("Should return zero tokens for user with no completed tasks")
        fun shouldReturnZeroTokensForUserWithNoCompletedTasks() = runTest {
            // When
            val tokensEarned = taskDao.countTokensEarned("user-with-no-tasks")

            // Then
            assertEquals(0, tokensEarned ?: 0) // Handle nullable result
        }

        @Test
        @DisplayName("Should handle null result for user with no tasks")
        fun shouldHandleNullResultForUserWithNoTasks() = runTest {
            // When
            val tokensEarned = taskDao.countTokensEarned("user-with-no-tasks")

            // Then - SUM can return null if no rows match
            assertEquals(0, tokensEarned ?: 0)
        }
    }

    @Nested
    @DisplayName("Data Integrity")
    inner class DataIntegrity {

        @Test
        @DisplayName("Should preserve all task fields correctly")
        fun shouldPreserveAllTaskFields() = runTest {
            // Given
            val currentTime = System.currentTimeMillis()
            val task = EntityTestFactory.createTestTaskEntity(
                id = "test-task",
                title = "Test Task",
                assignedToUserId = "test-user",
            ).copy(
                category = "PERSONAL_CARE",
                tokenReward = 42,
                isCompleted = true,
                createdAt = currentTime,
                completedAt = currentTime,
                customIconName = "default_icon",
                customColorHex = "#FFFFFF",
            )

            // When
            taskDao.insert(task)

            // Then
            val retrievedTask = taskDao.findById("test-task")
            assertNotNull(retrievedTask)
            assertEquals(task.id, retrievedTask.id)
            assertEquals(task.title, retrievedTask.title)
            assertEquals(task.category, retrievedTask.category)
            assertEquals(task.tokenReward, retrievedTask.tokenReward)
            assertEquals(task.isCompleted, retrievedTask.isCompleted)
            assertEquals(task.assignedToUserId, retrievedTask.assignedToUserId)
            assertEquals(task.createdAt, retrievedTask.createdAt)
        }

        @Test
        @DisplayName("Should handle task with zero token reward")
        fun shouldHandleZeroTokenReward() = runTest {
            // Given
            val task = EntityTestFactory.createTaskEntity(
                id = "zero-reward-task",
                tokenReward = 0,
            )

            // When
            taskDao.insert(task)

            // Then
            val retrievedTask = taskDao.findById("zero-reward-task")
            assertNotNull(retrievedTask)
            assertEquals(0, retrievedTask.tokenReward)
        }

        @Test
        @DisplayName("Should handle task with large token reward")
        fun shouldHandleLargeTokenReward() = runTest {
            // Given
            val task = EntityTestFactory.createTaskEntity(
                id = "high-reward-task",
                tokenReward = 999999,
            )

            // When
            taskDao.insert(task)

            // Then
            val retrievedTask = taskDao.findById("high-reward-task")
            assertNotNull(retrievedTask)
            assertEquals(999999, retrievedTask.tokenReward)
        }

        @Test
        @DisplayName("Should handle long task titles")
        fun shouldHandleLongTaskTitles() = runTest {
            // Given
            val longTitle = "This is a very long task title that might test the database's ability to handle longer strings without truncation or corruption"
            val task = EntityTestFactory.createTaskEntity(
                id = "long-title-task",
                title = longTitle,
            )

            // When
            taskDao.insert(task)

            // Then
            val retrievedTask = taskDao.findById("long-title-task")
            assertNotNull(retrievedTask)
            assertEquals(longTitle, retrievedTask.title)
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should handle large number of task insertions efficiently")
        fun shouldHandleManyInsertions() = runTest {
            val startTime = System.currentTimeMillis()

            // Given
            val tasks = (1..1000).map { index ->
                EntityTestFactory.createTestTaskEntity(
                    id = "task-$index",
                    title = "Task $index",
                    tokenReward = index % 20, // Vary rewards
                    isCompleted = index % 3 == 0, // Every 3rd task completed
                    assignedToUserId = "user-${index % 10}", // Distribute across 10 users
                )
            }

            // When
            tasks.forEach { taskDao.insert(it) }

            // Then
            val endTime = System.currentTimeMillis()
            val executionTime = endTime - startTime

            DatabaseAssertions.verifyTaskCount(taskDao, 1000)
            println("Inserted 1000 tasks in ${executionTime}ms")

            // Verify we can still query efficiently
            val queryStartTime = System.currentTimeMillis()
            val allTasks = taskDao.getAllTasks()
            val user1Tasks = taskDao.findByUserId("user-1")
            val completedTasks = taskDao.findCompletedByUserId("user-1")
            val queryEndTime = System.currentTimeMillis()
            val queryTime = queryEndTime - queryStartTime

            assertEquals(1000, allTasks.size)
            assertEquals(100, user1Tasks.size) // user-1, user-11, user-21, etc.
            assertTrue(completedTasks.isNotEmpty())
            println("Executed complex queries on 1000 tasks in ${queryTime}ms")
        }

        @Test
        @DisplayName("Should handle complex aggregation queries efficiently")
        fun shouldHandleComplexAggregations() = runTest {
            // Given - Create many tasks for aggregation testing
            val tasks = (1..500).map { index ->
                EntityTestFactory.createTestTaskEntity(
                    id = "task-$index",
                    tokenReward = index % 50, // Vary rewards 0-49
                    isCompleted = index % 2 == 0, // Half completed
                    assignedToUserId = "user-${index % 5}", // 5 users
                )
            }
            tasks.forEach { taskDao.insert(it) }

            // When - Execute aggregation queries
            val startTime = System.currentTimeMillis()

            val user0CompletedCount = taskDao.countCompletedTasks("user-0")
            val user1TokensEarned = taskDao.countTokensEarned("user-1")
            val user2CompletedTasks = taskDao.findCompletedByUserId("user-2")
            val user3IncompleteTasks = taskDao.findIncompleteByUserId("user-3")

            val endTime = System.currentTimeMillis()

            // Then
            assertTrue(user0CompletedCount > 0)
            assertTrue((user1TokensEarned ?: 0) > 0)
            assertTrue(user2CompletedTasks.isNotEmpty())
            assertTrue(user3IncompleteTasks.isNotEmpty())

            println("Executed aggregation queries in ${endTime - startTime}ms")
        }
    }

    @Nested
    @DisplayName("Domain Conversion")
    inner class DomainConversion {

        @Test
        @DisplayName("Should convert to domain model correctly")
        fun shouldConvertToDomainModel() = runTest {
            // Given
            val taskEntity = EntityTestFactory.createTaskEntity(
                id = "test-task",
                category = TaskCategory.PERSONAL_CARE,
                tokenReward = 15,
                isCompleted = false,
                assignedToUserId = "user-1",
            )
            taskDao.insert(taskEntity)

            // When
            val retrievedEntity = taskDao.findById("test-task")!!
            val domainTask = retrievedEntity.toDomain()

            // Then
            assertEquals(taskEntity.id, domainTask.id)
            assertEquals(taskEntity.title, domainTask.title)
            assertEquals(taskEntity.category, domainTask.category.name)
            assertEquals(taskEntity.tokenReward, domainTask.tokenReward)
            assertEquals(taskEntity.isCompleted, domainTask.isCompleted)
            assertEquals(taskEntity.assignedToUserId, domainTask.assignedToUserId)
            assertEquals(taskEntity.createdAt, domainTask.createdAt)
        }

        @Test
        @DisplayName("Should convert from domain model correctly")
        fun shouldConvertFromDomainModel() = runTest {
            // Given
            val domainTask = com.lemonqwest.app.domain.TestDataFactory.createTask(
                id = "domain-task",
                title = "Domain Task",
                category = com.lemonqwest.app.domain.task.TaskCategory.HOMEWORK,
                assignedToUserId = "user-1",
                isCompleted = true,
            )

            // When
            val taskEntity = TaskEntity.fromDomain(domainTask)
            taskDao.insert(taskEntity)

            // Then
            val retrievedEntity = taskDao.findById("domain-task")
            assertNotNull(retrievedEntity)
            assertEquals(domainTask.id, retrievedEntity.id)
            assertEquals(domainTask.title, retrievedEntity.title)
            assertEquals(domainTask.category.name, retrievedEntity.category)
            assertEquals(domainTask.tokenReward, retrievedEntity.tokenReward)
            assertEquals(domainTask.isCompleted, retrievedEntity.isCompleted)
            assertEquals(domainTask.assignedToUserId, retrievedEntity.assignedToUserId)
            assertEquals(domainTask.createdAt, retrievedEntity.createdAt)
        }
    }
}
