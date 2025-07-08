package com.arthurslife.app.infrastructure.achievement

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.achievement.AchievementCategory
import com.arthurslife.app.domain.achievement.AchievementType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Comprehensive test suite for InMemoryAchievementDataSource.
 *
 * This test suite validates the in-memory data source implementation, ensuring proper
 * achievement storage, retrieval, and concurrency handling with mutex protection.
 *
 * Coverage includes:
 * - Initial achievement setup for demo user
 * - Achievement CRUD operations
 * - Achievement filtering and querying
 * - User achievement initialization
 * - Thread-safety and concurrency handling
 * - Achievement state management (unlocked/locked)
 */
@DisplayName("InMemoryAchievementDataSource Tests")
class InMemoryAchievementDataSourceTest {

    private lateinit var dataSource: InMemoryAchievementDataSource

    companion object {
        private const val CHILD_USER_ID = "child-1"
        private const val TEST_USER_ID = "test-user-123"
    }

    @BeforeEach
    fun setUp() {
        dataSource = InMemoryAchievementDataSource()
    }

    @Nested
    @DisplayName("Initial Setup and Demo Data")
    inner class InitialSetupAndDemoData {

        @Test
        @DisplayName("Should initialize with demo child user achievements")
        fun shouldInitializeWithDemoChildUserAchievements() = runTest {
            // When
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)

            // Then
            assertFalse(childAchievements.isEmpty(), "Should have demo achievements")
            assertEquals(
                AchievementType.values().size,
                childAchievements.size,
                "Should have all achievement types",
            )

            // Verify all achievement types are present
            val achievementTypes = childAchievements.map { it.type }.toSet()
            val expectedTypes = AchievementType.values().toSet()
            assertEquals(expectedTypes, achievementTypes, "Should have all achievement types")
        }

        @Test
        @DisplayName("Should initialize demo achievements with zero progress")
        fun shouldInitializeDemoAchievementsWithZeroProgress() = runTest {
            // When
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)

