#!/bin/bash

# Trainvoc - Keystore Generation Script
# This script generates the upload keystore for Google Play Store signing
# Run this ONCE and store the keystore file and passwords securely!

set -e

echo "========================================="
echo "Trainvoc Keystore Generation Script"
echo "========================================="
echo ""

# Configuration
KEYSTORE_NAME="trainvoc-upload-key.jks"
KEY_ALIAS="trainvoc-upload"
VALIDITY_DAYS=10000  # ~27 years
KEY_SIZE=2048
KEY_ALGORITHM="RSA"

# Check if keystore already exists
if [ -f "$KEYSTORE_NAME" ]; then
    echo "⚠️  WARNING: Keystore file '$KEYSTORE_NAME' already exists!"
    echo ""
    read -p "Do you want to overwrite it? This will invalidate your existing app signature! (yes/no): " confirm
    if [ "$confirm" != "yes" ]; then
        echo "Aborted. Using existing keystore."
        exit 0
    fi
    echo ""
fi

echo "This script will generate a new keystore for Google Play Store signing."
echo ""
echo "⚠️  IMPORTANT SECURITY NOTES:"
echo "   1. Store the keystore file in a SECURE location"
echo "   2. BACKUP the keystore file and passwords"
echo "   3. NEVER commit the keystore to git"
echo "   4. Keep passwords in a password manager"
echo "   5. If you lose this keystore, you CANNOT update your app!"
echo ""

read -p "Press Enter to continue or Ctrl+C to cancel..."
echo ""

# Collect keystore information
echo "Enter the following information for the keystore:"
echo "(Press Enter to use default values shown in [brackets])"
echo ""

read -p "Full Name [Ahmet Abdullah Gultekin]: " cn
cn=${cn:-"Ahmet Abdullah Gultekin"}

read -p "Organizational Unit [Development]: " ou
ou=${ou:-"Development"}

read -p "Organization [Trainvoc]: " o
o=${o:-"Trainvoc"}

read -p "City/Locality: " l
l=${l:-""}

read -p "State/Province: " st
st=${st:-""}

read -p "Country Code (2 letters, e.g., US, TR): " c
c=${c:-""}

echo ""
echo "========================================="
echo "Generating keystore..."
echo "========================================="

# Generate the keystore
keytool -genkey -v \
    -keystore "$KEYSTORE_NAME" \
    -keyalg "$KEY_ALGORITHM" \
    -keysize $KEY_SIZE \
    -validity $VALIDITY_DAYS \
    -alias "$KEY_ALIAS" \
    -dname "CN=$cn, OU=$ou, O=$o, L=$l, ST=$st, C=$c"

echo ""
echo "========================================="
echo "✅ Keystore generated successfully!"
echo "========================================="
echo ""
echo "Keystore details:"
echo "  File: $KEYSTORE_NAME"
echo "  Alias: $KEY_ALIAS"
echo "  Algorithm: $KEY_ALGORITHM"
echo "  Key Size: $KEY_SIZE bits"
echo "  Validity: $VALIDITY_DAYS days (~27 years)"
echo ""
echo "📝 NEXT STEPS:"
echo ""
echo "1. BACKUP this keystore file immediately!"
echo "   - Store it in multiple secure locations"
echo "   - Use encrypted storage (e.g., password manager, encrypted USB)"
echo ""
echo "2. Set environment variables (or add to local.properties):"
echo "   export TRAINVOC_KEYSTORE_PATH=\"\$(pwd)/$KEYSTORE_NAME\""
echo "   export TRAINVOC_KEYSTORE_PASSWORD=\"your_keystore_password\""
echo "   export TRAINVOC_KEY_ALIAS=\"$KEY_ALIAS\""
echo "   export TRAINVOC_KEY_PASSWORD=\"your_key_password\""
echo ""
echo "3. Test the release build:"
echo "   ./gradlew bundleRelease"
echo ""
echo "4. NEVER commit this keystore to git!"
echo "   (Already added to .gitignore)"
echo ""
echo "⚠️  CRITICAL: If you lose this keystore, you will NOT be able to update"
echo "   your app on Google Play Store. Keep it safe!"
echo ""
