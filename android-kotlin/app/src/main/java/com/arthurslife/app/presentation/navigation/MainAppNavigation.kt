package com.arthurslife.app.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.screens.CaregiverDashboardScreen
import com.arthurslife.app.presentation.screens.ChildHomeScreen
import com.arthurslife.app.presentation.screens.PlaceholderScreen
import com.arthurslife.app.presentation.screens.caregiverProfileScreen
import com.arthurslife.app.presentation.screens.childProfileScreen
import com.arthurslife.app.presentation.screens.roleSwitchingDialog

@Composable
fun MainAppNavigation(
    userRole: UserRole,
    modifier: Modifier = Modifier,
    onRoleSwitch: (UserRole) -> Unit = {},
) {
    val navController = rememberNavController()
    val navigationItems = getNavigationItems(userRole)
    var selectedItem by remember { mutableStateOf(navigationItems[0].route) }
    var showRoleSwitchingDialog by remember { mutableStateOf(false) }
    var targetRole by remember { mutableStateOf<UserRole?>(null) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
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
        },
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = navigationItems[0].route,
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onRequestRoleSwitch = { role ->
                targetRole = role
                showRoleSwitchingDialog = true
            },
        )

        // Role switching dialog
        if (showRoleSwitchingDialog && targetRole != null) {
            roleSwitchingDialog(
                targetRole = targetRole!!,
                onSwitchSuccess = {
                    showRoleSwitchingDialog = false
                    onRoleSwitch(targetRole!!)
                    targetRole = null
                },
                onCancel = {
                    showRoleSwitchingDialog = false
                    targetRole = null
                },
            )
        }
    }
}

@Composable
private fun AppBottomNavigationBar(
    navigationItems: List<NavigationItem>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
) {
    NavigationBar {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedItem == item.route,
                onClick = { onItemSelected(item.route) },
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    onRequestRoleSwitch: (UserRole) -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // Child mode screens
        composable("child_home") {
            ChildHomeScreen()
        }
        composable("child_tasks") {
            PlaceholderScreen("Tasks", "View and complete your daily tasks")
        }
        composable("child_rewards") {
            PlaceholderScreen("Rewards", "Spend your tokens on rewards")
        }
        composable("child_achievements") {
            PlaceholderScreen("Achievements", "View your badges and achievements")
        }
        composable("child_profile") {
            childProfileScreen(
                onSwitchMode = { onRequestRoleSwitch(UserRole.CAREGIVER) },
            )
        }

        // Caregiver mode screens
        composable("caregiver_dashboard") {
            CaregiverDashboardScreen()
        }
        composable("caregiver_tasks") {
            PlaceholderScreen("Tasks", "Manage tasks for your children")
        }
        composable("caregiver_progress") {
            PlaceholderScreen("Progress", "View progress and analytics")
        }
        composable("caregiver_children") {
            PlaceholderScreen("Children", "Manage your children and their settings")
        }
        composable("caregiver_profile") {
            caregiverProfileScreen(
                onSwitchMode = { onRequestRoleSwitch(UserRole.CHILD) },
            )
        }
    }
}

private fun getNavigationItems(userRole: UserRole): List<NavigationItem> =
    when (userRole) {
        UserRole.CHILD ->
            listOf(
                NavigationItem("child_home", Icons.Default.Home, "Home"),
                NavigationItem("child_tasks", Icons.Default.Task, "Tasks"),
                NavigationItem("child_rewards", Icons.Default.ShoppingCart, "Rewards"),
                NavigationItem("child_achievements", Icons.Default.EmojiEvents, "Achievements"),
                NavigationItem("child_profile", Icons.Default.Person, "Profile"),
            )
        UserRole.CAREGIVER ->
            listOf(
                NavigationItem("caregiver_dashboard", Icons.Default.Dashboard, "Dashboard"),
                NavigationItem("caregiver_tasks", Icons.Default.Task, "Tasks"),
                NavigationItem("caregiver_progress", Icons.AutoMirrored.Filled.TrendingUp, "Progress"),
                NavigationItem("caregiver_children", Icons.Default.Group, "Children"),
                NavigationItem("caregiver_profile", Icons.Default.Person, "Profile"),
            )
    }

data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
)
