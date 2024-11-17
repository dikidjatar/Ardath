package com.djatar.ardath.feature.domain.models

import androidx.compose.runtime.Stable

@Stable
data class ChatState(
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = false,
    var selectedChatId: String? = null,
    val error: String? = null
)