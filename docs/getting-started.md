# Getting Started Guide - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Comprehensive setup and development guide for LemonQwest family task management Android application built with Kotlin and Jetpack Compose.

## 📋 Document Overview

### Purpose

Provide step-by-step setup instructions and development environment configuration for efficient development of LemonQwest Android application.

### Audience

- **Primary**: New developers setting up the development environment
- **Secondary**: Experienced developers onboarding to the project
- **Prerequisites**: Basic Android development knowledge and command line familiarity

### Scope

Covers development environment setup, project configuration, build instructions, and troubleshooting. Excludes deployment or production configuration.

## 🎯 Quick Reference

### Key Information

- **Summary**: Complete development environment setup for Android Kotlin development
- **Related**: [architecture.md](architecture.md), [development.md](development.md)

### Common Tasks

- [Prerequisites Setup](#prerequisites)
- [Project Installation](#installation)
- [Build and Run](#running-the-application)
- [Development Workflow](#development-workflow)

## 📖 Main Content

### Section 1: Core Concepts

#### Prerequisites

Before you begin, ensure you have the following installed:

- Android Studio (latest)
- Java Development Kit (JDK 21 preferred, JDK 17 fallback)
- Git

#### Platform-Specific Requirements

- Android Studio
- Android SDK (API level 24+; API 35 for compilation)
- Android Virtual Device (AVD)
- Java Development Kit (JDK 21 preferred, JDK 17 fallback)

#### Onboarding Flows

![Onboarding Flow](diagrams/onboarding-flow.svg)
![Family Setup Onboarding Flow](diagrams/family-setup-onboarding-flow.svg)

### Section 2: Implementation Details

#### Java Installation

**Linux/Ubuntu:**

```bash
sudo apt update && sudo apt install openjdk-21-jdk
sudo apt update && sudo apt install openjdk-17-jdk
java -version && javac -version
```

**macOS:**

```bash
brew install openjdk@21
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
brew install openjdk@17
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
```

**Windows:**
Download and install from [Adoptium](https://adoptium.net/) - choose JDK 21 or JDK 17

### Section 3: Configuration

#### Installation

1. Clone the repository:

```bash
git clone <repository-url>
cd arthurs-life-app/android-kotlin
```

2. Java setup:
Install Java 21 (recommended) or Java 17 (fallback)
Set environment variables in your shell profile:

```bash
export JAVA_HOME=/path/to/java21  # Or java17
```

### Section 4: Examples

// Add practical setup examples if needed

### Section 5: Best Practices

- Use latest Android Studio
- Prefer Java 21 unless compatibility issues arise
- Validate Java installation with `java -version`

### Section 6: Troubleshooting

- Switch to Java 17 if you encounter compatibility issues
- Clean and rebuild project after changing Java version

## 🔗 Integration Points

### Dependencies

- [architecture.md](architecture.md)
- [development.md](development.md)

### Related Features

- Development workflow, testing, code quality

## 📚 Additional Resources

### Internal Documentation

- [README.md](README.md)
- [architecture.md](architecture.md)
- [development.md](development.md)

## 📝 Contributing

### How to Contribute

Update documentation for major setup changes. Validate instructions for new environments.

### Review Process

Technical and editorial review required for all changes.

### Style Guidelines

Use clear, concise language and consistent terminology.

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md)
