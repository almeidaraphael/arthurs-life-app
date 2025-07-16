package com.arthurslife.app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arthurslife.app.domain.auth.AuthResult
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.screens.CaregiverChildrenManagementScreen
import com.arthurslife.app.presentation.screens.CaregiverDashboardScreen
import com.arthurslife.app.presentation.screens.CaregiverProfileScreen
import com.arthurslife.app.presentation.screens.CaregiverTaskManagementScreen
import com.arthurslife.app.presentation.screens.ChildAchievementScreen
import com.arthurslife.app.presentation.screens.ChildHomeScreen
import com.arthurslife.app.presentation.screens.ChildProfileScreen
import com.arthurslife.app.presentation.screens.ChildTaskScreen
import com.arthurslife.app.presentation.screens.ThemeSettingsScreen
import com.arthurslife.app.presentation.screens.caregiverRewardManagementScreen
import com.arthurslife.app.presentation.screens.childRewardScreen
import com.arthurslife.app.presentation.screens.profileCustomizationScreen
import com.arthurslife.app.presentation.screens.userSelectionScreen
import com.arthurslife.app.presentation.theme.ThemeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun MainAppNavigation(
    userRole: UserRole,
    themeViewModel: ThemeViewModel,
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navigationItems = BottomNavItem.getItemsForRole(userRole)
    var selectedItem by remember { mutableStateOf(navigationItems[0].route) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            // Only show bottom bar if we're not on user selection screen
            val currentDestination by navController.currentBackStackEntryAsState()
            val isUserSwitching = currentDestination?.destination?.route in listOf(
                "user_selection",
                "user_switch_dialog/{userId}",
            )

            if (!isUserSwitching) {
                AppBottomNavigationBar(
                    navigationItems = navigationItems,
                    selectedItem = selectedItem,
                    onItemSelected = { route ->
                        selectedItem = route
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = navigationItems[0].route,
            userRole = userRole,
            themeViewModel = themeViewModel,
            authViewModel = authViewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onRequestUserSwitch = {
                // Navigate to user selection screen
                navController.navigate("user_selection")
            },
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    userRole: UserRole,
    themeViewModel: ThemeViewModel,
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    modifier: Modifier = Modifier,
    onRequestUserSwitch: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        setupChildScreens(themeViewModel, navController, onRequestUserSwitch)
        setupCaregiverScreens(themeViewModel, navController, onRequestUserSwitch)
        setupCommonScreens(userRole, themeViewModel, navController)
        setupUserSwitchingScreens(authViewModel, themeViewModel, navController)
    }
}

private fun NavGraphBuilder.setupUserSwitchingScreens(
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    themeViewModel: ThemeViewModel,
    navController: NavHostController,
) {
    composable("user_selection") {
        userSelectionScreenWithDialog(
            authViewModel = authViewModel,
            themeViewModel = themeViewModel,
            navController = navController,
            title = "Switch User",
        )
    }
}

private fun handleUserSelection(
    user: com.arthurslife.app.domain.user.User,
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    navController: NavHostController,
    onShowDialog: (User) -> Unit,
) {
    when (user.role) {
        UserRole.CHILD -> {
            // Direct authentication for child users
            authViewModel.switchToUser(user) { result ->
                when (result) {
                    is AuthResult.Success -> {
                        navController.popBackStack()
                        navController.navigate("child_home") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                    else -> {
                        // Handle error - could show a snackbar or dialog
                    }
                }
            }
        }
        UserRole.CAREGIVER -> {
            // Show dialog for PIN entry (managed as state, not navigation)
            onShowDialog(user)
        }
    }
}

@Composable
private fun userSelectionScreenWithDialog(
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    themeViewModel: ThemeViewModel,
    navController: NavHostController,
    title: String,
) {
    // State for managing the dialog
    val selectedUserForDialog = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf<User?>(
            null,
        )
    }

    // Show the user selection screen
    com.arthurslife.app.presentation.screens.userSelectionScreen(
        onUserSelected = { user ->
            handleUserSelection(
                user = user,
                authViewModel = authViewModel,
                navController = navController,
                onShowDialog = { selectedUser ->
                    println(
                        "MainAppNavigation: Setting selected user for dialog: ${selectedUser.id}",
                    )
                    selectedUserForDialog.value = selectedUser
                },
            )
        },
        title = title,
    )

    // Show dialog when a caregiver user is selected
    selectedUserForDialog.value?.let { targetUser ->
        println("MainAppNavigation: Showing dialog for user ${targetUser.id}")

        androidx.compose.runtime.DisposableEffect(targetUser.id) {
            println("MainAppNavigation: DisposableEffect STARTED for user ${targetUser.id}")
            onDispose {
                println("MainAppNavigation: DisposableEffect DISPOSED for user ${targetUser.id}")
            }
        }

        com.arthurslife.app.presentation.screens.userSwitchDialog(
            targetUser = targetUser,
            themeViewModel = themeViewModel,
            onSwitchSuccess = {
                println("MainAppNavigation: onSwitchSuccess called for ${targetUser.id}")
                selectedUserForDialog.value = null // Clear dialog
                navigateToUserHome(targetUser, navController)
            },
            onCancel = {
                println("MainAppNavigation: onCancel called for ${targetUser.id}")
                selectedUserForDialog.value = null // Clear dialog
            },
            viewModel = authViewModel,
        )
    }
}

private fun navigateToUserHome(
    targetUser: com.arthurslife.app.domain.user.User,
    navController: NavHostController,
) {
    val homeRoute = when (targetUser.role) {
        UserRole.CAREGIVER -> "caregiver_dashboard"
        UserRole.CHILD -> "child_home"
    }
    navController.popBackStack("user_selection", inclusive = true)
    navController.navigate(homeRoute) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = false
        }
        launchSingleTop = true
    }
}

// No ripple interaction source
private class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<androidx.compose.foundation.interaction.Interaction> = emptyFlow()
    override suspend fun emit(interaction: androidx.compose.foundation.interaction.Interaction) {
        // Intentionally empty - we don't want to emit interactions for ripple effect
    }
    override fun tryEmit(
        interaction: androidx.compose.foundation.interaction.Interaction,
    ): Boolean = true
}

