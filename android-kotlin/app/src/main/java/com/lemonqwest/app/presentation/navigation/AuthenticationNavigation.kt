package com.lemonqwest.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.screens.PinEntryScreen
import com.lemonqwest.app.presentation.screens.UserSelectionScreen
import com.lemonqwest.app.presentation.theme.ThemeViewModel

@Composable
fun AuthenticationNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    authViewModel: com.lemonqwest.app.presentation.viewmodels.AuthViewModel,
    onChildSelected: (User) -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = "user_selection",
    ) {
        composable("user_selection") {
            UserSelectionScreen(
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
