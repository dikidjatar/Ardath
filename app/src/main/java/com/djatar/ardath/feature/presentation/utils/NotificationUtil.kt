package com.djatar.ardath.feature.presentation.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.djatar.ardath.ArdathApp
import com.djatar.ardath.ArdathApp.Companion.context
import com.djatar.ardath.R

object NotificationUtil {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private const val CHANNEL_ID = "messages"

    fun createNotificationChannel() {
        val name = ArdathApp.context.getString(R.string.channel_name)
        val important = NotificationManager.IMPORTANCE_HIGH
        val descriptionText = ArdathApp.context.getString(R.string.channel_description)
        val channel = NotificationChannel(CHANNEL_ID, name, important).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun notifyForMessages(
        userIcon: IconCompat? = null,
        userName: String?,
        messageText: String?,
        pendingIntent: PendingIntent? = null,
        notificationId: Int
    ) {
        val person = Person.Builder().setIcon(userIcon).setName(userName).build()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(
                NotificationCompat.MessagingStyle(person)
                    .addMessage(
                        NotificationCompat.MessagingStyle.Message(
                            messageText,
                            System.currentTimeMillis(),
                            person
                        )
                    )
            )
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, notification.build())
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}