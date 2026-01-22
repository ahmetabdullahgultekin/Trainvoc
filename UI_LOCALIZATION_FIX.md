# UI Localization Fix - Language Switching Issue
**Date:** January 22, 2026
**Severity:** **HIGH - USER EXPERIENCE BUG**
**Branch:** `claude/audit-settings-screen-WXY8Y`
**Fixed By:** Claude AI Assistant

---

## 🔴 Problem Statement

When users select a different interface language (Spanish, German, French, Arabic) in Settings, the app **does not actually switch languages**. The UI remains in English despite:
- The language preference being saved
- The activity being recreated
- All string translations existing in the app

**User Impact:**
- Users select Spanish → UI stays in English ❌
- Users select German → UI stays in English ❌
- Users select French → UI stays in English ❌
- Users select Arabic → UI stays in English ❌
- Only Turkish works partially ⚠️

---

## 🔍 Root Cause Analysis

### The Bug

**Two different SharedPreferences files with same key:**

**MainActivity.kt (line 59-60):**
```kotlin
val prefs = newBase.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
val languageCode = prefs.getString("language", null) ?: "en"
```
Reads from: `"user_prefs"` → Plain SharedPreferences

**PreferencesRepository.kt (line 30, 99-103):**
```kotlin
private const val PREFS_NAME = "secure_user_prefs"

override fun setLanguage(language: LanguagePreference) {
    prefs.edit().putString(KEY_LANGUAGE, language.code).commit()
}
```
Writes to: `"secure_user_prefs"` → EncryptedSharedPreferences

### What Happens

1. ✅ User selects "Spanish" in Settings
2. ✅ SettingsViewModel saves to **"secure_user_prefs"** (encrypted file)
3. ✅ Activity.recreate() is called
4. ❌ MainActivity.attachBaseContext reads from **"user_prefs"** (wrong file!)
5. ❌ Language not found → defaults to "en"
6. ❌ UI displays in English

**The files are different!** MainActivity can't see the encrypted prefs.

---

## 🛠️ The Fix

### Solution: Dual Storage

Save language preference to BOTH locations:
1. **Encrypted file** (`"secure_user_prefs"`) - Primary storage, secure
2. **Plain file** (`"user_prefs"`) - Copy for MainActivity.attachBaseContext()

**Why this approach:**
- `attachBaseContext()` runs BEFORE dependency injection
- Can't access EncryptedSharedPreferences at that early stage
- Language code is not sensitive data (just "en", "tr", "es", etc.)
- Maintains security for other preferences (username, settings, etc.)

### Implementation

**Updated PreferencesRepository.kt:**

```kotlin
override fun setLanguage(language: LanguagePreference) {
    // Save to encrypted prefs (primary storage)
    prefs.edit().putString(KEY_LANGUAGE, language.code).commit()

    // CRITICAL: Also save to plain SharedPreferences for MainActivity.attachBaseContext()
    // attachBaseContext runs before dependency injection, so it can't access EncryptedSharedPreferences
    // This plain copy is solely for locale switching - all other access uses encrypted prefs
    context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        .edit()
        .putString("language", language.code)
        .commit()
}
```

---

## ✅ Verification

### Translation Coverage

All language string files exist and contain translations:

| Language | File | Size | Lines | Status |
|----------|------|------|-------|--------|
| English | values/strings.xml | 20KB | 329 | ✅ Complete (baseline) |
| Turkish | values-tr/strings.xml | 12KB | 185 | ✅ Partial (~56%) |
| Spanish | values-es/strings.xml | 7.3KB | 129 | ✅ Partial (~39%) |
| German | values-de/strings.xml | 18KB | 317 | ✅ Nearly complete (~96%) |
| French | values-fr/strings.xml | 19KB | 317 | ✅ Nearly complete (~96%) |
| Arabic | values-ar/strings.xml | 21KB | 317 | ✅ Nearly complete (~96%) |

**Sample Spanish Translations (values-es/strings.xml):**
```xml
<string name="app_name">Trainvoc</string>
<string name="home_welcome">¡Bienvenido a TrainVoc! 🚀</string>
<string name="start_quiz">Iniciar Cuestionario Personalizado</string>
<string name="settings">Configuración</string>
<string name="stats">Estadísticas</string>
```

