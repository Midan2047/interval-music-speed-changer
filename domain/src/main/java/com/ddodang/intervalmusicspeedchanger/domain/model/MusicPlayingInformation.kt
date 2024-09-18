package com.ddodang.intervalmusicspeedchanger.domain.model

data class MusicPlayingInformation(
    val isPlaying: Boolean,
    val playTimeMillis: Long,
    val playbackSpeed: Float,
    val repeatMode: Int,
    val shuffle: Boolean,
)