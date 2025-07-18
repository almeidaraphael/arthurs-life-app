package com.lemonqwest.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Task
import androidx.compose.ui.graphics.vector.ImageVector
import com.lemonqwest.app.domain.user.UserRole

/**
 * Sealed class representing bottom navigation items with type-safe route definitions.
 * Each navigation item contains route, label resource, and icon resource information.
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    // Child navigation items
    data object ChildHome : BottomNavItem(
        route = "child_home",
        label = "Home",
        icon = Icons.Default.Home,
    )

    data object ChildTasks : BottomNavItem(
        route = "child_tasks",
        label = "Quests",
        icon = Icons.Default.Task,
    )

    data object ChildRewards : BottomNavItem(
        route = "child_rewards",
        label = "Rewards",
        icon = Icons.Default.ShoppingCart,
    )

    data object ChildAchievements : BottomNavItem(
        route = "child_achievements",
        label = "Awards",
        icon = Icons.Default.EmojiEvents,
    )

    // Caregiver navigation items
    data object CaregiverDashboard : BottomNavItem(
        route = "caregiver_dashboard",
        label = "Dashboard",
        icon = Icons.Default.Dashboard,
    )

    data object CaregiverTasks : BottomNavItem(
        route = "caregiver_tasks",
        label = "Tasks",
        icon = Icons.Default.Task,
    )

    data object CaregiverProgress : BottomNavItem(
        route = "caregiver_progress",
        label = "Rewards",
        icon = Icons.Default.ShoppingCart,
    )

    data object CaregiverChildren : BottomNavItem(
        route = "caregiver_children",
        label = "Children",
        icon = Icons.Default.Group,
    )

    data object CaregiverUsers : BottomNavItem(
        route = "caregiver_users",
        label = "Users",
        icon = Icons.Default.Group,
    )

    companion object {
        /**
         * Get navigation items based on user role with role-specific terminology
         */
        fun getItemsForRole(
            userRole: UserRole,
            isAdmin: Boolean = false,
        ): List<BottomNavItem> = when (userRole) {
            UserRole.CHILD -> listOf(
                ChildHome,
                ChildTasks,
                ChildRewards,
                ChildAchievements,
            )
            UserRole.CAREGIVER -> listOf(
                CaregiverDashboard,
                CaregiverTasks,
                CaregiverProgress,
                if (isAdmin) CaregiverUsers else CaregiverChildren,
            )
        }

        /**
         * Get all available navigation routes
         */
        fun getAllRoutes(): List<String> = listOf(
            ChildHome.route,
            ChildTasks.route,
            ChildRewards.route,
            ChildAchievements.route,
            CaregiverDashboard.route,
            CaregiverTasks.route,
            CaregiverProgress.route,
            CaregiverChildren.route,
            CaregiverUsers.route,
        )
    }
}
