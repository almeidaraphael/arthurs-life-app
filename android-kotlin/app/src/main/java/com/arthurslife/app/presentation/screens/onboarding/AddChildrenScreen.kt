package com.arthurslife.app.presentation.screens.onboarding

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arthurslife.app.R
import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.presentation.theme.LocalBaseTheme
import com.arthurslife.app.presentation.theme.components.avatarSelectionCard
import com.arthurslife.app.presentation.viewmodels.OnboardingViewModel

/**
 * Data class to group child form state parameters
 */
data class ChildFormState(
    val name: String,
    val avatarType: AvatarType,
    val avatarData: String,
    val profileImage: Bitmap?,
    val showForm: Boolean,
)

/**
 * Data class to group child form callback parameters
 */
data class ChildFormCallbacks(
    val onNameChange: (String) -> Unit,
    val onAvatarChange: (AvatarType, String, Bitmap?) -> Unit,
    val onShowFormChange: (Boolean) -> Unit,
    val onAddChild: () -> Unit,
    val onRemoveChild: () -> Unit,
    val onCancelForm: () -> Unit,
)

/**
 * Data class to group navigation callback parameters
 */
data class NavigationCallbacks(
    val onBack: () -> Unit,
    val onContinue: () -> Unit,
)

/**
 * Data class to group child profile form data
 */
data class ChildFormData(
    val childName: String,
    val childAvatarType: AvatarType,
    val childAvatarData: String,
    val childProfileImage: Bitmap?,
)

/**
 * Data class to group child profile form actions
 */
data class ChildFormActions(
    val onNameChange: (String) -> Unit,
    val onAvatarChange: (AvatarType, String, Bitmap?) -> Unit,
    val onSave: () -> Unit,
    val onCancel: () -> Unit,
)

/**
 * State holder for child form state management
 */
data class ChildFormStateHolder(
    val childName: String,
    val setChildName: (String) -> Unit,
    val childAvatarType: AvatarType,
    val setChildAvatarType: (AvatarType) -> Unit,
    val childAvatarData: String,
    val setChildAvatarData: (String) -> Unit,
    val childProfileImage: Bitmap?,
    val setChildProfileImage: (Bitmap?) -> Unit,
    val showForm: Boolean,
    val setShowForm: (Boolean) -> Unit,
)

/**
 * Parameters for main content area
 */
data class MainContentParams(
    val paddingValues: androidx.compose.foundation.layout.PaddingValues,
    val theme: com.arthurslife.app.presentation.theme.BaseAppTheme,
    val uiState: com.arthurslife.app.presentation.viewmodels.OnboardingUiState,
    val childFormState: ChildFormStateHolder,
    val viewModel: OnboardingViewModel,
    val keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
)

/**
 * Screen for adding child profiles during onboarding.
 * Supports adding one child with name and avatar selection as per MVP requirements.
 */
@Composable
fun addChildrenScreen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val theme = LocalBaseTheme.current
    val uiState = viewModel.uiState
    val keyboardController = LocalSoftwareKeyboardController.current

    val childFormState = rememberChildFormState(uiState)

    Scaffold(
        topBar = { addChildrenTopBar(theme, onBack) },
        containerColor = theme.containerColors.background,
    ) { paddingValues ->
        addChildrenMainContent(
            params = MainContentParams(
                paddingValues = paddingValues,
                theme = theme,
                uiState = uiState,
                childFormState = childFormState,
                viewModel = viewModel,
                keyboardController = keyboardController,
            ),
            navigationCallbacks = NavigationCallbacks(
                onBack = onBack,
                onContinue = onContinue,
            ),
        )
    }
}

@Composable
private fun addChildrenTopBar(
    theme: com.arthurslife.app.presentation.theme.BaseAppTheme,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.add_child_header),
                color = theme.textColors.primary,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.semantics {
                    contentDescription = "Go back to caregiver setup"
                    role = Role.Button
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = theme.textColors.primary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = theme.containerColors.surface,
        ),
    )
}

