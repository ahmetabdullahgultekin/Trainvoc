package com.gultekinahmetabdullah.trainvoc.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.AuthViewModel

/**
 * A banner prompting the user to verify their email (#191).
 *
 * Renders nothing when there is no signed-in user, or when the email is already
 * verified. Otherwise it shows a "verify your email" card with a "resend"
 * action wired to [AuthViewModel.sendEmailVerification]. Drop this at the top of
 * authenticated surfaces (e.g. Profile / Home).
 */
@Composable
fun EmailVerificationBanner(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val verificationSent by viewModel.emailVerificationSent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Hide entirely if there's no signed-in user or the email is verified.
    val email = viewModel.getCurrentEmail()
    if (email == null || viewModel.isEmailVerified()) {
        return
    }

    // Reset the "sent" flag automatically so the banner returns to its prompt
    // state if the screen is revisited.
    LaunchedEffect(verificationSent) {
        if (verificationSent) {
            // Leave the confirmation visible until the user navigates away;
            // the flag is cleared by clearEmailVerificationSent() on dispose.
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (verificationSent) {
                        Icons.Filled.MarkEmailRead
                    } else {
                        Icons.Outlined.Warning
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = stringResource(R.string.email_not_verified_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(start = Spacing.small)
                )
            }

            Text(
                text = if (verificationSent) {
                    stringResource(R.string.verification_email_sent)
                } else {
                    stringResource(R.string.email_not_verified_message)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(top = Spacing.extraSmall)
            )

            if (!verificationSent) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.sendEmailVerification() },
                        enabled = !isLoading
                    ) {
                        Text(stringResource(R.string.resend_verification_email))
                    }
                }
            }
        }
    }
}
