package com.djatar.ardath.core

import com.djatar.ardath.feature.domain.models.Chat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

fun FirebaseDatabase.registerChat(
    currentUser: FirebaseUser?,
    otherUserId: String,
    chatId: String,
    title: String,
    lastMessage: String
) {
    if (currentUser == null) return

    val createdAt = System.currentTimeMillis()
    val currentUserChat = Chat(
        id = chatId,
        userId = otherUserId,
        title = title,
        lastMessage = lastMessage,
        createdAt = createdAt
    )
    val otherUserChat = Chat(
        id = chatId,
        userId = currentUser.uid,
        title = currentUser.displayName ?: "",
        lastMessage = lastMessage,
        createdAt = createdAt
    )

    this.reference.child("chats").apply {
        child(currentUser.uid).child(chatId).setValue(currentUserChat)
        child(otherUserId).child(chatId).setValue(otherUserChat)
    }
}

fun FirebaseDatabase.registerToUserChats(
    currentUserId: String,
    otherUserId: String,
    chatId: String
) {
    this.reference.child("userChats").apply {
        child(currentUserId).child(chatId).setValue(true)
        child(otherUserId).child(chatId).setValue(true)
    }
}