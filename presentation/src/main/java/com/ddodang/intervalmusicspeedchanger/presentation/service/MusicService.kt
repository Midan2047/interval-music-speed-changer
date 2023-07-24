package com.ddodang.intervalmusicspeedchanger.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.receiver.AudioBroadcastReceiver
import com.ddodang.intervalmusicspeedchanger.presentation.ui.MainActivity
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service(), LifecycleOwner, LifecycleObserver {

    @Inject
    lateinit var musicPlayer: MusicPlayer

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
            Constants.ACTION.TOGGLE_PLAY -> togglePlay()
            Constants.ACTION.PAUSE -> pauseMusic()
            Constants.ACTION.NEXT -> playNextMusic()
            Constants.ACTION.PREVIOUS -> playPreviousMusic()
            Constants.ACTION.CLOSE -> finishService()
        }
        println("action : ${intent.action}")
        return START_NOT_STICKY
    }

    private fun togglePlay() {
        musicPlayer.togglePlay()
    }

    private fun playNextMusic() {
        musicPlayer.playNextMusic()
    }

    private fun pauseMusic() {
        musicPlayer.pauseMusic()
    }

    private fun playPreviousMusic() {
        musicPlayer.playPreviousMusic()
    }

    private fun initializeNotification() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val remoteView = createRemoteView()

        val notification = NotificationCompat.Builder(this, Constants.ID.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play)
            .setCustomContentView(remoteView)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSilent(true)
            .build()

        lifecycle.coroutineScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    musicPlayer.isPlayingFlow.collect { isPlaying ->
                        println("Music Playing Toggled : $isPlaying")
                        if (isPlaying) {
                            remoteView.setImageViewResource(R.id.imageButton_playPause_notification, R.drawable.ic_pause)
                        } else {
                            remoteView.setImageViewResource(R.id.imageButton_playPause_notification, R.drawable.ic_play)
                        }
                        notifyNotificationChanged(notification)
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    musicPlayer.currentPlayingMusicFlow.collect { currentMusic ->
                        println("Playing Music Changed : ${currentMusic?.title}")
                        remoteView.setTextViewText(R.id.textView_title_notification, currentMusic?.title)
                        remoteView.setTextViewText(R.id.textView_singer_notification, currentMusic?.artist)
                        notifyNotificationChanged(notification)
                    }
                }
            }
        }

        startForeground(Constants.ID.NOTIFICATION_ID, notification)

    }

    private fun notifyNotificationChanged(notification: Notification) {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(Constants.ID.NOTIFICATION_ID, notification)
    }

    private fun createRemoteView(): RemoteViews {
        val remoteView = RemoteViews("com.ddodang.intervalmusicspeedchanger", R.layout.notification_music)

        val playToggleIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MusicService::class.java).apply { action = Constants.ACTION.TOGGLE_PLAY },
            PendingIntent.FLAG_IMMUTABLE
        )
        val forwardIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MusicService::class.java).apply { action = Constants.ACTION.NEXT },
            PendingIntent.FLAG_IMMUTABLE
        )
        val rewindIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MusicService::class.java).apply { action = Constants.ACTION.PREVIOUS },
            PendingIntent.FLAG_IMMUTABLE
        )
        val closeIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MusicService::class.java).apply { action = Constants.ACTION.CLOSE },
            PendingIntent.FLAG_IMMUTABLE
        )

        remoteView.setOnClickPendingIntent(R.id.imageButton_playPause_notification, playToggleIntent)
        remoteView.setOnClickPendingIntent(R.id.imageButton_forward_notification, forwardIntent)
        remoteView.setOnClickPendingIntent(R.id.imageButton_rewind_notification, rewindIntent)
        remoteView.setOnClickPendingIntent(R.id.imageButton_close_notification, closeIntent)

        return remoteView
    }

    private fun finishService() {
        musicPlayer.stopMusic()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(Constants.ID.NOTIFICATION_ID)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(Constants.ID.CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    sealed class Constants {

        object ID : Constants() {

            const val CHANNEL_ID = "IntervalMusicPlayerChannel"
            const val NOTIFICATION_ID = 1
        }

        object ACTION : Constants() {

            const val TOGGLE_PLAY = "com.ddodang.intervalmusicspeedchager.action.TOGGLEPLAY"
            const val PAUSE = "com.ddodang.intervalmusicsppedchanger.action.PAUSE"
            const val NEXT = "com.ddodang.intervalmusicspeedchager.action.NEXT"
            const val PREVIOUS = "com.ddodang.intervalmusicspeedchager.action.PREVIOUS"
            const val CLOSE = "com.ddodang.intervalmusicspeedchanger.action.CLOSE"
        }
    }

    override val lifecycle: Lifecycle = lifecycleRegistry

}