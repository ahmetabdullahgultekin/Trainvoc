# Feature Flag UI - Integration Guide

**Date:** 2026-01-10
**Status:** UI Screens Ready for Integration

---

## 📱 UI Screens Created

### 1. AdminFeatureFlagScreen
**Route:** `Route.FEATURE_FLAGS_ADMIN`
**Purpose:** Admin dashboard for managing all 45 features

**Features:**
- ✅ Cost summary card (today, this month)
- ✅ Category filters (All, Core Learning, Multimedia, etc.)
- ✅ Feature list with toggle switches
- ✅ Expandable controls per feature
  - Rollout percentage slider (0-100%)
  - Daily API limit buttons (1K, 5K, 10K, Unlimited)
- ✅ Quick actions menu
  - Enable All Features
  - Disable Expensive Features
  - Reset Daily Usage
- ✅ Real-time cost tracking
- ✅ Error handling with dismissible alerts
- ✅ Loading states

### 2. UserFeatureFlagScreen
**Route:** `Route.FEATURE_FLAGS_USER`
**Purpose:** User preferences for feature opt-in/opt-out

**Features:**
- ✅ Info card explaining feature control
- ✅ Category filters
- ✅ Feature list with toggle switches
- ✅ Usage statistics (times used)
- ✅ Premium/Cost badges
- ✅ Quick actions menu (Enable All, Disable All)
- ✅ Error handling

---

## 🔌 Navigation Integration

### Step 1: Add to Navigation Graph

Find your main navigation composable (likely in `MainActivity.kt` or a dedicated navigation file) and add these routes:

```kotlin
import com.gultekinahmetabdullah.trainvoc.features.ui.AdminFeatureFlagScreen
import com.gultekinahmetabdullah.trainvoc.features.ui.UserFeatureFlagScreen
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route

NavHost(
    navController = navController,
    startDestination = Route.SPLASH
) {
    // ... existing routes ...

    // Admin Feature Flags
    composable(Route.FEATURE_FLAGS_ADMIN) {
        AdminFeatureFlagScreen(
            onBack = { navController.navigateUp() }
        )
    }

    // User Feature Preferences
    composable(Route.FEATURE_FLAGS_USER) {
        UserFeatureFlagScreen(
            onBack = { navController.navigateUp() }
        )
    }
}
```

### Step 2: Add Navigation Buttons

#### Option A: Add to Settings Screen

In your Settings screen, add buttons to navigate to these screens:

```kotlin
// In SettingsScreen.kt or similar
Button(
    onClick = { navController.navigate(Route.FEATURE_FLAGS_USER) },
    modifier = Modifier.fillMaxWidth()
) {
    Icon(Icons.Default.Tune, "Feature Preferences")
    Spacer(modifier = Modifier.width(8.dp))
    Text("Feature Preferences")
}

// Admin only (hide for regular users)
if (BuildConfig.DEBUG || isAdmin) {
    Button(
        onClick = { navController.navigate(Route.FEATURE_FLAGS_ADMIN) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.AdminPanelSettings, "Admin Dashboard")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Admin: Feature Flags")
    }
}
```

#### Option B: Add to Main Menu/Drawer

If you have a navigation drawer or main menu:

```kotlin
NavigationDrawerItem(
    icon = { Icon(Icons.Default.Tune, contentDescription = null) },
    label = { Text("Feature Preferences") },
    selected = false,
    onClick = {
        navController.navigate(Route.FEATURE_FLAGS_USER)
        scope.launch { drawerState.close() }
    }
)

// Admin menu item (conditional)
if (BuildConfig.DEBUG || isAdmin) {
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
        label = { Text("Admin Dashboard") },
        selected = false,
        onClick = {
            navController.navigate(Route.FEATURE_FLAGS_ADMIN)
            scope.launch { drawerState.close() }
        }
    )
}
```

---

## 🎨 UI Screenshots (Text Descriptions)

### Admin Dashboard

```
┌─────────────────────────────────────┐
│ ← Feature Flags (Admin)     ⟳  ⋮   │
├─────────────────────────────────────┤
│ ┌─ Cost Summary ───────────────┐   │
│ │ Today          This Month     │   │
│ │ $0.45          $123.50        │   │
│ └───────────────────────────────┘   │
│                                     │
│ [All] [Core] [Multimedia] [Content]│
│                                     │
│ ┌─ Audio Pronunciation ────────┐   │
│ │ Hear native speaker           │   │
│ │ [Premium] [💰 Cost]      [ON] │   │
│ │ Rollout: 100% | Usage: 450/∞  │   │
│ │              [More ▼]         │   │
│ └───────────────────────────────┘   │
│                                     │
│ ┌─ Speech Recognition ─────────┐   │
│ │ Practice speaking             │   │
│ │ [Premium] [💰 Cost]      [OFF]│   │
│ └───────────────────────────────┘   │
└─────────────────────────────────────┘
```

