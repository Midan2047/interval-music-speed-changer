package com.ddodang.intervalmusicspeedchanger.domain.usecase

import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadMusicState
import com.ddodang.intervalmusicspeedchanger.domain.repository.YouTubeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExtractYouTubeSoundUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
) {

    suspend operator fun invoke(videoId: String, videoName: String): Flow<DownloadMusicState> {
        return youTubeRepository.extractYouTubeMusicFlow(videoId, videoName)
    }
}