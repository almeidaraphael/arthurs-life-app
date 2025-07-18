package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareStartTaskButton(
    theme: BaseAppTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = theme.shapes.extraSmall,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.START_TASK,
            theme = theme,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Start Next ${theme.taskLabel.dropLast(1)}", // Remove 's' to make it singular
        )
    }
}

@Composable
fun ThemeAwareSecondaryActionsRow(
    theme: BaseAppTheme,
    onRewardsClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = onRewardsClick,
            modifier = Modifier.weight(1f),
            shape = theme.shapes.extraSmall,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.REWARDS,
                theme = theme,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Rewards", // Keep simple for now, can be enhanced later
            )
        }

        OutlinedButton(
            onClick = onAchievementsClick,
            modifier = Modifier.weight(1f),
            shape = theme.shapes.extraSmall,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.ACHIEVEMENTS,
                theme = theme,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = theme.achievementLabel,
            )
        }
    }
}

@Composable
fun ThemeAwareActionButtons(
    currentTheme: BaseAppTheme,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onPrimaryClick,
        modifier = modifier,
        shape = currentTheme.shapes.extraSmall,
    ) {
        Text(text = primaryText)
    }
}

@Composable
fun ThemeAwareFloatingActionButton(
    onClick: () -> Unit,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = theme.shapes.large,
        containerColor = theme.colorScheme.primaryContainer,
        contentColor = theme.colorScheme.onPrimaryContainer,
    ) {
        icon()
    }
}

@Composable
fun ThemeAwareIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        icon()
    }
}

@Composable
fun ThemeAwareTextButton(
    text: String,
    onClick: () -> Unit,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = theme.shapes.small,
    ) {
        Text(
            text = text,
            color = if (enabled) {
                theme.colorScheme.primary
            } else {
                theme.colorScheme.onSurface.copy(
                    alpha = 0.38f,
                )
            },
        )
    }
}

@Composable
fun ThemeAwareButton(
    text: String,
    onClick: () -> Unit,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = theme.shapes.small,
    ) {
        Text(text = text)
    }
}

@Composable
fun ThemeAwareOutlinedButton(
    text: String,
    onClick: () -> Unit,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = theme.shapes.small,
    ) {
        Text(text = text)
    }
}
