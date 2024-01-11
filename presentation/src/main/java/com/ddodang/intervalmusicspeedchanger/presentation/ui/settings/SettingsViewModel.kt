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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fetchIntervalSettingUseCase: FetchIntervalSettingUseCase,
    private val updateIntervalSettingUseCase: UpdateIntervalSettingUseCase,
    private val musicPlayer: MusicPlayer,
) : ViewModel() {

    private val _setCountFlow = MutableStateFlow(1)
    val setCountFlow = _setCountFlow.asStateFlow()

    private val _walkingMinuteFlow = MutableStateFlow(1)
    val walkingMinuteFlow = _walkingMinuteFlow.asStateFlow()

    private val _runningMinuteFlow = MutableStateFlow(1)
    val runningMinuteFlow = _runningMinuteFlow.asStateFlow()

    private val _showCallConfirmDialogFlow = MutableStateFlow(false)
    val showCallConfirmDialogFlow = _showCallConfirmDialogFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val intervalSetting = fetchIntervalSettingUseCase().getOrDefault(IntervalSetting(1, 1, 1))
            _setCountFlow.value = intervalSetting.setCount
            _walkingMinuteFlow.value = intervalSetting.walkingMinutes
            _runningMinuteFlow.value = intervalSetting.runningMinutes
        }
    }

    fun setSetCount(setCount: Int) {
        _setCountFlow.value = setCount
    }

    fun setWalkingMinute(walkingMinute: Int) {
        _walkingMinuteFlow.value = walkingMinute
    }

    fun setRunningMinute(runningMinute: Int) {
        _runningMinuteFlow.value = runningMinute
    }

    fun saveIntervalSettings() {
        viewModelScope.launch {
            val intervalSetting = IntervalSetting(
                setCount = setCountFlow.value,
                walkingMinutes = walkingMinuteFlow.value,
                runningMinutes = runningMinuteFlow.value
            )
            updateIntervalSettingUseCase(intervalSetting).onSuccess {
                musicPlayer.setInterval(it)
            }
        }
    }

    fun showCallConfirmDialog() {
        _showCallConfirmDialogFlow.value = true
    }

    fun dismissCallConfirmDialog() {
        _showCallConfirmDialogFlow.value = false
    }

}