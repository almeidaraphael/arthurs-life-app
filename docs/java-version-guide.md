# Java Version Guide - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Guide to Java version strategy for LemonQwest App, balancing modern features with dependency compatibility.

## 📋 Document Overview

### Purpose

Explain Java version policy, setup, troubleshooting, and CI/CD considerations.

### Audience

- **Primary**: Developers and maintainers
- **Secondary**: Technical reviewers
- **Prerequisites**: Familiarity with Android development and Java

### Scope

Covers Java version policy, setup, troubleshooting, and CI/CD. Excludes planning and deployment.

## 🎯 Quick Reference

### Key Information

- **Summary**: Java 21 preferred, Java 17 fallback strategy
- **Related**: [getting-started.md](getting-started.md), [development.md](development.md), [tech-stack.md](tech-stack.md)

### Common Tasks

- [Set up Java 21](#environment-setup)
- [Switch to Java 17](#troubleshooting)
- [Update build configuration](#implementation)

## 📖 Main Content

### Section 1: Core Concepts

#### Version Policy

- **Primary Target: Java 21 (LTS)**
  - Latest features, performance, LTS
  - Best compatibility with modern Android tooling
  - Enhanced garbage collection and memory management
- **Fallback Target: Java 17 (LTS)**
  - Used when Java 21 causes compatibility issues
  - Common issues: Hilt annotation errors, library compatibility, CI/CD limitations

### Section 2: Implementation Details

#### Build Configuration

Default is Java 21:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
kotlinOptions {
    jvmTarget = "21"
}
```

Fallback to Java 17:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions {
    jvmTarget = "17"
}
```

#### Environment Setup

Developers should have both versions available:

```bash
# Primary: Java 21
export JAVA_HOME=/path/to/java21
# Fallback: Java 17
# export JAVA_HOME=/path/to/java17
```

### Section 3: Configuration

#### Troubleshooting

Switch to Java 17 if you encounter:

- `Unsupported class file major version 65` errors
- Annotation processor compilation failures
- Third-party dependency compatibility issues

How to switch:

1. Install Java 17
2. Update `JAVA_HOME`
3. Update `gradle.properties` with `org.gradle.java.home`
4. Update build.gradle.kts compile options
5. Stop Gradle daemon: `make stop-daemons`
6. Clean and rebuild: `make clean-all && make build`

### Section 4: Examples

// Add practical setup examples if needed

### Section 5: Best Practices

- Prefer Java 21 unless compatibility issues arise
- Document fallback decisions in build logs

### Section 6: Troubleshooting

- See above for switching steps

## 🔗 Integration Points

### Dependencies

- [getting-started.md](getting-started.md)
- [development.md](development.md)
- [tech-stack.md](tech-stack.md)

### Related Features

- Build configuration, CI/CD, dependency management

## 📚 Additional Resources

### Internal Documentation

- [getting-started.md](getting-started.md)
- [development.md](development.md)
- [tech-stack.md](tech-stack.md)

## 📝 Contributing

### How to Contribute

Update documentation for major Java version changes. Validate instructions for new environments.

### Review Process

Technical and editorial review required for all changes.

### Style Guidelines

Use clear, concise language and consistent terminology.

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md)
