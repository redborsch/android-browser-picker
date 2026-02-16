package com.github.redborsch.browserpicker.common

import android.content.Context

class Settings(
    context: Context,
) {

    private val appContext = context.applicationContext

    val testUrl: String get() = "https://redborsch.github.io/android-browser-picker/"

    val keepInRecents: Boolean get() = true

    companion object {

        private var instance: Settings? = null

        fun getInstance(context: Context): Settings {
            return instance ?: synchronized(Companion) {
                instance ?: Settings(context).also {
                    instance = it
                }
            }
        }
    }
}
