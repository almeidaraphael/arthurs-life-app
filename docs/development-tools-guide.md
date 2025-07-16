# Development Tools Guide - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Comprehensive guide to development tools and code quality measures for Arthur's Life family task management application.

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
- **Summary**: Development tools and code quality standards for Arthur's Life App
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
./gradlew build
# Kotlin code formatting
./gradlew ktlintCheck
./gradlew ktlintFormat
# Static analysis
./gradlew detekt
# Run tests
./gradlew test
# Combined quality checks
./gradlew check
# Install on device
./gradlew installDebug
```

#### Available Scripts
```bash
# Development
./gradlew build             # Build the Android project
./gradlew installDebug      # Install debug APK on device/emulator
./gradlew assembleDebug     # Create debug APK
./gradlew assembleRelease   # Create release APK
# Code Quality
./gradlew ktlintCheck       # Check Kotlin formatting
./gradlew ktlintFormat      # Auto-format Kotlin code
./gradlew detekt            # Run static analysis
./gradlew check             # Run all quality checks
# Testing
./gradlew test              # Run unit tests
./gradlew testDebugUnitTest # Run debug unit tests
./gradlew connectedAndroidTest # Run instrumented tests
./gradlew testDebugUnitTestCoverage # Generate coverage report
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
