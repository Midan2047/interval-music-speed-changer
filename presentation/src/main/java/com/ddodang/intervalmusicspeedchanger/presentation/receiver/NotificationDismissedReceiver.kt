package com.ddodang.intervalmusicspeedchanger.presentation.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService

class NotificationDismissedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.extras?.getInt("com.ddodang.intervalmusicspeedchanger.notificationId")
        if (notificationId == MusicService.Constants.ID.NOTIFICATION_ID) {
            println("notification!")
            context?.startService(
                Intent(context, MusicService::class.java).apply {
                    action = MusicService.Constants.ACTION.CLOSE
                }
            )
        }
    }

}