package com.lemonqwest.app.infrastructure.achievement

import com.lemonqwest.app.domain.achievement.AchievementType
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for InMemoryAchievementDataSource user achievement initialization.
 *
 * Tests cover:
 * - New user achievement initialization
 * - Existing user reinitialization
 * - Multiple user initialization
 * - Unique ID assignment during initialization
 *
 * CATEGORY A TEST: Infrastructure/data layer test using standalone pattern.
 * No ViewModelTestBase needed as this tests data layer, not presentation ViewModels.
 */

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("InMemoryAchievementDataSource - Initialization Tests")
class InMemoryAchievementDataSourceInitializationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var dataSource: InMemoryAchievementDataSource

    companion object {
        private const val CHILD_USER_ID = "child-1"
    }

    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = InMemoryAchievementDataSource()
    }

    @Test
    @DisplayName("Should initialize achievements for new user")
    fun shouldInitializeAchievementsForNewUser() = runTest {
        setUp()
        // Given
        val newUserId = "new-user-456"

        // When
        dataSource.initializeAchievementsForUser(newUserId)

        // Then
        val userAchievements = dataSource.findByUserId(newUserId)
        assertEquals(
            AchievementType.entries.size,
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
        setUp()
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
            AchievementType.entries.size,
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
        setUp()
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
                AchievementType.entries.size,
                userAchievements.size,
                "User $userId should have all achievement types",
            )

            userAchievements.forEach { achievement ->
                assertEquals(userId, achievement.userId, "Should belong to correct user")
            }
        }

        // Verify total count
        val allAchievements = userIds.flatMap { dataSource.findByUserId(it) }
        val expectedTotal = userIds.size * AchievementType.entries.size
        assertEquals(
            expectedTotal,
            allAchievements.size,
            "Should have correct total achievement count",
        )
    }

    @Test
    @DisplayName("Should assign unique IDs during initialization")
    fun shouldAssignUniqueIdsDuringInitialization() = runTest {
        setUp()
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
