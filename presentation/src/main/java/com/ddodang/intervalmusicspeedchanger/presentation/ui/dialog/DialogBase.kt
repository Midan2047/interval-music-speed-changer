package com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.ddodang.intervalmusicspeedchanger.presentation.R

@Composable
fun MessageDialogHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        fontSize = TextUnit(24f, TextUnitType.Sp),
        textAlign = TextAlign.Center,
        color = colorResource(id = R.color.main_pink)
    )
}