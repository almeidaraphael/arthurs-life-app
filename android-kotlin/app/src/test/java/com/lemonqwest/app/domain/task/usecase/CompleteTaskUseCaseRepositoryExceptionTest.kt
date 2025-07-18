package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.TaskDomainException
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.task.TaskRepositoryException
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.UUID

/**
 * Focused test suite for repository exception scenarios.
 *
 * Tests cover:
 * - Task repository exceptions during find and update
 * - User repository exceptions during update
 * - Achievement tracking exceptions
 * - Proper exception propagation
 */
@DisplayName("CompleteTaskUseCase - Repository Exception Tests")
@Execution(ExecutionMode.SAME_THREAD)
class CompleteTaskUseCaseRepositoryExceptionTest {

    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var achievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        taskRepository = mockk()
        userRepository = mockk()
        achievementTrackingUseCase = mockk()
        completeTaskUseCase = CompleteTaskUseCase(
            taskRepository,
            userRepository,
            achievementTrackingUseCase,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("Should handle task repository exception during find")
    fun shouldHandleTaskRepositoryExceptionDuringFind() = mainDispatcherRule.runTest {
        // Given
        val taskId = UUID.randomUUID().toString()
        val repositoryException = TaskRepositoryException("Database connection failed")

        coEvery { taskRepository.findById(taskId) } throws repositoryException

        // When
        val result = completeTaskUseCase(taskId)

        // Then
        assertTrue(result.isFailure, "Should fail when repository throws exception")
        assertEquals(
            repositoryException,
            result.exceptionOrNull(),
            "Should propagate repository exception",
        )
    }

    @Test
    @DisplayName("Should handle task repository exception during update")
    fun shouldHandleTaskRepositoryExceptionDuringUpdate() = mainDispatcherRule.runTest {
        // Given
        val user = TestDataFactory.createChildUser()
        val task = TestDataFactory.createTask(assignedToUserId = user.id)
        val repositoryException = TaskRepositoryException("Failed to update task")

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(user.id) } returns user
        coEvery { taskRepository.updateTask(any()) } throws repositoryException

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isFailure, "Should fail when task update throws exception")
        assertEquals(
            repositoryException,
            result.exceptionOrNull(),
            "Should propagate repository exception",
        )
    }

    @Test
    @DisplayName("Should handle user repository exception during update")
    fun shouldHandleUserRepositoryExceptionDuringUpdate() = mainDispatcherRule.runTest {
        // Given
        val user = TestDataFactory.createChildUser()
        val task = TestDataFactory.createTask(assignedToUserId = user.id)
        val repositoryException = TaskRepositoryException("Failed to update user")

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(user.id) } returns user
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } throws repositoryException

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isFailure, "Should fail when user update throws exception")
        assertEquals(
            repositoryException,
            result.exceptionOrNull(),
            "Should propagate repository exception",
        )
    }

    @Test
    @DisplayName("Should handle achievement tracking exception")
    fun shouldHandleAchievementTrackingException() = mainDispatcherRule.runTest {
        // Given
        val user = TestDataFactory.createChildUser()
        val task = TestDataFactory.createTask(assignedToUserId = user.id)
        val achievementException = TaskDomainException("Achievement tracking failed")

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(user.id) } returns user
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } throws achievementException

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isFailure, "Should fail when achievement tracking throws exception")
        assertEquals(
            achievementException,
            result.exceptionOrNull(),
            "Should propagate achievement exception",
        )
    }
}
