package com.github.redborsch.preferences

import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

fun <T : Preference> PreferenceFragmentCompat.withPreference(@StringRes keyResId: Int, block: T.() -> Unit) {
    val key = getString(keyResId)
    requireNotNull(findPreference<T>(key)) {
        "Preference $key was not found"
    }.run(block)
}
