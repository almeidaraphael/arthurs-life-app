---
title: Tech Context – Arthur's Life App
author: Memory Bank (AI)
date: 2025-07-15
---

## Technologies Used
- Kotlin 2.1.0 (Android, Jetpack Compose)
- Java 21 (primary), Java 17 (fallback)
- Hilt (Dependency Injection)
- Room (Local database)
- DataStore (Preferences)
- JUnit, Espresso, MockK (Testing)
- Detekt, KtLint (Static analysis and formatting)

## Development Setup
- Native Android project structure
- Clean Architecture: domain, infrastructure, presentation, di, data
- Modular codebase for testability and maintainability
- All features designed for offline-first operation
- 80%+ test coverage for the domain layer is mandatory
- Zero Detekt/KtLint violations required

## Technical Constraints
- No external integrations or network dependencies for core features
- All data stored locally with encryption
- COPPA compliance and no PII collection
- Accessibility and child safety are mandatory
- All code, tests, and documentation must be cross-referenced and kept in sync

## Dependencies
- All dependencies are managed via Gradle and specified in `android-kotlin/` project
- Only trusted libraries for Android, DI, database, and testing

## Source of Truth
This tech context is based on the latest PRDs and updated documentation in `docs/`. In case of conflict, PRDs take precedence.
