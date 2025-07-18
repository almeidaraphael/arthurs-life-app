package com.lemonqwest.app.infrastructure.achievement

import com.lemonqwest.app.domain.achievement.AchievementType
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * CATEGORY A TEST: Infrastructure/data layer test using standalone pattern.
 * No ViewModelTestBase needed as this tests data layer, not presentation ViewModels.
 *
 * Focused test suite for InMemoryAchievementDataSource initial setup and demo data.
 *
 * Tests cover:
 * - Demo child user achievement initialization
 * - Zero progress validation
 * - Unique ID assignment
 * - Correct user ID assignment
 */

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("InMemoryAchievementDataSource - Initial Setup Tests")
class InMemoryAchievementDataSourceInitialSetupTest {

    private lateinit var dataSource: InMemoryAchievementDataSource

    companion object {
        private const val CHILD_USER_ID = "child-1"
    }

    @get:org.junit.Rule
    val mainDispatcherRule = com.lemonqwest.app.testutils.MainDispatcherRule(
        UnconfinedTestDispatcher(),
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = InMemoryAchievementDataSource()
    }

    // No manual dispatcher teardown needed

    @Test
    @DisplayName("Should initialize with demo child user achievements")
    fun shouldInitializeWithDemoChildUserAchievements() = runTest {
        // When
        val childAchievements = dataSource.findByUserId(CHILD_USER_ID)

        // Then
        assertFalse(childAchievements.isEmpty(), "Should have demo achievements")
        assertEquals(
            AchievementType.entries.size,
            childAchievements.size,
            "Should have all achievement types",
        )

        // Verify all achievement types are present
        val achievementTypes = childAchievements.map { it.type }.toSet()
        val expectedTypes = AchievementType.entries.toSet()
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
