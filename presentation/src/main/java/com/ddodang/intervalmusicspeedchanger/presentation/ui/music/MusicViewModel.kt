package com.ddodang.intervalmusicspeedchanger.presentation.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor() : ViewModel() {

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun showMusicPlayFragment() {
        viewModelScope.launch {
            _eventFlow.emit(Event.ShowMusicPlayFragment)
        }
    }

    sealed class Event {

        object ShowMusicPlayFragment : Event()
    }
}