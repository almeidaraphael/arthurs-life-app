package com.arthurslife.app.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.auth.AuthResult
import com.arthurslife.app.domain.auth.AuthenticationService
import com.arthurslife.app.domain.auth.AuthenticationSessionService
import com.arthurslife.app.domain.auth.AuthenticationState
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
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }
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
                Log.w(TAG, "Session service error during initialization", e)
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
                Log.w(TAG, "Authentication service error", e)
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
                Log.w(TAG, "Role switch service error", e)
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
                Log.w(TAG, "Child authentication service error", e)
                _authState.value = AuthenticationState()
            }
        }
    }

    private fun updateAuthState(result: AuthResult) {
        _authState.value =
            when (result) {
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
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authenticationService.logout()
                _authState.value = AuthenticationState()
            } catch (e: Exception) {
                // Gracefully handle logout service errors
                Log.w(TAG, "Logout service error", e)
                // Still clear the state regardless of service error
                _authState.value = AuthenticationState()
            }
        }
    }
}
