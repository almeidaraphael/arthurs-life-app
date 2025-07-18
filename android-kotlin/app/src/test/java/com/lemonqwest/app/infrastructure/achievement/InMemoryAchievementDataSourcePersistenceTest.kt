package com.lemonqwest.app.infrastructure.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * Focused test suite for InMemoryAchievementDataSource achievement persistence operations.
 *
 * Tests cover:
 * - Saving new achievements
 * - Updating existing achievements
 * - Achievement unlocking handling
 * - Order maintenance during updates
 * - Multiple achievement saves
 */

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("InMemoryAchievementDataSource - Persistence Tests")
class InMemoryAchievementDataSourcePersistenceTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var dataSource: InMemoryAchievementDataSource

    companion object {
        private const val CHILD_USER_ID = "child-1"
        private const val TEST_USER_ID = "test-user-123"
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = InMemoryAchievementDataSource()
    }

    @Test
    @DisplayName("Should save new achievement")
    fun shouldSaveNewAchievement() = mainDispatcherRule.runTest {
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
    fun shouldUpdateExistingAchievement() = mainDispatcherRule.runTest {
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
    fun shouldHandleAchievementUnlocking() = mainDispatcherRule.runTest {
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
    fun shouldMaintainAchievementOrderWhenUpdating() = mainDispatcherRule.runTest {
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
    fun shouldHandleMultipleAchievementSaves() = mainDispatcherRule.runTest {
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
