package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.components.SemanticIconType
import com.arthurslife.app.presentation.theme.components.ThemeAwareAvatar
import com.arthurslife.app.presentation.theme.components.ThemeAwareIcon

@Composable
fun childSelectorSection(theme: BaseAppTheme) {
    Column {
        Text(
            text = "Managing",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            childAvatar(theme = theme)
            Spacer(modifier = Modifier.width(8.dp))
            childNameWithDropdown(theme = theme)
        }
    }
}

@Composable
fun childAvatar(theme: BaseAppTheme) {
    ThemeAwareAvatar(
        theme = theme,
        size = 32.dp,
    )
}

@Composable
fun childNameWithDropdown(theme: BaseAppTheme) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Arthur",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        var expanded by remember { mutableStateOf(false) }

        IconButton(
            onClick = { expanded = !expanded },
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.EXPAND_MORE,
                theme = theme,
                contentDescription = "Switch child",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Arthur") },
                onClick = { expanded = false },
            )
        }
    }
}

@Composable
fun caregiverRoleLabel() {
    Text(
        text = "Caregiver",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
fun RowScope.tokenBalanceColumn(theme: BaseAppTheme) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.TOKEN,
                theme = theme,
                contentDescription = "Tokens",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "85",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = "tokens",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun RowScope.weeklyProgressColumn(theme: BaseAppTheme) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.PROGRESS_INDICATOR,
                theme = theme,
                contentDescription = "Progress",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "+15%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = "this week",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun overviewStatsRow(theme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        tokenBalanceColumn(theme = theme)
        weeklyProgressColumn(theme = theme)
    }
}
