package com.lemonqwest.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.presentation.theme.components.ThemeAwareCaregiverQuickActions
import com.lemonqwest.app.presentation.theme.components.ThemeAwareChildOverviewCard
import com.lemonqwest.app.presentation.theme.components.ThemeAwareChildSelectorHeader
import com.lemonqwest.app.presentation.theme.components.ThemeAwareWeeklyProgressCard
import com.lemonqwest.app.presentation.theme.components.ThemeAwareWishlistInsightsCard
import com.lemonqwest.app.presentation.viewmodels.TaskManagementViewModel

@Composable
fun CaregiverDashboardScreen(
    themeViewModel: ThemeViewModel,
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel(),
) {
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val taskStats by taskManagementViewModel.taskStats.collectAsState()
    val incompleteTasks by taskManagementViewModel.incompleteTasks.collectAsState()
    val completedTasks by taskManagementViewModel.completedTasks.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            // Header with child selector
            ThemeAwareChildSelectorHeader(
                currentTheme = currentTheme,
                childName = "Arthur",
            )
        }

        item {
            // Child overview stats
            val totalTasks = incompleteTasks.size + completedTasks.size
            val completedTasksCount = completedTasks.size
            val tokenBalance = taskStats?.totalTokensEarned ?: 0

            ThemeAwareChildOverviewCard(
                currentTheme = currentTheme,
                tokenBalance = tokenBalance,
                weeklyProgress = "+${((completedTasksCount.toFloat() / totalTasks.coerceAtLeast(1)) * 100).toInt()}%",
                completedTasks = completedTasksCount,
                totalTasks = totalTasks,
            )
        }

        item {
            // Weekly progress
            val totalTasks = incompleteTasks.size + completedTasks.size
            val completedTasksCount = completedTasks.size
            val tokenBalance = taskStats?.totalTokensEarned ?: 0

            ThemeAwareWeeklyProgressCard(
                currentTheme = currentTheme,
                tokenBalance = tokenBalance,
                weeklyProgress = "+${((completedTasksCount.toFloat() / totalTasks.coerceAtLeast(1)) * 100).toInt()}%",
                completedTasks = completedTasksCount,
                totalTasks = totalTasks,
            )
        }

        item {
            // Wishlist insights
            ThemeAwareWishlistInsightsCard(currentTheme = currentTheme)
        }

        item {
            // Quick actions
            ThemeAwareCaregiverQuickActions(currentTheme = currentTheme)
        }
    }
}
