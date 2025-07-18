package com.lemonqwest.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.DomainException
import com.lemonqwest.app.domain.reward.Reward
import com.lemonqwest.app.domain.reward.RewardCategory
import com.lemonqwest.app.domain.reward.RewardRepository
import com.lemonqwest.app.domain.reward.usecase.RedemptionValidation
import com.lemonqwest.app.domain.reward.usecase.RewardRedemptionUseCase
import com.lemonqwest.app.domain.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for reward redemption operations in LemonQwest MVP.
 *
 * This ViewModel handles the state and business logic for child reward redemption,
 * including browsing available rewards, checking affordability, and redeeming rewards.
 * It provides real-time token balance updates and reward availability.
 */
@HiltViewModel
class RewardViewModel
@Inject
constructor(
    private val rewardRepository: RewardRepository,
    private val rewardRedemptionUseCase: RewardRedemptionUseCase,
    private val authenticationSessionService: AuthenticationSessionService,
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(RewardUiState())
    val uiState: StateFlow<RewardUiState> = _uiState.asStateFlow()

    // Reward Lists
    private val _allRewards = MutableStateFlow<List<Reward>>(emptyList())
    val allRewards: StateFlow<List<Reward>> = _allRewards.asStateFlow()

    private val _availableRewards = MutableStateFlow<List<Reward>>(emptyList())
    val availableRewards: StateFlow<List<Reward>> = _availableRewards.asStateFlow()

    private val _redeemableRewards = MutableStateFlow<List<Reward>>(emptyList())
    val redeemableRewards: StateFlow<List<Reward>> = _redeemableRewards.asStateFlow()

    // Current User and Token Balance
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _tokenBalance = MutableStateFlow(0)
    val tokenBalance: StateFlow<Int> = _tokenBalance.asStateFlow()

    // Selected Category Filter
    private val _selectedCategory = MutableStateFlow<RewardCategory?>(null)
    val selectedCategory: StateFlow<RewardCategory?> = _selectedCategory.asStateFlow()

    // Current authenticated user ID
    private var currentUserId: String? = null

    init {
        loadCurrentUser()
    }

    /**
     * Refreshes the current user's token balance from the authentication service.
     */
    fun refreshTokenBalance() {
        viewModelScope.launch {
            try {
                val currentUser = authenticationSessionService.getCurrentUser()
                if (currentUser != null) {
                    _currentUser.value = currentUser
                    _tokenBalance.value = currentUser.tokenBalance.getValue()
                    // Also refresh reward affordability after token balance change
                    loadRewards(currentUser.id)
                }
            } catch (e: DomainException) {
                // Token balance refresh failed - this is a non-critical operation
                // We don't update error state to avoid disrupting user experience
                println("RewardViewModel: Token balance refresh failed - ${e.message}")
            }
        }
    }

    /**
     * Loads the current authenticated user and their rewards.
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val currentUser = authenticationSessionService.getCurrentUser()
                currentUserId = currentUser?.id

                if (currentUser != null) {
                    _currentUser.value = currentUser
                    _tokenBalance.value = currentUser.tokenBalance.getValue()
                    loadRewards(currentUser.id)
                }
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Loads all rewards for the current user.
     */
    private fun loadRewards(userId: String) {
        viewModelScope.launch {
            try {
                // Get all available rewards from repository
                val allRewards = rewardRepository.getAvailableRewards()
                _allRewards.value = allRewards
                _availableRewards.value = allRewards

                // Get redeemable rewards (affordable and no approval required)
                val redeemableRewardsResult = rewardRedemptionUseCase.getRedeemableRewards(userId)
                redeemableRewardsResult.onSuccess { rewards ->
                    _redeemableRewards.value = rewards
                }

                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Filters rewards by category.
     */
    fun filterByCategory(category: RewardCategory?) {
        _selectedCategory.value = category

        val baseRewards = _allRewards.value
        val filteredRewards = if (category != null) {
            baseRewards.filter { it.category == category }
        } else {
            baseRewards
        }

        _availableRewards.value = filteredRewards

        // Note: We don't update _redeemableRewards as it represents
        // the actual redeemable rewards regardless of filter
    }

    /**
     * Redeems a reward for the current user.
     */
    fun redeemReward(rewardId: String) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val result = rewardRedemptionUseCase.redeemReward(rewardId, userId)
                result.onSuccess { updatedUser ->
                    _currentUser.value = updatedUser
                    _tokenBalance.value = updatedUser.tokenBalance.getValue()

                    // Refresh rewards after redemption
                    loadRewards(userId)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Reward redeemed successfully!",
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false,
                    )
                }
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Checks if a reward can be redeemed by the current user.
     */
    fun canRedeemReward(rewardId: String, callback: (RedemptionValidation) -> Unit) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                val result = rewardRedemptionUseCase.canRedeemReward(rewardId, userId)
                result.onSuccess { validation ->
                    callback(validation)
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Refreshes the reward data.
     */
    fun refreshRewards() {
        currentUserId?.let { userId ->
            loadRewards(userId)
        }
    }

    /**
     * Clears any error or success messages.
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null,
        )
    }
}

/**
 * UI state for reward screen.
 */
data class RewardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)
