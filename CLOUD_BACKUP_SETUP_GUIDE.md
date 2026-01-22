# Cloud Backup Setup Guide
**Trainvoc Android App**
**Feature:** Google Drive Backup & Sync

---

## Overview

The Trainvoc app includes a comprehensive cloud backup system that allows users to:
- Backup vocabulary data to Google Drive
- Restore data across devices
- Enable automatic daily backups
- Manage backup history
- Encrypted data storage

**Current Status:** ✅ Technically Complete - Needs OAuth Configuration

---

## For Developers: OAuth 2.0 Setup

### Prerequisites
- Google Cloud Console access
- Android app SHA-1 fingerprint
- Package name: `com.gultekinahmetabdullah.trainvoc`

### Step 1: Google Cloud Console Configuration

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing: "Trainvoc"
3. Enable Google Drive API:
   - Navigate to **APIs & Services** → **Library**
   - Search for "Google Drive API"
   - Click **Enable**

### Step 2: Create OAuth 2.0 Credentials

1. Go to **APIs & Services** → **Credentials**
2. Click **+ CREATE CREDENTIALS** → **OAuth client ID**
3. Select **Android** as application type
4. Configure:
   ```
   Name: Trainvoc Android App
   Package name: com.gultekinahmetabdullah.trainvoc
   SHA-1 certificate fingerprint: [YOUR_SHA1_HERE]
   ```

#### Get SHA-1 Fingerprint

**For Debug Build:**
```bash
cd android
./gradlew signingReport
```

Look for the **SHA1** under **Variant: debug** → **Config: debug**

**For Release Build:**
```bash
keytool -list -v -keystore /path/to/your/keystore.jks -alias your-alias
```

### Step 3: Update google-services.json

1. In Google Cloud Console, go to **Project Settings**
2. Download the updated `google-services.json`
3. Replace the file at:
   ```
   app/google-services.json
   ```

### Step 4: Verify Integration

The app already has all necessary code:
- ✅ `GoogleAuthManager.kt` - OAuth authentication
- ✅ `DriveBackupService.kt` - Google Drive API integration
- ✅ `CloudBackupViewModel.kt` - UI state management
- ✅ `CloudBackupScreen.kt` - User interface
- ✅ `DriveBackupWorker.kt` - Auto-backup with WorkManager

### Step 5: Test the Integration

