# Trainvoc Branding & Constants Reference

> **Purpose:** Single source of truth for all branding, contact information, and common strings across TrainvocClient, TrainvocWeb, and TrainvocBackend.
>
> **Usage:** When updating any of these values, update this file first, then propagate changes to all modules.

---

## 1. Product Identity

| Key | Value | Notes |
|-----|-------|-------|
| **Product Name** | `Trainvoc` | Preferred casing. Avoid: TrainVoc, TRAINVOC, Train Voc |
| **Product Tagline** | `Learn vocabulary through play` | Unified tagline (EN) |
| **Product Tagline (TR)** | `Oynayarak kelime öğren` | Unified tagline (TR) |
| **Product Description** | `Trainvoc is a next-generation platform designed to make English-Turkish vocabulary learning fun, social, and sustainable.` | Long version |
| **Product Category** | Education / Language Learning | App store category |

---

## 2. Company Identity

| Key | Value | Notes |
|-----|-------|-------|
| **Company Name** | `Rolling Cat Software` | Official name |
| **Company Name (Package)** | `rollingcatsoftware` | For Java/Kotlin packages |
| **Location** | `Turkey` | For "Made in" statements |
| **Company Location (Full)** | `Istanbul, Turkey` | For contact pages |

---

## 3. Author / Developer

| Key | Value | Notes |
|-----|-------|-------|
| **Full Name** | `Ahmet Abdullah Gultekin` | ASCII version (no Turkish chars) |
| **Full Name (Turkish)** | `Ahmet Abdullah Gültekin` | With Turkish characters |
| **GitHub Username** | `ahmetabdullahgultekin` | |
| **Personal Email** | `ahmetabdullahgultekin@gmail.com` | For contributions/credits |
| **Personal Website** | `https://ahmetabdullahgultekin.com` | Portfolio |

---

## 4. Contact Information

| Purpose | Email | Notes |
|---------|-------|-------|
| **Primary Support** | `rollingcat.help@gmail.com` | User-facing support (OFFICIAL) |
| **Company Contact** | `info@rollingcatsoftware.com` | Business inquiries |
| **Developer Contact** | `ahmetabdullahgultekin@gmail.com` | Technical/contribution contact |

---

## 5. URLs

### Production URLs

| Purpose | URL |
|---------|-----|
| **Company Website** | `https://www.rollingcatsoftware.com` |
| **Product Website** | `https://trainvoc.rollingcatsoftware.com` |
| **API (Production)** | `https://api.trainvoc.rollingcatsoftware.com` |

### Development URLs

| Purpose | URL |
|---------|-----|
| **API (Local)** | `http://localhost:8080` |
| **Web (Local)** | `http://localhost:5173` |

### Social & Repository

| Platform | URL / Handle |
|----------|--------------|
| **GitHub Repository** | `https://github.com/ahmetabdullahgultekin/Trainvoc` |
| **Twitter/X (Company)** | `@rollingcatsoftware` |
| **Twitter/X (Product)** | `@trainvoc` |

---

## 6. Package Identifiers

| Module | Package/Identifier | Notes |
|--------|-------------------|-------|
| **TrainvocClient (Current)** | `com.gultekinahmetabdullah.trainvoc` | Legacy, in Play Store |
| **TrainvocClient (Target)** | `com.rollingcatsoftware.trainvoc` | Migrate to this |
| **TrainvocBackend** | `com.rollingcatsoftware.trainvocmultiplayerapplication` | |
| **TrainvocWeb (npm)** | `trainvoc-web` | |

---

## 7. Versioning

| Module | Current Version | Version Code | Notes |
|--------|-----------------|--------------|-------|
| **TrainvocClient** | `1.2.0` | `13` | Android versionCode |
| **TrainvocWeb** | `0.1.0` | - | npm version |
| **TrainvocBackend API** | `1.0.0` | - | OpenAPI version |

---

## 8. Legal

| Key | Value |
|-----|-------|
| **License** | MIT License |
| **License URL** | `https://opensource.org/licenses/MIT` |
| **Copyright Holder** | Ahmet Abdullah Gultekin |
| **Copyright Years** | 2024-2026 |
| **Copyright Notice** | `Copyright (c) 2024-2026 Ahmet Abdullah Gultekin` |
| **Privacy Policy** | TODO: Create at `/privacy-policy` |
| **Terms of Service** | TODO: Create at `/terms` |

---

## 9. Visual Identity

### Brand Colors

| Color | Hex Code | Usage |
|-------|----------|-------|
| **Primary (Indigo)** | `#6366F1` | Main brand color |
| **Primary Dark** | `#4F46E5` | Hover states, dark mode |
| **Secondary (Purple)** | `#8B5CF6` | Accents |

### Logo & Mascot

| Asset | Description | Status |
|-------|-------------|--------|
| **App Icon** | Trainvoc logo | `@mipmap/ic_launcher` |
| **Mascot** | Rolling Cat animation | Keep - matches company name |
| **Favicon** | Web favicon | `vite.svg` (update needed) |

---

## 10. Supported Languages

| Language | Code | Status |
|----------|------|--------|
| **English** | `en` | Full support |
| **Turkish** | `tr` | Full support |
| ~~Spanish~~ | ~~`es`~~ | Removed (incomplete) |
| ~~French~~ | ~~`fr`~~ | Removed (incomplete) |
| ~~German~~ | ~~`de`~~ | Removed (incomplete) |
| ~~Arabic~~ | ~~`ar`~~ | Removed (incomplete) |

