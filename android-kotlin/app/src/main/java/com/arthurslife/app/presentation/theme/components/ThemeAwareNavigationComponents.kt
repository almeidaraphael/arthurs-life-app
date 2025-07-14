package com.arthurslife.app.presentation.theme.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.BaseAppTheme

// Constants for PIN handling
private const val PIN_LENGTH = 4

// Data classes to group related parameters
data class RoleSwitchDialogState(
    val targetRole: UserRole,
    val pin: String,
    val errorMessage: String?,
    val isLoading: Boolean,
)

data class RoleSwitchDialogActions(
    val onPinChange: (String) -> Unit,
    val onSwitchSuccess: () -> Unit,
    val onCancel: () -> Unit,
)

data class PinEntryState(
    val targetRole: UserRole,
    val pin: String,
    val errorMessage: String?,
    val isLoading: Boolean,
)

data class PinEntryActions(
    val onPinChange: (String) -> Unit,
    val onAuthenticate: (String) -> Unit,
    val onCancel: () -> Unit,
)

data class DialogActionsState(
    val targetRole: UserRole,
    val pin: String,
    val isLoading: Boolean,
)

data class DialogActionsCallbacks(
    val onCancel: () -> Unit,
    val onSwitch: () -> Unit,
)

@Composable
fun ThemeAwareRoleSelectionButtons(
    theme: BaseAppTheme,
    onChildAccess: () -> Unit,
    onCaregiverAccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RoleSelectionHeader(theme = theme)

        Spacer(modifier = Modifier.height(16.dp))

        RoleSelectionButton(
            theme = theme,
            role = UserRole.CHILD,
            onClick = onChildAccess,
        )

        RoleSelectionButton(
            theme = theme,
            role = UserRole.CAREGIVER,
            onClick = onCaregiverAccess,
        )
    }
}

