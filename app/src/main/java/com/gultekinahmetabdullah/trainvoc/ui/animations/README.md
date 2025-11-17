# Trainvoc Animation System

Comprehensive animation system for Trainvoc app following Material Design 3 motion guidelines.

## 📁 File Structure

```
ui/animations/
├── AnimationSpecs.kt              # Core animation specifications
├── HapticFeedback.kt              # Haptic feedback utilities
├── ShimmerEffect.kt               # Loading shimmer effects
├── AnimatedComponents.kt          # Reusable animated components
├── SuccessErrorAnimations.kt      # Success/error feedback animations
├── AnimatedProgressIndicators.kt  # Progress indicators
└── README.md                      # This file
```

## 🎯 Animation Specifications

### Duration Constants
```kotlin
AnimationSpecs.DURATION_VERY_SHORT  // 100ms - Micro-interactions
AnimationSpecs.DURATION_SHORT       // 200ms - Quick transitions
AnimationSpecs.DURATION_MEDIUM      // 300ms - Standard transitions
AnimationSpecs.DURATION_LONG        // 400ms - Emphasized transitions
AnimationSpecs.DURATION_EXTRA_LONG  // 500ms - Complex transitions
```

### Easing Functions
```kotlin
AnimationSpecs.EasingStandard      // Default for most animations
AnimationSpecs.EasingEmphasized    // For important state changes
AnimationSpecs.EasingDecelerate    // For entering elements
AnimationSpecs.EasingAccelerate    // For exiting elements
```

### Spring Specifications
```kotlin
AnimationSpecs.SpringLowStiffness     // Smooth, slow bounce
AnimationSpecs.SpringMediumStiffness  // Standard bounce
AnimationSpecs.SpringHighStiffness    // Quick, snappy bounce
AnimationSpecs.SpringNoBounce         // Smooth without bounce
```

## 🎨 Animated Components

### Press Clickable Button
Adds press scale animation to any composable:

```kotlin
Box(
    modifier = Modifier.pressClickable {
        // Handle click
    }
)
```

### Bounce In
Animate appearance with bounce:

```kotlin
Box(modifier = Modifier.bounceIn()) {
    // Content
}
```

### Shake Animation
Shake element horizontally (great for errors):

```kotlin
var triggerShake by remember { mutableStateOf(false) }

Box(modifier = Modifier.shake(triggerShake)) {
    // Content that shakes
}

// Trigger shake
triggerShake = true
```

### Pulse Animation
Gentle pulsing effect:

```kotlin
Icon(
    modifier = Modifier.pulse(enabled = true),
    // ...
)
```

### Card Flip
Flip between front and back content:

```kotlin
var isFlipped by remember { mutableStateOf(false) }

FlippableCard(
    isFlipped = isFlipped,
    front = { Text("Front") },
    back = { Text("Back") }
)
```

### Staggered List Items
Animate list items with delay:

```kotlin
LazyColumn {
    itemsIndexed(items) { index, item ->
        StaggeredListItem(index = index) {
            // Item content
        }
    }
}
```

## 🔊 Haptic Feedback

### Basic Usage
```kotlin
val haptic = rememberHapticPerformer()

Button(
    onClick = {
        haptic.click()  // Provide tactile feedback
        // Handle click
    }
) {
    Text("Click Me")
}
```

### Feedback Types
```kotlin
haptic.click()       // Quick tap feedback
haptic.longPress()   // Sustained press feedback
haptic.success()     // Successful action
haptic.error()       // Error/invalid action
```

## ✨ Shimmer Effects

### Apply Shimmer to Any Component
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .shimmerEffect(isVisible = isLoading)
)
```

### Pre-built Skeleton Components
```kotlin
// Word card skeleton
WordCardSkeleton()

// List item skeleton
ListItemSkeleton(showAvatar = true)

// Stats card skeleton
StatsCardSkeleton()

// Generic text skeleton
TextSkeleton(width = 150.dp, height = 20.dp)

// Full loading screen
ShimmerLoadingScreen(itemCount = 5)
```

## ✅ Success/Error Animations

### Animated Checkmark
```kotlin
var showSuccess by remember { mutableStateOf(false) }

AnimatedCheckmark(
    isVisible = showSuccess,
    color = MaterialTheme.colorScheme.primary
)
```

### Animated Error Cross
```kotlin
var showError by remember { mutableStateOf(false) }

AnimatedErrorCross(
    isVisible = showError,
    color = MaterialTheme.colorScheme.error
)
```

### Confetti Celebration
```kotlin
var triggerConfetti by remember { mutableStateOf(false) }

ConfettiAnimation(
    trigger = triggerConfetti,
    particleCount = 50
)
```

### Complete Success Celebration
```kotlin
var celebrateSuccess by remember { mutableStateOf(false) }

SuccessCelebration(
    isVisible = celebrateSuccess,
    showConfetti = true,
    onComplete = {
        // Called when animation finishes
    }
)
```

### Complete Error Indication
```kotlin
var showError by remember { mutableStateOf(false) }

