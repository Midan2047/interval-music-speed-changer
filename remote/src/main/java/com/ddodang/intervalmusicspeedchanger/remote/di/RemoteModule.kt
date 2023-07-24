package com.ddodang.intervalmusicspeedchanger.remote.di

import com.ddodang.intervalmusicspeedchanger.data.source.remote.YouTubeRemoteDataSource
import com.ddodang.intervalmusicspeedchanger.remote.source.YouTubeRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteModule {

    @Binds
    abstract fun bindYoutubeRemoteDataSource(
        youTubeRemoteDataSource: YouTubeRemoteDataSourceImpl,
    ): YouTubeRemoteDataSource


}