package com.djatar.ardath.feature.domain.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "title" to title,
            "lastMessage" to lastMessage,
            "timestamp" to timestamp
        )
    }
}