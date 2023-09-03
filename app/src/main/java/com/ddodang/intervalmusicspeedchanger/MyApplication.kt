package com.ddodang.intervalmusicspeedchanger

import android.app.Application
import com.ddodang.intervalmusicspeedchanger.presentation.notiifcation.MusicNotification
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var musicNotification: MusicNotification

    override fun onCreate() {
        super.onCreate()
        musicNotification.createNotificationChannel()
    }
}