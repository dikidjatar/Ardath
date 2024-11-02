package com.djatar.ardath.feature.presentation.auth.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AuthPageTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 3
    )
}