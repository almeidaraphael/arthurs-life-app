---
goal: Fix bottom navigation bar violations to meet PRD requirements
version: 1.0
date_created: 2025-07-16
last_updated: 2025-07-16
owner: Engineering
tags: [feature, navigation, ui, compose, prd-fixes]
---

# Introduction

This document outlines the implementation plan for fixing violations in the bottom navigation bar implementation to meet PRD requirements. The current implementation violates the 4-tab maximum rule and lacks admin status differentiation. This plan addresses these issues to ensure full compliance with the PRD specifications.

## 1. Requirements & Constraints

- **REQ-001**: Bottom navigation bar must display maximum 4 tabs (currently showing 5)
- **REQ-002**: Child role must show exactly: Home, Rewards, Tasks, Achievements (no Profile)
- **REQ-003**: Caregiver role must show exactly: Dashboard, Tasks, Rewards, Users/Children (no Profile)
- **REQ-004**: Fourth tab for caregivers must adapt based on admin status (Users for Family Admin, Children for Non-Admin)
- **REQ-005**: Labels must match PRD exactly (Dashboard not Home for caregivers)
- **CON-001**: Must maintain existing theme-aware functionality
- **CON-002**: Must maintain existing test coverage
- **CON-003**: All code must pass Detekt and build successfully
- **GUD-001**: Follow existing Clean Architecture patterns
- **PAT-001**: Use existing User domain model for admin status

## 2. Implementation Steps

The implementation will be broken down into the following tasks:

| Task ID | Description | Files to Edit/Create | Details |
|---|---|---|---|
| **TASK-020** | Remove Profile Tab from Navigation | `BottomNavItem.kt` | Remove ChildProfile and CaregiverProfile items to meet 4-tab maximum |
| **TASK-021** | Add Admin Status to User Model | `User.kt` | Add isAdmin boolean property to User domain model |
| **TASK-022** | Update AuthPreferencesDataStore for Admin Status | `AuthPreferencesDataStore.kt` | Add admin status tracking to preferences |
| **TASK-023** | Implement Admin-Based Tab Logic | `BottomNavItem.kt`, `BottomNavViewModel.kt` | Add getItemsForRole with admin parameter, create Users tab |
| **TASK-024** | Fix Tab Labels to Match PRD | `BottomNavItem.kt` | Update CaregiverDashboard label from "Home" to "Dashboard" |
| **TASK-025** | Update Tests for 4-Tab Structure | `BottomNavItemTest.kt`, `BottomNavViewModelTest.kt`, `ThemeAwareBottomNavigationBarTest.kt` | Remove Profile tab references, add admin status tests |

## 3. Alternatives

- **ALT-001**: Keep Profile tab and remove another tab. Rejected as PRD specifically defines which tabs are required.
- **ALT-002**: Create separate roles for Family Admin vs Non-Admin. Rejected as it's cleaner to use admin status property.

## 4. Dependencies

- **DEP-001**: Existing User domain model
- **DEP-002**: Existing AuthPreferencesDataStore
- **DEP-003**: Existing theme system
- **DEP-004**: Existing test infrastructure

## 5. Files

- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/domain/user/User.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/infrastructure/preferences/AuthPreferencesDataStore.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/navigation/BottomNavItem.kt`
- **MODIFY**: `android-kotlin/app/src/main/java/com/arthurslife/app/presentation/viewmodels/BottomNavViewModel.kt`
- **MODIFY**: `android-kotlin/app/src/test/java/com/arthurslife/app/presentation/navigation/BottomNavItemTest.kt`
- **MODIFY**: `android-kotlin/app/src/test/java/com/arthurslife/app/presentation/viewmodels/BottomNavViewModelTest.kt`
- **MODIFY**: `android-kotlin/app/src/androidTest/java/com/arthurslife/app/presentation/theme/components/ThemeAwareBottomNavigationBarTest.kt`

## 6. Testing

- **TEST-001**: Verify Child shows exactly 4 tabs: Home, Rewards, Tasks, Achievements
- **TEST-002**: Verify Caregiver Family Admin shows exactly 4 tabs: Dashboard, Tasks, Rewards, Users
- **TEST-003**: Verify Caregiver Non-Admin shows exactly 4 tabs: Dashboard, Tasks, Rewards, Children
- **TEST-004**: Verify no Profile tab appears in any role
- **TEST-005**: Verify labels match PRD exactly
- **TEST-006**: Verify admin status logic works correctly

## 7. Risks & Assumptions

- **RISK-001**: Removing Profile tab may break existing navigation flows
- **ASSUMPTION-001**: Admin status can be determined from user session
- **ASSUMPTION-002**: Existing screens for Users and Children tabs already exist

## 8. Related Specifications / Further Reading

- `docs/product-requirements-documents/feature-bottom-navigation-bar.md`
- `docs/implementation-plans/feature-bottom-navigation-bar.md`
- `docs/architecture.md`