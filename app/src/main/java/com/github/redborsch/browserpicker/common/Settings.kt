package com.github.redborsch.browserpicker.common

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.preferences.AbstractPreference
import com.github.redborsch.preferences.AbstractPreferences

class Settings private constructor(
    context: Context,
) : AbstractPreferences() {

    override val appContext: Context = context.applicationContext
    override val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(appContext)

    val testUrl: String by stringPref(
        R.string.pref_key_test_url,
        R.string.pref_default_test_url
    )

    val truncateLink: Boolean by booleanPref(
        R.string.pref_key_truncate_link,
        R.bool.pref_default_truncate_link,
    )

    val maxLinkLines: Int by intPref(
        R.string.pref_key_max_link_lines,
        R.integer.pref_default_max_link_lines,
    )

    val keepInRecents: Boolean by booleanPref(
        R.string.pref_key_keep_in_recents,
        R.bool.pref_default_keep_in_recents
    )

    val peekHeight: Int by dimenPref(
        R.string.pref_key_peek_height,
        R.dimen.pref_default_peek_height,
    )

    val fullScreenByDefault: Boolean by booleanPref(
        R.string.pref_key_full_screen_by_default,
        R.bool.pref_default_full_screen_by_default,
    )

    var browserList: BrowserListSettings by BrowserListPreference("BrowserList")

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

class BrowserListPreference(override val key: String) : AbstractPreference<BrowserListSettings>() {

    override val defaultValue: BrowserListSettings
        get() = BrowserListSettings.empty()

    override fun SharedPreferences.read(): BrowserListSettings {
        return getStringSet(key, null)?.let {
            BrowserListSettings.deserialize(it)
        } ?: defaultValue
    }

    override fun SharedPreferences.Editor.write(
        value: BrowserListSettings
    ) {
        putStringSet(key, value.serialize())
    }
}

/*
FIXME remove
package=visible,order

my.package|1,0

com.github.redborsch.browserpicker.internal.copy
com.github.redborsch.browserpicker.internal.share
com.github.redborsch.browserpicker.internal.unknown-browsers

new browsers?

 */