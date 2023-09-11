package com.ddodang.intervalmusicspeedchanger.data.repository

import com.ddodang.intervalmusicspeedchanger.data.mapper.toData
import com.ddodang.intervalmusicspeedchanger.data.mapper.toDomain
import com.ddodang.intervalmusicspeedchanger.data.source.local.MusicLocalDataSource
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.repository.MusicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MusicRepositoryImpl @Inject constructor(
    private val local: MusicLocalDataSource,
) : MusicRepository {

    override suspend fun fetchMusicList(): Result<List<Music>> =
        local.fetchMusicList().map { list ->
            list.map { musicData -> musicData.toDomain() }
        }

    override suspend fun fetchIntervalSettings(): Result<IntervalSetting> =
        local.fetchIntervalSettings().map { settingData -> settingData.toDomain() }

    override suspend fun updateIntervalSettings(intervalSettings: IntervalSetting): Result<Unit> =
        local.updateIntervalSettings(intervalSettings.toData())

    override suspend fun deleteMusic(filePath: String): Result<Unit> = local.deleteMusic(filePath)
}