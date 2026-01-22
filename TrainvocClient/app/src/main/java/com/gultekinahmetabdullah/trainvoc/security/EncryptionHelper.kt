package com.gultekinahmetabdullah.trainvoc.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encryption helper for securing backup files using AES-256-GCM
 *
 * Features:
 * - AES-256-GCM encryption for authenticated encryption
 * - Keys stored in Android Keystore (hardware-backed when available)
 * - Unique IV for each encryption operation
 * - File format: [IV (12 bytes)][Encrypted Data][Auth Tag (16 bytes)]
 *
 * Security:
 * - GCM mode provides both confidentiality and authenticity
 * - 96-bit IV (recommended for GCM)
 * - 128-bit authentication tag
 * - Keys never leave Keystore
 *
 * Usage:
 * ```kotlin
 * val helper = EncryptionHelper(context)
 *
 * // Encrypt backup file
 * helper.encryptFile(File("backup.json"), File("backup.enc"))
 *
 * // Decrypt backup file
 * helper.decryptFile(File("backup.enc"), File("backup.json"))
 * ```
 */
class EncryptionHelper(private val context: Context) {

    companion object {
        private const val TAG = "EncryptionHelper"
        private const val KEY_ALIAS = "trainvoc_backup_encryption_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE_BYTES = 12 // 96 bits (recommended for GCM)
        private const val TAG_SIZE_BITS = 128 // 128-bit authentication tag
        private const val BUFFER_SIZE = 8192
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    init {
        // Create encryption key if it doesn't exist
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            createEncryptionKey()
        }
    }

    /**
     * Create AES-256-GCM key in Android Keystore
     */
    private fun createEncryptionKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
        } else {
            // For API < 23, use a simpler approach
            throw UnsupportedOperationException("Encryption requires Android M (API 23) or higher")
        }

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    /**
     * Get the SecretKey from Android Keystore
     */
    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    /**
     * Encrypt a file using AES-256-GCM
     *
     * @param inputFile File to encrypt
     * @param outputFile Destination for encrypted file
     * @return true if encryption successful, false otherwise
     */
    fun encryptFile(inputFile: File, outputFile: File): Boolean {
        return try {
            if (!inputFile.exists()) {
                throw IllegalArgumentException("Input file does not exist: ${inputFile.absolutePath}")
            }

            // Generate random IV
            val iv = ByteArray(IV_SIZE_BYTES)
            SecureRandom().nextBytes(iv)

            // Get cipher instance
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(TAG_SIZE_BITS, iv)

            // Initialize cipher for encryption
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), gcmSpec)

            // Write IV to output file first
            FileOutputStream(outputFile).use { outputStream ->
                outputStream.write(iv)

                // Encrypt and write data
                FileInputStream(inputFile).use { inputStream ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        val encryptedChunk = cipher.update(buffer, 0, bytesRead)
                        if (encryptedChunk != null) {
                            outputStream.write(encryptedChunk)
                        }
                    }

                    // Write final block (includes authentication tag)
                    val finalBlock = cipher.doFinal()
                    if (finalBlock != null) {
                        outputStream.write(finalBlock)
                    }
                }
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error encrypting file: ${inputFile.name}", e)
            // Clean up output file on failure
            if (outputFile.exists()) {
                outputFile.delete()
            }
            false
        }
    }

    /**
     * Decrypt a file encrypted with AES-256-GCM
     *
     * @param inputFile Encrypted file
     * @param outputFile Destination for decrypted file
     * @return true if decryption successful, false otherwise
     * @throws SecurityException if authentication fails
     */
    fun decryptFile(inputFile: File, outputFile: File): Boolean {
        return try {
            if (!inputFile.exists()) {
                throw IllegalArgumentException("Input file does not exist: ${inputFile.absolutePath}")
            }

            FileInputStream(inputFile).use { inputStream ->
                // Read IV from file
                val iv = ByteArray(IV_SIZE_BYTES)
                val ivBytesRead = inputStream.read(iv)
                if (ivBytesRead != IV_SIZE_BYTES) {
                    throw IllegalArgumentException("Invalid encrypted file format: IV missing or incomplete")
                }

                // Get cipher instance
                val cipher = Cipher.getInstance(TRANSFORMATION)
                val gcmSpec = GCMParameterSpec(TAG_SIZE_BITS, iv)

                // Initialize cipher for decryption
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), gcmSpec)

                // Decrypt and write data
                FileOutputStream(outputFile).use { outputStream ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        val decryptedChunk = cipher.update(buffer, 0, bytesRead)
                        if (decryptedChunk != null) {
                            outputStream.write(decryptedChunk)
                        }
                    }

                    // Write final block (will fail if authentication tag is invalid)
                    try {
                        val finalBlock = cipher.doFinal()
                        if (finalBlock != null) {
                            outputStream.write(finalBlock)
                        }
                    } catch (e: Exception) {
                        // Authentication failed - file was tampered with
                        outputFile.delete()
                        throw SecurityException("File authentication failed: file may have been tampered with", e)
                    }
                }
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting file: ${inputFile.name}", e)
            // Clean up output file on failure
            if (outputFile.exists()) {
                outputFile.delete()
            }
            false
        }
    }

    /**
     * Encrypt string data
     *
     * @param data String to encrypt
     * @return Encrypted data as Base64 string with IV prepended
     */
    fun encryptString(data: String): String {
        val iv = ByteArray(IV_SIZE_BYTES)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmSpec = GCMParameterSpec(TAG_SIZE_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), gcmSpec)

        val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

        // Combine IV + encrypted data
        val combined = iv + encryptedData

        return android.util.Base64.encodeToString(combined, android.util.Base64.NO_WRAP)
    }

    /**
     * Decrypt string data
     *
     * @param encryptedData Base64 encoded encrypted string with IV
     * @return Decrypted string
     * @throws SecurityException if authentication fails
     */
    fun decryptString(encryptedData: String): String {
        val combined = android.util.Base64.decode(encryptedData, android.util.Base64.NO_WRAP)

        // Extract IV and encrypted data
        val iv = combined.sliceArray(0 until IV_SIZE_BYTES)
        val encrypted = combined.sliceArray(IV_SIZE_BYTES until combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmSpec = GCMParameterSpec(TAG_SIZE_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), gcmSpec)

        val decryptedData = cipher.doFinal(encrypted)

        return String(decryptedData, Charsets.UTF_8)
    }

    /**
     * Check if encryption key exists in Keystore
     */
    fun hasEncryptionKey(): Boolean {
        return keyStore.containsAlias(KEY_ALIAS)
    }

    /**
     * Delete encryption key from Keystore
     * WARNING: This will make all encrypted files unrecoverable!
     */
    fun deleteEncryptionKey() {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.deleteEntry(KEY_ALIAS)
        }
    }
}

/**
 * Result wrapper for encryption/decryption operations
 */
sealed class EncryptionResult {
    data class Success(val file: File) : EncryptionResult()
    data class Failure(val error: String, val exception: Exception? = null) : EncryptionResult()
}

