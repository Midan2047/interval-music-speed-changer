package com.ddodang.intervalmusicspeedchanger.data.model

interface MusicDownloadModel {

    fun writeToFile(buffer: ByteArray, offset: Int, bytesRead: Int)

    fun close()
}