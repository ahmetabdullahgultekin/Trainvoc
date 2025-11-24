package com.gultekinahmetabdullah.trainvoc.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import java.io.File

/**
 * Security Utilities for detecting potentially unsafe environments
 *
 * Provides methods to detect:
 * - Root access
 * - Emulator environments
 * - Debug mode
 * - USB debugging
 * - Developer options
 *
 * Usage:
 * ```kotlin
 * if (SecurityUtils.isDeviceRooted()) {
 *     // Handle rooted device (show warning, disable features, etc.)
 * }
 *
 * if (SecurityUtils.isRunningOnEmulator()) {
 *     // Handle emulator environment
 * }
 * ```
 *
 * Note: These checks are not foolproof and can be bypassed by sophisticated attackers.
 * They serve as a first line of defense and deterrent for casual tampering.
 */
object SecurityUtils {

    /**
     * Check if the device is rooted
     *
     * Detection methods:
     * 1. Check for su binary in common paths
     * 2. Check for root management apps
     * 3. Check for test-keys build
     * 4. Check for dangerous props
     * 5. Check for RW system partition
     *
     * @return true if device appears to be rooted
     */
    fun isDeviceRooted(): Boolean {
        return checkSuBinary() ||
                checkRootManagementApps() ||
                checkTestKeys() ||
                checkDangerousProps() ||
                checkRWPaths()
    }

    /**
     * Check for su binary in common locations
     */
    private fun checkSuBinary(): Boolean {
        val suPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )

        return suPaths.any { path ->
            try {
                File(path).exists()
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Check for root management apps
     */
    private fun checkRootManagementApps(): Boolean {
        val rootApps = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk"
        )

        return rootApps.any { packageName ->
            try {
                Class.forName(packageName)
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }

    /**
     * Check for test-keys (indicates custom ROM or rooted device)
     */
    private fun checkTestKeys(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Check for dangerous system properties
     */
    private fun checkDangerousProps(): Boolean {
        val dangerousProps = mapOf(
            "[ro.debuggable]" to "[1]",
            "[ro.secure]" to "[0]"
        )

        return try {
            val process = Runtime.getRuntime().exec("getprop")
            val bufferedReader = process.inputStream.bufferedReader()
            val properties = bufferedReader.use { it.readText() }

            dangerousProps.any { (key, value) ->
                properties.contains(key) && properties.contains(value)
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check for read-write system paths (should be read-only on non-rooted devices)
     */
    private fun checkRWPaths(): Boolean {
        val paths = arrayOf("/system", "/system/bin", "/system/sbin", "/system/xbin", "/vendor/bin", "/sbin", "/etc")

        return paths.any { path ->
            try {
                val file = File(path)
                file.canWrite()
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Check if running on an emulator
     *
     * Detection methods:
     * 1. Check hardware characteristics
     * 2. Check build properties
     * 3. Check for emulator-specific files
     * 4. Check device identifiers
     *
     * @return true if running on an emulator
     */
    fun isRunningOnEmulator(): Boolean {
        return checkEmulatorBuild() ||
                checkEmulatorHardware() ||
                checkEmulatorFiles() ||
                checkOperatorName()
    }

    /**
     * Check build properties for emulator indicators
     */
    private fun checkEmulatorBuild(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
                "google_sdk" == Build.PRODUCT)
    }

    /**
     * Check hardware characteristics
     */
    private fun checkEmulatorHardware(): Boolean {
        return (Build.BOARD == "unknown" ||
                Build.BOARD == "goldfish" ||
                Build.HARDWARE == "goldfish" ||
                Build.HARDWARE == "ranchu" ||
                Build.HARDWARE == "vbox86")
    }

    /**
     * Check for emulator-specific files
     */
    private fun checkEmulatorFiles(): Boolean {
        val emulatorFiles = arrayOf(
            "/dev/socket/qemud",
            "/dev/qemu_pipe",
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
        )

        return emulatorFiles.any { file ->
            File(file).exists()
        }
    }

    /**
     * Check operator name (Android emulator uses "Android")
     */
    private fun checkOperatorName(): Boolean {
        return Build.BRAND.lowercase().contains("generic") ||
                Build.DEVICE.lowercase().contains("generic")
    }

    /**
     * Check if app is in debug mode
     *
     * @param context Application context
     * @return true if app is debuggable
     */
    fun isDebuggable(context: Context): Boolean {
        return try {
            val appInfo = context.applicationInfo
            (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if USB debugging is enabled
     *
     * @param context Application context
     * @return true if USB debugging is enabled
     */
    fun isUsbDebuggingEnabled(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if developer options are enabled
     *
     * @param context Application context
     * @return true if developer options are enabled
     */
    fun isDeveloperModeEnabled(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if app signature is valid (anti-tampering)
     *
     * This should be called with your actual release signature
     * to detect if the APK has been repackaged.
     *
     * @param context Application context
     * @param expectedSignature Expected signature hash (SHA-256)
     * @return true if signature matches
     */
    @Suppress("DEPRECATION")
    fun isAppSignatureValid(context: Context, expectedSignature: String): Boolean {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                packageInfo.signatures
            }

            signatures?.firstOrNull()?.let { signature ->
                val md = java.security.MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signature.toByteArray())
                val hexString = digest.joinToString("") { "%02x".format(it) }
                hexString == expectedSignature.lowercase()
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Comprehensive security check
     *
     * Performs all security checks and returns a summary.
     *
     * @param context Application context
     * @return SecurityCheckResult with all check results
     */
    fun performSecurityCheck(context: Context): SecurityCheckResult {
        return SecurityCheckResult(
            isRooted = isDeviceRooted(),
            isEmulator = isRunningOnEmulator(),
            isDebuggable = isDebuggable(context),
            isUsbDebuggingEnabled = isUsbDebuggingEnabled(context),
            isDeveloperModeEnabled = isDeveloperModeEnabled(context)
        )
    }

    /**
     * Data class holding security check results
     */
    data class SecurityCheckResult(
        val isRooted: Boolean,
        val isEmulator: Boolean,
        val isDebuggable: Boolean,
        val isUsbDebuggingEnabled: Boolean,
        val isDeveloperModeEnabled: Boolean
    ) {
        /**
         * Check if any security risks are detected
         */
        fun hasSecurityRisks(): Boolean {
            return isRooted || (isDebuggable && !isEmulator)
        }

        /**
         * Get list of detected risks
         */
        fun getDetectedRisks(): List<String> {
            val risks = mutableListOf<String>()
            if (isRooted) risks.add("Device is rooted")
            if (isEmulator) risks.add("Running on emulator")
            if (isDebuggable) risks.add("App is debuggable")
            if (isUsbDebuggingEnabled) risks.add("USB debugging enabled")
            if (isDeveloperModeEnabled) risks.add("Developer mode enabled")
            return risks
        }
    }
}
