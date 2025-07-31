package com.lemonqwest.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.domain.reward.Reward
import com.lemonqwest.app.domain.reward.RewardCategory
import com.lemonqwest.app.domain.reward.usecase.RewardStats
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.components.ThemeAwareAlertDialog
import com.lemonqwest.app.presentation.theme.components.ThemeAwareButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareCard
import com.lemonqwest.app.presentation.theme.components.ThemeAwareDialogTextButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareFloatingActionButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareIconButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareOutlinedTextField
import com.lemonqwest.app.presentation.theme.components.ThemeAwareRewardCategoryFilter
import com.lemonqwest.app.presentation.theme.components.ThemeAwareTextButton
import com.lemonqwest.app.presentation.viewmodels.RewardManagementUiState
import com.lemonqwest.app.presentation.viewmodels.RewardManagementViewModel

/**
 * Data class to group reward list parameters.
 */
private data class RewardListData(
    val filteredRewards: List<Reward>,
    val customRewards: List<Reward>,
    val predefinedRewards: List<Reward>,
    val rewardStats: RewardStats?,
    val selectedCategory: RewardCategory?,
)

/**
 * Data class to group reward actions.
 */
private data class RewardActions(
    val onCategorySelected: (RewardCategory?) -> Unit,
    val onEditReward: (Reward) -> Unit,
    val onDeleteReward: (Reward) -> Unit,
    val onToggleAvailability: (String, Boolean) -> Unit,
)

/**
 * Data class to group dialog content parameters.
 */
private data class RewardDialogState(
    val title: String,
    val description: String,
    val selectedCategory: RewardCategory,
    val tokenCost: Int,
    val requiresApproval: Boolean,
    val expanded: Boolean,
    val isEditing: Boolean,
)

/**
 * Data class to group dialog callbacks.
 */
private data class RewardDialogCallbacks(
    val onTitleChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onCategoryChange: (RewardCategory) -> Unit,
    val onTokenCostChange: (Int) -> Unit,
    val onRequiresApprovalChange: (Boolean) -> Unit,
    val onExpandedChange: (Boolean) -> Unit,
)

/**
 * Data class to group reward save parameters.
 */
private data class RewardSaveData(
    val title: String,
    val description: String,
    val category: RewardCategory,
    val tokenCost: Int,
    val requiresApproval: Boolean,
)

/**
 * Data class to group dialog state parameters.
 */
private data class DialogState(
    val showCreateDialog: Boolean,
    val editingReward: Reward?,
    val deletingReward: Reward?,
)

/**
 * Data class to group category selector parameters.
 */
private data class CategorySelectorData(
    val selectedCategory: RewardCategory,
    val expanded: Boolean,
    val onCategoryChange: (RewardCategory) -> Unit,
    val onTokenCostChange: (Int) -> Unit,
    val onExpandedChange: (Boolean) -> Unit,
    val theme: BaseAppTheme,
)

/**
 * Data class to manage reward dialog state.
 */
private data class RewardDialogStateManager(
    val dialogState: RewardDialogState,
    val callbacks: RewardDialogCallbacks,
    val canSave: Boolean,
    val isEditing: Boolean,
    val performSave: ((String, String, RewardCategory, Int, Boolean) -> Unit) -> Unit,
)

/**
 * Caregiver reward management screen for CRUD operations on rewards.
 *
 * This screen provides a Material Design interface for caregivers to manage rewards,
 * including creating custom rewards, editing existing ones, and managing availability.
 */
