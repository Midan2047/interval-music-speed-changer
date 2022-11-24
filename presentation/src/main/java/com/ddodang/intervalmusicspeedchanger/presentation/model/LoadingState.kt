package com.ddodang.intervalmusicspeedchanger.presentation.model

sealed class LoadingState {

    object NotShowing : LoadingState()

    data class Show(
        val message: String
    ) : LoadingState()
}