@Composable
private fun AppBottomNavigationBar(
    navigationItems: List<BottomNavItem>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
) {
    NavigationBar(
        modifier = Modifier
            .height(80.dp)
            .background(MaterialTheme.colorScheme.primary),
        windowInsets = WindowInsets(0),
        containerColor = Color.Transparent,
    ) {
        navigationItems.forEach { item ->
            val isSelected = selectedItem == item.route
            val itemColors = getNavigationItemColors()

            NavigationBarItem(
                modifier = Modifier.padding(0.dp),
                selected = isSelected,
                label = if (isSelected) {
                    {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }
                } else {
                    null
                },
                alwaysShowLabel = false,
                interactionSource = remember { NoRippleInteractionSource() },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(if (isSelected) 28.dp else 24.dp),
                        tint = if (isSelected) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.6f)
                        },
                    )
                },
                colors = itemColors,
                onClick = { onItemSelected(item.route) },
            )
        }
    }
}

@Composable
private fun getNavigationItemColors() = NavigationBarItemDefaults.colors(
    indicatorColor = Color.Transparent,
    selectedIconColor = Color.White,
    selectedTextColor = Color.White,
    unselectedIconColor = Color.White.copy(alpha = 0.6f),
    unselectedTextColor = Color.White.copy(alpha = 0.6f),
)

private fun NavGraphBuilder.setupChildScreens(
    themeViewModel: ThemeViewModel,
    navController: NavHostController,
    onRequestUserSwitch: () -> Unit,
) {
    composable("child_home") {
        ChildHomeScreen(
            themeViewModel = themeViewModel,
        )
    }
    composable("child_tasks") {
        ChildTaskScreen(
            currentTheme = themeViewModel.currentTheme.collectAsState().value,
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composable("child_rewards") {
        childRewardScreen(currentTheme = themeViewModel.currentTheme.collectAsState().value)
    }
    composable("child_achievements") {
        ChildAchievementScreen()
    }
    composable("child_profile") {
        ChildProfileScreen(
            themeViewModel = themeViewModel,
            onSwitchMode = onRequestUserSwitch,
            onThemeSettingsClick = {
                navController.navigate("theme_settings")
            },
            onProfileCustomizationClick = {
                navController.navigate("profile_customization/current_user")
            },
        )
    }
}

private fun NavGraphBuilder.setupCaregiverScreens(
    themeViewModel: ThemeViewModel,
    navController: NavHostController,
    onRequestUserSwitch: () -> Unit,
) {
    composable("caregiver_dashboard") {
        CaregiverDashboardScreen(themeViewModel = themeViewModel)
    }
    composable("caregiver_tasks") {
        CaregiverTaskManagementScreen(
            currentTheme = themeViewModel.currentTheme.collectAsState().value,
        )
    }
    composable("caregiver_progress") {
        caregiverRewardManagementScreen(
            currentTheme = themeViewModel.currentTheme.collectAsState().value,
        )
    }
    composable("caregiver_children") {
        CaregiverChildrenManagementScreen(
            currentTheme = themeViewModel.currentTheme.collectAsState().value,
        )
    }
    composable("caregiver_profile") {
        CaregiverProfileScreen(
            themeViewModel = themeViewModel,
            onSwitchMode = onRequestUserSwitch,
            onThemeSettingsClick = {
                navController.navigate("theme_settings")
            },
            onProfileCustomizationClick = {
                navController.navigate("profile_customization/current_user")
            },
        )
    }
}

private fun NavGraphBuilder.setupCommonScreens(
    userRole: UserRole,
    themeViewModel: ThemeViewModel,
    navController: NavHostController,
) {
    composable("theme_settings") {
        ThemeSettingsScreen(
            userRole = userRole,
            themeViewModel = themeViewModel,
            onBackClick = {
                navController.popBackStack()
            },
        )
    }

    composable("profile_customization/{userId}") { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: ""
        profileCustomizationScreen(
            userId = userId,
            onNavigateBack = {
                navController.popBackStack()
            },
        )
    }
}
