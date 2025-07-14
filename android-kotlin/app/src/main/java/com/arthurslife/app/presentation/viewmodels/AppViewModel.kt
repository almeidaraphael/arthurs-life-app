package com.arthurslife.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _appState: MutableStateFlow<AppState> = MutableStateFlow(AppState.Loading)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            try {
                val users = userRepository.getAllUsers()
                _appState.value = if (users.isEmpty()) {
                    AppState.NeedsOnboarding
                } else {
                    AppState.ReadyForAuth
                }
            } catch (e: IllegalStateException) {
                // If there's a state error accessing user data, assume we need onboarding
                println("AppViewModel: Error checking onboarding status - ${e.message}")
                _appState.value = AppState.NeedsOnboarding
            } catch (e: SecurityException) {
                // If there's a security error accessing user data, assume we need onboarding
                println("AppViewModel: Security error checking onboarding status - ${e.message}")
                _appState.value = AppState.NeedsOnboarding
            }
        }
    }

    fun onOnboardingCompleted() {
        _appState.value = AppState.ReadyForAuth
    }
}

sealed class AppState {
    data object Loading : AppState()
    data object NeedsOnboarding : AppState()
    data object ReadyForAuth : AppState()
}
