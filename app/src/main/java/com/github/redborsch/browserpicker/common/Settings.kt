package com.github.redborsch.browserpicker.common

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.data.BrowserPickerRepository
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.browserpicker.shared.repository.SettingsEntry
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
        R.bool.pref_default_keep_in_recents,
    )

    val useOriginalIntent: Boolean by booleanPref(
        R.string.pref_key_use_original_intent,
        R.bool.pref_default_use_original_intent,
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

class BrowserListPreference(
    override val key: String,
) : AbstractPreference<BrowserListSettings>() {

    override val defaultValue: BrowserListSettings
        get() = generateDefault()

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

    /**
     * Trying to keep the maximum backward compatibility and minimize potential inconvenience
     * while still showcasing new features.
     */
    private fun generateDefault(): BrowserListSettings {
        val repo = BrowserPickerRepository()
        return BrowserListSettings.Builder(3).apply {
            // Usually default apps handling URLs were always on top of the list
            addEntry(
                SettingsEntry(
                    repo.nonBrowserAppEntry.packageName,
                    visible = true,
                    order = 0,
                )
            )
            repo.handlersActions.forEach {
                addEntry(
                    SettingsEntry(
                        it.packageName,
                        visible = true,
                        order = SettingsEntry.MAX_ORDER,
                    )
                )
            }
        }.build()
    }
}
