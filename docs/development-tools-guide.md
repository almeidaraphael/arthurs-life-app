# Development Tools Guide - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Comprehensive guide to development tools and code quality measures for LemonQwest family task management application.

## 📋 Document Overview

### Purpose

Describe the toolchain and quality standards used to ensure high code quality, maintainability, and reliability.

### Audience

- **Primary**: Developers and maintainers
- **Secondary**: Technical reviewers
- **Prerequisites**: Familiarity with Android development and Kotlin

### Scope

Covers development tools, code quality, and workflow. Excludes planning and deployment.

## 🎯 Quick Reference

### Key Information

- **Summary**: Development tools and code quality standards for LemonQwest App
- **Related**: [development.md](development.md), [getting-started.md](getting-started.md), [contributing.md](contributing.md), [tech-stack.md](tech-stack.md)

### Common Tasks

- [Build the project](#immediate-usage)
- [Run static analysis](#immediate-usage)
- [Run tests](#immediate-usage)
- [Check code formatting](#immediate-usage)

## 📖 Main Content

### Section 1: Core Concepts

#### Core Development Tools

1. **Kotlin (Primary Language)**
   - Version: 2.1.0
   - Configuration: `build.gradle.kts`
2. **Detekt (Static Analysis)**
   - Configuration: `detekt.yml`
3. **Ktlint (Code Formatting)**
   - Configuration: `.editorconfig`
4. **JUnit 5 + MockK (Testing Framework)**
   - Configuration: `build.gradle.kts`
   - Coverage: 80% threshold
5. **Git Hooks (Quality Gates)**
   - Pre-commit validation: linting, formatting, tests

### Section 2: Implementation Details

#### Immediate Usage

```bash
# Build the Android project
make build
# Kotlin code formatting
make format
# Static analysis
make lint
# Run tests
make test
# Combined quality checks
make qa
# Install on device
make install
```

#### Available Scripts

```bash
# Development
make build                  # Build all variants (debug + release)
make install                # Install debug APK on device/emulator
make build-debug            # Create debug APK
make build-release          # Create release APK
# Code Quality
make format                 # Auto-format code with detekt
make lint                   # Run static analysis (detekt)
make qa                     # Run complete quality pipeline
make qa-full                # QA + coverage verification
# Testing
make test                   # Run all tests
make test-unit              # Run debug unit tests
make test-integration       # Run instrumented tests
make test-coverage          # Generate coverage report
```

#### Android Studio Integration

- Required Plugins: Kotlin, Detekt, PlantUML
- Auto-configured settings: Format on save, auto-import, Detekt on build

### Section 3: Configuration

#### Quality Standards

- Null safety: Explicit nullable types
- No raw types: Generic type parameters required
- Coroutine usage: Structured concurrency
- No unused imports: Clean codebase

#### Android Specific

- Accessibility compliance: All UI elements described
- Resource optimization: Efficient usage
- Lifecycle awareness: Proper handling

#### Accessibility Rules

- Content descriptions for all interactive elements
- Semantic markup for Compose components
- Screen reader compatibility
- Large text support

### Section 4: Examples

// Add practical examples if needed

### Section 5: Best Practices

- Run all checks before committing
- Use explicit imports
- Maintain code formatting

### Section 6: Troubleshooting

- Common toolchain issues and solutions

## 🔗 Integration Points

### Dependencies

- [development.md](development.md)
- [getting-started.md](getting-started.md)
- [contributing.md](contributing.md)
- [tech-stack.md](tech-stack.md)

### Related Features

- Code quality, testing, accessibility

## 📚 Additional Resources

### Internal Documentation

- [README.md](README.md)
- [development.md](development.md)
- [getting-started.md](getting-started.md)
- [contributing.md](contributing.md)
- [tech-stack.md](tech-stack.md)

## 📝 Contributing

### How to Contribute

Follow workflow and standards outlined above. Update documentation for major changes.

### Review Process

Technical and editorial review required for all contributions.

### Style Guidelines

Use clear, concise language and consistent terminology.

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md)
