package com.djatar.ardath.feature.data.repository

import android.content.Context
import android.util.Log
import com.djatar.ardath.ArdathApp
import com.djatar.ardath.ArdathApp.Companion.applicationScope
import com.djatar.ardath.R
import com.djatar.ardath.core.Resource
import com.djatar.ardath.core.presentation.listeners.ChatListener
import com.djatar.ardath.core.presentation.listeners.MessageListener
import com.djatar.ardath.feature.data.queryChats
import com.djatar.ardath.feature.domain.models.Chat
import com.djatar.ardath.feature.domain.models.Message
import com.djatar.ardath.feature.domain.models.MessageStatus
import com.djatar.ardath.feature.domain.repository.ChatRepository
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

class ChatRepositoryImpl(
    private val context: Context,
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) : ChatRepository {

    private val chats = mutableListOf<Chat>()

    override fun listenForChats(batchSize: Int, lastVisibleChatKey: String?): Flow<Resource<List<Chat>>> {
        return callbackFlow {
            val userId = auth.currentUser?.uid
            val query = database.reference.queryChats(userId, batchSize, lastVisibleChatKey)

            val listener = ChatListener(chats = chats, channel = this)

            query?.addChildEventListener(listener).also {
                query?.get()?.addOnCompleteListener { task ->
                    val hasData = (task.result?.childrenCount ?: 0) > 0
                    if (!hasData) {
                        trySend(Resource.Error(""))
                    }
                }
            }

            awaitClose {
                query?.removeEventListener(listener)
                chats.clear()
            }
        }
    }

    override fun getChatId(): String =
        database.reference.child("chats")
            .child(auth.currentUser?.uid!!).push().key ?: UUID.randomUUID().toString()

    override fun hasChat(otherUserId: String): Flow<String?> {
        return callbackFlow {
            val currentUser = auth.currentUser
            val userChatRef = database.reference.child("userChats")

            val myRef = userChatRef.child(currentUser?.uid!!)
            val otherRef = userChatRef.child(otherUserId)

            myRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val myList = task.result.children.mapNotNull { it.key }.toSet()

                    otherRef.get().addOnCompleteListener { otherTask ->
                        if (otherTask.isSuccessful) {
                            val otherList = otherTask.result.children.mapNotNull { it.key }
                            val commonKey = otherList.firstOrNull { it in myList }
                            trySend(commonKey)
                        } else { trySend(null) }
                    }
                } else { trySend(null) }
            }

            awaitClose()
        }
    }

    override fun sendMessage(
        otherUserId: String,
        chatId: String,
        chatTitle: String,
        messageText: String?,
        imageUrl: String?
    ) {
        if (messageText.isNullOrEmpty()) {
            return
        }

        val currentUserId = auth.currentUser?.uid!!

        val messageId = database.reference.child("messages").push().key
            ?:  UUID.randomUUID().toString()
        val timeStamp = System.currentTimeMillis()
        val message = Message(
            messageId,
            auth.currentUser?.uid ?: "",
            auth.currentUser?.displayName ?: "Unknown",
            null,
            messageText.trim(),
            timeStamp,
            imageUrl,
            status = MessageStatus.PENDING.name
        )
        val currentUserChat = Chat(
            id = chatId,
            userId = otherUserId,
            title = chatTitle,
            lastMessage = message.text.toString(),
            timestamp = timeStamp
        )
        val otherUserChat = Chat(
            id = chatId,
            userId = currentUserId,
            title = auth.currentUser?.displayName ?: "",
            lastMessage = message.text.toString(),
            timestamp = timeStamp
        )

        val messageValues = message.toMap()

        val updates = hashMapOf<String, Any?>(
            "/messages/$chatId/$currentUserId/$messageId" to messageValues,
            "/messages/$chatId/$otherUserId/$messageId" to messageValues,
            "/chats/$currentUserId/$chatId" to currentUserChat.toMap(),
            "/chats/$otherUserId/$chatId" to otherUserChat.toMap(),
            "/userChats/$currentUserId/$chatId" to true,
            "/userChats/$otherUserId/$chatId" to true
        )

        database.reference.updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updateStatus = hashMapOf<String, Any>(
                        "/messages/$chatId/$currentUserId/$messageId/status" to MessageStatus.SENT.name,
                        "/messages/$chatId/$otherUserId/$messageId/status" to MessageStatus.SENT.name
                    )
                    database.reference.updateChildren(updateStatus)
                    postNotificationToUser(
                        currentUserId,
                        chatId,
                        message.senderName,
                        message.text ?: ""
                    )
                } else {
                    Log.e(TAG, "Failed to send message: ", task.exception)
                }
            }
    }

    override fun listenForMessages(chatId: String): Flow<Resource<List<Message>>> {
        return callbackFlow {
            val currentUserId = auth.currentUser!!.uid
            val query = database.getReference("messages").child(chatId).child(currentUserId)
            val listener = MessageListener(
                database.reference,
                chatId,
                currentUserId,
                this
            )
            query.addValueEventListener(listener)
            subscribeForNotification(chatId)
            awaitClose {
                Log.d(TAG, "awaitClose call from listenForMessage")
                query.removeEventListener(listener)
            }
        }
    }

    override fun deleteChat(chats: List<Chat>, onFinish: (Boolean) -> Unit) {
        val currentUserId = auth.currentUser?.uid!!
        val updates = hashMapOf<String, Any?>()

        for (chat in chats) {
            val chatPath = "/chats/${currentUserId}/${chat.id}"
            val messagePath = "/messages/${chat.id}/$currentUserId"
            updates[chatPath] = null
            updates[messagePath] = null
        }

        database.reference.updateChildren(updates)
            .addOnCompleteListener { task ->
                onFinish(task.isSuccessful)
            }
    }

    override fun subscribeForNotification(chatId: String) {
        messaging.subscribeToTopic("chat_$chatId")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Subscribed to topic: chat_$chatId")
                } else {
                    Log.d(TAG, "Failed to subscribe to topic: chat_$chatId")
                }
            }
    }

    override fun postNotificationToUser(
        userId: String,
        chatId: String,
        senderName: String,
        messageText: String
    ) {
        val fcmUrl = "https://fcm.googleapis.com/v1/projects/pedat-community-id/messages:send"
        val jsonBody = JSONObject().apply {
            put("message", JSONObject().apply {
                put("topic", "chat_$chatId")
                put("notification", JSONObject().apply {
                    put("title", senderName)
                    put("body", messageText)
                })
                put("data", JSONObject().apply {
                    put("userId", userId)
                    put("chatId", chatId)
                    put("chatTitle", senderName)
                })
            })
        }
        applicationScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(fcmUrl)
                .addHeader("Authorization", "Bearer ${getAccessToken()}")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Failed to send notification", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Notification sent successfully")
                    } else {
                        Log.w(TAG, "Failed to send notification, CODE: ${response.code}")
                    }
                    response.body?.close()
                }
            })
        }
    }

    override fun getAccessToken(): String {
        val inputStream = ArdathApp.context.resources.openRawResource(R.raw.ardath_key)
        val googleCreds = GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        val token = googleCreds.refreshAccessToken().tokenValue
        Log.d(TAG, "getAccessToken($token)")
        return token
    }

    companion object {
        private const val TAG = "ChatRepository"
    }
}