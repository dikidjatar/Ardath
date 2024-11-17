package com.djatar.ardath.feature.domain.models

import androidx.compose.runtime.Stable

@Stable
sealed class MessageItem {
    abstract val key: String

    @Stable
    data class Header(
        override val key: String,
        val text: String
    ) : MessageItem()

    @Stable
    data class MessageViewItem(
        override val key: String,
        val message: Message
    ) : MessageItem()
}