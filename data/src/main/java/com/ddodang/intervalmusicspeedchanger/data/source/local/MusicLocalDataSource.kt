package com.ddodang.intervalmusicspeedchanger.data.source.local

import com.ddodang.intervalmusicspeedchanger.data.model.IntervalSettingData
import com.ddodang.intervalmusicspeedchanger.data.model.MusicData

interface MusicLocalDataSource {

    suspend fun fetchMusicList(): Result<List<MusicData>>
    suspend fun fetchIntervalSettings(): Result<IntervalSettingData>
    suspend fun updateIntervalSettings(intervalSetting: IntervalSettingData): Result<Unit>
    suspend fun deleteMusic(filePath: String): Result<Unit>
}