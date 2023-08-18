package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.download

import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import com.ddodang.intervalmusicspeedchanger.domain.usecase.ExtractYouTubeSoundUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.LoadMoreVideoListUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.SearchYouTubeVideoListUseCase
import com.ddodang.intervalmusicspeedchanger.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicDownloadViewModel @Inject constructor(
    private val searchYouTubeVideoList: SearchYouTubeVideoListUseCase,
    private val loadMoreVideoList: LoadMoreVideoListUseCase,
    private val extractYouTubeSound: ExtractYouTubeSoundUseCase,
) : BaseViewModel() {

    private val _videoSearchResultListFlow = MutableStateFlow<List<YouTubeSearchResult.VideoInfo>>(emptyList())
    val videoSearchResultList = _videoSearchResultListFlow.asStateFlow()

    private val _downloadStateFlow = MutableStateFlow<DownloadState?>(null)
    val downloadedState = _downloadStateFlow.asStateFlow()

    private var searchKeyword: String? = null
    private var nextPageToken: String? = null

    fun search(keyWord: String) {
        viewModelScope.launch {
            searchKeyword = keyWord
            searchYouTubeVideoList(keyWord).onSuccess { youTubeSearchResult ->
                nextPageToken = youTubeSearchResult.nextPageToken
                _videoSearchResultListFlow.value = youTubeSearchResult.videoList
            }
        }
    }

    fun loadMore() {
        throttle(key = "KEY_BLOCK_LOAD_MORE", blockingTime = 0L) {
            loadMoreVideoList(searchKeyword, nextPageToken).onSuccess { youTubeSearchResult ->
                nextPageToken = youTubeSearchResult.nextPageToken
                _videoSearchResultListFlow.update { currentVisibleVideoList ->
                    currentVisibleVideoList + youTubeSearchResult.videoList
                }

            }
        }
    }

    fun download(searchResult: YouTubeSearchResult.VideoInfo) {
        viewModelScope.launch {
            _downloadStateFlow.value = extractYouTubeSound(searchResult.videoId, searchResult.title)
        }
    }

    fun downloadDone() {
        _downloadStateFlow.value = null
    }
}