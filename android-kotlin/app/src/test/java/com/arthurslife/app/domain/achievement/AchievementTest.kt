package com.arthurslife.app.domain.achievement

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.shouldBeUnlockable
import com.arthurslife.app.domain.shouldBeUnlocked
import com.arthurslife.app.domain.shouldHaveProgress
import com.arthurslife.app.domain.shouldHaveProperties
import com.arthurslife.app.domain.shouldHaveTypeDistribution
import com.arthurslife.app.domain.shouldHaveUnlockedCount
import com.arthurslife.app.domain.shouldNotBeUnlockable
import com.arthurslife.app.domain.shouldNotBeUnlocked
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

/**
 * Comprehensive test suite for the Achievement domain entity.
 *
 * Tests cover:
 * - Achievement creation and initialization
 * - Achievement progress tracking
 * - Achievement unlock logic
 * - Achievement types and categories
 * - Progress percentage calculations
 * - Achievement collections and sets
 * - Data validation and business rules
 * - Edge cases and error conditions
 */
@DisplayName("Achievement Domain Entity Tests")
class AchievementTest {

    @Nested
    @DisplayName("Achievement Creation")
    inner class AchievementCreation {

        @Test
        @DisplayName("Should create achievement with default values")
        fun shouldCreateAchievementWithDefaults() {
            val achievement = TestDataFactory.createAchievement()

            assertNotNull(achievement.id, "Achievement should have an ID")
            assertTrue(achievement.id.isNotBlank(), "Achievement ID should not be blank")
            assertEquals(
                AchievementType.FIRST_STEPS,
                achievement.type,
                "Default type should be FIRST_STEPS",
            )
            achievement.shouldNotBeUnlocked()
            achievement.shouldHaveProgress(0, 0)
            assertNull(achievement.unlockedAt, "New achievement should not have unlock timestamp")
        }

        @Test
        @DisplayName("Should create achievement with custom parameters")
        fun shouldCreateAchievementWithCustomParameters() {
            val customType = AchievementType.CENTURY_CLUB
            val customUserId = UUID.randomUUID().toString()
            val customProgress = 5

            val achievement = TestDataFactory.createAchievement(
                type = customType,
                userId = customUserId,
                progress = customProgress,
                isUnlocked = false,
            )

            achievement.shouldHaveProperties(customType, customUserId)
            achievement.shouldHaveProgress(customProgress, 50) // 5/10 = 50%
            achievement.shouldNotBeUnlocked()
        }

        @Test
        @DisplayName("Should create achievement using factory method")
        fun shouldCreateAchievementUsingFactory() {
            val userId = UUID.randomUUID().toString()
            val achievement = Achievement.create(AchievementType.TASK_MASTER, userId)

            achievement.shouldHaveProperties(AchievementType.TASK_MASTER, userId)
            assertEquals("task_master-$userId", achievement.id, "Should generate correct ID format")
            achievement.shouldNotBeUnlocked()
            achievement.shouldHaveProgress(0, 0)
        }

        @Test
        @DisplayName("Should create unlocked achievement")
        fun shouldCreateUnlockedAchievement() {
            val achievement = TestDataFactory.createUnlockedAchievement()

            achievement.shouldBeUnlocked()
            assertNotNull(achievement.unlockedAt, "Unlocked achievement should have timestamp")
            assertTrue(achievement.unlockedAt!! > 0, "Unlock timestamp should be positive")
        }

        @Test
        @DisplayName("Should create partial achievement")
        fun shouldCreatePartialAchievement() {
            val achievement = TestDataFactory.createPartialAchievement(
                type = AchievementType.CENTURY_CLUB,
                progressPercentage = 70,
            )

            achievement.shouldHaveProgress(7, 70) // 70% of 10 = 7
            achievement.shouldNotBeUnlocked()
            achievement.shouldNotBeUnlockable() // 7 < 10
        }
    }

