package com.arthurslife.app.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.RoleSwitchDialogActions
import com.arthurslife.app.presentation.theme.components.RoleSwitchDialogState
import com.arthurslife.app.presentation.theme.components.ThemeAwareRoleSwitchingDialog
import com.arthurslife.app.presentation.viewmodels.AuthViewModel

@Composable
fun RoleSwitchingDialog(
    targetRole: UserRole,
    themeViewModel: ThemeViewModel,
    onSwitchSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var pin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    LaunchedEffect(authState) {
        when {
            authState.isAuthenticated && authState.currentRole == targetRole -> {
                onSwitchSuccess()
            }
        }
    }

    ThemeAwareRoleSwitchingDialog(
        theme = currentTheme,
        state = RoleSwitchDialogState(
            targetRole = targetRole,
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
                handleRoleSwitch(
                    targetRole = targetRole,
                    pin = pin,
                    viewModel = viewModel,
                    onLoadingChange = { isLoading = it },
                    onError = { errorMessage = it },
                )
            },
            onCancel = onCancel,
        ),
    )
}

private fun handleRoleSwitch(
    targetRole: UserRole,
    pin: String,
    viewModel: AuthViewModel,
    onLoadingChange: (Boolean) -> Unit,
    onError: (String) -> Unit,
) {
    when (targetRole) {
        UserRole.CAREGIVER -> {
            if (pin.isNotBlank()) {
                onLoadingChange(true)
                viewModel.authenticateWithPin(pin) { result ->
                    onLoadingChange(false)
                    when (result) {
                        is com.arthurslife.app.domain.auth.AuthResult.Success -> {
                            if (result.user.role != targetRole) {
                                onError("Wrong PIN for caregiver access")
                            }
                        }
                        is com.arthurslife.app.domain.auth.AuthResult.InvalidPin -> {
                            onError("Invalid PIN")
                        }
                        is com.arthurslife.app.domain.auth.AuthResult.UserNotFound -> {
                            onError("User not found")
                        }
                    }
                }
            }
        }
        UserRole.CHILD -> {
            onLoadingChange(true)
            viewModel.authenticateAsChild { _ ->
                onLoadingChange(false)
            }
        }
    }
}
