package com.djatar.ardath.feature.presentation.chatview.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyChat(text: String = "Empty") {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        text = text,
        textAlign = TextAlign.Center
    )
}