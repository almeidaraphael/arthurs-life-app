package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeUnlockable
import com.lemonqwest.app.domain.shouldNotBeUnlockable
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for achievement business rules.
 *
 * Tests cover:
 * - Unlock requirement enforcement
 * - Achievement immutability validation
 * - Type consistency validation
 * - Category relationship validation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Business Rules Tests")
class AchievementBusinessRulesTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should enforce unlock requirements")
    fun shouldEnforceUnlockRequirements() {
        AchievementType.values().forEach { type ->
            val belowTargetAchievement = TestDataFactory.createAchievement(
                type = type,
                progress = type.target - 1,
            )

            val atTargetAchievement = TestDataFactory.createAchievement(
                type = type,
                progress = type.target,
            )

            if (type.target > 1) {
                belowTargetAchievement.shouldNotBeUnlockable()
            }
            atTargetAchievement.shouldBeUnlockable()
        }
    }

    @Test
    @DisplayName("Should maintain achievement immutability")
    fun shouldMaintainAchievementImmutability() {
        val originalAchievement = TestDataFactory.createAchievement()
        val originalProgress = originalAchievement.progress
        val originalUnlocked = originalAchievement.isUnlocked

        // Modify achievement through methods
        val updatedAchievement = originalAchievement.updateProgress(5)

        // Original should remain unchanged
        assertEquals(
            originalProgress,
            originalAchievement.progress,
            "Original progress should remain unchanged",
        )
        assertEquals(
            originalUnlocked,
            originalAchievement.isUnlocked,
            "Original unlock status should remain unchanged",
        )

        // Updated should have changes
        assertEquals(
            5,
            updatedAchievement.progress,
            "Updated achievement should have new progress",
        )
    }

    @Test
    @DisplayName("Should validate achievement type consistency")
    fun shouldValidateAchievementTypeConsistency() {
        AchievementType.values().forEach { type ->
            val achievement = TestDataFactory.createAchievement(type = type)

            assertEquals(type, achievement.type, "Achievement type should match")
            assertEquals(
                type.displayName,
                achievement.name,
                "Achievement name should match type",
            )
            assertEquals(
                type.description,
                achievement.description,
                "Achievement description should match type",
            )
            assertEquals(
                type.target,
                achievement.target,
                "Achievement target should match type",
            )
        }
    }

    @Test
    @DisplayName("Should validate category relationships")
    fun shouldValidateCategoryRelationships() {
        AchievementType.values().forEach { type ->
            val achievement = TestDataFactory.createAchievement(type = type)

            // Verify category relationship
            assertEquals(
                type.category,
                achievement.type.category,
                "Achievement should have correct category",
            )

            // Verify category properties
            val category = type.category
            assertTrue(
                category.displayName.isNotBlank(),
                "Category display name should not be blank",
            )
            assertTrue(
                category.description.isNotBlank(),
                "Category description should not be blank",
            )
        }
    }
}
