package com.ddodang.intervalmusicspeedchanger.presentation.util

import android.content.Context
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchIntervalSettingUseCase
import com.ddodang.intervalmusicspeedchanger.domain.usecase.FetchMusicListUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    @ApplicationContext private val context:  Context,
    private val fetchMusicListUseCase: FetchMusicListUseCase,
    private val fetchIntervalSettingUseCase: FetchIntervalSettingUseCase,
) {
    private var job  : Job? = null


    fun test() {
        job
    }

}