# [TASK016] - Create a Dialog Management System

**Status:** Pending
**Added:** 2025-07-15
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md](docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md)
**Source PRD:** [docs/product-requirements-documents/feature-top-navigation-bar.prd.md](docs/product-requirements-documents/feature-top-navigation-bar.prd.md)

## Original Request
Implement a centralized system to show/hide dialogs. The `TopBarViewModel` will request dialogs, and a higher-level `MainViewModel` or manager will control which dialog is currently visible.

## Thought Process
Managing dialog state can get messy if handled independently on each screen. A centralized `DialogManager` or similar mechanism, likely coordinated by the `MainViewModel`, will prevent multiple dialogs from appearing at once and provide a single source of truth for which dialog (if any) should be displayed. This is a robust pattern for handling dialogs in a complex app.

## Implementation Plan Reference
- **Task ID**: TASK-004 (from plan)
- **Description**: Create a Dialog Management System
- **Files to Edit/Create**: `DialogManager.kt`, `MainViewModel.kt`
- **Details**: Implement a centralized system to show/hide dialogs. The `TopBarViewModel` will request dialogs, and a higher-level `MainViewModel` or manager will control which dialog is currently visible.

## Progress Tracking

**Overall Status:** Not Started - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 16.1 | Define a sealed class or enum for all possible dialogs | Not Started | 2025-07-15 | |
| 16.2 | Modify `MainViewModel.kt` to hold the state of the currently visible dialog | Not Started | 2025-07-15 | |
| 16.3 | Implement functions in `MainViewModel` to show/hide dialogs | Not Started | 2025-07-15 | |
| 16.4 | Refactor `TopBarViewModel` to request dialogs via the `MainViewModel` | Not Started | 2025-07-15 | |

## Progress Log
### 2025-07-15
- Task created from implementation plan.

### 2025-07-16
- Task renumbered from TASK021 to TASK016 as part of feature-based reorganization
- Updated task ID references and numbering throughout
