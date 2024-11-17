package com.djatar.ardath.feature.presentation.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.djatar.ardath.ArdathApp.Companion.context
import com.djatar.ardath.R

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
        userName: String?,
        messageText: String?,
        imageUrl: Uri? = null,
        pendingIntent: PendingIntent? = null,
        notificationId: Int
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(userName)
            .setContentText(messageText)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (imageUrl != null) {
            ImageUtils.getBitmapFromUrl(imageUrl.toString())?.let {
                builder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(it)
                        .bigLargeIcon(null as Bitmap?)
                        .setBigContentTitle(userName)
                        .setSummaryText(messageText)
                ).setLargeIcon(it)
            }
        }
        notificationManager.notify(notificationId, builder.build())
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    private const val TAG = "NotificationUtil"
}