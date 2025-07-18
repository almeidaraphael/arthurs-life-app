package com.lemonqwest.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRepositoryException
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.infrastructure.preferences.AuthPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state of the top navigation bar.
 *
 * This ViewModel follows Clean Architecture principles by encapsulating all logic
 * for determining what content should be displayed in the top bar based on:
 * - Current user role (Child/Caregiver)
 * - Current screen context
 * - Authentication state
 * - User data and progress information
 *
 * The ViewModel provides reactive state updates through StateFlow, ensuring the
 * top bar content stays synchronized with changes in user state, navigation, and
 * application data.
 */
@HiltViewModel
class TopBarViewModel @Inject constructor(
    private val authPreferencesDataStore: AuthPreferencesDataStore,
    private val userRepository: UserRepository,
) : ViewModel() {

    /**
     * Current screen context for determining which elements to show in the top bar.
     * This should be updated by the UI when navigation occurs.
     */
    private val _currentScreen = MutableStateFlow(TopBarScreen.HOME)

    /**
     * Public method to update the current screen context.
     * Should be called by navigation components when screen changes occur.
     */
    fun updateCurrentScreen(screen: TopBarScreen) {
        _currentScreen.value = screen
    }

    /**
     * StateFlow that emits the complete top bar state based on current user,
     * authentication status, role, and screen context.
     *
     * This flow automatically updates when any of the following change:
     * - User authentication state
     * - User role or admin status
     * - Current screen context
     * - User data (avatar, name, token balance)
     */
    val topBarState: StateFlow<TopBarState> = combine(
        authPreferencesDataStore.isAuthenticated,
        authPreferencesDataStore.currentUserId,
        authPreferencesDataStore.currentRole,
        authPreferencesDataStore.isAdmin,
        _currentScreen,
    ) { isAuthenticated, currentUserId, currentRole, isAdmin, currentScreen ->
        when {
            !isAuthenticated || currentUserId == null || currentRole == null -> {
                // User is not authenticated, return empty state
                TopBarState.empty()
            }
            else -> {
                // Fetch current user data and build appropriate state
                buildTopBarState(
                    userId = currentUserId,
                    role = currentRole,
                    screen = currentScreen,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TopBarState.empty(),
    )

    /**
     * StateFlow that indicates whether the top bar should be visible.
     * The top bar is visible when the user is authenticated.
     */
    val isTopBarVisible: StateFlow<Boolean> = authPreferencesDataStore.isAuthenticated
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    /**
     * StateFlow that emits the current user role for additional UI customization.
     * This can be used by the UI to adapt other elements beyond the top bar content.
     */
    val currentUserRole: StateFlow<UserRole?> = authPreferencesDataStore.currentRole
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    /**
     * Builds the appropriate TopBarState based on user role and screen context.
     *
     * This method encapsulates the business logic for determining which elements
     * should be visible in the top bar according to the PRD requirements.
     */
    private suspend fun buildTopBarState(
        userId: String,
        role: UserRole,
        screen: TopBarScreen,
    ): TopBarState {
        return try {
            // Fetch current user data
            val user = userRepository.getUserById(userId)

            if (user == null) {
                // User not found, return empty state
                Timber.w("User not found with ID: $userId")
                return TopBarState.empty()
            }

            when (role) {
                UserRole.CHILD -> buildChildTopBarState(user, screen)
                UserRole.CAREGIVER -> buildCaregiverTopBarState(user, screen)
            }
        } catch (e: UserRepositoryException) {
            // Log the error and return empty state if user data cannot be fetched
            Timber.w(e, "Failed to fetch user data for top bar state")
            TopBarState.empty()
        }
    }

    /**
     * Builds top bar state for child users based on current screen.
     * Implements PRD requirements for child mode top bar elements.
     */
    private suspend fun buildChildTopBarState(
        user: User,
        screen: TopBarScreen,
    ): TopBarState {
        // TODO: In future iterations, fetch actual progress data from repositories
        // For now, using placeholder data to establish the structure

        val tasksProgress = when (screen) {
            TopBarScreen.HOME, TopBarScreen.TASKS -> {
                // TODO: Fetch actual tasks progress from TaskRepository
                TasksProgress(completedTasks = 0, totalTasks = 0)
            }
            else -> null
        }

        val totalTokensEarned = when (screen) {
            TopBarScreen.TASKS -> {
                // TODO: Fetch total tokens earned from TokenRepository or TaskRepository
                user.tokenBalance // Placeholder: using current balance
            }
            else -> null
        }

        val rewardsAvailable = when (screen) {
            TopBarScreen.REWARDS -> {
                // TODO: Fetch available rewards count from RewardRepository
                0 // Placeholder
            }
            else -> null
        }

        val achievementsProgress = when (screen) {
            TopBarScreen.ACHIEVEMENTS -> {
                // TODO: Fetch achievements progress from AchievementRepository
                AchievementsProgress(unlockedAchievements = 0, totalAchievements = 0)
            }
            else -> null
        }

        return TopBarState.forChild(
            user = user,
            screen = screen,
            tasksProgress = tasksProgress,
            totalTokensEarned = totalTokensEarned,
            rewardsAvailable = rewardsAvailable,
            achievementsProgress = achievementsProgress,
        )
    }

    /**
     * Builds top bar state for caregiver users based on current screen.
     * Implements PRD requirements for caregiver mode top bar elements.
     */
    private suspend fun buildCaregiverTopBarState(
        user: User,
        screen: TopBarScreen,
    ): TopBarState {
        val selectedChild = when (screen) {
            TopBarScreen.HOME -> {
                // TODO: Fetch currently selected child from preferences or state management
                // For now, returning null to establish the structure
                null
            }
            else -> null
        }

        return TopBarState.forCaregiver(
            user = user,
            screen = screen,
            selectedChild = selectedChild,
        )
    }
}
