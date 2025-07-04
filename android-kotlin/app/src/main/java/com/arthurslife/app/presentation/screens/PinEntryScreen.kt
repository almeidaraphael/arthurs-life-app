package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.PinEntryActions
import com.arthurslife.app.presentation.theme.components.PinEntryState
import com.arthurslife.app.presentation.theme.components.ThemeAwarePinEntryScreen
import com.arthurslife.app.presentation.viewmodels.AuthViewModel

@Composable
fun PinEntryScreen(
    targetRole: UserRole,
    themeViewModel: ThemeViewModel,
    onAuthSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var pin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()
    val theme by themeViewModel.currentTheme.collectAsState()

    LaunchedEffect(authState) {
        when {
            authState.isAuthenticated && authState.currentRole == targetRole -> {
                onAuthSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ThemeAwarePinEntryScreen(
            theme = theme,
            state = PinEntryState(
                targetRole = targetRole,
                pin = pin,
                errorMessage = errorMessage,
                isLoading = isLoading,
            ),
            actions = PinEntryActions(
                onPinChange = { newPin ->
                    pin = newPin
                    errorMessage = null
                },
                onAuthenticate = { newPin ->
                    isLoading = true
                    viewModel.authenticateWithPin(newPin) { result ->
                        isLoading = false
                        errorMessage = handleAuthResult(result, targetRole)
                    }
                },
                onCancel = onCancel,
            ),
        )
    }
}

private fun handleAuthResult(
    result: com.arthurslife.app.domain.auth.AuthResult,
    targetRole: UserRole,
): String? =
    when (result) {
        is com.arthurslife.app.domain.auth.AuthResult.Success -> {
            if (result.user.role == targetRole) {
                // Authentication success is handled by LaunchedEffect watching authState
                null
            } else {
                "Wrong role for this PIN"
            }
        }
        is com.arthurslife.app.domain.auth.AuthResult.InvalidPin -> {
            "Invalid PIN"
        }
        is com.arthurslife.app.domain.auth.AuthResult.UserNotFound -> {
            "User not found"
        }
    }
