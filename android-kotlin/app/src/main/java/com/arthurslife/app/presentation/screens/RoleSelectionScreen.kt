package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.theme.components.ThemeAwareRoleSelectionButtons

@Composable
fun RoleSelectionScreen(
    themeViewModel: ThemeViewModel,
    onRoleSelected: (UserRole) -> Unit,
    onChildDirectAccess: () -> Unit,
) {
    val theme by themeViewModel.currentTheme.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ThemeAwareRoleSelectionButtons(
            theme = theme,
            onChildAccess = onChildDirectAccess,
            onCaregiverAccess = { onRoleSelected(UserRole.CAREGIVER) },
        )
    }
}
