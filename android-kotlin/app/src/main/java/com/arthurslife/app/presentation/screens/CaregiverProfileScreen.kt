package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun caregiverProfileScreen(
    onSwitchMode: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        caregiverProfileHeader()
        caregiverSettingsCard()
        Spacer(modifier = Modifier.weight(1f))
        switchToChildButton(onSwitchMode)
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
private fun caregiverSettingsCard() {
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
            caregiverSettingsItem("Profile Management")
            caregiverSettingsItem("Security & PIN")
            caregiverSettingsItem("Family Settings")
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
private fun switchToChildButton(onSwitchMode: () -> Unit) {
    Button(
        onClick = onSwitchMode,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Icon(
            imageVector = Icons.Default.ChildCare,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = "Switch to Child Mode",
            style = MaterialTheme.typography.titleMedium,
        )
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
