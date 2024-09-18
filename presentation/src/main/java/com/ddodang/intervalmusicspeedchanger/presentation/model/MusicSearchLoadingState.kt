package com.ddodang.intervalmusicspeedchanger.presentation.model

sealed class MusicSearchLoadingState {

    object None : MusicSearchLoadingState()

    object Searching : MusicSearchLoadingState()

    object PrepareDownload : MusicSearchLoadingState()
}