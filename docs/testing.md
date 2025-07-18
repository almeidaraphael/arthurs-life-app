# LemonQwest Android App – Essential Testing Guide (v2)

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

A concise, actionable guide to testing LemonQwest Android app, focused on proven patterns, architecture fixes, and troubleshooting. This document is for contributors, maintainers, and anyone writing or reviewing tests for the LemonQwest Android app.

## 📋 Document Overview

### Purpose
Establish robust, consistent standards for testing LemonQwest Android app, ensuring reliability, maintainability, and clarity across all test code.

### Audience
- **Primary**: Contributors, developers, and maintainers writing or reviewing tests
- **Secondary**: Technical reviewers, QA engineers
- **Prerequisites**: Familiarity with Kotlin, Android, and basic testing concepts

### Scope
Covers all test patterns, technologies, folder structure, and troubleshooting for the `/android-kotlin/app/src/test/` directory. Excludes planning documentation and non-Android test suites.

## 🎯 Quick Reference

### Key Information
- **Summary**: Standards and best practices for testing LemonQwest Android app
- **Status**: Actively maintained, v2 (2025-07-25)
- **Last Updated**: 2025-07-25
- **Related**: [Template](TEMPLATE.md), [Documentation Guidelines](DOCUMENTATION_GUIDELINES.md), [Planning Standards](../planning/FEATURE_DOCUMENTATION_STANDARDS.md)

