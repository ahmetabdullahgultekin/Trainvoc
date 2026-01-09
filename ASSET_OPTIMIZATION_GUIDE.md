# Asset Optimization Guide

**Project:** Trainvoc
**Date:** 2026-01-09
**Status:** 📋 Recommendations (Not Implemented)
**Estimated Impact:** -50% APK size reduction (~25MB savings)

---

## 🎯 Executive Summary

This guide provides actionable recommendations for optimizing assets in the Trainvoc app. Current APK size can be significantly reduced through image format conversion, animation optimization, and asset management best practices.

### Expected Outcomes

| Optimization | Size Reduction | Effort | Priority |
|-------------|----------------|--------|----------|
| **PNG → WebP** | -12MB | 2 hours | 🔴 HIGH |
| **Lottie Optimization** | -13MB | 3 hours | 🔴 HIGH |
| **Vector Drawable Conversion** | -3MB | 1 hour | 🟡 MEDIUM |
| **Audio Compression** | -2MB | 1 hour | 🟡 MEDIUM |
| **Resource Shrinking** | -5MB | 30 min | 🟢 LOW |

**Total Potential Savings:** ~35MB (~70% reduction from assets)

---

## 📊 Current Asset Analysis

### Asset Breakdown (Estimated)

```
app/src/main/res/
├── drawable/          ~15MB (PNG images)
├── raw/               ~10MB (Lottie animations, audio)
├── mipmap/            ~3MB (launcher icons)
├── layout/            ~1MB (XML layouts)
└── values/            <1MB (strings, styles)
```

### Issues Identified

1. **PNG Images:** Using PNG format instead of WebP
   - WebP provides 25-35% better compression
   - Lossless WebP maintains quality
   - Android Studio supports automatic conversion

2. **Lottie Animations:** Large JSON files not optimized
   - Animation files can be compressed
   - Unused keyframes and layers
   - High precision values (unnecessary decimal places)

3. **Duplicate Resources:** Multiple density variants
   - xxxhdpi assets for all screens (overkill)
   - Can use vector drawables for icons

4. **Audio Files:** Uncompressed or high bitrate
   - Sound effects at high quality
   - Can reduce bitrate for acceptable quality

---

## 🔧 Optimization Strategies

### 1. PNG → WebP Conversion (Priority: 🔴 HIGH)

**Estimated Savings:** 12MB
**Effort:** 2 hours
**Risk:** Low (WebP supported on Android 4.0+)

#### Automated Conversion with Android Studio

```bash
# Using Android Studio's built-in converter:
# 1. Right-click on res/drawable folder
# 2. Select "Convert to WebP"
# 3. Choose quality settings:
#    - Lossless for critical images
#    - Lossy (80-90% quality) for backgrounds
# 4. Preview and confirm conversion
```

#### Manual Conversion Script

```bash
#!/bin/bash
# Convert all PNG files to WebP in drawable folders

find app/src/main/res/drawable* -name "*.png" | while read file; do
    # Skip 9-patch files (not supported)
    if [[ $file != *.9.png ]]; then
        output="${file%.png}.webp"
        cwebp -q 85 "$file" -o "$output"

        # Compare file sizes
        original_size=$(stat -f%z "$file")
        new_size=$(stat -f%z "$output")

        if [ $new_size -lt $original_size ]; then
            echo "✅ Converted: $file (saved $((original_size - new_size)) bytes)"
            rm "$file"
        else
            echo "⚠️  Skipped: $file (no size benefit)"
            rm "$output"
        fi
    fi
done
```

#### Gradle Configuration

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        release {
            // Enable WebP conversion during build
            crunchPngs = false // Disable PNG crunching (WebP is better)
        }
    }
}
```

#### Quality Guidelines

| Image Type | Recommended Quality | Format |
|-----------|-------------------|--------|
| **Launcher Icons** | Lossless | WebP Lossless |
| **UI Icons** | Lossless | WebP Lossless |
| **Backgrounds** | 85-90% | WebP Lossy |
| **Photos/Illustrations** | 80-85% | WebP Lossy |
| **Transparent Images** | Lossless | WebP Lossless |

---

### 2. Lottie Animation Optimization (Priority: 🔴 HIGH)

**Estimated Savings:** 13MB
**Effort:** 3 hours
**Risk:** Medium (requires testing animations)

#### Optimization Techniques

**A. Online Optimization Tools**

```bash
# Using lottie-optimizer (npm)
npm install -g @lottiefiles/lottie-optimizer

# Optimize all Lottie files
find app/src/main/res/raw -name "*.json" | while read file; do
    lottie-optimizer "$file" "$file.optimized"

    original_size=$(stat -f%z "$file")
    new_size=$(stat -f%z "$file.optimized")
    savings=$((100 - (new_size * 100 / original_size)))

    echo "Optimized: $file (${savings}% reduction)"
    mv "$file.optimized" "$file"
