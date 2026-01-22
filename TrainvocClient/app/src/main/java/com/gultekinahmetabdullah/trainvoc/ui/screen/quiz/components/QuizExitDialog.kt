package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gultekinahmetabdullah.trainvoc.R

/**
 * Exit confirmation dialog for Quiz screen.
 * Extracted from QuizScreen for better organization.
 */
@Composable
fun QuizExitDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.quit_quiz_title)) },
        text = { Text(stringResource(id = R.string.quit_quiz_message)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.no))
            }
        }
    )
}
