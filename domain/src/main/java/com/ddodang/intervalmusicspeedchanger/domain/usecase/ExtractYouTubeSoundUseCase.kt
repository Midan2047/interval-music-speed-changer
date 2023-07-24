package com.ddodang.intervalmusicspeedchanger.domain.usecase

import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadState
import com.ddodang.intervalmusicspeedchanger.domain.repository.YouTubeRepository
import javax.inject.Inject

class ExtractYouTubeSoundUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
) {

    suspend operator fun invoke(videoId: String, videoName: String): DownloadState {
        return youTubeRepository.extractYouTubeMusic(videoId, videoName)
    }
}