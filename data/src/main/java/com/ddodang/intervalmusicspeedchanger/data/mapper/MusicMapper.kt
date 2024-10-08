package com.ddodang.intervalmusicspeedchanger.data.mapper

import com.ddodang.intervalmusicspeedchanger.data.model.IntervalSettingData
import com.ddodang.intervalmusicspeedchanger.data.model.MusicData
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music

internal fun MusicData.toDomain() = Music(
    id = id,
    artist = artist,
    title = title,
    location = location,
    durationInMillis = durationInMillis,
)

internal fun IntervalSettingData.toDomain() = IntervalSetting(
    setCount = setCount, walkingMinutes = walkingMinutes, runningMinutes = runningMinutes
)

internal fun IntervalSetting.toData() = IntervalSettingData(
    setCount = setCount, walkingMinutes = walkingMinutes, runningMinutes = runningMinutes
)