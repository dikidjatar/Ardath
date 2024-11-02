package com.djatar.ardath.feature.presentation.chatview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djatar.ardath.core.presentation.components.placeHolderShimmer
import com.djatar.ardath.ui.theme.ArdathTheme
import com.djatar.ardath.utils.getColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
    title: String,
    lastMessage: String,
    isLoading: Boolean = false,
    selectionState: MutableState<Boolean> = mutableStateOf(false),
    onClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier.combinedClickable(
            enabled = !isLoading,
            onClick = onClick,
            onLongClick = onLongClick
        ).padding(vertical = 8.dp),
        headlineContent = {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.W400,
                    )
                )
            }
        },
        supportingContent = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeHolderShimmer(visible = isLoading),
                text = lastMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingContent = {
            val char = if (title.isNotEmpty()) title.uppercase().first() else 'A'
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .combinedClickable(enabled = !isLoading, onClick = onProfileClick)
                    .background(char.getColor())
                    .size(50.dp)
                    .placeHolderShimmer(visible = isLoading),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }
    )
}

@Composable
fun ChatItemLoader() {
    ChatItem(isLoading = true, title = "", lastMessage = "last message")
}

@Preview
@Composable
private fun ChatItemPreview() {
    ArdathTheme {
        ChatItem(isLoading = false, title =  "Chat Item", lastMessage =  "last messages chat item")
    }
}