@Composable
fun CaregiverRewardManagementScreen(
    currentTheme: BaseAppTheme,
    viewModel: RewardManagementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredRewards by viewModel.filteredRewards.collectAsState()
    val customRewards by viewModel.customRewards.collectAsState()
    val predefinedRewards by viewModel.predefinedRewards.collectAsState()
    val rewardStats by viewModel.rewardStats.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingReward by remember { mutableStateOf<Reward?>(null) }
    var deletingReward by remember { mutableStateOf<Reward?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle messages
    RewardManagementMessageHandling(uiState, snackbarHostState, viewModel)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            RewardManagementFab(currentTheme) { showCreateDialog = true }
        },
        containerColor = currentTheme.colorScheme.background,
    ) { paddingValues ->
        CaregiverRewardManagementContent(
            currentTheme = currentTheme,
            uiState = uiState,
            rewardData = RewardListData(
                filteredRewards = filteredRewards,
                customRewards = customRewards,
                predefinedRewards = predefinedRewards,
                rewardStats = rewardStats,
                selectedCategory = selectedCategory,
            ),
            actions = RewardActions(
                onCategorySelected = { viewModel.filterByCategory(it) },
                onEditReward = { editingReward = it },
                onDeleteReward = { deletingReward = it },
                onToggleAvailability = { rewardId, isAvailable ->
                    viewModel.updateRewardAvailability(rewardId, isAvailable)
                },
            ),
            paddingValues = paddingValues,
        )
    }

    // Dialogs
    RewardManagementDialogs(
        dialogState = DialogState(
            showCreateDialog = showCreateDialog,
            editingReward = editingReward,
            deletingReward = deletingReward,
        ),
        currentTheme = currentTheme,
        viewModel = viewModel,
        onDismissCreateDialog = {
            showCreateDialog = false
            editingReward = null
        },
        onDismissDeleteDialog = { deletingReward = null },
    )
    ThemeAwareDialogTextButton(text = TODO(), onClick = TODO(), theme = TODO())
}

@Composable
private fun RewardManagementDialogs(
    dialogState: DialogState,
    currentTheme: BaseAppTheme,
    viewModel: RewardManagementViewModel,
    onDismissCreateDialog: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
) {
    // Create/Edit Dialog
    if (dialogState.showCreateDialog || dialogState.editingReward != null) {
        RewardCreateEditDialog(
            reward = dialogState.editingReward,
            theme = currentTheme,
            onDismiss = onDismissCreateDialog,
            onSave = { title, description, category, tokenCost, requiresApproval ->
                handleRewardSave(
                    dialogState.editingReward,
                    RewardSaveData(title, description, category, tokenCost, requiresApproval),
                    viewModel,
                )
                onDismissCreateDialog()
            },
        )
    }

    // Delete confirmation dialog
    dialogState.deletingReward?.let { reward ->
        RewardDeleteDialog(
            reward = reward,
            theme = currentTheme,
            onConfirm = {
                viewModel.deleteReward(reward.id)
                onDismissDeleteDialog()
            },
            onDismiss = onDismissDeleteDialog,
        )
    }
}

/**
 * Main content of the caregiver reward management screen.
 */
@Composable
private fun CaregiverRewardManagementContent(
    currentTheme: BaseAppTheme,
    uiState: RewardManagementUiState,
    rewardData: RewardListData,
    actions: RewardActions,
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
                color = currentTheme.colorScheme.primary,
            )
        } else {
            CaregiverRewardList(
                currentTheme = currentTheme,
                rewardData = rewardData,
                actions = actions,
            )
        }
    }
}

/**
 * Lazy column with reward management content.
 */
@Composable
private fun CaregiverRewardList(
    currentTheme: BaseAppTheme,
    rewardData: RewardListData,
    actions: RewardActions,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header section
        rewardManagementHeader(currentTheme)

        // Statistics section
        rewardData.rewardStats?.let {
            statisticsSection(currentTheme, it)
        }

        // Category filter section
        categoryFilterSection(currentTheme, rewardData.selectedCategory, actions.onCategorySelected)

        // Rewards sections
        if (rewardData.filteredRewards.isEmpty()) {
            emptyStateSection(currentTheme)
        } else {
            rewardsSection(
                currentTheme = currentTheme,
                filteredRewards = rewardData.filteredRewards,
                onEditReward = actions.onEditReward,
                onDeleteReward = actions.onDeleteReward,
                onToggleAvailability = actions.onToggleAvailability,
            )
        }
    }
}

