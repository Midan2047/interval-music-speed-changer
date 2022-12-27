package com.ddodang.intervalmusicspeedchanger.local.source

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.ddodang.intervalmusicspeedchanger.data.model.IntervalSettingData
import com.ddodang.intervalmusicspeedchanger.data.model.MusicData
import com.ddodang.intervalmusicspeedchanger.data.source.local.MusicLocalDataSource
import com.ddodang.intervalmusicspeedchanger.local.util.SharedManagerUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
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
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?: ""
                MusicData(
                    id = file.absolutePath.toHashSet().joinToString(""),
                    title = title,
                    artist = artist,
                    duration = duration,
                    location = file.absolutePath
                )
            } ?: emptyList()
        }.onFailure {
            println(it)
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

    override suspend fun copyMusic(fileUriPath: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val inputFileUri = Uri.parse(fileUriPath)
                val documentFile = DocumentFile.fromSingleUri(context, inputFileUri) ?: throw Exception("Not Document File")
                val inputStream = context.contentResolver.openInputStream(inputFileUri) ?: throw FileNotFoundException()
                val file =
                    File(
                        context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                        documentFile.name!!
                    )
                file.createNewFile()
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                while (true) {
                    val length = inputStream.read(buffer)
                    if (length <= 0) break
                    outputStream.write(buffer, 0, length)
                }
                outputStream.close()
                inputStream.close()
                println("Copy Finished / ${file.absolutePath}")
            }.onFailure {
                println(it)
            }
        }

    override suspend fun deleteMusic(filePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val deleteFile = File(filePath)
            if (deleteFile.exists()) deleteFile.delete()
        }
    }
}