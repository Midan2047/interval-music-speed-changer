package com.ddodang.intervalmusicspeedchanger.presentation.service

import android.annotation.SuppressLint
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
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        initializeNotification()
        registerReceiver(receiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        unregisterReceiver(receiver)
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
            Constants.ACTION.SEEK_TO -> musicPlayer.setMusicPosition(intent.getLongExtra(Constants.PARAMETER.POSITION, 0L).toInt())
            Constants.ACTION.INTERVAL_DONE -> intervalDone()
        }
        return START_STICKY
    }

    private fun initializeNotification() {
        musicNotification.createMusicPlayerNotification()

        lifecycle.coroutineScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    musicPlayer.musicPlayingInformationFlow.collect { musicPlayingInformation ->
                        musicNotification.setMusicPlayingInformation(musicPlayingInformation)
                        startForeground(MusicNotification.MUSIC_PLAYER_NOTIFICATION_ID, musicNotification.createMusicPlayerNotification())
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    musicPlayer.currentPlayingMusicFlow.collect { currentMusic ->
                        if (currentMusic != null) {
                            musicNotification.setRemoteViewMusicInfo(currentMusic)
                            startForeground(MusicNotification.MUSIC_PLAYER_NOTIFICATION_ID, musicNotification.createMusicPlayerNotification())
                        } else finishService()
                    }
                }
            }
        }
        startForeground(MusicNotification.MUSIC_PLAYER_NOTIFICATION_ID, musicNotification.createMusicPlayerNotification())
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    private fun finishService() {
        finishMusicPlayNotification()
        stopSelf()
    }

    private fun finishMusicPlayNotification() {
        musicPlayer.stopMusic()
        musicNotification.cancelNotification()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    @SuppressLint("MissingPermission")
    private fun intervalDone() {
        finishMusicPlayNotification()
        startForeground(MusicNotification.INTERVAL_DONE_NOTIFICATION_ID, musicNotification.createIntervalDoneNotification())
    }

    sealed class Constants {

        object ACTION : Constants() {

            const val SEEK_TO = "com.ddodang.intervalmusicspeedchanger.action.SEEKTO"
            const val TOGGLE_PLAY = "com.ddodang.intervalmusicspeedchager.action.TOGGLEPLAY"
            const val PAUSE = "com.ddodang.intervalmusicsppedchanger.action.PAUSE"
            const val NEXT = "com.ddodang.intervalmusicspeedchager.action.NEXT"
            const val PREVIOUS = "com.ddodang.intervalmusicspeedchager.action.PREVIOUS"
            const val CLOSE = "com.ddodang.intervalmusicspeedchanger.action.CLOSE"
            const val INTERVAL_DONE = "com.ddodang.intervalmusicspeedchanger.action.INTERVALDONE"
        }

        object PARAMETER : Constants() {

            const val POSITION = "POSITION"
        }
    }

    override val lifecycle: Lifecycle = lifecycleRegistry

}