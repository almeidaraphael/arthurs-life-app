package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arthurslife.app.domain.reward.Reward
import com.arthurslife.app.domain.reward.RewardCategory
import com.arthurslife.app.presentation.theme.BaseAppTheme

/**
 * Theme-aware reward card component for displaying individual rewards.
 * Supports both Mario Classic and Material themes with appropriate styling.
 */
@Composable
fun themeAwareRewardCard(
    reward: Reward,
    theme: BaseAppTheme,
    userTokenBalance: Int,
    onRedeemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val canAfford = userTokenBalance >= reward.tokenCost
    val canRedeem = reward.isAvailable && !reward.requiresApproval && canAfford

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getRewardCardColor(canRedeem, canAfford, theme),
        ),
        shape = theme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        rewardCardContent(
            reward = reward,
            theme = theme,
            canAfford = canAfford,
            canRedeem = canRedeem,
            onRedeemClick = onRedeemClick,
        )
    }
}

@Composable
private fun rewardCardContent(
    reward: Reward,
    theme: BaseAppTheme,
    canAfford: Boolean,
    canRedeem: Boolean,
    onRedeemClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        rewardCardHeader(reward, theme, canAfford)
        Spacer(modifier = Modifier.height(12.dp))
        rewardCardText(reward, theme)
        Spacer(modifier = Modifier.height(16.dp))
        themeAwareRewardActionButton(
            reward = reward,
            canRedeem = canRedeem,
            canAfford = canAfford,
            theme = theme,
            onRedeemClick = onRedeemClick,
        )
    }
}

@Composable
private fun rewardCardHeader(reward: Reward, theme: BaseAppTheme, canAfford: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        themeAwareRewardCategoryBadge(
            category = reward.category,
            theme = theme,
        )
        themeAwareTokenCost(
            cost = reward.tokenCost,
            canAfford = canAfford,
            theme = theme,
        )
    }
}

@Composable
private fun rewardCardText(reward: Reward, theme: BaseAppTheme) {
    Text(
        text = reward.title,
        style = theme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
        ),
        color = theme.colorScheme.onPrimaryContainer,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = reward.description,
        style = theme.typography.bodyMedium,
        color = theme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun getRewardCardColor(canRedeem: Boolean, canAfford: Boolean, theme: BaseAppTheme) = when {
    canRedeem -> theme.colorScheme.primaryContainer
    canAfford -> theme.colorScheme.secondaryContainer
    else -> theme.colorScheme.surfaceVariant
}

/**
 * Theme-aware reward category badge component.
 */
@Composable
fun themeAwareRewardCategoryBadge(
    category: RewardCategory,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.tertiary,
        ),
        shape = theme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = category.emoji,
                style = theme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category.displayName,
                style = theme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = theme.colorScheme.onTertiary,
            )
        }
    }
}

/**
 * Theme-aware token cost display component.
 */
@Composable
fun themeAwareTokenCost(
    cost: Int,
    canAfford: Boolean,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.TOKEN,
            theme = theme,
            modifier = Modifier.size(20.dp),
            tint = if (canAfford) {
                theme.colorScheme.primary
            } else {
                theme.colorScheme.error
            },
            contentDescription = "Token cost",
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = cost.toString(),
            style = theme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = if (canAfford) {
                theme.colorScheme.primary
            } else {
                theme.colorScheme.error
            },
        )
    }
}

/**
 * Theme-aware reward action button component.
 */
@Composable
fun themeAwareRewardActionButton(
    reward: Reward,
    canRedeem: Boolean,
    canAfford: Boolean,
    theme: BaseAppTheme,
    onRedeemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        canRedeem -> redeemButton(reward, theme, onRedeemClick, modifier)
        reward.requiresApproval -> approvalButton(reward, theme, canAfford, onRedeemClick, modifier)
        !canAfford -> insufficientTokensButton(theme, modifier)
        !reward.isAvailable -> unavailableButton(theme, modifier)
    }
}