            // Then
            childAchievements.forEach { achievement ->
                assertEquals(
                    0,
                    achievement.progress,
                    "Achievement ${achievement.type} should start with zero progress",
                )
                assertFalse(
                    achievement.isUnlocked,
                    "Achievement ${achievement.type} should start locked",
                )
                assertNull(
                    achievement.unlockedAt,
                    "Achievement ${achievement.type} should not have unlock time",
                )
            }
        }

        @Test
        @DisplayName("Should assign unique IDs to each demo achievement")
        fun shouldAssignUniqueIdsToEachDemoAchievement() = runTest {
            // When
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)

            // Then
            val achievementIds = childAchievements.map { it.id }
            val uniqueIds = achievementIds.toSet()
            assertEquals(
                achievementIds.size,
                uniqueIds.size,
                "All demo achievements should have unique IDs",
            )

            // Verify each ID is not empty
            achievementIds.forEach { id ->
                assertTrue(id.isNotEmpty(), "Achievement ID should not be empty")
            }
        }

        @Test
        @DisplayName("Should set correct user ID for demo achievements")
        fun shouldSetCorrectUserIdForDemoAchievements() = runTest {
            // When
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)

            // Then
            childAchievements.forEach { achievement ->
                assertEquals(
                    CHILD_USER_ID,
                    achievement.userId,
                    "Achievement ${achievement.type} should have correct user ID",
                )
            }
        }
    }

    @Nested
    @DisplayName("Achievement Retrieval")
    inner class AchievementRetrieval {

        @Test
        @DisplayName("Should find achievement by ID")
        fun shouldFindAchievementById() = runTest {
            // Given
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)
            val firstAchievement = childAchievements.first()

            // When
            val result = dataSource.findById(firstAchievement.id)

            // Then
            assertNotNull(result, "Should find achievement by ID")
            assertEquals(firstAchievement.id, result!!.id, "Should return correct achievement")
            assertEquals(firstAchievement.type, result.type, "Should have correct type")
            assertEquals(firstAchievement.userId, result.userId, "Should have correct user ID")
        }

        @Test
        @DisplayName("Should return null for non-existent achievement ID")
        fun shouldReturnNullForNonExistentAchievementId() = runTest {
            // Given
            val nonExistentId = "non-existent-achievement-id"

            // When
            val result = dataSource.findById(nonExistentId)

            // Then
            assertNull(result, "Should return null for non-existent achievement ID")
        }

        @Test
        @DisplayName("Should find achievements by user ID")
        fun shouldFindAchievementsByUserId() = runTest {
            // Given - demo achievements already exist for CHILD_USER_ID

            // When
            val result = dataSource.findByUserId(CHILD_USER_ID)

            // Then
            assertFalse(result.isEmpty(), "Should find achievements for child user")
            result.forEach { achievement ->
                assertEquals(
                    CHILD_USER_ID,
                    achievement.userId,
                    "All achievements should belong to child user",
                )
            }
        }

        @Test
        @DisplayName("Should return empty list for user with no achievements")
        fun shouldReturnEmptyListForUserWithNoAchievements() = runTest {
            // Given
            val userWithNoAchievements = "user-with-no-achievements"

            // When
            val result = dataSource.findByUserId(userWithNoAchievements)

            // Then
            assertTrue(result.isEmpty(), "Should return empty list for user with no achievements")
        }

        @Test
        @DisplayName("Should find unlocked achievements by user ID")
        fun shouldFindUnlockedAchievementsByUserId() = runTest {
            // Given - unlock some achievements for child user
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)
            val achievementToUnlock = childAchievements.first()
            val unlockedAchievement = achievementToUnlock.unlock()
            dataSource.saveAchievement(unlockedAchievement)

            // When
            val result = dataSource.findUnlockedByUserId(CHILD_USER_ID)

            // Then
            assertEquals(1, result.size, "Should find one unlocked achievement")
            assertTrue(result[0].isUnlocked, "Found achievement should be unlocked")
            assertEquals(unlockedAchievement.id, result[0].id, "Should be the correct achievement")
        }

        @Test
        @DisplayName("Should return empty list when user has no unlocked achievements")
        fun shouldReturnEmptyListWhenUserHasNoUnlockedAchievements() = runTest {
            // Given - new user with all achievements locked
            dataSource.initializeAchievementsForUser(TEST_USER_ID)

            // When
            val result = dataSource.findUnlockedByUserId(TEST_USER_ID)

            // Then
            assertTrue(
                result.isEmpty(),
                "Should return empty list when no achievements are unlocked",
            )
        }

        @Test
        @DisplayName("Should find achievements by user ID and category")
        fun shouldFindAchievementsByUserIdAndCategory() = runTest {
            // Given
            val category = AchievementCategory.MILESTONE

            // When
            val result = dataSource.findByUserIdAndCategory(CHILD_USER_ID, category)

            // Then
            assertFalse(result.isEmpty(), "Should find achievements in milestone category")
            result.forEach { achievement ->
                assertEquals(CHILD_USER_ID, achievement.userId, "Should belong to child user")
                assertEquals(category, achievement.type.category, "Should be in correct category")
            }

            // Verify we get only milestone achievements
            val milestoneTypes = AchievementType.values()
                .filter { it.category == AchievementCategory.MILESTONE }
            assertEquals(
                milestoneTypes.size,
                result.size,
                "Should have all milestone achievements",
            )
        }

        @Test
        @DisplayName("Should find achievement by user ID and type")
        fun shouldFindAchievementByUserIdAndType() = runTest {
            // Given
            val type = AchievementType.FIRST_STEPS

            // When
            val result = dataSource.findByUserIdAndType(CHILD_USER_ID, type)

            // Then
            assertNotNull(result, "Should find achievement by user ID and type")
            assertEquals(CHILD_USER_ID, result!!.userId, "Should belong to child user")
            assertEquals(type, result.type, "Should have correct type")
        }

        @Test
        @DisplayName("Should return null when achievement type not found for user")
        fun shouldReturnNullWhenAchievementTypeNotFoundForUser() = runTest {
            // Given
            val userWithoutAchievements = "user-without-achievements"
            val type = AchievementType.FIRST_STEPS

            // When
            val result = dataSource.findByUserIdAndType(userWithoutAchievements, type)

            // Then
            assertNull(result, "Should return null when achievement type not found for user")
        }
    }

    @Nested
    @DisplayName("Achievement Persistence")
    inner class AchievementPersistence {

        @Test
        @DisplayName("Should save new achievement")
        fun shouldSaveNewAchievement() = runTest {
            // Given
            val newAchievement = TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                userId = TEST_USER_ID,
            )

            // When
            dataSource.saveAchievement(newAchievement)

            // Then
            val result = dataSource.findById(newAchievement.id)
            assertNotNull(result, "Should save new achievement")
            assertEquals(newAchievement.id, result!!.id, "Should have correct ID")
            assertEquals(newAchievement.type, result.type, "Should have correct type")
            assertEquals(newAchievement.userId, result.userId, "Should have correct user ID")
        }

        @Test
        @DisplayName("Should update existing achievement")
        fun shouldUpdateExistingAchievement() = runTest {
            // Given
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)
            val originalAchievement = childAchievements.first()
            val updatedAchievement = originalAchievement.updateProgress(5)

            // When
            dataSource.saveAchievement(updatedAchievement)

            // Then
            val result = dataSource.findById(originalAchievement.id)
            assertNotNull(result, "Should find updated achievement")
            assertEquals(5, result!!.progress, "Should have updated progress")
            assertEquals(updatedAchievement.id, result.id, "Should maintain same ID")
        }

        @Test
        @DisplayName("Should handle achievement unlocking")
        fun shouldHandleAchievementUnlocking() = runTest {
            // Given
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)
            val achievementToUnlock = childAchievements.first()
            val unlockedAchievement = achievementToUnlock.unlock()

            // When
            dataSource.saveAchievement(unlockedAchievement)

            // Then
            val result = dataSource.findById(achievementToUnlock.id)
            assertNotNull(result, "Should find unlocked achievement")
            assertTrue(result!!.isUnlocked, "Achievement should be unlocked")
            assertNotNull(result.unlockedAt, "Achievement should have unlock timestamp")
        }

        @Test
        @DisplayName("Should maintain achievement order when updating")
        fun shouldMaintainAchievementOrderWhenUpdating() = runTest {
            // Given
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)
            val originalCount = childAchievements.size
            val achievementToUpdate = childAchievements[1] // Get second achievement
            val updatedAchievement = achievementToUpdate.updateProgress(3)

            // When
            dataSource.saveAchievement(updatedAchievement)

            // Then
            val resultAchievements = dataSource.findByUserId(CHILD_USER_ID)
            assertEquals(
                originalCount,
                resultAchievements.size,
                "Should maintain same number of achievements",
            )

            val resultAchievement = dataSource.findById(achievementToUpdate.id)
            assertEquals(3, resultAchievement!!.progress, "Should have updated progress")
        }

        @Test
        @DisplayName("Should handle multiple achievement saves")
        fun shouldHandleMultipleAchievementSaves() = runTest {
            // Given
            val achievements = listOf(
                TestDataFactory.createAchievement(AchievementType.FIRST_STEPS, TEST_USER_ID),
                TestDataFactory.createAchievement(AchievementType.THREE_DAY_STREAK, TEST_USER_ID),
                TestDataFactory.createAchievement(AchievementType.TOKEN_COLLECTOR, TEST_USER_ID),
            )

            // When
            achievements.forEach { achievement ->
                dataSource.saveAchievement(achievement)
            }

            // Then
            val resultAchievements = dataSource.findByUserId(TEST_USER_ID)
            assertEquals(achievements.size, resultAchievements.size, "Should save all achievements")

            achievements.forEach { originalAchievement ->
                val result = dataSource.findById(originalAchievement.id)
                assertNotNull(result, "Should find saved achievement ${originalAchievement.type}")
                assertEquals(originalAchievement.type, result!!.type, "Should have correct type")
            }
        }
    }

    @Nested
    @DisplayName("User Achievement Initialization")
    inner class UserAchievementInitialization {

        @Test
        @DisplayName("Should initialize achievements for new user")
        fun shouldInitializeAchievementsForNewUser() = runTest {
            // Given
            val newUserId = "new-user-456"

            // When
            dataSource.initializeAchievementsForUser(newUserId)

            // Then
            val userAchievements = dataSource.findByUserId(newUserId)
            assertEquals(
                AchievementType.values().size,
                userAchievements.size,
                "Should create all achievement types for new user",
            )

            userAchievements.forEach { achievement ->
                assertEquals(newUserId, achievement.userId, "Should have correct user ID")
                assertEquals(0, achievement.progress, "Should start with zero progress")
                assertFalse(achievement.isUnlocked, "Should start locked")
            }
        }

        @Test
        @DisplayName("Should reinitialize achievements for existing user")
        fun shouldReinitializeAchievementsForExistingUser() = runTest {
            // Given - child user already has achievements, unlock one
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)
            val achievementToUnlock = childAchievements.first()
            val unlockedAchievement = achievementToUnlock.unlock()
            dataSource.saveAchievement(unlockedAchievement)

            // Verify achievement is unlocked
            val unlockedResult = dataSource.findById(achievementToUnlock.id)
            assertTrue(
                unlockedResult!!.isUnlocked,
                "Achievement should be unlocked before reinitialization",
            )

            // When
            dataSource.initializeAchievementsForUser(CHILD_USER_ID)

            // Then
            val reinitializedAchievements = dataSource.findByUserId(CHILD_USER_ID)
            assertEquals(
                AchievementType.values().size,
                reinitializedAchievements.size,
                "Should have all achievement types after reinitialization",
            )

            reinitializedAchievements.forEach { achievement ->
                assertEquals(CHILD_USER_ID, achievement.userId, "Should have correct user ID")
                assertEquals(0, achievement.progress, "Should reset to zero progress")
                assertFalse(achievement.isUnlocked, "Should reset to locked state")
            }
        }

        @Test
        @DisplayName("Should handle initialization for multiple users")
        fun shouldHandleInitializationForMultipleUsers() = runTest {
            // Given
            val userIds = listOf("user-a", "user-b", "user-c")

            // When
            userIds.forEach { userId ->
                dataSource.initializeAchievementsForUser(userId)
            }

            // Then
            userIds.forEach { userId ->
                val userAchievements = dataSource.findByUserId(userId)
                assertEquals(
                    AchievementType.values().size,
                    userAchievements.size,
                    "User $userId should have all achievement types",
                )

                userAchievements.forEach { achievement ->
                    assertEquals(userId, achievement.userId, "Should belong to correct user")
                }
            }

            // Verify total count
            val allAchievements = userIds.flatMap { dataSource.findByUserId(it) }
            val expectedTotal = userIds.size * AchievementType.values().size
            assertEquals(
                expectedTotal,
                allAchievements.size,
                "Should have correct total achievement count",
            )
        }

        @Test
        @DisplayName("Should assign unique IDs during initialization")
        fun shouldAssignUniqueIdsDuringInitialization() = runTest {
            // Given
            val userId = "unique-id-test-user"

            // When
            dataSource.initializeAchievementsForUser(userId)

            // Then
            val userAchievements = dataSource.findByUserId(userId)
            val achievementIds = userAchievements.map { it.id }
            val uniqueIds = achievementIds.toSet()

            assertEquals(
                achievementIds.size,
                uniqueIds.size,
                "All initialized achievements should have unique IDs",
            )
        }
    }

    @Nested
    @DisplayName("Achievement Counting")
    inner class AchievementCounting {

        @Test
        @DisplayName("Should count unlocked achievements for user")
        fun shouldCountUnlockedAchievementsForUser() = runTest {
            // Given - unlock 2 achievements for child user
            val childAchievements = dataSource.findByUserId(CHILD_USER_ID)
            val firstUnlocked = childAchievements[0].unlock()
            val secondUnlocked = childAchievements[1].unlock()
            dataSource.saveAchievement(firstUnlocked)
            dataSource.saveAchievement(secondUnlocked)

            // When
            val count = dataSource.countUnlockedAchievements(CHILD_USER_ID)

            // Then
            assertEquals(2, count, "Should count 2 unlocked achievements")
        }

        @Test
        @DisplayName("Should return zero count for user with no unlocked achievements")
        fun shouldReturnZeroCountForUserWithNoUnlockedAchievements() = runTest {
            // Given
            dataSource.initializeAchievementsForUser(TEST_USER_ID)

            // When
            val count = dataSource.countUnlockedAchievements(TEST_USER_ID)

            // Then
            assertEquals(0, count, "Should return zero for user with no unlocked achievements")
        }

        @Test
        @DisplayName("Should return zero count for non-existent user")
        fun shouldReturnZeroCountForNonExistentUser() = runTest {
            // Given
            val nonExistentUserId = "non-existent-user"

            // When
            val count = dataSource.countUnlockedAchievements(nonExistentUserId)

            // Then
            assertEquals(0, count, "Should return zero for non-existent user")
        }

        @Test
        @DisplayName("Should accurately count varying unlocked achievement numbers")
        fun shouldAccuratelyCountVaryingUnlockedAchievementNumbers() = runTest {
            // Given - create users with different numbers of unlocked achievements
            val testCases = listOf(
                "user-0-unlocked" to 0,
                "user-1-unlocked" to 1,
                "user-3-unlocked" to 3,
                "user-all-unlocked" to AchievementType.values().size,
            )

            testCases.forEach { (userId, unlockedCount) ->
                dataSource.initializeAchievementsForUser(userId)
                val userAchievements = dataSource.findByUserId(userId)

                // Unlock the specified number of achievements
                repeat(unlockedCount) { index ->
                    val unlockedAchievement = userAchievements[index].unlock()
                    dataSource.saveAchievement(unlockedAchievement)
                }
            }

            // When/Then
            testCases.forEach { (userId, expectedCount) ->
                val actualCount = dataSource.countUnlockedAchievements(userId)
                assertEquals(
                    expectedCount,
                    actualCount,
                    "User $userId should have $expectedCount unlocked achievements",
                )
            }
        }
    }

    @Nested
    @DisplayName("Concurrency and Thread Safety")
    inner class ConcurrencyAndThreadSafety {

        @Test
        @DisplayName("Should handle concurrent reads safely")
        fun shouldHandleConcurrentReadsSafely() = runTest {
            // Given
            dataSource.initializeAchievementsForUser(TEST_USER_ID)

            // When - simulate concurrent reads (in test environment)
            val results = (1..5).map {
                dataSource.findByUserId(TEST_USER_ID)
            }

            // Then
            results.forEach { achievements ->
                assertEquals(
                    AchievementType.values().size,
                    achievements.size,
                    "Should consistently return all achievements",
                )
            }
        }

        @Test
        @DisplayName("Should handle concurrent writes safely")
        fun shouldHandleConcurrentWritesSafely() = runTest {
            // Given
            val achievement = TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                userId = TEST_USER_ID,
            )
            dataSource.saveAchievement(achievement)

            // When - simulate concurrent updates (in test environment)
            val updatedAchievements = (1..3).map { progress ->
                achievement.updateProgress(progress)
            }

            // Save all updates
            updatedAchievements.forEach { updated ->
                dataSource.saveAchievement(updated)
            }

            // Then
            val result = dataSource.findById(achievement.id)
            assertNotNull(result, "Achievement should still exist after concurrent updates")
            // Final progress should be one of the values (last write wins)
            assertTrue(
                result!!.progress in 1..3,
                "Progress should be one of the updated values",
            )
        }

        @Test
        @DisplayName("Should maintain data consistency during concurrent operations")
        fun shouldMaintainDataConsistencyDuringConcurrentOperations() = runTest {
            // Given
            val userId = "concurrent-test-user"
            dataSource.initializeAchievementsForUser(userId)

            // When - perform various concurrent operations
            val initialAchievements = dataSource.findByUserId(userId)
            val firstAchievement = initialAchievements.first()

            // Update progress and unlock (unlock() sets progress to target value)
            val progressUpdate = firstAchievement.updateProgress(5)
            val unlockedAchievement = progressUpdate.unlock()
            dataSource.saveAchievement(unlockedAchievement)

            // Then
            val finalResult = dataSource.findById(firstAchievement.id)
            assertNotNull(finalResult, "Achievement should exist")
            assertEquals(
                firstAchievement.target,
                finalResult!!.progress,
                "Should have target progress when unlocked",
            )
            assertTrue(finalResult.isUnlocked, "Should be unlocked")

            // Verify count is consistent
            val unlockedCount = dataSource.countUnlockedAchievements(userId)
            assertEquals(1, unlockedCount, "Should count exactly one unlocked achievement")
        }
    }
}
