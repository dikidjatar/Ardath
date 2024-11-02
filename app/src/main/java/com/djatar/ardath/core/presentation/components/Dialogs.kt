package com.djatar.ardath.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.djatar.ardath.R
import com.djatar.ardath.ui.theme.ArdathTheme

@Composable
fun ErrorDialog(
    title: String = "Error",
    errorMessage: String = "error occurred",
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = errorMessage) },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.ok))
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
fun ErrorDialog(
    title: String = "Error",
    errorMessage: String = "error occurred",
    visible: Boolean = false,
    onDismissRequest: () -> Unit = {}
) {
    if (visible) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = errorMessage) },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            onDismissRequest = onDismissRequest,
        )
    }
}

@Composable
fun InfoDialog(
    title: String = "Info",
    infoMessage: String = "Info message",
    visible: Boolean = false,
    onDismissRequest: () -> Unit = {}
) {
    if (visible) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = infoMessage) },
            confirmButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            onDismissRequest = onDismissRequest,
        )
    }
}

@Preview
@Composable
private fun ErrorDialogPreview() {
    ArdathTheme {
        ErrorDialog()
    }
}

@Composable
fun LoadingDialog(
    title: String = "Loading",
    visible: Boolean = false,
) {
    if (visible) {
        AlertDialog(
            title = { Text(text = title) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            },
            confirmButton = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun LoadingDialogPreview() {
    ArdathTheme {
        LoadingDialog(title = "Process your login", visible = true)
    }
}