# Google Play Store Submission Checklist

Complete this checklist before submitting Trainvoc to Google Play.

---

## Pre-Submission Requirements

### 1. Developer Account
- [ ] Google Play Developer account created ($25 one-time fee)
- [ ] Account identity verified
- [ ] Developer contact information updated

### 2. App Signing
- [ ] Upload keystore generated
  ```bash
  keytool -genkey -v -keystore trainvoc-upload-key.jks \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -alias trainvoc-upload
  ```
- [ ] Keystore stored securely (NOT in git)
- [ ] Keystore password documented securely
- [ ] Environment variables configured:
  - [ ] `TRAINVOC_KEYSTORE_PATH`
  - [ ] `TRAINVOC_KEYSTORE_PASSWORD`
  - [ ] `TRAINVOC_KEY_ALIAS`
  - [ ] `TRAINVOC_KEY_PASSWORD`
- [ ] Google Play App Signing enrolled (recommended)

### 3. Build Release AAB
- [ ] Version code incremented (current: 13)
- [ ] Version name updated (current: 1.2.0)
- [ ] Release build generated:
  ```bash
  cd TrainvocClient
  ./gradlew bundleRelease
  ```
- [ ] AAB file located at `app/build/outputs/bundle/release/app-release.aab`
- [ ] AAB file tested locally

---

## Play Console Configuration

### 4. App Information
- [ ] App name: "Trainvoc - English Vocabulary"
- [ ] Default language: English (US)
- [ ] App category: Education
- [ ] Tags/keywords added

### 5. Store Listing - Main
- [ ] Short description (80 chars) - see `store-listing/store-listing.md`
- [ ] Full description (4000 chars) - see `store-listing/store-listing.md`
- [ ] App icon uploaded (512x512 from app resources)

### 6. Store Listing - Graphics
- [ ] Feature graphic uploaded (1024x500 px)
- [ ] Phone screenshots uploaded (minimum 2, max 8)
  - [ ] Screenshot 1: Home/Games menu
  - [ ] Screenshot 2: Vocabulary list
  - [ ] Screenshot 3: Quiz gameplay
  - [ ] Screenshot 4: Memory game
  - [ ] Screenshot 5: Achievements
  - [ ] Screenshot 6: Statistics
  - [ ] Screenshot 7: Daily goals
  - [ ] Screenshot 8: Settings
- [ ] Tablet screenshots (optional but recommended)
  - [ ] 7-inch tablet
  - [ ] 10-inch tablet

### 7. Store Listing - Localization
- [ ] Turkish translation added:
  - [ ] App name (Turkish)
  - [ ] Short description (Turkish)
  - [ ] Full description (Turkish)

---

## Policy Compliance

### 8. Content Rating
- [ ] IARC questionnaire completed
- [ ] Rating received (expected: PEGI 3 / Everyone)
- [ ] Rating displayed in store listing

### 9. Privacy Policy
- [ ] Privacy policy page hosted at public URL
- [ ] URL: `https://trainvoc.rollingcatsoftware.com/privacy`
- [ ] Privacy policy URL added to Play Console
- [ ] Privacy policy covers:
  - [ ] Data collected
  - [ ] Data usage
  - [ ] Data storage
  - [ ] User rights (GDPR/KVKK)
  - [ ] Contact information

### 10. Data Safety
- [ ] Data safety form completed
- [ ] Data types declared:
  - [ ] App activity (quiz scores, progress)
  - [ ] App info (crash logs - if applicable)
- [ ] Data practices declared:
  - [ ] Data encrypted in transit: Yes
  - [ ] Data can be deleted: Yes
  - [ ] Data shared with third parties: No
- [ ] Review submitted

### 11. Target Audience
- [ ] Target age group selected: All ages
- [ ] App appeals to children declared: No (or Yes if applicable)
- [ ] Teacher Approved badge (optional)

### 12. Ads Declaration
- [ ] Contains ads: No
- [ ] (If yes, ad SDK declarations)

---

## App Access

### 13. App Functionality
- [ ] All features accessible without login
- [ ] Demo content available (vocabulary database)
- [ ] No restricted access requiring credentials

---

## Testing

### 14. Pre-Launch Testing
- [ ] Internal testing track created
- [ ] AAB uploaded to internal testing
- [ ] Tested on multiple devices:
  - [ ] Phone (Android 7.0+)
  - [ ] Phone (Android 14+)
  - [ ] Tablet (optional)
- [ ] All games functional
- [ ] Offline mode works
- [ ] No crashes reported
- [ ] Pre-launch report reviewed (Play Console)

### 15. Review Test Instructions
- [ ] Test instructions provided (if needed)
- [ ] No special access required

---

## Release

### 16. Release Track Selection
- [ ] Choose release track:
  - [ ] Internal testing (team only)
  - [ ] Closed testing (invited testers)
  - [ ] Open testing (public beta)
  - [ ] Production (full release)

### 17. Rollout Configuration
- [ ] Staged rollout percentage (recommended: 20% initially)
- [ ] Countries/regions selected
- [ ] Release notes added (What's New)

### 18. Final Submission
- [ ] All warnings resolved
- [ ] All errors fixed
- [ ] Review checklist in Play Console completed
- [ ] "Start rollout" clicked
- [ ] Review status monitored

---

## Post-Submission

### 19. Review Process
- [ ] Estimated review time: 1-7 days (typically 1-3)
- [ ] Monitor for rejection emails
- [ ] Check Play Console for status updates

### 20. If Rejected
- [ ] Read rejection reason carefully
- [ ] Make necessary changes
- [ ] Resubmit for review

### 21. After Approval
- [ ] Verify listing is live
- [ ] Test install from Play Store
- [ ] Share app link
- [ ] Monitor reviews and ratings
- [ ] Set up Google Play Console alerts

---

## Quick Reference

### Important URLs
- Play Console: https://play.google.com/console
- Privacy Policy: https://trainvoc.rollingcatsoftware.com/privacy
- Terms of Service: https://trainvoc.rollingcatsoftware.com/terms

### Key Files
- Store listing: `TrainvocClient/store-listing/store-listing.md`
- Screenshots folder: `TrainvocClient/store-listing/screenshots/`
- Privacy policy: `docs/privacy-policy.html`

### Build Commands
```bash
# Clean and build release
cd TrainvocClient
./gradlew clean bundleRelease

# Find AAB file
ls -la app/build/outputs/bundle/release/
```

### Support Contact
- Email: rollingcat.help@gmail.com
- Developer: Ahmet Abdullah Gultekin
- Company: Rolling Cat Software

---

*Checklist created: January 25, 2026*
