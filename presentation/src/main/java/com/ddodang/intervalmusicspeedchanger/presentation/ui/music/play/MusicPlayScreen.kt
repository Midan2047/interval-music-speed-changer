package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Slider
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import com.ddodang.intervalmusicspeedchanger.presentation.util.retrieveThumbnailBitmapFromFile

@Composable
fun MusicPlayScreen(
    progress: Float,
) {
    val viewModel = hiltViewModel<MusicPlayViewModel>()
    val isPlaying by viewModel.isPlayingFlow.collectAsStateWithLifecycle()
    val currentPlayTime by viewModel.playTimeFlow.collectAsStateWithLifecycle()
    val currentPlayingMusic by viewModel.currentPlayingMusicFlow.collectAsStateWithLifecycle()

    MusicPlayScreen(
        isPlaying = isPlaying,
        currentPlayingMusic = currentPlayingMusic,
        musicPlayTimeMillis = currentPlayTime,
        progress = progress,
        onMusicPlaySeekBarChanged = { musicPositionInMillis ->
            viewModel.setMusicPosition(musicPositionInMillis.toInt())
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun MusicPlayScreen(
    isPlaying: Boolean,
    currentPlayingMusic: Music?,
    musicPlayTimeMillis: Int,
    progress: Float,
    onMusicPlaySeekBarChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = Modifier.padding(0.dp)) {
        if (currentPlayingMusic == null) {
            MusicNullScreen(modifier = modifier)
        } else {
            MusicPlayingScreenWithMusic(
                isPlaying = isPlaying,
                currentPlayingMusic = currentPlayingMusic,
                musicPlayTimeMillis = musicPlayTimeMillis,
                progress = progress,
                onMusicPlaySeekBarChanged = onMusicPlaySeekBarChanged,
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

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMotionApi::class)
@Composable
private fun MusicPlayingScreenWithMusic(
    isPlaying: Boolean,
    currentPlayingMusic: Music,
    musicPlayTimeMillis: Int,
    progress: Float,
    onMusicPlaySeekBarChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    MotionLayout(
        start = startConstraintSet(),
        end = endConstraintSet(),
        progress = progress,
        modifier = modifier,
    ) {
        val bitmap = remember(currentPlayingMusic.location) { retrieveThumbnailBitmapFromFile(currentPlayingMusic.location) }

        Image(
            bitmap = bitmap?.asImageBitmap() ?: ImageBitmap.imageResource(id = R.drawable.ikmyung_profile),
            contentDescription = "",
            modifier = Modifier
                .layoutId("album_art")
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

        val thumbColor by animateColorAsState(targetValue = colorResource(id = R.color.main_pink).copy((1f - progress).coerceIn(0f, 1f)))
        val trackColor by animateColorAsState(targetValue = colorResource(id = R.color.sub_pink).copy(alpha = 0.8f))
        MusicPlayProgressSlider(
            value = musicPlayTimeMillis * 1f / currentPlayingMusic.durationInMillis,
            onValueChange = { progress -> onMusicPlaySeekBarChanged(progress * currentPlayingMusic.durationInMillis) },
            transformProgress = progress,
            colors = SliderDefaults.colors(
                thumbColor = thumbColor,
                activeTrackColor = trackColor,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )

        Text(
            text = millisecondToMinuteSeconds(milliSeconds = musicPlayTimeMillis),
            modifier = Modifier
                .layoutId("current_music_position")
                .alpha(1f - progress),
            style = LocalTextStyle.current.copy(
                color = colorResource(id = R.color.gray50),
                fontSize = 10.sp
            )
        )

        Text(
            text = millisecondToMinuteSeconds(milliSeconds = currentPlayingMusic.durationInMillis),
            modifier = Modifier
                .layoutId("current_music_duration")
                .alpha(1f - progress),
            style = LocalTextStyle.current.copy(
                color = colorResource(id = R.color.gray50),
                fontSize = 10.sp
            )
        )

        MusicController(
            isPlaying = isPlaying,
            progress = progress,
            modifier = Modifier.layoutId("music_controller")
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun MusicPlayProgressSlider(
    value: Float,
    transformProgress: Float,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    colors: SliderColors = SliderDefaults.colors(),
) {
    var sliderValueRaw by remember { mutableStateOf(value) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsDraggedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()
    val sliderValue by derivedStateOf {
        if (isPressed || isDragged) {
            sliderValueRaw
        } else {
            value
        }
    }

    Slider(
        value = sliderValue,
        onValueChange = {
            sliderValueRaw = it
        },
        onValueChangeFinished = { onValueChange(sliderValueRaw) },
        interactionSource = interactionSource,
        valueRange = valueRange,
        steps = steps,
        modifier = modifier
            .layoutId("music_seekbar")
            .alpha(1f - transformProgress),
        colors = colors
    )

    LinearProgressIndicator(
        progress = sliderValue,
        modifier = modifier
            .layoutId("music_progressbar")
            .alpha(transformProgress)
            .zIndex(-1f),
        color = colorResource(id = R.color.main_pink).copy(alpha = 0.3f),
        backgroundColor = colorResource(id = R.color.transparent)
    )
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
    context.startForegroundService(Intent(context, MusicService::class.java).apply {
        action = MusicService.Constants.ACTION.PREVIOUS
    })

}

private fun playNextMusic(context: Context) {
    context.startForegroundService(Intent(context, MusicService::class.java).apply {
        action = MusicService.Constants.ACTION.NEXT
    })
}

private fun togglePlay(context: Context) {
    context.startForegroundService(Intent(context, MusicService::class.java).apply {
        action = MusicService.Constants.ACTION.TOGGLE_PLAY
    })
}

private fun startConstraintSet() = ConstraintSet {
    val imageViewRef = createRefFor("album_art")
    val titleRef = createRefFor("music_title")
    val singerRef = createRefFor("music_artist")
    val controllerRef = createRefFor("music_controller")
    val seekBarRef = createRefFor("music_seekbar")
    val currentPositionRef = createRefFor("current_music_position")
    val musicDurationRef = createRefFor("current_music_duration")
    val musicProgressBarRef = createRefFor("music_progressbar")

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

    constrain(seekBarRef) {
        width = Dimension.percent(0.9f)
        height = Dimension.preferredWrapContent
        linkTo(parent.start, parent.end)
        linkTo(titleRef.bottom, controllerRef.top)
    }
    constrain(musicProgressBarRef) {
        width = Dimension.percent(0.9f)
        height = Dimension.preferredWrapContent
        linkTo(parent.start, parent.end)
        linkTo(titleRef.bottom, controllerRef.top)
    }

    constrain(controllerRef) {
        width = Dimension.fillToConstraints
        linkTo(parent.start, parent.end)
        linkTo(seekBarRef.bottom, parent.bottom)
    }

    constrain(currentPositionRef) {
        start.linkTo(seekBarRef.start)
        top.linkTo(seekBarRef.bottom)
    }
    constrain(musicDurationRef) {
        end.linkTo(seekBarRef.end)
        top.linkTo(seekBarRef.bottom)
    }
}

private fun endConstraintSet(): ConstraintSet = ConstraintSet {
    val imageViewRef = createRefFor("album_art")
    val titleRef = createRefFor("music_title")
    val singerRef = createRefFor("music_artist")
    val controllerRef = createRefFor("music_controller")
    val seekBarRef = createRefFor("music_seekbar")
    val currentPositionRef = createRefFor("current_music_position")
    val musicDurationRef = createRefFor("current_music_duration")
    val musicProgressBarRef = createRefFor("music_progressbar")

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

    constrain(seekBarRef) {
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
        linkTo(parent.start, parent.end)
        linkTo(imageViewRef.top, imageViewRef.bottom)
    }

    constrain(controllerRef) {
        width = Dimension.wrapContent
        height = Dimension.value(60.dp)
        end.linkTo(parent.end)
        top.linkTo(imageViewRef.top)
    }
    constrain(musicProgressBarRef) {
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
        linkTo(parent.start, parent.end)
        linkTo(imageViewRef.top, imageViewRef.bottom)
    }

    constrain(currentPositionRef) {
        start.linkTo(seekBarRef.start)
        top.linkTo(seekBarRef.bottom)
    }
    constrain(musicDurationRef) {
        end.linkTo(seekBarRef.end)
        top.linkTo(seekBarRef.bottom)
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
        end.linkTo(playRef.start, margin = 5.dp)
        linkTo(parent.top, parent.bottom)
    }
    constrain(playRef) {
        width = Dimension.value(24.dp)
        height = Dimension.value(24.dp)
        end.linkTo(nextRef.start, margin = 5.dp)
        linkTo(parent.top, parent.bottom)
    }
    constrain(nextRef) {
        width = Dimension.value(24.dp)
        height = Dimension.value(24.dp)
        end.linkTo(parent.end, margin = 5.dp)
        linkTo(parent.top, parent.bottom)
    }
    constrain(repeatRef) {
        width = Dimension.value(0.dp)
        height = Dimension.value(0.dp)
        linkTo(nextRef.end, parent.end)
        linkTo(parent.top, parent.bottom)
    }
}

@Composable
private fun millisecondToMinuteSeconds(milliSeconds: Int): String {
    val seconds = milliSeconds / 1000
    val minutes = seconds / 60
    val secondsInMinute = seconds % 60
    return stringResource(id = R.string.mm_ss, minutes, secondsInMinute)
}

@Preview
@Composable
private fun Preview() {
    Surface {
        MusicPlayScreen(
            isPlaying = true,
            currentPlayingMusic = Music("", "노미", "또당또당", "", 1000),
            musicPlayTimeMillis = 100,
            progress = 0f,
            onMusicPlaySeekBarChanged = {},
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
            currentPlayingMusic = Music("", "노미", "또당또당", "", 8271000),
            musicPlayTimeMillis = 92100,
            progress = 1f,
            onMusicPlaySeekBarChanged = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
