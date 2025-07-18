---
goal: Implement a user-based, persistent, and extensible theme system.
version: 2.0
date_created: 2025-07-15
last_updated: 2025-07-17
owner: Engineering
tags: [feature, ui, design, compose, theme]
source_prd: /docs/product-requirements-documents/feature-theme-system.prd.md
source_prd_section: 4, 9
---

# Implementation Plan Document: Theme System

> **Update 2025-07-17:**
> The PRD has changed from role-based to user-based theme selection. All users (children and caregivers) can select any available theme, independent of role. The implementation must update all requirements, steps, and tasks to match the new PRD. See progress log for rationale and next steps.

## 1. Requirements & Constraints

- **REQ-001**: Each user must be able to select a theme (Mario Classic, Material Light, Material Dark) regardless of their role. [FR-THEME-1, US-THEME-1, US-THEME-2]
- **REQ-002**: Theme preferences must be stored locally and persist across app sessions for each user. [FR-THEME-2, US-THEME-4]
- **REQ-003**: The entire app's UI, including icons, terminology, notifications, and dialogs, must update instantly when the theme is changed. [FR-THEME-3, FR-THEME-4, US-THEME-1, US-THEME-2, US-THEME-6, US-THEME-10]
- **REQ-004**: The system must be extensible to allow for new themes in the future, with dynamic registration. [FR-THEME-7]
- **REQ-005**: All themes must meet accessibility standards (4.5:1 contrast, TalkBack support). [FR-THEME-5]
- **REQ-006**: Theme selector dialog must provide live preview, color swatches, and accessible controls. [FR-THEME-11, US-THEME-3]
- **REQ-007**: Error handling for theme loading, with fallback and user notification. [FR-THEME-8]
- **CON-001**: The implementation must use Jetpack Compose and Hilt for dependency injection.
- **CON-002**: Theme preferences will be stored using Jetpack DataStore.
- **PAT-001**: The existing `BaseAppTheme` interface and `LocalBaseTheme` CompositionLocal will be used to provide theme properties to composables.

## 2. Implementation Steps

| Step ID   | Description | PRD User Story | Acceptance Criteria | Files to Edit/Create | Details |
|-----------|-------------|----------------|--------------------|---------------------|---------|
| TASK-001  | Define Theme Data Layer | US-THEME-1,2,4,7,8 | Theme selection, persistence, error fallback, extensibility | `ThemeRepository.kt`, `ThemeDataStore.kt`, `AppTheme.kt` | Create an `AppTheme` enum. Implement `ThemeDataStore` to save/retrieve a user's selected `AppTheme` using Jetpack DataStore. Create `ThemeRepository` as an abstraction over the DataStore. Handle error fallback and dynamic theme registration. |
| TASK-002  | Provide Theme Dependencies | US-THEME-1,2,4,8 | Dependency injection for theme system | `DataModule.kt` | Add Hilt providers in a `DataModule` for `ThemeRepository` and `ThemeDataStore`. |
| TASK-003  | Create Theme State Management | US-THEME-1,2,4,7 | Expose and update current theme, handle errors | `ThemeViewModel.kt` | Create a `ThemeViewModel` responsible for fetching the available themes, getting the current user's theme from the `ThemeRepository`, and providing a function to update the theme. It will expose the current theme via a `StateFlow`. Handle error fallback and user notification. |
| TASK-004  | Implement Theme Selector Dialog | US-THEME-3,11 | Live preview, swatches, accessible controls | `ThemeSelectorDialog.kt` | Create a composable dialog that displays the list of available themes. It will observe the current theme from `ThemeViewModel` and call the update function upon user selection. It will include previews for each theme, color swatches, and accessible controls. |
| TASK-005  | Create a Top-Level Theme Provider | US-THEME-1,2,3,4,5,6,10 | UI, icons, terminology, dialogs, notifications update instantly | `LemonQwestApp.kt`, `MainViewModel.kt` | The main `Activity` or a top-level composable (`LemonQwestApp`) will host the `ThemeViewModel` (likely via a `MainViewModel`). It will collect the current `AppTheme` from the ViewModel and wrap the entire UI content in the corresponding theme provider (`MarioClassicTheme`, `MaterialTheme`, etc.), making it available via `LocalBaseTheme`. Ensure all UI, icons, terminology, dialogs, and notifications are theme-aware. |
| TASK-006  | Integrate Dialog into Settings | US-THEME-1,2,3,11 | Settings dialog triggers theme selector | `SettingsDialog.kt` | Add a "Change Theme" option to the existing `SettingsDialog` that triggers the `ThemeSelectorDialog`. |
| TASK-007  | Implement Unit, UI, and Accessibility Tests | US-THEME-4,5,6,7,9,10 | Persistence, accessibility, semantic mapping, error handling, notifications | `ThemeViewModelTest.kt`, `ThemeRepositoryTest.kt`, `ThemeSelectorDialogTest.kt` | Write unit tests for the ViewModel and Repository. Write UI tests to verify the dialog's functionality and that theme changes are correctly applied across the app. Include accessibility and semantic mapping tests, error handling, and notification/dialog theming. |
| TASK-009  | Fix Theme Selector Dialog Bug | US-THEME-3,11 | Dialog must show all available themes (Material Light, Material Dark, Mario Classic) for any user, with live preview and accessible controls. When a theme is selected, the app's theme must update immediately for the current user. | `ThemeSelectorDialog.kt`, `ThemeViewModel.kt`, `ThemeRepository.kt` | Investigate and fix bug where only Material Light Theme is shown. Ensure dialog fetches and displays all available themes. On selection, update the app's theme for the current user and persist the change. Verify with UI and accessibility tests. |