/**
 * Header section with title and description.
 */
private fun LazyListScope.rewardManagementHeader(currentTheme: BaseAppTheme) {
    item {
        Column {
            Text(
                text = "Reward Management",
                style = currentTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = currentTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create and manage custom rewards for children",
                style = currentTheme.typography.bodyLarge,
                color = currentTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
        }
    }
}

/**
 * Statistics section.
 */
private fun LazyListScope.statisticsSection(
    currentTheme: BaseAppTheme,
    stats: RewardStats,
) {
    item {
        ThemeAwareCard(
            theme = currentTheme,
            modifier = Modifier.fillMaxWidth(),
            containerColor = currentTheme.colorScheme.primaryContainer,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Reward Statistics",
                    style = currentTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = currentTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatisticItem(
                        "Custom",
                        stats.totalCustomRewards.toString(),
                        currentTheme,
                    )
                    StatisticItem(
                        "Available",
                        stats.availableCustomRewards.toString(),
                        currentTheme,
                    )
                    StatisticItem(
                        "Predefined",
                        stats.totalPredefinedRewards.toString(),
                        currentTheme,
                    )
                    StatisticItem(
                        "Total",
                        stats.totalRewards.toString(),
                        currentTheme,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String,
    currentTheme: BaseAppTheme,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = currentTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = currentTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = label,
            style = currentTheme.typography.bodySmall,
            color = currentTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
        )
    }
}

/**
 * Category filter section.
 */
private fun LazyListScope.categoryFilterSection(
    currentTheme: BaseAppTheme,
    selectedCategory: RewardCategory?,
    onCategorySelected: (RewardCategory?) -> Unit,
) {
    item {
        Column {
            Text(
                text = "Filter by Category",
                style = currentTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = currentTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ThemeAwareRewardCategoryFilter(
                categories = RewardCategory.entries,
                selectedCategory = selectedCategory,
                theme = currentTheme,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * Rewards section with management cards.
 */
private fun LazyListScope.rewardsSection(
    currentTheme: BaseAppTheme,
    filteredRewards: List<Reward>,
    onEditReward: (Reward) -> Unit,
    onDeleteReward: (Reward) -> Unit,
    onToggleAvailability: (String, Boolean) -> Unit,
) {
    item {
        Text(
            text = "Rewards",
            style = currentTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = currentTheme.colorScheme.onBackground,
        )
    }

    items(
        items = filteredRewards,
        key = { reward -> reward.id },
    ) { reward ->
        RewardManagementCard(
            reward = reward,
            theme = currentTheme,
            onEditReward = onEditReward,
            onDeleteReward = onDeleteReward,
            onToggleAvailability = onToggleAvailability,
        )
    }
}

/**
 * Individual reward management card.
 */
@Composable
private fun RewardManagementCard(
    reward: Reward,
    theme: BaseAppTheme,
    onEditReward: (Reward) -> Unit,
    onDeleteReward: (Reward) -> Unit,
    onToggleAvailability: (String, Boolean) -> Unit,
) {
    ThemeAwareCard(
        theme = theme,
        modifier = Modifier.fillMaxWidth(),
        containerColor = if (reward.isAvailable) {
            theme.colorScheme.surface
        } else {
            theme.colorScheme.surfaceVariant
        },
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            RewardCardHeader(reward, theme, onEditReward, onDeleteReward)

            Spacer(modifier = Modifier.height(8.dp))

            RewardCardDescription(reward, theme)

            Spacer(modifier = Modifier.height(12.dp))

            RewardCardAvailabilityToggle(reward, theme, onToggleAvailability)
        }
    }
}

/**
 * Header section of reward card with title and actions.
 */
@Composable
private fun RewardCardHeader(
    reward: Reward,
    theme: BaseAppTheme,
    onEditReward: (Reward) -> Unit,
    onDeleteReward: (Reward) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reward.title,
                style = theme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = theme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            RewardCardSubtitle(reward, theme)
        }

        if (reward.isCustom) {
            RewardCardActions(reward, theme, onEditReward, onDeleteReward)
        }
    }
}

/**
 * Subtitle section with category and token info.
 */
@Composable
private fun RewardCardSubtitle(
    reward: Reward,
    theme: BaseAppTheme,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${reward.category.emoji} ${reward.category.displayName}",
            style = theme.typography.bodySmall,
            color = theme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        Text(
            text = "•",
            style = theme.typography.bodySmall,
            color = theme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        Text(
            text = "${reward.tokenCost} tokens",
            style = theme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = theme.colorScheme.primary,
        )
        if (reward.isCustom) {
            Text(
                text = "•",
                style = theme.typography.bodySmall,
                color = theme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Text(
                text = "Custom",
                style = theme.typography.bodySmall,
                color = theme.colorScheme.secondary,
            )
        }
    }
}

/**
 * Action buttons for custom rewards.
 */
@Composable
private fun RewardCardActions(
    reward: Reward,
    theme: BaseAppTheme,
    onEditReward: (Reward) -> Unit,
    onDeleteReward: (Reward) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ThemeAwareIconButton(
            onClick = { onEditReward(reward) },
            modifier = Modifier.size(40.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit reward",
                    tint = theme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            },
        )

        ThemeAwareIconButton(
            onClick = { onDeleteReward(reward) },
            modifier = Modifier.size(40.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete reward",
                    tint = theme.colorScheme.error,
                    modifier = Modifier.size(20.dp),
                )
            },
        )
    }
}

/**
 * Description section of reward card.
 */
@Composable
private fun RewardCardDescription(
    reward: Reward,
    theme: BaseAppTheme,
) {
    Text(
        text = reward.description,
        style = theme.typography.bodyMedium,
        color = theme.colorScheme.onSurface.copy(alpha = 0.8f),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

/**
 * Availability toggle section of reward card.
 */
@Composable
private fun RewardCardAvailabilityToggle(
    reward: Reward,
    theme: BaseAppTheme,
    onToggleAvailability: (String, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (reward.isAvailable) "Available for redemption" else "Disabled",
            style = theme.typography.bodySmall,
            color = if (reward.isAvailable) {
                theme.colorScheme.primary
            } else {
                theme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
        )

        Switch(
            checked = reward.isAvailable,
            onCheckedChange = { isChecked ->
                onToggleAvailability(reward.id, isChecked)
            },
        )
    }
}

/**
 * Empty state section when no rewards are available.
 */
private fun LazyListScope.emptyStateSection(currentTheme: BaseAppTheme) {
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "No Rewards Found",
                    style = currentTheme.typography.titleMedium,
                    color = currentTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create your first custom reward using the + button",
                    style = currentTheme.typography.bodyMedium,
                    color = currentTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                )
            }
        }
    }
}

/**
 * Create/Edit reward dialog.
 */
@Composable
private fun RewardCreateEditDialog(
    reward: Reward?,
    theme: BaseAppTheme,
    onDismiss: () -> Unit,
    onSave: (String, String, RewardCategory, Int, Boolean) -> Unit,
) {
    val dialogStateManager = createRewardDialogStateManager(reward)

    ThemeAwareAlertDialog(
        onDismissRequest = onDismiss,
        theme = theme,
        title = {
            RewardDialogTitle(dialogStateManager.isEditing, theme)
        },
        text = {
            RewardDialogContent(
                dialogState = dialogStateManager.dialogState,
                theme = theme,
                callbacks = dialogStateManager.callbacks,
            )
        },
        confirmButton = {
            RewardDialogConfirmButton(
                canSave = dialogStateManager.canSave,
                isEditing = dialogStateManager.isEditing,
                theme = theme,
                onSave = {
                    dialogStateManager.performSave(onSave)
                },
            )
        },
        dismissButton = {
            RewardDialogDismissButton(theme, onDismiss)
        },
    )
}

@Composable
private fun RewardDialogTitle(isEditing: Boolean, theme: BaseAppTheme) {
    Text(
        text = if (isEditing) "Edit Reward" else "Create Custom Reward",
        color = theme.colorScheme.onSurface,
    )
}

@Composable
private fun createRewardDialogStateManager(reward: Reward?): RewardDialogStateManager {
    var title by remember { mutableStateOf(reward?.title ?: "") }
    var description by remember { mutableStateOf(reward?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(reward?.category ?: RewardCategory.TREATS) }
    var tokenCost by remember { mutableIntStateOf(reward?.tokenCost ?: DEFAULT_TOKEN_COST) }
    var requiresApproval by remember { mutableStateOf(reward?.requiresApproval ?: false) }
    var expanded by remember { mutableStateOf(false) }

    val isEditing = reward != null
    val canSave = title.isNotBlank() && description.isNotBlank() && tokenCost > 0

    return RewardDialogStateManager(
        dialogState = RewardDialogState(
            title = title,
            description = description,
            selectedCategory = selectedCategory,
            tokenCost = tokenCost,
            requiresApproval = requiresApproval,
            expanded = expanded,
            isEditing = isEditing,
        ),
        callbacks = RewardDialogCallbacks(
            onTitleChange = { title = it },
            onDescriptionChange = { description = it },
            onCategoryChange = { selectedCategory = it },
            onTokenCostChange = { tokenCost = it },
            onRequiresApprovalChange = { requiresApproval = it },
            onExpandedChange = { expanded = it },
        ),
        canSave = canSave,
        isEditing = isEditing,
        performSave = { onSave ->
            onSave(
                title.trim(),
                description.trim(),
                selectedCategory,
                tokenCost,
                requiresApproval,
            )
        },
    )
}

@Composable
private fun RewardDialogConfirmButton(
    canSave: Boolean,
    isEditing: Boolean,
    theme: BaseAppTheme,
    onSave: () -> Unit,
) {
    ThemeAwareButton(
        text = if (isEditing) "Update" else "Create",
        onClick = {
            if (canSave) {
                onSave()
            }
        },
        theme = theme,
        enabled = canSave,
    )
}

@Composable
private fun RewardDialogDismissButton(
    theme: BaseAppTheme,
    onDismiss: () -> Unit,
) {
    ThemeAwareTextButton(
        text = "Cancel",
        onClick = onDismiss,
        theme = theme,
    )
}

/**
 * Content of the reward dialog.
 */
@Composable
private fun RewardDialogContent(
    dialogState: RewardDialogState,
    theme: BaseAppTheme,
    callbacks: RewardDialogCallbacks,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RewardDialogTitleField(dialogState.title, callbacks.onTitleChange, theme)
        RewardDialogDescriptionField(dialogState.description, callbacks.onDescriptionChange, theme)

        if (!dialogState.isEditing) {
            RewardDialogCategorySelector(
                CategorySelectorData(
                    selectedCategory = dialogState.selectedCategory,
                    expanded = dialogState.expanded,
                    onCategoryChange = callbacks.onCategoryChange,
                    onTokenCostChange = callbacks.onTokenCostChange,
                    onExpandedChange = callbacks.onExpandedChange,
                    theme = theme,
                ),
            )
        }

        RewardDialogTokenCostField(
            dialogState.tokenCost,
            dialogState.selectedCategory,
            callbacks.onTokenCostChange,
            theme,
        )

        if (!dialogState.isEditing) {
            RewardDialogApprovalToggle(
                dialogState.requiresApproval,
                theme,
                callbacks.onRequiresApprovalChange,
            )
        }
    }
}

@Composable
private fun RewardDialogTitleField(
    title: String,
    onTitleChange: (String) -> Unit,
    theme: BaseAppTheme,
) {
    ThemeAwareOutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        theme = theme,
        label = "Reward Title",
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun RewardDialogDescriptionField(
    description: String,
    onDescriptionChange: (String) -> Unit,
    theme: BaseAppTheme,
) {
    ThemeAwareOutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        theme = theme,
        label = "Description",
        maxLines = 3,
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RewardDialogCategorySelector(data: CategorySelectorData) {
    ExposedDropdownMenuBox(
        expanded = data.expanded,
        onExpandedChange = data.onExpandedChange,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ThemeAwareOutlinedTextField(
            value = "${data.selectedCategory.emoji} ${data.selectedCategory.displayName}",
            onValueChange = { },
            theme = data.theme,
            label = "Category",
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = data.expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
        )

        ExposedDropdownMenu(
            expanded = data.expanded,
            onDismissRequest = { data.onExpandedChange(false) },
        ) {
            RewardCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text("${category.emoji} ${category.displayName}")
                    },
                    onClick = {
                        data.onCategoryChange(category)
                        data.onTokenCostChange(category.minTokenCost)
                        data.onExpandedChange(false)
                    },
                )
            }
        }
    }
}

