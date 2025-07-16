# [TASK009] - Update AuthPreferencesDataStore for Admin Status

**Status:** Pending
**Added:** 2025-07-16
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plans/feature-bottom-navigation-bar-prd-fixes.md](docs/implementation-plans/feature-bottom-navigation-bar-prd-fixes.md)
**Source PRD:** [docs/product-requirements-documents/feature-bottom-navigation-bar.md](docs/product-requirements-documents/feature-bottom-navigation-bar.md)

## Original Request
Update AuthPreferencesDataStore to track admin status for proper navigation tab differentiation.

## Thought Process
The BottomNavViewModel needs to access the current user's admin status to determine which tabs to show. This requires adding admin status tracking to the AuthPreferencesDataStore alongside the existing role tracking.

## Implementation Plan Reference
- Add IS_ADMIN preference key to AuthPreferencesDataStore
- Add isAdmin Flow property
- Update setCurrentUser method to accept admin status
- Update clearCurrentUser method to clear admin status
- Ensure proper data flow to BottomNavViewModel

## Progress Tracking

**Overall Status:** Pending - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 9.1 | Add IS_ADMIN preference key | Pending | 2025-07-16 | Add to companion object |
| 9.2 | Add isAdmin Flow property | Pending | 2025-07-16 | Expose admin status as Flow |
| 9.3 | Update setCurrentUser method | Pending | 2025-07-16 | Accept admin status parameter |
| 9.4 | Update clearCurrentUser method | Pending | 2025-07-16 | Clear admin status on logout |

## Progress Log
### 2025-07-16
- Task created from PRD fix analysis
- Identified need for admin status in preferences layer
- Depends on TASK008 completion for User model changes