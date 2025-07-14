package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.ThemeAwareProgressCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareQuickStatsRow
import com.arthurslife.app.presentation.theme.components.ThemeAwareTokenCard
import com.arthurslife.app.presentation.theme.components.themeAwareMotivationalCard
import com.arthurslife.app.presentation.viewmodels.TaskManagementViewModel

@Composable
fun ChildHomeScreen(
    themeViewModel: ThemeViewModel,
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel(),
) {
    val theme by themeViewModel.currentTheme.collectAsState()
    val taskStats by taskManagementViewModel.taskStats.collectAsState()
    val incompleteTasks by taskManagementViewModel.incompleteTasks.collectAsState()
    val completedTasks by taskManagementViewModel.completedTasks.collectAsState()
    val currentTokenBalance by taskManagementViewModel.currentTokenBalance.collectAsState()
    val dailyProgress by taskManagementViewModel.dailyProgress.collectAsState()
    val recentAchievements by taskManagementViewModel.recentAchievements.collectAsState()

    // Refresh data when screen becomes visible to ensure real-time updates
    LaunchedEffect(Unit) {
        taskManagementViewModel.refreshCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Header with token balance and progress
        ThemeAwareTokenCard(
            theme = theme,
            tokenBalance = currentTokenBalance?.getValue() ?: 0,
            modifier = Modifier.padding(vertical = 4.dp),
        )

        // Daily progress section
        ThemeAwareProgressCard(
            theme = theme,
            progress = dailyProgress,
            modifier = Modifier.padding(vertical = 4.dp),
        )

        // Quick stats section
        val completedTasksCount = completedTasks.size
        val totalTasksCount = completedTasks.size + incompleteTasks.size
        ThemeAwareQuickStatsRow(
            theme = theme,
            completedTasks = completedTasksCount,
            totalTasks = totalTasksCount,
            newAchievements = recentAchievements.size,
            modifier = Modifier.padding(vertical = 4.dp),
        )

        // Motivational message - enhanced with more space
        themeAwareMotivationalCard(
            theme = theme,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}
