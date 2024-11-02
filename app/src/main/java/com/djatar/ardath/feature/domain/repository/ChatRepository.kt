package com.djatar.ardath.feature.domain.repository

import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.Chat
import com.djatar.ardath.feature.domain.models.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun listenForChats(
        batchSize: Int,
        lastVisibleChatKey: String?
    ) : Flow<Resource<List<Chat>>>

    fun getChatId() : String

    fun hasChat(otherUserId: String) : Flow<String?>

    fun sendMessage(
        otherUserId: String,
        chatId: String,
        chatTitle: String,
        messageText: String?,
        imageUrl: String? = null
    )

    fun listenForMessages(chatId: String) : Flow<Resource<List<Message>>>
}