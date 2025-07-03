# Development Tools Guide

[🏠 Back to Main README](../README.md)

## Overview

This guide covers the development tools and code quality measures used in Arthur's Life family task management application. Our toolchain ensures high code quality, maintainability, and reliability.

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **Development Guide** | [development.md](development.md) |
| **Getting Started** | [getting-started.md](getting-started.md) |
| **Contributing** | [contributing.md](contributing.md) |
| **Tech Stack** | [tech-stack.md](tech-stack.md) |

## Core Development Tools

### 1. **Kotlin (Primary Language)**

- **Version**: 2.1.0
- **Configuration**: `build.gradle.kts`
- **Key Features**:
  - Null safety for robust error prevention
  - Coroutines for efficient async programming
  - Data classes and sealed classes for domain modeling
  - Extension functions for clean, readable APIs

### 2. **Detekt (Static Analysis)**

- **Configuration**: `detekt.yml`
- **Key Rules**:
  - Code complexity analysis
  - Naming conventions enforcement
  - Potential bug detection
  - Code smell identification
  - **Critical for accessibility compliance**
  - Performance anti-pattern detection
  - Security vulnerability scanning

### 3. **Ktlint (Code Formatting)**

- **Configuration**: `.editorconfig`
- **Features**:
  - Consistent Kotlin code formatting
  - Automatic import organization
  - Standard Kotlin coding conventions
  - Git hook integration

### 4. **JUnit 5 + MockK (Testing Framework)**

- **Configuration**: `build.gradle.kts`
- **Coverage**: 80% threshold across all metrics
- **Features**:
  - Kotlin-first testing with MockK
  - Coroutine testing support
  - Accessibility testing with Espresso
  - Android instrumentation tests

### 5. **Git Hooks (Quality Gates)**

- **Pre-commit validation**: Runs linting, formatting, and tests
- **Quality gate**: Prevents broken code from entering repository

## ✅ **IMPLEMENTATION STATUS**

### 🎯 **SUCCESSFULLY IMPLEMENTED & WORKING**

#### 1. **Kotlin Configuration** ✅

- **Status**: ✅ **FULLY WORKING**
- **File**: `build.gradle.kts`
- **Test**: `./gradlew compileKotlin` - Successfully compiles Kotlin code
- **Features**: Null safety, coroutines, modern Kotlin features

#### 2. **Ktlint Code Formatting** ✅

- **Status**: ✅ **FULLY WORKING**
- **Files**: `.editorconfig`, `build.gradle.kts`
- **Test**: `./gradlew ktlintCheck` - Successfully formats Kotlin files
- **Features**: Standard Kotlin formatting, import organization

#### 3. **Detekt Static Analysis** ✅

- **Status**: ✅ **CONFIGURED & READY**
- **File**: `detekt.yml` 
- **Test**: `./gradlew detekt` - Runs static analysis
- **Features**: Code quality rules, complexity analysis

#### 4. **JUnit Testing Framework** ✅

- **Status**: ✅ **CONFIGURED & READY**
- **Files**: `build.gradle.kts`, test configurations
- **Features**: Unit tests, instrumentation tests, MockK integration

#### 5. **Gradle Build System** ✅

- **Status**: ✅ **DEPENDENCIES CONFIGURED**
- **File**: `build.gradle.kts` with comprehensive dependencies
- **Test**: `./gradlew build` completed successfully

---

## 🚀 **IMMEDIATE USAGE**

### Working Commands

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

### Current Results

- **Kotlin**: Compiles successfully with modern language features ✅
- **Ktlint**: Formats Kotlin code according to standards ✅
- **Detekt**: Analyzes code quality and finds issues ✅
- **JUnit**: Test framework configured for Android ✅

## 🚀 Available Scripts

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

## 🎛️ Android Studio Integration

### Required Plugins

- **Kotlin** - Built-in Kotlin support
- **Detekt** - Static analysis integration
- **PlantUML** - Diagram editing support

### Auto-configured Settings

- Format on save enabled for Kotlin
- Auto-import organization
- Detekt analysis on build
- Proper file exclusions for performance

## 🔍 Quality Standards

### Kotlin Rules

- **Null safety** - Explicit nullable types required
- **No raw types** - Generic type parameters required
- **Coroutine usage** - Proper structured concurrency
- **No unused imports** - Clean codebase

### Android Specific

- **Accessibility compliance** - All UI elements properly described
- **Resource optimization** - Efficient resource usage
- **Lifecycle awareness** - Proper Android component lifecycle handling

### Accessibility Rules (**Critical for Arthur's App**)

- **Content descriptions** required for all interactive elements
- **Semantic markup** for Compose components
- **Screen reader compatibility** validation
- **Large text support** testing

## 🔄 Development Workflow

### 1. **Before Coding**

```bash
./gradlew check  # Ensure starting point is clean
```

### 2. **During Development**

- Android Studio provides real-time feedback
- Kotlin compiler catches issues as you type
- Detekt highlights problems immediately

### 3. **Before Committing**

- Pre-commit hooks automatically run
- Only clean, formatted, tested code gets committed
- Failed checks prevent commit

### 4. **Testing Strategy**

```bash
./gradlew test              # Unit tests
./gradlew connectedAndroidTest # UI tests on device
./gradlew testDebugUnitTestCoverage # Coverage verification
```

## 📊 Code Quality Metrics

### Coverage Targets

- **Branches**: 80%
- **Functions**: 80%
- **Lines**: 80%
- **Statements**: 80%

### Performance Monitoring

- Build time optimization
- APK size tracking
- Unused code elimination

## 🚨 Accessibility Focus

Given Arthur's Life App's focus on children and accessibility:

### Automated Accessibility Checks

- Detekt rules catch common accessibility issues
- Screen reader compatibility validation
- Color contrast verification
- Keyboard navigation support

### Testing Accessibility

```bash
# All tests include accessibility assertions
./gradlew connectedAndroidTest
```

## 🔧 Troubleshooting

### Common Issues

**Detekt conflicts with Ktlint**

```bash
./gradlew ktlintFormat  # Auto-fixes most conflicts
./gradlew detekt        # Check remaining issues
```

**Kotlin compilation errors**

```bash
# Check build configuration
./gradlew clean build
```

**Test failures after refactoring**

```bash
# Update test snapshots if UI changes are intentional
./gradlew test --tests "com.arthurslife.app.*"
```

### Performance Optimization

**Slow builds**

```bash
# Use build cache and parallel builds
./gradlew build --build-cache --parallel
```

**Memory issues during testing**

```bash
# Configure Gradle daemon memory
echo "org.gradle.jvmargs=-Xmx4g" >> gradle.properties
```

## 🎯 Next Steps

1. **Setup environment**: Install Java 21 (preferred) or Java 17 (fallback) and Android Studio
2. **Validate setup**: `./gradlew check`
3. **Start developing**: `./gradlew build`
4. **Begin with tests**: Write tests first for critical accessibility features

## 📚 Additional Resources

- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Android Accessibility Guide](https://developer.android.com/guide/topics/ui/accessibility)
- [Detekt Rules](https://detekt.dev/docs/rules/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)

---

**Remember**: This toolchain is specifically designed to support Arthur's Life App's mission of creating accessible, high-quality Android software for children. Every tool and rule serves this purpose.

---

[🏠 Back to Main README](../README.md) | [🚀 Setup Guide](getting-started.md) | [📝 Contributing](contributing.md)