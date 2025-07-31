package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Focused test suite for achievement data validation.
 *
 * Tests cover:
 * - Achievement ID format validation
 * - User ID format validation
 * - Progress bounds validation
 * - Unlock timestamp validation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Data Validation Tests")
class AchievementDataValidationTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should validate achievement ID format")
    fun shouldValidateAchievementIdFormat() {
        val userId = UUID.randomUUID().toString()
        val achievement = Achievement.create(AchievementType.FIRST_STEPS, userId)

        val expectedId = "first_steps-$userId"
        assertEquals(expectedId, achievement.id, "Achievement ID should follow expected format")
    }

    @Test
    @DisplayName("Should validate user ID format")
    fun shouldValidateUserIdFormat() {
        val userId = UUID.randomUUID().toString()
        val achievement = TestDataFactory.createAchievement(userId = userId)

        assertNotNull(UUID.fromString(achievement.userId), "User ID should be valid UUID")
    }

    @Test
    @DisplayName("Should validate progress bounds")
    fun shouldValidateProgressBounds() {
        val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

        // Test various progress values
        val progressValues = listOf(-10, -1, 0, 1, 5, 10, 15, 100)

        progressValues.forEach { progress ->
            val updatedAchievement = achievement.updateProgress(progress)
            assertTrue(
                updatedAchievement.progress >= 0,
                "Progress should be non-negative for value $progress",
            )
        }
    }

    @Test
    @DisplayName("Should validate unlock timestamp")
    fun shouldValidateUnlockTimestamp() {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.FIRST_STEPS,
            progress = 1,
        )

        val unlockedAchievement = achievement.unlock()

        assertNotNull(
            unlockedAchievement.unlockedAt,
            "Unlocked achievement should have timestamp",
        )
        assertTrue(unlockedAchievement.unlockedAt!! > 0, "Unlock timestamp should be positive")
    }
}
