# Java Version Guide

[🏠 Back to Main README](../README.md)

## Overview

This project follows a **Java 21 preferred, Java 17 fallback** strategy to balance modern features with dependency compatibility.

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **Getting Started** | [getting-started.md](getting-started.md) |
| **Development Guide** | [development.md](development.md) |
| **Tech Stack** | [tech-stack.md](tech-stack.md) |

## Version Policy

### Primary Target: Java 21 (LTS)
- **Preference**: Java 21 is the preferred development environment
- **Benefits**: 
  - Latest language features and performance improvements
  - Best compatibility with modern Android tooling
  - Long-term support (LTS) version
  - Enhanced garbage collection and memory management

### Fallback Target: Java 17 (LTS)
- **Usage**: Used when Java 21 causes compatibility issues with dependencies
- **Common Issues Requiring Fallback**:
  - Hilt annotation processing errors
  - Third-party library compatibility issues
  - CI/CD environment limitations

## Implementation

### Build Configuration
The project build files target Java 21 by default:

```kotlin
// build.gradle.kts
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
kotlinOptions {
    jvmTarget = "21"
}
```

When fallback is needed, switch to:
```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions {
    jvmTarget = "17"
}
```

### Environment Setup
Developers should have both versions available:

```bash
# Primary: Java 21
export JAVA_HOME=/path/to/java21

# Fallback: Java 17
# export JAVA_HOME=/path/to/java17
```

## Troubleshooting

### When to Switch to Java 17
Switch to Java 17 when encountering:
- `Unsupported class file major version 65` errors
- Annotation processor compilation failures
- Third-party dependency compatibility issues

### How to Switch
1. Install Java 17 if not already available
2. Update `JAVA_HOME` environment variable
3. Update `gradle.properties` with `org.gradle.java.home`
4. Update build.gradle.kts compile options
5. Stop Gradle daemon: `./gradlew --stop`
6. Clean and rebuild: `./gradlew clean build`

## CI/CD Considerations
- CI environments should prioritize Java 21
- Fallback to Java 17 if compatibility issues arise
- Document any fallback decisions in build logs

## Future Strategy
- Monitor dependency ecosystem for Java 21 compatibility improvements
- Upgrade dependencies when Java 21 support is stable
- Eventually standardize on Java 21 when ecosystem matures

---

For setup instructions, see [Getting Started Guide](getting-started.md).
