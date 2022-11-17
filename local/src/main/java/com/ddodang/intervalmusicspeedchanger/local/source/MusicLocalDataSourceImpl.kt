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
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MusicLocalDataSourceImpl @Inject constructor(
    private val sharedManagerUtil: SharedManagerUtil,
    @ApplicationContext private val context: Context,
) : MusicLocalDataSource {

    override suspend fun fetchMusicList(): Result<List<MusicData>> = withContext(Dispatchers.IO) {
        runCatching {
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: return@runCatching emptyList()
            directory.listFiles()?.mapNotNull { file ->
                val metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.setDataSource(file.absolutePath)
                val title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.nameWithoutExtension
                val artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "알수 없는 가수"
                val duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: ""
                MusicData(
                    id = file.absolutePath.toHashSet().joinToString(""),
                    title = title,
                    artist = artist,
                    duration = duration,
                    location = file.absolutePath
                )
            } ?: emptyList()
        }
    }

    override suspend fun fetchIntervalSettings(): Result<IntervalSettingData> = withContext(Dispatchers.IO) {
        runCatching {
            IntervalSettingData(
                setCount = sharedManagerUtil.setCount,
                walkingMinutes = sharedManagerUtil.walkingTime,
                runningMinutes = sharedManagerUtil.runningTime
            )
        }
    }

    override suspend fun copyMusic(filePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val inputFile = File(filePath)
            val inputStream = inputFile.inputStream()
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), inputFile.name)
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            do {
                length = inputStream.read(buffer)
                outputStream.write(buffer, 0, length)
            } while (length > 0)
            outputStream.close()
            inputStream.close()
        }
    }

    override suspend fun deleteMusic(filePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val deleteFile = File(filePath)
            if (deleteFile.exists()) deleteFile.delete()
        }
    }
}