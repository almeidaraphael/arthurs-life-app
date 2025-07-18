package com.lemonqwest.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.components.SemanticIconType
import com.lemonqwest.app.presentation.theme.components.ThemeAwareAlertDialog
import com.lemonqwest.app.presentation.theme.components.ThemeAwareButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareCard
import com.lemonqwest.app.presentation.theme.components.ThemeAwareDialogTextButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareFloatingActionButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareIcon
import com.lemonqwest.app.presentation.theme.components.ThemeAwareIconButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareOutlinedTextField
import com.lemonqwest.app.presentation.viewmodels.AuthViewModel

/**
 * Caregiver children management screen for LemonQwest MVP.
 *
 * This screen allows caregivers to:
 * - View all children in the family
 * - Add new children
 * - Edit existing children's information
 * - Manage children's settings and token balances
 *
 * The screen follows Material Design 3 principles and integrates with
 * the existing theme system for consistent UI experience.
 */
@Composable
fun CaregiverChildrenManagementScreen(
    currentTheme: BaseAppTheme,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val authState by authViewModel.authState.collectAsState()

    var showAddChildDialog by remember { mutableStateOf(false) }
    var editingChild by remember { mutableStateOf<User?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // For now, we'll get children from the auth state
    // In a real implementation, this would come from a dedicated ViewModel
    val children = remember(authState.currentUser) {
        // Mock children data until we have proper user management
        listOf(
            User(
                id = "child-1",
                name = "Lemmy",
                role = UserRole.CHILD,
            ),
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ThemeAwareFloatingActionButton(
                onClick = { showAddChildDialog = true },
                theme = currentTheme,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Child",
                    tint = currentTheme.colorScheme.onPrimary,
                )
            }
        },
        containerColor = currentTheme.colorScheme.background,
    ) { paddingValues ->
        ChildrenManagementContent(
            currentTheme = currentTheme,
            children = children,
            paddingValues = paddingValues,
            onEditChild = { editingChild = it },
        )
    }

    // Add Child Dialog
    if (showAddChildDialog) {
        AddChildDialog(
            currentTheme = currentTheme,
            onDismiss = { showAddChildDialog = false },
            onSave = { _ ->
                showAddChildDialog = false
            },
        )
    }

    // Edit Child Dialog
    editingChild?.let { child ->
        EditChildDialog(
            currentTheme = currentTheme,
            child = child,
            onDismiss = { editingChild = null },
            onSave = { _ ->
                editingChild = null
            },
        )
    }
}

@Composable
private fun ChildrenManagementContent(
    currentTheme: BaseAppTheme,
    children: List<User>,
    paddingValues: PaddingValues,
    onEditChild: (User) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Children Management",
                    style = currentTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Manage your children and their settings",
                    style = currentTheme.typography.bodyLarge,
                    color = currentTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                )
            }
        }

        // Children List
        if (children.isEmpty()) {
            item {
                EmptyChildrenState(currentTheme)
            }
        } else {
            items(children) { child ->
                ChildCard(
                    child = child,
                    currentTheme = currentTheme,
                    onEdit = { onEditChild(child) },
                )
            }
        }
    }
}

@Composable
private fun ChildCard(
    child: User,
    currentTheme: BaseAppTheme,
    onEdit: () -> Unit,
) {
    ThemeAwareCard(
        theme = currentTheme,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = child.name,
                    style = currentTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${child.tokenBalance.getValue()} ${currentTheme.tokenLabel}",
                    style = currentTheme.typography.bodyMedium,
                    color = currentTheme.colorScheme.primary,
                )
                Text(
                    text = "Active",
                    style = currentTheme.typography.bodySmall,
                    color = currentTheme.colorScheme.tertiary,
                )
            }

            ThemeAwareIconButton(
                onClick = onEdit,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Child",
                    tint = currentTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun EmptyChildrenState(currentTheme: BaseAppTheme) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.AVATAR,
                theme = currentTheme,
                modifier = Modifier.size(64.dp),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Children Added",
                style = currentTheme.typography.titleMedium,
                color = currentTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your first child using the + button",
                style = currentTheme.typography.bodyMedium,
                color = currentTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
private fun AddChildDialog(
    currentTheme: BaseAppTheme,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var childName by remember { mutableStateOf("") }

    ThemeAwareAlertDialog(
        onDismissRequest = onDismiss,
        theme = currentTheme,
        title = {
            Text(
                text = "Add New Child",
                style = currentTheme.typography.titleLarge,
                color = currentTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter the child's name:",
                    style = currentTheme.typography.bodyMedium,
                    color = currentTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ThemeAwareOutlinedTextField(
                    value = childName,
                    onValueChange = { childName = it },
                    theme = currentTheme,
                    label = "Child's Name",
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            ThemeAwareButton(
                text = "Add Child",
                onClick = {
                    if (childName.isNotBlank()) {
                        onSave(childName.trim())
                    }
                },
                theme = currentTheme,
                enabled = childName.isNotBlank(),
            )
        },
        dismissButton = {
            ThemeAwareDialogTextButton(
                text = "Cancel",
                onClick = onDismiss,
                theme = currentTheme,
            )
        },
    )
}

@Composable
private fun EditChildDialog(
    currentTheme: BaseAppTheme,
    child: User,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var childName by remember { mutableStateOf(child.name) }

    ThemeAwareAlertDialog(
        onDismissRequest = onDismiss,
        theme = currentTheme,
        title = {
            Text(
                text = "Edit Child",
                style = currentTheme.typography.titleLarge,
                color = currentTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column {
                Text(
                    text = "Update the child's information:",
                    style = currentTheme.typography.bodyMedium,
                    color = currentTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ThemeAwareOutlinedTextField(
                    value = childName,
                    onValueChange = { childName = it },
                    theme = currentTheme,
                    label = "Child's Name",
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            ThemeAwareButton(
                text = "Update",
                onClick = {
                    if (childName.isNotBlank()) {
                        onSave(childName.trim())
                    }
                },
                theme = currentTheme,
                enabled = childName.isNotBlank(),
            )
        },
        dismissButton = {
            ThemeAwareDialogTextButton(
                text = "Cancel",
                onClick = onDismiss,
                theme = currentTheme,
            )
        },
    )
}
