package com.ddodang.intervalmusicspeedchanger.domain.model

import kotlinx.coroutines.flow.StateFlow

data class DownloadState(
    val downloadProgressState: StateFlow<Int>,
    val downloadedByteState: StateFlow<Int>,
    val totalByte: StateFlow<Long>,
    val errorState: StateFlow<Throwable?>,
)
