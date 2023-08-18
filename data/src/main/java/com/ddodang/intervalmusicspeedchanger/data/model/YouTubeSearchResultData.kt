package com.ddodang.intervalmusicspeedchanger.data.model

data class YouTubeSearchResultData(
    val nextPageToken: String?,
    val videoList: List<VideoInfo>,
) {

    data class VideoInfo(
        val videoId: String,
        val title: String,
        val thumbnailUrl: String?,
    )
}