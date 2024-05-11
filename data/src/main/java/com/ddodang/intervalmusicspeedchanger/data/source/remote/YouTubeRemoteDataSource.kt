package com.ddodang.intervalmusicspeedchanger.data.source.remote

import com.ddodang.intervalmusicspeedchanger.data.model.DownloadStateData
import com.ddodang.intervalmusicspeedchanger.data.model.DownloadMusicStateData
import com.ddodang.intervalmusicspeedchanger.data.model.YouTubeSearchResultData
import kotlinx.coroutines.flow.Flow

interface YouTubeRemoteDataSource {

    suspend fun fetchSearchResult(searchKey: String): Result<YouTubeSearchResultData>

    suspend fun loadMoreVideo(keyword: String, nextPageToken: String): Result<YouTubeSearchResultData>

    suspend fun extractYouTubeSound(videoId: String, videoName: String): DownloadStateData

    suspend fun extractYouTubeSoundFlow(videoId: String, videoName: String): Flow<DownloadMusicStateData>
}