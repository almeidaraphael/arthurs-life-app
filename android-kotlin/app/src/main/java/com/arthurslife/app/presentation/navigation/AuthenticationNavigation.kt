package com.arthurslife.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.screens.pinEntryScreen
import com.arthurslife.app.presentation.screens.roleSelectionScreen

@Composable
fun authenticationNavigation(
    navController: NavHostController,
    onAuthSuccess: (UserRole) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = "role_selection",
    ) {
        composable("role_selection") {
            roleSelectionScreen(
                onRoleSelected = { role ->
                    navController.navigate("pin_entry/${role.name}")
                },
            )
        }

        composable("pin_entry/{role}") { backStackEntry ->
            val roleString = backStackEntry.arguments?.getString("role") ?: return@composable
            val targetRole = UserRole.valueOf(roleString)

            pinEntryScreen(
                targetRole = targetRole,
                onAuthSuccess = { onAuthSuccess(targetRole) },
                onCancel = { navController.popBackStack() },
            )
        }
    }
}
