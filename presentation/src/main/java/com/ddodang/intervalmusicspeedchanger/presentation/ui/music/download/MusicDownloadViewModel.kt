package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.download

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadMusicState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import com.ddodang.intervalmusicspeedchanger.domain.usecase.ExtractYouTubeSoundUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.SearchYouTubeVideoListUseCase
import com.ddodang.intervalmusicspeedchanger.presentation.model.MusicDownloadStateItem
import com.ddodang.intervalmusicspeedchanger.presentation.model.MusicSearchLoadingState
import com.ddodang.intervalmusicspeedchanger.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicDownloadViewModel @Inject constructor(
    private val searchYouTubeVideoList: SearchYouTubeVideoListUseCase,
    private val extractYouTubeSound: ExtractYouTubeSoundUseCase,
) : BaseViewModel() {

    private val searchKeywordFlow = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val videoSearchResultListFlow = searchKeywordFlow.flatMapLatest { keyword ->
        if (keyword.isNotEmpty()) {
            Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = { VideoSearchResultPagingSource(searchYouTubeVideoList, keyword) }
            ).flow.cachedIn(viewModelScope)
        } else {
            flowOf()
        }
    }

    private val _musicDownloadStateFlow = MutableStateFlow(MusicDownloadStateItem(downloadInformation = null, downloadError = null))
    val musicDownloadStateFlow = _musicDownloadStateFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow<MusicSearchLoadingState>(MusicSearchLoadingState.None)
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    fun search(keyWord: String) {
        searchKeywordFlow.value = keyWord
    }

    fun download(searchResult: YouTubeSearchResult.VideoInfo) {
        viewModelScope.launch {
            extractYouTubeSound(searchResult.videoId, searchResult.title)
                .flowOn(Dispatchers.IO)
                .onStart { _loadingStateFlow.value = MusicSearchLoadingState.PrepareDownload }
                .collect { state ->
                    when (state) {
                        is DownloadMusicState.DownloadMusicFailed -> setErrorState(MusicDownloadStateItem.DownloadError.DownloadMusicFailed(state.throwable))
                        is DownloadMusicState.FetchMusicLinkFailed -> setErrorState(MusicDownloadStateItem.DownloadError.FetchDownloadLinkFailed(state.throwable))
                        is DownloadMusicState.InvalidLink -> setErrorState(MusicDownloadStateItem.DownloadError.InvalidLink)
                        is DownloadMusicState.OnSaveFileFailed -> setErrorState(MusicDownloadStateItem.DownloadError.OnSaveFileFailed)
                        DownloadMusicState.OnDownloadDoneMusic -> {
                            _musicDownloadStateFlow.update { musicDownloadState ->
                                musicDownloadState.copy(
                                    downloadInformation = musicDownloadState.downloadInformation?.let {
                                        it.copy(
                                            downloadedByteLength = it.downloadedByteLength,
                                            downloadedPercentage = 100
                                        )
                                    }
                                )
                            }
                        }

                        is DownloadMusicState.OnDownloadStartMusic -> {
                            _loadingStateFlow.value = MusicSearchLoadingState.None
                            _musicDownloadStateFlow.update { musicDownloadState ->
                                musicDownloadState.copy(
                                    downloadInformation = MusicDownloadStateItem.DownloadInformation(
                                        contentLength = state.contentLength,
                                        downloadedByteLength = 0,
                                        downloadedPercentage = 0
                                    )
                                )
                            }
                        }

                        is DownloadMusicState.OnDownloaded -> {
                            _musicDownloadStateFlow.update { musicDownloadState ->
                                musicDownloadState.copy(
                                    downloadInformation = musicDownloadState.downloadInformation?.let {
                                        it.copy(
                                            downloadedByteLength = it.downloadedByteLength + state.downloadedByte,
                                            downloadedPercentage = ((it.downloadedByteLength + state.downloadedByte) * 100 / it.contentLength).toInt()
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun setErrorState(error: MusicDownloadStateItem.DownloadError) {
        _loadingStateFlow.value = MusicSearchLoadingState.None
        _musicDownloadStateFlow.update {
            it.copy(
                downloadInformation = null,
                downloadError = error
            )
        }
    }

    fun downloadDone() {
        _musicDownloadStateFlow.update { musicDownloadState ->
            musicDownloadState.copy(downloadInformation = null, downloadError = null)
        }
    }
}