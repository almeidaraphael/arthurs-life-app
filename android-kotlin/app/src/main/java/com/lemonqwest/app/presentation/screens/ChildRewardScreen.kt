package com.lemonqwest.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.domain.reward.Reward
import com.lemonqwest.app.domain.reward.RewardCategory
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.components.ThemeAwareRewardBalanceHeader
import com.lemonqwest.app.presentation.theme.components.ThemeAwareRewardCard
import com.lemonqwest.app.presentation.theme.components.ThemeAwareRewardCategoryFilter
import com.lemonqwest.app.presentation.theme.components.ThemeAwareRewardEmptyState
import com.lemonqwest.app.presentation.viewmodels.RewardViewModel

/**
 * Child reward screen for browsing and redeeming rewards.
 *
 * This screen provides a child-friendly interface for viewing available rewards,
 * checking token balance, filtering by category, and redeeming rewards.
 * It supports both Mario Classic and Material themes with appropriate terminology.
 */
@Composable
fun ChildRewardScreen(
    currentTheme: BaseAppTheme,
    viewModel: RewardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val availableRewards by viewModel.availableRewards.collectAsState()
    val redeemableRewards by viewModel.redeemableRewards.collectAsState()
    val tokenBalance by viewModel.tokenBalance.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Refresh token balance when screen becomes visible
    LaunchedEffect(Unit) {
        viewModel.refreshTokenBalance()
    }

    // UI State organization
    val childRewardUiState = ChildRewardUiState(
        currentTheme = currentTheme,
        availableRewards = availableRewards,
        redeemableRewards = redeemableRewards,
        tokenBalance = tokenBalance,
        selectedCategory = selectedCategory,
        isLoading = uiState.isLoading,
        error = uiState.error,
        successMessage = uiState.successMessage,
    )

    // Actions
    val actions = ChildRewardActions(
        onRedeemReward = { rewardId -> viewModel.redeemReward(rewardId) },
        onCategorySelected = { category -> viewModel.filterByCategory(category) },
        onRefresh = { viewModel.refreshRewards() },
        onClearMessages = { viewModel.clearMessages() },
    )

    // Handle messages
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short,
            )
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
            )
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = currentTheme.colorScheme.background,
    ) { paddingValues ->
        ChildRewardScreenContent(
            uiState = childRewardUiState,
            actions = actions,
            paddingValues = paddingValues,
        )
    }
}

/**
 * Main content of the child reward screen.
 */
@Composable
private fun ChildRewardScreenContent(
    uiState: ChildRewardUiState,
    actions: ChildRewardActions,
    paddingValues: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = uiState.currentTheme.colorScheme.primary,
            )
        } else {
            ChildRewardList(uiState = uiState, actions = actions)
        }
    }
}

/**
 * Lazy column with reward list content.
 */
@Composable
private fun ChildRewardList(
    uiState: ChildRewardUiState,
    actions: ChildRewardActions,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header section
        rewardScreenHeader(uiState.currentTheme)

        // Token balance section
        tokenBalanceSection(uiState.currentTheme, uiState.tokenBalance)

        // Category filter section
        categoryFilterSection(uiState, actions)

        // Rewards section
        if (uiState.availableRewards.isEmpty()) {
            emptyStateSection(uiState.currentTheme)
        } else {
            rewardsSection(uiState, actions)
        }
    }
}

/**
 * Header section with title and description.
 */
private fun LazyListScope.rewardScreenHeader(currentTheme: BaseAppTheme) {
    item {
        Column {
            Text(
                text = if (currentTheme.taskLabel == "Quests") "Treasure Chest" else "Rewards",
                style = currentTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = currentTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentTheme.taskLabel == "Quests") {
                    "Use your coins to unlock amazing treasures!"
                } else {
                    "Use your tokens to unlock exciting rewards!"
                },
                style = currentTheme.typography.bodyLarge,
                color = currentTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
        }
    }
}

/**
 * Token balance section.
 */
private fun LazyListScope.tokenBalanceSection(
    currentTheme: BaseAppTheme,
    tokenBalance: Int,
) {
    item {
        ThemeAwareRewardBalanceHeader(
            tokenBalance = tokenBalance,
            theme = currentTheme,
        )
    }
}

/**
 * Category filter section.
 */
private fun LazyListScope.categoryFilterSection(
    uiState: ChildRewardUiState,
    actions: ChildRewardActions,
) {
    item {
        Column {
            Text(
                text = "Categories",
                style = uiState.currentTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = uiState.currentTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ThemeAwareRewardCategoryFilter(
                categories = RewardCategory.entries,
                selectedCategory = uiState.selectedCategory,
                theme = uiState.currentTheme,
                onCategorySelected = actions.onCategorySelected,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * Rewards section with available rewards.
 */
private fun LazyListScope.rewardsSection(
    uiState: ChildRewardUiState,
    actions: ChildRewardActions,
) {
    item {
        Text(
            text = "Available ${if (uiState.currentTheme.taskLabel == "Quests") "Treasures" else "Rewards"}",
            style = uiState.currentTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = uiState.currentTheme.colorScheme.onBackground,
        )
    }

    items(
        items = uiState.availableRewards,
        key = { reward -> reward.id },
    ) { reward ->
        ThemeAwareRewardCard(
            reward = reward,
            theme = uiState.currentTheme,
            userTokenBalance = uiState.tokenBalance,
            onRedeemClick = actions.onRedeemReward,
        )
    }
}

/**
 * Empty state section when no rewards are available.
 */
private fun LazyListScope.emptyStateSection(currentTheme: BaseAppTheme) {
    item {
        ThemeAwareRewardEmptyState(
            theme = currentTheme,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
        )
    }
}

/**
 * UI state data class for child reward screen.
 */
data class ChildRewardUiState(
    val currentTheme: BaseAppTheme,
    val availableRewards: List<Reward>,
    val redeemableRewards: List<Reward>,
    val tokenBalance: Int,
    val selectedCategory: RewardCategory?,
    val isLoading: Boolean,
    val error: String?,
    val successMessage: String?,
)

/**
 * Actions data class for child reward screen.
 */
data class ChildRewardActions(
    val onRedeemReward: (String) -> Unit,
    val onCategorySelected: (RewardCategory?) -> Unit,
    val onRefresh: () -> Unit,
    val onClearMessages: () -> Unit,
)
