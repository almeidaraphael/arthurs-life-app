package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareChildSelectorHeader(
    currentTheme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.colorScheme.primaryContainer,
        ),
        shape = currentTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            themeAwareChildSelectorSection(currentTheme = currentTheme)
            themeAwareCaregiverRoleLabel(currentTheme = currentTheme)
        }
    }
}

@Composable
private fun themeAwareChildSelectorSection(currentTheme: BaseAppTheme) {
    Column {
        Text(
            text = if (currentTheme.displayName.contains("Mario", ignoreCase = true)) "Supervising" else "Managing",
            style = MaterialTheme.typography.titleSmall,
            color = currentTheme.colorScheme.onPrimaryContainer,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemeAwareAvatar(
                theme = currentTheme,
                size = 32.dp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Arthur",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun themeAwareCaregiverRoleLabel(currentTheme: BaseAppTheme) {
    Text(
        text = if (currentTheme.displayName.contains("Mario", ignoreCase = true)) "Castle Guardian" else "Caregiver",
        style = MaterialTheme.typography.titleMedium,
        color = currentTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
fun ThemeAwareChildOverviewCard(
    currentTheme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = currentTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = if (currentTheme.displayName.contains(
                        "Mario",
                        ignoreCase = true,
                    )
                ) {
                    "Arthur's Adventure"
                } else {
                    "Arthur's Overview"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            themeAwareOverviewStatsRow(currentTheme = currentTheme)

            Spacer(modifier = Modifier.height(16.dp))

            themeAwareTasksCompletionSection(currentTheme = currentTheme)
        }
    }
}

@Composable
private fun themeAwareOverviewStatsRow(currentTheme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        themeAwareTokenBalanceColumn(currentTheme = currentTheme)
        themeAwareWeeklyProgressColumn(currentTheme = currentTheme)
    }
}

@Composable
private fun RowScope.themeAwareTokenBalanceColumn(currentTheme: BaseAppTheme) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.TOKEN,
                theme = currentTheme,
                tint = currentTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
                contentDescription = currentTheme.displayName + " Token",
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "85",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = "token",
            style = MaterialTheme.typography.bodySmall,
            color = currentTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RowScope.themeAwareWeeklyProgressColumn(currentTheme: BaseAppTheme) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.PROGRESS_INDICATOR,
                theme = currentTheme,
                tint = currentTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
                contentDescription = "Progress",
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "+15%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.primary,
            )
        }
        Text(
            text = if (currentTheme.displayName.contains("Mario", ignoreCase = true)) "this adventure" else "this week",
            style = MaterialTheme.typography.bodySmall,
            color = currentTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun themeAwareTasksCompletionSection(currentTheme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "Quests: 12/15"
            } else {
                "Tasks: 12/15"
            },
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "On track for kingdom goal"
            } else {
                "On track for weekly goal"
            },
            style = MaterialTheme.typography.bodySmall,
            color = currentTheme.colorScheme.primary,
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    ThemeAwareProgressIndicator(
        progress = 0.8f,
        theme = currentTheme,
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
    )
}

@Composable
fun ThemeAwareCaregiverQuickActions(
    currentTheme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = currentTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = if (currentTheme.displayName.contains(
                        "Mario",
                        ignoreCase = true,
                    )
                ) {
                    "Castle Commands"
                } else {
                    "Quick Actions"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))

            caregiverPrimaryActions(currentTheme = currentTheme)
            Spacer(modifier = Modifier.height(8.dp))
            caregiverSecondaryActions(currentTheme = currentTheme)
        }
    }
}

@Composable
private fun caregiverPrimaryActions(currentTheme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        caregiverAddTaskButton(currentTheme = currentTheme)
        caregiverManageRewardsButton(currentTheme = currentTheme)
    }
}

@Composable
private fun RowScope.caregiverAddTaskButton(currentTheme: BaseAppTheme) {
    Button(
        onClick = { /* Navigate to add task */ },
        modifier = Modifier.weight(1f),
        shape = currentTheme.shapes.small,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.START_TASK,
            theme = currentTheme,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            if (currentTheme.displayName.contains("Mario", ignoreCase = true)) "Add Quest" else "Add Task",
        )
    }
}

@Composable
private fun RowScope.caregiverManageRewardsButton(currentTheme: BaseAppTheme) {
    OutlinedButton(
        onClick = { /* Navigate to manage rewards */ },
        modifier = Modifier.weight(1f),
        shape = currentTheme.shapes.small,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.REWARDS,
            theme = currentTheme,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            if (currentTheme.displayName.contains("Mario", ignoreCase = true)) "Manage Shop" else "Manage Rewards",
        )
    }
}

@Composable
private fun caregiverSecondaryActions(currentTheme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        caregiverReportButton(currentTheme = currentTheme)
        caregiverSettingsButton(currentTheme = currentTheme)
    }
}

@Composable
private fun RowScope.caregiverReportButton(currentTheme: BaseAppTheme) {
    OutlinedButton(
        onClick = { /* Navigate to weekly report */ },
        modifier = Modifier.weight(1f),
        shape = currentTheme.shapes.small,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.PROGRESS_INDICATOR,
            theme = currentTheme,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            if (currentTheme.displayName.contains("Mario", ignoreCase = true)) "Adventure Log" else "Weekly Report",
        )
    }
}

@Composable
private fun RowScope.caregiverSettingsButton(currentTheme: BaseAppTheme) {
    OutlinedButton(
        onClick = { /* Navigate to child settings */ },
        modifier = Modifier.weight(1f),
        shape = currentTheme.shapes.small,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.AVATAR,
            theme = currentTheme,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            if (currentTheme.displayName.contains("Mario", ignoreCase = true)) "Player Settings" else "Child Settings",
        )
    }
}

@Composable
fun ThemeAwareWeeklyProgressCard(
    currentTheme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.colorScheme.secondaryContainer,
        ),
        shape = currentTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = "Weekly Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.onSecondaryContainer,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "⭐ 85 tokens",
                    style = MaterialTheme.typography.bodyLarge,
                    color = currentTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = "📈 +15% this week",
                    style = MaterialTheme.typography.bodyLarge,
                    color = currentTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "✅ 12/15 tasks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = currentTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = "🎯 On track for weekly goal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = currentTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun ThemeAwareWishlistInsightsCard(
    currentTheme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.colorScheme.tertiaryContainer,
        ),
        shape = currentTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = "Wishlist Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "🚲 Next Goal: New Bike (24% progress)",
                style = MaterialTheme.typography.bodyLarge,
                color = currentTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "💡 Gift Ideas: 3 affordable items in wishlist",
                style = MaterialTheme.typography.bodyMedium,
                color = currentTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}