@Composable
private fun RewardDialogTokenCostField(
    tokenCost: Int,
    selectedCategory: RewardCategory,
    onTokenCostChange: (Int) -> Unit,
    theme: BaseAppTheme,
) {
    ThemeAwareOutlinedTextField(
        value = tokenCost.toString(),
        onValueChange = { newValue ->
            newValue.toIntOrNull()?.let { cost ->
                if (cost >= 0) onTokenCostChange(cost)
            }
        },
        theme = theme,
        label = "Token Cost (${selectedCategory.minTokenCost}-${selectedCategory.maxTokenCost})",
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun RewardDialogApprovalToggle(
    requiresApproval: Boolean,
    theme: BaseAppTheme,
    onRequiresApprovalChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Requires approval",
            style = theme.typography.bodyMedium,
        )
        Switch(
            checked = requiresApproval,
            onCheckedChange = onRequiresApprovalChange,
        )
    }
}

@Composable
private fun RewardManagementMessageHandling(
    uiState: RewardManagementUiState,
    snackbarHostState: SnackbarHostState,
    viewModel: RewardManagementViewModel,
) {
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
}

@Composable
private fun RewardManagementFab(
    theme: BaseAppTheme,
    onClick: () -> Unit,
) {
    ThemeAwareFloatingActionButton(
        onClick = onClick,
        theme = theme,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Create Custom Reward",
            tint = theme.colorScheme.onPrimary,
        )
    }
}

