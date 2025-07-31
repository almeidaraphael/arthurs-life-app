package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.RegisterExtension

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
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("CompleteTaskUseCase Tests")
class CompleteTaskUseCaseTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var achievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase

    @BeforeEach
    fun setUp() {
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
