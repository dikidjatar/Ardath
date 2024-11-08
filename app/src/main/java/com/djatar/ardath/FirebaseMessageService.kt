package com.djatar.ardath

import android.annotation.SuppressLint
import android.util.Log
import com.djatar.ardath.feature.presentation.utils.NotificationUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "From: ${message.from}, notification: ${message.notification.toString()}")
        message.notification?.let { msg ->
            val userId = message.data["userId"]

            Firebase.auth.currentUser?.let { user ->
                if (user.uid != userId) {
//                    val pendingIntent: PendingIntent =
//                        Intent(this, MainActivity::class.java).apply {
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        }.let { notificationIntent ->
//                            PendingIntent.getActivity(
//                                this, 0, notificationIntent,
//                                PendingIntent.FLAG_IMMUTABLE
//                            )
//                        }

                    NotificationUtil.notifyForMessages(
                        title = msg.title,
                        message = msg.body
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "FirebaseMessageService"
    }
}