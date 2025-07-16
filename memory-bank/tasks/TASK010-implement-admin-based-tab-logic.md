# [TASK010] - Implement Admin-Based Tab Logic

**Status:** Pending
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

**Overall Status:** Pending - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 10.1 | Create CaregiverUsers data object | Pending | 2025-07-16 | Add Users tab for Family Admin |
| 10.2 | Update getItemsForRole with admin parameter | Pending | 2025-07-16 | Differentiate tabs based on admin status |
| 10.3 | Update BottomNavViewModel to use admin status | Pending | 2025-07-16 | Combine role and admin status flows |
| 10.4 | Test admin-based tab logic | Pending | 2025-07-16 | Verify correct tabs for each scenario |

## Progress Log
### 2025-07-16
- Task created from PRD fix analysis
- Identified need for admin-based tab differentiation
- Depends on TASK008 and TASK009 completion