package com.ddodang.intervalmusicspeedchanger.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddodang.intervalmusicspeedchanger.domain.model.IntervalSetting
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchIntervalSettingUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.UpdateIntervalSettingUseCase
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fetchIntervalSettingUseCase: FetchIntervalSettingUseCase,
    private val updateIntervalSettingUseCase: UpdateIntervalSettingUseCase,
    private val musicPlayer: MusicPlayer,
) : ViewModel() {

    private val _intervalSettingFlow = MutableStateFlow(IntervalSetting(1, 1, 1))
    val intervalSettingFlow = _intervalSettingFlow.asStateFlow()

    fun initialize() {
        viewModelScope.launch {
            _intervalSettingFlow.value = fetchIntervalSettingUseCase().getOrDefault(_intervalSettingFlow.value)
        }
    }

    fun increaseSet() {
        _intervalSettingFlow.update {
            it.copy(setCount = it.setCount + 1)
        }
    }

    fun increaseWalkingMinute() {
        _intervalSettingFlow.update {
            it.copy(walkingMinutes = it.walkingMinutes + 1)
        }
    }

    fun increaseRunningMinute() {
        _intervalSettingFlow.update {
            it.copy(runningMinutes = it.runningMinutes + 1)
        }
    }

    fun decreaseSet() {
        _intervalSettingFlow.update {
            it.copy(setCount = (it.setCount - 1).coerceAtLeast(1))
        }
    }

    fun decreaseWalkingMinute() {
        _intervalSettingFlow.update {
            it.copy(walkingMinutes = (it.walkingMinutes - 1).coerceAtLeast(1))
        }
    }

    fun decreaseRunningMinute() {
        _intervalSettingFlow.update {
            it.copy(runningMinutes = (it.runningMinutes - 1).coerceAtLeast(1))
        }
    }

    fun saveIntervalSettings() {
        viewModelScope.launch {
            updateIntervalSettingUseCase(intervalSettingFlow.value).onSuccess {
                musicPlayer.setInterval(it)
            }
        }
    }

}