package com.ddodang.intervalmusicspeedchanger.domain.usecase

import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import com.ddodang.intervalmusicspeedchanger.domain.repository.YouTubeRepository
import javax.inject.Inject

class SearchYouTubeVideoListUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
) {

    suspend operator fun invoke(searchKeyword: String): Result<YouTubeSearchResult> {
        return youTubeRepository.searchYouTubeByKeyword(searchKeyword)
    }
}