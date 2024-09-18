package com.ddodang.intervalmusicspeedchanger.data.model

sealed interface DownloadMusicStateData {

    data class FetchMusicLinkFailed(
        val throwable: Throwable,
    ) : Exception(throwable.message, throwable), DownloadMusicStateData

    data class InvalidLink(
        val throwable: Throwable,
    ) : Exception(throwable.message, throwable), DownloadMusicStateData

    data class DownloadMusicFailed(
        val throwable: Throwable,
    ) : Exception(throwable.message, throwable), DownloadMusicStateData

    data class OnDownloadStartMusic(
        val contentLength: Long,
    ) : DownloadMusicStateData

    data class OnDownloaded(
        val downloadedByte: Int,
    ) : DownloadMusicStateData

    object OnDownloadDoneMusic : DownloadMusicStateData

    data class OnSaveFileFailed(
        val throwable: Throwable,
    ) : Exception(throwable.message, throwable), DownloadMusicStateData
}