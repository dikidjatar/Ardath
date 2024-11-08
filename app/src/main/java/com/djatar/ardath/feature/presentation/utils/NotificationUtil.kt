package com.djatar.ardath.feature.presentation.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.djatar.ardath.ArdathApp.Companion.context
import com.djatar.ardath.R
import kotlin.random.Random

private const val TAG = "NotificationUtil"

object NotificationUtil {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private const val CHANNEL_ID = "messages"

    fun createNotificationChannel() {
        val name = context.getString(R.string.channel_name)
        val important = NotificationManager.IMPORTANCE_HIGH
        val descriptionText = context.getString(R.string.channel_description)
        val channel = NotificationChannel(CHANNEL_ID, name, important).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun notifyForMessages(
        title: String?,
        message: String?,
        pendingIntent: PendingIntent? = null
    ) {
        val notificationId = Random.nextInt(1000)
        val notificationBuilder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}