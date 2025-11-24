# Trainvoc ProGuard Rules
# Optimized for Room, Hilt, Compose, and Kotlin

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*

# ===== Kotlin =====
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ===== Kotlin Coroutines =====
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ===== Room Database =====
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** DATABASE_NAME;
}
-keep class com.gultekinahmetabdullah.trainvoc.database.** { *; }
-keep class com.gultekinahmetabdullah.trainvoc.classes.word.** { *; }

# ===== Hilt/Dagger =====
-dontwarn com.google.errorprone.annotations.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class com.gultekinahmetabdullah.trainvoc.di.** { *; }

# ===== Gson =====
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ===== Jetpack Compose =====
-keep class androidx.compose.** { *; }
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class androidx.compose.** {
    <init>(...);
}
-dontwarn androidx.compose.**

# ===== AndroidX =====
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.work.** { *; }

# ===== Data Classes & Sealed Classes =====
-keep class com.gultekinahmetabdullah.trainvoc.classes.** { *; }
-keepclassmembers class com.gultekinahmetabdullah.trainvoc.classes.** {
    <init>(...);
    <fields>;
}

# ===== Enum Classes =====
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===== Serialization =====
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===== Remove Logging in Release =====
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ===== Optimization =====
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# ===== Security - EncryptedSharedPreferences =====
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# ===== Security - Obfuscation =====
# Remove debug information
-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** w(...);
}

# Obfuscate class names and method names for security
-repackageclasses 'o'

# Remove unused code (reduces APK size and attack surface)
-dontnote **
-dontwarn **

# Additional optimization for security
-mergeinterfacesaggressively
-overloadaggressively

# ===== Security - Prevent Reflection Attacks =====
# Make it harder to reverse engineer by removing metadata
-keepattributes !SourceFile,!LineNumberTable,!InnerClasses,!Signature

# ===== Security - String Encryption =====
# Note: Strings are obfuscated but not encrypted by default
# For critical strings, consider using custom encryption

# ===== Security - WebView Hardening (if WebView is used) =====
-keepclassmembers class * extends android.webkit.WebView {
   public *;
}

# ===== Security - Prevent Tampering =====
# Keep native methods (if any)
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# ===== Security - Keep Security Classes =====
-keep class com.gultekinahmetabdullah.trainvoc.security.** { *; }