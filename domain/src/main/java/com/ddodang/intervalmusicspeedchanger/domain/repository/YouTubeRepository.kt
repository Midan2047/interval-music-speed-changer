package com.ddodang.intervalmusicspeedchanger.domain.repository

import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadState
import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadMusicState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import kotlinx.coroutines.flow.Flow

interface YouTubeRepository {

    suspend fun searchYouTubeByKeyword(keyword: String): Flow<YouTubeSearchResult>
    suspend fun loadMoreVideo(keyword: String, nextPageToken: String): Result<YouTubeSearchResult>

    suspend fun extractYouTubeMusic(videoId: String, videoName: String): DownloadState
    suspend fun extractYouTubeMusicFlow(videoId: String, videoName: String) : Flow<DownloadMusicState>
}