### User Preferences

```
┌─────────────────────────────────────┐
│ ← Feature Preferences            ⋮  │
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ ℹ️ Control which features you   │ │
│ │   want to use. Save battery!    │ │
│ └─────────────────────────────────┘ │
│                                     │
│ [All] [Core] [Multimedia] [Content]│
│                                     │
│ Core Learning                       │
│ ┌─ Adaptive Difficulty ────────┐   │
│ │ AI adjusts quiz difficulty    │   │
│ │ [Premium]              [ON]   │   │
│ │ Used 125 times                │   │
│ └───────────────────────────────┘   │
│                                     │
│ Multimedia                          │
│ ┌─ Audio Pronunciation ────────┐   │
│ │ Hear native pronunciation 💰  │   │
│ │ [Uses Data]            [ON]   │   │
│ │ Used 450 times                │   │
│ └───────────────────────────────┘   │
└─────────────────────────────────────┘
```

---

## 🧪 Testing the UI

### Test Admin Dashboard

1. Navigate to `Route.FEATURE_FLAGS_ADMIN`
2. Verify cost summary displays correctly
3. Test category filters
4. Toggle features on/off
5. Expand a feature and test:
   - Rollout percentage slider
   - Daily limit buttons
6. Test quick actions menu:
   - Enable All Features
   - Disable Expensive Features
   - Reset Daily Usage

### Test User Preferences

1. Navigate to `Route.FEATURE_FLAGS_USER`
2. Verify info card displays
3. Test category filters
4. Toggle features on/off
5. Verify only user-configurable features show (no admin-only)
6. Check usage statistics display

---

## 🔐 Admin Access Control

### Recommended: Hide Admin Dashboard for Non-Admins

```kotlin
// In your settings or wherever you link to admin dashboard
val isAdmin = BuildConfig.DEBUG ||
              userRepository.isUserAdmin() ||
              preferencesRepository.getUsername() == "admin"

if (isAdmin) {
    // Show admin dashboard link
}
```

### Alternative: Simple Debug-Only Access

```kotlin
if (BuildConfig.DEBUG) {
    // Show admin dashboard in debug builds only
}
```

---

## 📊 Usage Analytics

Both screens automatically integrate with the FeatureFlagViewModel which:

- ✅ Tracks all toggle changes
- ✅ Monitors costs in real-time
- ✅ Logs usage for analytics
- ✅ Provides error handling

No additional setup needed!

---

## 🎨 Customization

### Change Color Schemes

The screens use Material 3 theming. To customize:

```kotlin
// In your theme file
CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer
    // Change to your preferred color
)
```

### Add Custom Actions

In `AdminFeatureFlagScreen`, add to the menu:

```kotlin
DropdownMenuItem(
    text = { Text("Your Custom Action") },
    onClick = {
        // Your logic
        showMenu = false
    }
)
```

---

## 🐛 Troubleshooting

### "Unresolved reference: hiltViewModel"

Add to your `build.gradle.kts`:
```kotlin
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
```

### Features not showing

Make sure `FeatureFlagViewModel` is injected with Hilt:
```kotlin
@HiltViewModel
class FeatureFlagViewModel @Inject constructor(...)
```

### Costs showing as $0.00

Features need to be used first. Track usage with:
```kotlin
featureFlags.trackUsage(
    FeatureFlag.AUDIO_PRONUNCIATION,
    apiCalls = 1,
    estimatedCost = 0.001
)
```

---

## ✅ Complete Integration Checklist

- [ ] Routes added to navigation graph
- [ ] Links added to Settings screen
- [ ] Admin access control implemented
- [ ] Tested on device/emulator
- [ ] Error handling verified
- [ ] Cost tracking working
- [ ] Category filters working
- [ ] All toggles functional

---

## 🚀 Next Steps

1. **Integrate into your app** - Add navigation as shown above
2. **Test thoroughly** - Verify all features work
3. **Add admin authentication** - Secure admin dashboard
4. **Implement Phase 1 features** - Now you have the UI to control them!

**The feature flag system is now complete with full UI! 🎉**
