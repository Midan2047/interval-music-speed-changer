package com.ddodang.intervalmusicspeedchanger.local.di

import com.ddodang.intervalmusicspeedchanger.data.util.ApiKeyUtil
import com.ddodang.intervalmusicspeedchanger.data.util.FileUtil
import com.ddodang.intervalmusicspeedchanger.data.util.HtmlEscapeUtil
import com.ddodang.intervalmusicspeedchanger.local.util.ApiKeyUtilImpl
import com.ddodang.intervalmusicspeedchanger.local.util.FileUtilImpl
import com.ddodang.intervalmusicspeedchanger.local.util.HtmlEscapeUtilImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UtilModule {

    @Binds
    abstract fun bindApiKeyUtil(
        apiKeyUtil: ApiKeyUtilImpl,
    ): ApiKeyUtil

    @Binds
    abstract fun bindHtmlEscapeUtil(
        htmlEscapeUtil: HtmlEscapeUtilImpl,
    ): HtmlEscapeUtil

    @Binds
    abstract fun bindFileUtil(
        fileUtil: FileUtilImpl,
    ): FileUtil

}