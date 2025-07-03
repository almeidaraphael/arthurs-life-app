# App Structure & Roles

## App Architecture Overview

**Single App with Three Modes:**
- The app has three distinct modes: Admin Mode, Caregiver Mode, and Child Mode.
- Role hierarchy: Admin > Caregiver > Child (default: "child").
- Users can toggle between modes based on their role permissions.
- Multi-child families supported - caregivers can switch between managing different children.
- This ensures shared family data, easier maintenance, and seamless experience.

## Mode Definitions

### 🧒 Child Mode
- **Purpose:** Primary app experience for children completing tasks and earning rewards
- **Features:**
  - Home screen with token balance and progress
  - Dedicated tasks list with visual schedules and timers
  - Dedicated rewards store with wishlist functionality
  - Dedicated achievements system with global badges and milestones
  - Profile management with theme customization
  - Fun, themed UI with favorite characters
  - Accessibility features (text-to-speech, large buttons, color options)

### 👨‍👩‍👧‍👦 Caregiver Mode
- **Purpose:** Manage and monitor children's activities and progress
- **Features:**
  - Child selector to manage multiple children
  - Set up and customize tasks and rewards per child
  - Monitor each child's progress and statistics
  - Adjust token values, schedules, and reminders
  - Manage accessibility and personalization settings per child

### 👑 Admin Mode
- **Purpose:** Family-level administration and management
- **Features:**
  - Family-level administration and management
  - Add/remove caregivers and children from the family
  - Configure family settings and permissions
  - Manage all family members and their access levels
  - Has all caregiver capabilities plus family administration

## Role & Permission System

### 🧒 CHILD ROLE
- **Purpose:** Primary app users who complete tasks and earn rewards
- **Capabilities:** Task completion, token earning, reward redemption, profile customization
- **Access Level:** Own data only
- **Available Modes:** Child Mode only

### 👨‍👩‍👧‍👦 CAREGIVER ROLE
- **Purpose:** Manage and monitor children's activities and progress
- **Who:** Father, mother, teacher, older sibling, or any trusted adult
- **Capabilities:** Child management, task creation, progress monitoring, reward configuration
- **Access Level:** Assigned children's data only (as permitted by admin)
- **Available Modes:** Child Mode + Caregiver Mode

### 👑 ADMIN ROLE
- **Purpose:** Family administration and caregiver management
- **Who:** Primary family administrator (usually a parent)
- **Capabilities:** All caregiver features + family member management, caregiver permissions, family settings
- **Access Level:** All family data and settings
- **Available Modes:** Child Mode + Caregiver Mode + Admin Mode

## Simplified Flow Principles

1. **One Primary Goal Per Flow:** Each use case focuses on a single objective
2. **Clear Entry Points:** Obvious navigation paths to start each flow
3. **Minimal Steps:** Keep flows under 5 steps when possible
4. **Role Separation:** Distinct capabilities and interfaces per role:
   - **Child:** Task execution and reward redemption
   - **Caregiver:** Child management and progress monitoring
   - **Admin:** Family administration and caregiver management
5. **Recovery Paths:** Clear error handling and recovery options
6. **Accessibility First:** All flows support assistive technologies

## Role Summary Matrix

| Role | Primary Use Cases | Key Features | Navigation Tabs |
|------|------------------|--------------|-----------------|
| **Child** | Task completion, Token redemption/wishlist, Achievement discovery, Theme customization | Timers, Rewards store, Global achievements, Theme selection | Home, Tasks, Rewards, Achievements, Profile |
| **Caregiver** | Child setup, Task management, Progress monitoring, Reward config, Multi-child | Child selector, Analytics, Custom rewards, Templates | Dashboard, Tasks, Progress, Children, Profile |
| **Admin** | Family management, Caregiver permissions, Family settings, Family analytics | Family member management, Permission control, Family settings | Dashboard, Tasks, Progress, Family, Profile |

---

**Next:** [Use Cases & Flows](use-cases.md) for detailed user interactions by role.
