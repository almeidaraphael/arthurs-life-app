package com.lemonqwest.app.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.domain.user.AvatarType
import com.lemonqwest.app.presentation.theme.LocalBaseTheme
import com.lemonqwest.app.presentation.theme.components.AvatarSelectionCard

/**
 * Configuration data for the avatar selection dialog.
 */
data class AvatarSelectionDialogConfig(
    val isOpen: Boolean,
    val currentAvatarType: AvatarType,
    val currentAvatarData: String,
    val currentProfileImage: Bitmap?,
    val onDismiss: () -> Unit,
    val onAvatarSelected: (AvatarType, String, Bitmap?) -> Unit,
)

/**
 * Dialog for selecting user avatars with predefined and custom options.
 *
 * This reusable component provides a complete avatar selection experience
 * supporting both predefined themed avatars and custom image uploads.
 * Used for both caregiver and child onboarding flows.
 *
 * @param config Configuration object containing all dialog parameters
 */
@Composable
fun AvatarSelectionDialog(config: AvatarSelectionDialogConfig) {
    if (!config.isOpen) return

    val theme = LocalBaseTheme.current
    var selectedType by remember(config.isOpen) { mutableStateOf(config.currentAvatarType) }
    var selectedData by remember(config.isOpen) { mutableStateOf(config.currentAvatarData) }
    var selectedImage by remember(config.isOpen) { mutableStateOf(config.currentProfileImage) }

    AlertDialog(
        onDismissRequest = config.onDismiss,
        title = {
            Text(
                text = theme.profileText.avatarSectionTitle,
                color = theme.textColors.primary,
            )
        },
        text = {
            AvatarSelectionDialogContent(
                selectedType = selectedType,
                selectedData = selectedData,
                selectedImage = selectedImage,
                onPredefinedSelected = { avatarId ->
                    selectedType = AvatarType.PREDEFINED
                    selectedData = avatarId
                    selectedImage = null
                },
                onCustomSelected = { bitmap ->
                    selectedType = AvatarType.CUSTOM
                    selectedData = convertBitmapToBase64(bitmap)
                    selectedImage = bitmap
                },
            )
        },
        confirmButton = {
            AvatarDialogConfirmButton(
                onClick = { config.onAvatarSelected(selectedType, selectedData, selectedImage) },
                theme = theme,
            )
        },
        dismissButton = {
            AvatarDialogDismissButton(
                onClick = config.onDismiss,
                theme = theme,
            )
        },
        containerColor = theme.containerColors.surface,
    )
}

@Composable
private fun AvatarSelectionDialogContent(
    selectedType: AvatarType,
    selectedData: String,
    selectedImage: Bitmap?,
    onPredefinedSelected: (String) -> Unit,
    onCustomSelected: (Bitmap) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        AvatarSelectionCard(
            currentAvatarType = selectedType,
            currentAvatarData = selectedData,
            currentProfileImage = selectedImage,
            onPredefinedAvatarSelected = onPredefinedSelected,
            onCustomAvatarSelected = onCustomSelected,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

/**
 * Converts a Bitmap to base64 data URL for storage.
 *
 * @param bitmap The bitmap image to convert
 * @return Base64 data URL string
 */
private fun convertBitmapToBase64(bitmap: Bitmap): String {
    val outputStream = java.io.ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, PNG_COMPRESSION_QUALITY, outputStream)
    val base64 = android.util.Base64.encodeToString(
        outputStream.toByteArray(),
        android.util.Base64.DEFAULT,
    )
    return "data:image/png;base64,$base64"
}

private const val PNG_COMPRESSION_QUALITY = 100

@Composable
private fun AvatarDialogConfirmButton(
    onClick: () -> Unit,
    theme: com.lemonqwest.app.presentation.theme.BaseAppTheme,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.semantics {
            contentDescription = "Confirm avatar selection"
            role = Role.Button
        },
    ) {
        Text(
            text = "Select",
            color = theme.textColors.primary,
        )
    }
}

@Composable
private fun AvatarDialogDismissButton(
    onClick: () -> Unit,
    theme: com.lemonqwest.app.presentation.theme.BaseAppTheme,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.semantics {
            contentDescription = "Cancel avatar selection"
            role = Role.Button
        },
    ) {
        Text(
            text = "Cancel",
            color = theme.textColors.secondary,
        )
    }
}