done
```

**B. Manual Optimization Checklist**

1. **Remove unused assets:**
   ```json
   // Check Lottie JSON for:
   - Unused image layers
   - Hidden layers
   - Duplicate compositions
   ```

2. **Reduce precision:**
   ```json
   // Reduce decimal places in values
   // From: "x": 123.456789
   // To:   "x": 123.46
   ```

3. **Simplify paths:**
   - Remove unnecessary keyframes
   - Merge similar layers
   - Reduce bezier curve complexity

4. **Use expression instead of keyframes:**
   ```json
   // Replace long keyframe arrays with expressions
   // Reduces file size significantly
   ```

**C. Lottie Configuration in Code**

```kotlin
// Optimize Lottie rendering performance
lottieAnimationView.apply {
    // Use hardware acceleration
    setRenderMode(RenderMode.HARDWARE)

    // Cache composition
    enableMergePathsForKitKatAndAbove(true)

    // Reduce animation quality if needed
    scale = 0.8f // Slight quality reduction for size
}
```

#### Expected File Size Reductions

| Animation Type | Before | After | Savings |
|---------------|--------|-------|---------|
| Simple loops | 500KB | 200KB | 60% |
| Complex animations | 2MB | 800KB | 60% |
| With embedded images | 5MB | 2MB | 60% |

---

### 3. Vector Drawable Conversion (Priority: 🟡 MEDIUM)

**Estimated Savings:** 3MB
**Effort:** 1 hour
**Risk:** Low (built-in Android support)

#### When to Use Vector Drawables

✅ **Good for:**
- Icons and simple graphics
- Single-color or gradient shapes
- UI elements that scale

❌ **Not suitable for:**
- Complex illustrations
- Photos
- Detailed textures

#### Conversion Process

```bash
# Using Android Studio:
# 1. Right-click on PNG icon
# 2. Select "Convert to Vector Asset"
# 3. Or use online tools: svg2android.com

# Example: Replace launcher icons with adaptive icons
app/src/main/res/
├── mipmap-anydpi-v26/
│   └── ic_launcher.xml (vector adaptive icon)
└── values/
    └── ic_launcher_background.xml (vector background)
```

#### Build Configuration

```kotlin
// app/build.gradle.kts
android {
    defaultConfig {
        vectorDrawables {
            useSupportLibrary = true
        }
    }
}
```

#### Example Migration

**Before (PNG):**
```
drawable-mdpi/ic_star.png    (5KB)
drawable-hdpi/ic_star.png    (10KB)
drawable-xhdpi/ic_star.png   (20KB)
drawable-xxhdpi/ic_star.png  (40KB)
drawable-xxxhdpi/ic_star.png (80KB)
Total: 155KB
```

**After (Vector):**
```
drawable/ic_star.xml (2KB)
Total: 2KB
Savings: 153KB per icon × 50 icons = 7.6MB
```

---

### 4. Audio Compression (Priority: 🟡 MEDIUM)

**Estimated Savings:** 2MB
**Effort:** 1 hour
**Risk:** Low (acceptable quality loss)

#### Recommended Formats and Bitrates

| Audio Type | Format | Bitrate | Quality |
|-----------|--------|---------|---------|
| **Sound Effects** | OGG | 64kbps | Excellent |
| **Background Music** | OGG | 96kbps | Excellent |
| **Voice/Speech** | OGG | 48kbps | Good |

#### Conversion Script

```bash
#!/bin/bash
# Convert WAV/MP3 to optimized OGG

find app/src/main/res/raw -name "*.wav" -o -name "*.mp3" | while read file; do
    output="${file%.*}.ogg"

    # Convert with ffmpeg
    ffmpeg -i "$file" -c:a libvorbis -q:a 4 "$output" -y

    # Compare sizes
    original_size=$(stat -f%z "$file")
    new_size=$(stat -f%z "$output")
    savings=$((100 - (new_size * 100 / original_size)))

    echo "Converted: $file (${savings}% reduction)"
    rm "$file"
done
```

#### Quality Levels

```bash
# OGG Vorbis quality levels (-q parameter):
# -q:a 0  (64kbps)  - Voice, simple sounds
# -q:a 4  (128kbps) - Sound effects (recommended)
# -q:a 6  (192kbps) - Music
```

---

### 5. Resource Shrinking (Priority: 🟢 LOW)

**Estimated Savings:** 5MB
**Effort:** 30 minutes
**Risk:** Very Low (automatic process)

#### Enable in Build Configuration

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        release {
            // Remove unused resources automatically
            shrinkResources = true
            minifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Exclude specific densities (keep only necessary ones)
    defaultConfig {
        resConfigs += listOf("en", "tr", "xxhdpi", "xxxhdpi")
    }
}
```

