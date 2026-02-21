package com.github.redborsch.browserpicker.main

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.github.redborsch.preferences.AbstractPreferences

class UiPreferences(
    context: Context,
    private val nameSuffix: String,
) : AbstractPreferences() {

    override val appContext: Context = context.applicationContext

    override val sharedPreferences: SharedPreferences
        get() {
            val name = "${appContext.packageName}$nameSuffix"
            return appContext.getSharedPreferences(name, MODE_PRIVATE)
        }

    var defaultScreen: String? by stringOrNullPref("DefaultScreen")
}
