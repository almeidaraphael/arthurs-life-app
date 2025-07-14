package com.arthurslife.app.infrastructure.task

import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.TaskDataSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory implementation of TaskDataSource for Arthur's Life MVP.
 *
 * This implementation provides task persistence using in-memory storage,
 * following the same pattern as InMemoryUserDataSource. It includes
 * pre-populated sample tasks across all three categories to demonstrate
 * the task management functionality.
 *
 * The implementation uses a Mutex to ensure thread-safe operations on
 * the task collection, supporting concurrent access from multiple coroutines.
 */
@Singleton
class InMemoryTaskDataSource
@Inject
constructor() : TaskDataSource {

    private val mutex = Mutex()
    private val tasks = mutableListOf<Task>()

    companion object {
        // Child user ID from existing InMemoryUserDataSource
        private const val CHILD_USER_ID = "child-1"
    }

    init {
        // Initialize with sample tasks across all categories
        tasks.addAll(createSampleTasks())
    }

    /**
     * Creates a comprehensive set of sample tasks across all three categories.
     * These tasks demonstrate various age-appropriate activities and provide
     * immediate content for caregivers to work with.
     */
    private fun createSampleTasks(): List<Task> =
        createPersonalCareTasks() + createHouseholdTasks() + createHomeworkTasks()

    /**
     * Creates sample personal care tasks (5 tokens each).
     */
    private fun createPersonalCareTasks(): List<Task> = listOf(
        Task.create(
            title = "Brush your teeth",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Wash your hands",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Get dressed",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Comb your hair",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Take a shower",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Put on clean clothes",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = CHILD_USER_ID,
        ),
    )

    /**
     * Creates sample household tasks (10 tokens each).
     */
    private fun createHouseholdTasks(): List<Task> = listOf(
        Task.create(
            title = "Make your bed",
            category = TaskCategory.HOUSEHOLD,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Clean your room",
            category = TaskCategory.HOUSEHOLD,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Put away toys",
            category = TaskCategory.HOUSEHOLD,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Help set the table",
            category = TaskCategory.HOUSEHOLD,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Feed the pets",
            category = TaskCategory.HOUSEHOLD,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Take out trash",
            category = TaskCategory.HOUSEHOLD,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Water the plants",
            category = TaskCategory.HOUSEHOLD,
            assignedToUserId = CHILD_USER_ID,
        ),
    )

    /**
     * Creates sample homework tasks (15 tokens each).
     */
    private fun createHomeworkTasks(): List<Task> = listOf(
        Task.create(
            title = "Complete math homework",
            category = TaskCategory.HOMEWORK,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Read for 20 minutes",
            category = TaskCategory.HOMEWORK,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Practice spelling words",
            category = TaskCategory.HOMEWORK,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Write in journal",
            category = TaskCategory.HOMEWORK,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Study for test",
            category = TaskCategory.HOMEWORK,
            assignedToUserId = CHILD_USER_ID,
        ),
        Task.create(
            title = "Complete science project",
            category = TaskCategory.HOMEWORK,
            assignedToUserId = CHILD_USER_ID,
        ),
    )

    override suspend fun findById(id: String): Task? =
        mutex.withLock {
            tasks.find { it.id == id }
        }

    override suspend fun findByUserId(userId: String): List<Task> =
        mutex.withLock {
            tasks.filter { it.assignedToUserId == userId }
        }

    override suspend fun getAllTasks(): List<Task> =
        mutex.withLock {
            tasks.toList()
        }

    override suspend fun findByCategory(category: TaskCategory): List<Task> =
        mutex.withLock {
            tasks.filter { it.category == category }
        }

    override suspend fun findCompletedByUserId(userId: String): List<Task> =
        mutex.withLock {
            tasks.filter { it.assignedToUserId == userId && it.isCompleted }
        }

    override suspend fun findIncompleteByUserId(userId: String): List<Task> =
        mutex.withLock {
            tasks.filter { it.assignedToUserId == userId && !it.isCompleted }
        }

    override suspend fun saveTask(task: Task): Unit =
        mutex.withLock {
            val existingIndex = tasks.indexOfFirst { it.id == task.id }
            if (existingIndex >= 0) {
                tasks[existingIndex] = task
            } else {
                tasks.add(task)
            }
        }

    override suspend fun deleteTask(taskId: String): Unit =
        mutex.withLock {
            tasks.removeIf { it.id == taskId }
        }

    override suspend fun countCompletedTasks(userId: String): Int =
        mutex.withLock {
            tasks.count { it.assignedToUserId == userId && it.isCompleted }
        }

    override suspend fun countTokensEarned(userId: String): Int =
        mutex.withLock {
            tasks.filter { it.assignedToUserId == userId && it.isCompleted }
                .sumOf { it.tokenReward }
        }
}
