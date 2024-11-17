package com.djatar.ardath.feature.domain.models

import androidx.compose.runtime.Stable
import com.djatar.ardath.feature.presentation.utils.mapMessageToItem

@Stable
data class MessageState(
    val messages: List<Message> = emptyList(),
    val mappedMessage: List<MessageItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {

    fun copyWithMessages(newMessages: List<Message>): MessageState {
        return this.copy(
            messages = newMessages,
            mappedMessage = mapMessageToItem(newMessages),
            isLoading = false
        )
    }
}