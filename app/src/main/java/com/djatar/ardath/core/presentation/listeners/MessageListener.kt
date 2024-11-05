package com.djatar.ardath.core.presentation.listeners

import com.djatar.ardath.ArdathApp.Companion.context
import com.djatar.ardath.R
import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.Message
import com.djatar.ardath.feature.domain.models.MessageStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.SendChannel

class MessageListener(
    private val databaseRef: DatabaseReference,
    private val chatId: String,
    private val currentUserId: String,
    private val channel: SendChannel<Resource<List<Message>>>
) : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        val messageList = mutableListOf<Message>()
        val updates = hashMapOf<String, Any>()

        for (messageSnapshot in snapshot.children) {
            val message = messageSnapshot.getValue(Message::class.java)
            if (message != null) {
                messageList.add(message)

                if (message.senderId != currentUserId) {
                    if (message.status != MessageStatus.READ.name) {
                        val updateStatus = hashMapOf<String, Any>(
                            "/messages/$chatId/${message.senderId}/${message.id}/status" to MessageStatus.READ.name
                        )
                        updates.putAll(updateStatus)
                    }
                }
            }
        }
        channel.trySend(Resource.Success(messageList))
        if (updates.isNotEmpty()) {
            databaseRef.updateChildren(updates)
        }
    }

    override fun onCancelled(error: DatabaseError) {
        channel.trySend(Resource.Error(context.getString(R.string.error_listen_message)))
    }
}