package com.ddodang.intervalmusicspeedchanger.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseViewModel(
    private val defaultBlockingTime: Long = 500L,
) : ViewModel() {

    private val availableTimeMap = mutableMapOf<Any, Long>()
    private val runningMap = mutableMapOf<Any, AtomicBoolean>()

    protected fun throttle(key: Any = Unit, blockingTime: Long = defaultBlockingTime, block: suspend () -> Unit) {
        val currentTime = System.currentTimeMillis()
        val isAvailable = availableTimeMap[key]?.let { it <= currentTime } ?: true
        if (!isAvailable) return

        val isRunning = runningMap.getOrPut(key) { AtomicBoolean() }
        if (isRunning.getAndSet(true)) return

        val nextAvailableTime = currentTime + blockingTime
        availableTimeMap[key] = nextAvailableTime
        viewModelScope.launch {
            block()
            isRunning.set(false)
        }
    }

}