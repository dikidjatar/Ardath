package com.djatar.ardath.feature.domain.models

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderImage: String? = null,
    val text: String? = "",
    val timestamp: Long = 0L,
    val imageUrl: String? = null
) {

    fun toMap(): Map<String, Any> {
        return mapOf(
            "senderId" to senderId,
            "senderName" to senderName,
            "senderImage" to senderImage.toString(),
            "text" to text.toString(),
            "timestamp" to timestamp,
            "imageUrl" to imageUrl.toString()
        )
    }
}
