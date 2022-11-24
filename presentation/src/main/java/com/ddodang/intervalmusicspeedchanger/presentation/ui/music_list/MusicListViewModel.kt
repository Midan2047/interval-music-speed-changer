package com.ddodang.intervalmusicspeedchanger.presentation.ui.music_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.usecase.AddMusicUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.DeleteMusicUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchMusicListUseCase
import com.ddodang.intervalmusicspeedchanger.presentation.model.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val addMusicUseCase: AddMusicUseCase,
    private val fetchMusicListUseCase: FetchMusicListUseCase,
    private val deleteMusicUseCase : DeleteMusicUseCase
) : ViewModel() {

    private val _musicListFlow: MutableStateFlow<List<Music>> = MutableStateFlow(emptyList())
    val musicListFlow = _musicListFlow.asStateFlow()

    private val _loadingFlow : MutableStateFlow<LoadingState> = MutableStateFlow(LoadingState.NotShowing)
    val loadingFlow = _loadingFlow.asStateFlow()

    fun loadMusicList() {
        viewModelScope.launch {
            _loadingFlow.value = LoadingState.Show("음악을 불러오는 중입니다.")
            fetchMusicListUseCase().onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
            _loadingFlow.value = LoadingState.NotShowing
        }
    }

    fun copyMusic(filePath: String) {
        viewModelScope.launch {
            _loadingFlow.value = LoadingState.Show("음악을 이동 시키는 중입니다.")
            addMusicUseCase(filePath).onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
            _loadingFlow.value = LoadingState.NotShowing
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
}