package com.ddodang.intervalmusicspeedchanger.presentation.util

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.imageResource
import com.ddodang.intervalmusicspeedchanger.presentation.R

@Composable
fun retrieveThumbnailFromFile(filePath: String, @DrawableRes defaultImageId: Int = R.drawable.ikmyung_profile): ImageBitmap {
    if (LocalInspectionMode.current) return ImageBitmap.imageResource(id = defaultImageId)

    val retriever = MediaMetadataRetriever()
    return runCatching {
        retriever.setDataSource(filePath)
        val embeddedPictureByteArray = retriever.embeddedPicture!!
        BitmapFactory.decodeByteArray(embeddedPictureByteArray, 0, embeddedPictureByteArray.size)
    }.getOrNull()?.asImageBitmap() ?: ImageBitmap.imageResource(id = defaultImageId)
}