1. Build and install the app:
   ```bash
   ./gradlew assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

2. Open the app → **Settings** → **Backup & Restore**

3. Click **"Sign in with Google"**
   - Should show Google account picker
   - Request permission for Google Drive access
   - Return to app with signed-in state

4. Test backup:
   - Click **"Backup Now"**
   - Should upload current vocabulary data
   - Verify in Google Drive app (check "Trainvoc Backups" folder)

5. Test restore:
   - View backup history
   - Click restore on a backup
   - Verify data is restored correctly

6. Test auto-backup:
   - Enable **"Auto Backup"** toggle
   - Verify WorkManager job is scheduled:
     ```bash
     adb shell dumpsys jobscheduler | grep DriveBackup
     ```

---

## For End Users: How to Enable Cloud Backup

### First-Time Setup

1. Open Trainvoc app
2. Go to **Settings** (⚙️ icon)
3. Scroll to **"Backup & Sync"** section
4. Tap **"Backup & Restore"**

### Sign In to Google Drive

1. Tap **"Sign in with Google"** button
2. Select your Google account
3. Grant permission to access Google Drive
   - App requests access to create its own folder
   - Your existing Drive files are not accessible to the app

### Manual Backup

1. After signing in, tap **"Backup Now"**
2. Wait for upload to complete
3. You'll see a confirmation message with word count

### Enable Automatic Backup

1. Toggle **"Auto Backup"** switch to ON
2. App will automatically backup:
   - Every 24 hours
   - Only on WiFi (to save mobile data)
   - Only when battery is not low

### View Backup History

1. Scroll down to **"Backup History"** section
2. See list of all backups with dates and sizes
3. Each backup shows:
   - Date and time created
   - File size
   - Number of words backed up

### Restore from Backup

1. In backup history, find the backup you want to restore
2. Tap the download icon (☁️ ↓)
3. Confirm restore action
4. App will merge backup data with current data
   - Conflicts are resolved by preferring backup data
   - No data is lost, only updated

### Delete Old Backups

1. In backup history, find backup to delete
2. Tap the trash icon (🗑️)
3. Confirm deletion
4. Backup is permanently removed from Google Drive

### Sign Out

1. Tap your account name at the top
2. Tap **"Sign Out"** button
3. Confirm sign out
4. Auto-backup will be disabled

---

## Troubleshooting

### "Sign-in failed" Error

**Cause:** OAuth client ID not configured or SHA-1 mismatch

**Solution:**
1. Verify SHA-1 fingerprint in Google Cloud Console matches your build
2. Wait 5-10 minutes for Google to propagate changes
3. Uninstall and reinstall the app
4. Try signing in again

### "Backup failed: 403 Forbidden"

**Cause:** App doesn't have Drive API permission

**Solution:**
1. Check Google Cloud Console: APIs & Services → Google Drive API should be **Enabled**
2. Verify OAuth consent screen is configured
3. Add test users if app is in "Testing" mode

### "No backups found"

**Cause:** First time using backup or different Google account

**Solution:**
- Create a new backup using "Backup Now"
- Verify you're signed in with the correct Google account

### Auto-backup not working

**Cause:** WorkManager job not scheduled or constraints not met

**Solution:**
1. Verify auto-backup toggle is ON
2. Connect to WiFi (auto-backup only works on unmetered networks)
3. Charge device (auto-backup only works when battery is not low)
4. Wait up to 24 hours for first automatic backup

### "Restore failed" Error

**Cause:** Corrupted backup file or decryption error

**Solution:**
1. Try restoring a different backup
2. Create a new backup and try restoring it
3. If issue persists, check app logs

---

## Technical Details

### Encryption

- Backups are encrypted using AES-256-GCM before upload
- Encryption key is stored in Android Keystore
- Only your device can decrypt your backups

### Data Stored

Backups include:
- ✅ All vocabulary words
- ✅ Word definitions and meanings
- ✅ Learning statistics (correct/incorrect counts)
- ✅ Progress tracking data
- ✅ User preferences (optional)

Backups do NOT include:
- ❌ Login credentials
- ❌ Google account information
- ❌ Temporary cache data

### Backup File Format

```
Filename: trainvoc_backup_YYYY-MM-DD_HH-MM-SS.enc
Format: Encrypted JSON
Mime Type: application/octet-stream
Location: Google Drive / Trainvoc Backups folder
```

### API Usage

- Uses Google Drive API v3
- Scopes requested:
  - `drive.file` - Access to files created by the app
  - `drive.appdata` - Access to app-specific folder
- Does NOT request full Drive access

### Network Requirements

- Manual backup: Any internet connection
- Auto-backup: WiFi only (unmetered network)
- Upload size: Typically 50-500 KB per backup

### Privacy

- Data is stored in your personal Google Drive
- Only you have access to your backups
- App cannot access other users' backups
- Backups are deleted from Drive when you delete them in the app

---

## Implementation Status

### ✅ Fully Implemented

- OAuth 2.0 authentication with Google Sign-In
- Google Drive API integration
- Backup upload with encryption
- Backup listing from Drive
- Backup restore with decryption
- Backup deletion from Drive
- Auto-backup scheduling with WorkManager
- Conflict resolution strategies
- Progress tracking and error handling
- Comprehensive UI with loading states

### ⚠️ Configuration Required

- Google Cloud Console OAuth 2.0 client ID
- SHA-1 fingerprint registration
- Testing with real Google accounts
- Production OAuth consent screen approval

### 📋 Future Enhancements (Optional)

- Backup compression to reduce file size
- Selective backup (choose what to backup)
- Backup to other cloud providers (Dropbox, OneDrive)
- Backup scheduling customization (interval selection)
- Backup retention policies (auto-delete old backups)
- Delta backups (only backup changes)

---

## Files Reference

### Core Implementation
```
app/src/main/java/com/gultekinahmetabdullah/trainvoc/
├── cloud/
│   ├── GoogleAuthManager.kt        (OAuth authentication)
│   └── DriveBackupService.kt       (Drive API integration)
├── sync/
│   ├── DataExporter.kt             (Backup export with encryption)
│   ├── DataImporter.kt             (Backup import with decryption)
│   ├── ConflictResolver.kt         (Merge strategies)
│   └── BackupModels.kt             (Data models)
├── viewmodel/
│   └── CloudBackupViewModel.kt     (UI state management)
├── ui/screen/settings/
│   └── CloudBackupScreen.kt        (User interface)
└── worker/
    └── DriveBackupWorker.kt        (Auto-backup job)
```

### Configuration
```
app/
├── google-services.json            (Firebase/OAuth config)
└── build.gradle.kts                (Dependencies)
```

---

## Support

### For Developers

If you encounter issues during setup:
1. Check Google Cloud Console for API quotas
2. Verify OAuth credentials are correct
3. Review app logs for detailed error messages
4. Test with debug build before release build

### For Users

If you have questions about cloud backup:
1. Check this guide's Troubleshooting section
2. Contact app support via Settings → About → Contact
3. Visit the FAQ page in the app

---

**Last Updated:** January 22, 2026
**Feature Status:** Production-Ready (Pending OAuth Setup)
**Estimated Setup Time:** 30 minutes (first time), 5 minutes (updates)
