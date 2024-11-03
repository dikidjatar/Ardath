package com.djatar.ardath.core.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.djatar.ardath.R
import com.djatar.ardath.ui.theme.ArdathTheme

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = null
        )
    }
}

@Composable
fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = null
        )
    }
}

@Composable
fun DeleteButton(enabled: Boolean = true, onClick: () -> Unit) {
    IconButton(enabled = enabled, onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.DeleteOutline,
            contentDescription = null
        )
    }
}

@Composable
fun ConfirmButton(
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    onClick: () -> Unit
) {
    TextButton(
        enabled = enabled,
        colors = colors,
        onClick = onClick,
    ) {
        Text(text = stringResource(R.string.confirm),)
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmButtonPreview() {
    ArdathTheme {
        ConfirmButton {  }
    }
}

@Composable
fun DismissButton(enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(enabled = enabled, onClick = onClick) {
        Text(
            text = stringResource(R.string.cancel)
        )
    }
}

