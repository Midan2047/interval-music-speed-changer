package com.ddodang.intervalmusicspeedchanger.remote.model

import kotlinx.coroutines.flow.MutableStateFlow

data class DownloadStateDto(
    val downloadProgressState: MutableStateFlow<Int> = MutableStateFlow(0),
    val downloadedByteState: MutableStateFlow<Int> = MutableStateFlow(0),
    val totalByte: MutableStateFlow<Long> = MutableStateFlow(0L),
    val errorState: MutableStateFlow<Throwable?> = MutableStateFlow(null),
)
