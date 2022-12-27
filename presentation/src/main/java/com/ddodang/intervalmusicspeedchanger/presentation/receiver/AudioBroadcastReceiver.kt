package com.ddodang.intervalmusicspeedchanger.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService

class AudioBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            context.startService(
                Intent(context, MusicService::class.java).apply {
                    action = MusicService.Constants.ACTION.PAUSE
                }
            )
        }
    }
}