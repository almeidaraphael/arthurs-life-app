package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurslife.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeSelector(
    currentTheme: BaseAppTheme,
    availableThemes: List<BaseAppTheme>,
    onThemeSelected: (BaseAppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Choose Theme",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        availableThemes.forEach { theme ->
            themeOption(
                theme = theme,
                isSelected = currentTheme.displayName == theme.displayName,
                onThemeSelected = onThemeSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun themeOption(
    theme: BaseAppTheme,
    isSelected: Boolean,
    onThemeSelected: (BaseAppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = theme.shapes.medium
    val borderModifier = if (isSelected) {
        Modifier.border(
            BorderStroke(2.dp, theme.colorScheme.primary),
            cardShape,
        )
    } else {
        Modifier
    }

    Card(
        modifier = modifier
            .clickable { onThemeSelected(theme) }
            .then(borderModifier),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp,
        ),
    ) {
        themeOptionContent(
            theme = theme,
            isSelected = isSelected,
        )
    }
}

@Composable
private fun themeOptionContent(
    theme: BaseAppTheme,
    isSelected: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        themeInfoSection(theme = theme)

        if (isSelected) {
            themeSelectedIcon(currentTheme = theme)
        }
    }
}

@Composable
private fun themeInfoSection(theme: BaseAppTheme) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        themePreview(theme = theme)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = theme.description,
                style = MaterialTheme.typography.bodySmall,
                color = theme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun themeSelectedIcon(currentTheme: BaseAppTheme) {
    ThemeAwareIcon(
        semanticType = SemanticIconType.CHECK_SELECTED,
        theme = currentTheme,
        contentDescription = "Selected",
        tint = currentTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp),
    )
}

@Composable
private fun themePreview(
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    val colors = listOf(
        theme.colorScheme.primary,
        theme.colorScheme.secondary,
        theme.colorScheme.tertiary,
    )
    val shape = theme.shapes.small
    Row(
        modifier = modifier,
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(shape)
                    .background(color),
            )
            if (color != colors.last()) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
