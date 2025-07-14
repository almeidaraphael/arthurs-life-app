package com.arthurslife.app.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.RoleSwitchDialogActions
import com.arthurslife.app.presentation.theme.components.RoleSwitchDialogState
import com.arthurslife.app.presentation.theme.components.ThemeAwareRoleSwitchingDialog
import com.arthurslife.app.presentation.viewmodels.AuthViewModel

/**
 * Dialog for switching to a specific user.
 *
 * This dialog handles user switching with appropriate authentication:
 * - For caregiver users: Requires PIN entry
 * - For child users: Direct switching without PIN
 *
 * @param targetUser The user to switch to
 * @param themeViewModel Theme management ViewModel
 * @param onSwitchSuccess Callback when user switching succeeds
 * @param onCancel Callback when user cancels the operation
 * @param viewModel Authentication ViewModel for handling user switching
 */
@Composable
fun userSwitchDialog(
    targetUser: User,
    themeViewModel: ThemeViewModel,
    onSwitchSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    println("UserSwitchDialog: COMPOSABLE CREATED for user ${targetUser.id}")
    var pin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var switchInProgress by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    // Handle authentication success scenarios
    LaunchedEffect(authState.currentUser?.id) {
        println(
            "UserSwitchDialog: LaunchedEffect triggered - currentUser: ${authState.currentUser?.id}, targetUser: ${targetUser.id}, switchInProgress: $switchInProgress, isAuthenticated: ${authState.isAuthenticated}",
        )

        if (authState.isAuthenticated && authState.currentUser?.id == targetUser.id) {
            if (switchInProgress) {
                // Switch operation completed successfully
                println(
                    "UserSwitchDialog: Switch completed successfully, calling onSwitchSuccess()",
                )
                onSwitchSuccess()
            } else {
                // Already authenticated as target user from start
                println(
                    "UserSwitchDialog: Already authenticated as target user, calling onSwitchSuccess()",
                )
                onSwitchSuccess()
            }
        }
    }

    ThemeAwareRoleSwitchingDialog(
        theme = currentTheme,
        state = RoleSwitchDialogState(
            targetRole = targetUser.role,
            pin = pin,
            errorMessage = errorMessage,
            isLoading = isLoading,
        ),
        actions = RoleSwitchDialogActions(
            onPinChange = { newPin ->
                pin = newPin
                errorMessage = null
            },
            onSwitchSuccess = {
                if (!switchInProgress && !isLoading) {
                    switchInProgress = true
                    handleUserSwitch(
                        targetUser = targetUser,
                        pin = pin,
                        viewModel = viewModel,
                        onLoadingChange = { isLoading = it },
                        onError = {
                            errorMessage = it
                            switchInProgress = false
                        },
                    )
                }
            },
            onCancel = onCancel,
        ),
    )
}

/**
 * Handles the user switching logic based on the target user's role.
 *
 * @param targetUser The user to switch to
 * @param pin PIN entered by the user (only used for caregivers)
 * @param viewModel AuthViewModel for performing authentication
 * @param onLoadingChange Callback for loading state changes
 * @param onError Callback for error handling
 */
private fun handleUserSwitch(
    targetUser: User,
    pin: String,
    viewModel: AuthViewModel,
    onLoadingChange: (Boolean) -> Unit,
    onError: (String) -> Unit,
) {
    when (targetUser.role) {
        UserRole.CAREGIVER -> {
            if (pin.isNotBlank()) {
                onLoadingChange(true)
                viewModel.switchToUser(targetUser, pin) { result ->
                    onLoadingChange(false)
                    when (result) {
                        is com.arthurslife.app.domain.auth.AuthResult.Success -> {
                            // Success case is handled by LaunchedEffect watching authState
                            // No need to call onSwitchSuccess here as it will be triggered automatically
                        }
                        is com.arthurslife.app.domain.auth.AuthResult.InvalidPin -> {
                            onError("Invalid PIN for ${targetUser.name}")
                        }
                        is com.arthurslife.app.domain.auth.AuthResult.UserNotFound -> {
                            onError("User not found")
                        }
                    }
                }
            } else {
                onError("PIN is required for caregiver access")
            }
        }
        UserRole.CHILD -> {
            onLoadingChange(true)
            viewModel.switchToUser(targetUser) { result ->
                onLoadingChange(false)
                when (result) {
                    is com.arthurslife.app.domain.auth.AuthResult.Success -> {
                        // Success case is handled by LaunchedEffect watching authState
                    }
                    is com.arthurslife.app.domain.auth.AuthResult.UserNotFound -> {
                        onError("User not found")
                    }
                    is com.arthurslife.app.domain.auth.AuthResult.InvalidPin -> {
                        onError("Unexpected authentication error")
                    }
                }
            }
        }
    }
}
