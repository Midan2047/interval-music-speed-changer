package com.ddodang.intervalmusicspeedchanger.data.model

import kotlinx.coroutines.flow.StateFlow

data class DownloadStateData(
    val downloadProgressState: StateFlow<Int>,
    val downloadedByteState: StateFlow<Int>,
    val totalByte: StateFlow<Long>,
    val errorState: StateFlow<Throwable?>,
)
