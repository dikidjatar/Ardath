package com.djatar.ardath

import android.annotation.SuppressLint
import android.util.Log
import com.djatar.ardath.feature.presentation.utils.CHAT_USER_ID
import com.djatar.ardath.feature.presentation.utils.IS_CHAT_ON
import com.djatar.ardath.feature.presentation.utils.NotificationUtil
import com.djatar.ardath.feature.presentation.utils.PreferenceUtil.getBoolean
import com.djatar.ardath.feature.presentation.utils.PreferenceUtil.getString
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "From: ${message.from}, notification: ${message.senderId.toString()}")

        Firebase.auth.currentUser?.let { user ->
            val userId = message.data["userId"]

            message.notification?.let { msg ->
                if (user.uid != userId) {
                    if (!IS_CHAT_ON.getBoolean() && CHAT_USER_ID.getString() != userId) {
                        val notificationId = userId.hashCode()
                        NotificationUtil.notifyForMessages(
                            userName = msg.title,
                            messageText = msg.body,
                            imageUrl = msg.imageUrl,
                            notificationId = notificationId
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "FirebaseMessageService"
    }
}