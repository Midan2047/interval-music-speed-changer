package com.ddodang.intervalmusicspeedchanger.data.mapper

import com.ddodang.intervalmusicspeedchanger.data.model.DownloadMusicStateData
import com.ddodang.intervalmusicspeedchanger.data.model.DownloadStateData
import com.ddodang.intervalmusicspeedchanger.data.model.YouTubeSearchResultData
import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadMusicState
import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult

internal fun YouTubeSearchResultData.toDomain() = YouTubeSearchResult(
    nextPageToken = nextPageToken,
    videoList = videoList.map { video -> video.toDomain() }
)

internal fun YouTubeSearchResultData.VideoInfo.toDomain() = YouTubeSearchResult.VideoInfo(
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

internal fun DownloadMusicStateData.toDomain(): DownloadMusicState = when (this) {
    is DownloadMusicStateData.DownloadMusicFailed -> DownloadMusicState.DownloadMusicFailed(throwable)
    is DownloadMusicStateData.FetchMusicLinkFailed -> DownloadMusicState.FetchMusicLinkFailed(throwable)
    is DownloadMusicStateData.InvalidLink -> DownloadMusicState.InvalidLink(throwable)
    DownloadMusicStateData.OnDownloadDoneMusic -> DownloadMusicState.OnDownloadDoneMusic
    is DownloadMusicStateData.OnDownloadStartMusic -> DownloadMusicState.OnDownloadStartMusic(contentLength)
    is DownloadMusicStateData.OnDownloaded -> DownloadMusicState.OnDownloaded(downloadedByte)
    is DownloadMusicStateData.OnSaveFileFailed -> DownloadMusicState.OnSaveFileFailed(throwable)
}