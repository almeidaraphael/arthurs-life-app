# [TASK018] - Integrate Dialog into Settings

**Status:** Pending
**Added:** 2025-07-15
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md](docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md)
**Source PRD:** [docs/product-requirements-documents/feature-top-navigation-bar.prd.md](docs/product-requirements-documents/feature-top-navigation-bar.prd.md)

## Original Request
Add a "Change Theme" option to the existing `SettingsDialog` that triggers the `ThemeSelectorDialog`.

## Thought Process
This task connects the new theme selection feature to the existing user-facing settings. It's a small but important step for user experience, making the feature discoverable.

## Implementation Plan Reference
- **Task ID**: TASK-006 (from plan)
- **Description**: Integrate Dialog into Settings
- **Files to Edit/Create**: `SettingsDialog.kt`
- **Details**: Add a "Change Theme" option to the existing `SettingsDialog` that triggers the `ThemeSelectorDialog`.

## Progress Tracking

**Overall Status:** Not Started - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 18.1 | Modify `SettingsDialog.kt` to add a "Change Theme" button/item | Not Started | 2025-07-15 | |
| 18.2 | Wire up the button to trigger the `ThemeSelectorDialog` | Not Started | 2025-07-15 | |

## Progress Log
### 2025-07-15
- Task created from implementation plan.

### 2025-07-16
- Task renumbered from TASK023 to TASK018 as part of feature-based reorganization
- Updated task ID references and numbering throughout
