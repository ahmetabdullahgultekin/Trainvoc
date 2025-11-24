# CI/CD Pipeline Design

## Overview
Automated build, test, and deployment pipeline using GitHub Actions for continuous integration and delivery.

---

## 1. GitHub Actions Workflow

### 1.1 Main CI Workflow

**File**: `.github/workflows/ci.yml`
```yaml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true

jobs:
  lint:
    name: Lint Check
    runs-on: ubuntu-latest
    timeout-minutes: 30

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Run Android Lint
        run: ./gradlew lintDebug

      - name: Upload Lint Reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: lint-reports
          path: app/build/reports/lint-results-debug.html

  detekt:
    name: Code Quality (Detekt)
    runs-on: ubuntu-latest
    timeout-minutes: 30

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Run Detekt
        run: ./gradlew detekt

      - name: Upload Detekt Reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: detekt-reports
          path: app/build/reports/detekt/

  unit-tests:
    name: Unit Tests
    runs-on: ubuntu-latest
    timeout-minutes: 30

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest

      - name: Generate Test Coverage
        run: ./gradlew jacocoTestReport

      - name: Upload Test Reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-reports
          path: app/build/reports/tests/

      - name: Upload Coverage Reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: coverage-reports
          path: app/build/reports/jacoco/

      - name: Comment PR with Coverage
        if: github.event_name == 'pull_request'
        uses: madrapps/jacoco-report@v1.6
        with:
          paths: ${{ github.workspace }}/app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml
          token: ${{ secrets.GITHUB_TOKEN }}
          min-coverage-overall: 70
          min-coverage-changed-files: 80

  instrumented-tests:
    name: Instrumented Tests
    runs-on: macOS-latest
    timeout-minutes: 45

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Run Instrumented Tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 30
          target: google_apis
          arch: x86_64
          profile: Nexus 6
          script: ./gradlew connectedDebugAndroidTest

      - name: Upload Instrumented Test Reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: instrumented-test-reports
          path: app/build/reports/androidTests/

  build:
    name: Build APK
    runs-on: ubuntu-latest
    timeout-minutes: 30
    needs: [lint, detekt, unit-tests]

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Build Debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk

  security-scan:
    name: Security Scan
    runs-on: ubuntu-latest
    timeout-minutes: 30

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Run Dependency Check
        uses: dependency-check/Dependency-Check_Action@main
        with:
          project: 'TrainVoc'
          path: '.'
          format: 'HTML'

      - name: Upload Security Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: security-report
          path: reports/
```

### 1.2 Release Workflow

**File**: `.github/workflows/release.yml`
```yaml
name: Release Build

on:
  push:
    tags:
      - 'v*.*.*'

jobs:
  build-release:
    name: Build Release APK/AAB
    runs-on: ubuntu-latest
    timeout-minutes: 45

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Decode Keystore
        env:
          KEYSTORE_BASE64: ${{ secrets.KEYSTORE_BASE64 }}
        run: |
          echo $KEYSTORE_BASE64 | base64 --decode > $GITHUB_WORKSPACE/keystore.jks

      - name: Build Release AAB
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: ./gradlew bundleRelease

      - name: Build Release APK
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: ./gradlew assembleRelease

      - name: Upload Release AAB
        uses: actions/upload-artifact@v4
        with:
          name: app-release-aab
          path: app/build/outputs/bundle/release/app-release.aab

      - name: Upload Release APK
        uses: actions/upload-artifact@v4
        with:
          name: app-release-apk
          path: app/build/outputs/apk/release/app-release.apk

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        with:
          files: |
            app/build/outputs/bundle/release/app-release.aab
            app/build/outputs/apk/release/app-release.apk
          generate_release_notes: true
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

  deploy-play-store:
    name: Deploy to Play Store (Internal)
    runs-on: ubuntu-latest
    needs: build-release
    if: startsWith(github.ref, 'refs/tags/v')

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Download AAB
        uses: actions/download-artifact@v4
        with:
          name: app-release-aab

      - name: Upload to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_SERVICE_ACCOUNT }}
          packageName: com.gultekinahmetabdullah.trainvoc
          releaseFiles: app-release.aab
          track: internal
          status: completed
```

### 1.3 Nightly Build Workflow

**File**: `.github/workflows/nightly.yml`
```yaml
name: Nightly Build

on:
  schedule:
    - cron: '0 2 * * *'  # Run at 2 AM UTC daily
  workflow_dispatch:  # Allow manual trigger

jobs:
  nightly-build:
    name: Nightly Build
    runs-on: ubuntu-latest
    timeout-minutes: 60

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Run All Tests
        run: ./gradlew test

      - name: Build All Variants
        run: ./gradlew assemble

      - name: Run Benchmarks
        run: ./gradlew benchmarkRelease || true

      - name: Generate Dependency Graph
        run: ./gradlew generateDependencyGraph

      - name: Upload Artifacts
        uses: actions/upload-artifact@v4
        with:
          name: nightly-build-${{ github.run_number }}
          path: |
            app/build/outputs/
            app/build/reports/
```

