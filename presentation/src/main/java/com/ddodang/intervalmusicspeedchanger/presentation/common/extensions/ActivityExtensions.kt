package com.ddodang.intervalmusicspeedchanger.presentation.common.extensions

import android.app.Activity
import kotlin.system.exitProcess

internal fun Activity.finishApp() {
    finish()
    exitProcess(0)
}