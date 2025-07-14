package com.arthurslife.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.auth.AuthResult
import com.arthurslife.app.domain.auth.AuthenticationDomainService
import com.arthurslife.app.domain.auth.AuthenticationService
import com.arthurslife.app.domain.auth.AuthenticationSessionService
import com.arthurslife.app.domain.auth.AuthenticationState
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val authenticationService: AuthenticationService,
    private val authenticationSessionService: AuthenticationSessionService,
    private val authenticationDomainService: AuthenticationDomainService,
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthenticationState())
    val authState: StateFlow<AuthenticationState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val currentUser = authenticationSessionService.getCurrentUser()
                if (currentUser != null) {
                    _authState.value =
                        AuthenticationState(
                            currentUser = currentUser,
                            isAuthenticated = true,
                            currentRole = currentUser.role,
                        )
                }
            } catch (e: Exception) {
                // Gracefully handle session service errors during initialization
                println("AuthViewModel: Session service error during initialization - ${e.message}")
                _authState.value = AuthenticationState()
            }
        }
    }

    fun authenticateWithPin(
        pin: String,
        onResult: (AuthResult) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val result = authenticationService.authenticateWithPin(pin)
                updateAuthState(result)
                onResult(result)
            } catch (e: Exception) {
                // Gracefully handle authentication service errors
                println("AuthViewModel: Authentication service error - ${e.message}")
                _authState.value = AuthenticationState()
            }
        }
    }

    fun switchRole(
        targetRole: UserRole,
        onResult: (AuthResult) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val result = authenticationService.switchRole(targetRole)
                if (result is AuthResult.Success) {
                    updateAuthState(result)
                }
                onResult(result)
            } catch (e: Exception) {
                // Gracefully handle role switch service errors
                println("AuthViewModel: Role switch service error - ${e.message}")
                // Don't call onResult to indicate error was handled
            }
        }
    }

    fun authenticateAsChild(onResult: (AuthResult) -> Unit) {
        viewModelScope.launch {
            try {
                val result = authenticationService.authenticateAsChild()
                updateAuthState(result)
                onResult(result)
            } catch (e: Exception) {
                // Gracefully handle child authentication service errors
                println("AuthViewModel: Child authentication service error - ${e.message}")
                _authState.value = AuthenticationState()
            }
        }
    }

    fun authenticateAsSpecificChild(
        childUser: User,
        onResult: (AuthResult) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val result = authenticationDomainService.authenticateAsSpecificChild(childUser)
                updateAuthState(result)
                onResult(result)
            } catch (e: Exception) {
                // Gracefully handle specific child authentication service errors
                println("AuthViewModel: Specific child authentication service error - ${e.message}")
                _authState.value = AuthenticationState()
            }
        }
    }

    /**
     * Switches to a specific user with PIN authentication for caregivers.
     *
     * @param targetUser The user to switch to
     * @param pin PIN for authentication (required for caregivers)
     * @param onResult Callback with the authentication result
     */
    fun switchToUser(
        targetUser: User,
        pin: String,
        onResult: (AuthResult) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                when (targetUser.role) {
                    UserRole.CAREGIVER -> {
                        // Authenticate with PIN and verify user matches
                        val result = authenticationService.authenticateWithPin(pin)
                        if (result is AuthResult.Success && result.user.id == targetUser.id) {
                            // PIN valid and user matches - update state once
                            updateAuthState(result)
                            onResult(result)
                        } else if (result is AuthResult.Success) {
                            // PIN valid but wrong user - don't update auth state
                            onResult(AuthResult.InvalidPin)
                        } else {
                            // Authentication failed - don't update auth state
                            onResult(result)
                        }
                    }
                    UserRole.CHILD -> {
                        // Direct authentication for child users
                        val result = authenticationDomainService
                            .authenticateAsSpecificChild(targetUser)
                        updateAuthState(result)
                        onResult(result)
                    }
                }
            } catch (e: Exception) {
                println("AuthViewModel: User switch service error - ${e.message}")
                onResult(AuthResult.UserNotFound)
            }
        }
    }

    /**
     * Switches to a specific user without PIN (for child users only).
     *
     * @param targetUser The user to switch to
     * @param onResult Callback with the authentication result
     */
    fun switchToUser(
        targetUser: User,
        onResult: (AuthResult) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                when (targetUser.role) {
                    UserRole.CHILD -> {
                        val result = authenticationDomainService
                            .authenticateAsSpecificChild(targetUser)
                        updateAuthState(result)
                        onResult(result)
                    }
                    UserRole.CAREGIVER -> {
                        // Caregivers require PIN authentication
                        onResult(AuthResult.InvalidPin)
                    }
                }
            } catch (e: Exception) {
                println("AuthViewModel: User switch service error - ${e.message}")
                onResult(AuthResult.UserNotFound)
            }
        }
    }

    private fun updateAuthState(result: AuthResult) {
        val newState = when (result) {
            is AuthResult.Success ->
                AuthenticationState(
                    currentUser = result.user,
                    isAuthenticated = true,
                    currentRole = result.user.role,
                )
            else ->
                AuthenticationState(
                    currentUser = null,
                    isAuthenticated = false,
                    currentRole = null,
                )
        }

        println(
            "AuthViewModel: Updating auth state - result: $result, new state: user=${newState.currentUser?.id}, isAuthenticated=${newState.isAuthenticated}",
        )
        _authState.value = newState
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authenticationService.logout()
                _authState.value = AuthenticationState()
            } catch (e: Exception) {
                // Gracefully handle logout service errors
                println("AuthViewModel: Logout service error - ${e.message}")
                // Still clear the state regardless of service error
                _authState.value = AuthenticationState()
            }
        }
    }
}
