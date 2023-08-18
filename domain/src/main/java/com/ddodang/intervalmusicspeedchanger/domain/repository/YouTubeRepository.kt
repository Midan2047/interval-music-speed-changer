package com.ddodang.intervalmusicspeedchanger.domain.repository

import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult

interface YouTubeRepository {

    suspend fun searchYouTubeByKeyword(keyword: String): Result<YouTubeSearchResult>
    suspend fun loadMoreVideo(keyword: String, nextPageToken: String): Result<YouTubeSearchResult>

    suspend fun extractYouTubeMusic(videoId: String, videoName: String): DownloadState
}