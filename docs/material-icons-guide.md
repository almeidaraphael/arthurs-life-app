# Material Icons Usage Guide - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Guide to using Material Design icons in LemonQwest App, including setup, usage patterns, migration notes, and best practices.

## 📋 Document Overview

### Purpose

Explain how to use Material Design icons, including setup, usage, migration, and best practices.

### Audience

- **Primary**: UI developers and designers
- **Secondary**: Technical reviewers
- **Prerequisites**: Familiarity with Jetpack Compose and Android development

### Scope

Covers icon setup, usage, migration, and best practices. Excludes branding and custom icon design.

## 🎯 Quick Reference

### Key Information

- **Summary**: Material Design icons usage for LemonQwest App
- **Related**: [development.md](development.md), [tech-stack.md](tech-stack.md), [contributing.md](contributing.md)

### Common Tasks

- [Setup icons](#setup)
- [Use icons in Compose](#usage)
- [Migrate icon names](#migration-notes)

## 📖 Main Content

### Section 1: Core Concepts

#### Overview

LemonQwest App uses the `androidx.compose.material:material-icons-extended` library for a comprehensive set of Material Design icons.

### Section 2: Implementation Details

#### Setup

Add to project dependencies:

```kotlin
implementation(libs.androidx.material.icons.extended)
```

#### Usage

Import example:

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IconName
```

Common icons:

- `Icons.Default.ArrowBack` – Back navigation
- `Icons.Default.MoreVert` – Menu
- `Icons.Default.Settings` – Settings
- `Icons.Default.Add` – Add new item
- `Icons.Default.CloudUpload` – Upload/backup
- `Icons.Default.CloudDownload` – Download/restore
- `Icons.Default.Download` – Export
- `Icons.Default.Clear` – Delete/clear
- `Icons.Default.FilterList` – Filter
- `Icons.Default.Search` – Search
- `Icons.Default.CheckCircle` – Success/completed
- `Icons.Default.Star` – Favorites/tokens/rewards
- `Icons.Default.Info` – Information
- `Icons.Default.Person` – User profile
- `Icons.Default.AccountCircle` – Profile placeholder
- `Icons.Default.Face` – Child selection
- `Icons.Default.SupervisedUserCircle` – Family member/avatar

### Section 3: Configuration

#### Migration Notes

- `More` → `MoreVert`
- `Save` → `CloudDownload` or `Download`
- `Filter` → `FilterList`
- `Upload` → `CloudUpload`
Extended-only icons:
- `CloudUpload`, `CloudDownload`, `FilterList`, `FilterAlt`, `MoreVert`, `MoreHoriz`, `Download`, `Upload`, `Refresh`, `Sync`, `Visibility`, `VisibilityOff`, `Edit`, `Delete`, `KeyboardArrowUp`, `KeyboardArrowDown`

### Section 4: Examples

// Add practical icon usage examples if needed

### Section 5: Best Practices

1. Use semantic icons for clear meaning
2. Be consistent across the app
3. Always provide `contentDescription` for accessibility
4. Import only needed icons (no wildcards)

### Section 6: Troubleshooting

- Use IDE auto-completion to verify icon availability
- Consult official Compose Material Icons documentation

## 🔗 Integration Points

### Dependencies

- [development.md](development.md)
- [tech-stack.md](tech-stack.md)
- [contributing.md](contributing.md)

### Related Features

- UI components, accessibility, theming

## 📚 Additional Resources

### Internal Documentation

- [README.md](README.md)
- [development.md](development.md)
- [tech-stack.md](tech-stack.md)
- [contributing.md](contributing.md)

## 📝 Contributing

### How to Contribute

Update documentation for major icon usage changes. Validate instructions for new icon sets.

### Review Process

Technical and editorial review required for all changes.

### Style Guidelines

Use clear, concise language and consistent terminology.

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md)
