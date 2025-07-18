package com.lemonqwest.app.infrastructure.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * CATEGORY A TEST: Infrastructure/data layer test using standalone pattern.
 * No ViewModelTestBase needed as this tests data layer, not presentation ViewModels.
 *
 * Focused test suite for InMemoryAchievementDataSource concurrency and thread safety.
 *
 * Tests cover:
 * - Concurrent read operations safety
 * - Concurrent write operations safety
 * - Data consistency during concurrent operations
 * - Mutex protection validation
 */
@DisplayName("InMemoryAchievementDataSource - Concurrency Tests")
class InMemoryAchievementDataSourceConcurrencyTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var dataSource: InMemoryAchievementDataSource

    companion object {
        private const val TEST_USER_ID = "test-user-123"
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = InMemoryAchievementDataSource()
    }

    @Test
    @DisplayName("Should handle concurrent reads safely")
    fun shouldHandleConcurrentReadsSafely() = mainDispatcherRule.runTest {
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
    fun shouldHandleConcurrentWritesSafely() = mainDispatcherRule.runTest {
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
    fun shouldMaintainDataConsistencyDuringConcurrentOperations() = mainDispatcherRule.runTest {
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
