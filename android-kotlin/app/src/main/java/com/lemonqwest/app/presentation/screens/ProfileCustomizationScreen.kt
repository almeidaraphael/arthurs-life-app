package com.lemonqwest.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.LocalBaseTheme
import com.lemonqwest.app.presentation.theme.components.AvatarSelectionCard
import com.lemonqwest.app.presentation.theme.components.ColorSelectionCard
import com.lemonqwest.app.presentation.theme.components.PinManagementCard
import com.lemonqwest.app.presentation.viewmodels.ProfileCustomizationUiState
import com.lemonqwest.app.presentation.viewmodels.ProfileCustomizationViewModel

/**
 * Screen for comprehensive profile customization.
 *
 * Allows users to:
 * - Change display name
 * - Select avatar (predefined or custom)
 * - Choose favorite color
 * - Manage PIN (caregivers only)
 *
 * @param userId ID of the user to customize
 * @param onNavigateBack Callback when back navigation is requested
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCustomizationScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: ProfileCustomizationViewModel = hiltViewModel(),
) {
    val theme = LocalBaseTheme.current
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load user data when screen opens
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    // Show error messages in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    // Show PIN change success message
    LaunchedEffect(uiState.pinChangeSuccess) {
        if (uiState.pinChangeSuccess) {
            snackbarHostState.showSnackbar(theme.profileText.pinChangeSuccessMessage)
            viewModel.clearPinChangeSuccess()
        }
    }

    Scaffold(
        topBar = {
            ProfileCustomizationTopBar(
                theme = theme,
                onNavigateBack = onNavigateBack,
            )
        },
        floatingActionButton = {
            ProfileCustomizationFab(
                theme = theme,
                isSaving = uiState.isSaving,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = theme.containerColors.background,
    ) { paddingValues ->
        ProfileCustomizationContent(
            paddingValues = paddingValues,
            uiState = uiState,
            currentUser = currentUser,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun ProfileCustomizationTopBar(
    theme: BaseAppTheme,
    onNavigateBack: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = theme.profileText.customizationTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textColors.primary,
                ),
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = theme.textColors.primary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = theme.containerColors.surface,
            titleContentColor = theme.textColors.primary,
            navigationIconContentColor = theme.textColors.primary,
        ),
    )
}

@Composable
private fun ProfileCustomizationFab(
    theme: BaseAppTheme,
    isSaving: Boolean,
) {
    if (isSaving) {
        FloatingActionButton(
            onClick = { },
            containerColor = theme.containerColors.primary.copy(alpha = 0.6f),
        ) {
            CircularProgressIndicator(
                color = theme.textColors.onPrimary,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun ProfileCustomizationContent(
    paddingValues: PaddingValues,
    uiState: ProfileCustomizationUiState,
    currentUser: User?,
    viewModel: ProfileCustomizationViewModel,
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = LocalBaseTheme.current.containerColors.primary,
            )
        }
    } else {
        currentUser?.let { user ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                ProfileCustomizationSections(
                    uiState = uiState,
                    user = user,
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Composable
private fun ProfileCustomizationSections(
    uiState: ProfileCustomizationUiState,
    user: User,
    viewModel: ProfileCustomizationViewModel,
) {
    // Display Name Section
    DisplayNameSection(
        displayName = uiState.displayName,
        originalName = user.name,
        onDisplayNameChange = viewModel::updateDisplayNameLocal,
        onSave = viewModel::updateDisplayName,
    )

    // Avatar Selection Section
    AvatarSelectionCard(
        currentAvatarType = uiState.avatarType,
        currentAvatarData = uiState.avatarData,
        currentProfileImage = uiState.profileImage,
        onPredefinedAvatarSelected = viewModel::selectPredefinedAvatar,
        onCustomAvatarSelected = viewModel::selectCustomAvatar,
    )

    // Favorite Color Section
    ColorSelectionCard(
        selectedColor = uiState.selectedColor,
        onColorSelected = viewModel::updateFavoriteColor,
    )

    // PIN Management Section (Caregivers only)
    if (user.role == UserRole.CAREGIVER) {
        PinManagementCard(
            userRole = user.role,
            hasPin = user.pin != null,
            onPinChange = viewModel::changePin,
            onPinRemove = viewModel::removePin,
            isLoading = uiState.isSaving,
        )
    }

    // Bottom spacer for FAB
    Spacer(modifier = Modifier.height(80.dp))
}

/**
 * Display name editing section.
 */
@Composable
private fun DisplayNameSection(
    displayName: String,
    originalName: String,
    onDisplayNameChange: (String) -> Unit,
    onSave: (String) -> Unit,
) {
    val theme = LocalBaseTheme.current

    Column {
        Text(
            text = theme.profileText.displayNameTitle,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = theme.textColors.primary,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = theme.profileText.displayNameDescription,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = theme.textColors.secondary,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = onDisplayNameChange,
            label = {
                Text(theme.profileText.displayNameLabel)
            },
            placeholder = {
                Text(originalName)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = theme.outlinedTextFieldColors(),
            singleLine = true,
            trailingIcon = {
                if (displayName != originalName && displayName.isNotBlank()) {
                    IconButton(onClick = { onSave(displayName) }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save display name",
                            tint = theme.containerColors.primary,
                        )
                    }
                }
            },
        )
    }
}
