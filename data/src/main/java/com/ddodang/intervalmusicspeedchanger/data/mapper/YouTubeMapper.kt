package com.ddodang.intervalmusicspeedchanger.data.mapper

import com.ddodang.intervalmusicspeedchanger.data.model.DownloadStateData
import com.ddodang.intervalmusicspeedchanger.data.model.YouTubeSearchResultData
import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult

internal fun YouTubeSearchResultData.toDomain() = YouTubeSearchResult(
    videoId = videoId,
    title = title,
    thumbnail = thumbnailUrl
)

internal fun DownloadStateData.toDomain() = DownloadState(
    downloadProgressState = downloadProgressState,
    downloadedByteState = downloadedByteState,
    totalByte = totalByte,
    errorState = errorState
)