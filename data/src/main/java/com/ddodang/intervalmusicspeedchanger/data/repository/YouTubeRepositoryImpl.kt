package com.ddodang.intervalmusicspeedchanger.data.repository

import com.ddodang.intervalmusicspeedchanger.data.mapper.toDomain
import com.ddodang.intervalmusicspeedchanger.data.source.remote.YouTubeRemoteDataSource
import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import com.ddodang.intervalmusicspeedchanger.domain.repository.YouTubeRepository
import javax.inject.Inject

internal class YouTubeRepositoryImpl @Inject constructor(
    private val remote: YouTubeRemoteDataSource,
) : YouTubeRepository {

    override suspend fun searchYouTubeByKeyword(keyword: String): Result<List<YouTubeSearchResult>> {
        return remote.fetchSearchResult(keyword).map { searchedList ->
            searchedList.map { searchResultData ->
                searchResultData.toDomain()
            }
        }
    }

    override suspend fun extractYouTubeMusic(videoId: String, videoName: String): DownloadState {
        return remote.extractYouTubeSound(videoId, videoName).toDomain()
    }
}