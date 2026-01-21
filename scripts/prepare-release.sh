#!/bin/bash

# Trainvoc - Release Preparation Script
# Automates the release build preparation and verification process

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
AAB_PATH="app/build/outputs/bundle/release/app-release.aab"

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}Trainvoc Release Preparation Script${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Function to print status
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Step 1: Check environment variables
echo -e "${BLUE}[1/6] Checking signing configuration...${NC}"
if [ -z "$TRAINVOC_KEYSTORE_PATH" ] && [ -z "$(grep -s TRAINVOC_KEYSTORE_PATH local.properties)" ]; then
    print_warning "Signing environment variables not set"
    echo ""
    echo "To sign the release build, set these environment variables:"
    echo "  export TRAINVOC_KEYSTORE_PATH=\"/path/to/trainvoc-upload-key.jks\""
    echo "  export TRAINVOC_KEYSTORE_PASSWORD=\"your_keystore_password\""
    echo "  export TRAINVOC_KEY_ALIAS=\"trainvoc-upload\""
    echo "  export TRAINVOC_KEY_PASSWORD=\"your_key_password\""
    echo ""
    echo "Or add them to local.properties (not committed to git)"
    echo ""
    read -p "Continue without signing? The build will work but won't be uploadable to Play Store (y/n): " continue_unsigned
    if [ "$continue_unsigned" != "y" ]; then
        echo "Aborted. Please configure signing and try again."
        exit 1
    fi
else
    print_status "Signing configuration found"
fi
echo ""

# Step 2: Check version
echo -e "${BLUE}[2/6] Checking version configuration...${NC}"
VERSION_CODE=$(grep "versionCode = " app/build.gradle.kts | sed 's/.*= //')
VERSION_NAME=$(grep "versionName = " app/build.gradle.kts | sed 's/.*= //' | tr -d '"')
print_status "Version Code: $VERSION_CODE"
print_status "Version Name: $VERSION_NAME"
echo ""

# Step 3: Clean previous builds
echo -e "${BLUE}[3/6] Cleaning previous builds...${NC}"
./gradlew clean
print_status "Build directory cleaned"
echo ""

# Step 4: Run lint checks
echo -e "${BLUE}[4/6] Running lint checks...${NC}"
echo "(This may take a few minutes...)"
if ./gradlew lintRelease 2>&1 | tee /tmp/lint-output.log; then
    print_status "Lint checks passed"
else
    print_warning "Lint checks found issues (see /tmp/lint-output.log)"
    echo "Build will continue, but please review lint report:"
    echo "  app/build/reports/lint-results-release.html"
fi
echo ""

# Step 5: Build release AAB
echo -e "${BLUE}[5/6] Building release Android App Bundle...${NC}"
echo "(This may take several minutes...)"
if ./gradlew bundleRelease; then
    print_status "Release AAB built successfully"
else
    print_error "Build failed! Check the error messages above."
    exit 1
fi
echo ""

# Step 6: Verify the build
echo -e "${BLUE}[6/6] Verifying the build...${NC}"

if [ -f "$AAB_PATH" ]; then
    AAB_SIZE=$(ls -lh "$AAB_PATH" | awk '{print $5}')
    print_status "AAB file exists: $AAB_PATH"
    print_status "AAB size: $AAB_SIZE"

    # Check if AAB is signed
    if unzip -l "$AAB_PATH" | grep -q "META-INF/.*\.RSA\|META-INF/.*\.DSA\|META-INF/.*\.EC"; then
        print_status "AAB is signed"
    else
        print_warning "AAB is NOT signed (won't be uploadable to Play Store)"
    fi
else
    print_error "AAB file not found at $AAB_PATH"
    exit 1
fi
echo ""

# Summary
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}✓ Release build completed successfully!${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo "Build Information:"
echo "  Version: $VERSION_NAME (code: $VERSION_CODE)"
echo "  File: $AAB_PATH"
echo "  Size: $AAB_SIZE"
echo ""
echo "Next Steps:"
echo ""
echo "1. Test the AAB (optional):"
echo "   bundletool build-apks --bundle=$AAB_PATH --output=output.apks --mode=universal"
echo ""
echo "2. Review the build:"
echo "   - Check lint report: app/build/reports/lint-results-release.html"
echo "   - Verify version code and name"
echo ""
echo "3. Upload to Google Play Console:"
echo "   - Go to: https://play.google.com/console"
echo "   - Select your app"
echo "   - Create new release (Internal Testing recommended first)"
echo "   - Upload: $AAB_PATH"
echo "   - Add release notes (see docs/release-notes-*.md)"
echo ""
echo "4. Complete store listing (if first release):"
echo "   - See: docs/DEPLOYMENT_READY_CHECKLIST.md"
echo ""
echo -e "${GREEN}Good luck with your release! 🚀${NC}"
echo ""
