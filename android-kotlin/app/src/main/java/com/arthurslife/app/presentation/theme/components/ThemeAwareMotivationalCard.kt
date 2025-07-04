package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareMotivationalCard(
    theme: BaseAppTheme,
    streak: Int = 5,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = getMotivationalCardColors(theme),
        shape = theme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StreakHeader(theme = theme, streak = streak)
            Spacer(modifier = Modifier.height(12.dp))
            MotivationalMessage(theme = theme, streak = streak)
        }
    }
}

@Composable
private fun getMotivationalCardColors(theme: BaseAppTheme) = CardDefaults.cardColors(
    containerColor = theme.colorScheme.tertiaryContainer,
    contentColor = theme.colorScheme.onTertiaryContainer,
)

@Composable
private fun StreakHeader(
    theme: BaseAppTheme,
    streak: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.STREAK_FIRE,
            theme = theme,
            modifier = Modifier.size(32.dp),
            tint = theme.colorScheme.primary,
            contentDescription = "Streak Fire",
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$streak Day Streak!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = theme.colorScheme.primary,
        )
    }
}

@Composable
private fun MotivationalMessage(
    theme: BaseAppTheme,
    streak: Int,
) {
    Text(
        text = theme.motivationalMessage(streak),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = theme.colorScheme.onTertiaryContainer,
    )
}
