package com.djatar.ardath.core.presentation.listeners

import com.djatar.ardath.ArdathApp.Companion.context
import com.djatar.ardath.R
import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.SendChannel

class MessageListener(
    private val chatId: String,
    private val channel: SendChannel<Resource<List<Message>>>
) : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        val messageList = mutableListOf<Message>()
        for (messageSnapshot in snapshot.children) {
            val message = messageSnapshot.getValue(Message::class.java)
            if (message != null) {
                messageList.add(message)
            }
        }
        channel.trySend(Resource.Success(messageList))
    }

    override fun onCancelled(error: DatabaseError) {
        channel.trySend(Resource.Error(context.getString(R.string.error_listen_message)))
    }
}