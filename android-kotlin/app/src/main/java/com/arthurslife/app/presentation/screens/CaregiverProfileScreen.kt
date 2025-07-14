package com.arthurslife.app.presentation.screens

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
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.SemanticIconType
import com.arthurslife.app.presentation.theme.components.ThemeAwareIcon

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
        caregiverProfileHeader()
        caregiverSettingsCard(onThemeSettingsClick, onProfileCustomizationClick)
        Spacer(modifier = Modifier.weight(1f))
        switchToChildButton(onSwitchMode, theme)
        caregiverAuthenticationHint("No PIN required for child access")
    }
}

@Composable
private fun caregiverProfileHeader() {
    Text(
        text = "Caregiver Profile",
        style = MaterialTheme.typography.headlineLarge,
    )
}

@Composable
private fun caregiverSettingsCard(
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
            caregiverClickableSettingsItem("Profile Customization", onProfileCustomizationClick)
            caregiverSettingsItem("Security & PIN")
            caregiverSettingsItem("Family Settings")
            caregiverClickableSettingsItem("Theme & Display Settings", onThemeSettingsClick)
            caregiverSettingsItem("Notifications")
        }
    }
}

@Composable
private fun caregiverSettingsItem(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun switchToChildButton(onSwitchMode: () -> Unit, theme: BaseAppTheme) {
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
private fun caregiverClickableSettingsItem(text: String, onClick: () -> Unit) {
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
private fun caregiverAuthenticationHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
