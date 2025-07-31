package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.LocalBaseTheme

/**
 * PIN management component for caregivers to change or remove their PIN.
 *
 * @param userRole Current user role
 * @param hasPin Whether the user currently has a PIN set
 * @param onPinChange Callback when PIN should be changed
 * @param onPinRemove Callback when PIN should be removed
 * @param isLoading Whether a PIN operation is in progress
 * @param modifier Modifier for styling
 */
@Composable
fun PinManagementCard(
    userRole: UserRole,
    hasPin: Boolean,
    onPinChange: (currentPin: String?, newPin: String) -> Unit,
    onPinRemove: (currentPin: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val theme = LocalBaseTheme.current

    // Only show PIN management for caregivers
    if (userRole != UserRole.CAREGIVER) return

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
                text = theme.profileText.pinSectionTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textColors.primary,
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (hasPin) {
                    theme.profileText.pinSetDescription
                } else {
                    theme.profileText.pinNotSetDescription
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = theme.textColors.secondary,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (hasPin) {
                PinChangeSection(
                    onPinChange = onPinChange,
                    onPinRemove = onPinRemove,
                    isLoading = isLoading,
                )
            } else {
                PinSetupSection(
                    onPinSet = { newPin -> onPinChange(null, newPin) },
                    isLoading = isLoading,
                )
            }
        }
    }
}

/**
 * Section for changing or removing an existing PIN.
 */
@Composable
private fun PinChangeSection(
    onPinChange: (currentPin: String?, newPin: String) -> Unit,
    onPinRemove: (currentPin: String) -> Unit,
    isLoading: Boolean,
) {
    val theme = LocalBaseTheme.current
    var showChangePinForm by remember { mutableStateOf(false) }
    var showRemovePinForm by remember { mutableStateOf(false) }

    when {
        !showChangePinForm && !showRemovePinForm -> {
            PinActionButtons(
                theme = theme,
                isLoading = isLoading,
                onShowChangeForm = { showChangePinForm = true },
                onShowRemoveForm = { showRemovePinForm = true },
            )
        }
        showChangePinForm -> {
            PinChangeForm(
                theme = theme,
                isLoading = isLoading,
                onPinChange = onPinChange,
                onCancel = { showChangePinForm = false },
            )
        }
        showRemovePinForm -> {
            PinRemoveForm(
                theme = theme,
                isLoading = isLoading,
                onPinRemove = onPinRemove,
                onCancel = { showRemovePinForm = false },
            )
        }
    }
}

@Composable
private fun PinActionButtons(
    theme: BaseAppTheme,
    isLoading: Boolean,
    onShowChangeForm: () -> Unit,
    onShowRemoveForm: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onShowChangeForm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.containerColors.primary,
            ),
            enabled = !isLoading,
        ) {
            Text(theme.profileText.changePinButton)
        }

        OutlinedButton(
            onClick = onShowRemoveForm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = theme.textColors.error,
            ),
            enabled = !isLoading,
        ) {
            Text(theme.profileText.removePinButton)
        }
    }
}

@Composable
private fun PinChangeForm(
    theme: BaseAppTheme,
    isLoading: Boolean,
    onPinChange: (currentPin: String?, newPin: String) -> Unit,
    onCancel: () -> Unit,
) {
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var showCurrentPin by remember { mutableStateOf(false) }
    var showNewPin by remember { mutableStateOf(false) }
    var showConfirmPin by remember { mutableStateOf(false) }

    Column {
        PinTextField(
            value = currentPin,
            onValueChange = { currentPin = it },
            label = theme.profileText.currentPinLabel,
            isPassword = !showCurrentPin,
            onVisibilityToggle = { showCurrentPin = !showCurrentPin },
        )

        Spacer(modifier = Modifier.height(12.dp))

        PinTextField(
            value = newPin,
            onValueChange = { newPin = it },
            label = theme.profileText.newPinLabel,
            isPassword = !showNewPin,
            onVisibilityToggle = { showNewPin = !showNewPin },
        )

        Spacer(modifier = Modifier.height(12.dp))

        PinTextField(
            value = confirmPin,
            onValueChange = { confirmPin = it },
            label = theme.profileText.confirmPinLabel,
            isPassword = !showConfirmPin,
            onVisibilityToggle = { showConfirmPin = !showConfirmPin },
        )

        Spacer(modifier = Modifier.height(16.dp))

        PinFormActions(
            PinFormActionsConfig(
                theme = theme,
                isLoading = isLoading,
                isEnabled = newPin == confirmPin && currentPin.isNotBlank() && newPin.isNotBlank(),
                onConfirm = {
                    onPinChange(currentPin, newPin)
                    onCancel()
                },
                onCancel = onCancel,
                confirmText = theme.profileText.changeButton,
            ),
        )
    }
}

