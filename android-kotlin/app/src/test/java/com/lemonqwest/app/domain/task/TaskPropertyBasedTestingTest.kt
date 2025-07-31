package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

/**
 * Test suite for Task property-based testing scenarios with complete test isolation.
 *
 * Tests cover:
 * - Various token reward values with test isolation
 * - Various task title formats with parallel safety
 * - Both completion states with modern patterns
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Property-Based Testing Tests")
class TaskPropertyBasedTestingTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 5, 10, 15, 25, 50, 100])
    @DisplayName("Should handle various token reward values")
    fun shouldHandleVariousTokenRewardValues(rewardAmount: Int) = runTest {
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
    fun shouldHandleVariousTaskTitleFormats(title: String) = runTest {
        val task = TestDataFactory.createTask(title = title)

        assertEquals(title, task.title, "Task should preserve title exactly")
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    @DisplayName("Should handle both completion states")
    fun shouldHandleBothCompletionStates(isCompleted: Boolean) = runTest {
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
