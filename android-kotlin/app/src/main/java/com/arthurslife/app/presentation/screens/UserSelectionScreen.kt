package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.R
import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.viewmodels.UserSelectionViewModel

/**
 * Screen for selecting a user from the family member list.
 *
 * This screen displays all users created during family setup with their names and avatars.
 * It can be used for both initial login and in-app user switching scenarios.
 * Updates in real-time if users change and provides accessibility support through
 * content descriptions and semantic roles.
 *
 * @param onUserSelected Callback invoked when a user is selected
 * @param title Optional custom title for the screen
 * @param modifier Optional modifier for the composable
 * @param viewModel ViewModel managing the user list and selection state
 */
@Composable
fun userSelectionScreen(
    onUserSelected: (User) -> Unit,
    title: String? = null,
    modifier: Modifier = Modifier,
    viewModel: UserSelectionViewModel = hiltViewModel(),
) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        userSelectionTitle(title)
        userSelectionContent(
            users = users,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onUserSelected = onUserSelected,
            onRetry = { viewModel.refreshUsers() },
        )
    }
}

@Composable
private fun userSelectionTitle(customTitle: String?) {
    val titleText = customTitle ?: stringResource(R.string.user_selection_title)
    Text(
        text = titleText,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 24.dp),
    )
}

@Composable
private fun userSelectionContent(
    users: List<User>,
    isLoading: Boolean,
    errorMessage: String?,
    onUserSelected: (User) -> Unit,
    onRetry: () -> Unit,
) {
    when {
        isLoading -> loadingContent()
        errorMessage != null -> errorContent(errorMessage, onRetry)
        users.isEmpty() -> emptyContent()
        else -> userListContent(users, onUserSelected)
    }
}

@Composable
private fun loadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics {
                contentDescription = "Loading users"
            },
        )
    }
}

@Composable
private fun errorContent(errorMessage: String, onRetry: () -> Unit) {
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
        retryButton(onRetry)
    }
}

@Composable
private fun retryButton(onRetry: () -> Unit) {
    Card(
        onClick = onRetry,
        modifier = Modifier
            .padding(16.dp)
            .semantics {
                role = Role.Button
                contentDescription = "Retry loading users"
            },
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
private fun emptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_users_found),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun userListContent(users: List<User>, onUserSelected: (User) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = users,
            key = { user -> user.id },
        ) { user ->
            userListItem(
                user = user,
                onUserSelected = onUserSelected,
            )
        }
    }
}

/**
 * Individual user item in the user selection list.
 *
 * Displays the user's avatar, name, and role in a card format. Provides accessibility
 * support through content descriptions and semantic roles.
 *
 * @param user The user to display
 * @param onUserSelected Callback invoked when this user is selected
 * @param modifier Optional modifier for the composable
 */
@Composable
private fun userListItem(
    user: User,
    onUserSelected: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayName = user.displayName ?: user.name
    val roleText = when (user.role) {
        UserRole.CHILD -> stringResource(R.string.child_role)
        UserRole.CAREGIVER -> stringResource(R.string.caregiver_role)
    }

    Card(
        onClick = { onUserSelected(user) },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Button
                contentDescription = "Select $displayName as $roleText"
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            userAvatar(
                user = user,
                modifier = Modifier.size(56.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = roleText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

/**
 * User avatar component that displays either a predefined or custom avatar.
 *
 * Handles different avatar types and provides a fallback default avatar if needed.
 * Includes accessibility content description for screen readers.
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
                    // Fallback to default avatar
                    Icon(
                        painter = painterResource(R.drawable.default_avatar),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            AvatarType.CUSTOM -> {
                // For now, show default avatar for custom types
                // In a full implementation, this would decode base64 image data
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
