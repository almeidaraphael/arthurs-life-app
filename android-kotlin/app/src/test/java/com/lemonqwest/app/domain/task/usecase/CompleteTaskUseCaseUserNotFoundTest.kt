package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.task.TaskUserNotFoundException
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
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
 * Focused test suite for user not found scenarios.
 *
 * Tests cover:
 * - Assigned user not found handling
 * - Orphaned task scenarios
 * - Proper exception handling and repository interaction
 */
@DisplayName("CompleteTaskUseCase - User Not Found Tests")
@Execution(ExecutionMode.SAME_THREAD)
class CompleteTaskUseCaseUserNotFoundTest {

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
    @DisplayName("Should fail when assigned user does not exist")
    fun shouldFailWhenAssignedUserDoesNotExist() = mainDispatcherRule.runTest {
        // Given
        val nonExistentUserId = UUID.randomUUID().toString()
        val task = TestDataFactory.createTask(assignedToUserId = nonExistentUserId)

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(nonExistentUserId) } returns null

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isFailure, "Should fail when assigned user does not exist")

        val exception = result.exceptionOrNull()
        assertTrue(
            exception is TaskUserNotFoundException,
            "Should throw TaskUserNotFoundException",
        )
        assertEquals(
            nonExistentUserId,
            (exception as TaskUserNotFoundException).userId,
            "Exception should contain user ID",
        )

        coVerify { taskRepository.findById(task.id) }
        coVerify { userRepository.findById(nonExistentUserId) }
        coVerify(exactly = 0) { taskRepository.updateTask(any()) }
        coVerify(exactly = 0) { userRepository.updateUser(any()) }
    }

    @Test
    @DisplayName("Should handle orphaned tasks gracefully")
    fun shouldHandleOrphanedTasksGracefully() = mainDispatcherRule.runTest {
        // Given
        val deletedUserId = UUID.randomUUID().toString()
        val orphanedTask = TestDataFactory.createTask(assignedToUserId = deletedUserId)

        coEvery { taskRepository.findById(orphanedTask.id) } returns orphanedTask
        coEvery { userRepository.findById(deletedUserId) } returns null

        // When
        val result = completeTaskUseCase(orphanedTask.id)

        // Then
        assertTrue(result.isFailure, "Should fail for orphaned task")
        assertTrue(
            result.exceptionOrNull() is TaskUserNotFoundException,
            "Should throw TaskUserNotFoundException for orphaned task",
        )
    }
}
