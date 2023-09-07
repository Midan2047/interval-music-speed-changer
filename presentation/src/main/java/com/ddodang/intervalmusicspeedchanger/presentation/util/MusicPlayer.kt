package com.ddodang.intervalmusicspeedchanger.presentation.util

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import com.ddodang.intervalmusicspeedchanger.common.extensions.getOrDefault
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.model.MusicPlayingInformation
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private var mediaPlayer: MediaPlayer? = null

    private var playList: List<Music> = emptyList()
    private val _currentPlayingMusicFlow: MutableStateFlow<Music?> = MutableStateFlow(null)
    val currentPlayingMusicFlow = _currentPlayingMusicFlow.asStateFlow()

    private val currentPlayingMusic: Music?
        get() = currentPlayingMusicFlow.value

    private val _musicPlayingInformationFlow = MutableStateFlow(
        MusicPlayingInformation(
            isPlaying = false,
            playTimeMillis = 0,
            playbackSpeed = 1f
        )
    )
    val musicPlayingInformationFlow = _musicPlayingInformationFlow.asStateFlow()

    private val isPlaying: Boolean
        get() = musicPlayingInformationFlow.value.isPlaying

    private var intervalJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private var intervalRunning: Int = 1 * 60
    private var intervalWalking: Int = 1 * 60
    private var intervalSet: Int = 1
    private var currentTime: Int = 0

    private val secondPerIntervalSet
        get() = intervalWalking + intervalRunning

    fun setMusicList(musicList: List<Music>) {
        playList = musicList
    }

    fun initialize(musicInfo: Music, interval: IntervalSetting) {
        if (mediaPlayer == null) {
            context.startForegroundService(Intent(context, MusicService::class.java))
        }
        setInterval(interval)
        setMusic(musicInfo)

    }

    fun setInterval(interval: IntervalSetting) {
        intervalWalking = interval.walkingMinutes * 60
        intervalRunning = interval.runningMinutes * 60
        intervalSet = interval.setCount
    }

    private fun setMusic(musicInfo: Music?) {
        val needPlay = mediaPlayer == null || isPlaying
        if (isPlaying) stopMusic()
        _currentPlayingMusicFlow.value = musicInfo
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setOnPreparedListener {
                if (needPlay) {
                    startMusic()
                }
            }
            setOnCompletionListener { completedMediaPlayer ->
                setMusic(getNextMusic())
                completedMediaPlayer.release()
            }
            setOnErrorListener { mediaPlayer, _, _ ->
                mediaPlayer.reset()
                true
            }
            setDataSource(musicInfo?.location)
            prepareAsync()
            setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        }
    }

    fun togglePlay() {
        if (isPlaying) {
            pauseMusic()
        } else if (mediaPlayer == null) {
            setMusic(currentPlayingMusic)
        } else {
            startMusic()
        }
    }

    fun playNextMusic() {
        setMusic(getNextMusic())
    }

    fun playPreviousMusic() {
        setMusic(getPreviousMusic())
    }

    private fun startMusic() {
        if (!isPlaying) {
            mediaPlayer?.let {
                it.start()
                _musicPlayingInformationFlow.update { musicPlayingInformation ->
                    musicPlayingInformation.copy(isPlaying = true)
                }
            }
            startInterval()
        }
    }

    private fun startInterval() {
        intervalJob = CoroutineScope(Dispatchers.Default).launch {
            while (currentTime < intervalSet * secondPerIntervalSet) {
                setMusicProgressAndPlayingTime()
                if (currentTime % secondPerIntervalSet == intervalWalking) {
                    setPlaybackSpeed(2f)
                } else if (currentTime % secondPerIntervalSet == 0) {
                    setPlaybackSpeed(1f)
                }
            }
            currentTime = 0
            intervalJob = null
            context.startService(Intent(context, MusicService::class.java).apply { action = MusicService.Constants.ACTION.INTERVAL_DONE })
        }
    }

    private fun setPlaybackSpeed(playbackSpeed: Float) {
        mediaPlayer?.apply {
            playbackParams = playbackParams.setSpeed(playbackSpeed)
            _musicPlayingInformationFlow.update { musicPlayingInformation ->
                musicPlayingInformation.copy(playbackSpeed = playbackSpeed)
            }
        }
    }

    private suspend fun setMusicProgressAndPlayingTime() {
        _musicPlayingInformationFlow.update { musicPlayingInformation ->
            musicPlayingInformation.copy(
                playTimeMillis = mediaPlayer?.currentPosition ?: 0
            )
        }
        delay(1000L)
        currentTime += 1
        if (!isPlaying) intervalJob?.cancel()
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        _musicPlayingInformationFlow.update { musicPlayingInformation ->
            musicPlayingInformation.copy(isPlaying = false)
        }
    }

    fun stopMusic() {
        pauseMusic()
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setMusicPosition(positionInMillis: Int) {
        runCatching {
            mediaPlayer?.seekTo(positionInMillis)
        }.onSuccess {
            _musicPlayingInformationFlow.update { musicPlayingInformation ->
                musicPlayingInformation.copy(
                    playTimeMillis = positionInMillis
                )
            }
        }
    }

    private fun getNextMusic(): Music? {
        val nextIndex = (playList.indexOf(currentPlayingMusic) + 1) % playList.size
        return playList.getOrDefault(nextIndex, currentPlayingMusic)
    }

    private fun getPreviousMusic(): Music? {
        val previousIndex =
            (playList.indexOf(currentPlayingMusic) + playList.size - 1) % playList.size
        return playList.getOrDefault(previousIndex, currentPlayingMusic)
    }
}