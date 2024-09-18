package com.ddodang.intervalmusicspeedchanger.domain.model

sealed interface DownloadMusicState {

    data class FetchMusicLinkFailed(
        val throwable: Throwable,
    ) : DownloadMusicState

    data class InvalidLink(
        val throwable: Throwable?,
    ) : DownloadMusicState

    data class DownloadMusicFailed(
        val throwable: Throwable?,
    ) : DownloadMusicState

    data class OnDownloadStartMusic(
        val contentLength: Long,
    ) : DownloadMusicState

    data class OnDownloaded(
        val downloadedByte: Int,
    ) : DownloadMusicState

    object OnDownloadDoneMusic : DownloadMusicState

    data class OnSaveFileFailed(
        val throwable: Throwable?,
    ) : DownloadMusicState
}