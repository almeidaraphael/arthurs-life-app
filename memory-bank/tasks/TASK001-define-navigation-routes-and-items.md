# [TASK001] - Define Navigation Routes and Items

**Status:** Pending
**Added:** 2025-07-15
**Updated:** 2025-07-15

## Source Documents
**Implementation Plan:** [docs/implementation-plans/feature-bottom-navigation-bar.md](docs/implementation-plans/feature-bottom-navigation-bar.md)
**Source PRD:** [docs/product-requirements-documents/feature-bottom-navigation-bar.md](docs/product-requirements-documents/feature-bottom-navigation-bar.md)

## Original Request
Define the sealed class `BottomNavItem` to hold properties for each navigation item (route, label resource, icon resource). Update `AppNavigation` to include routes for all screens accessible from the bottom bar.

## Thought Process
This is the foundational step for the bottom navigation bar. By defining the navigation items in a sealed class, we create a type-safe and centralized definition for all possible destinations. This makes it easier to manage and less prone to errors.

## Implementation Plan Reference
- **Task ID**: TASK-001
- **Description**: Define Navigation Routes and Items
- **Files to Edit/Create**: `BottomNavItem.kt`, `AppNavigation.kt`
- **Details**: Create a sealed class `BottomNavItem` to define properties for each navigation item (route, label resource, icon resource). Update `AppNavigation` to include routes for all screens accessible from the bottom bar.

## Progress Tracking

**Overall Status:** Not Started - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 1.1 | Create `BottomNavItem.kt` with sealed class | Not Started | 2025-07-15 | |
| 1.2 | Add items for Child, Caregiver Admin, and Caregiver Non-Admin roles | Not Started | 2025-07-15 | |
| 1.3 | Update `AppNavigation.kt` with new routes | Not Started | 2025-07-15 | |

## Progress Log
### 2025-07-15
- Task created from implementation plan.
