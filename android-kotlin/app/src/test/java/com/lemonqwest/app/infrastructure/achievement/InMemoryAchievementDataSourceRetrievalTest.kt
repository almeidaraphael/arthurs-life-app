package com.lemonqwest.app.infrastructure.achievement

import com.lemonqwest.app.domain.achievement.AchievementCategory
import com.lemonqwest.app.domain.achievement.AchievementType
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * CATEGORY A TEST: Infrastructure/data layer test using standalone pattern.
 * No ViewModelTestBase needed as this tests data layer, not presentation ViewModels.
 *
 * Focused test suite for InMemoryAchievementDataSource achievement retrieval operations.
 *
 * Tests cover:
 * - Finding achievements by ID
 * - Finding achievements by user ID
 * - Finding unlocked achievements
 * - Finding achievements by category
 * - Finding achievements by type
 */

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("InMemoryAchievementDataSource - Retrieval Tests")
class InMemoryAchievementDataSourceRetrievalTest {

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