private fun handleRewardSave(
    editingReward: Reward?,
    saveData: RewardSaveData,
    viewModel: RewardManagementViewModel,
) {
    if (editingReward != null) {
        viewModel.updateReward(
            rewardId = editingReward.id,
            title = saveData.title,
            description = saveData.description,
            tokenCost = saveData.tokenCost,
        )
    } else {
        viewModel.createCustomReward(
            title = saveData.title,
            description = saveData.description,
            category = saveData.category,
            tokenCost = saveData.tokenCost,
            requiresApproval = saveData.requiresApproval,
        )
    }
}

@Composable
private fun RewardDeleteDialog(
    reward: Reward,
    theme: BaseAppTheme,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ThemeAwareAlertDialog(
        onDismissRequest = onDismiss,
        theme = theme,
        title = {
            Text(
                text = "Delete Reward",
                color = theme.colorScheme.onSurface,
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete \"${reward.title}\"? This action cannot be undone.",
                color = theme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            ThemeAwareButton(
                text = "Delete",
                onClick = onConfirm,
                theme = theme,
            )
        },
        dismissButton = {
            ThemeAwareTextButton(
                text = "Cancel",
                onClick = onDismiss,
                theme = theme,
            )
        },
    )
}

private const val DEFAULT_TOKEN_COST = 10
