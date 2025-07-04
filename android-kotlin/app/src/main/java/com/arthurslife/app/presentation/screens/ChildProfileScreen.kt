package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwitchAccount
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
fun childProfileScreen(
    onSwitchMode: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        profileHeader()
        profileSettingsCard()
        Spacer(modifier = Modifier.weight(1f))
        switchToCaregiverButton(onSwitchMode)
        authenticationHint("PIN required for caregiver access")
    }
}

@Composable
private fun profileHeader() {
    Text(
        text = "Profile",
        style = MaterialTheme.typography.headlineLarge,
    )
}

@Composable
private fun profileSettingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium,
            )
            settingsItem("Theme & Display Settings")
            settingsItem("Notifications")
            settingsItem("Accessibility")
        }
    }
}

@Composable
private fun settingsItem(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun switchToCaregiverButton(onSwitchMode: () -> Unit) {
    Button(
        onClick = onSwitchMode,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Icon(
            imageVector = Icons.Default.SwitchAccount,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = "Switch to Caregiver Mode",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun authenticationHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
