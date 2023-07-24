package com.ddodang.intervalmusicspeedchanger.data.source.remote

import com.ddodang.intervalmusicspeedchanger.data.model.DownloadStateData
import com.ddodang.intervalmusicspeedchanger.data.model.YouTubeSearchResultData

interface YouTubeRemoteDataSource {

    suspend fun fetchSearchResult(searchKey: String): Result<List<YouTubeSearchResultData>>

    suspend fun extractYouTubeSound(videoId: String, videoName: String): DownloadStateData
}