### Common Tasks
- [Create new test](#test-categories--patterns)
- [Troubleshoot IllegalStateException](#illegalstateexception--root-cause--solution)
- [Review MockK usage](#mockk-best-practices)

## 📖 Main Content

### Section 1: Core Concepts

#### Test Technologies
- **JUnit 5**: Core unit and integration test framework
- **MockK**: Mocking library for Kotlin, explicit behavior required
- **Turbine**: Flow testing utility for Kotlin coroutines
- **Robolectric**: JVM-based Android UI and integration testing
- **Espresso**: Instrumented UI testing for Android
- **Detekt**: Static code analysis for Kotlin (ZERO violations required)
- **KtLint**: Kotlin code formatting enforcement
- **Hilt**: Dependency injection for test environments
- **Compose Test Rule**: Jetpack Compose UI testing

All tests must pass lint, formatting, and build checks. See Makefile for pipeline commands.

#### Testing Folder Structure
Tests are organized to reflect Clean Architecture boundaries and DDD principles:

```
android-kotlin/app/src/test/
├── domain/           # Domain layer tests (aggregates, use cases, business rules)
├── infrastructure/   # Data layer tests (repositories, DAOs, data sources)
├── presentation/     # UI layer tests (ViewModels, composables, navigation)
├── di/               # Dependency injection test modules
├── testutils/        # Shared test utilities and base classes
└── ...               # Other feature-specific test folders
```

**Best Practices:**
- Place each test in the corresponding layer folder
- Use `testutils/` for reusable test infrastructure
- Keep test files focused and under 200 lines
- Maintain cross-references to implementation files for traceability

### Section 2: Implementation Details

#### Core Principles
- **Business logic first:** Test business rules, not framework quirks.
- **Category-based patterns:** Use the right pattern for Repository, UseCase, ViewModel, or Integration tests.
- **Simplicity:** Prefer clear, minimal test code. Avoid over-engineering.
- **Explicit setup:** Always set up mocks and dispatchers explicitly.

#### Test Categories & Patterns

##### Category A: Repository/UseCase Tests
- Use standalone `runTest` pattern.
- No ViewModelTestBase or dispatcher injection needed.
- Example:
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {
    private lateinit var mockRepo: Repository
    @BeforeEach fun setUp() { MockKAnnotations.init(this) }
    @Test fun test() = runTest { /* ... */ }
}
```

##### Category B: ViewModel Tests
- Use `ViewModelTestBase` and `runViewModelTest`.
- Inject dispatcher (`CoroutineDispatcher = Dispatchers.IO`) into ViewModel.
- Remove coroutine launches from `init` blocks; use explicit `initialize()`.
- Example:
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest : ViewModelTestBase() {
    @Test fun test() = runViewModelTest {
        val viewModel = createViewModel()
        viewModel.initialize()
        // ...
    }
}
```

##### Category C: Integration Tests
- Use appropriate pattern for cross-component or database tests.
- Ensure proper dispatcher and resource management.

### Section 3: Configuration

#### Dispatcher Management
- Always use `MainDispatcherRule` for coroutine tests.
- Prefer `UnconfinedTestDispatcher` for ViewModel tests.
- Clean up with `Dispatchers.resetMain()` in `@AfterEach`.

#### MockK Best Practices
- Always use explicit mock behavior (`every { ... } returns ...`).
- Create mocks after `MockKAnnotations.init(this)`.
- `relaxed = true` is FORBIDDEN except for:
  - Function type mocks (not business logic)
  - Infrastructure integration tests (documented)
  - Legacy code cleanup (must be marked with TODO)

**Example – Justified Use:**
```kotlin
val callback: (String) -> Unit = mockk(relaxed = true)
```
**Example – FORBIDDEN Use:**
```kotlin
val mockRepository = mockk<UserRepository>(relaxed = true) // ❌ FORBIDDEN
```
**Always prefer explicit mock behavior:**
```kotlin
val mockRepository = mockk<UserRepository>()
every { mockRepository.getUser(any()) } returns testUser
```

### Section 4: Examples

#### ViewModel Architecture Transformation
- Remove coroutine launches from `init` blocks.
- Inject dispatcher into ViewModel.
- Use explicit `initialize()` method.
- Guard with `isInitialized` flag to prevent double initialization.

#### Test Suite Structure
- One concern per test class.
- Use descriptive test method names (backticks recommended).
- Minimal, deterministic test data (factory methods).
- Parameterize tests to reduce duplication.

### Section 5: Best Practices
- Never mix test patterns (e.g., don’t use ViewModelTestBase for domain logic tests).
- Avoid time-dependent assertions; use deterministic data.
- Keep test files simple and focused.
- Refactor legacy or complex test utilities for simplicity.

### Section 6: Troubleshooting

#### IllegalStateException – Root Cause & Solution
- **Problem:** ViewModel `init` blocks launching coroutines before dispatcher setup.
- **Solution:**
  - Remove coroutine launches from `init`.
  - Inject dispatcher into ViewModel.
  - Use explicit `initialize()` method.
  - Create ViewModel inside test context.

#### Quick Troubleshooting
- Missing `@OptIn(ExperimentalCoroutinesApi::class)` → Add annotation to test class.
- IllegalStateException in ViewModel tests → Check for dispatcher injection and explicit initialization.
- MockK timing issues → Move mock creation to `@BeforeEach`.
- Flaky tests → Use deterministic data, avoid time-dependent assertions.

## 🔗 Integration Points

### Dependencies
- **Internal**: [Template](TEMPLATE.md), [Documentation Guidelines](DOCUMENTATION_GUIDELINES.md)
- **External**: [JUnit 5](https://junit.org/junit5/), [MockK](https://mockk.io/), [Robolectric](http://robolectric.org/), [Espresso](https://developer.android.com/training/testing/espresso), [Detekt](https://detekt.dev/), [KtLint](https://ktlint.github.io/)
- **Planning**: [Planning Standards](../planning/FEATURE_DOCUMENTATION_STANDARDS.md)

### Related Features
- Domain-driven design and Clean Architecture boundaries
- Theme system and accessibility requirements (see [theme-system.md](theme-system.md))

## 📚 Additional Resources

### Internal Documentation
- [Documentation Template](TEMPLATE.md)
- [Documentation Guidelines](DOCUMENTATION_GUIDELINES.md)
- [Planning Documentation Standards](../planning/FEATURE_DOCUMENTATION_STANDARDS.md)
- [Contributing Guide](contributing.md)

### External Resources
- [Markdown Guide](https://www.markdownguide.org/)
- [GitHub Flavored Markdown](https://github.github.com/gfm/)
- [Technical Writing Best Practices](https://developers.google.com/tech-writing)

### Tools and Utilities
- [Detekt](https://detekt.dev/) - Static analysis for Kotlin
- [KtLint](https://ktlint.github.io/) - Kotlin code formatting
- [MockK](https://mockk.io/) - Kotlin mocking library

---

## 📝 Contributing

### How to Contribute
- Follow these guidelines and use [TEMPLATE.md](TEMPLATE.md) for new docs
- Validate all updates and test changes
- Submit for review following the established process

### Review Process
- Self-review for template compliance and clarity
- Peer review for technical accuracy
- Editorial review for grammar and consistency
- Link validation for all references
- Final approval for publication

### Style Guidelines
- Use clear, concise language
- Follow established formatting standards
- Include appropriate cross-references
- Maintain consistent terminology

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md) | [📋 Planning](../planning/README.md)
