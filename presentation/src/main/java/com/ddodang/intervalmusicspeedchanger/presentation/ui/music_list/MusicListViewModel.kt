package com.ddodang.intervalmusicspeedchanger.presentation.ui.music_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.usecase.AddMusicUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.DeleteMusicUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchMusicListUseCase
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

    fun loadMusicList() {
        viewModelScope.launch {
            fetchMusicListUseCase().onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
        }
    }

    fun copyMusic(filePath: String) {
        viewModelScope.launch {
            addMusicUseCase(filePath).onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
        }
    }

    fun deleteMusic(music: Music) {
        viewModelScope.launch {
            deleteMusicUseCase(music.location).onSuccess { musicList ->
                _musicListFlow.value = musicList
            }
        }
    }
}