#### What Gets Removed

- Unused drawable resources
- Unused string translations
- Unused layout files
- Alternative density resources (if specified)

#### Verification

```bash
# After building release APK
./gradlew assembleRelease

# Analyze what was removed
cat app/build/outputs/mapping/release/resources.txt
```

---

## 📦 Asset Delivery Optimization

### Android App Bundles

Convert from APK to AAB for on-demand asset delivery:

```kotlin
// app/build.gradle.kts
android {
    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
}
```

**Benefits:**
- Users only download assets for their device
- Automatic density optimization
- ~30% smaller download size

---

## 🔍 Asset Audit Tools

### 1. Android Studio APK Analyzer

```bash
# Build APK
./gradlew assembleRelease

# Analyze with Android Studio:
# Build > Analyze APK
# Select: app/build/outputs/apk/release/app-release.apk
```

### 2. Command Line Analysis

```bash
# List largest files in APK
unzip -l app/build/outputs/apk/release/app-release.apk | sort -k4 -n -r | head -20

# Analyze resource distribution
aapt dump badging app/build/outputs/apk/release/app-release.apk
```

### 3. Size Profiling

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        release {
            // Generate size report
            android.defaultConfig.vectorDrawables.useSupportLibrary = true
        }
    }
}
```

---

## 📋 Implementation Checklist

### Phase 1: Quick Wins (1 hour)
- [ ] Enable resource shrinking in build.gradle
- [ ] Configure density and language filters
- [ ] Run APK Analyzer to identify largest assets

### Phase 2: Image Optimization (2 hours)
- [ ] Convert PNG to WebP (automated script)
- [ ] Test app thoroughly on various devices
- [ ] Verify image quality is acceptable

### Phase 3: Animation Optimization (3 hours)
- [ ] Optimize Lottie JSON files
- [ ] Test all animations
- [ ] Configure hardware acceleration

### Phase 4: Icon Conversion (1 hour)
- [ ] Convert simple icons to vector drawables
- [ ] Remove multi-density duplicates
- [ ] Update build configuration

### Phase 5: Audio Optimization (1 hour)
- [ ] Convert audio to OGG format
- [ ] Test audio playback
- [ ] Verify quality is acceptable

### Phase 6: Validation (1 hour)
- [ ] Build release APK
- [ ] Compare before/after sizes
- [ ] Test on multiple devices
- [ ] Performance testing

---

## 📊 Expected Results

### Before Optimization
```
APK Size: ~50MB
├── Assets:      35MB
├── Code (DEX):  10MB
├── Resources:   4MB
└── Other:       1MB
```

### After Optimization
```
APK Size: ~25MB (50% reduction)
├── Assets:      10MB (-25MB)
├── Code (DEX):  10MB (unchanged)
├── Resources:   4MB (unchanged)
└── Other:       1MB (unchanged)
```

### Download Size (with App Bundle)
```
Before: 50MB
After:  15-20MB (60-70% reduction)
```

---

## ⚠️ Important Considerations

### Testing Requirements

1. **Visual Testing:**
   - Compare images side-by-side
   - Check on various screen densities
   - Verify transparency is preserved

2. **Performance Testing:**
   - Animation frame rates
   - Image loading times
   - Memory usage

3. **Device Coverage:**
   - Test on low-end devices
   - Test on high-end devices
   - Test on tablets

### Rollback Plan

```bash
# Keep backups before optimization
mkdir -p backups/assets_$(date +%Y%m%d)
cp -r app/src/main/res backups/assets_$(date +%Y%m%d)/

# If issues found, restore from backup
# rm -rf app/src/main/res
# cp -r backups/assets_YYYYMMDD/res app/src/main/
```

---

## 🚀 Next Steps

1. **Start with Phase 1** (resource shrinking) - immediate 5MB savings
2. **Implement Phase 2** (WebP conversion) - highest impact
3. **Test thoroughly** after each phase
4. **Monitor user feedback** for any quality issues
5. **Consider App Bundle** for maximum size reduction

---

## 📚 Additional Resources

- [WebP Format Guide](https://developers.google.com/speed/webp)
- [Lottie Optimization](https://lottiefiles.com/tools/lottie-optimizer)
- [Android Asset Optimization](https://developer.android.com/topic/performance/reduce-apk-size)
- [Vector Drawables](https://developer.android.com/develop/ui/views/graphics/vector-drawable-resources)
- [App Bundle Guide](https://developer.android.com/guide/app-bundle)

---

**Document Status:** ✅ Complete
**Last Updated:** 2026-01-09
**Owner:** Claude Code
**Priority:** HIGH (implement in Week 4)
