package com.ddodang.intervalmusicspeedchanger.data.util

import com.ddodang.intervalmusicspeedchanger.data.model.MusicDownloadModel

interface FileUtil {

    fun getFileDownloadModel(fileName: String): MusicDownloadModel
}