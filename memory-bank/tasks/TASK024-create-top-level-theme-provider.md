# [TASK024] - Create a Top-Level Theme Provider

**Status:** Pending
**Added:** 2025-07-15
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plan-documents/feature-theme-system.ipd.md](docs/implementation-plan-documents/feature-theme-system.ipd.md)
**Source PRD:** [docs/product-requirements-documents/feature-theme-system.prd.md](docs/product-requirements-documents/feature-theme-system.prd.md)

## Original Request
The main `Activity` or a top-level composable (`ArthurLifeApp`) will host the `ThemeViewModel` (likely via a `MainViewModel`). It will collect the current `AppTheme` from the ViewModel and wrap the entire UI content in the corresponding theme provider (`MarioClassicTheme`, `MaterialTheme`, etc.), making it available via `LocalBaseTheme`.

## Thought Process
This is the highest level of integration for the theme system. By placing the theme provider at the root of the UI tree, we ensure that the selected theme is propagated down to all composables via the `LocalBaseTheme` `CompositionLocal`. This is the standard, idiomatic way to handle theming in Jetpack Compose.

## Implementation Plan Reference
- **Task ID**: TASK-005 (from plan)
- **Description**: Create a Top-Level Theme Provider
- **Files to Edit/Create**: `ArthurLifeApp.kt`, `MainViewModel.kt`
- **Details**: The main `Activity` or a top-level composable (`ArthurLifeApp`) will host the `ThemeViewModel` (likely via a `MainViewModel`). It will collect the current `AppTheme` from the ViewModel and wrap the entire UI content in the corresponding theme provider (`MarioClassicTheme`, `MaterialTheme`, etc.), making it available via `LocalBaseTheme`.

## Progress Tracking

**Overall Status:** Not Started - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 24.1 | Modify `MainViewModel.kt` to include `ThemeViewModel` logic or instance | Not Started | 2025-07-15 | |
| 24.2 | In `ArthurLifeApp.kt`, collect the current theme from the ViewModel | Not Started | 2025-07-15 | |
| 24.3 | Wrap the main content in a `when` statement that applies the correct theme provider | Not Started | 2025-07-15 | |

## Progress Log
### 2025-07-15
- Task created from implementation plan.

### 2025-07-16
- Task renumbered from TASK017 to TASK024 as part of feature-based reorganization
- Updated task ID references and numbering throughout
