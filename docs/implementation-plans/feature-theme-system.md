---
goal: Implement a user-based, persistent, and extensible theme system.
version: 1.0
date_created: 2025-07-15
last_updated: 2025-07-15
owner: Engineering
tags: [feature, ui, design, compose, theme]
---

# Introduction

This document outlines the implementation plan for the Theme System in Arthur's Life App, as specified in the `feature-theme-system.md` PRD. The plan leverages the existing theme architecture (`BaseAppTheme`, `LocalBaseTheme`) and extends it to support user-specific, persistent theme preferences that can be changed dynamically.

## 1. Requirements & Constraints

- **REQ-001**: Each user must be able to select a theme (Mario Classic, Material Light, Material Dark).
- **REQ-002**: Theme preferences must be stored locally and persist across app sessions for each user.
- **REQ-003**: The entire app's UI, including icons and terminology, must update instantly when the theme is changed.
- **REQ-004**: The system must be extensible to allow for new themes in the future.
- **REQ-005**: All themes must meet accessibility standards (4.5:1 contrast, TalkBack support).
- **CON-001**: The implementation must use Jetpack Compose and Hilt for dependency injection.
- **CON-002**: Theme preferences will be stored using Jetpack DataStore.
- **PAT-001**: The existing `BaseAppTheme` interface and `LocalBaseTheme` CompositionLocal will be used to provide theme properties to composables.

## 2. Implementation Steps

| Task ID | Description | Files to Edit/Create | Details |
|---|---|---|---|
| **TASK-001** | Define Theme Data Layer | `ThemeRepository.kt`, `ThemeDataStore.kt`, `AppTheme.kt` | Create an `AppTheme` enum. Implement `ThemeDataStore` to save/retrieve a user's selected `AppTheme` using Jetpack DataStore. Create `ThemeRepository` as an abstraction over the DataStore. |
| **TASK-002** | Provide Theme Dependencies | `DataModule.kt` | Add Hilt providers in a `DataModule` for `ThemeRepository` and `ThemeDataStore`. |
| **TASK-003** | Create Theme State Management | `ThemeViewModel.kt` | Create a `ThemeViewModel` responsible for fetching the available themes, getting the current user's theme from the `ThemeRepository`, and providing a function to update the theme. It will expose the current theme via a `StateFlow`. |
| **TASK-004** | Implement Theme Selector Dialog | `ThemeSelectorDialog.kt` | Create a composable dialog that displays the list of available themes. It will observe the current theme from `ThemeViewModel` and call the update function upon user selection. It will include previews for each theme. |
| **TASK-005** | Create a Top-Level Theme Provider | `ArthurLifeApp.kt`, `MainViewModel.kt` | The main `Activity` or a top-level composable (`ArthurLifeApp`) will host the `ThemeViewModel` (likely via a `MainViewModel`). It will collect the current `AppTheme` from the ViewModel and wrap the entire UI content in the corresponding theme provider (`MarioClassicTheme`, `MaterialTheme`, etc.), making it available via `LocalBaseTheme`. |
| **TASK-006** | Integrate Dialog into Settings | `SettingsDialog.kt` | Add a "Change Theme" option to the existing `SettingsDialog` that triggers the `ThemeSelectorDialog`. |
| **TASK-007** | Implement Unit and UI Tests | `ThemeViewModelTest.kt`, `ThemeRepositoryTest.kt`, `ThemeSelectorDialogTest.kt` | Write unit tests for the ViewModel and Repository. Write UI tests to verify the dialog's functionality and that theme changes are correctly applied across the app. |

## 3. Alternatives

- **ALT-001**: Storing theme preference in `SharedPreferences`. Rejected in favor of `DataStore` for its improved safety, asynchronous API, and robustness against runtime errors.
- **ALT-002**: Passing the theme object down the composable tree as a parameter. Rejected because using a `CompositionLocal` (`LocalBaseTheme`) is the idiomatic and more maintainable approach in Compose for providing ambient data.

## 4. Dependencies

- **DEP-001**: `androidx.datastore:datastore-preferences` - For local data storage.
- **DEP-002**: `com.google.dagger:hilt-android` - For dependency injection.
- **DEP-003**: Existing theme implementations (`MarioClassicTheme`, `MaterialTheme`).

## 5. Files

- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/data/theme/ThemeRepository.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/data/theme/ThemeDataStore.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/theme/AppTheme.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/di/DataModule.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/viewmodels/ThemeViewModel.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/components/dialogs/ThemeSelectorDialog.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/ArthurLifeApp.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/components/dialogs/SettingsDialog.kt`
- **CREATE**: `android-kotlin/app/src/test/java/com/arthurslife/app/presentation/viewmodels/ThemeViewModelTest.kt`
- **CREATE**: `android-kotlin/app/src/test/java/com/arthurslife/app/data/theme/ThemeRepositoryTest.kt`
- **CREATE**: `android-kotlin/app/src/androidTest/java/com/arthurslife/app/presentation/components/dialogs/ThemeSelectorDialogTest.kt`

## 6. Testing

- **TEST-001**: Verify `ThemeRepository` correctly saves and retrieves the selected theme from `DataStore`.
- **TEST-002**: Verify `ThemeViewModel` correctly exposes the current theme and updates it on request.
- **TEST-003**: In the `ThemeSelectorDialog`, verify that all themes are displayed and that selecting one updates the app's theme.
- **TEST-004**: Verify that the selected theme persists after the app is restarted.
- **TEST-005**: Verify that switching users also switches to their respective saved themes.
- **TEST-006**: Run accessibility checks on the `ThemeSelectorDialog` and on all themes to ensure compliance.

## 7. Risks & Assumptions

- **RISK-001**: A flicker or incorrect theme might be displayed for a moment on app startup before the user's preference is loaded from DataStore. This can be mitigated by loading the theme preference on a splash screen or showing a loading indicator.
- **ASSUMPTION-001**: A mechanism to observe the current user's ID is available to the `ThemeRepository` to manage user-specific settings.
- **ASSUMPTION-002**: The existing `SettingsDialog` can be easily modified to trigger the new `ThemeSelectorDialog`.

## 8. Related Specifications / Further Reading

- `docs/product-requirements-documents/feature-theme-system.md`
- `docs/architecture.md`
- [Jetpack DataStore Guide](https://developer.android.com/topic/libraries/architecture/datastore)
- [State and Jetpack Compose](https://developer.android.com/jetpack/compose/state)
