package com.gultekinahmetabdullah.trainvoc.ui.screen.auth

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.viewmodel.AuthViewModel

/**
 * Watches for session expiry (#193) and, when detected, shows a blocking dialog
 * that routes the user back to the login screen.
 *
 * Place this once near the top of the authenticated navigation host. Call
 * [AuthViewModel.validateSession] on resume / when entering protected surfaces to
 * trigger the check; if the Firebase session can no longer refresh its token the
 * view-model flips `sessionExpired` and this handler takes over.
 *
 * @param onReauthenticate invoked when the user acknowledges — navigate to login.
 */
@Composable
fun SessionExpiredHandler(
    onReauthenticate: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val sessionExpired by viewModel.sessionExpired.collectAsState()

    if (sessionExpired) {
        AlertDialog(
            onDismissRequest = { /* must act — non-dismissable */ },
            title = { Text(stringResource(R.string.session_expired_title)) },
            text = { Text(stringResource(R.string.session_expired_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearSessionExpired()
                        onReauthenticate()
                    }
                ) {
                    Text(stringResource(R.string.sign_in_again))
                }
            }
        )
    }
}
