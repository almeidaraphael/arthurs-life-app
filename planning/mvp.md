# MVP (Minimum Viable Product) Plan

[🏠 Back to Main README](../README.md) | [📋 Planning Overview](README.md)

## 📋 Page Navigation

| Section | Description |
|---------|-------------|
| [MVP Scope](#mvp-scope--objectives) | Core objectives and target users |
| [Core Features](#core-mvp-features) | Essential functionality for first release |
| [Development Plan](#development-timeline) | Implementation phases and timeline |
| [Success Metrics](#success-criteria) | Measurement criteria for MVP success |

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **App Structure** | [app-structure.md](app-structure.md) |
| **User Management** | [user-management.md](user-management.md) |
| **Task Management** | [features/task-management.md](features/task-management.md) |
| **Token Economy** | [token-economy.md](token-economy.md) |

## MVP Scope & Objectives

**Goal:** Create a functional family task management app with core features that demonstrate value to families while establishing a solid technical foundation for future expansion.

**Target Users:** Single-child families with one caregiver (expandable in future releases)

**Key Value Proposition:** 
- Children complete daily tasks and earn digital tokens
- Caregivers can assign tasks and monitor progress  
- Simple reward system motivates consistent task completion
- Offline-first approach works without internet connection

## Core MVP Features

### 1. User Roles & Authentication
**Scope:** Two-role system (Child + Caregiver)
- **Child Role:** Task completion, token earning, reward redemption
- **Caregiver Role:** Task management, progress monitoring, reward configuration
- **Authentication:** PIN-based access for secure role switching
- **Family Structure:** Single child per family unit

**Reference:** [App Structure & Roles](app-structure.md) - Child Mode + Caregiver Mode

### 2. Core Task Management
**Scope:** Essential task creation, assignment, and completion
- **Task Categories:** Personal Care, Household Chores, Homework (3 essential categories)
- **Task Creation:** Basic task with title, category, and token reward value
- **Task Scheduling:** Daily tasks with simple completion tracking
- **Task Execution:** Intuitive mark-complete interface for children
- **Progress Tracking:** Basic completion statistics and streak tracking

**Reference:** [Task Management System](features/task-management.md) - Core task operations

### 3. Basic Token Economy
**Scope:** Simple token earning and spending system
- **Token Earning:** Automatic token rewards upon task completion
- **Token Values:** Fixed reward amounts based on task category
- **Token Balance:** Clear display of current token balance
- **Spending System:** Token deduction for reward redemption
- **Balance Protection:** Prevent negative token balances

**Reference:** [Token Economy System](token-economy.md) - Core token operations

### 4. Essential Reward System
**Scope:** Basic reward catalog with custom reward creation
- **Reward Catalog:** 5-10 predefined rewards (screen time, treats, activities)
- **Custom Rewards:** Caregivers can create family-specific rewards
- **Token Redemption:** Simple redeem interface with confirmation
- **Reward Management:** Caregiver controls for reward availability
- **Cost Management:** Flexible token costs for different reward types

**Reference:** [Reward System](reward-system.md) - Basic reward operations

**Reference:** [Reward System](reward-system.md) - Basic redemption flow + simple custom creation

### 5. Basic Achievement System
**Scope:** 5 core achievements to demonstrate gamification
- **First Steps:** Complete first task
- **Task Master:** Complete all daily tasks
- **3-Day Streak:** Complete tasks for 3 consecutive days
- **Century Club:** Complete 10 total tasks (scaled down from 100)
- **Token Collector:** Earn 50 total tokens

**Reference:** [Global Achievement System](achievements.md) - Selected core achievements only  

### 6. Simple Progress Tracking
**Scope:** Basic analytics for caregiver oversight
- **Daily Overview:** Tasks completed today, tokens earned
- **Weekly Summary:** 7-day completion trends
- **Basic Charts:** Simple progress bars and percentages
- **No Advanced Analytics:** Complex insights post-MVP

**Reference:** [Progress Analytics](progress-analytics.md) - Basic reporting only

## MVP User Flows

### Core Child Flow
1. **Daily Task Completion**
   - Open app → see today's tasks
   - Select task → mark complete
   - Earn tokens → see balance update
   - View simple progress indicator

**Reference:** [Use Cases](use-cases.md) - UC-C1: Daily Task Completion Flow (simplified)

### Core Caregiver Flow  
1. **Task Setup & Monitoring**
   - Switch to caregiver mode
   - Create basic daily tasks for child
   - Monitor child's completion progress
   - Enable/disable rewards as needed

**Reference:** [Use Cases](use-cases.md) - UC-CG2: Daily Task Management (simplified)

## MVP Technical Architecture

### Core Data Models
```kotlin
// Simplified models for MVP
data class Task(
    val id: String,
    val title: String,
    val category: TaskCategory, // Personal Care, Household, Homework only
    val tokenReward: Int, // Fixed values only
    val isCompleted: Boolean,
    val assignedToUserId: String
)

data class User(
    val id: String,
    val name: String,
    val role: UserRole, // Child or Caregiver only
    val tokenBalance: Int
)

data class Reward(
    val id: String,
    val name: String,
    val description: String,
    val tokenCost: Int,
    val isActive: Boolean,
    val isCustom: Boolean // true for caregiver-created rewards
)

data class Achievement(
    val id: String,
    val name: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val target: Int
)
```

### MVP Tech Stack
- **Frontend:** Android (Kotlin) - Single platform initially
- **Database:** SQLite with Room persistence library
- **Architecture:** MVVM with Repository pattern
- **Authentication:** Local PIN-based role switching
- **Storage:** Local only - no cloud sync in MVP
- **Future:** Post-MVP migration to PostgreSQL API backend

## MVP Wireframes

### Key Screens for MVP
1. **Child Home Screen** - Today's tasks + token balance
2. **Child Tasks Screen** - Simple task list with complete buttons
3. **Child Rewards Screen** - Basic reward catalog
4. **Caregiver Dashboard** - Child overview + quick actions
5. **Caregiver Tasks Screen** - Add/edit tasks interface
6. **Caregiver Rewards Screen** - Add/edit custom rewards interface

**Reference:** [Wireframes](wireframes.md) - Core screens only, simplified layouts

## Features Explicitly Excluded from MVP

### User Management
- ❌ Admin role and family management
- ❌ Multi-child support
- ❌ Complex permission systems
- ❌ Family member invitations

### Task Management
- ❌ Complex recurring patterns (weekly, custom schedules)
- ❌ Task difficulty multipliers
- ❌ Task templates and bulk operations
- ❌ Task approval workflows

### Token Economy
- ❌ Token transaction history
- ❌ Difficulty-based multipliers
- ❌ Token penalties for missed tasks
- ❌ Advanced token validation

### Rewards & Wishlist
- ❌ Wishlist functionality and savings goals
- ❌ Reward categories and filtering
- ❌ Photo-based rewards
- ❌ Advanced reward validation and approval workflows

### Achievements
- ❌ Full achievement catalog (25+ achievements)
- ❌ Complex celebration animations
- ❌ Achievement sharing and family notifications
- ❌ Special unlocks and themes

### Analytics & Insights
- ❌ Advanced analytics and predictive insights
- ❌ Family comparison reports
- ❌ Machine learning recommendations
- ❌ Data export capabilities

### Accessibility
- ❌ Advanced accessibility features
- ❌ Text-to-speech integration
- ❌ Voice control
- ❌ Switch control support

### Data Management
- ❌ Cloud backup and restore
- ❌ Data export/import
- ❌ Cross-device synchronization
- ❌ Offline mode support

## MVP Success Metrics

### User Engagement
- **Task Completion Rate:** 70%+ daily task completion
- **Return Usage:** 60%+ users return after 7 days
- **Session Duration:** Average 5+ minutes per child session

### Feature Adoption
- **Token Earning:** 90%+ of completed tasks result in token rewards
- **Reward Redemption:** 50%+ of earned tokens are spent on rewards
- **Achievement Unlock:** 80%+ of users unlock at least 3 achievements

### Family Satisfaction
- **Caregiver Engagement:** 80%+ of caregivers use monitoring features weekly
- **Task Creation:** Average 5+ tasks created per child
- **Custom Reward Usage:** 60%+ of families create at least 3 custom rewards
- **Perceived Value:** 70%+ positive feedback on core value proposition

## Technical Architecture Details

### Local Database Schema (SQLite + Room)
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val tokenBalance: Int,
    val createdAt: Long
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val tokenReward: Int,
    val isCompleted: Boolean,
    val assignedToUserId: String,
    val createdAt: Long
)

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val tokenCost: Int,
    val isActive: Boolean,
    val isCustom: Boolean,
    val createdAt: Long
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val target: Int,
    val unlockedAt: Long?
)
```

### Core User Flows Enhancement

**Enhanced Caregiver Flow:**
1. **Custom Reward Creation**
   - Switch to caregiver mode
   - Navigate to rewards management
   - Create custom reward (name, description, token cost)
   - Enable/disable rewards as needed
   - Monitor child's reward usage

**Enhanced Child Flow:**
1. **Custom Reward Discovery**
   - Browse both predefined and custom rewards
   - See personalized rewards created by caregiver
   - Redeem tokens for family-specific rewards

---

**References:** 
- [App Structure & Roles](app-structure.md) - Full role system design
- [Use Cases & User Flows](use-cases.md) - Complete user flow documentation
- [Wireframes & Screen Layouts](wireframes.md) - Complete UI design reference
- [Task Management System](features/task-management.md) - Full task system specifications
- [Token Economy System](token-economy.md) - Complete economic system design
- [Global Achievement System](achievements.md) - Full gamification system
- [Reward System](reward-system.md) - Complete reward management
- [Progress Analytics](progress-analytics.md) - Full analytics capabilities