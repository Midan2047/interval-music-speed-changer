package com.ddodang.intervalmusicspeedchanger.domain.repository

import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music

interface MusicRepository {

    suspend fun fetchMusicList(): Result<List<Music>>
    suspend fun fetchIntervalSettings(): Result<IntervalSetting>
    suspend fun updateIntervalSettings(intervalSettings: IntervalSetting): Result<Unit>
    suspend fun copyMusic(filePath: String): Result<Unit>
    suspend fun deleteMusic(filePath: String): Result<Unit>
}