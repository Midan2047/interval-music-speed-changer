package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.usecase.DeleteMusicUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchIntervalSettingUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchMusicListUseCase
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

    private val _isRefreshingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshingFlow = _isRefreshingFlow.asStateFlow()

    val currentMusicFlow = musicPlayer.currentPlayingMusicFlow

    fun loadMusicList(doSilent: Boolean = false) {
        viewModelScope.launch {
            _isRefreshingFlow.value = true && !doSilent
            fetchMusicListUseCase().onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
            _isRefreshingFlow.value = false
        }
    }

    fun deleteMusic(music: Music) {
        viewModelScope.launch {
            deleteMusicUseCase(music.location).onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
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