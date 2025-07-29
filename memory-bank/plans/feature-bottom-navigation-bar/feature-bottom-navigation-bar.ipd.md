---
goal: Implement a responsive, theme-aware, and role-based bottom navigation bar.
version: 1.0
date_created: 2025-07-15
last_updated: 2025-07-15
owner: Engineering
tags: [feature, navigation, ui, compose]
---

# Introduction

This document outlines the implementation plan for creating a unified bottom navigation bar in the Arthur's Life App. The plan is derived from the `feature-bottom-navigation-bar.md` PRD and considers the existing application architecture. The goal is to provide a persistent, role-based navigation component that integrates seamlessly with the existing theme system and navigation controller.

## 1. Requirements & Constraints

- **REQ-001**: The navigation bar must display tabs based on the user's role (Child, Caregiver Admin, Caregiver Non-Admin).
- **REQ-002**: It must not display more than four tabs.
- **REQ-003**: The layout must be responsive to different screen sizes and orientations.
- **REQ-004**: The component must be theme-aware, adapting to both `MarioClassic` and `Material` themes.
- **REQ-005**: It must use consistent, semantically mapped icons and labels for each role and theme.
- **REQ-006**: The navigation bar must be persistent across all main screens of the application.
- **REQ-007**: All components must meet accessibility standards, including TalkBack support, 4.5:1 color contrast, and 48x48dp touch targets.
- **CON-001**: The implementation must use Jetpack Compose.
- **CON-002**: All code must pass Detekt and KtLint checks with zero violations.
- **CON-003**: Unit and UI tests must achieve at least 80% coverage for the navigation logic.
- **GUD-001**: The implementation will follow the existing Clean Architecture and Dependency Injection (Hilt) patterns.
- **PAT-001**: Navigation logic will be centralized and managed via a unified navigation controller.

## 2. Implementation Steps

The implementation will be broken down into the following tasks:

| Task ID | Description | Files to Edit/Create | Details |
|---|---|---|---|
| **TASK-001** | Define Navigation Routes and Items | `BottomNavItem.kt`, `AppNavigation.kt` | Create a sealed class `BottomNavItem` to define properties for each navigation item (route, label resource, icon resource). Update `AppNavigation` to include routes for all screens accessible from the bottom bar. |
| **TASK-002** | Create Role-Based Navigation Logic | `BottomNavViewModel.kt` | Create a `ViewModel` to determine which `BottomNavItem`s to display based on the current user's role and admin status. This ViewModel will expose a state Flow of the current list of navigation items. |
| **TASK-003** | Implement the Bottom Navigation Bar Composable | `BottomNavigationBar.kt` | Create a theme-aware composable that takes the list of `BottomNavItem`s and the `NavController`. It will use `BottomNavigation` and `BottomNavigationItem` from Compose Material to build the UI. |
| **TASK-004** | Integrate Bottom Bar into Main App UI | `MainScreen.kt`, `AppScaffold.kt` | Integrate the `BottomNavigationBar` into the main app scaffold. The scaffold will manage the visibility of the bottom bar based on the current route. |
| **TASK-005** | Update Main Navigation Host | `MainAppNavigation.kt` | Modify the main `NavHost` to include the `MainScreen` which contains the scaffold and bottom navigation. Ensure the `NavController` is correctly passed down. |
| **TASK-006** | Implement Unit and UI Tests | `BottomNavViewModelTest.kt`, `BottomNavigationBarTest.kt`, `BottomNavItemTest.kt`, `ThemeAwareBottomNavigationBarTest.kt` | Write JUnit tests for the `BottomNavViewModel` to verify role-based logic. Write unit tests for `BottomNavItem` sealed class. Write Compose UI tests for the `ThemeAwareBottomNavigationBar` to verify theme behavior, navigation clicks, and accessibility. Note: Integration tests already exist in `MainAppNavigationTest.kt`. |

## 3. Alternatives

- **ALT-001**: Using a third-party navigation library. This was rejected to maintain a lean dependency tree and leverage the full power of Jetpack Compose Navigation.
- **ALT-002**: Placing navigation logic directly in composables. This was rejected to adhere to Clean Architecture principles, separating UI from business logic.

## 4. Dependencies

- **DEP-001**: `androidx.navigation:navigation-compose` - For Jetpack Compose Navigation.
- **DEP-002**: `androidx.hilt:hilt-navigation-compose` - For Hilt integration with Navigation.
- **DEP-003**: `androidx.lifecycle:lifecycle-viewmodel-compose` - For ViewModel integration.
- **DEP-004**: Existing `Theme` system (`MarioClassicTheme`, `MaterialTheme`).
- **DEP-005**: Existing `UserSession` manager to get the current user's role.

## 5. Files

- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/navigation/BottomNavItem.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/viewmodels/BottomNavViewModel.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/components/BottomNavigationBar.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/navigation/MainAppNavigation.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/screens/MainScreen.kt` (or equivalent top-level screen composable)
- **CREATE**: `android-kotlin/app/src/test/java/com/arthurslife/app/presentation/viewmodels/BottomNavViewModelTest.kt`
- **CREATE**: `android-kotlin/app/src/test/java/com/arthurslife/app/presentation/navigation/BottomNavItemTest.kt`
- **CREATE**: `android-kotlin/app/src/androidTest/java/com/arthurslife/app/presentation/theme/components/ThemeAwareBottomNavigationBarTest.kt`
- **EXISTS**: `android-kotlin/app/src/androidTest/java/com/arthurslife/app/ui/navigation/MainAppNavigationTest.kt` (integration tests)

## 6. Testing

- **TEST-001**: Verify that the correct tabs are displayed for a `Child` user.
- **TEST-002**: Verify that the correct tabs are displayed for a `Caregiver` with `FamilyAdmin` role.
- **TEST-003**: Verify that the correct tabs are displayed for a `Caregiver` without `FamilyAdmin` role.
- **TEST-004**: Verify that tapping a tab navigates to the correct screen.
- **TEST-005**: Verify that the navigation bar's appearance updates correctly when the theme is changed.
- **TEST-006**: Verify that all navigation items meet accessibility standards (labels, touch targets).

## 7. Risks & Assumptions

- **RISK-001**: Potential for navigation state to be lost on configuration changes if not handled correctly by the `NavController`.
- **ASSUMPTION-001**: An observable source for the current user's role is available to be injected into the `BottomNavViewModel`.
- **ASSUMPTION-002**: All screens to be navigated to from the bottom bar are already defined as composables.

## 8. Related Specifications / Further Reading

- `docs/product-requirements-documents/feature-bottom-navigation-bar.md`
- `docs/theme-system.md`
- `docs/architecture.md`
- [Jetpack Compose Navigation Documentation](https://developer.android.com/jetpack/compose/navigation)

