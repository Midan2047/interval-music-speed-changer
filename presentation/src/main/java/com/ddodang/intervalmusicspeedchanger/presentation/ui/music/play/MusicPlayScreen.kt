package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ddodang.intervalmusicspeedchanger.domain.model.Music
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService
import com.ddodang.intervalmusicspeedchanger.presentation.ui.component.GifImage
import com.ddodang.intervalmusicspeedchanger.presentation.util.retrieveThumbnailFromFile

@Composable
fun MusicPlayScreen(
    onBackPressed: () -> Unit,
    progress: Float,
) {
    val viewModel = hiltViewModel<MusicPlayViewModel>()
    val isPlaying by viewModel.isPlayingFlow.collectAsStateWithLifecycle()
    val currentPlayingMusic by viewModel.currentPlayingMusicFlow.collectAsStateWithLifecycle()

    MusicPlayScreen(
        isPlaying = isPlaying,
        currentPlayingMusic = currentPlayingMusic,
        progress = progress,
        modifier = Modifier.fillMaxSize()
    )
    BackHandler(enabled = (progress == 0f), onBack = onBackPressed)
}

@Composable
private fun MusicPlayScreen(
    isPlaying: Boolean,
    currentPlayingMusic: Music?,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = Modifier.padding(0.dp)) {
        if (currentPlayingMusic == null) {
            MusicNullScreen(modifier = modifier)
        } else {
            MusicPlayingScreenWithMusic(
                isPlaying = isPlaying,
                currentPlayingMusic = currentPlayingMusic,
                progress = progress,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun MusicNullScreen(
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (runningIkmyungRef, messageRef) = createRefs()
        GifImage(
            gifResId = R.raw.running_ikmyung,
            modifier = Modifier.constrainAs(runningIkmyungRef) {
                linkTo(parent.start, parent.end)
                linkTo(parent.top, messageRef.top)
                width = Dimension.percent(0.6f)
                height = Dimension.ratio("1:1")
            }
        )

        Text(
            text = "비상비상!!! 무슨 일이 일어난거지!!!!",
            fontSize = TextUnit(18f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(messageRef) {
                linkTo(parent.start, parent.end)
                linkTo(runningIkmyungRef.bottom, parent.bottom)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMotionApi::class)
@Composable
private fun MusicPlayingScreenWithMusic(
    isPlaying: Boolean,
    currentPlayingMusic: Music,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    MotionLayout(
        start = startConstraintSet(),
        end = endConstraintSet(),
        progress = progress,
        modifier = modifier,
    ) {
        Image(
            bitmap = retrieveThumbnailFromFile(filePath = currentPlayingMusic.location),
            contentDescription = "",
            modifier = Modifier
                .layoutId("dancing_machine_ikmyung")
                .padding(0.dp)
        )
        Text(
            text = currentPlayingMusic.title,
            style = LocalTextStyle.current.copy(
                fontSize = (28f - (12f * progress)).sp
            ),
            maxLines = 1,
            modifier = Modifier
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    animationMode = MarqueeAnimationMode.Immediately,
                    initialDelayMillis = 0,
                    delayMillis = 0
                )
                .layoutId("music_title")
        )
        Text(
            text = currentPlayingMusic.artist,
            style = LocalTextStyle.current.copy(
                fontSize = (14f - (2f * progress)).sp
            ),
            modifier = Modifier.layoutId("music_artist")
        )

        MusicController(
            isPlaying = isPlaying,
            progress = progress,
            modifier = Modifier.layoutId("music_controller")
        )
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
private fun MusicController(
    isPlaying: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    MotionLayout(
        start = musicPlayerStartConstraintSet(),
        end = musicPlayerEndConstraintSet(),
        progress = progress,
        modifier = modifier,
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier.layoutId("button_shuffle")
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_shuffle),
                contentDescription = "랜덤재생",
                modifier = Modifier.size(32.dp)
            )
        }
        IconButton(
            onClick = { if (!isPreview) playPreviousMusic(context) },
            modifier = Modifier.layoutId("button_previous")
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_fast_rewind),
                contentDescription = "이전 노래",
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(
            onClick = { if (!isPreview) togglePlay(context) },
            modifier = Modifier.layoutId("button_play")
        ) {
            val icon = if (isPlaying) ImageVector.vectorResource(id = R.drawable.ic_pause)
            else ImageVector.vectorResource(id = R.drawable.ic_play)
            Icon(
                imageVector = icon,
                contentDescription = "play or pause",
                modifier = Modifier.size(32.dp)
            )

        }


        IconButton(
            onClick = { if (!isPreview) playNextMusic(context) },
            modifier = Modifier.layoutId("button_next")
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_fast_forward),
                contentDescription = "다음 노래",
                modifier = Modifier.size(32.dp)
            )
        }


        IconButton(
            onClick = { },
            modifier = Modifier.layoutId("button_repeat")
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_loop),
                contentDescription = "반복재생",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

private fun playPreviousMusic(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(Intent(context, MusicService::class.java).apply {
            action = MusicService.Constants.ACTION.PREVIOUS
        })
    } else {
        context.startService(Intent(context, MusicService::class.java).apply {
            action = MusicService.Constants.ACTION.PREVIOUS
        })
    }

}

private fun playNextMusic(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(Intent(context, MusicService::class.java).apply {
            action = MusicService.Constants.ACTION.NEXT
        })
    } else {
        context.startService(Intent(context, MusicService::class.java).apply {
            action = MusicService.Constants.ACTION.NEXT
        })
    }
}

private fun togglePlay(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(Intent(context, MusicService::class.java).apply {
            action = MusicService.Constants.ACTION.TOGGLE_PLAY
        })
    } else {
        context.startService(Intent(context, MusicService::class.java).apply {
            action = MusicService.Constants.ACTION.TOGGLE_PLAY
        })
    }
}

private fun startConstraintSet() = ConstraintSet {
    val imageViewRef = createRefFor("dancing_machine_ikmyung")
    val titleRef = createRefFor("music_title")
    val singerRef = createRefFor("music_artist")
    val controllerRef = createRefFor("music_controller")
    constrain(imageViewRef) {
        width = Dimension.percent(0.8f)
        height = Dimension.ratio("1:1")
        linkTo(parent.start, parent.end)
        linkTo(parent.top, titleRef.top)
    }

    constrain(titleRef) {
        width = Dimension.preferredWrapContent
        linkTo(parent.start, parent.end)
        linkTo(imageViewRef.bottom, controllerRef.top)
    }

    constrain(singerRef) {
        width = Dimension.preferredWrapContent
        linkTo(parent.start, parent.end)
        top.linkTo(titleRef.bottom)
    }

    constrain(controllerRef) {
        width = Dimension.fillToConstraints
        linkTo(parent.start, parent.end)
        linkTo(titleRef.bottom, parent.bottom)
    }
}

private fun endConstraintSet(): ConstraintSet = ConstraintSet {
    val imageViewRef = createRefFor("dancing_machine_ikmyung")
    val titleRef = createRefFor("music_title")
    val singerRef = createRefFor("music_artist")
    val controllerRef = createRefFor("music_controller")
    constrain(imageViewRef) {
        width = Dimension.value(60.dp)
        height = Dimension.value(60.dp)
        start.linkTo(parent.start)
        linkTo(parent.top, parent.bottom, bias = 0f)
    }

    constrain(titleRef) {
        width = Dimension.preferredWrapContent
        linkTo(imageViewRef.end, controllerRef.start, startMargin = 5.dp, bias = 0f)
        linkTo(imageViewRef.top, singerRef.top)
    }

    constrain(singerRef) {
        width = Dimension.preferredWrapContent
        linkTo(imageViewRef.end, controllerRef.start, startMargin = 5.dp, bias = 0f)
        linkTo(titleRef.bottom, imageViewRef.bottom)
    }

    constrain(controllerRef) {
        width = Dimension.wrapContent
        height = Dimension.value(60.dp)
        end.linkTo(parent.end)
        top.linkTo(imageViewRef.top)
    }
}

private fun musicPlayerStartConstraintSet(): ConstraintSet = ConstraintSet {
    val shuffleRef = createRefFor("button_shuffle")
    val previousRef = createRefFor("button_previous")
    val playRef = createRefFor("button_play")
    val nextRef = createRefFor("button_next")
    val repeatRef = createRefFor("button_repeat")

    constrain(shuffleRef) {
        width = Dimension.value(32.dp)
        height = Dimension.value(32.dp)
        linkTo(parent.start, previousRef.start)
        linkTo(parent.top, parent.bottom)
    }
    constrain(previousRef) {
        width = Dimension.value(32.dp)
        height = Dimension.value(32.dp)
        linkTo(shuffleRef.end, playRef.start)
        linkTo(parent.top, parent.bottom)
    }
    constrain(playRef) {
        width = Dimension.value(32.dp)
        height = Dimension.value(32.dp)
        linkTo(previousRef.end, nextRef.start)
        linkTo(parent.top, parent.bottom)
    }
    constrain(nextRef) {
        width = Dimension.value(32.dp)
        height = Dimension.value(32.dp)
        linkTo(playRef.end, repeatRef.start)
        linkTo(parent.top, parent.bottom)
    }
    constrain(repeatRef) {
        width = Dimension.value(32.dp)
        height = Dimension.value(32.dp)
        linkTo(nextRef.end, parent.end)
        linkTo(parent.top, parent.bottom)
    }
}

private fun musicPlayerEndConstraintSet(): ConstraintSet = ConstraintSet {
    val shuffleRef = createRefFor("button_shuffle")
    val previousRef = createRefFor("button_previous")
    val playRef = createRefFor("button_play")
    val nextRef = createRefFor("button_next")
    val repeatRef = createRefFor("button_repeat")

    constrain(shuffleRef) {
        width = Dimension.value(0.dp)
        height = Dimension.value(0.dp)
        end.linkTo(previousRef.start)
        linkTo(parent.top, parent.bottom)
    }
    constrain(previousRef) {
        width = Dimension.value(24.dp)
        height = Dimension.value(24.dp)
        end.linkTo(playRef.start)
        linkTo(parent.top, parent.bottom)
    }
    constrain(playRef) {
        width = Dimension.value(24.dp)
        height = Dimension.value(24.dp)
        end.linkTo(nextRef.start)
        linkTo(parent.top, parent.bottom)
    }
    constrain(nextRef) {
        width = Dimension.value(24.dp)
        height = Dimension.value(24.dp)
        end.linkTo(parent.end)
        linkTo(parent.top, parent.bottom)
    }
    constrain(repeatRef) {
        width = Dimension.value(0.dp)
        height = Dimension.value(0.dp)
        linkTo(nextRef.end, parent.end)
        linkTo(parent.top, parent.bottom)
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        MusicPlayScreen(
            isPlaying = true,
            currentPlayingMusic = Music("", "노미", "또당또당", "", ""),
            progress = 0f,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun PreviewMusicNull() {
    Surface {
        MusicPlayScreen(
            isPlaying = false,
            currentPlayingMusic = Music("", "노미", "또당또당", "", ""),
            progress = 1f,
            modifier = Modifier.fillMaxSize()
        )
    }
}
