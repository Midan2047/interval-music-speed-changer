package com.ddodang.intervalmusicspeedchanger.remote.di

import com.ddodang.intervalmusicspeedchanger.remote.retrofit.service.RapidApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val RAPID_API_BASE_URL = "https://youtube-mp36.p.rapidapi.com/"

    @Singleton
    @Provides
    fun provideRapidAPi(): RapidApiService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(RAPID_API_BASE_URL)
        .build()
        .create()

}