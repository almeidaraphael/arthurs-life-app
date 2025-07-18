package com.lemonqwest.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemonqwest.app.domain.achievement.Achievement
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.common.DomainException
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for achievement viewing in LemonQwest MVP.
 *
 * This ViewModel handles loading and displaying achievements for the current child user.
 */
@HiltViewModel
class AchievementViewModel(
    private val achievementTrackingUseCase: AchievementTrackingUseCase,
    private val achievementRepository: AchievementRepository,
    private val userRepository: UserRepository,
    private val achievementEventManager: AchievementEventManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    @Inject
    constructor(
        achievementTrackingUseCase: AchievementTrackingUseCase,
        achievementRepository: AchievementRepository,
        userRepository: UserRepository,
        achievementEventManager: AchievementEventManager,
    ) : this(
        achievementTrackingUseCase,
        achievementRepository,
        userRepository,
        achievementEventManager,
        Dispatchers.IO,
    )

    // UI State
    private val _uiState = MutableStateFlow(AchievementUiState())
    val uiState: StateFlow<AchievementUiState> = _uiState.asStateFlow()

    // Achievements
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private var isInitialized = false

    /**
     * Initialize the achievement system. This should be called explicitly in tests,
     * but will be called automatically in production when needed.
     */
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        // Use IO dispatcher for initialization to avoid Main dispatcher issues in tests
        CoroutineScope(ioDispatcher).launch {
            loadAchievements()
        }
    }

    /**
     * Reset initialization state for testing purposes.
     * This allows tests to control when initialization happens.
     */
    fun resetInitializationForTesting() {
        isInitialized = false
    }

    /**
     * Sets up achievement event listening. Called automatically in production use.
     * Can be called explicitly in tests if needed.
     */
    fun setupEventListening() {
        // Listen for achievement update events and refresh data
        achievementEventManager.achievementUpdates
            .onEach { event ->
                // Refresh achievements when an update event is received
                refresh()
            }
            .launchIn(viewModelScope)
    }

    /**
     * Loads achievements for the child user.
     * Initializes achievements if they don't exist yet.
     */
    private suspend fun loadAchievements() {
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

    /**
     * Refreshes achievement data.
     */
    fun refresh() {
        initialize() // Ensure initialization happens in production
        viewModelScope.launch {
            loadAchievements()
        }
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