@Composable
private fun rememberChildFormState(
    uiState: com.arthurslife.app.presentation.viewmodels.OnboardingUiState,
): ChildFormStateHolder {
    var childName by remember { mutableStateOf("") }
    var childAvatarType by remember { mutableStateOf(AvatarType.PREDEFINED) }
    var childAvatarData by remember { mutableStateOf("mario_child") }
    var childProfileImage by remember { mutableStateOf<Bitmap?>(null) }
    var showForm by remember { mutableStateOf(uiState.children.isEmpty()) }

    return ChildFormStateHolder(
        childName = childName,
        setChildName = { childName = it },
        childAvatarType = childAvatarType,
        setChildAvatarType = { childAvatarType = it },
        childAvatarData = childAvatarData,
        setChildAvatarData = { childAvatarData = it },
        childProfileImage = childProfileImage,
        setChildProfileImage = { childProfileImage = it },
        showForm = showForm,
        setShowForm = { showForm = it },
    )
}

@Composable
private fun addChildrenMainContent(
    params: MainContentParams,
    navigationCallbacks: NavigationCallbacks,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(params.paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        addChildrenScreenContent(
            theme = params.theme,
            uiState = params.uiState,
            formState = createChildFormState(params.childFormState),
            formCallbacks = createChildFormCallbacks(
                childFormState = params.childFormState,
                viewModel = params.viewModel,
                keyboardController = params.keyboardController,
            ),
            navigationCallbacks = navigationCallbacks,
        )
    }
}

@Composable
private fun createChildFormState(childFormState: ChildFormStateHolder): ChildFormState {
    return ChildFormState(
        name = childFormState.childName,
        avatarType = childFormState.childAvatarType,
        avatarData = childFormState.childAvatarData,
        profileImage = childFormState.childProfileImage,
        showForm = childFormState.showForm,
    )
}

@Composable
private fun createChildFormCallbacks(
    childFormState: ChildFormStateHolder,
    viewModel: OnboardingViewModel,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
): ChildFormCallbacks {
    return ChildFormCallbacks(
        onNameChange = childFormState.setChildName,
        onAvatarChange = { type, data, image ->
            childFormState.setChildAvatarType(type)
            childFormState.setChildAvatarData(data)
            childFormState.setChildProfileImage(image)
        },
        onShowFormChange = childFormState.setShowForm,
        onAddChild = {
            if (childFormState.childName.isNotBlank()) {
                viewModel.addChild(
                    name = childFormState.childName.trim(),
                    avatarType = childFormState.childAvatarType,
                    avatarData = childFormState.childAvatarData,
                )
                childFormState.setShowForm(false)
                keyboardController?.hide()
            }
        },
        onRemoveChild = {
            viewModel.removeChild(0)
            childFormState.setShowForm(true)
            childFormState.setChildName("")
            childFormState.setChildAvatarType(AvatarType.PREDEFINED)
            childFormState.setChildAvatarData("mario_child")
            childFormState.setChildProfileImage(null)
        },
        onCancelForm = {
            childFormState.setShowForm(false)
            childFormState.setChildName("")
            childFormState.setChildAvatarType(AvatarType.PREDEFINED)
            childFormState.setChildAvatarData("mario_child")
            childFormState.setChildProfileImage(null)
        },
    )
}

@Composable
private fun addChildrenScreenContent(
    theme: com.arthurslife.app.presentation.theme.BaseAppTheme,
    uiState: com.arthurslife.app.presentation.viewmodels.OnboardingUiState,
    formState: ChildFormState,
    formCallbacks: ChildFormCallbacks,
    navigationCallbacks: NavigationCallbacks,
) {
    addChildrenHeader(theme)
    addChildrenFormSection(uiState, formState, formCallbacks)
    addChildrenNavigationSection(uiState, navigationCallbacks)
}

@Composable
private fun addChildrenHeader(
    theme: com.arthurslife.app.presentation.theme.BaseAppTheme,
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.add_child_subheader),
        style = MaterialTheme.typography.bodyLarge,
        color = theme.textColors.secondary,
        modifier = Modifier.semantics {
            contentDescription = "Instructions for adding child profiles"
        },
    )
}

