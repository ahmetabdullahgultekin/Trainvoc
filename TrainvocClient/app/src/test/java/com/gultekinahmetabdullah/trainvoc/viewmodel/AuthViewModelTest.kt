package com.gultekinahmetabdullah.trainvoc.viewmodel

import com.gultekinahmetabdullah.trainvoc.auth.AuthRepository
import com.gultekinahmetabdullah.trainvoc.auth.AuthResult
import com.gultekinahmetabdullah.trainvoc.auth.AuthState
import com.gultekinahmetabdullah.trainvoc.test.util.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for AuthViewModel — focused on the features added for
 * #192 (Google Sign-In), #191 (email verification) and #193 (session timeout).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        authRepository = mockk(relaxed = true)
        // Provide non-null flows backing the view-model's exposed state.
        every { authRepository.authState } returns MutableStateFlow(AuthState.NotAuthenticated)
        every { authRepository.currentUser } returns MutableStateFlow(null)
        // init {} calls checkAuthState() → checkAuthState() in repo.
        coEvery { authRepository.checkAuthState() } just Runs
        viewModel = AuthViewModel(authRepository)
    }

    // ---- #192 Google Sign-In ----

    @Test
    fun `loginWithGoogle with blank token sets a login error and never calls repository`() = runTest {
        viewModel.loginWithGoogle(null)
        advanceUntilIdle()

        assertTrue(viewModel.loginError.value != null)
        coVerify(exactly = 0) { authRepository.loginWithGoogle(any()) }
    }

    @Test
    fun `loginWithGoogle with valid token delegates to repository`() = runTest {
        coEvery { authRepository.loginWithGoogle("tok") } returns AuthResult.Success(null)

        viewModel.loginWithGoogle("tok")
        advanceUntilIdle()

        coVerify(exactly = 1) { authRepository.loginWithGoogle("tok") }
        assertEquals(null, viewModel.loginError.value)
    }

    @Test
    fun `loginWithGoogle surfaces repository error`() = runTest {
        coEvery { authRepository.loginWithGoogle("tok") } returns AuthResult.Error("boom")

        viewModel.loginWithGoogle("tok")
        advanceUntilIdle()

        assertEquals("boom", viewModel.loginError.value)
    }

    // ---- #191 Email verification ----

    @Test
    fun `sendEmailVerification flips the sent flag on success`() = runTest {
        coEvery { authRepository.sendEmailVerification() } returns AuthResult.EmailVerificationSent

        viewModel.sendEmailVerification()
        advanceUntilIdle()

        assertTrue(viewModel.emailVerificationSent.value)

        viewModel.clearEmailVerificationSent()
        assertFalse(viewModel.emailVerificationSent.value)
    }

    // ---- #193 Session timeout ----

    @Test
    fun `validateSession flags expiry when repository reports invalid`() = runTest {
        coEvery { authRepository.validateSession() } returns false

        viewModel.validateSession()
        advanceUntilIdle()

        assertTrue(viewModel.sessionExpired.value)

        viewModel.clearSessionExpired()
        assertFalse(viewModel.sessionExpired.value)
    }

    @Test
    fun `validateSession does not flag expiry when session is valid`() = runTest {
        coEvery { authRepository.validateSession() } returns true

        viewModel.validateSession()
        advanceUntilIdle()

        assertFalse(viewModel.sessionExpired.value)
    }
}
