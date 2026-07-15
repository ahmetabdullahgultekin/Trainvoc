package com.gultekinahmetabdullah.trainvoc.auth

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Regression tests for issue #103: CI-built APKs have no google-services.json,
 * so the google-services plugin is skipped and the default FirebaseApp is never
 * initialized. FirebaseAuthRepository must construct and operate gracefully in
 * that state instead of crashing the app during Hilt graph creation.
 *
 * Runs under Robolectric without initializing FirebaseApp, which reproduces the
 * exact production state of a CI-built APK: FirebaseAuth.getInstance() throws
 * IllegalStateException("Default FirebaseApp is not initialized").
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FirebaseAuthRepositoryNoFirebaseTest {

    @Test
    fun `constructing repository without FirebaseApp does not throw`() {
        FirebaseAuthRepository()
    }

    @Test
    fun `getCurrentUser returns null without FirebaseApp`() {
        assertNull(FirebaseAuthRepository().getCurrentUser())
    }

    @Test
    fun `currentUser flow emits null without FirebaseApp`() = runTest {
        FirebaseAuthRepository().currentUser.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `email sign-in returns Error without FirebaseApp`() = runTest {
        val result = FirebaseAuthRepository()
            .signInWithEmailAndPassword("user@example.com", "password")
        assertTrue(result is FirebaseAuthResult.Error)
    }

    @Test
    fun `registration returns Error without FirebaseApp`() = runTest {
        val result = FirebaseAuthRepository()
            .createUserWithEmailAndPassword("user@example.com", "password")
        assertTrue(result is FirebaseAuthResult.Error)
    }

    @Test
    fun `google sign-in returns Error without FirebaseApp`() = runTest {
        val result = FirebaseAuthRepository().signInWithGoogle("fake-google-id-token")
        assertTrue(result is FirebaseAuthResult.Error)
    }

    @Test
    fun `password reset returns Error without FirebaseApp`() = runTest {
        val result = FirebaseAuthRepository().sendPasswordResetEmail("user@example.com")
        assertTrue(result is FirebaseAuthResult.Error)
    }

    @Test
    fun `signOut is a no-op without FirebaseApp`() {
        FirebaseAuthRepository().signOut()
    }

    @Test
    fun `getIdToken returns null without FirebaseApp`() = runTest {
        assertNull(FirebaseAuthRepository().getIdToken())
    }
}
