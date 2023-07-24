package com.ddodang.intervalmusicspeedchanger.presentation.ui.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.ui.component.GifImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
) {
    val viewModel = hiltViewModel<SettingsViewModel>()

    val setsCount by viewModel.setCountFlow.collectAsStateWithLifecycle()
    val walkPeriod by viewModel.walkingMinuteFlow.collectAsStateWithLifecycle()
    val runPeriod by viewModel.runningMinuteFlow.collectAsStateWithLifecycle()

    SettingsScreen(
        setsCount = setsCount,
        walkPeriod = walkPeriod,
        runPeriod = runPeriod,
        onSetChange = { viewModel.setSetCount(it) },
        onWalkChange = { viewModel.setWalkingMinute(it) },
        onRunChange = { viewModel.setRunningMinute(it) },
        onSaveSettings = { viewModel.saveIntervalSettings() },
        modifier = Modifier.fillMaxSize()
    )
    BackHandler(enabled = true, onBack = onBackPressed)
}

@Composable
private fun SettingsScreen(
    setsCount: Int,
    walkPeriod: Int,
    runPeriod: Int,
    onSetChange: (Int) -> Unit,
    onWalkChange: (Int) -> Unit,
    onRunChange: (Int) -> Unit,
    onSaveSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface {
        ConstraintLayout(modifier = modifier) {
            val (headerRef, settingRef, callMeButtonRef) = createRefs()
            SettingsHeader(modifier = Modifier
                .padding(10.dp)
                .constrainAs(headerRef) {
                    linkTo(parent.start, parent.end)
                    top.linkTo(parent.top)
                }
            )

            SettingsPage(
                setsCount = setsCount,
                walkPeriod = walkPeriod,
                runPeriod = runPeriod,
                onSetChange = onSetChange,
                onWalkChange = onWalkChange,
                onRunChange = onRunChange,
                onSaveSettings = onSaveSettings,
                modifier = Modifier.constrainAs(settingRef) {
                    width = Dimension.percent(0.95f)
                    height = Dimension.wrapContent
                    linkTo(parent.start, parent.end)
                    top.linkTo(headerRef.bottom)
                }
            )

            CallMeButton(
                modifier = Modifier.constrainAs(callMeButtonRef) {
                    width = Dimension.fillToConstraints
                    linkTo(parent.start, parent.end)
                    linkTo(settingRef.bottom, parent.bottom)
                }
            )
        }
    }
}

@Composable
private fun SettingsPage(
    setsCount: Int,
    walkPeriod: Int,
    runPeriod: Int,
    onSetChange: (Int) -> Unit,
    onWalkChange: (Int) -> Unit,
    onRunChange: (Int) -> Unit,
    onSaveSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (settingAreaNameRef, settingPageBodyRef, saveButtonRef) = createRefs()
        Text(
            text = "타이머",
            fontSize = TextUnit(20f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(settingAreaNameRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        )
        SettingsPageBody(
            setsCount = setsCount,
            walkPeriod = walkPeriod,
            runPeriod = runPeriod,
            onSetChange = onSetChange,
            onWalkChange = onWalkChange,
            onRunChange = onRunChange,
            modifier = Modifier.constrainAs(settingPageBodyRef) {
                linkTo(parent.start, parent.end)
                top.linkTo(settingAreaNameRef.bottom, margin = 5.dp)
            }
        )
        OutlinedButton(
            onClick = onSaveSettings,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colorResource(id = R.color.sub_pink),
                backgroundColor = Color.Transparent
            ),
            border = null,
            modifier = Modifier
                .border(1.dp, color = colorResource(id = R.color.sub_pink))
                .constrainAs(saveButtonRef) {
                    width = Dimension.fillToConstraints
                    linkTo(parent.start, parent.end)
                    top.linkTo(settingPageBodyRef.bottom)
                }
        ) {
            Text(
                text = "저장",
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
private fun SettingsPageBody(
    setsCount: Int,
    walkPeriod: Int,
    runPeriod: Int,
    onSetChange: (Int) -> Unit,
    onWalkChange: (Int) -> Unit,
    onRunChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = colorResource(id = R.color.sub_pink),
        modifier = modifier,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            val (setsRef, walkRef, runRef) = createRefs()
            SettingsBar(
                settingsTitle = "SETS",
                value = setsCount,
                onValueChange = onSetChange,
                modifier = Modifier.constrainAs(setsRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    linkTo(parent.start, parent.end)
                    linkTo(parent.top, walkRef.top)
                }
            )
            SettingsBar(
                settingsTitle = "WALK",
                value = walkPeriod,
                onValueChange = onWalkChange,
                modifier = Modifier.constrainAs(walkRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    linkTo(parent.start, parent.end)
                    linkTo(setsRef.bottom, runRef.top)
                }
            )
            SettingsBar(
                settingsTitle = "RUN",
                value = runPeriod,
                onValueChange = onRunChange,
                modifier = Modifier.constrainAs(runRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    linkTo(parent.start, parent.end)
                    linkTo(walkRef.bottom, parent.bottom)
                }
            )
        }
    }
}

@Composable
private fun SettingsBar(
    settingsTitle: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (settingTitleRef, leftButtonRef, valueRef, rightButtonRef) = createRefs()
        Text(
            text = settingsTitle,
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(settingTitleRef) {
                linkTo(parent.start, parent.end)
                top.linkTo(parent.top)
            }
        )
        IconButton(
            onClick = { onValueChange((value - 1).coerceAtLeast(1)) },
            enabled = value > 1,
            modifier = Modifier.constrainAs(leftButtonRef) {
                linkTo(parent.start, valueRef.start)
                linkTo(settingTitleRef.bottom, parent.bottom)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_left),
                contentDescription = "줄이기"
            )
        }

        Text(
            text = value.toString(),
            modifier = Modifier.constrainAs(valueRef) {
                linkTo(leftButtonRef.end, rightButtonRef.start)
                linkTo(settingTitleRef.bottom, parent.bottom)
            }
        )

        IconButton(
            onClick = { onValueChange((value + 1).coerceAtLeast(1)) },
            enabled = value < 10,
            modifier = Modifier.constrainAs(rightButtonRef) {
                linkTo(valueRef.end, parent.end)
                linkTo(settingTitleRef.bottom, parent.bottom)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                contentDescription = "늘리기"
            )
        }
    }
}

@Composable
private fun SettingsHeader(modifier: Modifier = Modifier) {
    Text(
        text = "설정",
        fontSize = TextUnit(value = 24f, type = TextUnitType.Sp),
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CallMeButton(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            callMe(context)
        } else {
            Toast.makeText(context, "전화를 하려면 허락해줘요!", Toast.LENGTH_SHORT).show()
        }
    }
    val callPermission = rememberPermissionState(permission = Manifest.permission.CALL_PHONE)
    val isPreview = LocalInspectionMode.current
    Button(
        onClick = {
            if (!isPreview) {
                if (callPermission.status.isGranted) {
                    callMe(context)
                } else {
                    launcher.launch(Manifest.permission.CALL_PHONE)
                }
            }
        },
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        modifier = modifier
    ) {
        Column {
            GifImage(
                modifier = modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f),
                gifResId = R.raw.shouting_ikmyung
            )

            Text(
                text = stringResource(R.string.contact_guide)
            )
        }
    }
}

private fun callMe(context: Context) {
    context.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "01090382047")))
}

@Preview
@Composable
private fun Preview() {
    Surface {
        SettingsScreen(
            setsCount = 1,
            walkPeriod = 1,
            runPeriod = 1,
            onSetChange = {},
            onWalkChange = {},
            onRunChange = {},
            onSaveSettings = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}