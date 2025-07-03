package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun pinEntryScreen(
    targetRole: UserRole,
    onAuthSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var pin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when {
            authState.isAuthenticated && authState.currentRole == targetRole -> {
                onAuthSuccess()
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        pinEntryHeader(targetRole)

        Spacer(modifier = Modifier.height(16.dp))

        pinInputField(
            pin = pin,
            onPinChange = { newPin ->
                pin = newPin
                errorMessage = null
            },
            errorMessage = errorMessage,
        )

        Spacer(modifier = Modifier.height(16.dp))

        pinEntryActions(
            pin = pin,
            isLoading = isLoading,
            onCancel = onCancel,
            onAuthenticate = { newPin ->
                isLoading = true
                viewModel.authenticateWithPin(newPin) { result ->
                    isLoading = false
                    errorMessage = handleAuthResult(result, targetRole, onAuthSuccess)
                }
            },
        )
    }
}

@Composable
private fun pinEntryHeader(targetRole: UserRole) {
    Text(
        text = "Enter PIN for ${targetRole.name} Mode",
        style = MaterialTheme.typography.headlineMedium,
    )
}

@Composable
private fun pinInputField(
    pin: String,
    onPinChange: (String) -> Unit,
    errorMessage: String?,
) {
    OutlinedTextField(
        value = pin,
        onValueChange = onPinChange,
        label = { Text("PIN") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        isError = errorMessage != null,
    )

    errorMessage?.let { message ->
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun pinEntryActions(
    pin: String,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onAuthenticate: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(
            onClick = onCancel,
            enabled = !isLoading,
        ) {
            Text("Cancel")
        }

        authenticateButton(
            pin = pin,
            isLoading = isLoading,
            onAuthenticate = onAuthenticate,
        )
    }
}

@Composable
private fun authenticateButton(
    pin: String,
    isLoading: Boolean,
    onAuthenticate: (String) -> Unit,
) {
    Button(
        onClick = {
            if (pin.isNotBlank()) {
                onAuthenticate(pin)
            }
        },
        enabled = !isLoading && pin.isNotBlank(),
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier =
                    androidx.compose.ui.Modifier
                        .size(16.dp),
            )
        } else {
            Text("Authenticate")
        }
    }
}

private fun handleAuthResult(
    result: com.arthurslife.app.domain.auth.AuthResult,
    targetRole: UserRole,
    onAuthSuccess: () -> Unit,
): String? =
    when (result) {
        is com.arthurslife.app.domain.auth.AuthResult.Success -> {
            if (result.user.role == targetRole) {
                onAuthSuccess()
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
