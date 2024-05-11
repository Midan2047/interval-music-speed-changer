package com.ddodang.intervalmusicspeedchanger.data.model

sealed interface DownloadMusicStateData {

    data class FetchMusicLinkFailed(
        val throwable: Throwable,
    ) : DownloadMusicStateData

    object InvalidLink : DownloadMusicStateData

    data class DownloadMusicFailed(
        val throwable: Throwable?,
    ) : DownloadMusicStateData

    data class OnDownloadStartMusic(
        val contentLength: Long,
    ) : DownloadMusicStateData

    data class OnDownloaded(
        val downloadedByte: Int,
    ) : DownloadMusicStateData

    object OnDownloadDoneMusic : DownloadMusicStateData

    object OnSaveFileFailed : DownloadMusicStateData
}