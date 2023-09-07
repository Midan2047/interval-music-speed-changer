package com.ddodang.intervalmusicspeedchanger.domain.model

data class MusicPlayingInformation(
    val isPlaying: Boolean,
    val playTimeMillis: Int,
    val playbackSpeed: Float,
)