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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.auth.AuthState
import com.gultekinahmetabdullah.trainvoc.ui.components.ButtonLoader
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.AuthViewModel

/**
 * Registration screen for new users.
 * Uses Firebase Authentication with email/password.
 * Display name is optional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val registerError by viewModel.registerError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Navigate on successful registration
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated || authState is AuthState.AuthenticatedOffline) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_account)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = Spacing.mediumLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(Spacing.large))

            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.extraSmall))

            Text(
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            // Display Name field (optional)
            OutlinedTextField(
                value = displayName,
                onValueChange = {
                    displayName = it
                    viewModel.clearRegisterError()
                },
                label = { Text(stringResource(R.string.display_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !isLoading,
                supportingText = {
                    Text(stringResource(R.string.display_name_hint))
                }
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearRegisterError()
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
                    viewModel.clearRegisterError()
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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
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
                enabled = !isLoading,
                supportingText = {
                    Text(stringResource(R.string.password_hint))
                }
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    viewModel.clearRegisterError()
                },
                label = { Text(stringResource(R.string.confirm_password)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                visualTransformation = if (confirmPasswordVisible) {
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
                        viewModel.register(email, password, confirmPassword, displayName)
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (confirmPasswordVisible) {
                                stringResource(R.string.hide_password)
                            } else {
                                stringResource(R.string.show_password)
                            }
                        )
                    }
                },
                enabled = !isLoading,
                isError = confirmPassword.isNotEmpty() && password != confirmPassword
            )

            // Error message
            if (registerError != null) {
                Spacer(modifier = Modifier.height(Spacing.small))
                Text(
                    text = registerError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            // Register button
            Button(
                onClick = { viewModel.register(email, password, confirmPassword, displayName) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                enabled = !isLoading &&
                        email.isNotBlank() &&
                        password.isNotBlank() &&
                        confirmPassword.isNotBlank()
            ) {
                if (isLoading) {
                    ButtonLoader(modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        stringResource(R.string.create_account),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // Login link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.already_have_account),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateBack) {
                    Text(stringResource(R.string.login))
                }
            }

            Spacer(modifier = Modifier.height(Spacing.large))
        }
    }
}
