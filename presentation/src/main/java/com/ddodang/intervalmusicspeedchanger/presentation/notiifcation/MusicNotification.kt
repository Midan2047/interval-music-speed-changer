package com.ddodang.intervalmusicspeedchanger.presentation.notiifcation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import androidx.core.app.NotificationManagerCompat
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.receiver.NotificationDismissedReceiver
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService
import com.ddodang.intervalmusicspeedchanger.presentation.ui.MainActivity
import com.ddodang.intervalmusicspeedchanger.presentation.util.retrieveThumbnailBitmapFromFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicNotification @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private var musicInfo: Music? = null

    private val mediaSession = MediaSession(context, javaClass.name).also {
        it.setCallback(object : MediaSession.Callback() {
            override fun onPlay() {
                context.startForegroundService(
                    Intent(context, MusicService::class.java).apply { action = MusicService.Constants.ACTION.TOGGLE_PLAY }
                )
            }

            override fun onSkipToNext() {
                seekToStart()
                context.startForegroundService(
                    Intent(context, MusicService::class.java).apply { action = MusicService.Constants.ACTION.NEXT }
                )
            }

            override fun onSkipToPrevious() {
                seekToStart()
                context.startForegroundService(
                    Intent(context, MusicService::class.java).apply { action = MusicService.Constants.ACTION.PREVIOUS }
                )
            }

            override fun onPause() {
                context.startForegroundService(
                    Intent(context, MusicService::class.java).apply { action = MusicService.Constants.ACTION.TOGGLE_PLAY }
                )
            }

            override fun onSeekTo(pos: Long) {
                seekTo(pos)
                context.startForegroundService(
                    Intent(context, MusicService::class.java).apply {
                        action = MusicService.Constants.ACTION.SEEK_TO
                        putExtra(MusicService.Constants.PARAMETER.POSITION, pos)
                    }
                )
            }
        })
    }

    fun createNotification() = Notification.Builder(context, CHANNEL_ID)
        .setSmallIcon(Icon.createWithResource(context, R.drawable.ic_music_note))
        .setLargeIcon(retrieveThumbnailBitmapFromFile(musicInfo?.location))
        .setContentTitle(musicInfo?.title)
        .setContentText(musicInfo?.artist)
        .setContentIntent(createContentPendingIntent())
        .setVisibility(Notification.VISIBILITY_PUBLIC)
        .setOnlyAlertOnce(true)
        .setShowWhen(false)
        .setStyle(
            Notification.DecoratedMediaCustomViewStyle()
                .setMediaSession(mediaSession.sessionToken)
        )
        .setDeleteIntent(createOnDismissedIntent(context, notificationId = NOTIFICATION_ID))
        .build()

    fun setRemoteViewMusicPlayState(isPlaying: Boolean) {
        setPlaybackState(state = if (isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED)
    }

    fun setRemoteViewMusicInfo(musicInfo: Music) {
        this.musicInfo = musicInfo
        val mediaMetadata = MediaMetadata.Builder()
            .putLong(MediaMetadata.METADATA_KEY_DURATION, musicInfo.durationInMillis.toLong()).build()
        mediaSession.setMetadata(mediaMetadata)
    }

    private fun seekTo(position: Long) {
        setPlaybackState(position = position)
    }

    private fun seekToStart() {
        setPlaybackState(position = 0L)
    }

    private fun setPlaybackState(state: Int? = null, position: Long? = null, speed: Float? = null) {
        val currentPlaybackState = mediaSession.controller.playbackState
        mediaSession.setPlaybackState(
            PlaybackState.Builder()
                .setState(
                    state ?: currentPlaybackState?.state ?: PlaybackState.STATE_PLAYING,
                    position ?: currentPlaybackState?.position ?: 0,
                    speed ?: currentPlaybackState?.playbackSpeed ?: 1f
                ).setActions(
                    PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS or PlaybackState.ACTION_SEEK_TO
                )
                .build()
        )
    }

    fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(CHANNEL_ID, javaClass.name, NotificationManager.IMPORTANCE_HIGH).also {
            it.group = CHANNEL_GROUP_ID
        }
        NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
    }

    private fun createContentPendingIntent() =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

    private fun createOnDismissedIntent(context: Context, notificationId: Int): PendingIntent {
        val intent = Intent(context, NotificationDismissedReceiver::class.java)
        intent.putExtra("com.ddodang.intervalmusicspeedchanger.notificationId", notificationId)

        return PendingIntent.getBroadcast(context.applicationContext, notificationId, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    companion object {

        private const val CHANNEL_GROUP_ID = "IntervalMusicPlayerChannelGroup"
        private const val CHANNEL_GROUP_NAME = "인터벌 메이커"
        private const val CHANNEL_ID = "IntervalMusicPlayerChannel"
        const val NOTIFICATION_ID = 1
    }
}