package com.lemonqwest.app.infrastructure.achievement

import com.lemonqwest.app.domain.achievement.AchievementType
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for InMemoryAchievementDataSource achievement counting operations.
 *
 * Tests cover:
 * - Counting unlocked achievements for users
 * - Zero count handling for users with no unlocked achievements
 * - Zero count for non-existent users
 * - Accurate counting with varying numbers of unlocked achievements
 *
 * CATEGORY A TEST: Infrastructure/data layer test using standalone pattern.
 * No ViewModelTestBase needed as this tests data layer, not presentation ViewModels.
 */

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("InMemoryAchievementDataSource - Counting Tests")
class InMemoryAchievementDataSourceCountingTest {

    private lateinit var dataSource: InMemoryAchievementDataSource

    companion object {
        private const val CHILD_USER_ID = "child-1"
        private const val TEST_USER_ID = "test-user-123"
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
            "user-all-unlocked" to AchievementType.entries.size,
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
