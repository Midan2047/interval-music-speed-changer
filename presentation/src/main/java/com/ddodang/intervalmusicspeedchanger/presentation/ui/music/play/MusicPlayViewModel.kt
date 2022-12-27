package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

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
    musicPlayer: MusicPlayer,
) : ViewModel() {

    val isPlayingFlow = musicPlayer.isPlayingFlow
    val currentPlayingMusicFlow = musicPlayer.currentPlayingMusicFlow
}