@Composable
private fun redeemButton(
    reward: Reward,
    theme: BaseAppTheme,
    onRedeemClick: (String) -> Unit,
    modifier: Modifier,
) {
    Button(
        onClick = { onRedeemClick(reward.id) },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = theme.colorScheme.primary,
        ),
        shape = theme.shapes.small,
    ) {
        Text(
            text = "Redeem ${if (theme.taskLabel == "Quests") "Treasure" else "Reward"}",
            style = theme.typography.labelLarge,
            color = theme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun approvalButton(
    reward: Reward,
    theme: BaseAppTheme,
    canAfford: Boolean,
    onRedeemClick: (String) -> Unit,
    modifier: Modifier,
) {
    FilledTonalButton(
        onClick = { onRedeemClick(reward.id) },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = theme.colorScheme.secondary,
        ),
        shape = theme.shapes.small,
        enabled = canAfford,
    ) {
        Text(
            text = "Request Approval",
            style = theme.typography.labelLarge,
            color = theme.colorScheme.onSecondary,
        )
    }
}

@Composable
private fun insufficientTokensButton(theme: BaseAppTheme, modifier: Modifier) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = theme.colorScheme.error,
        ),
        shape = theme.shapes.small,
        enabled = false,
    ) {
        Text(
            text = "Not Enough Tokens",
            style = theme.typography.labelLarge,
        )
    }
}

@Composable
private fun unavailableButton(theme: BaseAppTheme, modifier: Modifier) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = theme.colorScheme.onSurfaceVariant,
        ),
        shape = theme.shapes.small,
        enabled = false,
    ) {
        Text(
            text = "Unavailable",
            style = theme.typography.labelLarge,
        )
    }
}

/**
 * Theme-aware reward category filter component.
 */
@Composable
fun themeAwareRewardCategoryFilter(
    categories: List<RewardCategory>,
    selectedCategory: RewardCategory?,
    theme: BaseAppTheme,
    onCategorySelected: (RewardCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // "All" filter button
        item {
            FilledTonalButton(
                onClick = { onCategorySelected(null) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (selectedCategory == null) {
                        theme.colorScheme.primary
                    } else {
                        theme.colorScheme.surfaceVariant
                    },
                ),
                shape = theme.shapes.small,
            ) {
                Text(
                    text = "All",
                    style = theme.typography.labelMedium,
                    color = if (selectedCategory == null) {
                        theme.colorScheme.onPrimary
                    } else {
                        theme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }

        // Category filter buttons
        items(categories) { category ->
            FilledTonalButton(
                onClick = { onCategorySelected(category) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (selectedCategory == category) {
                        theme.colorScheme.primary
                    } else {
                        theme.colorScheme.surfaceVariant
                    },
                ),
                shape = theme.shapes.small,
            ) {
                Text(
                    text = category.emoji,
                    style = theme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = category.displayName,
                    style = theme.typography.labelMedium,
                    color = if (selectedCategory == category) {
                        theme.colorScheme.onPrimary
                    } else {
                        theme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

/**
 * Theme-aware reward balance header component.
 */
@Composable
fun themeAwareRewardBalanceHeader(
    tokenBalance: Int,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.primaryContainer,
        ),
        shape = theme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ThemeAwareIcon(
                    semanticType = SemanticIconType.TOKEN,
                    theme = theme,
                    modifier = Modifier.size(32.dp),
                    tint = if (theme.useOriginalIconColors) null else theme.colorScheme.primary,
                    contentDescription = "Token balance",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tokenBalance.toString(),
                    style = theme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = theme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (theme.taskLabel == "Quests") "Coins Available" else "Tokens Available",
                style = theme.typography.bodyMedium,
                color = theme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Theme-aware empty state component for when no rewards are available.
 */
@Composable
fun themeAwareRewardEmptyState(
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.REWARDS,
                theme = theme,
                modifier = Modifier.size(64.dp),
                tint = theme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                contentDescription = "No rewards",
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (theme.taskLabel == "Quests") "No Treasure Available" else "No Rewards Available",
                style = theme.typography.titleMedium,
                color = theme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (theme.taskLabel == "Quests") {
                    "Complete more quests to unlock treasure!"
                } else {
                    "Complete more tasks to unlock rewards!"
                },
                style = theme.typography.bodyMedium,
                color = theme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
        }
    }
}
