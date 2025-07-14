package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareProfileHeader(theme: BaseAppTheme) {
    Text(
        text = theme.profileTitle,
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun ThemeAwareProfileSettingsCard(
    theme: BaseAppTheme,
    onThemeSettingsClick: () -> Unit,
    onProfileCustomizationClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = theme.shapes.small,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = theme.settingsTitle,
                style = MaterialTheme.typography.titleMedium,
            )

            ThemeAwareClickableSettingsItem(
                theme = theme,
                text = theme.profileText.customizationMenuText,
                onClick = onProfileCustomizationClick,
            )

            ThemeAwareClickableSettingsItem(
                theme = theme,
                text = theme.displaySettingsText,
                onClick = onThemeSettingsClick,
            )

            ThemeAwareSettingsItem(
                theme = theme,
                text = theme.notificationSettingsText,
            )

            ThemeAwareSettingsItem(
                theme = theme,
                text = theme.accessibilitySettingsText,
            )
        }
    }
}

@Composable
fun ThemeAwareSettingsItem(
    theme: BaseAppTheme,
    text: String,
) {
    Text(
        text = "${theme.listItemPrefix} $text",
        style = MaterialTheme.typography.bodyMedium,
        color = theme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun ThemeAwareClickableSettingsItem(
    theme: BaseAppTheme,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "${theme.listItemPrefix} $text",
            style = MaterialTheme.typography.bodyMedium,
            color = theme.colorScheme.onSurfaceVariant,
        )
        ThemeAwareIcon(
            semanticType = SemanticIconType.NAVIGATE_NEXT,
            theme = theme,
            contentDescription = null,
            tint = theme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun ThemeAwareSwitchUserButton(
    theme: BaseAppTheme,
    onSwitchUser: () -> Unit,
) {
    Button(
        onClick = onSwitchUser,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = theme.shapes.small,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.SWITCH_ACCOUNT,
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

@Deprecated("Use ThemeAwareSwitchUserButton instead")
@Composable
fun ThemeAwareSwitchToCaregiverButton(
    theme: BaseAppTheme,
    onSwitchMode: () -> Unit,
) {
    ThemeAwareSwitchUserButton(
        theme = theme,
        onSwitchUser = onSwitchMode,
    )
}

@Composable
fun ThemeAwareAuthenticationHint(
    theme: BaseAppTheme,
) {
    Text(
        text = theme.pinRequiredText,
        style = MaterialTheme.typography.bodySmall,
        color = theme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
