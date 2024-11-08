package com.djatar.ardath.feature.domain.repository

import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.Chat
import com.djatar.ardath.feature.domain.models.Message
import kotlinx.coroutines.flow.Flow

/**
 * Interface that defines the contract for the chat repository.
 * This repository handles the business logic related to chat interactions,
 * such as listening for chat data changes, sending messages, and deleting chats from the system.
 */
interface ChatRepository {

    /**
     * Listens for a list of chats in a specified batch size.
     * Produces a flow of [Resource] containing the list of chats.
     *
     * @param batchSize The batch size of chats to retrieve.
     * @param lastVisibleChatKey The last visible chat key for pagination,
     * null if retrieving from the beginning.
     * @return [Flow] emitting a resource containing the list of chats.
     */
    fun listenForChats(
        batchSize: Int,
        lastVisibleChatKey: String?
    ) : Flow<Resource<List<Chat>>>

    /**
     * Generates a new chat ID.
     * This method is used to get a unique ID that will be used when creating a new chat.
     *
     * @return A string representing the chat ID.
     */
    fun getChatId() : String

    /**
     * Checks whether there is an existing chat with another user based on their user ID.
     *
     * @param otherUserId The ID of the other user whose chat needs to be checked.
     * @return [Flow] emitting the chat ID if found, or null if no chat is found.
     */
    fun hasChat(otherUserId: String) : Flow<String?>

    /**
     * Sends a message to an existing or new chat.
     * This method is used to send a text or image message to another user within a chat.
     *
     * @param otherUserId The ID of the other user who will receive the message.
     * @param chatId The ID of the chat where the message will be sent.
     * @param chatTitle The title or topic of the chat.
     * @param messageText The text of the message to be sent (optional).
     * @param imageUrl The URL of the image to be sent in the message (optional).
     */
    fun sendMessage(
        otherUserId: String,
        chatId: String,
        chatTitle: String,
        messageText: String?,
        imageUrl: String? = null
    )

    /**
     * Listens for messages in a chat based on the chat ID.
     * Produces a flow of [Resource] containing the list of messages, updated in real-time.
     *
     * @param chatId The ID of the chat whose messages will be listened to.
     * @return [Flow] emitting a resource containing the list of messages.
     */
    fun listenForMessages(chatId: String) : Flow<Resource<List<Message>>>

    /**
     * Deletes one or more chats from the system.
     * This method deletes the given chats and calls the [onFinish] callback when the process is complete.
     *
     * @param chats The list of chats to be deleted.
     * @param onFinish Callback called after deletion is complete.
     * Returns `true` if successful, `false` if failed.
     */
    fun deleteChat(chats: List<Chat>, onFinish: (Boolean) -> Unit = {})

    fun subscribeForNotification(chatId: String)

    fun postNotificationToUser(
        userId: String,
        chatId: String,
        senderName: String,
        messageText: String
    )

    fun getAccessToken(): String
}