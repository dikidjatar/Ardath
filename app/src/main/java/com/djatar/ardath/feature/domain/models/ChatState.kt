package com.djatar.ardath.feature.domain.models

import androidx.compose.runtime.Stable

@Stable
data class ChatState(
    val chats: List<Chat> = emptyList(),
    var messages: MessageState = MessageState(),
    val isLoading: Boolean = false,
    var selectedChatId: String? = null,
    val error: String? = null
)

@Stable
data class MessageState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)