package com.arthurslife.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.screens.PinEntryScreen
import com.arthurslife.app.presentation.screens.userSelectionScreen
import com.arthurslife.app.presentation.theme.ThemeViewModel

@Composable
fun AuthenticationNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    onChildSelected: (User) -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = "user_selection",
    ) {
        composable("user_selection") {
            userSelectionScreen(
                onUserSelected = { user ->
                    when (user.role) {
                        UserRole.CHILD -> {
                            // Direct access for child - no PIN required
                            // Pass the specific child user to the callback
                            onChildSelected(user)
                        }
                        UserRole.CAREGIVER -> {
                            // Navigate to PIN entry for caregiver
                            navController.navigate("pin_entry/${user.role.name}")
                        }
                    }
                },
            )
        }

        composable("pin_entry/{role}") { backStackEntry ->
            val roleString = backStackEntry.arguments?.getString("role") ?: return@composable
            val targetRole = UserRole.valueOf(roleString)

            PinEntryScreen(
                targetRole = targetRole,
                themeViewModel = themeViewModel,
                onCancel = { navController.popBackStack() },
                authViewModel = authViewModel,
            )
        }
    }
}
