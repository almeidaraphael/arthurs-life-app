---
goal: Implement a persistent, theme-aware, and role-based top navigation bar.
version: 1.0
date_created: 2025-07-15
last_updated: 2025-07-15
owner: Engineering
tags: [feature, navigation, ui, compose, dialog]
---

# Introduction

This document provides the implementation plan for the top navigation bar in Arthur's Life App, based on the `feature-top-navigation-bar.md` PRD. The plan details the creation of a persistent, transparent, and context-aware top bar that provides access to profiles, settings, and role-specific information. It will integrate with the existing theme, navigation, and dialog systems.

## 1. Requirements & Constraints

- **REQ-001**: The top bar must be persistent and visible on all screens with a transparent background.
- **REQ-002**: Its content must adapt based on the user's role (Child, Caregiver) and the current screen.
- **REQ-003**: It must provide trigger points for various dialogs: Profile, Settings, User Selector, Child Selector, Theme Selector, and Language Selector.
- **REQ-004**: All elements must be theme-aware, supporting `MarioClassic` and `Material` themes.
- **REQ-005**: All components must be fully accessible, meeting WCAG 2.1 AA standards (4.5:1 contrast, TalkBack support, 48x48dp touch targets).
- **CON-001**: The implementation must use Jetpack Compose.
- **CON-002**: All code must be free of Detekt and KtLint violations.
- **GUD-001**: The implementation will adhere to the established Clean Architecture, using ViewModels for state management and Hilt for dependency injection.
- **PAT-001**: Dialogs will be managed centrally to ensure a consistent user experience.

## 2. Implementation Steps

| Task ID | Description | Files to Edit/Create | Details |
|---|---|---|---|
| **TASK-001** | Define Top Bar State | `TopBarState.kt`, `TopBarViewModel.kt` | Create a data class `TopBarState` to model all possible elements of the top bar (avatar, token balance, progress, etc.). Create a `TopBarViewModel` to build and expose a `StateFlow<TopBarState>` based on the current user role and navigation route. |
| **TASK-002** | Implement the Top Navigation Bar Composable | `TopAppBar.kt` | Create a composable that renders the UI based on the `TopBarState`. It will handle the layout of elements and delegate click events to the ViewModel or a navigation handler. |
| **TASK-003** | Implement Required Dialogs | `ProfileDialog.kt`, `SettingsDialog.kt`, `UserSelectorDialog.kt`, etc. | Create or refactor the required dialog composables. Ensure they are self-contained and can be triggered from the top bar. |
| **TASK-004** | Create a Dialog Management System | `DialogManager.kt`, `MainViewModel.kt` | Implement a centralized system to show/hide dialogs. The `TopBarViewModel` will request dialogs, and a higher-level `MainViewModel` or manager will control which dialog is currently visible. |
| **TASK-005** | Integrate Top Bar into Main App UI | `MainScreen.kt`, `AppScaffold.kt` | Add the `TopAppBar` to the main app scaffold, ensuring it's placed correctly and receives the `TopBarState`. |
| **TASK-006** | Implement Unit and UI Tests | `TopBarViewModelTest.kt`, `TopAppBarTest.kt` | Write JUnit tests for the `TopBarViewModel` to verify state logic for different roles and screens. Write Compose UI tests for the `TopAppBar` and its interaction with dialogs. |

## 3. Alternatives

- **ALT-001**: Using the stock `TopAppBar` from Material Design. Rejected because a custom layout is required to accommodate the dynamic, role-based elements.
- **ALT-002**: Handling dialog visibility with simple boolean states in each screen. Rejected in favor of a centralized `DialogManager` to prevent state conflicts and simplify the logic in individual screens.

## 4. Dependencies

- **DEP-001**: `androidx.navigation:navigation-compose`
- **DEP-002**: `androidx.lifecycle:lifecycle-viewmodel-compose`
- **DEP-003**: Existing `Theme` system.
- **DEP-004**: Existing `UserSession` manager.
- **DEP-005**: Existing `NavController` instance.

## 5. Files

- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/viewmodels/TopBarViewModel.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/components/TopAppBar.kt`
- **CREATE/MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/components/dialogs/*.kt` (for all required dialogs)
- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/navigation/DialogManager.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/screens/MainScreen.kt`
- **CREATE**: `android-kotlin/app/src/test/java/com/arthurslife/app/presentation/viewmodels/TopBarViewModelTest.kt`
- **CREATE**: `android-kotlin/app/src/androidTest/java/com/arthurslife/app/presentation/components/TopAppBarTest.kt`

## 6. Testing

- **TEST-001**: Verify the correct UI elements are displayed on the top bar for a `Child` on the `Home` screen.
- **TEST-002**: Verify the correct UI elements are displayed for a `Caregiver` on the `Tasks` screen.
- **TEST-003**: Verify that tapping the avatar opens the `ProfileDialog`.
- **TEST-004**: Verify that tapping the settings icon opens the `SettingsDialog`.
- **TEST-005**: From the `SettingsDialog`, verify that the `ThemeSelector` and `LanguageSelector` dialogs can be opened.
- **TEST-006**: Verify that the top bar content updates correctly when the user navigates between screens.
- **TEST-007**: Verify all top bar elements and dialogs are accessible.

## 7. Risks & Assumptions

- **RISK-001**: The complexity of state management for the top bar across different screens and roles could lead to bugs if not carefully designed. A centralized ViewModel is intended to mitigate this.
- **RISK-002**: Managing multiple dialogs and ensuring only one is shown at a time can be complex. The `DialogManager` is designed to address this.
- **ASSUMPTION-001**: The current navigation setup provides a way to get the current route as a `State` or `Flow` to the `TopBarViewModel`.
- **ASSUMPTION-002**: Data required for the top bar (e.g., token balance, task progress) is available via repositories that can be injected into the `TopBarViewModel`.

## 8. Related Specifications / Further Reading

- `docs/product-requirements-documents/feature-top-navigation-bar.md`
- `docs/theme-system.md`
- `docs/architecture.md`
- [Jetpack Compose Dialogs](https://developer.android.com/jetpack/compose/dialogs)