---

## 11. Localized Strings

### App Name
| Language | Value |
|----------|-------|
| English | `Trainvoc` |
| Turkish | `Trainvoc` |

### Tagline (Unified)
| Language | Value |
|----------|-------|
| English | `Learn vocabulary through play` |
| Turkish | `Oynayarak kelime öğren` |

### Description (Short)
| Language | Value |
|----------|-------|
| English | `Learn English-Turkish vocabulary through fun games.` |
| Turkish | `Eğlenceli oyunlarla İngilizce-Türkçe kelime öğren.` |

### Description (Long)
| Language | Value |
|----------|-------|
| English | `Trainvoc is a next-generation platform designed to make English-Turkish vocabulary learning fun, social, and sustainable. Compete with friends, track your progress on leaderboards, and practice anywhere with our mobile app.` |
| Turkish | `Trainvoc, İngilizce-Türkçe kelime öğrenimini eğlenceli, sosyal ve sürdürülebilir hale getirmek için geliştirilmiş yeni nesil bir platformdur. Arkadaşlarınla yarış, liderlik tablosunda yerini gör ve mobil uygulamamızla her yerde pratik yap.` |

### Copyright Footer
| Language | Value |
|----------|-------|
| English | `© 2024-2026 Trainvoc. All rights reserved.` |
| Turkish | `© 2024-2026 Trainvoc. Tüm hakları saklıdır.` |

### Made In
| Language | Value |
|----------|-------|
| English | `Made with love in Turkey` |
| Turkish | `Türkiye'de sevgiyle yapıldı` |

---

## 12. UI Common Labels

### Navigation
| Key | English | Turkish |
|-----|---------|---------|
| `nav.home` | Home | Ana Sayfa |
| `nav.about` | About | Hakkında |
| `nav.contact` | Contact | İletişim |
| `nav.help` | Help | Yardım |
| `nav.faq` | FAQ | SSS |
| `nav.settings` | Settings | Ayarlar |
| `nav.profile` | Profile | Profil |

### Actions
| Key | English | Turkish |
|-----|---------|---------|
| `action.login` | Login | Giriş Yap |
| `action.logout` | Logout | Çıkış Yap |
| `action.signup` | Sign Up | Kayıt Ol |
| `action.submit` | Submit | Gönder |
| `action.cancel` | Cancel | İptal |
| `action.save` | Save | Kaydet |
| `action.delete` | Delete | Sil |
| `action.edit` | Edit | Düzenle |
| `action.back` | Back | Geri |
| `action.next` | Next | İleri |
| `action.retry` | Retry | Tekrar Dene |

### Status Messages
| Key | English | Turkish |
|-----|---------|---------|
| `status.loading` | Loading... | Yükleniyor... |
| `status.error` | An error occurred | Bir hata oluştu |
| `status.success` | Success | Başarılı |
| `status.offline` | You are offline | Çevrimdışısınız |
| `status.online` | You are online | Çevrimiçisiniz |

---

## 13. File Locations Reference

When updating branding, check these files:

### TrainvocClient (Android/Kotlin)
```
app/src/main/res/values/strings.xml          # Primary string resources
app/src/main/res/values-tr/strings.xml       # Turkish translations
app/build.gradle.kts                          # Version info, package ID
app/src/main/AndroidManifest.xml              # App name, package
ui/screen/other/AboutScreen.kt                # About page content
ui/screen/other/HelpScreen.kt                 # Help/FAQ content
```

### TrainvocWeb (React/TypeScript)
```
src/locales/en/translation.json               # English strings
src/locales/tr/translation.json               # Turkish strings
src/pages/AboutPage.tsx                       # About page content
src/pages/ContactPage.tsx                     # Contact page content
src/components/layout/Footer.tsx              # Footer content
public/manifest.json                          # PWA manifest
package.json                                  # Version info
index.html                                    # Meta tags, title
```

### TrainvocBackend (Java/Spring)
```
src/main/resources/application.properties     # App configuration
build.gradle                                  # Group ID, version
```

### Root
```
README.md                                     # Repository description
LICENSE                                       # Copyright notice
BRANDING.md                                   # This file
```

---

## 14. Consistency Checklist

When making branding updates, verify:

- [x] Product name uses consistent casing (`Trainvoc`)
- [x] Copyright year is current (`2024-2026`)
- [ ] Support email updated to `rollingcat.help@gmail.com`
- [ ] Website URLs point to `trainvoc.rollingcatsoftware.com`
- [ ] Package ID migrated to `com.rollingcatsoftware.trainvoc`
- [ ] Remove incomplete language translations (ES, FR, DE, AR)
- [ ] Unified tagline applied across all platforms
- [ ] Privacy Policy created
- [ ] Terms of Service created
- [x] Translations exist for all user-facing strings (EN, TR)
- [x] License information is consistent across all files

---

## 15. Pending Tasks

| Task | Priority | Status |
|------|----------|--------|
| Update support email in all files | High | TODO |
| Create Privacy Policy page | High | TODO |
| Create Terms of Service page | High | TODO |
| Migrate Android package ID | Medium | TODO |
| Remove incomplete translations | Medium | TODO |
| Update website URLs in meta tags | Medium | TODO |
| Unify tagline across all pages | Medium | TODO |

---

*Last Updated: January 2026*
*Maintainer: Ahmet Abdullah Gultekin*
*Company: Rolling Cat Software*