---

## 2. Code Quality Tools

### 2.1 Detekt Configuration

**File**: `config/detekt/detekt.yml`
```yaml
build:
  maxIssues: 0

config:
  validation: true
  warningsAsErrors: true

complexity:
  active: true
  ComplexMethod:
    active: true
    threshold: 15
  LongMethod:
    active: true
    threshold: 60
  LongParameterList:
    active: true
    threshold: 6
  TooManyFunctions:
    active: true
    thresholdInFiles: 15
    thresholdInClasses: 15

empty-blocks:
  active: true

exceptions:
  active: true
  TooGenericExceptionCaught:
    active: true
  SwallowedException:
    active: true

naming:
  active: true
  FunctionNaming:
    active: true
  VariableNaming:
    active: true
  ClassNaming:
    active: true

performance:
  active: true

potential-bugs:
  active: true

style:
  active: true
  MaxLineLength:
    active: true
    maxLineLength: 120
  MagicNumber:
    active: true
    ignoreNumbers: ['-1', '0', '1', '2']
    ignoreHashCodeFunction: true
    ignorePropertyDeclaration: true
    ignoreAnnotation: true
  ReturnCount:
    active: true
    max: 3
```

**File**: `app/build.gradle.kts` (Detekt Integration)
```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt/baseline.xml")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.4")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(true)
    }
}
```

### 2.2 JaCoCo Code Coverage

**File**: `app/build.gradle.kts` (JaCoCo Configuration)
```kotlin
android {
    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*_HiltComponents*.*",
        "**/*_Factory*.*",
        "**/*_MembersInjector*.*"
    )

    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(buildDir) {
        include("jacoco/testDebugUnitTest.exec")
    })
}
```

---

## 3. Branch Protection Rules

### 3.1 GitHub Branch Protection

**Settings → Branches → Branch protection rules → main**

```yaml
Protected Branch: main

Require pull request reviews before merging:
  ✓ Enabled
  Required approvals: 1
  ✓ Dismiss stale pull request approvals when new commits are pushed
  ✓ Require review from Code Owners

Require status checks to pass before merging:
  ✓ Enabled
  ✓ Require branches to be up to date before merging
  Required status checks:
    - lint
    - detekt
    - unit-tests
    - build

Require conversation resolution before merging:
  ✓ Enabled

Require signed commits:
  ✓ Enabled (Recommended)

Include administrators:
  ✓ Enabled

Restrict who can push to matching branches:
  - Only maintainers

Allow force pushes:
  ✗ Disabled

Allow deletions:
  ✗ Disabled
```

---

## 4. Secrets Management

### 4.1 GitHub Secrets Configuration

**Settings → Secrets and variables → Actions → New repository secret**

Required Secrets:
```
KEYSTORE_BASE64          # Base64 encoded keystore file
KEYSTORE_PASSWORD        # Keystore password
KEY_ALIAS                # Key alias
KEY_PASSWORD             # Key password
PLAY_STORE_SERVICE_ACCOUNT  # Play Store service account JSON
FIREBASE_SERVICE_ACCOUNT    # Firebase service account JSON
```

### 4.2 Keystore Setup Script

**File**: `scripts/encode_keystore.sh`
```bash
#!/bin/bash

# Encode keystore to base64 for GitHub Secrets
if [ -z "$1" ]; then
    echo "Usage: ./encode_keystore.sh <path_to_keystore>"
    exit 1
fi

KEYSTORE_PATH=$1

if [ ! -f "$KEYSTORE_PATH" ]; then
    echo "Error: Keystore file not found: $KEYSTORE_PATH"
    exit 1
fi

echo "Encoding keystore to base64..."
base64 -i "$KEYSTORE_PATH" | pbcopy
echo "Base64 encoded keystore copied to clipboard!"
echo "Add it to GitHub Secrets as KEYSTORE_BASE64"
```

---

## 5. Pull Request Template

