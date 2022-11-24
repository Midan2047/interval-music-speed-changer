package com.ddodang.intervalmusicspeedchanger.domain.model

import java.io.Serializable

data class Music(
    val id: String,
    val artist: String,
    val title: String,
    val location: String,
    val duration: String,
) : Serializable