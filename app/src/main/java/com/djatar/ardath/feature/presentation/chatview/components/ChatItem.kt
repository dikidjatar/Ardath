package com.djatar.ardath.feature.presentation.chatview.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djatar.ardath.core.presentation.components.placeHolderShimmer
import com.djatar.ardath.feature.domain.models.Chat
import com.djatar.ardath.feature.presentation.utils.getColor
import com.djatar.ardath.feature.presentation.utils.getDate
import com.djatar.ardath.ui.theme.ArdathTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
    chat: Chat,
    isLoading: Boolean = false,
    selectionState: MutableState<Boolean>,
    selectedChatState: SnapshotStateList<Chat>,
    onClick: (Chat) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLongClick: (Chat) -> Unit = {}
) {
    var isSelected by remember { mutableStateOf(false) }
    LaunchedEffect(selectionState.value, selectedChatState.size) {
        isSelected = if (!selectionState.value) false else {
            selectedChatState.find { it.id == chat.id } != null
        }
    }

    ListItem(
        modifier = Modifier
            .combinedClickable(
                enabled = !isLoading,
                onClick = { onClick(chat) },
                onLongClick = { onLongClick(chat) }
            )
            .padding(vertical = 8.dp),
        headlineContent = {
            if (!isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = chat.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.W400
                        )
                    )
                    Text(
                        text = chat.timestamp.getDate("dd/mm/y"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        supportingContent = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeHolderShimmer(visible = isLoading),
                text = chat.lastMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingContent = {
            val char = if (chat.title.isNotEmpty()) chat.title.uppercase().first() else 'A'
            Box(modifier = Modifier.size(50.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .combinedClickable(enabled = !isLoading, onClick = onProfileClick)
                        .background(char.getColor())
                        .fillMaxSize()
                        .placeHolderShimmer(visible = isLoading),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                AnimatedVisibility(
                    visible = selectionState.value && isSelected,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    CircularCheckbox(
                        isSelected = true
                    )
                }
            }
        }
    )
}

@Composable
fun ChatItemLoader() {
    ChatItem(
        isLoading = true,
        chat = Chat(lastMessage = "last message"),
        selectionState = remember { mutableStateOf(false) },
        selectedChatState = remember { mutableStateListOf() }
    )
}

@Composable
fun CircularCheckbox(
    isSelected: Boolean,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .then(
                if (onClick != null) Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(22.dp) /* The size of the checkbox circle */
                .clip(CircleShape)
                .border(1.3.dp, Color.White, CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(
                        alpha = 0.2f
                    )
                )
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                modifier = Modifier
                    .size(22.dp) /* The appropriate checkbox size */
                    .clip(CircleShape),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = Color.Transparent
                )
            )
        }
    }
}

@Preview
@Composable
private fun ChatItemPreview() {
    ArdathTheme {
        val selectionState = remember { mutableStateOf(true) }
        val selectedChatState = remember { mutableStateListOf<Chat>() }
        ChatItem(
            selectionState = selectionState,
            isLoading = false,
            chat = Chat(title = "Chat Item", lastMessage = "last messages chat item".repeat(2)),
            selectedChatState = selectedChatState
        )
    }
}