# Theme System Feature

## Feature Overview

The theme system provides role-based theme customization with deep integration throughout Arthur's Life app. It goes beyond simple visual changes to include customized terminology, icons, backgrounds, and user experience elements that adapt to different user roles and preferences.

## Core Capabilities

### Theme Management
- **Role-based defaults**: Child users default to Mario Classic, Caregivers to Material Light
- **User customization**: Users can override defaults through Settings
- **Persistent preferences**: Individual theme settings saved per user role
- **Immediate application**: Theme changes apply instantly without restart

### Available Themes

#### Material Light Theme
- **Target**: Caregivers and professional users
- **Style**: Clean Material Design 3 implementation
- **Colors**: Light background with Material You palette
- **Typography**: Standard Material Design scales
- **Shapes**: Rounded corners (4dp-28dp)
- **Language**: Professional ("Tasks", "Badges", "Settings")
- **Avatar**: 👤

#### Material Dark Theme  
- **Target**: Users preferring dark interfaces
- **Style**: Dark mode Material Design 3
- **Colors**: Dark backgrounds with high contrast
- **Typography**: Matches Material Light
- **Shapes**: Same rounded corner system
- **Language**: Professional terminology
- **Avatar**: 👤

#### Mario Classic Theme
- **Target**: Children and gaming enthusiasts  
- **Style**: Playful retro gaming aesthetic
- **Colors**: Mario-inspired palette (red, blue, green, yellow, orange)
- **Typography**: Monospace fonts for retro feel
- **Shapes**: Pixel-perfect (0dp corners)
- **Language**: Gaming ("Quests", "Coins", "Castle Guardian")
- **Background**: Mario yellow tint
- **Avatar**: 🍄

## Implementation Architecture

### Core Components

#### BaseAppTheme Interface
Defines the contract all themes must implement:
- Material Design 3 theming (ColorScheme, Shapes, Typography)
- Theme-specific customization (background, icons, terminology)
- Semantic icon mapping system
- Avatar and visual identity elements

#### ThemeManager
- Central theme registry and lookup
- Theme metadata generation for UI
- Fallback logic for missing themes
- Bidirectional theme-key mapping

#### ThemeViewModel
- Role-based theme loading
- Default theme assignment logic
- Theme persistence through DataStore
- Reactive theme state management

### Theme-Aware Components

#### Semantic Icon System
Maps abstract concepts to theme-specific icons:
- Tasks: clipboard (Material) vs sword (Mario)
- Rewards: gift (Material) vs treasure (Mario)  
- Progress: bars (Material) vs XP meters (Mario)
- Settings: gear (Material) vs castle (Mario)

#### ThemeAwareIcon
The system requires a theme-aware icon component that:

- **Semantic Icon Mapping**: Maps abstract concepts to theme-specific icons based on current theme selection
- **Content Description Support**: Provides accessible content descriptions for screen readers and accessibility tools
- **Modifier Support**: Allows for flexible styling and layout customization through modifier parameters
- **Dynamic Theme Response**: Automatically updates icon representations when theme changes occur

#### ThemeAwareAvatar
- Role-appropriate avatar based on theme
- Mario: 🍄 mushroom icon
- Material: 👤 professional user icon

#### ThemeAwareBackground  
- Theme-specific background styling
- Mario: Yellow background tint
- Material: Standard backgrounds

### UI Integration

#### Theme Selector Component
- Visual theme preview with color swatches
- Theme metadata display (name, description)
- Interactive selection with live preview
- Theme-aware styling of selector itself

#### Theme Settings Screen
- Role-based theme management interface
- Integration with app navigation
- Settings persistence and loading
- Live theme preview capabilities

## User Experience Features

### Terminology System
Each theme uses consistent language throughout the app:

| Concept | Material Themes | Mario Theme |
|---------|----------------|-------------|
| Tasks | "Tasks" | "Quests" |
| Tokens | "Badges" | "Coins" |
| Rewards | "Rewards" | "Power-ups" |
| Settings | "Settings" | "Castle Guardian" |
| Profile | "Profile" | "Player Card" |

### Role-Based Experience

#### Child Mode + Mario Theme
- Gaming terminology makes tasks feel like quests
- Bright, engaging color palette
- Retro gaming aesthetic
- Mushroom avatar for playful identity
- Yellow background tint for immersion

#### Caregiver Mode + Material Theme
- Professional, clean interface
- Standard task management terminology
- High readability and information density
- Professional user avatar
- Optimized for monitoring and management

### Visual Customization Depth

#### Color Systems
- Complete Material Design 3 color scheme customization
- Theme-specific accent and primary colors
- High contrast ratios for accessibility
- Consistent color application across components

#### Shape Systems
- Material themes: Modern rounded corners (4dp-28dp range)
- Mario theme: Pixel-perfect retro styling (0dp corners)
- Consistent shape application to buttons, cards, and containers
- Theme-specific component styling

#### Typography
- Material themes: Standard Material Design typography scales
- Mario theme: Monospace fonts for retro gaming feel
- Consistent type hierarchy across themes
- Readable text sizing for all user groups

## Technical Implementation

### Data Persistence
The theme system requires role-based preference storage:

- **Role-Specific Defaults**: Child users default to Mario Classic theme, while Caregivers default to Material Light theme
- **User Override Support**: Individual users can override default themes through settings interface
- **Persistent Storage**: Theme preferences are saved locally and persist across app sessions
- **Cross-Device Sync**: Theme preferences sync across family devices to maintain consistent user experience

### Theme Loading Flow
1. User authentication determines role
2. ThemeViewModel loads theme preference for role
3. ThemeManager resolves theme implementation
4. BaseAppTheme provides complete theme configuration
5. Compose theme providers apply throughout UI
6. All components automatically adapt to new theme

### Integration Points

#### Application Level
- MainActivity automatically loads theme based on user role
- Theme context available throughout component tree
- Navigation components use theme-aware styling
- State preserved across screen transitions

#### Component Level
- All UI components automatically adapt through composition locals
- Semantic icon system ensures theme consistency
- Terminology system provides appropriate language
- Material Design 3 integration for consistent behavior

### Performance Considerations
- Efficient theme switching with minimal recomposition
- Theme assets loaded on-demand
- Cached theme configurations for smooth transitions
- Optimized semantic icon resolution

## Future Extensibility

### Adding New Themes
1. Implement BaseAppTheme interface
2. Register with ThemeManager
3. Add theme metadata and preview assets
4. Define semantic icon mappings
5. Create terminology translations
6. Test across all user roles and components

### Theme Customization Options
- User-defined color scheme modifications
- Custom terminology overrides
- Personalized icon selections
- Background image customization
- Typography size adjustments

### Advanced Features
- Seasonal theme variations
- Achievement-unlocked themes
- Family-shared custom themes
- Theme synchronization across devices
- Theme preview in settings

---

**Related Features:** [User Management](user-management.md) | [Achievement System](achievement-system.md) | [Accessibility Features](accessibility-features.md)