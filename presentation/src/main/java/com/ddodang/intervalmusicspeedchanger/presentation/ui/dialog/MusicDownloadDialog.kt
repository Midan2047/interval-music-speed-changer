package com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.ui.component.GifImage

@Composable
fun MusicDownloadDialog(
    downloadProgress: Int,
    downloadProgressMessage: String,
    modifier: Modifier = Modifier,
) {

    Surface(
        modifier = modifier,
        color = Color.White
    ) {
        ConstraintLayout(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            val (headerRef, bodyRef) = createRefs()
            MessageDialogHeader(
                modifier = Modifier.constrainAs(headerRef) {
                    width = Dimension.fillToConstraints
                    linkTo(parent.start, parent.end)
                    top.linkTo(parent.top)
                },
                title = "다운로드 중..."
            )
            ProgressDialogBody(
                progress = downloadProgress,
                message = downloadProgressMessage,
                modifier = Modifier.constrainAs(bodyRef) {
                    width = Dimension.fillToConstraints
                    linkTo(headerRef.bottom, parent.bottom)
                    linkTo(parent.start, parent.end)
                }
            )
        }
    }

}

@Composable
fun ProgressDialogBody(
    modifier: Modifier = Modifier,
    message: String,
    progress: Int,
) {
    ConstraintLayout(modifier = modifier.padding(8.dp)) {
        val (imageRef, progressRef, messageRef) = createRefs()
        GifImage(
            gifResId = R.raw.ikmyung_party,
            modifier = Modifier.constrainAs(imageRef) {
                linkTo(parent.start, parent.end)
                top.linkTo(parent.top, margin = 8.dp)
                width = Dimension.percent(0.6f)
                height = Dimension.ratio("1:1")
            }
        )
        CircularProgressIndicator(
            progress = progress / 100f,
            color = colorResource(id = R.color.main_pink),
            modifier = Modifier.constrainAs(progressRef) {
                width = Dimension.fillToConstraints
                height = Dimension.ratio("1:1")
                linkTo(imageRef.start, imageRef.end)
                linkTo(imageRef.top, imageRef.bottom)
            }
        )
        Text(
            text = message,
            color = colorResource(id = R.color.main_pink),
            fontSize = TextUnit(18f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(messageRef) {
                linkTo(imageRef.bottom, parent.bottom, topMargin = 10.dp)
                linkTo(parent.start, parent.end)
            }
        )
    }
}

@Preview
@Composable
fun ProgressDialogPreview() {
    Surface {
        MusicDownloadDialog(70, "70% ( 13.2MB  / 24.3MB )")
    }
}