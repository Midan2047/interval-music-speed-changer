package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.domain.model.DownloadState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import com.ddodang.intervalmusicspeedchanger.domain.usecase.ExtractYouTubeSoundUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.SearchYouTubeVideoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicDownloadViewModel @Inject constructor(
    private val searchYouTubeVideoList: SearchYouTubeVideoListUseCase,
    private val extractYouTubeSound: ExtractYouTubeSoundUseCase,
) : ViewModel() {

    private val _videoSearchResultListFlow = MutableStateFlow<List<YouTubeSearchResult>>(emptyList())
    val videoSearchResultList = _videoSearchResultListFlow.asStateFlow()

    private val _downloadStateFlow = MutableStateFlow<DownloadState?>(null)
    val downloadedState = _downloadStateFlow.asStateFlow()

    fun search(keyWord: String) {
        viewModelScope.launch {
            _videoSearchResultListFlow.value = searchYouTubeVideoList(keyWord).getOrDefault(emptyList())
        }
    }

    fun download(searchResult: YouTubeSearchResult) {
        viewModelScope.launch {
            _downloadStateFlow.value = extractYouTubeSound(searchResult.videoId, searchResult.title)
        }
    }

    fun downloadDone() {
        _downloadStateFlow.value = null
    }
}