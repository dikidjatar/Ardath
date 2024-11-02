package com.djatar.ardath.feature.presentation.chatview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.djatar.ardath.ui.theme.ArdathTheme
import com.djatar.ardath.utils.getColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserItem(
    name: String,
    status: String,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {

    ListItem(
        modifier = Modifier.combinedClickable(
            enabled = enabled,
            onClick = onClick,
            onLongClick = onLongClick
        ),
        headlineContent = { Text(text = name) },
        supportingContent = { Text(text = status) },
        leadingContent = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(name.first().getColor())
                    .size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().uppercase(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun UserItemPreview() {
    ArdathTheme {
        UserItem("User Name", "online")
    }
}