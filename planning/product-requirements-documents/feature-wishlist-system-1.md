---
title: Wishlist System PRD
version: 1.0
date: 2025-07-13
---

# PRD: Wishlist System

## 1. Product overview

### 1.1 Document title and version
* PRD: Wishlist System
* Version: 1.0

### 1.2 Product summary
* The wishlist system enables children to save toward higher-cost rewards, track progress, and set savings goals. Caregivers gain insights for gift planning and can contribute bonus tokens. The system supports family integration, motivation, and analytics for savings achievement.
* Integration with rewards, tokens, user management, and achievements ensures seamless savings and planning.

## 2. Goals

### 2.1 Business goals
* Increase motivation for long-term goals
* Support family coordination and gift planning
* Provide analytics for wishlist usage and savings achievement

### 2.2 User goals
* Children can add, track, and prioritize wishlist items
* Caregivers can view, plan, and contribute to wishlists

### 2.3 Non-goals
* External wishlist integration
* Real-money purchases

## 3. User personas

### 3.1 Key user types
* Child
* Caregiver

### 3.2 Basic persona details
* **Child**: Adds and tracks wishlist items, sets savings goals
* **Caregiver**: Views and contributes to wishlists, plans gifts

### 3.3 Role-based access
* **Child**: Add/remove items, set savings goals, view progress
* **Caregiver**: View, contribute, and plan gifts

## 4. Functional requirements

* **Wishlist Management** (Priority: High)
  * Add/remove items, progress tracking, savings goals
* **Caregiver Insights & Contributions** (Priority: Medium)
  * View wishlists, contribute bonus tokens, gift planning
* **Family Integration** (Priority: Medium)
  * Event coordination, family bonuses
* **Analytics & Success Metrics** (Priority: Medium)
  * Usage, savings achievement, family interaction
* **Integration** (Priority: High)
  * Reward, token, user, achievement systems

## 5. User experience

### 5.1 Entry points & first-time user flow
* Wishlist accessible from reward catalog and profile
* Onboarding introduces wishlist feature and management

### 5.2 Core experience
* **Add to Wishlist**: Child browses rewards, adds desired items to wishlist, tracks progress toward redemption
  * Ensures motivation and goal setting

### 5.3 Detailed User Flows

#### Wishlist Management Flow (from use-cases.md)
**Primary Actor:** Child
**Goal:** Track desired rewards and progress toward redemption
**Flow:**
1. Opens reward catalog or profile
2. Adds rewards to wishlist
3. Views wishlist with token requirements and progress
4. Tracks progress and receives notifications when close to redemption
5. Removes items from wishlist after redemption

#### Wishlist UI (from wireframes.md)
```
+---------------------------------------------------+
| Wishlist                                          |
|---------------------------------------------------|
| Desired Rewards:                                  |
| 🎮 Game Time (50 coins)   🍕 Pizza Night (100 coins) |
| 🎨 Art Set (80 coins)                              |
|                                                   |
| Progress:                                         |
| [Game Time] 40/50 coins (80%)                     |
| [Pizza Night] 90/100 coins (90%)                  |
|                                                   |
| Notifications:                                    |
| [Game Time] - Only 10 coins left!                 |
+---------------------------------------------------+
| 🏠 Home  ✅ Tasks  🛍️ Rewards  🏆 Achieve  👤 Profile |
+=====================================================+
```

### 5.4 UI/UX highlights
* Progress tracking and notifications
* Animated wishlist updates
* Theme-aware terminology: "Coins" and "Power-ups" for Mario Classic; "Tokens" and "Rewards" for Material themes

## 6. Narrative
Children plan and save for desired rewards, tracking progress and setting goals. Caregivers gain insights for gift planning and can contribute to savings. The system supports motivation and coordination.

## 7. Success metrics

### 7.1 User-centric metrics
* Wishlist usage rate
* Savings achievement frequency

### 7.2 Business metrics
* Increase in long-term motivation
* Family interaction frequency

### 7.3 Technical metrics
* Wishlist update latency
* Data persistence success rate

## 8. Technical considerations

### 8.1 Integration points
* Reward, token, achievement, analytics systems

### 8.2 Data storage & privacy
* Store wishlist data locally; no external sharing
* No collection of personally identifiable information

### 8.3 Scalability & performance
* Efficient wishlist updates and notifications

### 8.4 Non-Functional Requirements
* Responsive wishlist UI for phones/tablets
* Secure local storage for wishlist data
* Accessible progress bars and notifications
* Error messages in child-appropriate language

### 8.5 Potential challenges
* Preventing accidental removal of wishlist items
* Handling large wishlists efficiently

## 9. Milestones & sequencing

### 9.1 Project estimate
* Medium: 2 weeks

### 9.2 Team size & composition
* 2: Android dev, backend dev

### 9.3 Suggested phases
* **Phase 1**: Wishlist management & progress tracking (1 week)
  * Add/remove, progress, savings goals
* **Phase 2**: Caregiver insights & family integration (1 week)
  * Contributions, planning, events

## 10. User stories

### 10.1. Child adds and tracks wishlist item
* **ID**: US-WISH-1
* **Description**: As a child, I want to add items to my wishlist and track progress.
* **Acceptance criteria**:
  * Items can be added/removed
  * Progress is tracked and visualized

### 10.2. Caregiver views and contributes to wishlist
* **ID**: US-WISH-2
* **Description**: As a caregiver, I want to view my child's wishlist and contribute bonus tokens.
* **Acceptance criteria**:
  * Wishlists are visible
  * Bonus contributions are tracked

### 10.3. Caregiver coordinates family events for wishlist bonuses
* **ID**: US-WISH-3
* **Description**: As a caregiver, I want to coordinate family events and manage wishlist bonuses.
* **Acceptance criteria**:
  * Events can be created and managed
  * Bonus contributions are distributed
