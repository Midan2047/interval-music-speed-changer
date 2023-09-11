package com.ddodang.intervalmusicspeedchanger.local.source

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import com.ddodang.intervalmusicspeedchanger.data.model.IntervalSettingData
import com.ddodang.intervalmusicspeedchanger.data.model.MusicData
import com.ddodang.intervalmusicspeedchanger.data.source.local.MusicLocalDataSource
import com.ddodang.intervalmusicspeedchanger.local.util.SharedManagerUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.net.URLDecoder
import javax.inject.Inject

internal class MusicLocalDataSourceImpl @Inject constructor(
    private val sharedManagerUtil: SharedManagerUtil,
    @ApplicationContext private val context: Context,
) : MusicLocalDataSource {

    override suspend fun fetchMusicList(): Result<List<MusicData>> = withContext(Dispatchers.IO) {
        runCatching {
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                ?: return@runCatching emptyList()
            directory.listFiles()?.mapNotNull { file ->
                val metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.setDataSource(FileInputStream(file).fd)
                val title =
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        ?: file.nameWithoutExtension
                val artist =
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                        ?: "알수 없는 가수"
                val duration =
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toIntOrNull()
                        ?: 0
                MusicData(
                    id = file.absolutePath.toHashSet().joinToString(""),
                    title = URLDecoder.decode(title, "UTF-8"),
                    artist = artist,
                    durationInMillis = duration,
                    location = file.absolutePath
                )
            } ?: emptyList()
        }
    }

    override suspend fun fetchIntervalSettings(): Result<IntervalSettingData> =
        withContext(Dispatchers.IO) {
            runCatching {
                IntervalSettingData(
                    setCount = sharedManagerUtil.setCount,
                    walkingMinutes = sharedManagerUtil.walkingTime,
                    runningMinutes = sharedManagerUtil.runningTime
                )
            }
        }

    override suspend fun updateIntervalSettings(intervalSetting: IntervalSettingData): Result<Unit> =
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                sharedManagerUtil.setCount = intervalSetting.setCount
                sharedManagerUtil.walkingTime = intervalSetting.walkingMinutes
                sharedManagerUtil.runningTime = intervalSetting.runningMinutes
            }
        }

    override suspend fun deleteMusic(filePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val deleteFile = File(filePath)
            if (deleteFile.exists()) deleteFile.delete()
        }
    }

}