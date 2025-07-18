package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementType
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.OptIn

/**
 * Focused test suite for AchievementViewModel achievement types and categories.
 *
 * Tests cover:
 * - Handle different achievement types
 * - Handle achievements with progress tracking
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AchievementViewModel Types Tests")
class AchievementViewModelTypesTest : AchievementViewModelTestBase() {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    @Test
    fun `should handle different achievement types`() {
        val diverseAchievements = listOf(
            TestDataFactory.createAchievement(
                type = AchievementType.TASK_MASTER,
                userId = testChild.id,
                isUnlocked = true,
            ),
            TestDataFactory.createAchievement(
                type = AchievementType.THREE_DAY_STREAK,
                userId = testChild.id,
                isUnlocked = false,
            ),
            TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                userId = testChild.id,
                isUnlocked = true,
            ),
        )
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns diverseAchievements
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val achievements = viewModel.achievements.first()
        assertEquals(diverseAchievements.size, achievements.size)
        val achievementTypes = achievements.map { it.type }.toSet()
        assertTrue(achievementTypes.contains(AchievementType.TASK_MASTER))
        assertTrue(achievementTypes.contains(AchievementType.THREE_DAY_STREAK))
        assertTrue(achievementTypes.contains(AchievementType.FIRST_STEPS))
    }

    @Test
    fun `should handle achievements with progress tracking`() {
        val progressAchievements = listOf(
            TestDataFactory.createAchievement(
                type = AchievementType.CENTURY_CLUB,
                userId = testChild.id,
                progress = 7,
                isUnlocked = false,
            ),
            TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                userId = testChild.id,
                progress = 1,
                isUnlocked = true,
            ),
        )
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns progressAchievements
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val achievements = viewModel.achievements.first()
        assertEquals(progressAchievements.size, achievements.size)
        val progressAchievement = achievements.find { it.type == AchievementType.CENTURY_CLUB }
        assertEquals(7, progressAchievement?.progress)
        assertEquals(10, progressAchievement?.target) // CENTURY_CLUB target is 10
        assertFalse(progressAchievement?.isUnlocked ?: true)
        val completedAchievement = achievements.find { it.type == AchievementType.FIRST_STEPS }
        assertTrue(completedAchievement?.isUnlocked ?: false)
    }
}
