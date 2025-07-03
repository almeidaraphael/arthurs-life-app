# Navigation Structure

Complete navigation architecture for role-based access control and seamless user experience across all modes.

## Unified Bottom Navigation Bar (Role-Based)

### Child Mode Navigation (5 tabs)
- **Home** 🏠 - Token balance, progress, daily overview
- **Tasks** ✅ - Task list with timers and completion
- **Rewards** 🛍️ - Rewards store and wishlist management
- **Achievements** 🏆 - Global achievements and badges
- **Profile** 👤 - Theme selection and basic settings

### Caregiver Mode Navigation (5 tabs)
- **Dashboard** 📊 - Child selector, quick stats, overview
- **Tasks** ✅ - Manage tasks for selected child
- **Progress** 📈 - Analytics, reports, achievements
- **Children** 👥 - Multi-child management and switching
- **Profile** 👤 - Settings access, mode switching

### Admin Mode Navigation (5 tabs)
- **Dashboard** 📊 - Family overview, caregiver management
- **Tasks** ✅ - Family task templates and management
- **Progress** 📈 - Family-wide analytics and reports
- **Family** 👥 - Member management, roles, permissions
- **Profile** 👤 - Family settings, mode switching

## Secondary Navigation

### Through Profile Screen
**Available in All Modes:**
- **Settings** - Theme selection (Child), accessibility, notifications
- **Account** (Caregiver/Admin) - Profile management, security
- **Mode Switch** (Caregiver/Admin) - Switch between available modes

### Through Dashboard Screen (Caregiver/Admin)
**Caregiver Mode:**
- **Rewards Management** - Configure rewards for selected child
- **Child Settings & Permissions** - Individual child configuration
- **Task Templates Library** - Access and manage task templates
- **Weekly Report Generator** - Progress reports and analytics
- **View Child Wishlist** - Wishlist monitoring and gift planning

**Admin Mode:**
- **Family Management** - Add/remove family members, set permissions
- **Family Settings** - Family-wide configuration and security
- **Quick Actions** - Bulk operations and shortcuts
- **Family Backup & Security** - Data management and security
- **Caregiver Permission Templates** - Permission management

### Through Children Screen (Caregiver)
- **Child Switching** - Quick toggle between managed children
- **Child Settings** - Individual child configuration and permissions
- **Multi-Child Comparisons** - Side-by-side progress comparisons
- **View All Wishlists** - Family wishlist overview

## Navigation Hierarchy & Access Patterns

### 1. Primary Navigation (Bottom Bar)
- Always visible across the app
- Role-based tab visibility
- Contextual icons and labels
- Maintains active state across mode switches

### 2. Secondary Navigation (Within Primary Screens)
- Accessible through dedicated sections in primary screens
- Reduces bottom bar clutter
- Maintains clear information architecture
- Contextual actions based on current selection

### 3. Contextual Navigation (Mode Switching)
- Mode toggle button in profile for Caregiver/Admin users
- Child selector in dashboard for multi-child families (Caregiver Mode)
- Family overview in dashboard for family management (Admin Mode)
- Breadcrumbs for deep navigation states

## Screen Structure by Mode

### Child Mode Screens

