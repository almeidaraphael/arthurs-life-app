package com.arthurslife.app.infrastructure.achievement

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.achievement.AchievementCategory
import com.arthurslife.app.domain.achievement.AchievementDataSource
import com.arthurslife.app.domain.achievement.AchievementType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Comprehensive test suite for AchievementRepositoryImpl.
 *
 * This test suite validates the repository implementation following the Repository pattern,
 * ensuring proper delegation to the data source and correct behavior for all achievement
 * operations.
 *
 * Coverage includes:
 * - Achievement retrieval by ID and user
 * - Achievement filtering by category and type
 * - Achievement saving and updating
 * - Achievement initialization for users
 * - Achievement counting operations
 * - Error handling and edge cases
 */
@DisplayName("AchievementRepositoryImpl Tests")
class AchievementRepositoryImplTest {

    private lateinit var achievementDataSource: AchievementDataSource
    private lateinit var achievementRepository: AchievementRepositoryImpl

    @BeforeEach
    fun setUp() {
        achievementDataSource = mockk()
        achievementRepository = AchievementRepositoryImpl(achievementDataSource)
    }

    @Nested
    @DisplayName("Achievement Retrieval")
    inner class AchievementRetrieval {

        @Test
        @DisplayName("Should find achievement by ID")
        fun shouldFindAchievementById() = runTest {
            // Given
            val achievementId = "achievement-123"
            val expectedAchievement = TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                userId = "user-1",
            )
            coEvery { achievementDataSource.findById(achievementId) } returns expectedAchievement

            // When
            val result = achievementRepository.findById(achievementId)

            // Then
            assertEquals(expectedAchievement, result, "Should return achievement from data source")
            coVerify(exactly = 1) { achievementDataSource.findById(achievementId) }
        }

        @Test
        @DisplayName("Should return null when achievement not found by ID")
        fun shouldReturnNullWhenAchievementNotFoundById() = runTest {
            // Given
            val achievementId = "nonexistent-achievement"
            coEvery { achievementDataSource.findById(achievementId) } returns null

            // When
            val result = achievementRepository.findById(achievementId)

            // Then
            assertNull(result, "Should return null when achievement not found")
            coVerify(exactly = 1) { achievementDataSource.findById(achievementId) }
        }

        @Test
        @DisplayName("Should find achievements by user ID")
        fun shouldFindAchievementsByUserId() = runTest {
            // Given
            val userId = "user-123"
            val expectedAchievements = listOf(
                TestDataFactory.createAchievement(AchievementType.FIRST_STEPS, userId),
                TestDataFactory.createAchievement(AchievementType.THREE_DAY_STREAK, userId),
                TestDataFactory.createAchievement(AchievementType.TOKEN_COLLECTOR, userId),
            )
            coEvery { achievementDataSource.findByUserId(userId) } returns expectedAchievements

            // When
            val result = achievementRepository.findByUserId(userId)

            // Then
            assertEquals(expectedAchievements, result, "Should return all user achievements")
            assertEquals(3, result.size, "Should return correct number of achievements")
            coVerify(exactly = 1) { achievementDataSource.findByUserId(userId) }
        }

        @Test
        @DisplayName("Should find unlocked achievements by user ID")
        fun shouldFindUnlockedAchievementsByUserId() = runTest {
            // Given
            val userId = "user-456"
            val unlockedAchievements = listOf(
                TestDataFactory.createAchievement(AchievementType.FIRST_STEPS, userId).unlock(),
                TestDataFactory.createAchievement(
                    AchievementType.THREE_DAY_STREAK,
                    userId,
                ).unlock(),
            )
            coEvery { achievementDataSource.findUnlockedByUserId(userId) } returns unlockedAchievements

            // When
            val result = achievementRepository.findUnlockedByUserId(userId)

            // Then
            assertEquals(unlockedAchievements, result, "Should return unlocked achievements")
            assertEquals(2, result.size, "Should return correct number of unlocked achievements")
            coVerify(exactly = 1) { achievementDataSource.findUnlockedByUserId(userId) }
        }

