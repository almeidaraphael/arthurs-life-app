package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

/**
 * Task customization component for selecting custom colors and icons.
 *
 * Allows users to personalize their tasks with:
 * - Color palette selection
 * - Icon selection from predefined set
 * - Preview of current selection
 *
 * @param selectedColor Currently selected color hex
 * @param selectedIcon Currently selected icon name
 * @param onColorSelected Callback when a color is selected
 * @param onIconSelected Callback when an icon is selected
 * @param modifier Modifier for styling
 */
@Composable
fun TaskCustomizationPicker(
    selectedColor: String?,
    selectedIcon: String?,
    onColorSelected: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TaskCustomizationTitle()
            ColorSelectionSection(selectedColor, onColorSelected)
            IconSelectionSection(selectedIcon, onIconSelected)
        }
    }
}

@Composable
private fun TaskCustomizationTitle() {
    Text(
        text = "Customize Your Task",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
        ),
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorSelectionSection(
    selectedColor: String?,
    onColorSelected: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Choose a Color",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            colorPalette.forEach { colorHex ->
                ColorOption(
                    colorHex = colorHex,
                    isSelected = selectedColor == colorHex,
                    onSelected = { onColorSelected(colorHex) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconSelectionSection(
    selectedIcon: String?,
    onIconSelected: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Choose an Icon",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            iconOptions.forEach { iconData ->
                IconOption(
                    iconName = iconData.name,
                    iconResource = iconData.resource,
                    isSelected = selectedIcon == iconData.name,
                    onSelected = { onIconSelected(iconData.name) },
                )
            }
        }
    }
}

/**
 * Individual color option in the color palette.
 */
@Composable
private fun ColorOption(
    colorHex: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = try {
        Color(colorHex.toColorInt())
    } catch (e: IllegalArgumentException) {
        // Log the exception and use fallback color
        println("Invalid color hex: $colorHex, error: ${e.message}")
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                shape = CircleShape,
            )
            .clickable { onSelected() },
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_send),
                contentDescription = "Selected",
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.Center),
                tint = Color.White,
            )
        }
    }
}

/**
 * Individual icon option in the icon grid.
 */
@Composable
private fun IconOption(
    iconName: String,
    iconResource: Int,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                } else {
                    MaterialTheme.colorScheme.surface
                },
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(8.dp),
            )
            .clickable { onSelected() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconResource),
            contentDescription = iconName,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            },
        )
    }
}

/**
 * Data class for icon options.
 */
private data class IconData(
    val name: String,
    val resource: Int,
)

/**
 * Predefined color palette for task customization.
 */
private val colorPalette = listOf(
    "#E53935", // Red
    "#D81B60", // Pink
    "#8E24AA", // Purple
    "#5E35B1", // Deep Purple
    "#3949AB", // Indigo
    "#1E88E5", // Blue
    "#039BE5", // Light Blue
    "#00ACC1", // Cyan
    "#00897B", // Teal
    "#43A047", // Green
    "#7CB342", // Light Green
    "#C0CA33", // Lime
    "#FDD835", // Yellow
    "#FFB300", // Amber
    "#FB8C00", // Orange
    "#F4511E", // Deep Orange
)

/**
 * Predefined icon options for task customization.
 */
private val iconOptions = listOf(
    IconData("person", android.R.drawable.ic_menu_my_calendar),
    IconData("home", android.R.drawable.ic_menu_mylocation),
    IconData("school", android.R.drawable.ic_menu_agenda),
    IconData("sports", android.R.drawable.ic_menu_compass),
    IconData("music", android.R.drawable.ic_btn_speak_now),
    IconData("art", android.R.drawable.ic_menu_edit),
    IconData("food", android.R.drawable.ic_menu_today),
    IconData("pets", android.R.drawable.ic_menu_zoom),
    IconData("car", android.R.drawable.ic_menu_directions),
    IconData("gift", android.R.drawable.ic_menu_gallery),
    IconData("star", android.R.drawable.btn_star_big_on),
    IconData("heart", android.R.drawable.btn_star_big_off),
)
