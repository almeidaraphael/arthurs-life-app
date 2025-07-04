package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.ThemeAwareMotivationalCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareProgressCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareQuickActionsSection
import com.arthurslife.app.presentation.theme.components.ThemeAwareQuickStatsRow
import com.arthurslife.app.presentation.theme.components.ThemeAwareTokenCard

@Composable
fun ChildHomeScreen(
    themeViewModel: ThemeViewModel,
) {
    val theme by themeViewModel.currentTheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header with token balance and progress
        ThemeAwareTokenCard(
            theme = theme,
            tokenBalance = 100,
        )

        // Daily progress section
        ThemeAwareProgressCard(
            theme = theme,
            progress = 0.7f,
        )

        // Quick stats section
        ThemeAwareQuickStatsRow(theme = theme)

        // Quick actions section
        ThemeAwareQuickActionsSection(
            theme = theme,
            onStartTaskClick = { /* Navigate to tasks */ },
            onRewardsClick = { /* Navigate to rewards */ },
            onAchievementsClick = { /* Navigate to achievements */ },
        )

        // Motivational message
        ThemeAwareMotivationalCard(
            theme = theme,
            streak = 5,
        )
    }
}
