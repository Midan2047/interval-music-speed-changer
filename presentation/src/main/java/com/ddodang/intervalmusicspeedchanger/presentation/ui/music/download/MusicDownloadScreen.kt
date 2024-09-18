package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.download

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ddodang.intervalmusicspeedchanger.domain.model.YouTubeSearchResult
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.model.LoadingState
import com.ddodang.intervalmusicspeedchanger.presentation.model.MusicDownloadStateItem
import com.ddodang.intervalmusicspeedchanger.presentation.model.MusicSearchLoadingState
import com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog.ErrorMessageDialog
import com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog.LoadingDialog
import com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog.MusicDownloadDialog
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun MusicDownloadScreen(
    onBackPressed: () -> Unit,
) {
    val viewModel = hiltViewModel<MusicDownloadViewModel>()
    val youtubeSearchResult by viewModel.videoSearchResultList.collectAsState()
    val musicDownloadStateItem by viewModel.musicDownloadStateFlow.collectAsState()
    val loadingState by viewModel.loadingStateFlow.collectAsState()
    MusicDownloadScreen(
        youtubeSearchResult = youtubeSearchResult,
        musicDownloadStateItem = musicDownloadStateItem,
        loadingState = loadingState,
        onSearch = { searchKeyword -> viewModel.search(searchKeyword) },
        onVideoToExtractSelected = { result -> viewModel.download(result) },
        onScrollReachEnd = { viewModel.loadMore() },
        onProgressDone = { viewModel.downloadDone() }
    )

    BackHandler(enabled = true, onBack = onBackPressed)
}

@Composable
private fun MusicDownloadScreen(
    youtubeSearchResult: List<YouTubeSearchResult.VideoInfo>,
    musicDownloadStateItem: MusicDownloadStateItem,
    loadingState: MusicSearchLoadingState,
    onSearch: (String) -> Unit,
    onVideoToExtractSelected: (YouTubeSearchResult.VideoInfo) -> Unit,
    onScrollReachEnd: () -> Unit,
    onProgressDone: () -> Unit,
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (textFieldRef, dialogRef, resultListRef) = createRefs()
        SearchTextField(
            onSearch = onSearch,
            modifier = Modifier
                .border(
                    color = colorResource(id = R.color.main_pink),
                    width = 1.dp,
                )
                .padding(5.dp)
                .constrainAs(textFieldRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    linkTo(parent.start, parent.end, startMargin = 15.dp, endMargin = 15.dp)
                    linkTo(parent.top, resultListRef.top, topMargin = 15.dp, bottomMargin = 15.dp)
                }
        )
        YouTubeSearchResultList(
            results = youtubeSearchResult,
            onVideoToExtractSelected = onVideoToExtractSelected,
            onScrollReachEnd = onScrollReachEnd,
            modifier = Modifier.constrainAs(resultListRef) {
                height = Dimension.fillToConstraints
                linkTo(parent.start, parent.end)
                linkTo(textFieldRef.bottom, parent.bottom)
            }
        )

        musicDownloadStateItem.downloadError?.let {
            Dialog(onDismissRequest = {}) {
                ErrorMessageDialog(
                    errorMessage = it.message ?: "에러가 발생해따!!!",
                    onConfirm = onProgressDone,
                    modifier = Modifier.constrainAs(dialogRef) {
                        width = Dimension.percent(0.8f)
                        linkTo(parent.start, parent.end)
                        linkTo(parent.top, parent.bottom)
                    }.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(6.dp)
                    )
                )
            }
        }

        musicDownloadStateItem.downloadInformation?.let {
            if (it.downloadedPercentage != 100) {
                Dialog(
                    onDismissRequest = {},
                ) {
                    MusicDownloadDialog(
                        downloadProgress = it.downloadedPercentage,
                        downloadProgressMessage = "${it.downloadedPercentage}%(${(it.downloadedByteLength.toDouble() / 1024 / 1024).toRoundedString(2)}MB / ${
                            (it.contentLength.toDouble() / 1024 / 1024).toRoundedString(2)
                        }MB )",
                        modifier = Modifier.constrainAs(dialogRef) {
                            width = Dimension.percent(0.8f)
                            linkTo(parent.start, parent.end)
                            linkTo(parent.top, parent.bottom)
                        }.shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(6.dp)
                        )
                    )
                }
            }
        }
        when (loadingState) {
            MusicSearchLoadingState.None -> {}
            MusicSearchLoadingState.Searching -> {
                LoadingDialog(
                    title = stringResource(id = R.string.searchingMusic),
                    description = stringResource(id = R.string.waitForSearch),
                    modifier = Modifier.constrainAs(dialogRef) {
                        width = Dimension.percent(0.8f)
                        linkTo(parent.start, parent.end)
                        linkTo(parent.top, parent.bottom)
                    }.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(6.dp)
                    )
                )
            }

            MusicSearchLoadingState.PrepareDownload -> {
                LoadingDialog(
                    title = stringResource(id = R.string.preparingDownload),
                    description = stringResource(id = R.string.waitForPrepare),
                    modifier = Modifier.constrainAs(dialogRef) {
                        width = Dimension.percent(0.8f)
                        linkTo(parent.start, parent.end)
                        linkTo(parent.top, parent.bottom)
                    }.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(6.dp)
                    )
                )
            }
        }

    }
}

