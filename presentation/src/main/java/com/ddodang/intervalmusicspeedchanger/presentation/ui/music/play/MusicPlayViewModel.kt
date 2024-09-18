package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.presentation.model.RepeatMode
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MusicPlayViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer,
) : ViewModel() {

    val isPlayingFlow = musicPlayer.musicPlayingInformationFlow.map {
        it.isPlaying
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500L), false)

    val currentPlayingMusicFlow = musicPlayer.currentPlayingMusicFlow

    val playTimeFlow = musicPlayer.musicPlayingInformationFlow.map {
        it.playTimeMillis.toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500L), 0)

    val shuffleEnabledFlow = musicPlayer.musicPlayingInformationFlow.map {
        it.shuffle
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500L), false)

    val repeatModeFlow = musicPlayer.musicPlayingInformationFlow.map {
        RepeatMode.parse(it.repeatMode)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500L), RepeatMode.All)

    fun setMusicPosition(musicPositionMillis: Int) {
        musicPlayer.setMusicPosition(musicPositionMillis)
    }
}