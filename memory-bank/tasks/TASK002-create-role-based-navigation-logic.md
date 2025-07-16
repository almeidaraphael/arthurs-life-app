# [TASK002] - Create Role-Based Navigation Logic

**Status:** Pending
**Added:** 2025-07-15
**Updated:** 2025-07-15

## Source Documents
**Implementation Plan:** [docs/implementation-plans/feature-bottom-navigation-bar.md](docs/implementation-plans/feature-bottom-navigation-bar.md)
**Source PRD:** [docs/product-requirements-documents/feature-bottom-navigation-bar.md](docs/product-requirements-documents/feature-bottom-navigation-bar.md)

## Original Request
Create a `ViewModel` to determine which `BottomNavItem`s to display based on the current user's role and admin status. This ViewModel will expose a state Flow of the current list of navigation items.

## Thought Process
Separating the logic for determining navigation items into a `ViewModel` is crucial for adhering to Clean Architecture. It keeps the UI layer (`Composable`) clean and focused on rendering, while the `ViewModel` handles the business logic of user roles.

## Implementation Plan Reference
- **Task ID**: TASK-002
- **Description**: Create Role-Based Navigation Logic
- **Files to Edit/Create**: `BottomNavViewModel.kt`
- **Details**: Create a `ViewModel` to determine which `BottomNavItem`s to display based on the current user's role and admin status. This ViewModel will expose a state Flow of the current list of navigation items.

## Progress Tracking

**Overall Status:** Not Started - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 2.1 | Create `BottomNavViewModel.kt` | Not Started | 2025-07-15 | |
| 2.2 | Inject `UserSession` repository to get user role | Not Started | 2025-07-15 | |
| 2.3 | Implement logic to select `BottomNavItem`s based on role | Not Started | 2025-07-15 | |
| 2.4 | Expose a `StateFlow<List<BottomNavItem>>` | Not Started | 2025-07-15 | |

## Progress Log
### 2025-07-15
- Task created from implementation plan.