ErrorIndication(
    isVisible = showError,
    onComplete = {
        // Called when animation finishes
    }
)
```

## 📊 Progress Indicators

### Circular Progress with Percentage
```kotlin
AnimatedCircularProgress(
    progress = 0.75f,  // 0f to 1f
    size = 120.dp,
    strokeWidth = 12.dp,
    showPercentage = true
)
```

### Learning Progress Ring
```kotlin
LearningProgressRing(
    learned = 45,
    total = 100,
    size = 100.dp
)
```

### Linear Progress Bar
```kotlin
AnimatedLinearProgress(
    progress = 0.6f,
    height = 8.dp
)
```

### Segmented Progress Bar
```kotlin
SegmentedProgressBar(
    current = 3,
    total = 5,
    height = 8.dp,
    spacing = 4.dp
)
```

### Quiz Progress Indicator
```kotlin
QuizProgressIndicator(
    currentQuestion = 5,
    totalQuestions = 10
)
```

### Streak Progress Indicator
```kotlin
StreakProgressIndicator(
    currentStreak = 12,
    nextMilestone = 30
)
```

### Level Progress Bar
```kotlin
LevelProgressBar(
    currentXP = 450,
    requiredXP = 1000,
    level = 5
)
```

### Loading Pulse
```kotlin
LoadingPulse(
    size = 40.dp,
    color = MaterialTheme.colorScheme.primary
)
```

## 🎬 Screen Transitions

### Navigation Transitions
```kotlin
// Forward navigation (slide right to left)
enterTransition = AnimationSpecs.slideInFromRight()
exitTransition = AnimationSpecs.slideOutToLeft()

// Backward navigation (slide left to right)
popEnterTransition = AnimationSpecs.slideInFromLeft()
popExitTransition = AnimationSpecs.slideOutToRight()

// Fade transitions
enterTransition = AnimationSpecs.fadeIn()
exitTransition = AnimationSpecs.fadeOut()

// Scale transitions
enterTransition = AnimationSpecs.scaleInFadeIn()
exitTransition = AnimationSpecs.scaleOutFadeOut()
```

## 💡 Usage Examples

### Quiz Answer Feedback
```kotlin
var showSuccess by remember { mutableStateOf(false) }
var showError by remember { mutableStateOf(false) }

// When user answers correctly
SuccessCelebration(
    isVisible = showSuccess,
    showConfetti = true,
    onComplete = { /* Move to next question */ }
)

// When user answers incorrectly
ErrorIndication(
    isVisible = showError,
    onComplete = { /* Show correct answer */ }
)
```

### Button with Haptic Feedback
```kotlin
val haptic = rememberHapticPerformer()

Button(
    onClick = {
        haptic.click()
        onButtonClick()
    },
    modifier = Modifier.pressClickable { }
) {
    Text("Submit")
}
```

### Loading State
```kotlin
if (isLoading) {
    ShimmerLoadingScreen(itemCount = 5)
} else {
    // Actual content
    LazyColumn {
        itemsIndexed(items) { index, item ->
            StaggeredListItem(index = index) {
                ItemCard(item)
            }
        }
    }
}
```

### Streak Achievement
```kotlin
var showConfetti by remember { mutableStateOf(false) }

LaunchedEffect(streakMilestone) {
    if (streakMilestone) {
        showConfetti = true
        delay(3000)
        showConfetti = false
    }
}

Box(modifier = Modifier.fillMaxSize()) {
    StreakProgressIndicator(
        currentStreak = currentStreak,
        nextMilestone = 30
    )

    ConfettiAnimation(trigger = showConfetti)
}
```

## 🎯 Best Practices

1. **Consistent Duration**: Use AnimationSpecs duration constants for consistency
2. **Appropriate Easing**: Match easing to animation purpose (enter/exit/emphasis)
3. **Haptic Feedback**: Add haptic feedback to all buttons and important actions
4. **Loading States**: Use shimmer effects for loading states
5. **Success Feedback**: Always provide visual feedback for successful actions
6. **Error Indication**: Use shake + error cross for validation errors
7. **Celebrations**: Use confetti sparingly for major achievements only
8. **Staggered Animations**: Use staggered delays for list animations (50ms per item)
9. **Performance**: Avoid complex animations on low-end devices
10. **Accessibility**: Ensure animations respect user's motion preferences

## 🚀 Performance Tips

- Use `remember` to cache animation instances
- Avoid creating new animatables in recomposition
- Use `LaunchedEffect` for animation triggers
- Keep animation durations reasonable (< 500ms)
- Test on low-end devices
- Consider reducing animation complexity in power-saving mode

## 📱 Platform Considerations

- Haptic feedback requires `android.permission.VIBRATE`
- Check `Settings.System.HAPTIC_FEEDBACK_ENABLED` for user preference
- Test animations on different screen sizes
- Ensure animations work on both phones and tablets
- Respect system animation scale settings

---

For questions or improvements, refer to Material Design 3 motion guidelines:
https://m3.material.io/styles/motion/overview
