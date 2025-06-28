package com.ddodang.intervalmusicspeedchanger.presentation.util

import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.presentation.player.BaseMusicPlayer
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class IntervalMusicPlayer @OptIn(UnstableApi::class) @Inject constructor(
    @ApplicationContext context: Context,
) : BaseMusicPlayer(context) {

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

    fun initialize(musicInfo: Music, interval: IntervalSetting) {
        initialize(musicInfo)
        setInterval(interval)
    }

    fun setInterval(interval: IntervalSetting) {
        intervalWalking = interval.walkingMinutes * 60
        intervalRunning = interval.runningMinutes * 60
        intervalSet = interval.setCount
    }

    override fun onMusicResume() {
        startInterval()
    }

    override fun onMusicPause() {}

    override fun onMusicStop() {
        intervalJob?.cancel()
        intervalJob = null
    }

    private fun startInterval() {
        if (intervalJob == null && isValidInterval()) {
            intervalJob = CoroutineScope(Dispatchers.Default).launch {
                while (intervalSet == 0 || currentTime < intervalSet * secondPerIntervalSet) {
                    tickIfMusicPlaying()
                    if(secondPerIntervalSet > 0) {
                        if (currentTime % secondPerIntervalSet in 0 .. intervalWalking) {
                            setPlaybackSpeed(1.5f)
                        } else if (currentTime % secondPerIntervalSet in intervalWalking .. secondPerIntervalSet) {
                            setPlaybackSpeed(1f)
                        }
                    }
                }
                currentTime = 0
                intervalJob = null
                context.startService(Intent(context, MusicService::class.java).apply { action = MusicService.Constants.ACTION.INTERVAL_DONE })
            }
        }
    }

    private suspend fun tickIfMusicPlaying() {
        do {
            delay(1000L)
        } while (!isPlaying)
        currentTime += 1
    }

    private fun isValidInterval() : Boolean {
        return (intervalRunning != 0 || intervalWalking != 0)
    }

}