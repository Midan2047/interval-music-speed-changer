package com.ddodang.intervalmusicspeedchanger.presentation.util

import android.content.Context
import androidx.core.content.edit

class SharedManageUtil private constructor(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, 0)

    var setCount: Int
        get() = sharedPreferences.getInt(SET_COUNT, 0)
        set(value) {
            sharedPreferences.edit {
                putInt(SET_COUNT, value)
            }
        }

    var walkingTime: Int
        get() = sharedPreferences.getInt(WALKING_TIME, 0)
        set(value) {
            sharedPreferences.edit {
                putInt(WALKING_TIME, value)
            }
        }

    var runningTime: Int
        get() = sharedPreferences.getInt(RUNNING_TIME, 0)
        set(value) {
            sharedPreferences.edit {
                putInt(RUNNING_TIME, value)
            }
        }


    companion object {

        private var instance: SharedManageUtil? = null

        private const val SHARED_PREFERENCE_FILE_NAME = "SHARED_PREFERENCE_FILE_NAME"

        private const val SET_COUNT = "SET_COUNT"
        private const val WALKING_TIME = "WALKING_TIME"
        private const val RUNNING_TIME = "RUNNING_TIME"

        fun getInstance(context: Context): SharedManageUtil {
            if (instance == null) {
                instance = SharedManageUtil(context)
            }
            return instance!!
        }
    }
}