package com.arthurslife.app.presentation.theme.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurslife.app.R
import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.domain.user.ChangeUserAvatarUseCase
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.LocalBaseTheme

/**
 * Avatar selection component that supports both predefined and custom avatars.
 *
 * @param currentAvatarType Current avatar type
 * @param currentAvatarData Current avatar data (ID for predefined, base64 for custom)
 * @param currentProfileImage Current profile image bitmap (for custom avatars)
 * @param onPredefinedAvatarSelected Callback when predefined avatar is selected
 * @param onCustomAvatarSelected Callback when custom avatar is selected
 * @param modifier Modifier for styling
 */
@Composable
fun avatarSelectionCard(
    currentAvatarType: AvatarType,
    currentAvatarData: String,
    currentProfileImage: Bitmap?,
    onPredefinedAvatarSelected: (String) -> Unit,
    onCustomAvatarSelected: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalBaseTheme.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.containerColors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = theme.profileText.avatarSectionTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textColors.primary,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current Avatar Display
            currentAvatarDisplay(
                avatarType = currentAvatarType,
                avatarData = currentAvatarData,
                profileImage = currentProfileImage,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Predefined Avatars Section
            Text(
                text = theme.profileText.predefinedAvatarsTitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = theme.textColors.secondary,
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            predefinedAvatarGrid(
                selectedAvatarId = if (currentAvatarType == AvatarType.PREDEFINED) currentAvatarData else null,
                onAvatarSelected = onPredefinedAvatarSelected,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Avatar Section
            Text(
                text = theme.profileText.customAvatarTitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = theme.textColors.secondary,
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            customAvatarOptions(
                onImageSelected = onCustomAvatarSelected,
            )
        }
    }
}

/**
 * Displays the current user avatar.
 */
@Composable
private fun currentAvatarDisplay(
    avatarType: AvatarType,
    avatarData: String,
    profileImage: Bitmap?,
    modifier: Modifier = Modifier,
) {
    val theme = LocalBaseTheme.current

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(theme.containerColors.primary.copy(alpha = 0.1f))
            .border(
                width = 3.dp,
                color = theme.containerColors.primary,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        when (avatarType) {
            AvatarType.PREDEFINED -> {
                predefinedAvatarImage(
                    avatarId = avatarData,
                    size = 100.dp,
                )
            }
            AvatarType.CUSTOM -> {
                profileImage?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } ?: Icon(
                    painter = painterResource(R.drawable.default_avatar),
                    contentDescription = "Default avatar",
                    modifier = Modifier.size(60.dp),
                    tint = theme.textColors.secondary,
                )
            }
        }
    }
}

/**
 * Grid of predefined avatar options.
 */
@Composable
private fun predefinedAvatarGrid(
    selectedAvatarId: String?,
    onAvatarSelected: (String) -> Unit,
) {
    val theme = LocalBaseTheme.current
    val predefinedAvatars = ChangeUserAvatarUseCase.PREDEFINED_AVATARS.toList()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        items(predefinedAvatars) { avatarId ->
            predefinedAvatarItem(
                avatarId = avatarId,
                isSelected = avatarId == selectedAvatarId,
                onSelected = { onAvatarSelected(avatarId) },
            )
        }
    }
}

/**
 * Individual predefined avatar item.
 */
@Composable
private fun predefinedAvatarItem(
    avatarId: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val theme = LocalBaseTheme.current

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) {
                    theme.containerColors.primary.copy(alpha = 0.2f)
                } else {
                    theme.containerColors.surface
                },
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) {
                    theme.containerColors.primary
                } else {
                    theme.containerColors.outline
                },
                shape = CircleShape,
            )
            .clickable { onSelected() },
        contentAlignment = Alignment.Center,
    ) {
        predefinedAvatarImage(
            avatarId = avatarId,
            size = 60.dp,
        )
    }
}

/**
 * Displays a predefined avatar image based on ID.
 */
@Composable
private fun predefinedAvatarImage(
    avatarId: String,
    size: androidx.compose.ui.unit.Dp,
) {
    val resourceId = when (avatarId) {
        "mario_child" -> R.drawable.avatar_mario
        "luigi_child" -> R.drawable.avatar_luigi
        "peach_child" -> R.drawable.avatar_peach
        "toad_child" -> R.drawable.avatar_toad
        "koopa_child" -> R.drawable.avatar_koopa
        "goomba_child" -> R.drawable.avatar_goomba
        "star_child" -> R.drawable.avatar_star
        "mushroom_child" -> R.drawable.avatar_mushroom
        "default_caregiver" -> R.drawable.avatar_caregiver
        else -> R.drawable.default_avatar
    }

    Icon(
        painter = painterResource(resourceId),
        contentDescription = "Avatar: $avatarId",
        modifier = Modifier.size(size),
        tint = androidx.compose.ui.graphics.Color.Unspecified,
    )
}

/**
 * Custom avatar selection options (camera/gallery).
 */
@Composable
private fun customAvatarOptions(
    onImageSelected: (Bitmap) -> Unit,
) {
    val theme = LocalBaseTheme.current

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        bitmap?.let { onImageSelected(it) }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            // Convert URI to Bitmap (simplified - in real implementation you'd handle this properly)
            // For now, this is a placeholder
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        avatarCameraButton(
            theme = theme,
            onClick = { cameraLauncher.launch(null) },
            modifier = Modifier.weight(1f),
        )

        avatarGalleryButton(
            theme = theme,
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun avatarCameraButton(
    theme: BaseAppTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = theme.containerColors.surface,
            contentColor = theme.textColors.primary,
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            theme.containerColors.outline,
        ),
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Camera")
    }
}

@Composable
private fun avatarGalleryButton(
    theme: BaseAppTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = theme.containerColors.surface,
            contentColor = theme.textColors.primary,
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            theme.containerColors.outline,
        ),
    ) {
        Icon(
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Gallery")
    }
}

/**
 * Color selection component for favorite color.
 */
@Composable
fun colorSelectionCard(
    selectedColor: String?,
    onColorSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalBaseTheme.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.containerColors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = theme.profileText.favoriteColorTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textColors.primary,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
            ) {
                // No color option
                item {
                    colorOption(
                        color = null,
                        isSelected = selectedColor == null,
                        onSelected = { onColorSelected(null) },
                    )
                }

                items(PREDEFINED_COLORS) { color ->
                    colorOption(
                        color = color,
                        isSelected = selectedColor == color,
                        onSelected = { onColorSelected(color) },
                    )
                }
            }
        }
    }
}

@Composable
private fun colorOption(
    color: String?,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val theme = LocalBaseTheme.current

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                color = if (color != null) {
                    androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(color))
                } else {
                    theme.containerColors.surface
                },
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) {
                    theme.containerColors.primary
                } else {
                    theme.containerColors.outline
                },
                shape = CircleShape,
            )
            .clickable { onSelected() },
        contentAlignment = Alignment.Center,
    ) {
        if (color == null) {
            Icon(
                painter = painterResource(R.drawable.ic_no_color),
                contentDescription = "No color",
                modifier = Modifier.size(24.dp),
                tint = theme.textColors.secondary,
            )
        }
    }
}

private val PREDEFINED_COLORS = listOf(
    "#FF5722", // Red
    "#FF9800", // Orange
    "#FFEB3B", // Yellow
    "#4CAF50", // Green
    "#2196F3", // Blue
    "#9C27B0", // Purple
    "#E91E63", // Pink
    "#795548", // Brown
    "#607D8B", // Blue Grey
    "#000000", // Black
)
