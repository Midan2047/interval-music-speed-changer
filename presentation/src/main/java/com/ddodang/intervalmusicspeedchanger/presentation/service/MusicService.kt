package com.ddodang.intervalmusicspeedchanger.presentation.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.ddodang.intervalmusicspeedchanger.presentation.notiifcation.MusicNotification
import com.ddodang.intervalmusicspeedchanger.presentation.receiver.AudioBroadcastReceiver
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service(), LifecycleOwner, LifecycleObserver {

    @Inject
    lateinit var musicPlayer: MusicPlayer

    @Inject
    lateinit var musicNotification: MusicNotification

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val receiver = AudioBroadcastReceiver()
    override fun onCreate() {
        super.onCreate()
        println("Service Created")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        initializeNotification()
        registerReceiver(receiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        unregisterReceiver(receiver)
        println("Service Destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Constants.ACTION.TOGGLE_PLAY -> musicPlayer.togglePlay()
            Constants.ACTION.PAUSE -> musicPlayer.pauseMusic()
            Constants.ACTION.NEXT -> musicPlayer.playNextMusic()
            Constants.ACTION.PREVIOUS -> musicPlayer.playPreviousMusic()
            Constants.ACTION.CLOSE -> finishService()
            Constants.ACTION.SEEK_TO -> musicPlayer.setMusicPosition(intent.getLongExtra(Constants.PARAMETER.POSITION,0L).toInt())
        }
        println("action : ${intent.action}")
        return START_STICKY
    }

    private fun initializeNotification() {
        musicNotification.createNotification()

        lifecycle.coroutineScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    musicPlayer.isPlayingFlow.collect { isPlaying ->
                        musicNotification.setRemoteViewMusicPlayState(isPlaying)
                        startForeground(MusicNotification.NOTIFICATION_ID, musicNotification.createNotification())
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    musicPlayer.currentPlayingMusicFlow.collect { currentMusic ->
                        if (currentMusic != null) {
                            musicNotification.setRemoteViewMusicInfo(currentMusic)
                            startForeground(MusicNotification.NOTIFICATION_ID, musicNotification.createNotification())
                        } else finishService()
                    }
                }
            }
        }
        startForeground(MusicNotification.NOTIFICATION_ID, musicNotification.createNotification())
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    private fun finishService() {
        musicPlayer.stopMusic()
        musicNotification.cancelNotification()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        stopSelf()
    }

    sealed class Constants {

        object ACTION : Constants() {

            const val SEEK_TO = "com.ddodang.intervalmusicspeedchanger.action.SEEKTO"
            const val TOGGLE_PLAY = "com.ddodang.intervalmusicspeedchager.action.TOGGLEPLAY"
            const val PAUSE = "com.ddodang.intervalmusicsppedchanger.action.PAUSE"
            const val NEXT = "com.ddodang.intervalmusicspeedchager.action.NEXT"
            const val PREVIOUS = "com.ddodang.intervalmusicspeedchager.action.PREVIOUS"
            const val CLOSE = "com.ddodang.intervalmusicspeedchanger.action.CLOSE"
        }

        object PARAMETER : Constants() {

            const val POSITION = "POSITION"
        }
    }

    override val lifecycle: Lifecycle = lifecycleRegistry

}