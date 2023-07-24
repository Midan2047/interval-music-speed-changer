package com.ddodang.intervalmusicspeedchanger.remote.mapper

import com.ddodang.intervalmusicspeedchanger.data.model.DownloadStateData
import com.ddodang.intervalmusicspeedchanger.remote.model.DownloadStateDto

internal fun DownloadStateDto.toData() = DownloadStateData(
    downloadProgressState = downloadProgressState,
    downloadedByteState = downloadedByteState,
    totalByte = totalByte,
    errorState = errorState
)