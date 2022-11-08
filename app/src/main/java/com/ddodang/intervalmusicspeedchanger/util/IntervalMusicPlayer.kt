package com.ddodang.intervalmusicspeedchanger.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Environment
import com.ddodang.intervalmusicspeedchanger.model.MusicInfo
import java.io.File
import java.util.*

object IntervalMusicPlayer {

    var musicPlayer: MediaPlayer? = null
    private lateinit var sharedManageUtil: SharedManageUtil
    var setCount = 0
    var walkingTime = 0
    var runningTime = 0
    var isLooping = false

    var musicSpeed = 1f
        set(value) {
            musicPlayer?.apply {
                playbackParams = playbackParams.setSpeed(value)
            }
            field = value
        }

    var playingMusicPosition = -1

    private var timer = Timer()

    private val walkingTask = object : TimerTask() {
        override fun run() {
            println("run!")
            musicSpeed = 2f
        }
    }
    private val runningTask = object : TimerTask() {
        override fun run() {
            println("walk")
            musicSpeed = 1f
        }
    }

    fun startTimer() {
        timer.schedule(walkingTask, walkingTime * 60 * 1000L, (walkingTime + runningTime) * 60 * 1000L)
        timer.schedule(runningTask, 0L, (runningTime + walkingTime) * 60 * 1000L)
    }

    fun pauseTimer() {
        timer.cancel()
        timer = Timer()
    }

    val onMusicListChangedListeners: ArrayList<(List<MusicInfo>) -> Unit> = arrayListOf()

    var musicList: List<MusicInfo> = emptyList()
        private set(value) {
            onMusicListChangedListeners.forEach { listener ->
                listener.invoke(value)
            }
            field = value
        }

    fun initialize(context: Context) {
        setSharedManageUtil(SharedManageUtil.getInstance(context))
        musicList = loadMusicInfo(context)
    }

    fun deleteMusic(context: Context, position: Int) {
        val filePath = musicList.getOrNull(position)?.location ?: return
        val deleteFile = File(filePath)
        deleteFile.delete()
        musicList = loadMusicInfo(context)
    }

    private fun loadMusicInfo(context: Context): List<MusicInfo> {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: return emptyList()
        val musicList = directory.listFiles()?.mapNotNull { file ->
            val metadataRetriever = MediaMetadataRetriever()
            metadataRetriever.setDataSource(file.absolutePath)
            val title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.nameWithoutExtension
            val artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "알수 없는 가수"
            val duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: ""
            MusicInfo(
                id = file.absolutePath.toHashSet().joinToString(""),
                title = title,
                artist = artist,
                duration = duration,
                location = file.absolutePath
            )
        } ?: emptyList()

        return musicList
    }

    private fun setSharedManageUtil(util: SharedManageUtil) {
        sharedManageUtil = util
        setCount = sharedManageUtil.setCount
        walkingTime = sharedManageUtil.walkingTime
        runningTime = sharedManageUtil.runningTime
    }

    fun saveSettings() {
        sharedManageUtil.setCount = setCount
        sharedManageUtil.walkingTime = walkingTime
        sharedManageUtil.runningTime = runningTime
    }
}