        @Test
        @DisplayName("Should find achievements by user ID and category")
        fun shouldFindAchievementsByUserIdAndCategory() = runTest {
            // Given
            val userId = "user-789"
            val category = AchievementCategory.MILESTONE
            val categoryAchievements = listOf(
                TestDataFactory.createAchievement(AchievementType.FIRST_STEPS, userId),
                TestDataFactory.createAchievement(AchievementType.THREE_DAY_STREAK, userId),
            )
            coEvery {
                achievementDataSource.findByUserIdAndCategory(userId, category)
            } returns categoryAchievements

            // When
            val result = achievementRepository.findByUserIdAndCategory(userId, category)

            // Then
            assertEquals(categoryAchievements, result, "Should return category achievements")
            assertEquals(2, result.size, "Should return correct number of category achievements")
            coVerify(
                exactly = 1,
            ) { achievementDataSource.findByUserIdAndCategory(userId, category) }
        }

        @Test
        @DisplayName("Should find achievement by user ID and type")
        fun shouldFindAchievementByUserIdAndType() = runTest {
            // Given
            val userId = "user-321"
            val type = AchievementType.FIRST_STEPS
            val expectedAchievement = TestDataFactory.createAchievement(
                AchievementType.FIRST_STEPS,
                userId,
            )
            coEvery { achievementDataSource.findByUserIdAndType(userId, type) } returns expectedAchievement

            // When
            val result = achievementRepository.findByUserIdAndType(userId, type)

            // Then
            assertEquals(expectedAchievement, result, "Should return achievement of specific type")
            coVerify(exactly = 1) { achievementDataSource.findByUserIdAndType(userId, type) }
        }

