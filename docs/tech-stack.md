# Technology Stack Guide - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Comprehensive overview of technologies, frameworks, and architectural decisions for LemonQwest Android application development.

## 📋 Document Overview

### Purpose

Document technology choices, architectural implementations, and provide rationale for platform decisions to guide development and ensure consistent technology usage across the project.

### Audience

- **Primary**: Developers and architects
- **Secondary**: New team members
- **Prerequisites**: Understanding of Android development and modern software architecture

### Scope

Covers core frameworks, architecture patterns, development tools, and platform support. Excludes deployment infrastructure or CI/CD tooling.

## 🎯 Quick Reference

### Key Information

- **Summary**: Android native development with Kotlin, Jetpack Compose, and DDD architecture
- **Related**: [architecture.md](architecture.md), [getting-started.md](getting-started.md)

### Common Tasks

- [Core Technologies](#core-framework)
- [Architecture Patterns](#architecture-implementation)
- [Development Tools](#development-tools)
- [Technology Decisions](#technology-decision-rationale)

## 📖 Main Content

### Section 1: Core Concepts

#### Core Framework

- Android Native
- Kotlin 2.1.0
- Java 21 (preferred) / Java 17 (fallback)
- Jetpack Compose
- Android Gradle Plugin 8.7.3
- Gradle 8.11

### Section 2: Implementation Details

#### Architecture Implementation

Domain-Driven Design (DDD):

- Aggregate Roots: User, Task, Token, Reward
- Value Objects: UserRole, TaskCategory, TaskDifficulty, RewardCategory
- Domain Events: TaskCompletedEvent, TokensEarnedEvent, RewardRedeemedEvent
- Repository Pattern: Clean data access interfaces
Application Layer:
- Use Cases: EarnToken, RedeemReward, TrackProgress
- Command/Query Separation (CQRS)
- Event-driven architecture

#### Architecture Overview

![System Context Diagram](diagrams/c4-context.svg)
![Component Diagram](diagrams/c4-component.svg)
![Container Diagram](diagrams/c4-container.svg)

#### State Management

- Hilt
- Jetpack Compose State
- Room Database
- DataStore
- Kotlin Coroutines
- Flow

#### Type Safety & Validation

- Kotlin null safety
- Kotlinx Serialization
- Data Classes
- Sealed Classes

#### Testing Framework

- JUnit 5
- MockK
- Turbine
- Robolectric
- Espresso
- Detekt
- KtLint

#### UI & Design System

- Jetpack Compose
- Material Design 3
- Theme System (Material Light/Dark, Mario Classic)
- Compose Navigation
- Coil
- TalkBack support

### Section 3: Configuration

#### Development Tools

- Android Studio
- Detekt
- KtLint

### Section 4: Examples

// Add practical technology usage examples if needed

### Section 5: Best Practices

- Use latest stable versions
- Prefer type safety and explicit configuration
- Validate toolchain setup

### Section 6: Troubleshooting

- Common technology stack issues and solutions

## 🔗 Integration Points

### Dependencies

- [architecture.md](architecture.md)
- [getting-started.md](getting-started.md)

### Related Features

- Domain-driven design, testing, UI, accessibility

## 📚 Additional Resources

### Internal Documentation

- [README.md](README.md)
- [architecture.md](architecture.md)
- [getting-started.md](getting-started.md)

## 📝 Contributing

### How to Contribute

Update documentation for major technology changes. Validate instructions for new tools.

### Review Process

Technical and editorial review required for all changes.

### Style Guidelines

Use clear, concise language and consistent terminology.

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md)