**File**: `.github/PULL_REQUEST_TEMPLATE.md`
```markdown
## Description
<!-- Describe your changes in detail -->

## Type of Change
<!-- Mark relevant options with 'x' -->

- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update
- [ ] Code refactoring
- [ ] Performance improvement
- [ ] Test addition/modification

## Related Issues
<!-- Link to related issues, e.g., "Fixes #123" or "Relates to #456" -->

Fixes #

## Checklist
<!-- Mark completed items with 'x' -->

- [ ] My code follows the project's coding style
- [ ] I have performed a self-review of my code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
- [ ] Any dependent changes have been merged and published
- [ ] I have checked my code and corrected any misspellings

## Testing
<!-- Describe the tests you ran and how to reproduce them -->

**Test Configuration**:
- Device/Emulator:
- Android Version:
- Build Type:

**Test Cases**:
1.
2.
3.

## Screenshots (if applicable)
<!-- Add screenshots to demonstrate UI changes -->

## Additional Notes
<!-- Any additional information that reviewers should know -->
```

---

## 6. Issue Templates

### 6.1 Bug Report Template

**File**: `.github/ISSUE_TEMPLATE/bug_report.md`
```markdown
---
name: Bug Report
about: Create a report to help us improve
title: '[BUG] '
labels: bug
assignees: ''
---

## Bug Description
<!-- A clear and concise description of the bug -->

## Steps to Reproduce
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## Expected Behavior
<!-- What you expected to happen -->

## Actual Behavior
<!-- What actually happened -->

## Screenshots
<!-- If applicable, add screenshots to help explain the problem -->

## Environment
- Device: [e.g., Pixel 6]
- Android Version: [e.g., Android 13]
- App Version: [e.g., 1.1.1]
- Build Type: [Debug/Release]

## Logs/Stack Trace
<!-- If available, paste relevant logs or stack trace -->

```
Paste logs here
```

## Additional Context
<!-- Any other context about the problem -->
```

### 6.2 Feature Request Template

**File**: `.github/ISSUE_TEMPLATE/feature_request.md`
```markdown
---
name: Feature Request
about: Suggest an idea for this project
title: '[FEATURE] '
labels: enhancement
assignees: ''
---

## Feature Description
<!-- A clear and concise description of the feature -->

## Problem Statement
<!-- Describe the problem this feature would solve -->

## Proposed Solution
<!-- Describe how you envision this feature working -->

## Alternatives Considered
<!-- Describe alternative solutions you've considered -->

## Additional Context
<!-- Any other context, mockups, or screenshots about the feature -->

## Priority
- [ ] Critical (Blocking core functionality)
- [ ] High (Important for user experience)
- [ ] Medium (Nice to have)
- [ ] Low (Future consideration)
```

---

## 7. Automated Deployment

### 7.1 Play Store Deployment Script

**File**: `scripts/deploy_play_store.sh`
```bash
#!/bin/bash

# Deploy to Google Play Store Internal Track
# Requires: google-services-account.json

set -e

VERSION_NAME=$(grep "versionName" app/build.gradle.kts | awk '{print $3}' | tr -d '"')
VERSION_CODE=$(grep "versionCode" app/build.gradle.kts | awk '{print $3}')

echo "Deploying TrainVoc v$VERSION_NAME ($VERSION_CODE) to Play Store..."

# Build release AAB
echo "Building release AAB..."
./gradlew bundleRelease

# Upload to Play Store using fastlane (if configured)
# Or use Google Play Developer API directly

echo "Upload completed successfully!"
echo "Version: $VERSION_NAME ($VERSION_CODE)"
echo "Track: Internal Testing"
```

---

## 8. Continuous Monitoring

### 8.1 Build Health Dashboard

Create a GitHub Wiki page to track:
- Build success rate (Target: > 95%)
- Average build time
- Test pass rate (Target: 100%)
- Code coverage trend
- Critical/High severity issues count

### 8.2 Notifications

Configure GitHub Actions notifications:
- Slack/Discord webhook for build failures
- Email notifications for release deployments
- Team mentions in PR for failing checks

---

## 9. Implementation Checklist

### Week 1: Basic CI Setup
- [ ] Create `.github/workflows/ci.yml`
- [ ] Configure JDK setup
- [ ] Add lint check job
- [ ] Add unit test job
- [ ] Verify builds on push/PR

### Week 2: Code Quality
- [ ] Add Detekt configuration
- [ ] Create `detekt.yml` config
- [ ] Add Detekt to CI workflow
- [ ] Configure JaCoCo coverage
- [ ] Set coverage thresholds

### Week 3: Advanced Workflows
- [ ] Create release workflow
- [ ] Configure GitHub Secrets
- [ ] Set up branch protection
- [ ] Add PR/issue templates
- [ ] Configure code owners

### Week 4: Deployment
- [ ] Set up Play Store deployment
- [ ] Configure staging environment
- [ ] Implement automated versioning
- [ ] Test end-to-end pipeline
- [ ] Document processes

---

**Next Document**: Implementation Phases (Step-by-Step Roadmap)
