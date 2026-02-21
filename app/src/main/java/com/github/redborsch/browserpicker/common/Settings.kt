package com.github.redborsch.browserpicker.common

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.github.redborsch.browserpicker.R
import com.github.redborsch.preferences.AbstractPreferences

class Settings(
    context: Context,
) : AbstractPreferences() {

    override val appContext: Context = context.applicationContext
    override val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(appContext)

    val testUrl: String by stringPref(
        R.string.pref_key_test_url,
        R.string.pref_default_test_url
    )

    val keepInRecents: Boolean by booleanPref(
        R.string.pref_key_keep_in_recents,
        R.bool.pref_default_keep_in_recents
    )

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
