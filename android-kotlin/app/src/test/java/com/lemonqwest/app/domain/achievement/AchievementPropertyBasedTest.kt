package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Comprehensive property-based test suite for Achievement domain entity.
 *
 * Tests cover:
 * - Various progress value handling with test isolation
 * - Both unlock state scenarios with complete isolation
 * - Progress percentage calculations with parallel safety
 * - Parameterized test scenarios with modern patterns
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Property-Based Tests")
class AchievementPropertyBasedTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 5, 10, 25, 50, 100])
    @DisplayName("Should handle various progress values")
    fun shouldHandleVariousProgressValues(progress: Int) = runTest {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.TOKEN_COLLECTOR,
            progress = progress,
        )

        assertEquals(
            progress,
            achievement.progress,
            "Achievement should have specified progress",
        )
        assertTrue(
            achievement.progressPercentage >= 0,
            "Progress percentage should be non-negative",
        )
        assertTrue(
            achievement.progressPercentage <= 100,
            "Progress percentage should not exceed 100%",
        )

        val expectedPercentage = (progress * 100 / 50).coerceAtMost(100)
        assertEquals(
            expectedPercentage,
            achievement.progressPercentage,
            "Progress percentage should be calculated correctly",
        )
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    @DisplayName("Should handle both unlock states")
    fun shouldHandleBothUnlockStates(isUnlocked: Boolean) = runTest {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.FIRST_STEPS,
            progress = 1,
            isUnlocked = isUnlocked,
        )

        assertEquals(
            isUnlocked,
            achievement.isUnlocked,
            "Achievement should have specified unlock state",
        )

        if (isUnlocked) {
            assertNotNull(achievement.unlockedAt, "Unlocked achievement should have timestamp")
        } else {
            assertNull(achievement.unlockedAt, "Locked achievement should not have timestamp")
        }
    }
}