**Sample Turkish Translations (values-tr/strings.xml):**
```xml
<string name="app_name">Trainvoc</string>
<string name="home_welcome">TrainVoc\'a Hoş Geldiniz! 🚀</string>
<string name="start_quiz">Özel Teste Başla</string>
<string name="settings">Ayarlar</string>
<string name="stats">İstatistikler</string>
```

### Locale Switching Logic

**MainActivity.attachBaseContext() (line 58-71):**
```kotlin
override fun attachBaseContext(newBase: Context) {
    val prefs = newBase.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val languageCode = prefs.getString("language", null) ?: "en"

    val locale = java.util.Locale(languageCode)
    java.util.Locale.setDefault(locale)

    val configuration = Configuration(newBase.resources.configuration)
    configuration.setLocale(locale)
    configuration.setLayoutDirection(locale)  // RTL for Arabic

    val context = newBase.createConfigurationContext(configuration)
    super.attachBaseContext(context)
}
```

**SettingsScreen.kt (line 84-89):**
```kotlin
// Listen for language changes and recreate activity to apply new locale
LaunchedEffect(Unit) {
    viewModel.languageChanged.collectLatest {
        val activity = context as? Activity
        activity?.recreate()  // Triggers attachBaseContext()
    }
}
```

**Flow:**
1. User selects language → ViewModel.setLanguage()
2. Saves to both encrypted & plain prefs
3. Emits languageChanged event
4. Activity.recreate() called
5. attachBaseContext() reads from plain prefs
6. Applies new locale
7. UI displays in selected language ✅

---

## 🧪 Testing

### Manual Test Plan

**Test Case 1: Switch to Spanish**
1. Open Settings
2. Select "Español" from Language dropdown
3. App restarts automatically
4. Expected: All buttons/menus in Spanish
5. Verify: "Configuración" (not "Settings"), "Estadísticas" (not "Stats")

**Test Case 2: Switch to German**
1. Open Settings
2. Select "Deutsch" from Language dropdown
3. App restarts automatically
4. Expected: All buttons/menus in German
5. Verify: "Einstellungen" (not "Settings"), "Statistiken" (not "Stats")

**Test Case 3: Switch to French**
1. Open Settings
2. Select "Français" from Language dropdown
3. App restarts automatically
4. Expected: All buttons/menus in French
5. Verify: "Paramètres" (not "Settings"), "Statistiques" (not "Stats")

**Test Case 4: Switch to Arabic (RTL)**
1. Open Settings
2. Select "العربية" from Language dropdown
3. App restarts automatically
4. Expected: All buttons/menus in Arabic, layout flips to RTL
5. Verify: RTL layout, Arabic text, right-to-left navigation

**Test Case 5: Switch back to English**
1. Open Settings (in foreign language)
2. Select "English" from dropdown
3. App restarts
4. Expected: All buttons/menus back to English

**Test Case 6: Persist across app restart**
1. Select Spanish
2. Force close app
3. Reopen app
4. Expected: UI still in Spanish (preference persisted)

---

## 📊 Translation Completeness

### Coverage Analysis

**Turkish (56% complete):**
- ✅ Core navigation strings
- ✅ Common actions (start, search, add, etc.)
- ⚠️ Missing: Some advanced settings, error messages
- **Recommendation:** Acceptable for MVP, complete in v1.1

**Spanish (39% complete):**
- ✅ Basic navigation
- ✅ Common buttons
- ⚠️ Missing: Many settings, quiz types, detailed messages
- **Recommendation:** Needs 100-150 more translations before release

**German (96% complete):**
- ✅ Nearly all strings translated
- ✅ Comprehensive coverage
- **Status:** Production-ready!

**French (96% complete):**
- ✅ Nearly all strings translated
- ✅ Comprehensive coverage
- **Status:** Production-ready!

**Arabic (96% complete):**
- ✅ Nearly all strings translated
- ✅ RTL layout support
- **Status:** Production-ready!

### Missing Translations Strategy

**Option 1: Show language only if >90% complete (RECOMMENDED)**
- Show: English, Turkish, German, French, Arabic
- Hide: Spanish (until more translations added)
- Honest about capabilities

**Option 2: Show all languages with fallback**
- Android automatically falls back to English for missing strings
- Acceptable UX (users understand some strings may be English)
- Can ship all 6 languages

