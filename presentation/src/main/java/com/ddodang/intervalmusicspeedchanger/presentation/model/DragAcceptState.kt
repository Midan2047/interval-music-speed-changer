package com.ddodang.intervalmusicspeedchanger.presentation.model

sealed interface DragAcceptState {

    object None : DragAcceptState
    object Accept : DragAcceptState
    object Decline : DragAcceptState
}