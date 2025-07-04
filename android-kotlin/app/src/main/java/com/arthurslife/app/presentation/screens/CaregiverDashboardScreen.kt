package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CaregiverDashboardScreen() {
    LazyColumn(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            // Header with child selector
            ChildSelectorHeader()
        }

        item {
            // Child overview stats
            ChildOverviewCard()
        }

        item {
            // Weekly progress
            WeeklyProgressCard()
        }

        item {
            // Wishlist insights
            WishlistInsightsCard()
        }

        item {
            // Quick actions
            QuickActionsSection()
        }
    }
}

@Composable
private fun ChildSelectorHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChildSelectorSection()
            CaregiverRoleLabel()
        }
    }
}

@Composable
private fun ChildOverviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = "Arthur's Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OverviewStatsRow()

            Spacer(modifier = Modifier.height(16.dp))

            TasksCompletionSection()
        }
    }
}

@Composable
private fun TasksCompletionSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Tasks: 12/15",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "On track for weekly goal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    LinearProgressIndicator(
        progress = { 0.8f },
        modifier =
        Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
    )
}

@Composable
private fun WeeklyProgressCard() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Weekly Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                TextButton(
                    onClick = { /* Navigate to detailed progress */ },
                ) {
                    Text("View Details")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            val dailyProgress =
                listOf(
                    "Mon" to DashboardConstants.MONDAY_PROGRESS,
                    "Tue" to DashboardConstants.TUESDAY_PROGRESS,
                    "Wed" to DashboardConstants.WEDNESDAY_PROGRESS,
                    "Thu" to DashboardConstants.THURSDAY_PROGRESS,
                    "Fri" to DashboardConstants.FRIDAY_PROGRESS,
                    "Sat" to DashboardConstants.SATURDAY_PROGRESS,
                    "Sun" to DashboardConstants.SUNDAY_PROGRESS,
                )

            dailyProgress.forEach { (day, progress) ->
                DailyProgressRow(day = day, progress = progress)
            }
        }
    }
}

@Composable
private fun DailyProgressRow(
    day: String,
    progress: Float,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(32.dp),
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun WishlistInsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Header
            Text(
                text = "Wishlist Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            Spacer(modifier = Modifier.height(12.dp))

            WishlistGoalSection()

            Spacer(modifier = Modifier.height(12.dp))

            WishlistTipSection()
        }
    }
}

@Composable
private fun QuickActionsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuickActionsPrimary()
            Spacer(modifier = Modifier.height(8.dp))
            QuickActionsSecondary()
        }
    }
}

@Composable
private fun WishlistGoalSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "🚲",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "Next Goal: New Bike",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            Text(
                text = "24% progress (120/500 tokens)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { 0.24f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
            )
        }
    }
}

@Composable
private fun WishlistTipSection() {
    Text(
        text = "💡 Gift Ideas: 3 affordable items in wishlist",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}

@Composable
private fun QuickActionsPrimary() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = { /* Navigate to add task */ },
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Task")
        }

        OutlinedButton(
            onClick = { /* Navigate to manage rewards */ },
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Manage Rewards")
        }
    }
}

@Composable
private fun QuickActionsSecondary() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = { /* Navigate to weekly report */ },
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector = Icons.Default.Assessment,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Weekly Report")
        }

        OutlinedButton(
            onClick = { /* Navigate to child settings */ },
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Child Settings")
        }
    }
}
