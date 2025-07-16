# [TASK013] - Define Top Bar State

**Status:** Pending
**Added:** 2025-07-15
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md](docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md)
**Source PRD:** [docs/product-requirements-documents/feature-top-navigation-bar.prd.md](docs/product-requirements-documents/feature-top-navigation-bar.prd.md)

## Original Request
Create a data class `TopBarState` to model all possible elements of the top bar (avatar, token balance, progress, etc.). Create a `TopBarViewModel` to build and expose a `StateFlow<TopBarState>` based on the current user role and navigation route.

## Thought Process
Centralizing the top bar's state into a `TopBarState` data class and managing it with a dedicated `TopBarViewModel` is a clean architectural approach. It decouples the UI from the complex logic of determining what to show based on user role and current screen, making the system more maintainable and testable.

## Implementation Plan Reference
- **Task ID**: TASK-001 (from plan)
- **Description**: Define Top Bar State
- **Files to Edit/Create**: `TopBarState.kt`, `TopBarViewModel.kt`
- **Details**: Create a data class `TopBarState` to model all possible elements of the top bar (avatar, token balance, progress, etc.). Create a `TopBarViewModel` to build and expose a `StateFlow<TopBarState>` based on the current user role and navigation route.

## Progress Tracking

**Overall Status:** Not Started - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 13.1 | Create `TopBarState.kt` data class | Not Started | 2025-07-15 | |
| 13.2 | Create `TopBarViewModel.kt` | Not Started | 2025-07-15 | |
| 13.3 | Inject dependencies (`UserSession`, `NavController`, etc.) | Not Started | 2025-07-15 | |
| 13.4 | Implement logic to build `TopBarState` based on role and route | Not Started | 2025-07-15 | |
| 13.5 | Expose `StateFlow<TopBarState>` from the ViewModel | Not Started | 2025-07-15 | |
| 13.6 | Define exact elements for Children Mode (Home/Tasks/Rewards/Achievements) | Not Started | 2025-07-16 | Per PRD TB-2 requirements |
| 13.7 | Define exact elements for Caregiver Mode (Home/Other screens) | Not Started | 2025-07-16 | Per PRD TB-2 requirements |
| 13.8 | Implement role/screen detection logic in TopBarViewModel | Not Started | 2025-07-16 | Per PRD TB-2 requirements |

## Progress Log
### 2025-07-15
- Task created from implementation plan.

### 2025-07-16
- Task renumbered from TASK018 to TASK013 as part of feature-based reorganization
- Updated task ID references and numbering throughout
