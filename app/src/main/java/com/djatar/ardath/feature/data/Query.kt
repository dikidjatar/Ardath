package com.djatar.ardath.feature.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

fun DatabaseReference.queryChats(
    userId: String?,
    batchSize: Int = 10,
    lastVisibleChatKey: String? = null
): Query? {
    if (userId == null) {
        return null
    }
    val limit = if (batchSize <= 0) 10 else batchSize
    val chatRef = child("chats").child(userId)
    return if (lastVisibleChatKey == null) {
        chatRef.limitToFirst(limit)
    } else {
        chatRef.orderByKey().startAfter(lastVisibleChatKey).limitToFirst(limit)
    }
}