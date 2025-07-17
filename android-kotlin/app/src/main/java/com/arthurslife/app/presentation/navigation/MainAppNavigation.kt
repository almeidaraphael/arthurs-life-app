package com.arthurslife.app.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.arthurslife.app.presentation.theme.components.dialogManager
import com.arthurslife.app.presentation.theme.components.themeAwareBottomNavigationBar
import com.arthurslife.app.presentation.theme.components.topNavigationBar
import com.arthurslife.app.presentation.viewmodels.DialogManagementViewModel
import com.arthurslife.app.presentation.viewmodels.TopBarScreen

@Composable
fun MainAppNavigation(
    userRole: UserRole,
    themeViewModel: ThemeViewModel,
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navigationItems = BottomNavItem.getItemsForRole(userRole)

    MainScreen(
        navController = navController,
        navigationItems = navigationItems,
        userRole = userRole,
        themeViewModel = themeViewModel,
        authViewModel = authViewModel,
        modifier = modifier,
    )
}

/**
 * Data class for top bar callbacks to reduce parameter count.
 */
data class TopBarCallbacks(
    val onAvatarClick: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onSelectedChildClick: () -> Unit,
)

@Composable
fun MainScreen(
    navController: NavHostController,
    navigationItems: List<BottomNavItem>,
    userRole: UserRole,
    themeViewModel: ThemeViewModel,
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    modifier: Modifier = Modifier,
    dialogViewModel: DialogManagementViewModel = hiltViewModel(),
) {
    var selectedItem by remember { mutableStateOf(navigationItems[0].route) }
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val currentDestination by navController.currentBackStackEntryAsState()

    val isUserSwitching = currentDestination?.destination?.route in listOf(
        "user_selection",
        "user_switch_dialog/{userId}",
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            if (!isUserSwitching) {
                topNavigationBar(
                    theme = currentTheme,
                    currentScreen = getCurrentTopBarScreen(currentDestination?.destination?.route),
                    onAvatarClick = {
                        authViewModel.authState.value.currentUser?.let { user ->
                            dialogViewModel.showUserProfileDialog(user)
                        }
                    },
                    onSettingsClick = {
                        dialogViewModel.showSettingsDialog()
                    },
                )
            }
        },
        bottomBar = {
            if (!isUserSwitching) {
                mainScreenBottomBar(
                    navigationItems = navigationItems,
                    selectedItem = selectedItem,
                    currentTheme = currentTheme,
                    navController = navController,
                    onItemSelected = { selectedItem = it },
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
                navController.navigate("user_selection")
            },
        )
    }

    mainScreenDialogs(
        currentTheme = currentTheme,
        dialogViewModel = dialogViewModel,
        authViewModel = authViewModel,
        navController = navController,
    )
}

/**
 * Maps navigation routes to TopBarScreen enum values.
 */
fun getCurrentTopBarScreen(route: String?): TopBarScreen {
    return when (route) {
        "child_home", "caregiver_dashboard" -> TopBarScreen.HOME
        "child_tasks", "caregiver_tasks" -> TopBarScreen.TASKS
        "child_rewards", "caregiver_progress" -> TopBarScreen.REWARDS
        "child_achievements" -> TopBarScreen.ACHIEVEMENTS
        "caregiver_children" -> TopBarScreen.CHILDREN_MANAGEMENT
        "child_profile", "caregiver_profile" -> TopBarScreen.OTHER
        "theme_settings" -> TopBarScreen.OTHER
        else -> TopBarScreen.HOME
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

@Composable
private fun mainScreenBottomBar(
    navigationItems: List<BottomNavItem>,
    selectedItem: String,
    currentTheme: com.arthurslife.app.presentation.theme.BaseAppTheme,
    navController: NavHostController,
    onItemSelected: (String) -> Unit,
) {
    themeAwareBottomNavigationBar(
        navigationItems = navigationItems,
        selectedItem = selectedItem,
        theme = currentTheme,
        onItemSelected = { route ->
            onItemSelected(route)
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

@Composable
private fun mainScreenDialogs(
    currentTheme: com.arthurslife.app.presentation.theme.BaseAppTheme,
    dialogViewModel: DialogManagementViewModel,
    authViewModel: com.arthurslife.app.presentation.viewmodels.AuthViewModel,
    navController: NavHostController,
) {
    dialogManager(
        theme = currentTheme,
        onUserSelected = { user ->
            handleUserSelection(
                user = user,
                authViewModel = authViewModel,
                navController = navController,
                onShowDialog = { selectedUser ->
                    dialogViewModel.showUserProfileDialog(selectedUser)
                },
            )
        },
        onLanguageSelected = { _ -> },
        onSaveProfile = { dialogViewModel.hideDialog() },
        onChangeAvatar = { },
        onChangePIN = { },
        onSwitchUsers = { navController.navigate("user_selection") },
        onThemeClick = { navController.navigate("theme_settings") },
        onThemeSelected = { selectedTheme ->
            // TODO: Implement theme switching through ThemeViewModel
        },
    )
}