## 3. Alternatives

- **ALT-001**: Storing theme preference in `SharedPreferences`. Rejected in favor of `DataStore` for its improved safety, asynchronous API, and robustness against runtime errors.
- **ALT-002**: Passing the theme object down the composable tree as a parameter. Rejected because using a `CompositionLocal` (`LocalBaseTheme`) is the idiomatic and more maintainable approach in Compose for providing ambient data.

## 4. Dependencies

- **DEP-001**: `androidx.datastore:datastore-preferences` - For local data storage.
- **DEP-002**: `com.google.dagger:hilt-android` - For dependency injection.
- **DEP-003**: Existing theme implementations (`MarioClassicTheme`, `MaterialTheme`).

## 5. Files

- **CREATE**: `android-kotlin/app/src/main/java/com/lemonqwest/app/data/theme/ThemeRepository.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/lemonqwest/app/data/theme/ThemeDataStore.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/lemonqwest/app/presentation/theme/AppTheme.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/lemonqwest/app/di/DataModule.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/lemonqwest/app/presentation/viewmodels/ThemeViewModel.kt`
- **CREATE**: `android-kotlin/app/src/main/java/com/lemonqwest/app/presentation/components/dialogs/ThemeSelectorDialog.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/lemonqwest/app/presentation/LemonQwestApp.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/lemonqwest/app/presentation/components/dialogs/SettingsDialog.kt`
- **CREATE**: `android-kotlin/app/src/test/java/com/lemonqwest/app/presentation/viewmodels/ThemeViewModelTest.kt`
- **CREATE**: `android-kotlin/app/src/test/java/com/lemonqwest/app/data/theme/ThemeRepositoryTest.kt`
- **CREATE**: `android-kotlin/app/src/androidTest/java/com/lemonqwest/app/presentation/components/dialogs/ThemeSelectorDialogTest.kt`

## 6. Testing

| Test ID   | Description | PRD Acceptance Criteria | Implementation Step |
|-----------|-------------|------------------------|--------------------|
| TEST-001  | Verify `ThemeRepository` correctly saves and retrieves the selected theme from `DataStore` | US-THEME-4 | TASK-001 |
| TEST-002  | Verify `ThemeViewModel` correctly exposes the current theme and updates it on request | US-THEME-1,2,4,7 | TASK-003 |
| TEST-003  | In the `ThemeSelectorDialog`, verify that all themes are displayed and that selecting one updates the app's theme | US-THEME-1,2,3,11 | TASK-004, TASK-006 |
| TEST-004  | Verify that the selected theme persists after the app is restarted | US-THEME-4 | TASK-001, TASK-003, TASK-005 |
| TEST-005  | Verify that switching users also switches to their respective saved themes | US-THEME-4 | TASK-001, TASK-003 |
| TEST-006  | Run accessibility checks on the `ThemeSelectorDialog` and on all themes to ensure compliance | US-THEME-5,9 | TASK-004, TASK-007 |
| TEST-007  | Verify semantic icon and terminology mapping updates with theme | US-THEME-6 | TASK-005, TASK-007 |
| TEST-008  | Verify error handling and fallback to default theme | US-THEME-7 | TASK-001, TASK-003, TASK-005, TASK-007 |
| TEST-009  | Verify notifications and dialogs are theme-aware | US-THEME-10 | TASK-005, TASK-007 |

## 7. Risks & Assumptions

- **RISK-001**: A flicker or incorrect theme might be displayed for a moment on app startup before the user's preference is loaded from DataStore. This can be mitigated by loading the theme preference on a splash screen or showing a loading indicator.
- **RISK-002**: Semantic mapping or notification theming may be missed in new features if not enforced in code review.
- **ASSUMPTION-001**: A mechanism to observe the current user's ID is available to the `ThemeRepository` to manage user-specific settings. The system no longer assigns themes by role; all theme selection and persistence is user-based.
- **ASSUMPTION-002**: The existing `SettingsDialog` can be easily modified to trigger the new `ThemeSelectorDialog`.

## 8. Related Specifications / Further Reading

## 9. Progress Log

### 2025-07-17 (PRD Update: Role-based → User-based)

### 2025-07-18 (Bug: Theme Selector Dialog Only Shows Material Light)

- Discovered bug: Theme selector dialog only displays Material Light Theme. Created TASK-009 to track investigation and resolution. Will ensure dialog displays all available themes for any user, and that selecting a theme immediately updates the app's theme for the current user. Live preview and accessibility features required by PRD and IPD will be verified with tests.