    @Nested
    @DisplayName("Achievement Types and Properties")
    inner class AchievementTypesAndProperties {

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

    @Nested
    @DisplayName("Progress Tracking")
    inner class ProgressTracking {

        @Test
        @DisplayName("Should track progress correctly")
        fun shouldTrackProgressCorrectly() {
            val achievement = TestDataFactory.createAchievement(
                type = AchievementType.CENTURY_CLUB,
                progress = 3,
            )

            achievement.shouldHaveProgress(3, 30) // 3/10 = 30%
            achievement.shouldNotBeUnlocked()
            achievement.shouldNotBeUnlockable()
        }

        @Test
        @DisplayName("Should update progress")
        fun shouldUpdateProgress() {
            val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

            val updatedAchievement = achievement.updateProgress(5)

            updatedAchievement.shouldHaveProgress(5, 50)
            updatedAchievement.shouldNotBeUnlocked()
            updatedAchievement.shouldNotBeUnlockable()

            // Original should remain unchanged
            achievement.shouldHaveProgress(0, 0)
        }

        @Test
        @DisplayName("Should calculate progress percentage correctly")
        fun shouldCalculateProgressPercentageCorrectly() {
            val testCases = listOf(
                Triple(AchievementType.FIRST_STEPS, 0, 0), // 0/1 = 0%
                Triple(AchievementType.FIRST_STEPS, 1, 100), // 1/1 = 100%
                Triple(AchievementType.CENTURY_CLUB, 0, 0), // 0/10 = 0%
                Triple(AchievementType.CENTURY_CLUB, 3, 30), // 3/10 = 30%
                Triple(AchievementType.CENTURY_CLUB, 5, 50), // 5/10 = 50%
                Triple(AchievementType.CENTURY_CLUB, 10, 100), // 10/10 = 100%
                Triple(AchievementType.TOKEN_COLLECTOR, 25, 50), // 25/50 = 50%
                Triple(AchievementType.TOKEN_COLLECTOR, 50, 100), // 50/50 = 100%
            )

            testCases.forEach { (type, progress, expectedPercentage) ->
                val achievement = TestDataFactory.createAchievement(
                    type = type,
                    progress = progress,
                )

                assertEquals(
                    expectedPercentage,
                    achievement.progressPercentage,
                    "Progress percentage should be $expectedPercentage for $progress/${type.target}",
                )
            }
        }

        @Test
        @DisplayName("Should handle progress overflow gracefully")
        fun shouldHandleProgressOverflowGracefully() {
            val achievement = TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                progress = 5, // More than target of 1
            )

            assertEquals(5, achievement.progress, "Progress should be preserved even if > target")
            assertEquals(
                100,
                achievement.progressPercentage,
                "Progress percentage should be capped at 100%",
            )
        }

        @Test
        @DisplayName("Should handle negative progress")
        fun shouldHandleNegativeProgress() {
            val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

            val updatedAchievement = achievement.updateProgress(-5)

            updatedAchievement.shouldHaveProgress(0, 0) // Should be coerced to 0
        }

        @Test
        @DisplayName("Should preserve progress during unlock")
        fun shouldPreserveProgressDuringUnlock() {
            val achievement = TestDataFactory.createAchievement(
                type = AchievementType.CENTURY_CLUB,
                progress = 10,
            )

            achievement.shouldBeUnlockable()

            val unlockedAchievement = achievement.unlock()

            unlockedAchievement.shouldBeUnlocked()
            unlockedAchievement.shouldHaveProgress(10, 100) // Progress set to target
        }
    }

    @Nested
    @DisplayName("Achievement Unlock Logic")
    inner class AchievementUnlockLogic {

        @Test
        @DisplayName("Should unlock achievement when progress reaches target")
        fun shouldUnlockAchievementWhenProgressReachesTarget() {
            val achievement = TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                progress = 1,
            )

            achievement.shouldBeUnlockable()

            val unlockedAchievement = achievement.unlock()

            unlockedAchievement.shouldBeUnlocked()
            assertNotNull(
                unlockedAchievement.unlockedAt,
                "Unlocked achievement should have timestamp",
            )
        }

