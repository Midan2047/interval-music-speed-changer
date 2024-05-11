package com.ddodang.intervalmusicspeedchanger.presentation.model

data class MusicDownloadStateItem(
    val downloadInformation: DownloadInformation? = null,
    val downloadError: DownloadError? = null,
) {

    data class DownloadInformation(
        val contentLength: Long,
        val downloadedByteLength: Long,
        val downloadedPercentage: Int,
    )

    sealed class DownloadError(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : Throwable(message = message, cause = cause) {

        data class FetchDownloadLinkFailed(
            val throwable: Throwable,
        ) : DownloadError(message = throwable.message, cause = throwable)

        data class DownloadMusicFailed(
            val throwable: Throwable?,
        ) : DownloadError(throwable?.message, throwable)

        object InvalidLink : DownloadError("링크가 이상해요!")
        object OnSaveFileFailed : DownloadError("파일 저장 실패해따!")
    }
}