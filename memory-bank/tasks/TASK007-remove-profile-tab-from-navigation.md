# [TASK007] - Remove Profile Tab from Navigation

**Status:** In Progress
**Added:** 2025-07-16
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plans/feature-bottom-navigation-bar-prd-fixes.md](docs/implementation-plans/feature-bottom-navigation-bar-prd-fixes.md)
**Source PRD:** [docs/product-requirements-documents/feature-bottom-navigation-bar.md](docs/product-requirements-documents/feature-bottom-navigation-bar.md)

## Original Request
Fix PRD violations in bottom navigation bar - currently showing 5 tabs instead of maximum 4 as required by PRD.

## Thought Process
The PRD clearly states FR2: "The navigation bar must never display more than four tabs." Currently, both Child and Caregiver roles show 5 tabs including Profile. According to the PRD acceptance criteria:
- Child should have: Home, Rewards, Tasks, Achievements (4 tabs)
- Caregiver should have: Dashboard, Tasks, Rewards, Users/Children (4 tabs)

Profile tab is not mentioned in the PRD requirements and must be removed to meet compliance.

## Implementation Plan Reference
- Remove ChildProfile and CaregiverProfile data objects from BottomNavItem.kt
- Remove Profile tab from getItemsForRole() method for both roles
- Remove Profile routes from getAllRoutes() method
- Update tests to reflect 4-tab structure

## Progress Tracking

**Overall Status:** In Progress - 25%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 7.1 | Remove ChildProfile and CaregiverProfile data objects | In Progress | 2025-07-16 | Started removing Profile tab references |
| 7.2 | Update getItemsForRole() method to exclude Profile | Pending | 2025-07-16 | Waiting for 7.1 completion |
| 7.3 | Update getAllRoutes() method to exclude Profile | Pending | 2025-07-16 | Waiting for 7.1 completion |
| 7.4 | Run detekt and build verification | Pending | 2025-07-16 | Final verification step |

## Progress Log
### 2025-07-16
- Task created from PRD fix analysis
- Started removing Profile tab data objects from BottomNavItem.kt
- Identified need to update getItemsForRole() and getAllRoutes() methods
- Currently working on removing all Profile tab references