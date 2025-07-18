package com.lemonqwest.app.presentation.screens.onboarding

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.R
import com.lemonqwest.app.domain.user.AvatarType
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.LocalBaseTheme
import com.lemonqwest.app.presentation.theme.components.AvatarSelectionCard
import com.lemonqwest.app.presentation.viewmodels.OnboardingViewModel
import java.io.ByteArrayOutputStream

private const val PIN_LENGTH = 4
private const val PNG_COMPRESSION_QUALITY = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilySetupScreen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val uiState = viewModel.uiState
    val theme = LocalBaseTheme.current

    Scaffold(
        topBar = { FamilySetupTopBar(theme, onBack) },
    ) { paddingValues ->
        FamilySetupContent(paddingValues, uiState, viewModel, onBack, onContinue)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FamilySetupTopBar(
    theme: com.lemonqwest.app.presentation.theme.BaseAppTheme,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                "Family Setup",
                color = theme.textColors.primary,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.semantics {
                    contentDescription = "Go back to welcome screen"
                    role = Role.Button
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = theme.textColors.primary,
                )
            }
        },
    )
}

@Composable
private fun FamilySetupContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    uiState: com.lemonqwest.app.presentation.viewmodels.OnboardingUiState,
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            FamilySetupContent(uiState, viewModel)
            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            FamilySetupNavigationButtons(viewModel, onBack, onContinue)
        }
    }
}

@Composable
private fun FamilySetupNavigationButtons(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = "Go back to welcome screen"
                    role = Role.Button
                },
        ) {
            Text("Back")
        }

        Button(
            onClick = onContinue,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = if (viewModel.validateCaregiverSetup()) {
                        "Continue to add children - caregiver setup is complete"
                    } else {
                        "Complete caregiver name and PIN setup to continue"
                    }
                    role = Role.Button
                },
            enabled = viewModel.validateCaregiverSetup(),
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun FamilySetupContent(
    uiState: com.lemonqwest.app.presentation.viewmodels.OnboardingUiState,
    viewModel: OnboardingViewModel,
) {
    var customAvatarBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SetupHeader()
        NameInputCard(uiState.caregiverName, viewModel::updateCaregiverName)
        AvatarSelectionCard(
            currentAvatarType = uiState.caregiverAvatarType,
            currentAvatarData = uiState.caregiverAvatarData,
            currentProfileImage = customAvatarBitmap,
            onPredefinedAvatarSelected = { avatarId ->
                viewModel.updateCaregiverAvatar(AvatarType.PREDEFINED, avatarId)
            },
            onCustomAvatarSelected = { bitmap ->
                customAvatarBitmap = bitmap
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, PNG_COMPRESSION_QUALITY, outputStream)
                val base64 = android.util.Base64.encodeToString(
                    outputStream.toByteArray(),
                    android.util.Base64.DEFAULT,
                )
                val imageData = "data:image/png;base64,$base64"
                viewModel.updateCaregiverAvatar(AvatarType.CUSTOM, imageData)
            },
        )
        PinSetupCard(uiState.caregiverPin, viewModel::updateCaregiverPin)
    }
}

@Composable
private fun SetupHeader() {
    val theme = LocalBaseTheme.current

    val context = LocalContext.current
    Text(
        text = context.getString(R.string.caregiver_setup_header),
        style = MaterialTheme.typography.headlineSmall,
        color = theme.textColors.primary,
    )
    Text(
        text = context.getString(R.string.caregiver_setup_subheader),
        style = MaterialTheme.typography.bodyMedium,
        color = theme.textColors.secondary,
    )
}

@Composable
private fun NameInputCard(
    name: String,
    onNameChange: (String) -> Unit,
) {
    val theme = LocalBaseTheme.current

    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.containerColors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = context.getString(R.string.caregiver_name_label),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textColors.primary,
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(context.getString(R.string.caregiver_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                colors = theme.outlinedTextFieldColors(),
                singleLine = true,
            )
        }
    }
}

@Composable
private fun PinSetupCard(
    pin: String,
    onPinChange: (String) -> Unit,
) {
    val theme = LocalBaseTheme.current
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.containerColors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = context.getString(R.string.security_pin_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textColors.primary,
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = context.getString(R.string.security_pin_description),
                style = MaterialTheme.typography.bodyMedium,
                color = theme.textColors.secondary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            SimplifiedPinSetup(
                pin = pin,
                onPinChange = onPinChange,
            )
        }
    }
}

@Composable
private fun SimplifiedPinSetup(
    pin: String,
    onPinChange: (String) -> Unit,
) {
    val theme = LocalBaseTheme.current
    val context = LocalContext.current
    var showPin by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = pin,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() } && newValue.length <= PIN_LENGTH) {
                onPinChange(newValue)
            }
        },
        label = { Text(context.getString(R.string.pin_digits_label)) },
        visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { showPin = !showPin }) {
                Icon(
                    imageVector = if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showPin) {
                        context.getString(R.string.hide_pin)
                    } else {
                        context.getString(R.string.show_pin)
                    },
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
        colors = theme.outlinedTextFieldColors(),
        singleLine = true,
        isError = pin.isNotEmpty() && pin.length < PIN_LENGTH,
    )

    if (pin.isNotEmpty() && pin.length < PIN_LENGTH) {
        Text(
            text = context.getString(R.string.pin_length_error),
            style = MaterialTheme.typography.bodySmall,
            color = theme.textColors.error,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
