package com.arthurslife.app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.screens.CaregiverDashboardScreen
import com.arthurslife.app.presentation.screens.CaregiverProfileScreen
import com.arthurslife.app.presentation.screens.ChildHomeScreen
import com.arthurslife.app.presentation.screens.ChildProfileScreen
import com.arthurslife.app.presentation.screens.PlaceholderScreen
import com.arthurslife.app.presentation.screens.RoleSwitchingDialog
import com.arthurslife.app.presentation.screens.ThemeSettingsScreen
import com.arthurslife.app.presentation.theme.ThemeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// No ripple interaction source
class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<androidx.compose.foundation.interaction.Interaction> = emptyFlow()
    override suspend fun emit(interaction: androidx.compose.foundation.interaction.Interaction) {
        // Intentionally empty - we don't want to emit interactions for ripple effect
    }
    override fun tryEmit(interaction: androidx.compose.foundation.interaction.Interaction): Boolean = true
}

@Composable
fun MainAppNavigation(
    userRole: UserRole,
    themeViewModel: ThemeViewModel,
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
            userRole = userRole,
            themeViewModel = themeViewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onRequestRoleSwitch = { role ->
                targetRole = role
                showRoleSwitchingDialog = true
            },
        )

        // Role switching dialog
        if (showRoleSwitchingDialog && targetRole != null) {
            RoleSwitchingDialog(
                targetRole = targetRole!!,
                themeViewModel = themeViewModel,
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

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    userRole: UserRole,
    themeViewModel: ThemeViewModel,
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
            ChildHomeScreen(themeViewModel = themeViewModel)
        }
        composable("child_tasks") {
            PlaceholderScreen("Tasks", "View and complete your daily tasks", themeViewModel = themeViewModel)
        }
        composable("child_rewards") {
            PlaceholderScreen("Rewards", "Spend your tokens on rewards", themeViewModel = themeViewModel)
        }
        composable("child_achievements") {
            PlaceholderScreen("Achievements", "View your badges and achievements", themeViewModel = themeViewModel)
        }
        composable("child_profile") {
            ChildProfileScreen(
                themeViewModel = themeViewModel,
                onSwitchMode = { onRequestRoleSwitch(UserRole.CAREGIVER) },
                onThemeSettingsClick = {
                    navController.navigate("theme_settings")
                },
            )
        }

        // Caregiver mode screens
        composable("caregiver_dashboard") {
            CaregiverDashboardScreen(themeViewModel = themeViewModel)
        }
        composable("caregiver_tasks") {
            PlaceholderScreen("Tasks", "Manage tasks for your children", themeViewModel = themeViewModel)
        }
        composable("caregiver_progress") {
            PlaceholderScreen("Progress", "View progress and analytics", themeViewModel = themeViewModel)
        }
        composable("caregiver_children") {
            PlaceholderScreen("Children", "Manage your children and their settings", themeViewModel = themeViewModel)
        }
        composable("caregiver_profile") {
            CaregiverProfileScreen(
                themeViewModel = themeViewModel,
                onSwitchMode = { onRequestRoleSwitch(UserRole.CHILD) },
                onThemeSettingsClick = {
                    navController.navigate("theme_settings")
                },
            )
        }

        // Theme settings screen
        composable("theme_settings") {
            ThemeSettingsScreen(
                userRole = userRole,
                themeViewModel = themeViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
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
                NavigationItem("child_achievements", Icons.Default.EmojiEvents, "Awards"),
                NavigationItem("child_profile", Icons.Default.Person, "Profile"),
            )
        UserRole.CAREGIVER ->
            listOf(
                NavigationItem("caregiver_dashboard", Icons.Default.Dashboard, "Home"),
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
