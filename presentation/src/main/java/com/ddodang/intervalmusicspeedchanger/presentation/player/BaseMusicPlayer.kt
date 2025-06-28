package com.ddodang.intervalmusicspeedchanger.presentation.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.model.MusicPlayingInformation
import com.ddodang.intervalmusicspeedchanger.presentation.model.RepeatMode
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@UnstableApi
@OptIn(UnstableApi::class)
abstract class BaseMusicPlayer(protected val context: Context) {

    protected lateinit var exoPlayer: ExoPlayer

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) onMusicResume()
            else onMusicPause()
            _musicPlayingInformationFlow.update {
                it.copy(isPlaying = isPlaying)
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            _currentPlayingMusicFlow.value = playList.firstOrNull { it.id == mediaItem?.mediaId }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            _musicPlayingInformationFlow.update {
                it.copy(
                    repeatMode = repeatMode
                )
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            _musicPlayingInformationFlow.update {
                it.copy(
                    shuffle = shuffleModeEnabled
                )
            }
        }
    }

    private var playList: List<Music> = emptyList()
    private val _currentPlayingMusicFlow: MutableStateFlow<Music?> = MutableStateFlow(null)
    val currentPlayingMusicFlow = _currentPlayingMusicFlow.asStateFlow()

    private val currentPlayingMusic: Music?
        get() = currentPlayingMusicFlow.value

    private val _musicPlayingInformationFlow = MutableStateFlow(
        MusicPlayingInformation(
            isPlaying = false,
            playTimeMillis = 0L,
            playbackSpeed = 1f,
            repeatMode = Player.REPEAT_MODE_ALL,
            shuffle = false
        )
    )
    val musicPlayingInformationFlow = _musicPlayingInformationFlow.asStateFlow()

    protected val isPlaying: Boolean
        get() = musicPlayingInformationFlow.value.isPlaying

    init {
        createExoPlayer()
    }

    fun setMusicList(musicList: List<Music>) {
        playList = musicList
    }

    protected fun initialize(musicInfo: Music) {
        if (!exoPlayer.isPlaying) {
            context.startForegroundService(Intent(context, MusicService::class.java))
        }
        setMusic(musicInfo)
    }

    @OptIn(UnstableApi::class)
    private fun setMusic(musicInfo: Music?) {
        if (isPlaying) stopMusic()
        if (playList.isNotEmpty()) {
            if (exoPlayer.isReleased) createExoPlayer()
            exoPlayer.setMediaItems(
                playList.map { music -> music.toMediaItem() },
                playList.indexOfFirst { it.id == musicInfo?.id }.coerceAtLeast(0),
                C.TIME_UNSET
            )
            exoPlayer.prepare()
        }
    }

    private fun createExoPlayer() {
        exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            .setWakeMode(PowerManager.PARTIAL_WAKE_LOCK)
            .build()

        exoPlayer.playWhenReady = true
        exoPlayer.pauseAtEndOfMediaItems = false
        exoPlayer.addListener(playerListener)
        exoPlayer.repeatMode = musicPlayingInformationFlow.value.repeatMode
        exoPlayer.shuffleModeEnabled = musicPlayingInformationFlow.value.shuffle
    }

    fun togglePlay() {
        if (exoPlayer.isReleased) createExoPlayer()

        if (exoPlayer.mediaItemCount == 0) {
            setMusic(currentPlayingMusic)
        } else if (exoPlayer.isPlaying) {
            pauseMusic()
        } else {
            startMusic()
        }
    }

    fun playNextMusic() {
        exoPlayer.seekToNextMediaItem()
    }

    fun playPreviousMusic() {
        exoPlayer.seekToPreviousMediaItem()
    }

    private fun startMusic() {
        if (!isPlaying) {
            exoPlayer.play()
            exoPlayer.setPlaybackSpeed(musicPlayingInformationFlow.value.playbackSpeed)
            observePlayingState()
            onMusicResume()
        }
    }

    private fun observePlayingState() {
        CoroutineScope(Dispatchers.Default).launch {
            while (exoPlayer.isPlaying) {
                _musicPlayingInformationFlow.update { musicPlayingInformation ->
                    musicPlayingInformation.copy(
                        playTimeMillis = exoPlayer.currentPosition
                    )
                }
                delay(300L)
            }
        }
    }

    abstract fun onMusicResume()

    abstract fun onMusicPause()

    abstract fun onMusicStop()

    protected suspend fun setPlaybackSpeed(playbackSpeed: Float) = withContext(Dispatchers.Main) {
        exoPlayer.setPlaybackSpeed(playbackSpeed)
        _musicPlayingInformationFlow.update { musicPlayingInformation ->
            musicPlayingInformation.copy(playbackSpeed = playbackSpeed)
        }

    }

    private suspend fun setMusicProgressAndPlayingTime() {
        withContext(Dispatchers.Main) {
            _musicPlayingInformationFlow.update { musicPlayingInformation ->
                musicPlayingInformation.copy(
                    playTimeMillis = exoPlayer.currentPosition
                )
            }
        }
        do {
            delay(1000L)
        } while (!isPlaying)
    }

    fun pauseMusic() {
        exoPlayer.pause()
        onMusicPause()
    }

    fun stopMusic() {
        pauseMusic()
        _musicPlayingInformationFlow.update { it.copy(playTimeMillis = 0L) }
        exoPlayer.stop()
        exoPlayer.release()
        onMusicStop()
    }

    fun setMusicPosition(positionInMillis: Int) {
        runCatching {
            exoPlayer.seekTo(positionInMillis.toLong())
        }.onSuccess {
            _musicPlayingInformationFlow.update { musicPlayingInformation ->
                musicPlayingInformation.copy(
                    playTimeMillis = positionInMillis.toLong()
                )
            }
        }
    }

    fun setShuffleMode(shuffle: Boolean) {
        exoPlayer.shuffleModeEnabled = shuffle
    }

    fun setRepeatMode(repeatMode: RepeatMode) {
        exoPlayer.repeatMode = repeatMode.mode
    }

    private fun Music.toMediaItem() = MediaItem.Builder()
        .setMediaId(id)
        .setUri(Uri.fromFile(File(location)))
        .build()
}