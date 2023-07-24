package com.ddodang.intervalmusicspeedchanger.local.util

import android.content.Context
import android.os.Environment
import com.ddodang.intervalmusicspeedchanger.data.model.MusicDownloadModel
import com.ddodang.intervalmusicspeedchanger.data.util.FileUtil
import com.ddodang.intervalmusicspeedchanger.local.model.MusicDownloadModelImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject

class FileUtilImpl @Inject constructor(
    @ApplicationContext val context: Context,
) : FileUtil {

    override fun getFileDownloadModel(fileName: String): MusicDownloadModel {
        val cacheDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: throw IllegalStateException()
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val encodedString = URLEncoder.encode(fileName, "UTF-8")
        val file = File(cacheDir, "${encodedString}.mp3")

        return MusicDownloadModelImpl(file)
    }
}