@Composable
private fun PinRemoveForm(
    theme: BaseAppTheme,
    isLoading: Boolean,
    onPinRemove: (currentPin: String) -> Unit,
    onCancel: () -> Unit,
) {
    var currentPin by remember { mutableStateOf("") }
    var showCurrentPin by remember { mutableStateOf(false) }

    Column {
        PinTextField(
            value = currentPin,
            onValueChange = { currentPin = it },
            label = theme.profileText.currentPinLabel,
            isPassword = !showCurrentPin,
            onVisibilityToggle = { showCurrentPin = !showCurrentPin },
        )

        Spacer(modifier = Modifier.height(16.dp))

        PinFormActions(
            PinFormActionsConfig(
                theme = theme,
                isLoading = isLoading,
                isEnabled = currentPin.isNotBlank(),
                onConfirm = {
                    onPinRemove(currentPin)
                    onCancel()
                },
                onCancel = onCancel,
                confirmText = theme.profileText.removeButton,
                isDestructive = true,
            ),
        )
    }
}

data class PinFormActionsConfig(
    val theme: BaseAppTheme,
    val isLoading: Boolean,
    val isEnabled: Boolean,
    val onConfirm: () -> Unit,
    val onCancel: () -> Unit,
    val confirmText: String,
    val isDestructive: Boolean = false,
)

@Composable
private fun PinFormActions(config: PinFormActionsConfig) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = config.onCancel,
            modifier = Modifier.weight(1f),
            enabled = !config.isLoading,
        ) {
            Text(config.theme.profileText.cancelButton)
        }

        Button(
            onClick = config.onConfirm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (config.isDestructive) config.theme.textColors.error else config.theme.containerColors.primary,
            ),
            enabled = !config.isLoading && config.isEnabled,
        ) {
            Text(config.confirmText)
        }
    }
}

/**
 * Section for setting up a new PIN.
 */
@Composable
private fun PinSetupSection(
    onPinSet: (newPin: String) -> Unit,
    isLoading: Boolean,
) {
    val theme = LocalBaseTheme.current
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var showNewPin by remember { mutableStateOf(false) }
    var showConfirmPin by remember { mutableStateOf(false) }

    Column {
        PinTextField(
            value = newPin,
            onValueChange = { newPin = it },
            label = theme.profileText.newPinLabel,
            isPassword = !showNewPin,
            onVisibilityToggle = { showNewPin = !showNewPin },
        )

        Spacer(modifier = Modifier.height(12.dp))

        PinTextField(
            value = confirmPin,
            onValueChange = { confirmPin = it },
            label = theme.profileText.confirmPinLabel,
            isPassword = !showConfirmPin,
            onVisibilityToggle = { showConfirmPin = !showConfirmPin },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (newPin == confirmPin && newPin.isNotBlank()) {
                    onPinSet(newPin)
                    newPin = ""
                    confirmPin = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.containerColors.primary,
            ),
            enabled = !isLoading && newPin == confirmPin && newPin.isNotBlank(),
        ) {
            Text(theme.profileText.setPinButton)
        }
    }
}

/**
 * PIN input text field with visibility toggle.
 */
@Composable
private fun PinTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean,
    onVisibilityToggle: () -> Unit,
) {
    val theme = LocalBaseTheme.current

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Only allow digits and limit length
            if (newValue.all { it.isDigit() } && newValue.length <= 8) {
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Icon(
                    imageVector = if (isPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isPassword) "Show PIN" else "Hide PIN",
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
        colors = theme.outlinedTextFieldColors(),
        singleLine = true,
    )
}