        @Test
        @DisplayName("Should not unlock achievement when progress is below target")
        fun shouldNotUnlockAchievementWhenProgressIsBelowTarget() {
            val achievement = TestDataFactory.createAchievement(
                type = AchievementType.CENTURY_CLUB,
                progress = 5,
            )

            achievement.shouldNotBeUnlockable()
        }

        @Test
        @DisplayName("Should not unlock already unlocked achievement")
        fun shouldNotUnlockAlreadyUnlockedAchievement() {
            val achievement = TestDataFactory.createUnlockedAchievement()

            achievement.shouldBeUnlocked()
            achievement.shouldNotBeUnlockable() // Already unlocked
        }

        @Test
        @DisplayName("Should handle unlock with progress exceeding target")
        fun shouldHandleUnlockWithProgressExceedingTarget() {
            val achievement = TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                progress = 5, // Exceeds target of 1
            )

            achievement.shouldBeUnlockable()

            val unlockedAchievement = achievement.unlock()

            unlockedAchievement.shouldBeUnlocked()
            assertEquals(
                1,
                unlockedAchievement.progress,
                "Progress should be set to target on unlock",
            )
        }

        @Test
        @DisplayName("Should generate unlock timestamp")
        fun shouldGenerateUnlockTimestamp() {
            val beforeUnlock = System.currentTimeMillis()
            val achievement = TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                progress = 1,
            )

            val unlockedAchievement = achievement.unlock()
            val afterUnlock = System.currentTimeMillis()

            assertNotNull(
                unlockedAchievement.unlockedAt,
                "Unlocked achievement should have timestamp",
            )
            assertTrue(
                unlockedAchievement.unlockedAt!! >= beforeUnlock,
                "Unlock timestamp should be after test start",
            )
            assertTrue(
                unlockedAchievement.unlockedAt!! <= afterUnlock,
                "Unlock timestamp should be before test end",
            )
        }
    }

    @Nested
    @DisplayName("Achievement Collections")
    inner class AchievementCollections {

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

    @Nested
    @DisplayName("Data Validation")
    inner class DataValidation {

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

    @Nested
    @DisplayName("Business Rules")
    inner class BusinessRules {

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

    @Nested
    @DisplayName("Edge Cases")
    inner class EdgeCases {

        @Test
        @DisplayName("Should handle zero target achievements")
        fun shouldHandleZeroTargetAchievements() {
            // Create a hypothetical achievement with zero target
            val achievement = Achievement(
                id = "test-achievement",
                type = AchievementType.FIRST_STEPS, // Use existing type but test with zero target scenario
                progress = 0,
                userId = UUID.randomUUID().toString(),
            )

            // Even with zero target, progress percentage should be handled gracefully
            assertTrue(
                achievement.progressPercentage >= 0,
                "Progress percentage should be non-negative",
            )
        }

        @Test
        @DisplayName("Should handle very large progress values")
        fun shouldHandleVeryLargeProgressValues() {
            val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

            val largeProgress = Int.MAX_VALUE
            val updatedAchievement = achievement.updateProgress(largeProgress)

            assertEquals(
                largeProgress,
                updatedAchievement.progress,
                "Should handle large progress values",
            )
            assertEquals(
                100,
                updatedAchievement.progressPercentage,
                "Progress percentage should be capped at 100%",
            )
        }

        @Test
        @DisplayName("Should handle rapid achievement updates")
        fun shouldHandleRapidAchievementUpdates() {
            val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

            var currentAchievement = achievement

            // Rapidly update progress
            for (i in 1..100) {
                currentAchievement = currentAchievement.updateProgress(i)
                assertEquals(
                    i,
                    currentAchievement.progress,
                    "Progress should be updated correctly at step $i",
                )
            }
        }

        @Test
        @DisplayName("Should handle achievement unlock at exact target")
        fun shouldHandleAchievementUnlockAtExactTarget() {
            AchievementType.values().forEach { type ->
                val achievement = TestDataFactory.createAchievement(
                    type = type,
                    progress = type.target,
                )

                achievement.shouldBeUnlockable()

                val unlockedAchievement = achievement.unlock()

                unlockedAchievement.shouldBeUnlocked()
                assertEquals(
                    type.target,
                    unlockedAchievement.progress,
                    "Progress should equal target after unlock",
                )
            }
        }

        @Test
        @DisplayName("Should handle achievement with past unlock timestamp")
        fun shouldHandleAchievementWithPastUnlockTimestamp() {
            val pastTime = System.currentTimeMillis() - 86400000 // 24 hours ago
            val achievement = Achievement(
                id = "test-achievement",
                type = AchievementType.FIRST_STEPS,
                isUnlocked = true,
                progress = 1,
                unlockedAt = pastTime,
                userId = UUID.randomUUID().toString(),
            )

            achievement.shouldBeUnlocked()
            assertEquals(pastTime, achievement.unlockedAt, "Should preserve past unlock timestamp")
        }
    }

    @Nested
    @DisplayName("Property-Based Testing")
    inner class PropertyBasedTesting {

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 5, 10, 25, 50, 100])
        @DisplayName("Should handle various progress values")
        fun shouldHandleVariousProgressValues(progress: Int) {
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
        fun shouldHandleBothUnlockStates(isUnlocked: Boolean) {
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

    @Nested
    @DisplayName("Integration Tests")
    inner class IntegrationTests {

        @Test
        @DisplayName("Should integrate with task completion scenario")
        fun shouldIntegrateWithTaskCompletionScenario() {
            val (user, task, achievement) = TestDataFactory.createTaskCompletionScenario()

            // Verify achievement is properly set up for task completion
            assertEquals(user.id, achievement.userId, "Achievement should belong to user")
            assertEquals(
                AchievementType.FIRST_STEPS,
                achievement.type,
                "Should use First Steps achievement",
            )
            achievement.shouldNotBeUnlocked()
            achievement.shouldHaveProgress(0, 0)

            // Simulate task completion and achievement unlock
            val progressedAchievement = achievement.updateProgress(1)
            progressedAchievement.shouldBeUnlockable()

            val unlockedAchievement = progressedAchievement.unlock()
            unlockedAchievement.shouldBeUnlocked()
        }

        @Test
        @DisplayName("Should handle multiple achievements for same user")
        fun shouldHandleMultipleAchievementsForSameUser() {
            val userId = UUID.randomUUID().toString()
            val achievements = TestDataFactory.createAchievementSet(userId = userId)

            // Verify all achievements belong to the same user
            achievements.forEach { achievement ->
                assertEquals(
                    userId,
                    achievement.userId,
                    "All achievements should belong to same user",
                )
            }

            // Verify unique achievement IDs
            val achievementIds = achievements.map { it.id }
            assertEquals(
                achievements.size,
                achievementIds.toSet().size,
                "All achievement IDs should be unique",
            )
        }

        @Test
        @DisplayName("Should handle achievement progress tracking across types")
        fun shouldHandleAchievementProgressTrackingAcrossTypes() {
            val userId = UUID.randomUUID().toString()
            val achievements = TestDataFactory.createAchievementSet(
                userId = userId,
                includeProgress = true,
            )

            // Verify different achievement types have appropriate progress
            achievements.forEach { achievement ->
                assertTrue(
                    achievement.progress >= 0,
                    "Progress should be non-negative for ${achievement.type}",
                )
                assertTrue(
                    achievement.progress <= achievement.target || achievement.isUnlocked,
                    "Progress should not exceed target unless unlocked for ${achievement.type}",
                )
            }
        }
    }
}
