package com.ddodang.intervalmusicspeedchanger.domain.model

data class YouTubeSearchResult(
    val nextPageToken: String?,
    val videoList: List<VideoInfo>,
) {
    data class VideoInfo(
        val videoId: String,
        val title: String,
        val thumbnail: String?,
    )
}