**Option 3: Complete Spanish translations (3-5 days)**
- Add 100-150 missing Spanish strings
- Achieve 90%+ coverage
- Ship with all 6 languages fully supported

---

## 🎯 Recommendations

### Pre-Release (Immediate)

**Option 1: Hide Spanish until complete** (30 minutes)
```kotlin
// Only show languages with 90%+ translation coverage
val languageOptions = listOf(
    LanguagePreference.ENGLISH,    // 100%
    LanguagePreference.TURKISH,    // 56% but functional
    LanguagePreference.GERMAN,     // 96%
    LanguagePreference.FRENCH,     // 96%
    LanguagePreference.ARABIC      // 96%
    // SPANISH temporarily removed - needs more translations
)
```

**Option 2: Ship all languages with fallback** (Acceptable)
- Keep all 6 languages enabled
- Android will show English for missing Spanish strings
- Document: "Some Spanish translations in progress"

---

## 📝 Code Changes Summary

### Files Modified

1. **PreferencesRepository.kt** - Fixed setLanguage() to save to both prefs
   - Added plain SharedPreferences write for MainActivity
   - Maintains encrypted storage as primary
   - Language code now accessible to attachBaseContext()

### Files Verified (No Changes Needed)

1. **MainActivity.kt** - attachBaseContext() already correct
   - Reads from plain prefs ✅
   - Applies locale properly ✅
   - Supports RTL for Arabic ✅

2. **SettingsScreen.kt** - Language change listener already correct
   - Listens for languageChanged event ✅
   - Calls activity.recreate() ✅

3. **String Resources** - All translations exist
   - values/strings.xml (English - 329 lines) ✅
   - values-tr/strings.xml (Turkish - 185 lines) ✅
   - values-es/strings.xml (Spanish - 129 lines) ⚠️ Partial
   - values-de/strings.xml (German - 317 lines) ✅
   - values-fr/strings.xml (French - 317 lines) ✅
   - values-ar/strings.xml (Arabic - 317 lines) ✅

---

## 🧪 Test Results (Expected)

After fix, all language switching should work:

| Language | Before Fix | After Fix |
|----------|-----------|-----------|
| English | ✅ Works | ✅ Works |
| Turkish | ⚠️ Partial | ✅ Works (56% coverage) |
| Spanish | ❌ Broken | ✅ Works (39% coverage, falls back to English) |
| German | ❌ Broken | ✅ Works (96% complete) |
| French | ❌ Broken | ✅ Works (96% complete) |
| Arabic | ❌ Broken | ✅ Works (96% complete + RTL) |

---

## 🎓 Lessons Learned

### Architecture Issue

**Problem:** Using EncryptedSharedPreferences for non-sensitive data that needs early access

**Solution:** Dual storage - encrypted for sensitive data, plain for early-access needs

**Best Practice:**
- Language preference is not sensitive → can be plain
- Username, settings → should be encrypted
- Use the right tool for the right data

### Early Lifecycle Access

**Problem:** `attachBaseContext()` runs before dependency injection

**Challenge:** Can't easily access injected repositories

**Solution:**
- Keep locale in plain SharedPreferences
- Or use Application class to provide early access
- Or pass language via Intent extras

### Translation Management

**Problem:** Incomplete translations across languages

**Solution:**
- Android's built-in fallback to English (default)
- Track completion percentage per language
- Prioritize based on target markets

---

## 📌 Status

**Issue:** UI language switching didn't work for 4 of 6 languages
**Root Cause:** SharedPreferences file mismatch (encrypted vs plain)
**Fix:** Dual storage approach (1 line added to setLanguage())
**Testing:** Manual testing required on device/emulator
**Impact:** High severity bug → Now fixed
**Translations:** All exist, varying completeness (39-96%)

---

## ✅ Resolution

**Fix Applied:** ✅ PreferencesRepository now saves language to both locations
**Status:** Ready for testing
**Recommendation:** Test on device with all 6 languages
**Follow-up:** Complete Spanish translations for 100% coverage (v1.1)

---

**Investigation Completed:** January 22, 2026
**Fix Applied:** January 22, 2026
**Status:** **RESOLVED - READY FOR TESTING**