#### Home Screen (Bottom Tab 1)
- [Token Balance: ⭐ 120] [Character Avatar]
- [Daily Progress Bar: 70%]
- [Today's Quick Stats]
- [Achievement Celebrations]
- [Quick Actions: Start Next Task, Check Rewards, View Today's Achievements]
- [Motivational Messages & Streak Counters]

#### Tasks Screen (Bottom Tab 2)
- [Active Tasks List with Timers]
- [Completed Tasks (Today)]
- [Tomorrow's Preview]
- [Task Completion Animations]

#### Rewards Screen (Bottom Tab 3)
- [Token Balance Display]
- [Available Rewards Grid]
- [Wishlist Section]
- [Wishlist Progress Indicators]

#### Achievements Screen (Bottom Tab 4)
- [Achievement Categories]
- [Unlocked Badges Gallery]
- [Locked Achievement Previews]
- [Progress Tracking for Near Achievements]

#### Profile Screen (Bottom Tab 5)
- [Character & Theme Settings]
- [Basic Settings Access]
- [Mode Toggle] (if Caregiver/Admin role)
- [Account Information]

### Caregiver Mode Screens

#### Dashboard Screen (Bottom Tab 1)
- [Selected Child Overview]
- [Daily Stats Cards]
- [Wishlist Insights]
- [Quick Actions Menu]
- **→ Rewards Management** (Secondary)
- **→ Child Settings & Permissions** (Secondary)
- **→ Weekly Report Generator** (Secondary)
- **→ Task Templates Library** (Secondary)
- **→ View Child Wishlist** (Secondary)

#### Tasks Screen (Bottom Tab 2)
- [Task Management for Selected Child]
- [Add/Edit/Delete Tasks]
- [Bulk Actions]
- [Task Templates]

#### Progress Screen (Bottom Tab 3)
- [Analytics & Reports for Selected Child]
- [Trends & Insights]
- [Export Options]
- [Achievement Tracking]

#### Children Screen (Bottom Tab 4)
- [Child Selector Interface]
- [Multi-Child Quick Stats]
- [Child Management Options]
- [Quick Switch Between Children]
- **→ View All Wishlists** (Secondary)

#### Profile Screen (Bottom Tab 5)
- [Caregiver Account Info]
- **→ Settings** (Secondary)
- **→ Account Management** (Secondary)
- [Mode Toggle: Switch Mode]

### Admin Mode Screens

#### Dashboard Screen (Bottom Tab 1)
- [Family Overview]
- [Caregiver Management Dashboard]
- [Family Health Status]
- **→ Quick Actions** (Secondary)
- **→ Family Settings Management** (Secondary)
- **→ Bulk Operations & Analytics** (Secondary)
- **→ Family Backup & Security** (Secondary)
- **→ Caregiver Permission Templates** (Secondary)

#### Tasks Screen (Bottom Tab 2)
- [Family Task Templates]
- [Task Management Across Family]
- [Template Creation & Sharing]
- [Feature Configuration]

#### Progress Screen (Bottom Tab 3)
- [Family-wide Analytics]
- [Caregiver Activity Reports]
- [Child Progress Comparison]
- [Data Export & Analysis]

#### Family Screen (Bottom Tab 4)
- [Family Member Management]
- [Caregiver Permission Settings]
- [Family Structure Configuration]
- [Access Control]

#### Profile Screen (Bottom Tab 5)
- [Admin Account]
- **→ Family Settings** (Secondary)
- **→ Security** (Secondary)
- [Mode Toggle: Switch Mode]

## Navigation State Management

### Mode Switching
- Authenticated mode switching via PIN
- Navigation state preservation during mode changes
- Role-appropriate tab visibility and access control
- Smooth transitions between modes

### Multi-Child Navigation (Caregiver Mode)
- Child selector persists across navigation
- Quick child switching from dashboard
- Child-specific data loading and state management
- Visual indicators showing currently selected child

### Deep Linking & Breadcrumbs
- Support for deep links to specific child data
- Breadcrumb navigation for complex flows
- Back button behavior preservation
- Navigation history for user convenience

## Accessibility Navigation

### Screen Reader Support
- Semantic navigation labels
- Role-based tab announcements
- Clear navigation hierarchy for assistive technologies
- Focus management during navigation

### Keyboard Navigation
- Tab order follows logical flow
- Keyboard shortcuts for power users
- Skip links for efficient navigation
- Clear focus indicators

### Visual Navigation Aids
- High contrast navigation elements
- Large touch targets for all navigation elements
- Clear visual hierarchy and grouping
- Color-blind friendly navigation indicators

---

**Next:** [Wireframes & Screen Layouts](wireframes.md) for detailed visual design specifications.
