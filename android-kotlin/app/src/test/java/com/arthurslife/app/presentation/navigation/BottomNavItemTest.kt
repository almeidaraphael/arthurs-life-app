package com.arthurslife.app.presentation.navigation

import com.arthurslife.app.domain.user.UserRole
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BottomNavItemTest {

    @Test
    fun `given child role when getItemsForRole called then should return child navigation items`() {
        // Given
        val role = UserRole.CHILD

        // When
        val items = BottomNavItem.getItemsForRole(role)

        // Then
        assertEquals(EXPECTED_CHILD_ITEMS.size, items.size)
        assertEquals(EXPECTED_CHILD_ITEMS, items)
    }

    @Test
    fun `given caregiver role when getItemsForRole called then should return caregiver navigation items`() {
        // Given
        val role = UserRole.CAREGIVER

        // When
        val items = BottomNavItem.getItemsForRole(role)

        // Then
        assertEquals(EXPECTED_CAREGIVER_ITEMS.size, items.size)
        assertEquals(EXPECTED_CAREGIVER_ITEMS, items)
    }

    @Test
    fun `when getAllRoutes called then should return all navigation routes`() {
        // When
        val routes = BottomNavItem.getAllRoutes()

        // Then
        val expectedRoutes = setOf(
            "child_home", "child_tasks", "child_rewards", "child_achievements", "child_profile",
            "caregiver_dashboard", "caregiver_tasks", "caregiver_progress", "caregiver_children", "caregiver_profile",
        )
        assertEquals(expectedRoutes.size, routes.size)
        expectedRoutes.forEach { expectedRoute ->
            assertContains(routes, expectedRoute)
        }
    }

    @Test
    fun `given child navigation items when checking properties then should have correct routes and labels`() {
        // Given & When
        val childHome = BottomNavItem.ChildHome
        val childTasks = BottomNavItem.ChildTasks
        val childRewards = BottomNavItem.ChildRewards
        val childAchievements = BottomNavItem.ChildAchievements
        val childProfile = BottomNavItem.ChildProfile

        // Then
        assertEquals("child_home", childHome.route)
        assertEquals("Home", childHome.label)

        assertEquals("child_tasks", childTasks.route)
        assertEquals("Quests", childTasks.label)

        assertEquals("child_rewards", childRewards.route)
        assertEquals("Rewards", childRewards.label)

        assertEquals("child_achievements", childAchievements.route)
        assertEquals("Awards", childAchievements.label)

        assertEquals("child_profile", childProfile.route)
        assertEquals("Profile", childProfile.label)
    }

    @Test
    fun `given caregiver navigation items when checking properties then should have correct routes and labels`() {
        // Given & When
        val caregiverDashboard = BottomNavItem.CaregiverDashboard
        val caregiverTasks = BottomNavItem.CaregiverTasks
        val caregiverProgress = BottomNavItem.CaregiverProgress
        val caregiverChildren = BottomNavItem.CaregiverChildren
        val caregiverProfile = BottomNavItem.CaregiverProfile

        // Then
        assertEquals("caregiver_dashboard", caregiverDashboard.route)
        assertEquals("Home", caregiverDashboard.label)

        assertEquals("caregiver_tasks", caregiverTasks.route)
        assertEquals("Tasks", caregiverTasks.label)

        assertEquals("caregiver_progress", caregiverProgress.route)
        assertEquals("Rewards", caregiverProgress.label)

        assertEquals("caregiver_children", caregiverChildren.route)
        assertEquals("Children", caregiverChildren.label)

        assertEquals("caregiver_profile", caregiverProfile.route)
        assertEquals("Profile", caregiverProfile.label)
    }

    @Test
    fun `given child navigation items when checking icons then should have appropriate icons`() {
        // Given & When
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)

        // Then
        childItems.forEach { item ->
            assertTrue(item.icon != null, "Navigation item ${item.label} should have an icon")
        }
    }

    @Test
    fun `given caregiver navigation items when checking icons then should have appropriate icons`() {
        // Given & When
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER)

        // Then
        caregiverItems.forEach { item ->
            assertTrue(item.icon != null, "Navigation item ${item.label} should have an icon")
        }
    }

    @Test
    fun `given all navigation items when checking uniqueness then routes should be unique`() {
        // Given
        val allRoutes = BottomNavItem.getAllRoutes()

        // When
        val uniqueRoutes = allRoutes.toSet()

        // Then
        assertEquals(allRoutes.size, uniqueRoutes.size, "All navigation routes should be unique")
    }

    @Test
    fun `given child and caregiver items when comparing then should have different terminology`() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER)

        // When & Then
        val childTasksLabel = childItems.find { it.route.contains("tasks") }?.label
        val caregiverTasksLabel = caregiverItems.find { it.route.contains("tasks") }?.label
        assertEquals("Quests", childTasksLabel)
        assertEquals("Tasks", caregiverTasksLabel)

        val childRewardsLabel = childItems.find { it.route.contains("rewards") }?.label
        assertEquals("Rewards", childRewardsLabel)

        val childAchievementsLabel = childItems.find { it.route.contains("achievements") }?.label
        assertEquals("Awards", childAchievementsLabel)
    }

    @Test
    fun `given navigation items when checking sealed class structure then should be properly sealed`() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER)

        // When & Then
        val allItems = childItems + caregiverItems
        allItems.forEach { item ->
            assertTrue(
                item is BottomNavItem,
                "All items should be instances of BottomNavItem sealed class",
            )
        }
    }

    companion object {
        private val EXPECTED_CHILD_ITEMS = listOf(
            BottomNavItem.ChildHome,
            BottomNavItem.ChildTasks,
            BottomNavItem.ChildRewards,
            BottomNavItem.ChildAchievements,
            BottomNavItem.ChildProfile,
        )

        private val EXPECTED_CAREGIVER_ITEMS = listOf(
            BottomNavItem.CaregiverDashboard,
            BottomNavItem.CaregiverTasks,
            BottomNavItem.CaregiverProgress,
            BottomNavItem.CaregiverChildren,
            BottomNavItem.CaregiverProfile,
        )
    }
}
