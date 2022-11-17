package com.ddodang.intervalmusicspeedchanger.local.di

import com.ddodang.intervalmusicspeedchanger.data.source.local.MusicLocalDataSource
import com.ddodang.intervalmusicspeedchanger.local.source.MusicLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindMusicDataSource(
        musicDataSource: MusicLocalDataSourceImpl,
    ): MusicLocalDataSource
}