# [TASK015] - Implement Required Dialogs

**Status:** Pending
**Added:** 2025-07-15
**Updated:** 2025-07-16

## Source Documents
**Implementation Plan:** [docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md](docs/implementation-plan-documents/feature-top-navigation-bar.ipd.md)
**Source PRD:** [docs/product-requirements-documents/feature-top-navigation-bar.prd.md](docs/product-requirements-documents/feature-top-navigation-bar.prd.md)

## Original Request
Create or refactor the required dialog composables. Ensure they are self-contained and can be triggered from the top bar.

## Thought Process
The top bar acts as a trigger for many different dialogs. This task involves ensuring all these dialogs (`ProfileDialog`, `SettingsDialog`, etc.) are implemented as clean, reusable composables. They should be independent of the screen they are shown on, receiving all necessary data and callbacks as parameters.

## Implementation Plan Reference
- **Task ID**: TASK-003 (from plan)
- **Description**: Implement Required Dialogs
- **Files to Edit/Create**: `ProfileDialog.kt`, `SettingsDialog.kt`, `UserSelectorDialog.kt`, etc.
- **Details**: Create or refactor the required dialog composables. Ensure they are self-contained and can be triggered from the top bar.

## Progress Tracking

**Overall Status:** Not Started - 0%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 15.1 | Implement `ProfileDialog.kt` | Not Started | 2025-07-15 | |
| 15.2 | Implement/Refactor `SettingsDialog.kt` | Not Started | 2025-07-15 | |
| 15.3 | Implement `UserSelectorDialog.kt` | Not Started | 2025-07-15 | |
| 15.4 | Ensure all dialogs are stateless and theme-aware | Not Started | 2025-07-15 | |
| 15.5 | Implement `AvatarDialog.kt` (avatars, photo, gallery) | Not Started | 2025-07-16 | Per PRD FR-4 requirements |
| 15.6 | Implement `ChildSelectorDialog.kt` (child selection for caregivers) | Not Started | 2025-07-16 | Per PRD FR-4 requirements |
| 15.7 | Implement `LanguageSelectorDialog.kt` (EN-US, PT-BR) | Not Started | 2025-07-16 | Per PRD FR-4 requirements |

## Progress Log
### 2025-07-15
- Task created from implementation plan.

### 2025-07-16
- Task renumbered from TASK020 to TASK015 as part of feature-based reorganization
- Updated task ID references and numbering throughout
