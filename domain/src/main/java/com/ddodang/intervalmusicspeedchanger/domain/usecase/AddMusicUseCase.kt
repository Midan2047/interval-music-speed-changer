package com.ddodang.intervalmusicspeedchanger.domain.usecase

import com.ddodang.intervalmusicspeedchanger.common.extensions.flatMap
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.repository.MusicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
) {

    suspend operator fun invoke(filePath: String): Result<List<Music>> {
        return musicRepository.copyMusic(filePath).flatMap {
            musicRepository.fetchMusicList()
        }
    }
}