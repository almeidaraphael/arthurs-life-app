package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.lemonqwest.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareMotivationalCard(
    theme: BaseAppTheme,
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MotivationalMessage(theme = theme)
        }
    }
}

@Composable
private fun getMotivationalCardColors(theme: BaseAppTheme) = CardDefaults.cardColors(
    containerColor = theme.colorScheme.tertiaryContainer,
    contentColor = theme.colorScheme.onTertiaryContainer,
)

@Composable
private fun MotivationalMessage(
    theme: BaseAppTheme,
) {
    Text(
        text = theme.motivationalMessage(),
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        color = theme.colorScheme.onTertiaryContainer,
        fontWeight = FontWeight.Medium,
    )
}
