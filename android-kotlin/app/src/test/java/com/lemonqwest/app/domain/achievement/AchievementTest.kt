package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Comprehensive test suite for the Achievement domain entity.
 *
 * Tests cover:
 * - Achievement basic entity behavior
 * - Achievement domain logic
 * - Achievement state management
 * - Core achievement functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Domain Entity Tests")
class AchievementTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var achievement: Achievement

    @BeforeEach
    fun setUp() {
        achievement = TestDataFactory.createAchievement()
    }

    @Test
    @DisplayName("Should have valid achievement entity")
    fun shouldHaveValidAchievementEntity() = runTest {
        assertNotNull(achievement.id, "Achievement should have an ID")
        assertTrue(achievement.id.isNotBlank(), "Achievement ID should not be blank")
        assertNotNull(achievement.type, "Achievement should have a type")
        assertNotNull(achievement.userId, "Achievement should have a user ID")
    }

    @Test
    @DisplayName("Should have valid achievement state")
    fun shouldHaveValidAchievementState() = runTest {
        assertTrue(achievement.progress >= 0, "Current progress should be non-negative")
        assertTrue(
            achievement.progressPercentage >= 0,
            "Progress percentage should be non-negative",
        )
        assertTrue(
            achievement.progressPercentage <= 100,
            "Progress percentage should not exceed 100",
        )
    }

    @Test
    @DisplayName("Should handle achievement type correctly")
    fun shouldHandleAchievementTypeCorrectly() = runTest {
        val taskMasterAchievement = TestDataFactory.createAchievement(
            type = AchievementType.TASK_MASTER,
        )
        assertEquals(AchievementType.TASK_MASTER, taskMasterAchievement.type)
        assertEquals(
            AchievementType.TASK_MASTER.target,
            taskMasterAchievement.target,
            "Task Master target should match achievement type target",
        )
    }

    @Test
    @DisplayName("Should calculate progress percentage correctly")
    fun shouldCalculateProgressPercentageCorrectly() = runTest {
        val partialAchievement = TestDataFactory.createPartialAchievement(
            type = AchievementType.CENTURY_CLUB,
            progressPercentage = 60,
        )

        assertEquals(
            60,
            partialAchievement.progressPercentage,
            "Progress percentage should match expected",
        )
        val expectedProgress = (AchievementType.CENTURY_CLUB.target * 60) / 100
        assertEquals(
            expectedProgress,
            partialAchievement.progress,
            "Current progress should be calculated correctly",
        )
    }

    // Note: Detailed test coverage is provided by specialized test files:
    // - AchievementCreationTest.kt: Achievement creation and initialization
    // - AchievementProgressTrackingTest.kt: Progress tracking logic
    // - AchievementUnlockLogicTest.kt: Unlock conditions and logic
    // - AchievementBusinessRulesTest.kt: Business rule validation
    // - AchievementEdgeCasesTest.kt: Edge cases and error conditions
    // - AchievementDataValidationTest.kt: Data validation rules
    // - AchievementCollectionsTest.kt: Achievement collections and sets
}
