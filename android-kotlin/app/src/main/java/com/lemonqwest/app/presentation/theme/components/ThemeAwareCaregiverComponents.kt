package com.lemonqwest.app.presentation.theme.components

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
import com.lemonqwest.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareChildSelectorHeader(
    currentTheme: BaseAppTheme,
    childName: String = "Arthur", // Default for backward compatibility
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
            ThemeAwareChildSelectorSection(currentTheme = currentTheme, childName = childName)
            ThemeAwareCaregiverRoleLabel(currentTheme = currentTheme)
        }
    }
}

@Composable
private fun ThemeAwareChildSelectorSection(currentTheme: BaseAppTheme, childName: String) {
    Column {
        Text(
            text = if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "Supervising"
            } else {
                "Managing"
            },
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
                text = childName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun ThemeAwareCaregiverRoleLabel(currentTheme: BaseAppTheme) {
    Text(
        text = if (currentTheme.displayName.contains(
                "Mario",
                ignoreCase = true,
            )
        ) {
            "Castle Guardian"
        } else {
            "Caregiver"
        },
        style = MaterialTheme.typography.titleMedium,
        color = currentTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
fun ThemeAwareChildOverviewCard(
    currentTheme: BaseAppTheme,
    tokenBalance: Int = 85, // Default for backward compatibility
    weeklyProgress: String = "+15%", // Default for backward compatibility
    completedTasks: Int = 12,
    totalTasks: Int = 15,
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

            ThemeAwareOverviewStatsRow(
                currentTheme = currentTheme,
                tokenBalance = tokenBalance,
                weeklyProgress = weeklyProgress,
            )

            Spacer(modifier = Modifier.height(16.dp))

            ThemeAwareTasksCompletionSection(
                currentTheme = currentTheme,
                completedTasks = completedTasks,
                totalTasks = totalTasks,
            )
        }
    }
}

@Composable
private fun ThemeAwareOverviewStatsRow(
    currentTheme: BaseAppTheme,
    tokenBalance: Int,
    weeklyProgress: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ThemeAwareTokenBalanceColumn(
            currentTheme = currentTheme,
            tokenBalance = tokenBalance,
        )
        ThemeAwareWeeklyProgressColumn(
            currentTheme = currentTheme,
            weeklyProgress = weeklyProgress,
        )
    }
}

@Composable
private fun RowScope.ThemeAwareTokenBalanceColumn(
    currentTheme: BaseAppTheme,
    tokenBalance: Int,
) {
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
                text = tokenBalance.toString(),
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
private fun RowScope.ThemeAwareWeeklyProgressColumn(
    currentTheme: BaseAppTheme,
    weeklyProgress: String,
) {
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
                text = weeklyProgress,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.primary,
            )
        }
        Text(
            text = if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "this adventure"
            } else {
                "this week"
            },
            style = MaterialTheme.typography.bodySmall,
            color = currentTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ThemeAwareTasksCompletionSection(
    currentTheme: BaseAppTheme,
    completedTasks: Int,
    totalTasks: Int,
) {
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

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
                "Quests: $completedTasks/$totalTasks"
            } else {
                "Tasks: $completedTasks/$totalTasks"
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
        progress = progress,
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

            CaregiverPrimaryActions(currentTheme = currentTheme)
            Spacer(modifier = Modifier.height(8.dp))
            CaregiverSecondaryActions(currentTheme = currentTheme)
        }
    }
}

@Composable
private fun CaregiverPrimaryActions(currentTheme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CaregiverAddTaskButton(currentTheme = currentTheme)
        CaregiverManageRewardsButton(currentTheme = currentTheme)
    }
}

@Composable
private fun RowScope.CaregiverAddTaskButton(currentTheme: BaseAppTheme) {
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
            if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "Add Quest"
            } else {
                "Add Task"
            },
        )
    }
}

@Composable
private fun RowScope.CaregiverManageRewardsButton(currentTheme: BaseAppTheme) {
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
            if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "Manage Shop"
            } else {
                "Manage Rewards"
            },
        )
    }
}

@Composable
private fun CaregiverSecondaryActions(currentTheme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CaregiverReportButton(currentTheme = currentTheme)
        CaregiverSettingsButton(currentTheme = currentTheme)
    }
}

@Composable
private fun RowScope.CaregiverReportButton(currentTheme: BaseAppTheme) {
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
            if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "Adventure Log"
            } else {
                "Weekly Report"
            },
        )
    }
}

@Composable
private fun RowScope.CaregiverSettingsButton(currentTheme: BaseAppTheme) {
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
            if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "Player Settings"
            } else {
                "Child Settings"
            },
        )
    }
}

@Composable
fun ThemeAwareWeeklyProgressCard(
    currentTheme: BaseAppTheme,
    modifier: Modifier = Modifier,
    tokenBalance: Int = 85,
    weeklyProgress: String = "+15%",
    completedTasks: Int = 12,
    totalTasks: Int = 15,
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
            WeeklyProgressHeader(currentTheme)
            Spacer(modifier = Modifier.height(12.dp))
            WeeklyProgressStatsRow(currentTheme, tokenBalance, weeklyProgress)
            Spacer(modifier = Modifier.height(8.dp))
            WeeklyProgressTasksRow(currentTheme, completedTasks, totalTasks)
        }
    }
}

@Composable
private fun WeeklyProgressHeader(currentTheme: BaseAppTheme) {
    Text(
        text = if (currentTheme.displayName.contains(
                "Mario",
                ignoreCase = true,
            )
        ) {
            "Adventure Progress"
        } else {
            "Weekly Progress"
        },
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = currentTheme.colorScheme.onSecondaryContainer,
    )
}

@Composable
private fun WeeklyProgressStatsRow(
    currentTheme: BaseAppTheme,
    tokenBalance: Int,
    weeklyProgress: String,
) {
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
                "⭐ $tokenBalance coins"
            } else {
                "⭐ $tokenBalance tokens"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = currentTheme.colorScheme.onSecondaryContainer,
        )
        Text(
            text = if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "📈 $weeklyProgress this adventure"
            } else {
                "📈 $weeklyProgress this week"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = currentTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun WeeklyProgressTasksRow(
    currentTheme: BaseAppTheme,
    completedTasks: Int,
    totalTasks: Int,
) {
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
                "✅ $completedTasks/$totalTasks quests"
            } else {
                "✅ $completedTasks/$totalTasks tasks"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = currentTheme.colorScheme.onSecondaryContainer,
        )
        Text(
            text = if (currentTheme.displayName.contains(
                    "Mario",
                    ignoreCase = true,
                )
            ) {
                "🎯 On track for kingdom goal"
            } else {
                "🎯 On track for weekly goal"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = currentTheme.colorScheme.primary,
        )
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
