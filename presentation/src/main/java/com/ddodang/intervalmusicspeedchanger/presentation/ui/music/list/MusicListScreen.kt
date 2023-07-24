package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.list

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.common.extensions.finishApp
import com.ddodang.intervalmusicspeedchanger.presentation.model.Screen
import com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog.ErrorMessageDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusicListScreen(
    onNavigate: (Screen) -> Unit,
) {
    val viewModel = hiltViewModel<MusicListViewModel>()
    val musicList by viewModel.musicListFlow.collectAsState()
    val isRefreshing by viewModel.isRefreshingFlow.collectAsStateWithLifecycle()
    var permissionRefused by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) {
            permissionRefused = true
        }
    }
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null

    SideEffect {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission?.let {
                if(!it.status.isGranted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    if (permissionRefused) {
        ErrorMessageDialog(errorMessage = "알림을 보내야 해요..!", onConfirm = { (context as? Activity)?.finishApp() })
    }

    MusicListScreen(
        musicList = musicList,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadMusicList() },
        onMusicAddButtonClicked = { onNavigate(Screen.Download) },
        onSettingsButtonClicked = { onNavigate(Screen.Settings) },
        onMusicDelete = { music -> viewModel.deleteMusic(music) },
        onMusicSelected = { music ->
            viewModel.setMusic(music)
            onNavigate(Screen.MusicPlay)
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicListScreen(
    musicList: List<Music>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onMusicSelected: (Music) -> Unit,
    onMusicAddButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
    onMusicDelete: (Music) -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = onRefresh)
    Surface(color = Color.White) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (headerRef, listRef, refreshIndicatorRef) = createRefs()

            MusicListHeader(
                modifier = Modifier
                    .constrainAs(headerRef) {
                        width = Dimension.fillToConstraints
                        linkTo(parent.start, parent.end)
                        top.linkTo(parent.top)
                    },
                onSettingsButtonClicked = onSettingsButtonClicked,
                onMusicAddButtonClicked = onMusicAddButtonClicked
            )
            MusicList(
                modifier = Modifier
                    .constrainAs(listRef) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        linkTo(parent.start, parent.end)
                        linkTo(headerRef.bottom, parent.bottom, topMargin = 4.dp, bias = 0f)
                    }
                    .pullRefresh(pullRefreshState),
                musicList = musicList,
                onMusicSelected = onMusicSelected,
                onMusicDelete = onMusicDelete
            )
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.constrainAs(refreshIndicatorRef) {
                    top.linkTo(headerRef.top)
                    linkTo(parent.start, parent.end)
                }
            )
        }
    }
}

@Composable
private fun MusicListHeader(
    modifier: Modifier = Modifier,
    onSettingsButtonClicked: () -> Unit,
    onMusicAddButtonClicked: () -> Unit,
) {
    ConstraintLayout(modifier = modifier.padding(vertical = 12.dp)) {
        val (headerTitleRef, settingButtonRef, addButtonRef) = createRefs()
        Text(
            text = stringResource(id = R.string.list_wantToBe_playList),
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(headerTitleRef) {
                    top.linkTo(parent.top)
                    linkTo(parent.start, settingButtonRef.start, startMargin = 4.dp, endMargin = 4.dp, bias = 0f)
                }
        )

        IconButton(
            onClick = onSettingsButtonClicked,
            modifier = Modifier.constrainAs(settingButtonRef) {
                linkTo(headerTitleRef.top, headerTitleRef.bottom)
                linkTo(headerTitleRef.end, addButtonRef.start)
            }
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "설정창")
        }

        IconButton(
            onClick = onMusicAddButtonClicked,
            modifier = Modifier
                .constrainAs(addButtonRef) {
                    linkTo(headerTitleRef.top, headerTitleRef.bottom)
                    end.linkTo(parent.end, margin = 8.dp)
                }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "노래 추가 버튼")
        }

    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun MusicList(
    modifier: Modifier = Modifier,
    musicList: List<Music>,
    onMusicSelected: (Music) -> Unit,
    onMusicDelete: (Music) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(
            items = musicList,
            key = { music -> music.id }
        ) { music ->
            val currentItem by rememberUpdatedState(music)
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                        onMusicDelete(currentItem)
                        true
                    } else false
                }
            )
            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier
                    .padding(vertical = 1.dp)
                    .animateItemPlacement(),
                background = {
                    SwipeBackground(dismissState)
                },
                dismissThresholds = {
                    FractionalThreshold(0.3f)
                },
                dismissContent = {
                    MusicItem(music = music, onMusicSelected = { onMusicSelected(music) })
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.White
            else -> Color.Red
        }
    )

    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }

    val icon = Icons.Default.Delete

    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "삭제",
            modifier = Modifier.scale(scale)
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicItem(
    modifier: Modifier = Modifier,
    music: Music,
    onMusicSelected: () -> Unit,
) {
    Surface(color = Color.White, modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clickable(
                    enabled = true,
                    onClick = onMusicSelected
                )
        ) {
            val (imageRef, titleRef, singerRef) = createRefs()
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.ikmyung_profile),
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(40.dp)
                    .constrainAs(imageRef) {
                        linkTo(parent.top, parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            Text(
                text = music.title,
                fontSize = TextUnit(16f, TextUnitType.Sp),
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        animationMode = MarqueeAnimationMode.Immediately,
                        initialDelayMillis = 0,
                        delayMillis = 0
                    )
                    .constrainAs(titleRef) {
                        width = Dimension.fillToConstraints
                        linkTo(imageRef.end, parent.end, startMargin = 12.dp, endMargin = 4.dp, bias = 0f)
                        linkTo(parent.top, singerRef.top, bottomMargin = 4.dp, bias = 1f)
                    }
            )

            Text(
                text = music.artist,
                fontSize = TextUnit(10f, TextUnitType.Sp),
                maxLines = 1,
                modifier = Modifier
                    .constrainAs(singerRef) {
                        linkTo(imageRef.end, parent.end, startMargin = 12.dp, endMargin = 4.dp, bias = 0f)
                        linkTo(titleRef.bottom, parent.bottom, bias = 0f)
                    }
            )
        }
    }
}

@Composable
@Preview
fun Preview() {
    MusicListScreen(
        musicList = List(12) {
            Music(
                id = it.toString(),
                artist = "익명이",
                title = "익명이는 익명익명 Pt.$it ".repeat(it + 1),
                duration = "12분16초",
                location = ""
            )
        },
        isRefreshing = true,
        onRefresh = {},
        onMusicAddButtonClicked = {},
        onSettingsButtonClicked = {},
        onMusicSelected = {},
        onMusicDelete = {}
    )
}