package com.lemonqwest.app.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.presentation.theme.components.SemanticIconType
import com.lemonqwest.app.presentation.theme.components.ThemeAwareIcon

@Composable
fun CaregiverProfileScreen(
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
        CaregiverProfileHeader()
        CaregiverSettingsCard(onThemeSettingsClick, onProfileCustomizationClick)
        Spacer(modifier = Modifier.weight(1f))
        SwitchToChildButton(onSwitchMode, theme)
        CaregiverAuthenticationHint("No PIN required for child access")
    }
}

@Composable
private fun CaregiverProfileHeader() {
    Text(
        text = "Caregiver Profile",
        style = MaterialTheme.typography.headlineLarge,
    )
}

@Composable
private fun CaregiverSettingsCard(
    onThemeSettingsClick: () -> Unit = {},
    onProfileCustomizationClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleMedium,
            )
            CaregiverClickableSettingsItem("Profile Customization", onProfileCustomizationClick)
            CaregiverSettingsItem("Security & PIN")
            CaregiverSettingsItem("Family Settings")
            CaregiverClickableSettingsItem("Theme & Display Settings", onThemeSettingsClick)
            CaregiverSettingsItem("Notifications")
        }
    }
}

@Composable
private fun CaregiverSettingsItem(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SwitchToChildButton(onSwitchMode: () -> Unit, theme: BaseAppTheme) {
    Button(
        onClick = onSwitchMode,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.CHILD_CARE,
            theme = theme,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = "Switch User",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun CaregiverClickableSettingsItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // Add icon or other UI as needed
    }
}

@Composable
private fun CaregiverAuthenticationHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
