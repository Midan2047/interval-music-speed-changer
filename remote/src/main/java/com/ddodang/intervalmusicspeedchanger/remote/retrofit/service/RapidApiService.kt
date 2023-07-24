package com.ddodang.intervalmusicspeedchanger.remote.retrofit.service

import com.ddodang.intervalmusicspeedchanger.remote.model.DownloadLinkDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RapidApiService {

    @GET("dl")
    suspend fun extractVideo(
        @Query("id") idCode: String?,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") apiHost: String,
    ): DownloadLinkDto
}
