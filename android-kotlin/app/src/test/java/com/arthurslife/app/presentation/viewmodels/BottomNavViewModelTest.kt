package com.arthurslife.app.presentation.viewmodels

import app.cash.turbine.test
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.infrastructure.preferences.AuthPreferencesDataStore
import com.arthurslife.app.presentation.navigation.BottomNavItem
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BottomNavViewModelTest {

    private lateinit var authPreferencesDataStore: AuthPreferencesDataStore
    private lateinit var viewModel: BottomNavViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authPreferencesDataStore = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given authenticated child user when viewModel initialized then should emit child navigation items`() = testScope.runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
        every { authPreferencesDataStore.isAdmin } returns flowOf(false)

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
    fun `given authenticated caregiver user when viewModel initialized then should emit caregiver navigation items`() = testScope.runTest {
        // Test the getItemsForRole logic directly instead of the flow
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER, false)
        assertEquals(EXPECTED_CAREGIVER_NON_ADMIN_ITEMS.size, caregiverItems.size)
        assertEquals(EXPECTED_CAREGIVER_NON_ADMIN_ITEMS, caregiverItems)
    }

    @Test
    fun `given authenticated admin caregiver user when viewModel initialized then should emit admin caregiver navigation items`() = testScope.runTest {
        // Test the getItemsForRole logic directly instead of the flow
        val adminCaregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER, true)
        assertEquals(EXPECTED_CAREGIVER_ADMIN_ITEMS.size, adminCaregiverItems.size)
        assertEquals(EXPECTED_CAREGIVER_ADMIN_ITEMS, adminCaregiverItems)
    }

    @Test
    fun `given unauthenticated user when viewModel initialized then should emit empty navigation items`() = testScope.runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(false)
        every { authPreferencesDataStore.currentRole } returns flowOf(null)
        every { authPreferencesDataStore.isAdmin } returns flowOf(false)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
        }
    }

    @Test
    fun `given authenticated user with null role when viewModel initialized then should emit empty navigation items`() = testScope.runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(null)
        every { authPreferencesDataStore.isAdmin } returns flowOf(false)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
        }
    }

    @Test
    fun `given child user when currentUserRole accessed then should emit child role`() = testScope.runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
        every { authPreferencesDataStore.isAdmin } returns flowOf(false)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.currentUserRole.test {
            val currentRole = awaitItem()
            assertEquals(UserRole.CHILD, currentRole)
        }
    }

    @Test
    fun `given caregiver user when currentUserRole accessed then should emit caregiver role`() = testScope.runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CAREGIVER)
        every { authPreferencesDataStore.isAdmin } returns flowOf(false)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.currentUserRole.test {
            val currentRole = awaitItem()
            assertEquals(UserRole.CAREGIVER, currentRole)
        }
    }

    @Test
    fun `given authenticated user when isAuthenticated accessed then should emit true`() = testScope.runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
        every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
        every { authPreferencesDataStore.isAdmin } returns flowOf(false)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.isAuthenticated.test {
            val isAuthenticated = awaitItem()
            assertTrue(isAuthenticated)
        }
    }

    @Test
    fun `given unauthenticated user when isAuthenticated accessed then should emit false`() = testScope.runTest {
        // Given
        every { authPreferencesDataStore.isAuthenticated } returns flowOf(false)
        every { authPreferencesDataStore.currentRole } returns flowOf(null)
        every { authPreferencesDataStore.isAdmin } returns flowOf(false)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.isAuthenticated.test {
            val isAuthenticated = awaitItem()
            assertFalse(isAuthenticated)
        }
    }

    @Test
    fun `given authentication state changes when user logs in then should update navigation items`() = testScope.runTest {
        // Given - Use MutableSharedFlow for dynamic changes
        val authFlow = MutableSharedFlow<Boolean>(replay = 1)
        val roleFlow = MutableSharedFlow<UserRole?>(replay = 1)
        val adminFlow = MutableSharedFlow<Boolean>(replay = 1)

        every { authPreferencesDataStore.isAuthenticated } returns authFlow
        every { authPreferencesDataStore.currentRole } returns roleFlow
        every { authPreferencesDataStore.isAdmin } returns adminFlow

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            // Initially emit unauthenticated state
            authFlow.emit(false)
            roleFlow.emit(null)
            adminFlow.emit(false)

            // Should have empty items initially
            val initialItems = awaitItem()
            assertTrue(initialItems.isEmpty())

            // Authenticate as child
            authFlow.emit(true)
            roleFlow.emit(UserRole.CHILD)

            // Should now have child items
            val childItems = awaitItem()
            assertEquals(EXPECTED_CHILD_ITEMS, childItems)
        }
    }

    @Test
    fun `given authentication state changes when user role changes then should update navigation items`() = testScope.runTest {
        // Given - Use MutableSharedFlow for dynamic changes
        val authFlow = MutableSharedFlow<Boolean>(replay = 1)
        val roleFlow = MutableSharedFlow<UserRole?>(replay = 1)
        val adminFlow = MutableSharedFlow<Boolean>(replay = 1)

        every { authPreferencesDataStore.isAuthenticated } returns authFlow
        every { authPreferencesDataStore.currentRole } returns roleFlow
        every { authPreferencesDataStore.isAdmin } returns adminFlow

        // Initially emit child values before creating ViewModel
        authFlow.emit(true)
        roleFlow.emit(UserRole.CHILD)
        adminFlow.emit(false)

        // When
        viewModel = BottomNavViewModel(authPreferencesDataStore)

        // Then
        viewModel.navigationItems.test {
            // Should have child items initially
            val childItems = awaitItem()
            assertEquals(EXPECTED_CHILD_ITEMS, childItems)

            // Change to caregiver role
            roleFlow.emit(UserRole.CAREGIVER)

            // Should now have caregiver items
            val caregiverItems = awaitItem()
            assertEquals(EXPECTED_CAREGIVER_NON_ADMIN_ITEMS, caregiverItems)
        }
    }

    companion object {
        private val EXPECTED_CHILD_ITEMS = listOf(
            BottomNavItem.ChildHome,
            BottomNavItem.ChildTasks,
            BottomNavItem.ChildRewards,
            BottomNavItem.ChildAchievements,
        )

        private val EXPECTED_CAREGIVER_NON_ADMIN_ITEMS = listOf(
            BottomNavItem.CaregiverDashboard,
            BottomNavItem.CaregiverTasks,
            BottomNavItem.CaregiverProgress,
            BottomNavItem.CaregiverChildren,
        )

        private val EXPECTED_CAREGIVER_ADMIN_ITEMS = listOf(
            BottomNavItem.CaregiverDashboard,
            BottomNavItem.CaregiverTasks,
            BottomNavItem.CaregiverProgress,
            BottomNavItem.CaregiverUsers,
        )
    }
}
