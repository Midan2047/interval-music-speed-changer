package com.ddodang.intervalmusicspeedchanger.remote.source

import com.ddodang.intervalmusicspeedchanger.data.model.DownloadMusicStateData
import com.ddodang.intervalmusicspeedchanger.data.model.DownloadStateData
import com.ddodang.intervalmusicspeedchanger.data.model.YouTubeSearchResultData
import com.ddodang.intervalmusicspeedchanger.data.source.remote.YouTubeRemoteDataSource
import com.ddodang.intervalmusicspeedchanger.data.util.ApiKeyUtil
import com.ddodang.intervalmusicspeedchanger.data.util.FileUtil
import com.ddodang.intervalmusicspeedchanger.data.util.HtmlEscapeUtil
import com.ddodang.intervalmusicspeedchanger.remote.mapper.toData
import com.ddodang.intervalmusicspeedchanger.remote.model.DownloadStateDto
import com.ddodang.intervalmusicspeedchanger.remote.retrofit.service.RapidApiService
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Thumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import javax.inject.Inject

internal class YouTubeRemoteDataSourceImpl @Inject constructor(
    private val apiKeyUtil: ApiKeyUtil,
    private val androidHtmlUtil: HtmlEscapeUtil,
    private val fileUtil: FileUtil,
    private val rapidApiService: RapidApiService,
) : YouTubeRemoteDataSource {

    private val youtube by lazy {
        YouTube.Builder(
            NetHttpTransport(), GsonFactory()
        ) {}.setApplicationName("intervalmusicspeedchanger")
            .build()
    }

    override suspend fun fetchSearchResult(searchKey: String): Flow<YouTubeSearchResultData> = flow {
        val search = youtube.search().list("id,snippet")
        val result = search.setKey(apiKeyUtil.getYoutubeApiKey())
            .setQ(searchKey)
            .setOrder("date")
            .setType("video")
            .setRegionCode("KR")
            .setOrder("viewCount")
            .setTopicId("10")
            .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet),nextPageToken")
            .setMaxResults(50)
            .execute()
        emit(result)
    }.map { searchResult ->
        YouTubeSearchResultData(
            nextPageToken = searchResult.nextPageToken,
            videoList = searchResult.items.map { videoInfo ->
                YouTubeSearchResultData.VideoInfo(
                    videoInfo.id.videoId,
                    androidHtmlUtil.fromHtml(videoInfo.snippet.title),
                    (videoInfo.snippet.thumbnails["default"] as? Thumbnail)?.url
                )
            }
        )
    }

    override suspend fun loadMoreVideo(searchKey: String, nextPageToken: String): Result<YouTubeSearchResultData> = withContext(Dispatchers.IO) {
        runCatching {
            val search = youtube.search().list("id,snippet")
            search.setKey(apiKeyUtil.getYoutubeApiKey())
                .setQ(searchKey)
                .setPageToken(nextPageToken)
                .setOrder("date")
                .setType("video")
                .setRegionCode("KR")
                .setOrder("viewCount")
                .setTopicId("10")
                .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet),nextPageToken")
                .setMaxResults(50)
                .execute()
        }.map { searchResult ->
            YouTubeSearchResultData(
                nextPageToken = searchResult.nextPageToken,
                videoList = searchResult.items.map { videoInfo ->
                    YouTubeSearchResultData.VideoInfo(
                        videoInfo.id.videoId,
                        androidHtmlUtil.fromHtml(videoInfo.snippet.title),
                        (videoInfo.snippet.thumbnails["default"] as? Thumbnail)?.url
                    )
                }

            )
        }
    }

    override suspend fun extractYouTubeSound(videoId: String, videoName: String): DownloadStateData = withContext(Dispatchers.IO) {
        val downloadState = DownloadStateDto()
        runCatching {
            rapidApiService.extractVideo(idCode = videoId, apiKeyUtil.getRapidApiKey(), apiKeyUtil.getRapidApiHost())
        }.onSuccess {
            if (it.link == null) downloadState.errorState.value = NullPointerException()
            else extractYouTubeSound(downloadState, it.link, videoName)
        }.onFailure {
            downloadState.errorState.value = it
        }

        downloadState.toData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun extractYouTubeSoundFlow(videoId: String, videoName: String): Flow<DownloadMusicStateData> {
        return flow {
            emit(rapidApiService.extractVideo(videoId, apiKeyUtil.getRapidApiKey(), apiKeyUtil.getRapidApiHost()))
        }.catch {
            throw DownloadMusicStateData.FetchMusicLinkFailed(it)
        }.map {
            Request.Builder().url(it.link!!).build()
        }.catch {
            throw DownloadMusicStateData.InvalidLink(it)
        }.map { request ->
            extractYouTubeSound(request)
        }.flatMapLatest {
            flow {
                val contentLength = it.contentLength()
                val inputStream = it.byteStream()
                val musicDownloadModel = fileUtil.getFileDownloadModel(fileName = videoName)
                val buffer = ByteArray(4096)
                var bytesRead: Int
                emit(DownloadMusicStateData.OnDownloadStartMusic(contentLength))

                while (inputStream.read(buffer).also { byte -> bytesRead = byte } != -1) {
                    musicDownloadModel.writeToFile(buffer, 0, bytesRead)
                    emit(DownloadMusicStateData.OnDownloaded(bytesRead))
                }
                emit(DownloadMusicStateData.OnDownloadDoneMusic)
            }.flowOn(Dispatchers.IO)
        }.catch { exception ->
            if (exception is DownloadMusicStateData) {
                emit(exception)
            } else {
                emit(DownloadMusicStateData.OnSaveFileFailed(exception))
            }
        }
    }

    private suspend fun extractYouTubeSound(request: Request) =
        suspendCancellableCoroutine { cancellableContinuation ->
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cancellableContinuation.resumeWith(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        cancellableContinuation.resumeWith(Result.failure(Exception("Unexpected response code :${response.code}")))
                        return
                    }
                    val responseBody = response.body
                    if (responseBody == null) {
                        cancellableContinuation.resumeWith(Result.failure(Exception("Empty Body")))
                        return
                    }
                    cancellableContinuation.resumeWith(Result.success(responseBody))
                }

            })
        }

    private fun extractYouTubeSound(downloadState: DownloadStateDto, requestLink: String, musicName: String) = runCatching {
        val request = Request.Builder().url(requestLink).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                downloadState.errorState.value = e
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected response code: ${response.code}")
                }

                val responseBody: ResponseBody? = response.body
                if (responseBody != null) {
                    downloadState.totalByte.value = responseBody.contentLength()
                    val inputStream = responseBody.byteStream()
                    runCatching {
                        val musicDownloadModel = fileUtil.getFileDownloadModel(fileName = musicName)
                        val buffer = ByteArray(4096)
                        var bytesRead: Int

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            musicDownloadModel.writeToFile(buffer, 0, bytesRead)
                            downloadState.downloadedByteState.update { downloadedByte ->
                                downloadedByte + bytesRead
                            }
                            downloadState.downloadProgressState.value =
                                (downloadState.downloadedByteState.value * 100 / downloadState.totalByte.value).toInt()

                        }

                        musicDownloadModel.close()
                        inputStream.close()
                    }.onFailure {
                        downloadState.errorState.value = it
                    }

                } else {
                    throw IOException("Response body is null")
                }
            }

        })
    }
}
