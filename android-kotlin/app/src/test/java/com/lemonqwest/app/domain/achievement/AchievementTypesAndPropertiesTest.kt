package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldHaveProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

/**
 * Focused test suite for achievement types and properties validation.
 *
 * Tests cover:
 * - All achievement type validation
 * - Type-specific property verification
 * - Achievement category validation
 * - Category-type relationship validation
 */
@DisplayName("Achievement Types and Properties Tests")
class AchievementTypesAndPropertiesTest {

    @ParameterizedTest
    @EnumSource(AchievementType::class)
    @DisplayName("Should validate all achievement types")
    fun shouldValidateAllAchievementTypes(type: AchievementType) {
        val achievement = TestDataFactory.createAchievement(type = type)

        achievement.shouldHaveProperties(type, achievement.userId)
        assertEquals(
            type.displayName,
            achievement.name,
            "Achievement name should match type display name",
        )
        assertEquals(
            type.description,
            achievement.description,
            "Achievement description should match type",
        )
        assertEquals(type.target, achievement.target, "Achievement target should match type")

        // Verify type-specific properties
        verifyAchievementTypeSpecificProperties(type, achievement)
    }

    private fun verifyAchievementTypeSpecificProperties(type: AchievementType, achievement: Achievement) {
        when (type.category) {
            AchievementCategory.MILESTONE -> verifyMilestoneAchievement(type, achievement)
            AchievementCategory.DAILY -> verifyDailyAchievement(type, achievement)
            AchievementCategory.CONSISTENCY -> verifyConsistencyAchievement(type, achievement)
            AchievementCategory.ECONOMY -> verifyEconomyAchievement(type, achievement)
        }
    }

    private fun verifyMilestoneAchievement(type: AchievementType, achievement: Achievement) {
        when (type) {
            AchievementType.FIRST_STEPS -> {
                assertEquals("First Steps", achievement.name)
                assertEquals("Complete your first task", achievement.description)
                assertEquals(1, achievement.target)
                assertEquals(AchievementCategory.MILESTONE, type.category)
            }
            AchievementType.CENTURY_CLUB -> {
                assertEquals("Century Club", achievement.name)
                assertEquals("Complete 10 total tasks", achievement.description)
                assertEquals(10, achievement.target)
                assertEquals(AchievementCategory.MILESTONE, type.category)
            }
            AchievementType.TASK_CHAMPION -> {
                assertEquals("Task Champion Trophy", achievement.name)
                assertEquals("Complete 25 total tasks", achievement.description)
                assertEquals(25, achievement.target)
                assertEquals(AchievementCategory.MILESTONE, type.category)
            }
            else -> error("Invalid milestone achievement type: $type")
        }
    }

    private fun verifyDailyAchievement(type: AchievementType, achievement: Achievement) {
        when (type) {
            AchievementType.TASK_MASTER -> {
                assertEquals("Task Master", achievement.name)
                assertEquals("Complete all daily tasks", achievement.description)
                assertEquals(1, achievement.target)
                assertEquals(AchievementCategory.DAILY, type.category)
            }
            AchievementType.SPEED_DEMON -> {
                assertEquals("Speed Demon Trophy", achievement.name)
                assertEquals("Complete 5 tasks in one day", achievement.description)
                assertEquals(5, achievement.target)
                assertEquals(AchievementCategory.DAILY, type.category)
            }
            AchievementType.EARLY_BIRD -> {
                assertEquals("Early Bird Badge", achievement.name)
                assertEquals("Complete 3 tasks before noon", achievement.description)
                assertEquals(3, achievement.target)
                assertEquals(AchievementCategory.DAILY, type.category)
            }
            else -> error("Invalid daily achievement type: $type")
        }
    }

    private fun verifyConsistencyAchievement(type: AchievementType, achievement: Achievement) {
        when (type) {
            AchievementType.THREE_DAY_STREAK -> {
                assertEquals("3-Day Streak", achievement.name)
                assertEquals("Complete tasks for 3 consecutive days", achievement.description)
                assertEquals(3, achievement.target)
                assertEquals(AchievementCategory.CONSISTENCY, type.category)
            }
            AchievementType.PERFECT_WEEK -> {
                assertEquals("Perfect Week Badge", achievement.name)
                assertEquals("Complete tasks for 7 consecutive days", achievement.description)
                assertEquals(7, achievement.target)
                assertEquals(AchievementCategory.CONSISTENCY, type.category)
            }
            else -> error("Invalid consistency achievement type: $type")
        }
    }

    private fun verifyEconomyAchievement(type: AchievementType, achievement: Achievement) {
        when (type) {
            AchievementType.TOKEN_COLLECTOR -> {
                assertEquals("Token Collector", achievement.name)
                assertEquals("Earn 50 total tokens", achievement.description)
                assertEquals(50, achievement.target)
                assertEquals(AchievementCategory.ECONOMY, type.category)
            }
            AchievementType.BIG_SPENDER -> {
                assertEquals("Big Spender Badge", achievement.name)
                assertEquals("Spend 100 total tokens on rewards", achievement.description)
                assertEquals(100, achievement.target)
                assertEquals(AchievementCategory.ECONOMY, type.category)
            }
            else -> error("Invalid economy achievement type: $type")
        }
    }

    @ParameterizedTest
    @EnumSource(AchievementCategory::class)
    @DisplayName("Should validate all achievement categories")
    fun shouldValidateAllAchievementCategories(category: AchievementCategory) {
        val typesInCategory = AchievementType.values().filter { it.category == category }
        assertTrue(
            typesInCategory.isNotEmpty(),
            "Category $category should have at least one achievement type",
        )

        // Verify category-specific properties
        when (category) {
            AchievementCategory.MILESTONE -> {
                assertEquals("Milestones", category.displayName)
                assertEquals(
                    "Major accomplishments and first-time achievements",
                    category.description,
                )
                assertTrue(typesInCategory.contains(AchievementType.FIRST_STEPS))
                assertTrue(typesInCategory.contains(AchievementType.CENTURY_CLUB))
            }
            AchievementCategory.DAILY -> {
                assertEquals("Daily Goals", category.displayName)
                assertEquals(
                    "Daily task completion and routine achievements",
                    category.description,
                )
                assertTrue(typesInCategory.contains(AchievementType.TASK_MASTER))
            }
            AchievementCategory.CONSISTENCY -> {
                assertEquals("Consistency", category.displayName)
                assertEquals(
                    "Streak-based achievements for building habits",
                    category.description,
                )
                assertTrue(typesInCategory.contains(AchievementType.THREE_DAY_STREAK))
            }
            AchievementCategory.ECONOMY -> {
                assertEquals("Token Economy", category.displayName)
                assertEquals(
                    "Achievements related to earning and managing tokens",
                    category.description,
                )
                assertTrue(typesInCategory.contains(AchievementType.TOKEN_COLLECTOR))
            }
        }
    }
}
