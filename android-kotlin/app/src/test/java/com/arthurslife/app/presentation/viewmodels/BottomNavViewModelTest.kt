package com.arthurslife.app.presentation.viewmodels

import app.cash.turbine.test
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.infrastructure.preferences.AuthPreferencesDataStore
import com.arthurslife.app.presentation.navigation.BottomNavItem
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BottomNavViewModelTest {

    private lateinit var authPreferencesDataStore: AuthPreferencesDataStore
    private lateinit var viewModel: BottomNavViewModel

    @Before
    fun setUp() {
        authPreferencesDataStore = mockk()
    }

    @Test
    fun `given authenticated child user when viewModel initialized then should emit child navigation items`() = runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            val items = awaitItem()
            assertEquals(EXPECTED_CHILD_ITEMS.size, items.size)
            assertEquals(EXPECTED_CHILD_ITEMS, items)
        }
    }

    @Test
    fun `given authenticated caregiver user when viewModel initialized then should emit caregiver navigation items`() = runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CAREGIVER)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            val items = awaitItem()
            assertEquals(EXPECTED_CAREGIVER_ITEMS.size, items.size)
            assertEquals(EXPECTED_CAREGIVER_ITEMS, items)
        }
    }

    @Test
    fun `given unauthenticated user when viewModel initialized then should emit empty navigation items`() = runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(false)
        every { authPreferencesDataStore.currentRole } returns flowOf(null)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
        }
    }

    @Test
    fun `given authenticated user with null role when viewModel initialized then should emit empty navigation items`() = runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(null)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
        }
    }

    @Test
    fun `given child user when currentUserRole accessed then should emit child role`() = runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.currentUserRole.test {
            val role = awaitItem()
            assertEquals(UserRole.CHILD, role)
        }
    }

    @Test
    fun `given caregiver user when currentUserRole accessed then should emit caregiver role`() = runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CAREGIVER)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.currentUserRole.test {
            val role = awaitItem()
            assertEquals(UserRole.CAREGIVER, role)
        }
    }

    @Test
    fun `given authenticated user when isAuthenticated accessed then should emit true`() = runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.isAuthenticated.test {
            val isAuthenticated = awaitItem()
            assertTrue(isAuthenticated)
        }
    }

    @Test
    fun `given unauthenticated user when isAuthenticated accessed then should emit false`() = runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(false)
        every { authPreferencesDataStore.currentRole } returns flowOf(null)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.isAuthenticated.test {
            val isAuthenticated = awaitItem()
            assertFalse(isAuthenticated)
        }
    }

    @Test
    fun `given authentication state changes when user logs in then should update navigation items`() = runTest {
        // Given - Initially unauthenticated
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(false, true)
        every { authPreferencesDataStore.currentRole } returns flowOf(null, UserRole.CHILD)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            // First emission: empty items for unauthenticated user
            val emptyItems = awaitItem()
            assertTrue(emptyItems.isEmpty())

            // Second emission: child items after authentication
            val childItems = awaitItem()
            assertEquals(EXPECTED_CHILD_ITEMS.size, childItems.size)
            assertEquals(EXPECTED_CHILD_ITEMS, childItems)
        }
    }

    @Test
    fun `given authentication state changes when user role changes then should update navigation items`() = runTest {
        // Given - Role changes from child to caregiver
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true, true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD, UserRole.CAREGIVER)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            // First emission: child items
            val childItems = awaitItem()
            assertEquals(EXPECTED_CHILD_ITEMS, childItems)

            // Second emission: caregiver items after role change
            val caregiverItems = awaitItem()
            assertEquals(EXPECTED_CAREGIVER_ITEMS, caregiverItems)
        }
    }

    companion object {
        private val EXPECTED_CHILD_ITEMS = listOf(
            BottomNavItem.ChildHome,
            BottomNavItem.ChildTasks,
            BottomNavItem.ChildRewards,
            BottomNavItem.ChildAchievements,
        )

        private val EXPECTED_CAREGIVER_ITEMS = listOf(
            BottomNavItem.CaregiverDashboard,
            BottomNavItem.CaregiverTasks,
            BottomNavItem.CaregiverProgress,
            BottomNavItem.CaregiverChildren,
        )
    }
}
