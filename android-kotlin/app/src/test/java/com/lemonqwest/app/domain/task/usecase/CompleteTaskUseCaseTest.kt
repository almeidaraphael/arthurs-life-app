package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.user.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName

/**
 * Comprehensive test suite for CompleteTaskUseCase.
 *
 * Tests cover:
 * - Successful task completion with token rewards
 * - Achievement tracking integration
 * - Error handling for various failure scenarios
 * - Repository interaction validation
 * - Business rule enforcement
 * - Edge cases and concurrent scenarios
 */
@DisplayName("CompleteTaskUseCase Tests")
class CompleteTaskUseCaseTest {

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
            taskRepository = taskRepository,
            userRepository = userRepository,
            achievementTrackingUseCase = achievementTrackingUseCase,
        )
    }

// CompleteTaskUseCase tests are now split across focused test files:
    // - CompleteTaskUseCaseSuccessfulCompletionTest.kt
    // - CompleteTaskUseCaseTaskNotFoundTest.kt
    // - CompleteTaskUseCaseTaskAlreadyCompletedTest.kt
    // - CompleteTaskUseCaseUserNotFoundTest.kt
    // - CompleteTaskUseCaseRepositoryExceptionTest.kt
    // - CompleteTaskUseCaseBusinessRulesTest.kt
    // - CompleteTaskUseCaseIntegrationTest.kt
}
