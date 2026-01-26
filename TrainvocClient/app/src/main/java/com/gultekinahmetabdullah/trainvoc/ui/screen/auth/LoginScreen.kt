package com.gultekinahmetabdullah.trainvoc.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.auth.AuthState
import com.gultekinahmetabdullah.trainvoc.ui.components.ButtonLoader
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.AuthViewModel

/**
 * Login screen for user authentication.
 * Uses Firebase Authentication with email/password.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onSkipLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val passwordResetSent by viewModel.passwordResetSent.collectAsState()
    val passwordResetError by viewModel.passwordResetError.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("animations/anime_typing.json")
    )

    // Navigate on successful login
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated || authState is AuthState.AuthenticatedOffline) {
            onLoginSuccess()
        }
    }

    // Handle password reset sent
    LaunchedEffect(passwordResetSent) {
        if (passwordResetSent) {
            showForgotPasswordDialog = false
            viewModel.clearPasswordResetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.mediumLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Animation
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        Text(
            text = stringResource(R.string.welcome_back),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Spacing.extraSmall))

        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.large))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.clearLoginError()
            },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.clearLoginError()
            },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.login(email, password)
                }
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (passwordVisible) {
                            stringResource(R.string.hide_password)
                        } else {
                            stringResource(R.string.show_password)
                        }
                    )
                }
            },
            enabled = !isLoading
        )

        // Forgot password link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    forgotPasswordEmail = email
                    showForgotPasswordDialog = true
                }
            ) {
                Text(
                    stringResource(R.string.forgot_password),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Error message
        if (loginError != null) {
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = loginError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Login button
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                ButtonLoader(modifier = Modifier.size(20.dp))
            } else {
                Text(
                    stringResource(R.string.login),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Register link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.no_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.create_account))
            }
        }

        Spacer(modifier = Modifier.height(Spacing.medium))

        // Skip login option
        TextButton(onClick = onSkipLogin) {
            Text(
                stringResource(R.string.skip_login),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            email = forgotPasswordEmail,
            onEmailChange = { forgotPasswordEmail = it },
            onDismiss = {
                showForgotPasswordDialog = false
                viewModel.clearPasswordResetState()
            },
            onSendResetEmail = {
                viewModel.sendPasswordResetEmail(forgotPasswordEmail)
            },
            isLoading = isLoading,
            error = passwordResetError
        )
    }
}

/**
 * Forgot password dialog.
 */
@Composable
private fun ForgotPasswordDialog(
    email: String,
    onEmailChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSendResetEmail: () -> Unit,
    isLoading: Boolean,
    error: String?
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.forgot_password_title))
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.forgot_password_description),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(Spacing.medium))

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text(stringResource(R.string.email)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSendResetEmail() }
                    ),
                    enabled = !isLoading
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(Spacing.small))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSendResetEmail,
                enabled = !isLoading && email.isNotBlank()
            ) {
                if (isLoading) {
                    ButtonLoader(modifier = Modifier.size(16.dp))
                } else {
                    Text(stringResource(R.string.send_reset_email))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
