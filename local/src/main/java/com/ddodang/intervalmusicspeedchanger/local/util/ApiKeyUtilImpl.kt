package com.ddodang.intervalmusicspeedchanger.local.util

import com.ddodang.intervalmusicspeedchanger.data.util.ApiKeyUtil
import com.ddodang.intervalmusicspeedchanger.local.BuildConfig
import javax.inject.Inject

internal class ApiKeyUtilImpl @Inject constructor() : ApiKeyUtil {

    override fun getYoutubeApiKey(): String {
        return BuildConfig.YOUTUBE_API_KEY
    }

    override fun getRapidApiKey(): String {
        return BuildConfig.RAPID_API_KEY
    }

    override fun getRapidApiHost(): String {
        return BuildConfig.RAPID_API_HOST
    }

}