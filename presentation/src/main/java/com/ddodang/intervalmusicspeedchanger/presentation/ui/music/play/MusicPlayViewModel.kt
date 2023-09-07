package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val currentPlayingMusicFlow = musicPlayer.currentPlayingMusicFlow

    val playTimeFlow = musicPlayer.musicPlayingInformationFlow.map {
        it.playTimeMillis
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun setMusicPosition(musicPositionMillis: Int) {
        musicPlayer.setMusicPosition(musicPositionMillis)
    }
}