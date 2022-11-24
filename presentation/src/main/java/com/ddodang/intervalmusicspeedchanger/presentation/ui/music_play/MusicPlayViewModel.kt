package com.ddodang.intervalmusicspeedchanger.presentation.ui.music_play

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchIntervalSettingUseCase
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val musicPlayer: MusicPlayer,
    private val fetchIntervalSettings: FetchIntervalSettingUseCase,
) : ViewModel() {

    val isPlayingFlow = musicPlayer.isPlayingFlow
    val currentPlayingMusicFlow = musicPlayer.currentPlayingMusicFlow

    fun initialize() {
        viewModelScope.launch {
            val selectedMusic = savedStateHandle.get<Music>("selectedMusic")!!
            val interval = fetchIntervalSettings().getOrDefault(IntervalSetting(1, 1, 1))
            musicPlayer.initialize(selectedMusic, interval)
        }
    }
}