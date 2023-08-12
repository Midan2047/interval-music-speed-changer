package com.ddodang.intervalmusicspeedchanger.presentation.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

fun retrieveThumbnailBitmapFromFile(filePath: String): Bitmap? {
    return runCatching {
        MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(filePath)
            val embeddedPictureByteArray = retriever.embeddedPicture!!
            BitmapFactory.decodeByteArray(embeddedPictureByteArray, 0, embeddedPictureByteArray.size)
        }
    }.getOrNull()
}
