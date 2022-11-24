package com.ddodang.intervalmusicspeedchanger.presentation.util

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.ddodang.intervalmusicspeedchanger.common.extensions.getOrDefault
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor() {

    private var mediaPlayer: MediaPlayer? = null

    private var playList: List<Music> = emptyList()
    private val _currentPlayingMusicFlow = MutableStateFlow(Music("", "", "", "", ""))
    val currentPlayingMusicFlow = _currentPlayingMusicFlow.asStateFlow()

    private val currentPlayingMusic: Music
        get() = currentPlayingMusicFlow.value

    private val _isPlayingFlow = MutableStateFlow(false)
    val isPlayingFlow = _isPlayingFlow.asStateFlow()

    private val isPlaying: Boolean
        get() = isPlayingFlow.value

    private var intervalJob: Job? = null

    private var intervalRunning: Int = 1 * 60
    private var intervalWalking: Int = 1 * 60
    private var intervalSet: Int = 1
    private var currentTime: Int = 0

    fun setMusicList(musicList: List<Music>) {
        playList = musicList
    }

    fun initialize(musicInfo: Music, interval: IntervalSetting) {
        setInterval(interval)
        if (mediaPlayer == null) {
            setMusic(musicInfo)
        }
    }

    fun setInterval(interval: IntervalSetting) {
        intervalWalking = interval.walkingMinutes * 60
        intervalRunning = interval.runningMinutes * 60
        intervalSet = interval.setCount
    }

    private fun setMusic(musicInfo: Music) {
        if (isPlaying) stopMusic()
        _currentPlayingMusicFlow.value = musicInfo
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setOnPreparedListener { mediaPlayer ->
                startMusic()
            }
            setOnCompletionListener { completedMediaPlayer ->
                setMusic(getNextMusic())
                completedMediaPlayer.release()
            }
            setOnErrorListener { mediaPlayer, what, extra ->
                println("what? $what")
                println("extra $extra")
                mediaPlayer.reset()
                true
            }
            setDataSource(musicInfo.location)
            prepareAsync()
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
                _isPlayingFlow.value = true
            }
            startInterval()
        }
    }

    private fun startInterval() {
        intervalJob = CoroutineScope(Dispatchers.Default).launch {
            while (currentTime < intervalSet * (intervalWalking + intervalRunning)) {
                while (currentTime % (intervalWalking + intervalRunning) < intervalWalking) {
                    delay(1000L)
                    currentTime += 1
                    if (!isPlaying) intervalJob?.cancel()
                }
                mediaPlayer?.apply {
                    playbackParams = playbackParams.setSpeed(2f)
                }

                while (currentTime % (intervalWalking + intervalRunning) < intervalWalking + intervalRunning) {
                    delay(1000L)
                    currentTime += 1
                    if (!isPlaying) intervalJob?.cancel()
                }
                mediaPlayer?.apply {
                    playbackParams = playbackParams.setSpeed(1f)
                }
            }
            currentTime = 0
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        _isPlayingFlow.value = false
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlayingFlow.value = false
    }

    private fun getNextMusic(): Music {
        val nextIndex = (playList.indexOf(currentPlayingMusic) + 1) % playList.size
        return playList.getOrDefault(nextIndex, currentPlayingMusic)
    }

    private fun getPreviousMusic(): Music {
        val previousIndex =
            (playList.indexOf(currentPlayingMusic) + playList.size - 1) % playList.size
        return playList.getOrDefault(previousIndex, currentPlayingMusic)
    }
}