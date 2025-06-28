package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.presentation.model.RepeatMode
import com.ddodang.intervalmusicspeedchanger.presentation.util.IntervalMusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MusicPlayViewModel @Inject constructor(
    private val intervalMusicPlayer: IntervalMusicPlayer,
) : ViewModel() {

    val isPlayingFlow = intervalMusicPlayer.musicPlayingInformationFlow.map {
        it.isPlaying
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500L), false)

    val currentPlayingMusicFlow = intervalMusicPlayer.currentPlayingMusicFlow

    val playTimeFlow = intervalMusicPlayer.musicPlayingInformationFlow.map {
        it.playTimeMillis.toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500L), 0)

    val shuffleEnabledFlow = intervalMusicPlayer.musicPlayingInformationFlow.map {
        it.shuffle
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500L), false)

    val repeatModeFlow = intervalMusicPlayer.musicPlayingInformationFlow.map {
        RepeatMode.parse(it.repeatMode)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500L), RepeatMode.All)

    fun setMusicPosition(musicPositionMillis: Int) {
        intervalMusicPlayer.setMusicPosition(musicPositionMillis)
    }
}