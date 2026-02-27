package com.github.redborsch.browserpicker.common

import com.github.redborsch.browserpicker.BuildConfig

object Globals {
    /**
     * 80% of the screen height
     */
    const val MAX_COLLAPSED_BOTTOM_SHEET_HEIGHT = 0.8f

    val ownPackageName: String = BuildConfig.APPLICATION_ID

    fun urlRegex(): Regex = "(HTTP|http)([Ss])?://.+".toRegex()

    fun internalAction(name: String): String =
        "$ownPackageName.$name"
}
