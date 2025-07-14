package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.ThemeAwareAuthenticationHint
import com.arthurslife.app.presentation.theme.components.ThemeAwareProfileHeader
import com.arthurslife.app.presentation.theme.components.ThemeAwareProfileSettingsCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareSwitchUserButton

@Composable
fun ChildProfileScreen(
    themeViewModel: ThemeViewModel,
    onSwitchMode: () -> Unit = {},
    onThemeSettingsClick: () -> Unit = {},
    onProfileCustomizationClick: () -> Unit = {},
) {
    val theme by themeViewModel.currentTheme.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ThemeAwareProfileHeader(theme = theme)

        ThemeAwareProfileSettingsCard(
            theme = theme,
            onThemeSettingsClick = onThemeSettingsClick,
            onProfileCustomizationClick = onProfileCustomizationClick,
        )

        Spacer(modifier = Modifier.weight(1f))

        ThemeAwareSwitchUserButton(
            theme = theme,
            onSwitchUser = onSwitchMode,
        )

        ThemeAwareAuthenticationHint(
            theme = theme,
        )
    }
}
