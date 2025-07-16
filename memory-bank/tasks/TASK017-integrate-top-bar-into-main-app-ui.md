# [TASK017] - Integrate Top Bar into Main App UI

**Status:** Pending
**Added:** 2025-07-15
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md](docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md)
**Source PRD:** [docs/product-requirements-documents/feature-top-navigation-bar.prd.md](docs/product-requirements-documents/feature-top-navigation-bar.prd.md)

## Original Request
Add the `TopAppBar` to the main app scaffold, ensuring it's placed correctly and receives the `TopBarState`.

## Thought Process
This is the final integration step for the top bar UI. Placing it in the `topBar` slot of the main `AppScaffold` ensures it is displayed consistently across all relevant screens. It will be driven by the state exposed by the `TopBarViewModel`.

## Implementation Plan Reference
- **Task ID**: TASK-005 (from plan)
- **Description**: Integrate Top Bar into Main App UI
- **Files to Edit/Create**: `MainScreen.kt`, `AppScaffold.kt`
- **Details**: Add the `TopAppBar` to the main app scaffold, ensuring it's placed correctly and receives the `TopBarState`.

## Progress Tracking

**Overall Status:** Not Started - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 17.1 | Modify `AppScaffold.kt` to include the `TopAppBar` in the `topBar` slot | Not Started | 2025-07-15 | |
| 17.2 | In `MainScreen.kt`, collect `TopBarState` from the `TopBarViewModel` | Not Started | 2025-07-15 | |
| 17.3 | Pass the state and event handlers to the `TopAppBar` | Not Started | 2025-07-15 | |

## Progress Log
### 2025-07-15
- Task created from implementation plan.

### 2025-07-16
- Task renumbered from TASK022 to TASK017 as part of feature-based reorganization
- Updated task ID references and numbering throughout
