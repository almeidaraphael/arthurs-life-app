package com.arthurslife.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.auth.AuthenticationSessionService
import com.arthurslife.app.domain.common.DomainException
import com.arthurslife.app.domain.reward.Reward
import com.arthurslife.app.domain.reward.RewardCategory
import com.arthurslife.app.domain.reward.usecase.RewardManagementUseCases
import com.arthurslife.app.domain.reward.usecase.RewardStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for caregiver reward management operations in Arthur's Life MVP.
 *
 * This ViewModel handles the state and business logic for caregiver reward management,
 * including creating, editing, deleting custom rewards, and managing reward availability.
 * It provides comprehensive CRUD operations and statistics tracking.
 */
@HiltViewModel
class RewardManagementViewModel
@Inject
constructor(
    private val rewardManagementUseCases: RewardManagementUseCases,
    private val authenticationSessionService: AuthenticationSessionService,
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(RewardManagementUiState())
    val uiState: StateFlow<RewardManagementUiState> = _uiState.asStateFlow()

    // Reward Lists
    private val _allRewards = MutableStateFlow<List<Reward>>(emptyList())
    val allRewards: StateFlow<List<Reward>> = _allRewards.asStateFlow()

    private val _customRewards = MutableStateFlow<List<Reward>>(emptyList())
    val customRewards: StateFlow<List<Reward>> = _customRewards.asStateFlow()

    private val _predefinedRewards = MutableStateFlow<List<Reward>>(emptyList())
    val predefinedRewards: StateFlow<List<Reward>> = _predefinedRewards.asStateFlow()

    // Statistics
    private val _rewardStats = MutableStateFlow<RewardStats?>(null)
    val rewardStats: StateFlow<RewardStats?> = _rewardStats.asStateFlow()

    // Filter state
    private val _selectedCategory = MutableStateFlow<RewardCategory?>(null)
    val selectedCategory: StateFlow<RewardCategory?> = _selectedCategory.asStateFlow()

    private val _filteredRewards = MutableStateFlow<List<Reward>>(emptyList())
    val filteredRewards: StateFlow<List<Reward>> = _filteredRewards.asStateFlow()

    // Current authenticated user ID
    private var currentUserId: String? = null

    init {
        loadCurrentUser()
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
                currentUser?.let { loadRewards() }
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Loads all rewards and statistics.
     */
    private fun loadRewards() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Load all rewards
                val allRewardsResult = rewardManagementUseCases.getAllRewards()
                allRewardsResult.onSuccess { rewards ->
                    _allRewards.value = rewards
                    updateFilteredRewards(rewards)
                }

                // Load custom rewards
                currentUserId?.let { userId ->
                    val customRewardsResult = rewardManagementUseCases.getCustomRewardsByCreator(
                        userId,
                    )
                    customRewardsResult.onSuccess { customRewards ->
                        _customRewards.value = customRewards
                    }

                    // Load statistics
                    val statsResult = rewardManagementUseCases.getRewardStats(userId)
                    statsResult.onSuccess { stats ->
                        _rewardStats.value = stats
                    }
                }

                // Load predefined rewards
                val allRewards = _allRewards.value
                _predefinedRewards.value = allRewards.filter { !it.isCustom }

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
     * Creates a new custom reward.
     */
    fun createCustomReward(
        title: String,
        description: String,
        category: RewardCategory,
        tokenCost: Int,
        requiresApproval: Boolean = false,
    ) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val result = rewardManagementUseCases.createCustomReward(
                    title = title,
                    description = description,
                    category = category,
                    tokenCost = tokenCost,
                    createdByUserId = userId,
                    requiresApproval = requiresApproval,
                )

                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Custom reward created successfully!",
                    )
                    loadRewards() // Refresh the data
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
     * Updates an existing reward.
     */
    fun updateReward(
        rewardId: String,
        title: String,
        description: String,
        tokenCost: Int,
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val result = rewardManagementUseCases.updateReward(
                    rewardId = rewardId,
                    title = title,
                    description = description,
                    tokenCost = tokenCost,
                )

                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Reward updated successfully!",
                    )
                    loadRewards() // Refresh the data
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
     * Deletes a custom reward.
     */
    fun deleteReward(rewardId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val result = rewardManagementUseCases.deleteReward(rewardId)

                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Reward deleted successfully!",
                    )
                    loadRewards() // Refresh the data
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
     * Updates the availability status of a reward.
     */
    fun updateRewardAvailability(rewardId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                val result = rewardManagementUseCases.updateRewardAvailability(
                    rewardId,
                    isAvailable,
                )

                result.onSuccess {
                    val statusText = if (isAvailable) "enabled" else "disabled"
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Reward $statusText successfully!",
                    )
                    loadRewards() // Refresh the data
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Updates the token cost of a reward.
     */
    fun updateRewardTokenCost(rewardId: String, newTokenCost: Int) {
        viewModelScope.launch {
            try {
                val result = rewardManagementUseCases.updateRewardTokenCost(rewardId, newTokenCost)

                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Token cost updated successfully!",
                    )
                    loadRewards() // Refresh the data
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Filters rewards by category.
     */
    fun filterByCategory(category: RewardCategory?) {
        _selectedCategory.value = category
        updateFilteredRewards(_allRewards.value)
    }

    /**
     * Updates the filtered rewards based on selected category.
     */
    private fun updateFilteredRewards(allRewards: List<Reward>) {
        val filtered = if (_selectedCategory.value != null) {
            allRewards.filter { it.category == _selectedCategory.value }
        } else {
            allRewards
        }
        _filteredRewards.value = filtered
    }

    /**
     * Refreshes all reward data.
     */
    fun refreshRewards() {
        loadRewards()
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
 * UI state for reward management screen.
 */
data class RewardManagementUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)
