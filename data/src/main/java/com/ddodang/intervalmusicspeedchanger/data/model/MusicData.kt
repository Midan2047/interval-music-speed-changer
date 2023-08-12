package com.ddodang.intervalmusicspeedchanger.data.model

data class MusicData(
    val id: String,
    val artist: String,
    val title: String,
    val location: String,
    val durationInMillis: Int,
)