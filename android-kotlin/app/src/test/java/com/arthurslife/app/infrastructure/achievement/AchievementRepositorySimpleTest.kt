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
 */
@DisplayName("AchievementRepositoryImpl Tests")
class AchievementRepositorySimpleTest {

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
        @DisplayName("Should find achievements by user ID and category")
        fun shouldFindAchievementsByUserIdAndCategory() = runTest {
            // Given
            val userId = "user-789"
            val category = AchievementCategory.MILESTONE
            val categoryAchievements = listOf(
                TestDataFactory.createAchievement(AchievementType.FIRST_STEPS, userId),
                TestDataFactory.createAchievement(AchievementType.CENTURY_CLUB, userId),
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
            val expectedAchievement = TestDataFactory.createAchievement(type, userId)
            coEvery { achievementDataSource.findByUserIdAndType(userId, type) } returns expectedAchievement

            // When
            val result = achievementRepository.findByUserIdAndType(userId, type)

            // Then
            assertEquals(expectedAchievement, result, "Should return achievement of specific type")
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
                type = AchievementType.FIRST_STEPS,
                userId = "user-111",
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
                type = AchievementType.THREE_DAY_STREAK,
                userId = "user-222",
                progress = 2,
            )
            coEvery { achievementDataSource.saveAchievement(achievement) } returns Unit

            // When
            achievementRepository.updateAchievement(achievement)

            // Then
            coVerify(exactly = 1) { achievementDataSource.saveAchievement(achievement) }
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
    }
}
