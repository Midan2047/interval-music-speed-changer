package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

import androidx.lifecycle.ViewModel
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicPlayViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer,
) : ViewModel() {

    val isPlayingFlow = musicPlayer.isPlayingFlow
    val currentPlayingMusicFlow = musicPlayer.currentPlayingMusicFlow
    val playTimeFlow = musicPlayer.playTimeMillisFlow

    fun setMusicPosition(musicPositionMillis: Int) {
        musicPlayer.setMusicPosition(musicPositionMillis)
    }
}