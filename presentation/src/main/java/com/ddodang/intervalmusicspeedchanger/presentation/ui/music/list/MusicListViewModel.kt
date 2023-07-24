package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.usecase.AddMusicUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.DeleteMusicUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchIntervalSettingUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchMusicListUseCase
import com.ddodang.intervalmusicspeedchanger.presentation.model.LoadingState
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val fetchMusicListUseCase: FetchMusicListUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val fetchIntervalSettings: FetchIntervalSettingUseCase,
    private val musicPlayer: MusicPlayer,
) : ViewModel() {

    private val _musicListFlow: MutableStateFlow<List<Music>> = MutableStateFlow(emptyList())
    val musicListFlow = _musicListFlow.asStateFlow()

    private val _isRefreshingFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isRefreshingFlow = _isRefreshingFlow.asStateFlow()

    private val _loadingFlow: MutableStateFlow<LoadingState> = MutableStateFlow(LoadingState.NotShowing)

    init {
        loadMusicList()
    }

    fun loadMusicList() {
        viewModelScope.launch {
            _isRefreshingFlow.value = true
            fetchMusicListUseCase().onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
            _isRefreshingFlow.value = false
        }
    }

    fun deleteMusic(music: Music) {
        viewModelScope.launch {
            _loadingFlow.value = LoadingState.Show("음악을 삭제하는 중입니다.")
            deleteMusicUseCase(music.location).onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
            _loadingFlow.value = LoadingState.NotShowing
        }
    }

    fun setMusic(music: Music) {
        viewModelScope.launch {
            musicPlayer.setMusicList(musicListFlow.value)
            val interval = fetchIntervalSettings().getOrDefault(IntervalSetting(1, 1, 1))
            musicPlayer.initialize(music, interval)
        }
    }
}