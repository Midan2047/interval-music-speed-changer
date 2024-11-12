package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.download

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import com.ddodang.intervalmusicspeedchanger.domain.usecase.SearchYouTubeVideoListUseCase

class VideoSearchResultPagingSource(
    private val searchYouTubeVideoList: SearchYouTubeVideoListUseCase,
    private val queryString: String,
) : PagingSource<String, YouTubeSearchResult.VideoInfo>() {

    private var tokenList: List<String?> = listOf(null)

    override fun getRefreshKey(state: PagingState<String, YouTubeSearchResult.VideoInfo>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            tokenList.getOrNull(anchorPosition)
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, YouTubeSearchResult.VideoInfo> {
        return searchYouTubeVideoList(queryString, params.key).map { searchResult ->
            val tokenIndex = tokenList.indexOfFirst { it == params.key }.takeIf { it >= 0 } ?: 0
            val prevToken = tokenList.getOrNull(tokenIndex - 1)
            tokenList = tokenList + searchResult.nextPageToken
            LoadResult.Page(searchResult.videoList, prevToken, searchResult.nextPageToken)
        }.recover {
            LoadResult.Error(it)
        }.getOrDefault(LoadResult.Invalid())

    }
}