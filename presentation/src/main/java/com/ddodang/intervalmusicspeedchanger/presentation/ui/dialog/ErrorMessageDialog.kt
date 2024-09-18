package com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ddodang.intervalmusicspeedchanger.presentation.R

@Composable
fun ErrorMessageDialog(
    errorMessage: String,
    onConfirm: () -> Unit,
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
                    linkTo(parent.start, parent.end)
                    top.linkTo(parent.top)
                    height = Dimension.wrapContent
                    width = Dimension.fillToConstraints
                },
                title = "에러가 발생해따!"
            )
            ErrorMessageBody(
                errorMessage = errorMessage,
                onConfirm = onConfirm,
                modifier = Modifier.constrainAs(bodyRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    linkTo(parent.start, parent.end)
                    linkTo(headerRef.bottom, parent.bottom)
                }
            )
        }
    }
}

@Composable
fun ErrorMessageBody(
    errorMessage: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (imageRef, textRef, confirmButtonRef) = createRefs()
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.ikmyung_surprise),
            contentDescription = "에러가...!!",
            modifier = Modifier.constrainAs(imageRef) {
                width = Dimension.percent(0.4f)
                height = Dimension.ratio("1:1")
                linkTo(parent.start, parent.end)
                linkTo(parent.top, textRef.top)
            }
        )
        Text(
            text = errorMessage,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(textRef) {
                linkTo(parent.start, parent.end)
                linkTo(imageRef.bottom, confirmButtonRef.top, topMargin = 10.dp)
            }
        )

        OutlinedButton(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(Color.Transparent, colorResource(id = R.color.main_pink)),
            modifier = Modifier.constrainAs(confirmButtonRef) {
                end.linkTo(parent.end, margin = 10.dp)
                linkTo(textRef.bottom, parent.bottom, topMargin = 5.dp, bottomMargin = 5.dp)
            }
        ) {
            Text(text = "확인")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorDialogPreview() {

    ConstraintLayout {
        val dialogRef = createRef()
        ErrorMessageDialog(
            errorMessage = "에러가 발생해따!!!!",
            onConfirm = {},
            modifier = Modifier
                .constrainAs(dialogRef) {
                    linkTo(parent.start, parent.end)
                    linkTo(parent.top, parent.bottom)
                }
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(6.dp)
                )
        )
    }
}