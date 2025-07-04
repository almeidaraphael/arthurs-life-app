package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun roleSwitchingDialog(
    targetRole: UserRole,
    onSwitchSuccess: () -> Unit,
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
                onSwitchSuccess()
            }
        }
    }

    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                dialogHeader(targetRole)

                if (targetRole == UserRole.CAREGIVER) {
                    caregiverPinSection(
                        pin = pin,
                        onPinChange = { newPin ->
                            pin = newPin
                            errorMessage = null
                        },
                        errorMessage = errorMessage,
                    )
                } else {
                    childAccessSection()
                }

                Spacer(modifier = Modifier.height(8.dp))

                dialogActions(
                    targetRole = targetRole,
                    pin = pin,
                    isLoading = isLoading,
                    onCancel = onCancel,
                    onSwitch = {
                        handleRoleSwitch(
                            targetRole = targetRole,
                            pin = pin,
                            viewModel = viewModel,
                            onLoadingChange = { isLoading = it },
                            onError = { errorMessage = it },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun dialogHeader(targetRole: UserRole) {
    Text(
        text = when (targetRole) {
            UserRole.CAREGIVER -> "Switch to Caregiver Mode"
            UserRole.CHILD -> "Switch to Child Mode"
        },
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun caregiverPinSection(
    pin: String,
    onPinChange: (String) -> Unit,
    errorMessage: String?,
) {
    Text(
        text = "Enter your PIN to access caregiver features",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = pin,
        onValueChange = onPinChange,
        label = { Text("PIN") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        isError = errorMessage != null,
        modifier = Modifier.fillMaxWidth(),
    )

    errorMessage?.let { message ->
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun childAccessSection() {
    Text(
        text = "Switch to child mode with no PIN required",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun dialogActions(
    targetRole: UserRole,
    pin: String,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onSwitch: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextButton(
            onClick = onCancel,
            enabled = !isLoading,
            modifier = Modifier.weight(1f),
        ) {
            Text("Cancel")
        }

        Button(
            onClick = onSwitch,
            enabled = !isLoading && (targetRole == UserRole.CHILD || pin.isNotBlank()),
            modifier = Modifier.weight(1f),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("Switch")
            }
        }
    }
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
