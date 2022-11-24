package com.ddodang.intervalmusicspeedchanger.domain.usecase

import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.repository.MusicRepository
import javax.inject.Inject

class UpdateIntervalSettingUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
) {

    suspend operator fun invoke(intervalSetting: IntervalSetting): Result<IntervalSetting> =
        musicRepository.updateIntervalSettings(intervalSetting).map { intervalSetting }

}