        @Test
        @DisplayName("Should return null when achievement type not found for user")
        fun shouldReturnNullWhenAchievementTypeNotFoundForUser() = runTest {
            // Given
            val userId = "user-654"
            val type = AchievementType.THREE_DAY_STREAK
            coEvery { achievementDataSource.findByUserIdAndType(userId, type) } returns null

            // When
            val result = achievementRepository.findByUserIdAndType(userId, type)

            // Then
            assertNull(result, "Should return null when achievement type not found")
            coVerify(exactly = 1) { achievementDataSource.findByUserIdAndType(userId, type) }
        }
    }

    @Nested
    @DisplayName("Achievement Persistence")
    inner class AchievementPersistence {

        @Test
        @DisplayName("Should save new achievement")
        fun shouldSaveNewAchievement() = runTest {
            // Given
            val achievement = TestDataFactory.createAchievement(
                AchievementType.FIRST_STEPS,
                "user-111",
            )
            coEvery { achievementDataSource.saveAchievement(achievement) } returns Unit

            // When
            achievementRepository.saveAchievement(achievement)

            // Then
            coVerify(exactly = 1) { achievementDataSource.saveAchievement(achievement) }
        }

        @Test
        @DisplayName("Should update existing achievement")
        fun shouldUpdateExistingAchievement() = runTest {
            // Given
            val achievement = TestDataFactory.createAchievement(
                AchievementType.FIRST_STEPS,
                "user-222",
            )
                .updateProgress(5)
            coEvery { achievementDataSource.saveAchievement(achievement) } returns Unit

            // When
            achievementRepository.updateAchievement(achievement)

            // Then
            coVerify(exactly = 1) { achievementDataSource.saveAchievement(achievement) }
        }

        @Test
        @DisplayName("Should handle multiple achievement saves")
        fun shouldHandleMultipleAchievementSaves() = runTest {
            // Given
            val achievements = listOf(
                TestDataFactory.createAchievement(AchievementType.FIRST_STEPS, "user-333"),
                TestDataFactory.createAchievement(AchievementType.THREE_DAY_STREAK, "user-333"),
                TestDataFactory.createAchievement(AchievementType.TOKEN_COLLECTOR, "user-333"),
            )
            achievements.forEach { achievement ->
                coEvery { achievementDataSource.saveAchievement(achievement) } returns Unit
            }

            // When
            achievements.forEach { achievement ->
                achievementRepository.saveAchievement(achievement)
            }

            // Then
            achievements.forEach { achievement ->
                coVerify(exactly = 1) { achievementDataSource.saveAchievement(achievement) }
            }
        }

        @Test
        @DisplayName("Should handle achievement progress updates")
        fun shouldHandleAchievementProgressUpdates() = runTest {
            // Given
            val baseAchievement = TestDataFactory.createAchievement(
                AchievementType.THREE_DAY_STREAK,
                "user-444",
            )
            val updatedAchievement = baseAchievement.updateProgress(3)
            coEvery { achievementDataSource.saveAchievement(updatedAchievement) } returns Unit

            // When
            achievementRepository.updateAchievement(updatedAchievement)

            // Then
            coVerify(exactly = 1) { achievementDataSource.saveAchievement(updatedAchievement) }
        }

        @Test
        @DisplayName("Should handle achievement unlocking")
        fun shouldHandleAchievementUnlocking() = runTest {
            // Given
            val baseAchievement = TestDataFactory.createAchievement(
                AchievementType.FIRST_STEPS,
                "user-555",
            )
            val unlockedAchievement = baseAchievement.unlock()
            coEvery { achievementDataSource.saveAchievement(unlockedAchievement) } returns Unit

            // When
            achievementRepository.updateAchievement(unlockedAchievement)

            // Then
            coVerify(exactly = 1) { achievementDataSource.saveAchievement(unlockedAchievement) }
        }
    }

    @Nested
    @DisplayName("Achievement Initialization")
    inner class AchievementInitialization {

        @Test
        @DisplayName("Should initialize achievements for new user")
        fun shouldInitializeAchievementsForNewUser() = runTest {
            // Given
            val userId = "new-user-123"
            coEvery { achievementDataSource.initializeAchievementsForUser(userId) } returns Unit

            // When
            achievementRepository.initializeAchievementsForUser(userId)

            // Then
            coVerify(exactly = 1) { achievementDataSource.initializeAchievementsForUser(userId) }
        }

        @Test
        @DisplayName("Should handle multiple user initializations")
        fun shouldHandleMultipleUserInitializations() = runTest {
            // Given
            val userIds = listOf("user-001", "user-002", "user-003")
            userIds.forEach { userId ->
                coEvery { achievementDataSource.initializeAchievementsForUser(userId) } returns Unit
            }

            // When
            userIds.forEach { userId ->
                achievementRepository.initializeAchievementsForUser(userId)
            }

            // Then
            userIds.forEach { userId ->
                coVerify(
                    exactly = 1,
                ) { achievementDataSource.initializeAchievementsForUser(userId) }
            }
        }

        @Test
        @DisplayName("Should handle reinitialization for existing user")
        fun shouldHandleReinitializationForExistingUser() = runTest {
            // Given
            val userId = "existing-user-456"
            coEvery { achievementDataSource.initializeAchievementsForUser(userId) } returns Unit

            // When
            achievementRepository.initializeAchievementsForUser(userId)

            // Then
            coVerify(exactly = 1) { achievementDataSource.initializeAchievementsForUser(userId) }
        }
    }

    @Nested
    @DisplayName("Achievement Counting")
    inner class AchievementCounting {

        @Test
        @DisplayName("Should count unlocked achievements for user")
        fun shouldCountUnlockedAchievementsForUser() = runTest {
            // Given
            val userId = "user-777"
            val expectedCount = 3
            coEvery { achievementDataSource.countUnlockedAchievements(userId) } returns expectedCount

            // When
            val result = achievementRepository.countUnlockedAchievements(userId)

            // Then
            assertEquals(
                expectedCount,
                result,
                "Should return correct count of unlocked achievements",
            )
            coVerify(exactly = 1) { achievementDataSource.countUnlockedAchievements(userId) }
        }

        @Test
        @DisplayName("Should return zero when user has no unlocked achievements")
        fun shouldReturnZeroWhenUserHasNoUnlockedAchievements() = runTest {
            // Given
            val userId = "new-user-888"
            coEvery { achievementDataSource.countUnlockedAchievements(userId) } returns 0

            // When
            val result = achievementRepository.countUnlockedAchievements(userId)

            // Then
            assertEquals(0, result, "Should return zero for user with no unlocked achievements")
            coVerify(exactly = 1) { achievementDataSource.countUnlockedAchievements(userId) }
        }

        @Test
        @DisplayName("Should handle varying unlocked achievement counts")
        fun shouldHandleVaryingUnlockedAchievementCounts() = runTest {
            // Given
            val testCases = mapOf(
                "user-a" to 0,
                "user-b" to 1,
                "user-c" to 5,
                "user-d" to 12,
            )
            testCases.forEach { (userId, count) ->
                coEvery { achievementDataSource.countUnlockedAchievements(userId) } returns count
            }

            // When/Then
            testCases.forEach { (userId, expectedCount) ->
                val result = achievementRepository.countUnlockedAchievements(userId)
                assertEquals(expectedCount, result, "Should return correct count for $userId")
                coVerify(exactly = 1) { achievementDataSource.countUnlockedAchievements(userId) }
            }
        }
    }

    @Nested
    @DisplayName("Repository Delegation Pattern")
    inner class RepositoryDelegationPattern {

        @Test
        @DisplayName("Should delegate all operations to data source")
        fun shouldDelegateAllOperationsToDataSource() = runTest {
            // Given
            val userId = "test-user"
            val achievementId = "test-achievement"
            val achievement = TestDataFactory.createAchievement(AchievementType.FIRST_STEPS, userId)
            val type = AchievementType.FIRST_STEPS
            val category = AchievementCategory.MILESTONE

            // Setup all mocks
            coEvery { achievementDataSource.findById(achievementId) } returns achievement
            coEvery { achievementDataSource.findByUserId(userId) } returns listOf(achievement)
            coEvery { achievementDataSource.findUnlockedByUserId(userId) } returns listOf(achievement)
            coEvery { achievementDataSource.findByUserIdAndCategory(userId, category) } returns listOf(achievement)
            coEvery { achievementDataSource.findByUserIdAndType(userId, type) } returns achievement
            coEvery { achievementDataSource.saveAchievement(achievement) } returns Unit
            coEvery { achievementDataSource.initializeAchievementsForUser(userId) } returns Unit
            coEvery { achievementDataSource.countUnlockedAchievements(userId) } returns 1

            // When - call all repository methods
            achievementRepository.findById(achievementId)
            achievementRepository.findByUserId(userId)
            achievementRepository.findUnlockedByUserId(userId)
            achievementRepository.findByUserIdAndCategory(userId, category)
            achievementRepository.findByUserIdAndType(userId, type)
            achievementRepository.saveAchievement(achievement)
            achievementRepository.updateAchievement(achievement)
            achievementRepository.initializeAchievementsForUser(userId)
            achievementRepository.countUnlockedAchievements(userId)

            // Then - verify all delegations
            coVerify(exactly = 1) { achievementDataSource.findById(achievementId) }
            coVerify(exactly = 1) { achievementDataSource.findByUserId(userId) }
            coVerify(exactly = 1) { achievementDataSource.findUnlockedByUserId(userId) }
            coVerify(
                exactly = 1,
            ) { achievementDataSource.findByUserIdAndCategory(userId, category) }
            coVerify(exactly = 1) { achievementDataSource.findByUserIdAndType(userId, type) }
            coVerify(
                exactly = 2,
            ) { achievementDataSource.saveAchievement(achievement) } // save + update
            coVerify(exactly = 1) { achievementDataSource.initializeAchievementsForUser(userId) }
            coVerify(exactly = 1) { achievementDataSource.countUnlockedAchievements(userId) }
        }

        @Test
        @DisplayName("Should maintain consistency between save and update operations")
        fun shouldMaintainConsistencyBetweenSaveAndUpdateOperations() = runTest {
            // Given
            val achievement = TestDataFactory.createAchievement(
                AchievementType.THREE_DAY_STREAK,
                "user-consistency",
            )
            coEvery { achievementDataSource.saveAchievement(achievement) } returns Unit

            // When
            achievementRepository.saveAchievement(achievement)
            achievementRepository.updateAchievement(achievement)

            // Then
            // Both should delegate to the same data source method
            coVerify(exactly = 2) { achievementDataSource.saveAchievement(achievement) }
        }

        @Test
        @DisplayName("Should handle null returns from data source gracefully")
        fun shouldHandleNullReturnsFromDataSourceGracefully() = runTest {
            // Given
            val userId = "missing-user"
            val achievementId = "missing-achievement"
            val type = AchievementType.TOKEN_COLLECTOR
            coEvery { achievementDataSource.findById(achievementId) } returns null
            coEvery { achievementDataSource.findByUserIdAndType(userId, type) } returns null

            // When
            val resultById = achievementRepository.findById(achievementId)
            val resultByType = achievementRepository.findByUserIdAndType(userId, type)

            // Then
            assertNull(resultById, "Should handle null from findById gracefully")
            assertNull(resultByType, "Should handle null from findByUserIdAndType gracefully")
            coVerify(exactly = 1) { achievementDataSource.findById(achievementId) }
            coVerify(exactly = 1) { achievementDataSource.findByUserIdAndType(userId, type) }
        }
    }
}
