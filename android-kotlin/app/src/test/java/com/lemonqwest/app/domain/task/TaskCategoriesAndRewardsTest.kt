package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldHaveDefaultReward
import com.lemonqwest.app.domain.shouldHaveProperties
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.UUID

/**
 * Focused test suite for Task categories and token rewards with complete test isolation.
 *
 * Tests cover:
 * - Default reward assignment per category with test isolation
 * - Personal care task category validation with parallel safety
 * - Household task category validation with modern patterns
 * - Homework task category validation with thread safety
 * - Custom token reward handling with complete isolation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Categories and Rewards Tests")
class TaskCategoriesAndRewardsTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @ParameterizedTest
    @EnumSource(TaskCategory::class)
    @DisplayName("Should assign correct default rewards for each category")
    fun shouldAssignCorrectDefaultRewards(category: TaskCategory) = runTest {
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
    fun shouldValidatePersonalCareTaskProperties() = runTest {
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
    fun shouldValidateHouseholdTaskProperties() = runTest {
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
    fun shouldValidateHomeworkTaskProperties() = runTest {
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
    fun shouldHandleCustomTokenRewards() = runTest {
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
