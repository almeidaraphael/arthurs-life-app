package com.lemonqwest.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.infrastructure.preferences.AuthPreferencesDataStore
import com.lemonqwest.app.presentation.navigation.BottomNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel responsible for determining which bottom navigation items to display
 * based on the current user's role and authentication status.
 *
 * This ViewModel follows Clean Architecture principles by keeping navigation logic
 * separate from the UI layer while providing reactive updates through StateFlow.
 */
@HiltViewModel
class BottomNavViewModel @Inject constructor(
    private val authPreferencesDataStore: AuthPreferencesDataStore,
) : ViewModel() {

    /**
     * StateFlow that emits the current list of bottom navigation items
     * based on the authenticated user's role and admin status.
     *
     * This flow automatically updates when the user role, admin status, or
     * authentication state changes (login, role switching, logout).
     */
    val navigationItems: StateFlow<List<BottomNavItem>> =
        combine(
            authPreferencesDataStore.isAuthenticated,
            authPreferencesDataStore.currentRole,
            authPreferencesDataStore.isAdmin,
        ) { isAuthenticated, currentRole, isAdmin ->
            when {
                isAuthenticated && currentRole != null -> {
                    // Get navigation items based on the current user's role and admin status
                    BottomNavItem.getItemsForRole(currentRole, isAdmin)
                }
                else -> {
                    // Return empty list when user is not authenticated
                    emptyList()
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    /**
     * StateFlow that emits the current user role for additional UI customization.
     * This can be used by the UI to adapt other elements beyond navigation items.
     */
    val currentUserRole: StateFlow<UserRole?> =
        authPreferencesDataStore.currentRole
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )

    /**
     * StateFlow that indicates whether the user is currently authenticated.
     * This can be used to show/hide the bottom navigation bar entirely.
     */
    val isAuthenticated: StateFlow<Boolean> =
        authPreferencesDataStore.isAuthenticated
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = false,
            )
}
