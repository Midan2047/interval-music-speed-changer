package com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.ui.component.GifImage

@Composable
fun LoadingDialog(
    title: String,
    description: String,
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
            val (headerRef, imageRef, messageRef) = createRefs()
            MessageDialogHeader(
                title = title,
                modifier = Modifier.constrainAs(headerRef) {
                    linkTo(parent.start, parent.end)
                    top.linkTo(parent.top)
                }
            )
            GifImage(
                gifResId = R.raw.searching_ikmyung,
                modifier = Modifier.constrainAs(imageRef) {
                    linkTo(parent.start, parent.end)
                    top.linkTo(headerRef.bottom, margin = 10.dp)
                    width = Dimension.percent(0.6f)
                    height = Dimension.ratio("1:1")
                }
            )
            Text(
                text = description,
                color = colorResource(id = R.color.main_pink),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(messageRef) {
                    linkTo(imageRef.bottom, parent.bottom, topMargin = 10.dp)
                    linkTo(parent.start, parent.end)
                }
            )
        }
    }
}

@Preview
@Composable
private fun LoadingDialogPreview() {
    ConstraintLayout {
        val dialogRef = createRef()
        LoadingDialog(title = "음악을 검색 중 이에요", description = "열심히 찾고 있으니 잠시만 기다려 주세요.",
            modifier = Modifier.constrainAs(dialogRef) {
                linkTo(parent.top, parent.bottom)
                linkTo(parent.start, parent.end)
            })
    }

}