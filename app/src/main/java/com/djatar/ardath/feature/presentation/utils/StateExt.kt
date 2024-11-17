package com.djatar.ardath.feature.presentation.utils

import com.djatar.ardath.feature.domain.models.Message
import com.djatar.ardath.feature.domain.models.MessageItem
import com.djatar.ardath.feature.domain.models.MessageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "StateExt"

suspend fun mapMessageToItem(
    data: List<Message>,
    error: String?,
) = withContext(Dispatchers.IO) {
    val mappedData = mutableListOf<MessageItem>()
    val groupedData = data.groupBy {
        it.timestamp.getDate(
            stringToday = "Today",
            stringYesterday = "Yesterday"
        )
    }

    groupedData.forEach { (date, data) ->
        val header = MessageItem.Header("header_$date", date)

        val groupedMessage = data.map {
            MessageItem.MessageViewItem("message_${it.id}_${it.senderName}", it)
        }
        mappedData.add(header)
        mappedData.addAll(groupedMessage)
    }

    MessageState(
        messages = data,
        mappedMessage = mappedData,
        isLoading = false,
        error = error
    )
}

fun mapMessageToItem(data: List<Message>): List<MessageItem> {
    val groupedData = data.groupBy {
        it.timestamp.getDate(
            stringToday = "Today",
            stringYesterday = "Yesterday"
        )
    }

    return groupedData.flatMap { (date, messages) ->
        listOf(MessageItem.Header("header_$date", date)) +
                messages.map {
                    MessageItem.MessageViewItem("message_${it.id}_${it.senderName}", it)
                }
    }
}