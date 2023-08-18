package com.ddodang.intervalmusicspeedchanger.domain.usecase

import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import com.ddodang.intervalmusicspeedchanger.domain.repository.YouTubeRepository
import javax.inject.Inject

class LoadMoreVideoListUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
) {

    suspend operator fun invoke(keyword: String?, nextPageToken: String?): Result<YouTubeSearchResult> {
        if (nextPageToken == null || keyword == null) return Result.failure(Exception("Next Page Not Found"))
        return youTubeRepository.loadMoreVideo(keyword, nextPageToken)
    }
}