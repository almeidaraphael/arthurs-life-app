package com.lemonqwest.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.presentation.theme.components.SemanticIconType
import com.lemonqwest.app.presentation.theme.components.ThemeAwareIcon

@Composable
fun PlaceholderScreen(
    title: String,
    description: String,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
) {
    val theme by themeViewModel.currentTheme.collectAsState()
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.CONSTRUCTION,
            theme = theme,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = theme.colorScheme.primary,
        )

        Text(
            text = title,
            style = theme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = theme.colorScheme.onSurface,
        )

        Text(
            text = description,
            style = theme.typography.bodyLarge,
            color = theme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        Text(
            text = "Coming Soon!",
            style = theme.typography.titleMedium,
            color = theme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
