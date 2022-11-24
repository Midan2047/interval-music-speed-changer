package com.ddodang.intervalmusicspeedchanger.domain.usecase

import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.repository.MusicRepository
import javax.inject.Inject

class FetchIntervalSettingUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
) {

    suspend operator fun invoke(): Result<IntervalSetting> = musicRepository.fetchIntervalSettings()
}