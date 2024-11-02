package com.djatar.ardath.core.presentation.listeners

import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.Chat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking

class ChatListener(
    private val chats: MutableList<Chat>,
    private val channel: SendChannel<Resource<List<Chat>>>
) : ChildEventListener {
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        snapshot.getValue(Chat::class.java)?.let { chat ->
            val index = chats.indexOfFirst { it.id == chat.id }
            if (index == -1) {
                chats.add(chat)
            } else {
                chats[index] = chat
            }
            channel.trySendBlocking(Resource.Success(chats))
        }
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        val updatedChat = snapshot.getValue(Chat::class.java)
        updatedChat?.let { updated ->
            val index = chats.indexOfFirst { it.id == updated.id }
            if (index != -1) {
                chats[index] = updated
                channel.trySendBlocking(Resource.Success(chats))
            }
        }
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        snapshot.getValue(Chat::class.java)?.let { removedChat ->
            chats.removeAll { it.id == removedChat.id }.let { isRemoved ->
                if (isRemoved) {
                    channel.trySendBlocking(Resource.Success(chats))
                }
            }
        }
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        channel.trySendBlocking(Resource.Error(error.message))
    }
}