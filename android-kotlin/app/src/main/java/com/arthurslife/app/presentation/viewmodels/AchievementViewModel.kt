package com.arthurslife.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.achievement.Achievement
import com.arthurslife.app.domain.achievement.AchievementRepository
import com.arthurslife.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.arthurslife.app.domain.common.AchievementEventManager
import com.arthurslife.app.domain.common.DomainException
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for achievement viewing in Arthur's Life MVP.
 *
 * This ViewModel handles loading and displaying achievements for the current child user.
 */
@HiltViewModel
class AchievementViewModel
@Inject
constructor(
    private val achievementTrackingUseCase: AchievementTrackingUseCase,
    private val achievementRepository: AchievementRepository,
    private val userRepository: UserRepository,
    private val achievementEventManager: AchievementEventManager,
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(AchievementUiState())
    val uiState: StateFlow<AchievementUiState> = _uiState.asStateFlow()

    // Achievements
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    init {
        loadAchievements()

        // Listen for achievement update events and refresh data
        achievementEventManager.achievementUpdates
            .onEach { event ->
                // Refresh achievements when an update event is received
                loadAchievements()
            }
            .launchIn(viewModelScope)
    }

    /**
     * Loads achievements for the child user.
     * Initializes achievements if they don't exist yet.
     */
    private fun loadAchievements() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val child = userRepository.findByRole(UserRole.CHILD)
                if (child != null) {
                    // Initialize achievements for the user if they don't exist
                    achievementRepository.initializeAchievementsForUser(child.id)

                    // Load all achievements
                    val achievements = achievementTrackingUseCase.getAllAchievements(child.id)
                    _achievements.value = achievements
                    _uiState.value = _uiState.value.copy(isLoading = false)
                } else {
                    _achievements.value = emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No child user found",
                    )
                }
            } catch (e: DomainException) {
                _achievements.value = emptyList()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    /**
     * Refreshes achievement data.
     */
    fun refresh() {
        loadAchievements()
    }

    /**
     * Clears any error messages.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI state for achievement screens.
 */
data class AchievementUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)
