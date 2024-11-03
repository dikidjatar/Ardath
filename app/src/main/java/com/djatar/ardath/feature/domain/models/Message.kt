package com.djatar.ardath.feature.domain.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderImage: String? = "",
    val text: String? = "",
    val timestamp: Long = 0L,
    val imageUrl: String? = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "senderId" to senderId,
            "senderName" to senderName,
            "senderImage" to senderImage,
            "text" to text,
            "timestamp" to timestamp,
            "imageUrl" to imageUrl
        )
    }
}
