# Material Icons Extended Usage Guide

[🏠 Back to Main README](../README.md)

## Overview
This project uses `androidx.compose.material:material-icons-extended` library to access a comprehensive set of Material Design icons. This provides many more icons than the basic `androidx.compose.material.icons.filled.*` package.

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **Development Guide** | [development.md](development.md) |
| **Tech Stack** | [tech-stack.md](tech-stack.md) |
| **Contributing** | [contributing.md](contributing.md) |

## Setup
The library is already included in the project dependencies:
```kotlin
implementation(libs.androidx.material.icons.extended)
```

## Usage

### Import Structure
```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IconName
```

### Common Icons Used in This Project

#### Navigation & Actions
- `Icons.Default.ArrowBack` - Back navigation
- `Icons.Default.MoreVert` - Three-dot menu
- `Icons.Default.Settings` - Settings/configuration
- `Icons.Default.Add` - Add/create new items

#### Data Management
- `Icons.Default.CloudUpload` - Upload/backup data
- `Icons.Default.CloudDownload` - Download/restore data
- `Icons.Default.Download` - Download/export files
- `Icons.Default.Clear` - Clear/delete data

#### Filtering & Search
- `Icons.Default.FilterList` - Filter options (replaces `Filter`)
- `Icons.Default.Search` - Search functionality

#### Status & Progress
- `Icons.Default.CheckCircle` - Completed/success
- `Icons.Default.Star` - Favorites/tokens/rewards
- `Icons.Default.Info` - Information

#### User & Profile
- `Icons.Default.Person` - User profile
- `Icons.Default.AccountCircle` - Profile picture placeholder
- `Icons.Default.Face` - Child selection

## Migration Notes

### Replaced Icons
- `Icons.Default.More` → `Icons.Default.MoreVert`
- `Icons.Default.Save` → `Icons.Default.CloudDownload` (for data operations) or `Icons.Default.Download` (for exports)
- `Icons.Default.Filter` → `Icons.Default.FilterList`
- `Icons.Default.Upload` → `Icons.Default.CloudUpload`

### Available but Not Basic
These icons are available in the extended library but not in the basic package:
- `CloudUpload`, `CloudDownload`
- `FilterList`, `FilterAlt`
- `MoreVert`, `MoreHoriz`
- `Download`, `Upload`
- `Refresh`, `Sync`
- `Visibility`, `VisibilityOff`
- `Edit`, `Delete`
- `KeyboardArrowUp`, `KeyboardArrowDown`

## Best Practices

1. **Always use semantic icons**: Choose icons that clearly represent the action
2. **Consistency**: Use the same icon for the same action throughout the app
3. **Accessibility**: Always provide `contentDescription` for screen readers
4. **Import only what you need**: Import specific icons rather than using wildcards

## Verification
To verify an icon exists, check the Android Compose Material Icons documentation or use IDE auto-completion after typing `Icons.Default.`
