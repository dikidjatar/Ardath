package com.djatar.ardath.feature.domain.models

data class Chat(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
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