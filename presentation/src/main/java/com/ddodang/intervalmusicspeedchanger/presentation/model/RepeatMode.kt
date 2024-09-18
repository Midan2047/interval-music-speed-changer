package com.ddodang.intervalmusicspeedchanger.presentation.model

import androidx.media3.common.Player
import java.io.Serializable

sealed class RepeatMode(val mode: Int) : Serializable {

    object Off : RepeatMode(Player.REPEAT_MODE_OFF) {

        override fun nextMode(): RepeatMode {
            return All
        }
    }

    object One : RepeatMode(Player.REPEAT_MODE_ONE) {

        override fun nextMode(): RepeatMode {
            return Off
        }
    }

    object All : RepeatMode(Player.REPEAT_MODE_ALL) {

        override fun nextMode(): RepeatMode {
            return One
        }
    }

    abstract fun nextMode(): RepeatMode

    companion object {

        fun parse(mode: Int): RepeatMode {
            return when (mode) {
                Player.REPEAT_MODE_OFF -> Off
                Player.REPEAT_MODE_ONE -> One
                Player.REPEAT_MODE_ALL -> All
                else -> All
            }
        }
    }
}