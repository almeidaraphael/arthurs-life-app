package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareQuickStatsRow(
    theme: BaseAppTheme,
    completedTasks: Int = 0,
    totalTasks: Int = 0,
    newAchievements: Int = 0,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        themeAwareStatCard(
            theme = theme,
            value = "$completedTasks/$totalTasks",
            label = "${theme.taskLabel} Done",
            containerColor = theme.colorScheme.secondaryContainer,
            contentColor = theme.colorScheme.onSecondaryContainer,
            modifier = Modifier.weight(1f),
        )

        themeAwareStatCard(
            theme = theme,
            value = "$newAchievements",
            label = "New ${theme.achievementLabel}",
            containerColor = theme.colorScheme.tertiaryContainer,
            contentColor = theme.colorScheme.onTertiaryContainer,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun themeAwareStatCard(
    theme: BaseAppTheme,
    value: String,
    label: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = theme.shapes.small,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = theme.statValueFormatter(value),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ThemeAwareQuickActionsSection(
    theme: BaseAppTheme,
    onStartTaskClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onAchievementsClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = theme.shapes.small,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = theme.actionSectionTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            ThemeAwareStartTaskButton(
                theme = theme,
                onClick = onStartTaskClick,
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(4.dp))

            ThemeAwareSecondaryActionsRow(
                theme = theme,
                onRewardsClick = onRewardsClick,
                onAchievementsClick = onAchievementsClick,
            )
        }
    }
}
