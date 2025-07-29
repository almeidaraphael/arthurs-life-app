package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
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
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Theme selection area with ${availableThemes.size} available themes"
            },
    ) {
        Text(
            text = "Choose Theme",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .semantics {
                    contentDescription = "Theme selector heading"
                },
        )

        availableThemes.forEach { theme ->
            val validation = rememberThemeValidation(theme)
            themeOption(
                theme = theme,
                isSelected = currentTheme.displayName == theme.displayName,
                onThemeSelected = onThemeSelected,
                validation = validation,
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
    validation: ThemeValidationResult,
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

    val selectionStateDescription = if (isSelected) "Currently selected" else "Not selected"
    val accessibilityInfo = if (validation.isFullyWcagAACompliant) {
        "Fully accessible"
    } else {
        "${validation.wcagAACompliantCount} of ${validation.totalChecks} color combinations meet accessibility standards"
    }
    val themeDescription = "${theme.displayName} theme option. ${theme.description}. $accessibilityInfo. $selectionStateDescription"

    Card(
        modifier = modifier
            .selectable(
                selected = isSelected,
                onClick = { onThemeSelected(theme) },
                role = Role.RadioButton,
            )
            .then(borderModifier)
            .semantics {
                contentDescription = themeDescription
                selected = isSelected
                stateDescription = selectionStateDescription
                role = Role.RadioButton
            },
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
        modifier = Modifier.semantics {
            contentDescription = "Theme information for ${theme.displayName}"
        },
    ) {
        themePreview(theme = theme)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.semantics {
                    contentDescription = "Theme name: ${theme.displayName}"
                },
            )
            Text(
                text = theme.description,
                style = MaterialTheme.typography.bodySmall,
                color = theme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics {
                    contentDescription = "Theme description: ${theme.description}"
                },
            )
        }
    }
}

@Composable
private fun themeSelectedIcon(currentTheme: BaseAppTheme) {
    ThemeAwareIcon(
        semanticType = SemanticIconType.CHECK_SELECTED,
        theme = currentTheme,
        contentDescription = "${currentTheme.displayName} theme is currently selected",
        tint = currentTheme.colorScheme.primary,
        modifier = Modifier
            .size(24.dp)
            .semantics {
                contentDescription = "Selected theme indicator for ${currentTheme.displayName}"
            },
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
        modifier = modifier
            .semantics {
                contentDescription = "Color preview for ${theme.displayName} theme showing primary, secondary, and tertiary colors"
            },
    ) {
        colors.forEachIndexed { index, color ->
            val colorName = when (index) {
                0 -> "primary"
                1 -> "secondary"
                2 -> "tertiary"
                else -> "color"
            }
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(shape)
                    .background(color)
                    .semantics {
                        contentDescription = "$colorName color sample for ${theme.displayName}"
                    },
            )
            if (color != colors.last()) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
