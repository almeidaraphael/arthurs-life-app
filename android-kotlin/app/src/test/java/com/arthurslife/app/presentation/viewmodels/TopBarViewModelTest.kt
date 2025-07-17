package com.arthurslife.app.presentation.viewmodels

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRepositoryException
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.infrastructure.preferences.AuthPreferencesDataStore
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TopBarViewModel")
class TopBarViewModelTest {

    private lateinit var viewModel: TopBarViewModel
    private lateinit var authPreferencesDataStore: AuthPreferencesDataStore
    private lateinit var userRepository: UserRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authPreferencesDataStore = mockk()
        userRepository = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearMocks(authPreferencesDataStore, userRepository)
    }

    private fun createViewModel(): TopBarViewModel {
        return TopBarViewModel(
            authPreferencesDataStore = authPreferencesDataStore,
            userRepository = userRepository,
        )
    }

    @Nested
    @DisplayName("Authentication State")
    inner class AuthenticationState {

        @Test
        @DisplayName("should return empty state when not authenticated")
        fun shouldReturnEmptyStateWhenNotAuthenticated() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(false)
            every { authPreferencesDataStore.currentUserId } returns flowOf(null)
            every { authPreferencesDataStore.currentRole } returns flowOf(null)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)

            // When
            viewModel = createViewModel()
            val state = viewModel.topBarState.first()

            // Then
            assertEquals(TopBarState.empty(), state)
            assertFalse(state.isChildMode)
            assertFalse(state.isCaregiverMode)
            assertFalse(state.isSettingsVisible)
        }

        @Test
        @DisplayName("should return empty state when user ID is null")
        fun shouldReturnEmptyStateWhenUserIdIsNull() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf(null)
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)

            // When
            viewModel = createViewModel()
            val state = viewModel.topBarState.first()

            // Then
            assertEquals(TopBarState.empty(), state)
        }

        @Test
        @DisplayName("should return empty state when role is null")
        fun shouldReturnEmptyStateWhenRoleIsNull() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("user-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(null)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)

            // When
            viewModel = createViewModel()
            val state = viewModel.topBarState.first()

            // Then
            assertEquals(TopBarState.empty(), state)
        }
    }

    @Nested
    @DisplayName("Child Mode State")
    inner class ChildModeState {

        private val childUser = TestDataFactory.createChildUser(
            id = "child-123",
            name = "Test Child",
        )

        @Test
        @DisplayName("should build child state for home screen")
        fun shouldBuildChildStateForHomeScreen() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("child-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)
            coEvery { userRepository.getUserById("child-123") } returns childUser

            // When
            viewModel = createViewModel()
            viewModel.updateCurrentScreen(TopBarScreen.HOME)
            val state = viewModel.topBarState.first()

            // Then
            assertTrue(state.isChildMode)
            assertFalse(state.isCaregiverMode)
            assertEquals(UserRole.CHILD, state.userRole)
            assertEquals(TopBarScreen.HOME, state.currentScreen)
            assertNotNull(state.tokenBalance)
            assertTrue(state.isSettingsVisible)
            assertNotNull(state.userAvatar)
            assertEquals("Test Child", state.userAvatar.userName)
        }

        @Test
        @DisplayName("should build child state for tasks screen with progress")
        fun shouldBuildChildStateForTasksScreen() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("child-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)
            coEvery { userRepository.getUserById("child-123") } returns childUser

            // When
            viewModel = createViewModel()
            viewModel.updateCurrentScreen(TopBarScreen.TASKS)
            val state = viewModel.topBarState.first()

            // Then
            assertTrue(state.isChildMode)
            assertEquals(TopBarScreen.TASKS, state.currentScreen)
            assertNotNull(state.tokenBalance)
            assertNotNull(state.tasksProgress)
            assertNotNull(state.totalTokensEarned)
            // Total tokens earned comparison
        }

        @Test
        @DisplayName("should build child state for rewards screen")
        fun shouldBuildChildStateForRewardsScreen() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("child-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)
            coEvery { userRepository.getUserById("child-123") } returns childUser

            // When
            viewModel = createViewModel()
            viewModel.updateCurrentScreen(TopBarScreen.REWARDS)
            val state = viewModel.topBarState.first()

            // Then
            assertTrue(state.isChildMode)
            assertEquals(TopBarScreen.REWARDS, state.currentScreen)
            assertNotNull(state.tokenBalance)
            assertNotNull(state.rewardsAvailable)
            assertEquals(0, state.rewardsAvailable) // Placeholder value
        }

        @Test
        @DisplayName("should build child state for achievements screen")
        fun shouldBuildChildStateForAchievementsScreen() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("child-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)
            coEvery { userRepository.getUserById("child-123") } returns childUser

            // When
            viewModel = createViewModel()
            viewModel.updateCurrentScreen(TopBarScreen.ACHIEVEMENTS)
            val state = viewModel.topBarState.first()

            // Then
            assertTrue(state.isChildMode)
            assertEquals(TopBarScreen.ACHIEVEMENTS, state.currentScreen)
            assertNotNull(state.tokenBalance)
            assertNotNull(state.achievementsProgress)
            assertEquals(0, state.achievementsProgress!!.unlockedAchievements)
            assertEquals(0, state.achievementsProgress!!.totalAchievements)
        }
    }

    @Nested
    @DisplayName("Caregiver Mode State")
    inner class CaregiverModeState {

        private val caregiverUser = TestDataFactory.createCaregiverUser(
            id = "caregiver-123",
            name = "Test Caregiver",
        )

        @Test
        @DisplayName("should build caregiver state for home screen")
        fun shouldBuildCaregiverStateForHomeScreen() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("caregiver-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CAREGIVER)
            every { authPreferencesDataStore.isAdmin } returns flowOf(true)
            coEvery { userRepository.getUserById("caregiver-123") } returns caregiverUser

            // When
            viewModel = createViewModel()
            viewModel.updateCurrentScreen(TopBarScreen.HOME)
            val state = viewModel.topBarState.first()

            // Then
            assertFalse(state.isChildMode)
            assertTrue(state.isCaregiverMode)
            assertEquals(UserRole.CAREGIVER, state.userRole)
            assertEquals(TopBarScreen.HOME, state.currentScreen)
            assertTrue(state.isSettingsVisible)
            assertNotNull(state.userAvatar)
            assertEquals("Test Caregiver", state.userAvatar.userName)
            // Selected child is null in placeholder implementation
        }

        @Test
        @DisplayName("should build caregiver state for children management screen")
        fun shouldBuildCaregiverStateForChildrenManagementScreen() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("caregiver-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CAREGIVER)
            every { authPreferencesDataStore.isAdmin } returns flowOf(true)
            coEvery { userRepository.getUserById("caregiver-123") } returns caregiverUser

            // When
            viewModel = createViewModel()
            viewModel.updateCurrentScreen(TopBarScreen.CHILDREN_MANAGEMENT)
            val state = viewModel.topBarState.first()

            // Then
            assertTrue(state.isCaregiverMode)
            assertEquals(TopBarScreen.CHILDREN_MANAGEMENT, state.currentScreen)
            // No selected child for non-home screens
        }

        @Test
        @DisplayName("should build caregiver state for other screens")
        fun shouldBuildCaregiverStateForOtherScreens() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("caregiver-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CAREGIVER)
            every { authPreferencesDataStore.isAdmin } returns flowOf(true)
            coEvery { userRepository.getUserById("caregiver-123") } returns caregiverUser

            // When
            viewModel = createViewModel()
            viewModel.updateCurrentScreen(TopBarScreen.OTHER)
            val state = viewModel.topBarState.first()

            // Then
            assertTrue(state.isCaregiverMode)
            assertEquals(TopBarScreen.OTHER, state.currentScreen)
            // No token balance for caregivers
            // No selected child for non-home screens
        }
    }

    @Nested
    @DisplayName("Error Handling")
    inner class ErrorHandling {

        @Test
        @DisplayName("should return empty state when user repository throws exception")
        fun shouldReturnEmptyStateWhenUserRepositoryThrowsException() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("user-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)
            coEvery { userRepository.getUserById("user-123") } throws UserRepositoryException("User not found")

            // When
            viewModel = createViewModel()
            val state = viewModel.topBarState.first()

            // Then
            assertEquals(TopBarState.empty(), state)
            coVerify { userRepository.getUserById("user-123") }
        }

        @Test
        @DisplayName("should return empty state when user is null")
        fun shouldReturnEmptyStateWhenUserIsNull() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("user-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)
            coEvery { userRepository.getUserById("user-123") } returns null

            // When
            viewModel = createViewModel()
            val state = viewModel.topBarState.first()

            // Then
            assertEquals(TopBarState.empty(), state)
        }
    }

    @Nested
    @DisplayName("Screen Updates")
    inner class ScreenUpdates {

        @Test
        @DisplayName("should update current screen when updateCurrentScreen is called")
        fun shouldUpdateCurrentScreenWhenUpdateCurrentScreenIsCalled() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)
            every { authPreferencesDataStore.currentUserId } returns flowOf("user-123")
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CHILD)
            every { authPreferencesDataStore.isAdmin } returns flowOf(false)
            val user = TestDataFactory.createChildUser(
                id = "user-123",
                name = "Test User",
            )
            coEvery { userRepository.getUserById("user-123") } returns user

            // When
            viewModel = createViewModel()
            viewModel.updateCurrentScreen(TopBarScreen.TASKS)
            val state = viewModel.topBarState.first()

            // Then
            assertEquals(TopBarScreen.TASKS, state.currentScreen)
        }
    }

    @Nested
    @DisplayName("Visibility State")
    inner class VisibilityState {

        @Test
        @DisplayName("should return true for isTopBarVisible when authenticated")
        fun shouldReturnTrueForIsTopBarVisibleWhenAuthenticated() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(true)

            // When
            viewModel = createViewModel()
            val isVisible = viewModel.isTopBarVisible.first()

            // Then
            assertTrue(isVisible)
        }

        @Test
        @DisplayName("should return false for isTopBarVisible when not authenticated")
        fun shouldReturnFalseForIsTopBarVisibleWhenNotAuthenticated() = runTest {
            // Given
            every { authPreferencesDataStore.isAuthenticated } returns flowOf(false)

            // When
            viewModel = createViewModel()
            val isVisible = viewModel.isTopBarVisible.first()

            // Then
            assertFalse(isVisible)
        }
    }

    @Nested
    @DisplayName("Current User Role")
    inner class CurrentUserRole {

        @Test
        @DisplayName("should return current user role from auth preferences")
        fun shouldReturnCurrentUserRoleFromAuthPreferences() = runTest {
            // Given
            every { authPreferencesDataStore.currentRole } returns flowOf(UserRole.CAREGIVER)

            // When
            viewModel = createViewModel()
            val role = viewModel.currentUserRole.first()

            // Then
            assertEquals(UserRole.CAREGIVER, role)
        }

        @Test
        @DisplayName("should return null when no role is set")
        fun shouldReturnNullWhenNoRoleIsSet() = runTest {
            // Given
            every { authPreferencesDataStore.currentRole } returns flowOf(null)

            // When
            viewModel = createViewModel()
            val role = viewModel.currentUserRole.first()

            // Then
            assertEquals(null, role)
        }
    }
}
