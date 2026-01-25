# Trainvoc Store Listing Assets

This folder contains all assets required for Google Play Store submission.

## Folder Structure

```
store-listing/
├── README.md                    # This file
├── graphics/
│   ├── feature-graphic.png      # 1024x500 px (REQUIRED)
│   ├── promo-graphic.png        # 180x120 px (optional)
│   └── tv-banner.png            # 1280x720 px (optional, for TV)
├── screenshots/
│   ├── phone/                   # 2-8 screenshots (REQUIRED)
│   │   ├── 01-home.png
│   │   ├── 02-vocabulary.png
│   │   ├── 03-quiz.png
│   │   ├── 04-memory-game.png
│   │   ├── 05-achievements.png
│   │   ├── 06-statistics.png
│   │   ├── 07-daily-goals.png
│   │   └── 08-settings.png
│   ├── tablet-7/                # Optional (7" tablet)
│   └── tablet-10/               # Optional (10" tablet)
└── store-listing.md             # Copy for Play Console
```

## Screenshot Requirements

| Type | Dimensions | Min | Max | Format |
|------|-----------|-----|-----|--------|
| Phone | 16:9 or 9:16 | 320px | 3840px | PNG/JPG |
| Tablet 7" | 16:9 or 9:16 | 320px | 3840px | PNG/JPG |
| Tablet 10" | 16:9 or 9:16 | 320px | 3840px | PNG/JPG |
| Feature Graphic | 1024x500 | - | - | PNG/JPG |

## Recommended Screenshots

1. **Home Screen** - Show the main games menu
2. **Vocabulary List** - Word collection with categories
3. **Quiz Mode** - Multiple choice question in action
4. **Memory Game** - Card matching gameplay
5. **Achievements** - Gamification badges/trophies
6. **Statistics** - Learning progress charts
7. **Daily Goals** - Streak and goal tracking
8. **Settings** - Customization options

## Capturing Screenshots

### Using Android Studio
```bash
# Connect device/emulator
adb devices

# Capture screenshot
adb exec-out screencap -p > screenshot.png
```

### Using Emulator
1. Run app in emulator
2. Click camera icon in emulator toolbar
3. Save to this folder

### Recommended Device Profiles
- **Phone:** Pixel 6 Pro (1440x3120)
- **Tablet 7":** Nexus 7 (1200x1920)
- **Tablet 10":** Pixel Tablet (2560x1600)

## Feature Graphic Guidelines

- **Size:** 1024 x 500 pixels exactly
- **Content:**
  - App logo/icon
  - App name "Trainvoc"
  - Tagline: "Learn vocabulary through play"
  - Brand colors (#6366F1 indigo)
- **Do NOT include:**
  - Device frames
  - "New" or "Sale" badges
  - Time-sensitive text

## Store Listing Text

See `store-listing.md` for ready-to-copy text content.
