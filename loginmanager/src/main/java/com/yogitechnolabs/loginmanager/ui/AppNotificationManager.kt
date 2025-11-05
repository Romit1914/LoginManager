package com.yogitechnolabs.loginmanager.ui

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object AppNotificationManager {

    private const val CHANNEL_ID = "app_default_channel"

    fun initChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 1,
        imageResId: Int? = null,     // 👉 new param for image
        smallIconRes: Int = R.drawable.ic_dialog_info // 👉 custom small icon
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // 👉 If image is provided, show Big Picture Style
        imageResId?.let {
            val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, it)
            val style = NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
            builder.setStyle(style)
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
}
