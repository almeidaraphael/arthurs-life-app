package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldHaveProgress
import com.lemonqwest.app.domain.shouldHaveProperties
import com.lemonqwest.app.domain.shouldHaveTypeDistribution
import com.lemonqwest.app.domain.shouldHaveUnlockedCount
import com.lemonqwest.app.domain.shouldNotBeUnlocked
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Focused test suite for achievement collections and sets.
 *
 * Tests cover:
 * - Achievement set creation for users
 * - Type distribution validation
 * - Progress handling in collections
 * - Multi-user achievement separation
 * - Category-based filtering
 * - Progress calculations across sets
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Collections Tests")
class AchievementCollectionsTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should create achievement set for user")
    fun shouldCreateAchievementSetForUser() {
        val userId = UUID.randomUUID().toString()
        val achievements = TestDataFactory.createAchievementSet(userId = userId)

        assertEquals(10, achievements.size, "Should create all 10 achievement types")
        achievements.shouldHaveUnlockedCount(1) // First Steps is unlocked by default

        // Verify all achievements belong to the user
        achievements.forEach { achievement ->
            achievement.shouldHaveProperties(achievement.type, userId)
        }
    }

    @Test
    @DisplayName("Should create achievement set with correct type distribution")
    fun shouldCreateAchievementSetWithCorrectTypeDistribution() {
        val achievements = TestDataFactory.createAchievementSet()

        val expectedDistribution = AchievementType.values().associateWith { 1 }
        achievements.shouldHaveTypeDistribution(expectedDistribution)
    }

    @Test
    @DisplayName("Should create achievement set without progress")
    fun shouldCreateAchievementSetWithoutProgress() {
        val achievements = TestDataFactory.createAchievementSet(includeProgress = false)

        achievements.forEach { achievement ->
            achievement.shouldHaveProgress(0, 0)
            achievement.shouldNotBeUnlocked()
        }
    }

    @Test
    @DisplayName("Should create achievement set with progress")
    fun shouldCreateAchievementSetWithProgress() {
        val achievements = TestDataFactory.createAchievementSet(includeProgress = true)

        val unlockedAchievements = achievements.filter { it.isUnlocked }
        val partialAchievements = achievements.filter { it.progress > 0 && !it.isUnlocked }

        assertTrue(
            unlockedAchievements.isNotEmpty(),
            "Should have at least one unlocked achievement",
        )
        assertTrue(
            partialAchievements.isNotEmpty(),
            "Should have at least one partial achievement",
        )
    }

    @Test
    @DisplayName("Should handle multiple users with separate achievement sets")
    fun shouldHandleMultipleUsersWithSeparateAchievementSets() {
        val user1Id = UUID.randomUUID().toString()
        val user2Id = UUID.randomUUID().toString()

        val user1Achievements = TestDataFactory.createAchievementSet(userId = user1Id)
        val user2Achievements = TestDataFactory.createAchievementSet(userId = user2Id)

        // Verify separation
        user1Achievements.forEach { achievement ->
            assertEquals(
                user1Id,
                achievement.userId,
                "User 1 achievement should belong to user 1",
            )
        }

        user2Achievements.forEach { achievement ->
            assertEquals(
                user2Id,
                achievement.userId,
                "User 2 achievement should belong to user 2",
            )
        }

        // Verify unique IDs
        val allIds = (user1Achievements + user2Achievements).map { it.id }
        assertEquals(allIds.size, allIds.toSet().size, "All achievement IDs should be unique")
    }

    @Test
    @DisplayName("Should filter achievements by category")
    fun shouldFilterAchievementsByCategory() {
        val achievements = TestDataFactory.createAchievementSet()

        val milestoneAchievements = achievements.filter { it.type.category == AchievementCategory.MILESTONE }
        val dailyAchievements = achievements.filter { it.type.category == AchievementCategory.DAILY }
        val consistencyAchievements = achievements.filter { it.type.category == AchievementCategory.CONSISTENCY }
        val economyAchievements = achievements.filter { it.type.category == AchievementCategory.ECONOMY }

        assertEquals(3, milestoneAchievements.size, "Should have 3 milestone achievements")
        assertEquals(3, dailyAchievements.size, "Should have 3 daily achievements")
        assertEquals(2, consistencyAchievements.size, "Should have 2 consistency achievements")
        assertEquals(2, economyAchievements.size, "Should have 2 economy achievements")
    }

    @Test
    @DisplayName("Should calculate total progress across achievement set")
    fun shouldCalculateTotalProgressAcrossAchievementSet() {
        val achievements = TestDataFactory.createAchievementSet(includeProgress = true)

        val totalProgress = achievements.sumOf { it.progress }
        val totalTarget = achievements.sumOf { it.target }
        val overallPercentage = if (totalTarget > 0) (totalProgress * 100 / totalTarget) else 0

        assertTrue(totalProgress >= 0, "Total progress should be non-negative")
        assertTrue(totalTarget > 0, "Total target should be positive")
        assertTrue(overallPercentage >= 0, "Overall percentage should be non-negative")
        assertTrue(overallPercentage <= 100, "Overall percentage should not exceed 100%")
    }
}