@Composable
private fun addChildrenFormSection(
    uiState: com.arthurslife.app.presentation.viewmodels.OnboardingUiState,
    formState: ChildFormState,
    formCallbacks: ChildFormCallbacks,
) {
    if (uiState.children.isNotEmpty()) {
        existingChildCard(
            child = uiState.children.first(),
            onRemove = formCallbacks.onRemoveChild,
        )
    }

    if (formState.showForm && uiState.children.isEmpty()) {
        childProfileForm(
            formData = ChildFormData(
                childName = formState.name,
                childAvatarType = formState.avatarType,
                childAvatarData = formState.avatarData,
                childProfileImage = formState.profileImage,
            ),
            formActions = ChildFormActions(
                onNameChange = formCallbacks.onNameChange,
                onAvatarChange = formCallbacks.onAvatarChange,
                onSave = formCallbacks.onAddChild,
                onCancel = formCallbacks.onCancelForm,
            ),
        )
    }

    if (uiState.children.isEmpty() && !formState.showForm) {
        Button(
            onClick = { formCallbacks.onShowFormChange(true) },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Add a child profile"
                    role = Role.Button
                },
        ) {
            Icon(
                imageVector = Icons.Default.ChildCare,
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.add_child_button),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun addChildrenNavigationSection(
    uiState: com.arthurslife.app.presentation.viewmodels.OnboardingUiState,
    navigationCallbacks: NavigationCallbacks,
) {
    Spacer(modifier = Modifier.height(24.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = navigationCallbacks.onBack,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = "Go back to caregiver setup"
                    role = Role.Button
                },
        ) {
            Text("Back")
        }

        Button(
            onClick = navigationCallbacks.onContinue,
            enabled = uiState.children.isNotEmpty(),
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = if (uiState.children.isNotEmpty()) {
                        "Continue to onboarding completion"
                    } else {
                        "Add at least one child to continue"
                    }
                    role = Role.Button
                },
        ) {
            Text("Continue")
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun existingChildCard(
    child: com.arthurslife.app.presentation.viewmodels.ChildSetupData,
    onRemove: () -> Unit,
) {
    val theme = LocalBaseTheme.current

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Child Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = theme.textColors.primary,
                        ),
                    )
                    Text(
                        text = child.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = theme.textColors.secondary,
                    )
                }

                FilledTonalButton(
                    onClick = onRemove,
                    modifier = Modifier.semantics {
                        contentDescription = "Remove ${child.name} from the family"
                        role = Role.Button
                    },
                ) {
                    Text("Remove")
                }
            }
        }
    }
}

@Composable
private fun childProfileForm(
    formData: ChildFormData,
    formActions: ChildFormActions,
) {
    val theme = LocalBaseTheme.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.containerColors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            childProfileFormHeader(theme)
            childProfileNameField(formData, formActions, theme)
            childProfileAvatarSection(formData, formActions)
            childProfileFormActions(formData, formActions)
        }
    }
}

@Composable
private fun childProfileFormHeader(
    theme: com.arthurslife.app.presentation.theme.BaseAppTheme,
) {
    Text(
        text = stringResource(R.string.add_child_form_header),
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = theme.textColors.primary,
        ),
    )
}

@Composable
private fun childProfileNameField(
    formData: ChildFormData,
    formActions: ChildFormActions,
    theme: com.arthurslife.app.presentation.theme.BaseAppTheme,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = formData.childName,
        onValueChange = formActions.onNameChange,
        label = {
            Text(
                text = stringResource(R.string.child_name_label),
                color = theme.textColors.secondary,
            )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.child_name_hint),
                color = theme.textColors.secondary.copy(alpha = 0.6f),
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Enter the child's name"
            },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Words,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            },
        ),
    )
}

@Composable
private fun childProfileAvatarSection(
    formData: ChildFormData,
    formActions: ChildFormActions,
) {
    avatarSelectionCard(
        currentAvatarType = formData.childAvatarType,
        currentAvatarData = formData.childAvatarData,
        currentProfileImage = formData.childProfileImage,
        onPredefinedAvatarSelected = { avatarId ->
            formActions.onAvatarChange(AvatarType.PREDEFINED, avatarId, null)
        },
        onCustomAvatarSelected = { bitmap ->
            val customData = "custom_${System.currentTimeMillis()}"
            formActions.onAvatarChange(AvatarType.CUSTOM, customData, bitmap)
        },
    )
}

@Composable
private fun childProfileFormActions(
    formData: ChildFormData,
    formActions: ChildFormActions,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = formActions.onCancel,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = "Cancel adding child"
                    role = Role.Button
                },
        ) {
            Text(stringResource(R.string.cancel_button))
        }

        Button(
            onClick = formActions.onSave,
            enabled = formData.childName.isNotBlank(),
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = if (formData.childName.isNotBlank()) {
                        "Save child profile for ${formData.childName.trim()}"
                    } else {
                        "Enter a name to save child profile"
                    }
                    role = Role.Button
                },
        ) {
            Text(stringResource(R.string.add_child_button))
        }
    }
}
