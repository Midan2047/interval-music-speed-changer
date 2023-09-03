package com.ddodang.intervalmusicspeedchanger.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ddodang.intervalmusicspeedchanger.presentation.notiifcation.MusicNotification
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService

class NotificationDismissedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.extras?.getInt("com.ddodang.intervalmusicspeedchanger.notificationId")
        if (notificationId == MusicNotification.NOTIFICATION_ID) {
            println("notification!")
            context?.startService(
                Intent(context, MusicService::class.java).apply {
                    action = MusicService.Constants.ACTION.CLOSE
                }
            )
        }
    }

}