package com.arthurslife.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.screens.PinEntryScreen
import com.arthurslife.app.presentation.screens.RoleSelectionScreen
import com.arthurslife.app.presentation.theme.ThemeViewModel

@Composable
fun AuthenticationNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    onAuthSuccess: (UserRole) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = "role_selection",
    ) {
        composable("role_selection") {
            RoleSelectionScreen(
                themeViewModel = themeViewModel,
                onRoleSelected = { role ->
                    navController.navigate("pin_entry/${role.name}")
                },
                onChildDirectAccess = {
                    // Direct access for child - no PIN required
                    onAuthSuccess(UserRole.CHILD)
                },
            )
        }

        composable("pin_entry/{role}") { backStackEntry ->
            val roleString = backStackEntry.arguments?.getString("role") ?: return@composable
            val targetRole = UserRole.valueOf(roleString)

            PinEntryScreen(
                targetRole = targetRole,
                themeViewModel = themeViewModel,
                onAuthSuccess = { onAuthSuccess(targetRole) },
                onCancel = { navController.popBackStack() },
            )
        }
    }
}
