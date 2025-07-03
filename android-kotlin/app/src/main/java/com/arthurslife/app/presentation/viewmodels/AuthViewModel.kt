package com.arthurslife.app.presentation.viewmodels

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

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authenticationService: AuthenticationService,
        private val authenticationSessionService: AuthenticationSessionService,
    ) : ViewModel() {
        private val _authState = MutableStateFlow(AuthenticationState())
        val authState: StateFlow<AuthenticationState> = _authState.asStateFlow()

        init {
            viewModelScope.launch {
                val currentUser = authenticationSessionService.getCurrentUser()
                if (currentUser != null) {
                    _authState.value =
                        AuthenticationState(
                            currentUser = currentUser,
                            isAuthenticated = true,
                            currentRole = currentUser.role,
                        )
                }
            }
        }

        fun authenticateWithPin(
            pin: String,
            onResult: (AuthResult) -> Unit,
        ) {
            viewModelScope.launch {
                val result = authenticationService.authenticateWithPin(pin)
                updateAuthState(result)
                onResult(result)
            }
        }

        fun switchRole(
            targetRole: UserRole,
            onResult: (AuthResult) -> Unit,
        ) {
            viewModelScope.launch {
                val result = authenticationService.switchRole(targetRole)
                if (result is AuthResult.Success) {
                    updateAuthState(result)
                }
                onResult(result)
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
                authenticationService.logout()
                _authState.value = AuthenticationState()
            }
        }
    }
