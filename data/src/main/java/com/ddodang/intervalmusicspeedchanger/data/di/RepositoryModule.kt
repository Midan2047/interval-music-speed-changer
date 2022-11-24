package com.ddodang.intervalmusicspeedchanger.data.di

import com.ddodang.intervalmusicspeedchanger.data.repository.MusicRepositoryImpl
import com.ddodang.intervalmusicspeedchanger.domain.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    abstract fun bindMusicRepository(
        musicRepository: MusicRepositoryImpl,
    ): MusicRepository
}