@Composable
fun RoleSelectionHeader(theme: BaseAppTheme) {
    Text(
        text = theme.roleSelectionPrompt,
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun RoleSelectionButton(
    theme: BaseAppTheme,
    role: UserRole,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = theme.shapes.small,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.AVATAR,
                theme = theme,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = getRoleButtonText(theme, role),
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

fun getRoleButtonText(theme: BaseAppTheme, role: UserRole): String {
    return theme.roleButtonText(role)
}

@Composable
fun ThemeAwareRoleSwitchingDialog(
    theme: BaseAppTheme,
    state: RoleSwitchDialogState,
    actions: RoleSwitchDialogActions,
    modifier: Modifier = Modifier,
) {
    println("ThemeAwareRoleSwitchingDialog: DIALOG COMPOSABLE CREATED for role ${state.targetRole}")
    Dialog(
        onDismissRequest = {
            println(
                "ThemeAwareRoleSwitchingDialog: onDismissRequest called, isLoading: ${state.isLoading}",
            )
            // Only allow dismissal if not currently loading/processing
            if (!state.isLoading) {
                println("ThemeAwareRoleSwitchingDialog: Calling actions.onCancel()")
                actions.onCancel()
            }
        },
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = theme.dialogShape,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ThemeAwareDialogHeader(
                    theme = theme,
                    targetRole = state.targetRole,
                )

                if (state.targetRole == UserRole.CAREGIVER) {
                    ThemeAwarePinSection(
                        theme = theme,
                        pin = state.pin,
                        onPinChange = actions.onPinChange,
                        errorMessage = state.errorMessage,
                    )
                } else {
                    ThemeAwareChildAccessSection(theme = theme)
                }

                Spacer(modifier = Modifier.height(8.dp))

                ThemeAwareDialogActions(
                    theme = theme,
                    state = DialogActionsState(
                        targetRole = state.targetRole,
                        pin = state.pin,
                        isLoading = state.isLoading,
                    ),
                    callbacks = DialogActionsCallbacks(
                        onCancel = actions.onCancel,
                        onSwitch = actions.onSwitchSuccess,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ThemeAwareDialogHeader(
    theme: BaseAppTheme,
    targetRole: UserRole,
) {
    Text(
        text = theme.getRoleSwitchHeader(targetRole),
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ThemeAwarePinSection(
    theme: BaseAppTheme,
    pin: String,
    onPinChange: (String) -> Unit,
    errorMessage: String?,
) {
    Text(
        text = theme.pinEntryPrompt,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = pin,
        onValueChange = onPinChange,
        label = {
            Text(theme.pinLabel)
        },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        isError = errorMessage != null,
        modifier = Modifier.fillMaxWidth(),
        shape = theme.pinFieldShape,
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
private fun ThemeAwareChildAccessSection(theme: BaseAppTheme) {
    Text(
        text = theme.childAccessPrompt,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ThemeAwareDialogActions(
    theme: BaseAppTheme,
    state: DialogActionsState,
    callbacks: DialogActionsCallbacks,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextButton(
            onClick = callbacks.onCancel,
            enabled = !state.isLoading,
            modifier = Modifier.weight(1f),
            shape = theme.buttonShape,
        ) {
            Text(theme.cancelButtonText)
        }

        Button(
            onClick = callbacks.onSwitch,
            enabled = !state.isLoading && (state.targetRole == UserRole.CHILD || state.pin.isNotBlank()),
            modifier = Modifier.weight(1f),
            shape = theme.buttonShape,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(theme.switchButtonText)
            }
        }
    }
}

@Composable
fun ThemeAwarePinEntryScreen(
    theme: BaseAppTheme,
    state: PinEntryState,
    actions: PinEntryActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        Text(
            text = theme.pinEntryHeader(state.targetRole),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        // PIN input field
        ThemeAwarePinInputField(
            theme = theme,
            pin = state.pin,
            onPinChange = actions.onPinChange,
            errorMessage = state.errorMessage,
        )

        // Action buttons
        ThemeAwarePinEntryActions(
            theme = theme,
            pin = state.pin,
            isLoading = state.isLoading,
            onCancel = actions.onCancel,
            onAuthenticate = actions.onAuthenticate,
        )
    }
}

@Composable
private fun ThemeAwarePinInputField(
    theme: BaseAppTheme,
    pin: String,
    onPinChange: (String) -> Unit,
    errorMessage: String?,
) {
    OutlinedTextField(
        value = pin,
        onValueChange = { newPin ->
            // Child safety: Restrict to numeric input only and limit length
            val sanitizedPin = newPin.filter { it.isDigit() }.take(PIN_LENGTH)
            onPinChange(sanitizedPin)
        },
        label = {
            Text(theme.pinLabel)
        },
        placeholder = {
            Text("••••") // Visual placeholder showing expected PIN format
        },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        isError = errorMessage != null,
        shape = theme.pinFieldShape,
        modifier = Modifier.semantics {
            contentDescription = if (errorMessage != null) {
                "${theme.pinLabel}. Error: $errorMessage"
            } else {
                "${theme.pinLabel}. Enter your 4-digit PIN to continue."
            }
        },
    )

    errorMessage?.let { message ->
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics {
                contentDescription = "Error: $message. Please try again."
            },
        )
    }
}

@Composable
private fun ThemeAwarePinEntryActions(
    theme: BaseAppTheme,
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
            shape = theme.buttonShape,
            modifier = Modifier.semantics {
                contentDescription = "${theme.cancelButtonText}. Cancel PIN entry and go back."
            },
        ) {
            Text(theme.cancelButtonText)
        }

        Button(
            onClick = {
                if (pin.isNotBlank()) {
                    onAuthenticate(pin)
                }
            },
            enabled = !isLoading && pin.isNotBlank(),
            shape = theme.buttonShape,
            modifier = Modifier.semantics {
                contentDescription = if (isLoading) {
                    "Authenticating, please wait"
                } else if (pin.isBlank()) {
                    "${theme.authenticateButtonText}. Please enter your PIN first."
                } else {
                    "${theme.authenticateButtonText}. Submit your PIN to continue."
                }
            },
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(theme.authenticateButtonText)
            }
        }
    }
}
