package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.presentation.theme.BaseAppTheme

private const val AVATAR_ICON_SIZE_RATIO = 0.6f

// Theme-aware icon component
@Composable
fun ThemeAwareIcon(
    semanticType: SemanticIconType,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    contentDescription: String? = null,
) {
    val icon = theme.icons(semanticType)
    val resolvedTint = tint ?: if (theme.useOriginalIconColors) Color.Unspecified else theme.colorScheme.primary

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = resolvedTint,
    )
}

// Theme-aware avatar component
@Composable
fun ThemeAwareAvatar(
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(theme.shapes.extraSmall)
            .background(theme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.AVATAR,
            theme = theme,
            modifier = Modifier.size(size * AVATAR_ICON_SIZE_RATIO),
            tint = if (theme.useOriginalIconColors) null else theme.colorScheme.primary,
            contentDescription = "Avatar",
        )
    }
}

// Theme-aware progress indicator
@Composable
fun ThemeAwareProgressIndicator(
    progress: Float,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    color: Color = theme.colorScheme.primary,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier.clip(theme.shapes.extraSmall),
        color = color,
        trackColor = trackColor,
    )
}

// Theme-aware background component
@Composable
fun ThemeAwareBackground(
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background image layer
        theme.backgroundImage?.invoke()

        // Background tint layer
        theme.backgroundTint?.let { tint ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tint),
            )
        }

        // Content layer
        content()
    }
}

// Theme-aware card component
@Composable
fun ThemeAwareCard(
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    containerColor: Color = theme.colorScheme.surface,
    elevation: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = theme.shapes.medium,
        content = content,
    )
}

// Removed: getIconForTheme, getDefaultTint, getAvatarShape (all AppTheme-based logic is now obsolete)
