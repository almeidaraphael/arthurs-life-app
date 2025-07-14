package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.R
import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.viewmodels.ProfileViewModel

/**
 * Screen for displaying user profile information based on the selected role.
 *
 * This screen dynamically shows information for the selected user (child or caregiver),
 * including their name, avatar, role-specific details, and token balance for children.
 * It provides accessibility support and handles loading and error states.
 *
 * @param selectedUser The user whose profile should be displayed
 * @param onRefresh Callback invoked when the user requests a refresh
 * @param modifier Optional modifier for the composable
 * @param viewModel ViewModel managing the profile state and data
 */
@Composable
fun profileScreen(
    selectedUser: User?,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val currentUser by viewModel.selectedUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Update ViewModel when selectedUser changes
    LaunchedEffect(selectedUser) {
        if (selectedUser != null) {
            viewModel.setSelectedUser(selectedUser)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        profileScreenContent(
            currentUser = currentUser,
            isLoading = isLoading,
            errorMessage = errorMessage,
            viewModel = viewModel,
            onRefresh = onRefresh,
        )
    }
}

@Composable
private fun profileScreenContent(
    currentUser: User?,
    isLoading: Boolean,
    errorMessage: String?,
    viewModel: ProfileViewModel,
    onRefresh: () -> Unit,
) {
    when {
        isLoading -> profileLoadingContent()
        errorMessage != null -> profileErrorContent(errorMessage, viewModel, onRefresh)
        currentUser == null -> profileEmptyContent()
        else -> profileContent(user = currentUser, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun profileLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics {
                contentDescription = "Loading profile"
            },
        )
    }
}

@Composable
private fun profileErrorContent(
    errorMessage: String,
    viewModel: ProfileViewModel,
    onRefresh: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
        )
        profileRetryButton(viewModel, onRefresh)
    }
}

@Composable
private fun profileRetryButton(viewModel: ProfileViewModel, onRefresh: () -> Unit) {
    Card(
        onClick = {
            viewModel.clearError()
            onRefresh()
        },
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
    ) {
        Text(
            text = stringResource(R.string.retry_button),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun profileEmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_user_selected),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Main content for the profile screen when a user is selected.
 *
 * Displays the user's avatar, name, role information, and role-specific details
 * such as token balance for children.
 *
 * @param user The user whose profile to display
 * @param modifier Optional modifier for the composable
 */
@Composable
private fun profileContent(
    user: User,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Profile Header
        profileHeader(user = user)

        // User Information Card
        userInfoCard(user = user)

        // Role-specific Information
        when (user.role) {
            UserRole.CHILD -> {
                childSpecificInfo(user = user)
            }
            UserRole.CAREGIVER -> {
                caregiverSpecificInfo(user = user)
            }
        }
    }
}

/**
 * Profile header component displaying the user's avatar and display name.
 *
 * @param user The user whose header to display
 * @param modifier Optional modifier for the composable
 */
@Composable
private fun profileHeader(
    user: User,
    modifier: Modifier = Modifier,
) {
    val displayName = user.displayName ?: user.name

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        userAvatar(
            user = user,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Card displaying basic user information including role.
 *
 * @param user The user whose information to display
 * @param modifier Optional modifier for the composable
 */
@Composable
private fun userInfoCard(
    user: User,
    modifier: Modifier = Modifier,
) {
    val roleText = when (user.role) {
        UserRole.CHILD -> stringResource(R.string.child_role)
        UserRole.CAREGIVER -> stringResource(R.string.caregiver_role)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.profile_info_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            profileInfoRow(
                label = stringResource(R.string.name_label),
                value = user.displayName ?: user.name,
            )

            Spacer(modifier = Modifier.height(12.dp))

            profileInfoRow(
                label = stringResource(R.string.role_label),
                value = roleText,
            )

            if (user.favoriteColor != null) {
                Spacer(modifier = Modifier.height(12.dp))

                profileInfoRow(
                    label = stringResource(R.string.favorite_color_label),
                    value = user.favoriteColor,
                )
            }
        }
    }
}

/**
 * Child-specific information section displaying token balance and achievements.
 *
 * @param user The child user whose information to display
 * @param modifier Optional modifier for the composable
 */
@Composable
private fun childSpecificInfo(
    user: User,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.child_stats_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.token_balance_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Text(
                        text = user.tokenBalance.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = stringResource(R.string.tokens_icon_content_desc),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

/**
 * Caregiver-specific information section displaying management options.
 *
 * @param user The caregiver user whose information to display
 * @param modifier Optional modifier for the composable
 */
@Composable
private fun caregiverSpecificInfo(
    user: User,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.caregiver_info_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.caregiver_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            if (user.pin != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(16.dp),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.pin_secured_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }
    }
}

/**
 * Row component for displaying profile information with label and value.
 *
 * @param label The label text
 * @param value The value text
 * @param modifier Optional modifier for the composable
 */
@Composable
private fun profileInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * User avatar component that displays either a predefined or custom avatar.
 *
 * @param user The user whose avatar to display
 * @param modifier Optional modifier for the composable
 */
@Composable
private fun userAvatar(
    user: User,
    modifier: Modifier = Modifier,
) {
    val contentDesc = stringResource(
        R.string.user_avatar_content_desc,
        user.displayName ?: user.name,
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .semantics {
                contentDescription = contentDesc
            },
        contentAlignment = Alignment.Center,
    ) {
        when (user.avatarType) {
            AvatarType.PREDEFINED -> {
                val avatarResId = getAvatarResourceId(user.avatarData)
                if (avatarResId != null) {
                    Icon(
                        painter = painterResource(avatarResId),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.default_avatar),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            AvatarType.CUSTOM -> {
                Icon(
                    painter = painterResource(R.drawable.default_avatar),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

/**
 * Maps avatar data string to drawable resource ID.
 *
 * @param avatarData The avatar identifier string
 * @return Drawable resource ID or null if not found
 */
private fun getAvatarResourceId(avatarData: String): Int? {
    return when (avatarData) {
        "default_child" -> R.drawable.default_avatar
        "mario", "mario_child" -> R.drawable.avatar_mario
        "luigi", "luigi_child" -> R.drawable.avatar_luigi
        "peach", "peach_child" -> R.drawable.avatar_peach
        "toad", "toad_child" -> R.drawable.avatar_toad
        "koopa", "koopa_child" -> R.drawable.avatar_koopa
        "goomba", "goomba_child" -> R.drawable.avatar_goomba
        "mushroom", "mushroom_child" -> R.drawable.avatar_mushroom
        "star", "star_child" -> R.drawable.avatar_star
        "avatar_caregiver", "caregiver" -> R.drawable.avatar_caregiver
        else -> null
    }
}
