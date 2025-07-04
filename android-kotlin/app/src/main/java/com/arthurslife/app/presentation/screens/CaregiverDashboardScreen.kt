package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.ThemeAwareCaregiverQuickActions
import com.arthurslife.app.presentation.theme.components.ThemeAwareChildOverviewCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareChildSelectorHeader
import com.arthurslife.app.presentation.theme.components.ThemeAwareWeeklyProgressCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareWishlistInsightsCard

@Composable
fun CaregiverDashboardScreen(
    themeViewModel: ThemeViewModel,
) {
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            // Header with child selector
            ThemeAwareChildSelectorHeader(currentTheme = currentTheme)
        }

        item {
            // Child overview stats
            ThemeAwareChildOverviewCard(currentTheme = currentTheme)
        }

        item {
            // Weekly progress
            ThemeAwareWeeklyProgressCard(currentTheme = currentTheme)
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