private fun Double.toRoundedString(decimalPoint: Int): String {
    val decimalFormat = DecimalFormat("#.${"#".repeat(decimalPoint)}")
    decimalFormat.roundingMode = RoundingMode.FLOOR
    return decimalFormat.format(this)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = text,
        onValueChange = {
            text = it
        },
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onSearch(text)
            }
        ),
        modifier = modifier.then(Modifier.onKeyEvent {
            if (it.key == Key.Enter) {
                focusManager.clearFocus()
                onSearch(text)
                true
            } else false
        }),
        decorationBox = { innerTextField ->
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (placeHolderRef, innerTextRef, iconRef) = createRefs()
                if (text.isEmpty()) {
                    Text(
                        text = "검색어를 입력하세요",
                        color = Color(0xA0000000),
                        modifier = Modifier.constrainAs(placeHolderRef) {
                            linkTo(parent.start, parent.end)
                            linkTo(parent.top, parent.bottom)
                            width = Dimension.fillToConstraints
                        }
                    )
                }
                Row(modifier = Modifier.constrainAs(innerTextRef) {
                    linkTo(parent.start, iconRef.start)
                    linkTo(parent.top, parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }) {
                    innerTextField()
                }
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색 버튼",
                    modifier = Modifier.constrainAs(iconRef) {
                        linkTo(innerTextRef.end, parent.end)
                        linkTo(parent.top, parent.bottom)
                        width = Dimension.ratio("1:1")
                        height = Dimension.wrapContent
                    }
                )
            }

        }
    )

}

@Composable
fun YouTubeSearchResultList(
    results: List<YouTubeSearchResult.VideoInfo>,
    onVideoToExtractSelected: (YouTubeSearchResult.VideoInfo) -> Unit,
    onScrollReachEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(key1 = scrollState.isScrolledToEnd()) {
        if (scrollState.isScrolledToEnd()) {
            onScrollReachEnd()
        }
    }


    LazyColumn(
        modifier = modifier,
        state = scrollState
    ) {
        items(
            items = results,
            itemContent = { result ->
                YouTubeSearchResultItem(
                    youTubeSearchResult = result,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onVideoToExtractSelected(result)
                        }
                )
            }
        )
    }
}

private fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YouTubeSearchResultItem(
    youTubeSearchResult: YouTubeSearchResult.VideoInfo,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (thumbnailRef, titleRef) = createRefs()
        AsyncImage(
            model = youTubeSearchResult.thumbnail,
            contentDescription = "Album Title",
            modifier = Modifier
                .padding(5.dp)
                .clip(CircleShape)
                .constrainAs(thumbnailRef) {
                    width = Dimension.value(50.dp)
                    height = Dimension.ratio("1:1")
                    start.linkTo(parent.start)
                    linkTo(parent.top, parent.bottom)
                },
            placeholder = debugPlaceholder(debugPreview = R.drawable.ikmyung_profile)
        )

        Text(
            text = youTubeSearchResult.title,
            modifier = Modifier
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    animationMode = MarqueeAnimationMode.Immediately,
                    initialDelayMillis = 0,
                    delayMillis = 0
                )
                .constrainAs(titleRef) {
                    width = Dimension.fillToConstraints
                    linkTo(thumbnailRef.end, parent.end, startMargin = 5.dp, endMargin = 5.dp)
                    linkTo(parent.top, parent.bottom)
                }
        )
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun Preview() {
    Surface {
        MusicDownloadScreen(
            youtubeSearchResult = List(10) {
                YouTubeSearchResult.VideoInfo(
                    "$it",
                    "익명이는 익명익명",
                    "https://scontent-ssn1-1.xx.fbcdn.net/v/t39.30808-6/301695561_554393220031753_7691574438003420149_n.jpg?_nc_cat=104&ccb=1-7&_nc_sid=09cbfe&_nc_ohc=M6IbwKUFqVIAX-FlCde&_nc_ht=scontent-ssn1-1.xx&oh=00_AfBrPCNny2bRfb6DiVEsfwtmnV2dMveXTIitQ75tjgk9yQ&oe=64894605"
                )
            },
            musicDownloadStateItem = MusicDownloadStateItem(null, null),
            loadingState = MusicSearchLoadingState.PrepareDownload,
            onSearch = {},
            onVideoToExtractSelected = {},
            onScrollReachEnd = {},
            onProgressDone = {}
        )
    }
}

@Composable
fun debugPlaceholder(@DrawableRes debugPreview: Int) =
    if (LocalInspectionMode.current) {
        painterResource(id = debugPreview)
    } else {
        null
    }