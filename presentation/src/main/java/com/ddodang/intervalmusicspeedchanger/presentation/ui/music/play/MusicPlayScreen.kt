package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
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
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService
import com.ddodang.intervalmusicspeedchanger.presentation.ui.component.GifImage

@Composable
fun MusicPlayScreen(
    onBackPressed: () -> Unit,
) {
    val viewModel = hiltViewModel<MusicPlayViewModel>()
    val isPlaying by viewModel.isPlayingFlow.collectAsStateWithLifecycle()
    val currentPlayingMusic by viewModel.currentPlayingMusicFlow.collectAsStateWithLifecycle()

    MusicPlayScreen(
        isPlaying = isPlaying,
        currentPlayingMusic = currentPlayingMusic,
        modifier = Modifier.fillMaxSize()
    )
    BackHandler(enabled = true, onBack = onBackPressed)
}

@Composable
private fun MusicPlayScreen(
    isPlaying: Boolean,
    currentPlayingMusic: Music?,
    modifier: Modifier = Modifier,
) {
    Surface(color = colorResource(id = R.color.sub_pink)) {
        if (currentPlayingMusic == null) {
            MusicNullScreen(modifier)
        } else {
            MusicPlayingScreen(
                isPlaying,
                currentPlayingMusic,
                modifier
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MusicPlayingScreen(
    isPlaying: Boolean,
    currentPlayingMusic: Music,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (imageRef, titleRef, singerRef, playingControllerRef) = createRefs()

        GifImage(
            gifResId = R.raw.ikmyung_dance,
            modifier = Modifier.constrainAs(imageRef) {
                width = Dimension.percent(0.8f)
                height = Dimension.ratio("1:1")
                linkTo(parent.start, parent.end)
                linkTo(parent.top, titleRef.top)
            }
        )
        Text(
            text = currentPlayingMusic.title,
            fontSize = TextUnit(28f, TextUnitType.Sp),
            maxLines = 1,
            modifier = Modifier
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    animationMode = MarqueeAnimationMode.Immediately,
                    initialDelayMillis = 0,
                    delayMillis = 0
                )
                .constrainAs(titleRef) {
                    linkTo(parent.start, parent.end)
                    linkTo(imageRef.bottom, playingControllerRef.top)
                }
        )
        Text(
            text = currentPlayingMusic.artist,
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(singerRef) {
                linkTo(parent.start, parent.end)
                top.linkTo(titleRef.bottom)
            }
        )

        MusicController(
            isPlaying = isPlaying,
            modifier = Modifier.constrainAs(playingControllerRef) {
                width = Dimension.fillToConstraints
                linkTo(parent.start, parent.end)
                linkTo(titleRef.bottom, parent.bottom)
            }
        )
    }
}

@Composable
private fun MusicController(
    isPlaying: Boolean,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    ConstraintLayout(modifier = modifier) {
        val (shuffleRef, previousRef, playStopRef, nextRef, repeatRef) = createRefs()

        IconButton(
            onClick = { },
            modifier = Modifier.constrainAs(shuffleRef) {
                linkTo(parent.start, previousRef.start)
                linkTo(parent.top, parent.bottom)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_shuffle),
                contentDescription = "랜덤재생",
                modifier = Modifier.size(32.dp)
            )
        }
        IconButton(
            onClick = { if (!isPreview) playPreviousMusic(context) },
            modifier = Modifier.constrainAs(previousRef) {
                linkTo(shuffleRef.end, playStopRef.start)
                linkTo(parent.top, parent.bottom)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_fast_rewind),
                contentDescription = "이전 노래",
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(
            onClick = { if (!isPreview) togglePlay(context) },
            modifier = Modifier.constrainAs(playStopRef) {
                linkTo(previousRef.end, nextRef.start)
                linkTo(parent.top, parent.bottom)
            }
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
            modifier = Modifier.constrainAs(nextRef) {
                linkTo(playStopRef.end, repeatRef.start)
                linkTo(parent.top, parent.bottom)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_fast_forward),
                contentDescription = "다음 노래",
                modifier = Modifier.size(32.dp)
            )
        }


        IconButton(
            onClick = { },
            modifier = Modifier.constrainAs(repeatRef) {
                linkTo(nextRef.end, parent.end)
                linkTo(parent.top, parent.bottom)
            }
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

@Preview
@Composable
private fun Preview() {
    Surface {
        MusicPlayScreen(
            isPlaying = true,
            currentPlayingMusic = Music("", "노미", "또당또당", "", ""),
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
            currentPlayingMusic = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}