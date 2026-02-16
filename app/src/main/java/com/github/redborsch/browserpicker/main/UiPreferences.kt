package com.github.redborsch.browserpicker.main

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

class UiPreferences(
    private val context: Context,
    private val nameSuffix: String,
) {

    private val prefs: SharedPreferences
        get() {
            val name = "${context.packageName}$nameSuffix"
            return context.getSharedPreferences(name, MODE_PRIVATE)
        }

    var defaultScreen: String?
        get() = prefs.getString(KEY_DEFAULT_SCREEN, null)
        set(value) {
            prefs.edit {
                putString(KEY_DEFAULT_SCREEN, value)
            }
        }

    companion object {
        private const val KEY_DEFAULT_SCREEN = "defaultScreen"
    }
}
