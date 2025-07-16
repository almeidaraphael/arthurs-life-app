# [TASK010] - Implement Admin-Based Tab Logic

**Status:** In Progress
**Added:** 2025-07-16
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plans/feature-bottom-navigation-bar-prd-fixes.md](docs/implementation-plans/feature-bottom-navigation-bar-prd-fixes.md)
**Source PRD:** [docs/product-requirements-documents/feature-bottom-navigation-bar.md](docs/product-requirements-documents/feature-bottom-navigation-bar.md)

## Original Request
Implement admin status logic to show Users vs Children tab for caregivers as required by PRD.

## Thought Process
The PRD requires different fourth tabs for caregivers based on admin status:
- Family Admin: Dashboard, Tasks, Rewards, Users
- Non-Admin: Dashboard, Tasks, Rewards, Children

This requires creating a CaregiverUsers tab, updating getItemsForRole to accept admin parameter, and updating BottomNavViewModel to use admin status from AuthPreferencesDataStore.

## Implementation Plan Reference
- Create CaregiverUsers data object in BottomNavItem.kt
- Update getItemsForRole method to accept admin status parameter
- Update BottomNavViewModel to use admin status from AuthPreferencesDataStore
- Ensure proper tab selection logic

## Progress Tracking

**Overall Status:** In Progress - 75%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 10.1 | Create CaregiverUsers data object | Completed | 2025-07-16 | Added CaregiverUsers data object with Users label |
| 10.2 | Update getItemsForRole with admin parameter | Completed | 2025-07-16 | Added isAdmin parameter with conditional logic |
| 10.3 | Update BottomNavViewModel to use admin status | Completed | 2025-07-16 | Updated combine to use isAdmin flow |
| 10.4 | Test admin-based tab logic | In Progress | 2025-07-16 | Tests failing - need to debug flow combination |

## Progress Log
### 2025-07-16
- Task created from PRD fix analysis
- Identified need for admin-based tab differentiation
- Depends on TASK008 and TASK009 completion
- Created CaregiverUsers data object in BottomNavItem.kt
- Updated getItemsForRole method to accept isAdmin parameter
- Updated BottomNavViewModel to combine isAdmin flow with role and auth flows
- Added admin caregiver test case
- Tests failing - flows not emitting properly, need to debug combine logic