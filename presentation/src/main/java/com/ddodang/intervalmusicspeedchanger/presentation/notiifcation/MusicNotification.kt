package com.ddodang.intervalmusicspeedchanger.presentation.notiifcation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import androidx.core.app.NotificationManagerCompat
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.model.MusicPlayingInformation
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

    fun createIntervalDoneNotification() = Notification.Builder(context, CHANNEL_ID)
        .setSmallIcon(Icon.createWithResource(context, R.drawable.ikmyung_profile))
        .setContentTitle("인터벌이 종료되어쯥니다!!")
        .setContentText("수고하셔쯥니다!!")
        .setContentIntent(createIntervalDoneContentPendingIntent())
        .setVisibility(Notification.VISIBILITY_PUBLIC)
        .setAutoCancel(true)
        .setDeleteIntent(createOnDismissedIntent(context, notificationId = MUSIC_PLAYER_NOTIFICATION_ID))
        .build()

    fun createMusicPlayerNotification() = Notification.Builder(context, CHANNEL_ID)
        .setSmallIcon(Icon.createWithResource(context, R.drawable.ikmyung_profile))
        .setLargeIcon(retrieveThumbnailBitmapFromFile(musicInfo?.location))
        .setContentTitle(musicInfo?.title)
        .setContentText(musicInfo?.artist)
        .setContentIntent(createMusicPlayerContentPendingIntent())
        .setVisibility(Notification.VISIBILITY_PUBLIC)
        .setOnlyAlertOnce(true)
        .setShowWhen(false)
        .setStyle(
            Notification.DecoratedMediaCustomViewStyle()
                .setMediaSession(mediaSession.sessionToken)
        )
        .setDeleteIntent(createOnDismissedIntent(context, notificationId = MUSIC_PLAYER_NOTIFICATION_ID))
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
        NotificationManagerCompat.from(context).cancel(MUSIC_PLAYER_NOTIFICATION_ID)
    }

    fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(CHANNEL_ID, javaClass.name, NotificationManager.IMPORTANCE_HIGH).also {
            it.group = CHANNEL_GROUP_ID
        }
        NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
    }

    private fun createIntervalDoneContentPendingIntent() =
        PendingIntent.getService(
            context,
            0,
            Intent(context, MusicService::class.java).apply { action = MusicService.Constants.ACTION.CLOSE },
            PendingIntent.FLAG_IMMUTABLE
        )

    private fun createMusicPlayerContentPendingIntent() =
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

    fun setMusicPlayingInformation(musicPlayingInformation: MusicPlayingInformation) {
        setPlaybackState(
            state = if (musicPlayingInformation.isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
            position = musicPlayingInformation.playTimeMillis.toLong(),
            speed = musicPlayingInformation.playbackSpeed
        )

    }

    companion object {

        private const val CHANNEL_GROUP_ID = "IntervalMusicPlayerChannelGroup"
        private const val CHANNEL_GROUP_NAME = "인터벌 메이커"
        private const val CHANNEL_ID = "IntervalMusicPlayerChannel"

        const val MUSIC_PLAYER_NOTIFICATION_ID = 1
        const val INTERVAL_DONE_NOTIFICATION_ID = 2
    }
}