package com.ddodang.intervalmusicspeedchanger.presentation.model

sealed class Screen(val route: String) {

    object MusicList : Screen("musicList")
    object Settings : Screen("settings")
    object Download : Screen("download")
    object MusicPlay : Screen("music")
}
