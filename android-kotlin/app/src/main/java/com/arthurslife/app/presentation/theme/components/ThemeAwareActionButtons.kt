package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.BaseAppTheme

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
            text = when (theme.displayName) {
                "Mario Classic" -> "Start Next Quest"
                else -> "Start Next Task"
            },
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
                text = when (theme.displayName) {
                    "Mario Classic" -> "Shop"
                    else -> "Rewards"
                },
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
                text = when (theme.displayName) {
                    "Mario Classic" -> "Trophies"
                    else -> "Achievements"
                },
            )
        }
    }
}
