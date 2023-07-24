package com.ddodang.intervalmusicspeedchanger.local.model

import com.ddodang.intervalmusicspeedchanger.data.model.MusicDownloadModel
import java.io.File
import java.io.FileOutputStream

class MusicDownloadModelImpl(file: File) : MusicDownloadModel {

    private val outputStream = FileOutputStream(file)

    override fun writeToFile(buffer: ByteArray, offset: Int, bytesRead: Int) {
        outputStream.write(buffer, offset, bytesRead)
    }

    override fun close() {
        outputStream.flush()
        outputStream.close()
    }
}