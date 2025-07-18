# Contributing Guide – Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Guide for contributing to LemonQwest App, a native Android family task management app built with Kotlin, Java, and Jetpack Compose. Read carefully to ensure your contributions meet technical and quality standards.

## 📋 Document Overview

### Purpose

Explain how to contribute to the project, including workflow, standards, and review process.

### Audience

- **Primary**: New and existing contributors
- **Secondary**: Technical reviewers and maintainers
- **Prerequisites**: Familiarity with Android development and project structure

### Scope

Covers contribution workflow, code standards, testing, accessibility, and PR guidelines. Excludes project management and planning.

## 🎯 Quick Reference

### Key Information

- **Summary**: Contribution workflow and standards for LemonQwest App
- **Related**: [architecture.md](architecture.md), [tech-stack.md](tech-stack.md), [testing.md](testing.md)

### Common Tasks

- [Development Setup](#development-setup)
- [Workflow & Best Practices](#workflow--best-practices)
- [Testing & Quality Assurance](#testing--quality-assurance)
- [Accessibility & Child Safety](#accessibility--child-safety)
- [Commit & PR Guidelines](#commit--pr-guidelines)

## 📖 Main Content

### Section 1: Core Concepts

#### Code of Conduct

- Prioritize child safety, privacy, and well-being.
- Maintain high code quality and documentation standards.
- Communicate respectfully and inclusively.
- Support educational value and positive behavior reinforcement.

### Section 2: Implementation Details

#### Development Setup

**Prerequisites:**

- Java 21 (preferred) or Java 17 (fallback)
- Android Studio (latest)
- Android SDK API level 24+
- Git

**Quick Setup:**

```zsh
# Verify Java version
java -version  # Should show Java 21 or Java 17

# Clone and setup
git clone <repository-url>
cd arthurs-life-app

# Build and verify
make build
make format
make lint
make test
```

#### Workflow & Best Practices

1. Start with Domain Layer
2. Implement Use Cases
3. Add Infrastructure
4. Build UI
5. Wire Dependencies
6. Test Thoroughly

Patterns to Follow:

- DDD, SOLID, DRY
- Repository pattern
- MVVM architecture
- Dependency Injection (Hilt)
- Explicit imports only (no wildcards)
- No business logic in composables
- Use string resources (no hardcoded strings)
- Use named constants (no magic numbers)
- Prefer immutable data classes

#### Testing & Quality Assurance

- Essential business rules testing required (focus on critical business logic)
- Use JUnit, Espresso, MockK
- Run all checks before submitting:

  ```zsh
  make copilot-pipeline     # Complete mandatory pipeline
  # OR step by step:
  make format
  make lint
  make build
  make test
  make install
  ```

- All tests and static analysis must pass (ZERO Detekt violations)
- Use descriptive test names (Given-When-Then)
- Mock external dependencies

#### Accessibility & Child Safety

- All UI must support TalkBack and semantic roles
- Minimum 4.5:1 color contrast
- Input validation and secure storage
- COPPA compliance
- Provide `contentDescription` for images
- Use `LazyColumn` for long lists

#### Commit & PR Guidelines

- Use clear, descriptive commit messages (imperative mood)
- Reference related issues in PRs
- Ensure all checks pass before requesting review
- Document major architectural or pattern decisions in PR description
- Update documentation if you change APIs, workflows, or patterns

## 🔗 Integration Points

### Dependencies

- [architecture.md](architecture.md)
- [tech-stack.md](tech-stack.md)
- [theme-system.md](theme-system.md)
- [testing.md](testing.md)
- [security.md](security.md)

### Related Features

- Domain-driven design, theme system, token economy

## 📚 Additional Resources

### Internal Documentation

- [README.md](README.md)
- [architecture.md](architecture.md)
- [tech-stack.md](tech-stack.md)
- [theme-system.md](theme-system.md)
- [testing.md](testing.md)
- [security.md](security.md)

## 📝 Contributing

### How to Contribute

Follow workflow and standards outlined above. Update documentation for major changes.

### Review Process

Technical and editorial review required for all contributions.

### Style Guidelines

Use clear, concise language and consistent terminology.

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md)
