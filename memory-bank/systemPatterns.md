---
title: System Patterns – Arthur's Life App
author: Memory Bank (AI)
date: 2025-07-15
---

## System Architecture
- Native Android app (Kotlin 2.1.0, Jetpack Compose)
- Clean Architecture with strict separation: Domain → Infrastructure → Presentation
- DDD (Domain-Driven Design) for aggregates: User, Task, Token, Reward, Achievement
- Repository pattern for data access and abstraction
- Offline-first, local data storage with encryption
- Role-based access: Child, Caregiver, Admin
- Theme-aware UI with semantic icon mapping and terminology (role-based: Material for Caregiver/Admin, Mario Classic for Child)
- All code and architecture must strictly follow DDD, SOLID, and DRY principles
- Java 21 is the primary target, with Java 17 as fallback (explicit troubleshooting and build config steps)
- 80%+ test coverage for the domain layer is mandatory
- Zero Detekt/KtLint violations (static analysis and formatting)
- All UI must be accessible (TalkBack, semantic roles, 4.5:1 contrast, contentDescription for images)
- Security is a top priority: input validation, secure storage, COPPA compliance, and defense-in-depth
- Documentation standards: kebab-case filenames, emoji section headers, required navigation, and template compliance

## Key Technical Decisions
- All business logic in domain layer, UI logic in presentation layer
- Use of immutable value objects for roles, categories, balances, etc.
- Domain events for cross-aggregate communication (e.g., TaskCompleted, TokensEarned)
- Audit logging for all critical actions
- Accessibility and child safety as first-class requirements
- No external integrations or PII collection
- All code, tests, and documentation must be cross-referenced and kept in sync

## Design Patterns in Use
- Repository pattern (domain interfaces, infrastructure implementations)
- Dependency injection (Hilt)
- Observer pattern for real-time updates and progress tracking
- State machine for token economy and achievement unlocks
- Modular, testable components

## Component Relationships
- Task System integrates with Reward and Achievement Systems for token earning and progress
- Task Management System (caregiver) manages assignment, templates, analytics, and multi-child features
- Top Navigation Bar and dialogs are role- and theme-aware, with persistent access to profile/settings

## Source of Truth
All patterns and decisions are based on the latest PRDs and updated documentation in `docs/`. In case of conflict, PRDs override all other documentation.
